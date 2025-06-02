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
package br.ufrn.caze.holterci.collectors.impl.gitlab.dora

import br.com.jadson.snooper.gitlab.data.issue.GitLabIssueInfo
import br.com.jadson.snooper.gitlab.data.tag.GitLabTagInfo
import br.com.jadson.snooper.gitlab.operations.GitLabIssueQueryExecutor
import br.com.jadson.snooper.gitlab.operations.GitLabTagQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.collectors.dtos.CollectResult
import br.ufrn.caze.holterci.domain.models.metric.*
import br.ufrn.caze.holterci.domain.utils.GitLabUtil
import br.ufrn.caze.holterci.domain.utils.LabelsUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Change failure rate is how often a change cause failure in production.
 *
 * In GitLab, Change failure rate is measured as the percentage of deployments that cause an incident in production
 * in the given time period.
 * GitLab calculates this by the number of incidents divided by the number of deployments to a production environment.
 *
 *  This assumes:
 *
 *     GitLab incidents are tracked.
 *     All incidents are related to a production environment.
 *     Incidents and deployments have a strictly one-to-one relationship. An incident is related to only one production deployment, and any production deployment is related to no more than one incident
 *
 * To simplify we will calculate the number of  issues "of error" was open for a project.
 * dividing by amount issue
 * dividing by amount of releases for the project (tags)
 *
 * ----------------------------------------------------------------------------------------------------------------------------------
 * Why?  Because:  If we have few errors issue compared with the total of issues in a version, this is not a problem.
 *                 The problem is if we have a relative qty of error issues compared with the amount of issues closed in version
 *                 The system is generating a lot of errors.
 * ----------------------------------------------------------------------------------------------------------------------------------
 *
 *
 * This way we not need to create incidents, normal issues works. incidents not need relation one-to-one with deploy.
 *
 *  Change Failure Rate =  (  ( qty Error Issues / qty  Issues ) / qty of tags ) * 100
 *
 * @author jadson santos - jadsonjs@gmail.com
 */
@Component
class ChangeFailureRateByIssueGitlabCollector
    : Collector(UUID.fromString("c8ce25cc-331e-4011-a5cc-ab256aae54f3"), Metric.CHANGE_FAILURE_RATE, "Change Failure Rate By Issue at Gitlab", MetricRepository.GITLAB) {

    @Autowired
    lateinit var gitLabUtils: GitLabUtil


    /** cache all issues of a project, because is very slow this query*/
    var issuesCache = mutableListOf<GitLabIssueInfo>()

    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): CollectResult {
        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)



        ///////////////////////  Get Issues of error  ////////////////////////

        val executor = GitLabIssueQueryExecutor()
        executor.setGitlabURL(projectConfiguration.mainRepository.url)
        executor.setGitlabToken(projectConfiguration.mainRepository.token)
        executor.setDisableSslVerification(disableSslVerification)
        executor.setQueryParameters(arrayOf("scope=all", "state=closed", "labels="+projectConfiguration.mainRepository.issuesErrosLabels))
        executor.setPageSize(100)


        // bring all issues first time to memory
        if(issuesCache.isEmpty()){
            var allIssues = executor.issuesInPeriod(project.organization + "/" +project.name, globalPeriod.init, globalPeriod.end)
            issuesCache = allIssues
        }
        var issuesInPeriod = gitLabUtils.getIssueClosedInPeriod(issuesCache, period.init, period.end)
        var errorIssuesInPeriod = mutableListOf<GitLabIssueInfo>()
        for (issue in issuesInPeriod){
            if(isErrorIssue(issue, projectConfiguration.mainRepository.issuesErrosLabels))
                errorIssuesInPeriod.add(issue)
        }

        ///////////////////////  Get Versions (tags) of period ////////////////////////

        val executorTags = GitLabTagQueryExecutor()
        executorTags.setGitlabURL(projectConfiguration.mainRepository.url)
        executorTags.setGitlabToken(projectConfiguration.mainRepository.token)
        executorTags.setDisableSslVerification(disableSslVerification)


        /**
         * This will considerer a release a tag, bacause some places do not create release on gitlab
         */
        var allTags : MutableList<GitLabTagInfo> = executorTags.tags(project.organization + "/" + project.name)

        var tagsOfPeriod : MutableList<GitLabTagInfo> = arrayListOf()

        // for each release
        for(r in allTags){
            if( r.commit.created_at != null && dateUtil.isBetweenDates(dateUtil.toLocalDateTime( r.commit.created_at), period.init, period.end) ) {
                tagsOfPeriod.add(r)
            }
        }


        ////////////////////// Calc Metric /////////////////////////

        if(errorIssuesInPeriod.size == 0)
            return CollectResult(BigDecimal.ZERO, "", null)

        if(tagsOfPeriod.size > 0) { // there are releases in this period, divide by the number of releases
            return CollectResult( ( errorIssuesInPeriod.size.toBigDecimal().divide(issuesInPeriod.size.toBigDecimal()).divide(tagsOfPeriod.size.toBigDecimal()) ).multiply(BigDecimal(100)), generateMetricInfo(period, errorIssuesInPeriod, tagsOfPeriod), null )
        }else{
            return CollectResult( ( errorIssuesInPeriod.size.toBigDecimal().divide(issuesInPeriod.size.toBigDecimal())).multiply(BigDecimal(100)), generateMetricInfo(period, errorIssuesInPeriod, tagsOfPeriod), null )        }

    }

    override fun cleanCache() {
        issuesCache.clear()
    }

    @Autowired
    lateinit var labelsUtil : LabelsUtil

    private fun isErrorIssue(issue: GitLabIssueInfo, errorsWord : String?): Boolean {
        if (issue.labels != null && issue.labels.size > 0) {
            for (label in issue.labels) {
                if (labelsUtil.isErrorLabels(label, errorsWord)) {
                    return true
                }
            }
        }
        return false
    }

    fun generateMetricInfo(period: Period, errorIssuesInPeriod : List<GitLabIssueInfo>, tagsOfPeriod : List<GitLabTagInfo>): String{

        val statusCountMap = mutableMapOf<String, Int>()
        var issueUniqueNumbers = mutableSetOf<Int>()



        for (issue in errorIssuesInPeriod) {
            // Count occurrences of each status
            statusCountMap[issue.state] = statusCountMap.getOrDefault(issue.state, 0) + 1
            issueUniqueNumbers.add(issue.iid)
        }

        val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        return  "<strong> [ "+ dtf.format(period.init ) +" to "+ dtf.format(period.end) + "] </strong> <br> "+
                "  - Error Issues: "+errorIssuesInPeriod.size+" <br>"+
                "  - Issues Status: "+statusCountMap+" <br>"+
                "  - Issues Numbers: "+issueUniqueNumbers+" <br>"+
                "  - Tags: "+tagsOfPeriod.size+" <br>"+
                "  - Tags Name: "+tagsOfPeriod.joinToString("; "){ it.name }+" <br>"+
                "  - Tags Date: "+tagsOfPeriod.joinToString("; "){ dateUtil.format(dateUtil.toLocalDateTime(it.commit.created_at), "dd/MM/yyyy") }+" <br>"
    }


}