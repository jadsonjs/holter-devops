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
package br.ufrn.caze.holterci.collectors.impl.github.repository

import br.com.jadson.snooper.github.data.pull.GitHubPullRequestInfo
import br.com.jadson.snooper.github.operations.PullRequestQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.collectors.dtos.CollectResult
import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricRepository
import br.ufrn.caze.holterci.domain.models.metric.MetricSegment
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.utils.GitHubUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

@Component
class ApprovedPullRequestsPerDeveloperGitHubCollector
    : Collector(UUID.fromString("f782fa50-8712-44d8-9c74-75f101076a77"), Metric.APPROVED_MERGE_REQUESTS_PER_DEVELOPER, "Number of pull requests approved per developer at GitHub", MetricRepository.GITHUB){

    @Autowired
    lateinit var gitHubUtils: GitHubUtil


    var pullRequestCache = mutableListOf<GitHubPullRequestInfo>()

    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): CollectResult {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

        val executor = PullRequestQueryExecutor(projectConfiguration.mainRepository.token)
        executor.setQueryParameters(arrayOf("state=closed"))
        executor.setPageSize(100)

        // first bring all pull request of the global period to memory
        if(pullRequestCache.isEmpty()){
            val allPRs = executor.pullRequestsCreatedInPeriod(project.organization+"/"+project.name, globalPeriod.init, globalPeriod.end)
            pullRequestCache = allPRs
        }

        val contributors = pullRequestCache.associate { it.user.login to 0 }.toMutableMap()
        val mergedPRsInPeriod = gitHubUtils.getPRsMergedInPeriod(pullRequestCache, period.init, period.end)
        val mergedPRsPerContributor = countApprovedPRsPerContributor(mergedPRsInPeriod, contributors)

        val resultMetric = mutableListOf<CollectResult>()

        for((author, qttOfPRs) in mergedPRsPerContributor){

            val metricSegment = MetricSegment()
            metricSegment.type = "DEV"
            metricSegment.identifier = author

            resultMetric.add(CollectResult(BigDecimal(qttOfPRs), generateMetricInfo(period, mergedPRsInPeriod, mergedPRsPerContributor), metricSegment))
        }
        return CollectResult(resultMetric)

    }

    private fun countApprovedPRsPerContributor(mergedPRsInPeriod: List<GitHubPullRequestInfo>, contributors: MutableMap<String, Int>): Map<String, Int> {

        for (pullRequest in mergedPRsInPeriod) {
            val author = pullRequest.user.login

            if (author != null) {
                contributors[author] = contributors.getOrDefault(author, 0) + 1
            }
        }

        return contributors
    }

    override fun cleanCache() {
        pullRequestCache.clear()
    }

    fun generateMetricInfo(period: Period, mergedPRsInPeriod : List<GitHubPullRequestInfo>, mergedPRsPerContributor: Map<String, Int> ): String{

        var metricInfo = "<strong> [ "+ dateUtil.format(period.init, "dd/MM/yyyy" ) +" to "+ dateUtil.format(period.end, "dd/MM/yyyy") + "] </strong> <br> "+
                "  - Approved Pull Requests : "+mergedPRsInPeriod.size+" <br>"

        for ((contributor, value) in mergedPRsPerContributor) {
            val data = "  - Merged requests authored by " + contributor + ": " + value+ " <br>"

            metricInfo += data
        }

        return metricInfo
    }

}