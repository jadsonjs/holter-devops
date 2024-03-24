/*
 * Federal University of Rio Grande do Norte
 * Department of Informatics and Applied Mathematics
 * Collaborative & Automated Software Engineering (CASE) Research Group
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package br.ufrn.caze.holterci.collectors.impl.gitlab.performance

import br.com.jadson.snooper.gitlab.data.commit.GitLabCommitInfo
import br.com.jadson.snooper.gitlab.data.issue.GitLabIssueInfo
import br.com.jadson.snooper.gitlab.operations.GitLabCommitQueryExecutor
import br.com.jadson.snooper.gitlab.operations.GitLabIssueQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.domain.models.metric.*
import br.ufrn.caze.holterci.domain.utils.GitLabUtil
import br.ufrn.caze.holterci.domain.utils.StringUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Component
class IssuesCycleTimeGitlabCollector
    : Collector(UUID.fromString("07d193c4-ee6e-412f-bd23-8648e28846e2"), Metric.CYCLE_TIME, "Mean time from first commit to issue closed", MetricRepository.GITLAB){

    @Autowired
    lateinit var gitLabUtils: GitLabUtil

    @Autowired
    lateinit var stringUtil: StringUtil


    /** cache all issues of a project, because is very slow this query*/
    var issuesCache = mutableListOf<GitLabIssueInfo>()

    /**
     * time for first commit to issue closed.
     * There is not this information on Gitlab, thus, we will try to find the commit comment and issues notes some relationship process the textual information
     *
     * This metric assumes that the developer will log this information.
     */
    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): Pair<BigDecimal, String>  {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

        // get all commits in the period os analysis
        var commitsOfPeriod : List<GitLabCommitInfo> = getAllCommitsInPeriod(projectConfiguration, project, period)

        // get all closed issues of project
        var issuesClosedOfProject : List<GitLabIssueInfo> = getAllIssuesClosedOfProject(projectConfiguration, project, period, globalPeriod)

        // association of issue of the project and a commit
        val map: MutableMap<Int, MutableList<GitLabCommitInfo>> = HashMap()

        for (commit in commitsOfPeriod) { // for all commits in the period, get the commit message

            val issueNumberInCommitMessage : Int = stringUtil.extractIssuesNumber(commit.message)

            if(issueNumberInCommitMessage > 0){ // commit contains issues number
                /**
                 * If there is this issues in this period, added a commit to it, in the end, see who is the fisrt commit
                 * id = id
                 * iid = number
                 */
                for (issue in issuesClosedOfProject){

                    if( issueNumberInCommitMessage.equals(issue.iid) ){
                        if(! map.containsKey(issue.id)) {
                            map.put(issue.id, ArrayList<GitLabCommitInfo>().apply { add(commit) })
                        }else {
                            map.get(issue.id)?.add(commit)
                        }
                    }
                }
            }
        }

        val issueCommitMap: MutableMap<Int, Int> = HashMap()

        // now, get the first commit of each issue
        // and finally calc the cycle time
        val cycleTimes : MutableList<Long> = arrayListOf()
        for ((id, commits) in map) {

            var issue = getIssueById(id, issuesClosedOfProject)
            var fisrtCommit = getFirstCommit(commits)

            issueCommitMap[issue.iid] = issueCommitMap.getOrDefault(issue.iid, 0) + 1

            var duration = dateUtil.daysBetweenInclusive(dateUtil.toLocalDateTime(fisrtCommit.created_at), dateUtil.toLocalDateTime(issue.closed_at) )
            cycleTimes.add( duration.toLong()  )
        }

        var mean = mathUtil.meanOfLongValues(cycleTimes)
        return Pair(mean, generateMetricInfo(period, issuesClosedOfProject , commitsOfPeriod, issueCommitMap, cycleTimes ))
    }

    override fun cleanCache() {
        issuesCache.clear()
    }

    fun generateMetricInfo(period: Period, issuesClosedOfProject : List<GitLabIssueInfo>, commitsOfPeriod : List<GitLabCommitInfo>, issueCommitMap: MutableMap<Int, Int>, cycleTimes : MutableList<Long> ): String{

        val authourCountMap = mutableMapOf<String, Int>()

        for (commit in commitsOfPeriod) {
            // Count occurrences of each status
            authourCountMap[commit.committer_email] = authourCountMap.getOrDefault(commit.committer_email, 0) + 1
        }

        val statusCountMap = mutableMapOf<String, Int>()
        var issueUniqueNumbers = mutableSetOf<Int>()

        for (issue in issuesClosedOfProject) {
            // Count occurrences of each status
            statusCountMap[issue.state] = statusCountMap.getOrDefault(issue.state, 0) + 1
            issueUniqueNumbers.add(issue.iid)
        }


        return  "<strong> [ "+ dateUtil.format(period.init, "dd/MM/yyyy" ) +" to "+ dateUtil.format(period.end, "dd/MM/yyyy") + "] </strong> <br> "+
                    "  - All Closed Issues: "+issuesClosedOfProject.size+" <br>"+
                    "  - All Closed Issues Status: "+statusCountMap+" <br>"+
                    "  - All Closed  Issues Numbers: "+issueUniqueNumbers+" <br>"+
                    "  - Commits: "+commitsOfPeriod.size+" <br>"+
                    "  - Commiters: "+authourCountMap+" <br>"+
                    "  - Number of commits associated with issues: "+issueCommitMap+" <br>"+
                    "  - Fisrt Commit to Closed Issue Durations (in days): "+cycleTimes.joinToString("; ")+" <br>"
    }


    /**
     * first get all commits in the period
     */
    fun getAllCommitsInPeriod(projectConfiguration: ProjectConfiguration, project: Project, period: Period) :List<GitLabCommitInfo> {


        val commitExecutor = GitLabCommitQueryExecutor()
        commitExecutor.setGitlabURL(projectConfiguration.mainRepositoryURL)
        commitExecutor.setGitlabToken(projectConfiguration.mainRepositoryToken)
        commitExecutor.setDisableSslVerification(disableSslVerification)
        commitExecutor.setPageSize(100)
        commitExecutor.setQueryParameters(arrayOf("since=" + dateUtil.toIso8601(period.init), "until=" + dateUtil.toIso8601(period.end)))

        var commitsInPeriod : List<GitLabCommitInfo>
        commitsInPeriod = commitExecutor.getCommits(project.organization + "/" + project.name)
        return commitsInPeriod
    }


    /***
     * And get all closed issues of project, do not matter the period when they were closed.
     */
    fun getAllIssuesClosedOfProject(projectConfiguration: ProjectConfiguration, project: Project, period: Period, globalPeriod: Period) :List<GitLabIssueInfo> {
        val issueExecutor = GitLabIssueQueryExecutor()
        issueExecutor.setGitlabURL(projectConfiguration.mainRepositoryURL)
        issueExecutor.setGitlabToken(projectConfiguration.mainRepositoryToken)
        issueExecutor.setDisableSslVerification(disableSslVerification)
        issueExecutor.setQueryParameters(arrayOf("scope=all", "state=closed"))
        issueExecutor.setPageSize(100)

        // bring all issues first time to memory
        if (issuesCache.isEmpty()) {
            var allIssues = issueExecutor.issues(project.organization + "/" + project.name)
            issuesCache = allIssues
        }

        return issuesCache
    }

    fun getIssueById(issueId: Int, issuesInPeriod: List<GitLabIssueInfo>): GitLabIssueInfo{
        for (issue in issuesInPeriod){
            if(issueId.equals(issue.id))
                return issue
        }
        throw IllegalArgumentException("all issues should be here....")
    }


    fun getFirstCommit(commits: MutableList<GitLabCommitInfo>): GitLabCommitInfo {
        var first = GitLabCommitInfo()
        first.created_at = dateUtil.toDate(LocalDateTime.now().plusYears(1)) // 1 year in the future
        for (commit in commits) {
            if (commit.created_at != null) {
                if (dateUtil.toLocalDateTime(commit.created_at).isBefore(dateUtil.toLocalDateTime(first.created_at))) {
                    first = commit
                }
            }
        }
        return first;
    }

}