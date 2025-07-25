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

import br.com.jadson.snooper.gitlab.data.issue.GitLabIssueInfo
import br.com.jadson.snooper.gitlab.operations.GitLabIssueQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.collectors.dtos.CollectResult
import br.ufrn.caze.holterci.domain.models.metric.*
import br.ufrn.caze.holterci.domain.utils.GitLabUtil
import br.ufrn.caze.holterci.domain.utils.LabelsUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
class NClosedIssuesErrorRateGitlabCollector
    : Collector(UUID.fromString("ac855b0c-2c82-4b0d-ba84-459146d51885"), Metric.BUGS_RATE, "Rate of Issues closed associated with errors and total of Issues at GitLab",
    MetricRepository.GITLAB){

    @Autowired
    lateinit var gitLabUtils: GitLabUtil



    /** cache all issues of a project, because is very slow this query*/
    var issuesCache = mutableListOf<GitLabIssueInfo>()

    /**
     * GET ALL ISSUES CLOSED ASSOCIATED WITH ERROR in period
     */
    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): CollectResult {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

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
        var issuesOfPeriod = gitLabUtils.getIssueClosedInPeriod(issuesCache, period.init, period.end)
        var errorIssuesOfPeriod = mutableListOf<GitLabIssueInfo>()
        for (issue in issuesOfPeriod){
            if(isErrorIssue(issue, projectConfiguration.mainRepository.issuesErrosLabels))
                errorIssuesOfPeriod.add(issue)
        }

        if(issuesOfPeriod.isEmpty())
            return CollectResult(BigDecimal.ZERO, generateMetricInfo(period, errorIssuesOfPeriod, issuesOfPeriod ), null)

        return CollectResult(BigDecimal(errorIssuesOfPeriod.size).divide(BigDecimal(issuesOfPeriod.size)),"", null)

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

    fun generateMetricInfo(period: Period, errorIssuesOfPeriod : List<GitLabIssueInfo>, issuesOfPeriod : List<GitLabIssueInfo> ): String{

        val statusCountMap = mutableMapOf<String, Int>()
        var issueUniqueNumbers = mutableSetOf<Int>()

        for (issue in errorIssuesOfPeriod) {
            // Count occurrences of each status
            statusCountMap[issue.state] = statusCountMap.getOrDefault(issue.state, 0) + 1
            issueUniqueNumbers.add(issue.iid)
        }

        return  "<strong> [ "+ dateUtil.format(period.init, "dd/MM/yyyy" ) +" to "+ dateUtil.format(period.end, "dd/MM/yyyy") + "] </strong> <br> "+
        "  - Closed Issues: "+issuesOfPeriod.size+" <br>"+
        "  - Closed Error Issues: "+errorIssuesOfPeriod.size+" <br>"+
        "  - Closed Error Issues Status: "+statusCountMap+" <br>"+
        "  - Closed Error Issues Numbers: "+issueUniqueNumbers+" <br>"+
        "  - Closed Error Issues Labels: "+errorIssuesOfPeriod.joinToString("; "){ it.labels.toString() }+" <br>"
    }

}