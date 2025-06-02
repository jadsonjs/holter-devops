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
 * Mean Time to Recovery: Measures the time between an interruption due to deployment or system failure and full recovery.
 *
 * In GitLab, Time to restore service (Mean Time to Recovery) is measured as the median time an incident was open for on a production environment.
 * GitLab calculates the number of seconds an incident was open on a production environment in the given time period. This assumes:
 *
 *     GitLab incidents are tracked.
 *     All incidents are related to a production environment.
 *     Incidents and deployments have a strictly one-to-one relationship. An incident is related to only one production deployment,
 *     and any production deployment is related to no more than one incident
 *
 *
 * Gitlab makes many assumptions that is difficult to use in a project. ''an incident'' is a special kind of issue.
 * So, to simplify we will calculate the median time of an issues "of error" was open and closed for a project.
 * dividing by amount of releases for the project (tags)
 *
 * This way we not need to create incidents, normal issues works. incidents not need relation one-to-one with deploy.
 *
 * Mean Time to Recovery  = Mean ( SUM Issues ( issue close date - issues open date) )   / qty of tags
 *
 * @author jadson santos - jadsonjs@gmail.com
 */
@Component
class MeanTimetoRecoverByIssueGitlabCollector
    : Collector(UUID.fromString("b9826b09-8f24-4ddd-82ee-7078df1f7cdf"), Metric.MEAN_TIME_TO_RECOVERY, "Mean time to Recover By Issue at Gitlab", MetricRepository.GITLAB) {

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

        var timeCloseErrors : MutableList<BigDecimal> = mutableListOf()
        for(error in errorIssuesInPeriod){
            timeCloseErrors.add(dateUtil.daysBetween(dateUtil.toLocalDateTime(error.created_at), dateUtil.toLocalDateTime(error.closed_at)).toBigDecimal())
        }

        if(tagsOfPeriod.size > 0) { // there are releases in this period, divide by the number of releases
            return CollectResult(mathUtil.meanOfValues(timeCloseErrors).divide(tagsOfPeriod.size.toBigDecimal()), "", null)
        }else{
            return CollectResult(mathUtil.meanOfValues(timeCloseErrors), generateMetricInfo(period, errorIssuesInPeriod, tagsOfPeriod ), null )
        }

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