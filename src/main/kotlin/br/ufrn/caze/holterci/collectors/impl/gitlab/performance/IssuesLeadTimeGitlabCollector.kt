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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
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
import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricRepository
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.utils.GitLabUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
class IssuesLeadTimeGitlabCollector
    : Collector(UUID.fromString("94d1ccf0-b144-4d05-8759-d41f2ab8e2c9"), Metric.LEAD_TIME, "Mean time to close a Issue at GitLab", MetricRepository.GITLAB){

    @Autowired
    lateinit var gitLabUtils: GitLabUtil


    /** cache all issues of a project, because is very slow this query*/
    var issuesCache = mutableListOf<GitLabIssueInfo>()

    /**
     * GET ALL ISSUES CLOSED ASSOCIATED AND CALC the time spend to closed it.
     */
    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): Pair<BigDecimal, String>  {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

        val executor = GitLabIssueQueryExecutor()
        executor.setGitlabURL(projectConfiguration.mainRepositoryURL)
        executor.setGitlabToken(projectConfiguration.mainRepositoryToken)
        executor.setDisableSslVerification(disableSslVerification)
        executor.setQueryParameters(arrayOf("scope=all", "state=closed"))
        executor.setPageSize(100)

        // bring all issues first time to memory
        if(issuesCache.isEmpty()){
            val allIssues = executor.issuesInPeriod(project.organization + "/" +project.name, globalPeriod.init, globalPeriod.end)
            issuesCache = allIssues
        }

        // commit merge production branch - commit create date
        val issuesLeadTime : MutableList<Long> = arrayListOf()

        val issuesOfPeriod = gitLabUtils.getIssueClosedInPeriod(issuesCache, period.init, period.end)

        for (issue in issuesOfPeriod){
            if (issue.closed_at != null && issue.created_at != null) {
                val duration = dateUtil.daysBetweenInclusive(dateUtil.toLocalDateTime(issue.created_at), dateUtil.toLocalDateTime(issue.closed_at) )
                issuesLeadTime.add( duration.toLong()  )
            }
        }


        val mean = mathUtil.meanOfLongValues(issuesLeadTime)
        return Pair(mean, generateMetricInfo(period, issuesOfPeriod, issuesLeadTime ))

    }

    override fun cleanCache() {
        issuesCache.clear()
    }

    fun generateMetricInfo(period: Period, issuesOfPeriod : List<GitLabIssueInfo>, issuesLeadTime : MutableList<Long> ): String{

        val statusCountMap = mutableMapOf<String, Int>()
        var issueUniqueNumbers = mutableSetOf<Int>()

        for (issue in issuesOfPeriod) {
            // Count occurrences of each status
            statusCountMap[issue.state] = statusCountMap.getOrDefault(issue.state, 0) + 1
            issueUniqueNumbers.add(issue.iid)
        }

        return  "<strong> [ "+ dateUtil.format(period.init, "dd/MM/yyyy" ) +" to "+ dateUtil.format(period.end, "dd/MM/yyyy") + "] </strong> <br> "+
                "  - Closed Issues: "+issuesOfPeriod.size+" <br>"+
                "  - Closed Issues Status: "+statusCountMap+" <br>"+
                "  - Closed Issues Numbers: "+issueUniqueNumbers+" <br>"+
                "  - Closed Issues Durations (in days): "+issuesLeadTime.joinToString("; ")+" <br>"
    }


}