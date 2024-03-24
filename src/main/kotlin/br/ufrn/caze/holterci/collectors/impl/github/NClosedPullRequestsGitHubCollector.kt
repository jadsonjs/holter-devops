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
 *
 */
package br.ufrn.caze.holterci.collectors.impl.github

import br.com.jadson.snooper.github.data.pull.GitHubPullRequestInfo
import br.com.jadson.snooper.github.operations.PullRequestQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricRepository
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.utils.GitHubUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*


/**
 * This class is a collector of pull requests in Git Hub
 *
 * @author bruno batista - brunokaike@ufrn.edu.br
 */
@Component
class NClosedPullRequestsGitHubCollector
    : Collector(UUID.fromString("37c1ce17-b086-430a-a4d5-7179d23c7a12"), Metric.NUMBER_OF_CHANGES_DELIVERED, "Number of Closed Pull Requests at GitHub", MetricRepository.GITHUB){

    @Autowired
    lateinit var gitHubUtils: GitHubUtil


    var pullRequestCache = mutableListOf<GitHubPullRequestInfo>()

    /**
     * GET ALL PULL REQUESTS closed in period
     *
     */
    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): Pair<BigDecimal, String> {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

        val executor = PullRequestQueryExecutor(projectConfiguration.mainRepositoryToken)
        executor.setQueryParameters(arrayOf("state=closed"))
        executor.setPageSize(100)

        // first bring all pull request of the global period to memory
        if(pullRequestCache.isEmpty()){
            val allPRs = executor.pullRequestsCreatedInPeriod(project.organization+"/"+project.name, globalPeriod.init, globalPeriod.end)
            pullRequestCache = allPRs
        }

        // get from the list of global PR, that in the period selected
        // why?  because the github API, there is no a filter by date, so we have to bing all form the API and filter manually.
        // this is time consuming
        val prsClosedInPeriod = gitHubUtils.getPRsClosedInPeriod(pullRequestCache, period.init, period.end)
        return Pair( BigDecimal(prsClosedInPeriod.size), generateMetricInfo(period, prsClosedInPeriod ))

    }

    override fun cleanCache() {
        pullRequestCache.clear()
    }

    fun generateMetricInfo(period: Period, prsClosedInPeriod : List<GitHubPullRequestInfo> ): String{

        val statusCountMap = mutableMapOf<String, Int>()
        var issueUniqueNumbers = mutableSetOf<Long>()

        for (issue in prsClosedInPeriod) {
            // Count occurrences of each status
            statusCountMap[issue.state] = statusCountMap.getOrDefault(issue.state, 0) + 1
            issueUniqueNumbers.add(issue.number)
        }

        return  "<strong> [ "+ dateUtil.format(period.init, "dd/MM/yyyy" ) +" to "+ dateUtil.format(period.end, "dd/MM/yyyy") + "] </strong> <br> "+
            "  - Closed Pull Requests : "+prsClosedInPeriod.size+" <br>"+
            "  - Closed Pull Requests Status: "+statusCountMap+" <br>"+
            "  - Closed Pull Requests Numbers: "+issueUniqueNumbers+" <br>"
    }

}