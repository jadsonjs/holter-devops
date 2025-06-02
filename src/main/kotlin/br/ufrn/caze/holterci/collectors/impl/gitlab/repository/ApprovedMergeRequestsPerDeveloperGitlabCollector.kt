package br.ufrn.caze.holterci.collectors.impl.gitlab.repository

import br.com.jadson.snooper.gitlab.data.merge.GitLabMergeRequestInfo
import br.com.jadson.snooper.gitlab.operations.GitLabMergeRequestQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.collectors.dtos.CollectResult
import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricRepository
import br.ufrn.caze.holterci.domain.models.metric.MetricSegment
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.utils.GitLabUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.UUID
import kotlin.collections.iterator

@Component
class ApprovedMergeRequestsPerDeveloperGitlabCollector
    : Collector(UUID.fromString("8344b026-3b29-497f-840a-dda87eb00357"), Metric.APPROVED_MERGE_REQUESTS_PER_DEVELOPER, "Number of pull requests approved per developer at GitLab", MetricRepository.GITLAB){

    @Autowired
    lateinit var gitLabUtils: GitLabUtil

    var mergeRequestCache = mutableListOf<GitLabMergeRequestInfo>()

    /**
     * GET ALL MERGE REQUESTS approved in period
     *
     */
    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): CollectResult {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

        val executor = GitLabMergeRequestQueryExecutor()
        executor.setGitlabURL(projectConfiguration.mainRepository.url)
        executor.setGitlabToken(projectConfiguration.mainRepository.token)
        executor.setDisableSslVerification(disableSslVerification)
        executor.setQueryParameters(arrayOf("state=merged"))
        executor.setPageSize(100)

        // bring all issues first time to memory
        if(mergeRequestCache.isEmpty()){
            var allMerges = executor.mergeRequestsInPeriod(project.organization + "/" +project.name, globalPeriod.init, globalPeriod.end)
            mergeRequestCache = allMerges
        }

        // get from the list of global PR, that in the period selected
        // why?  because the github API, there is no a filter by date, so we have to bing all form the API and filter manually.
        // this is time consuming
        val contributors = mergeRequestCache.associate { it.author.username to 0 }.toMutableMap()
        val mrsMergedInPeriod = gitLabUtils.getMRsMergedInPeriod(mergeRequestCache, period.init, period.end);
        val mergedMRsByContributorInPeriod = countApprovedMRsPerContributor(mrsMergedInPeriod, contributors)

        val resultMetric = mutableListOf<CollectResult>()

        for((author, qttOfPRs) in mergedMRsByContributorInPeriod){

            val metricSegment = MetricSegment()
            metricSegment.type = "DEV"
            metricSegment.identifier = author

            resultMetric.add(
                CollectResult(
                    BigDecimal(qttOfPRs),
                    generateMetricInfo(period, mrsMergedInPeriod, mergedMRsByContributorInPeriod),
                    metricSegment
                )
            )
        }

        return CollectResult(resultMetric)

    }

    private fun countApprovedMRsPerContributor(mergedMRsInPeriod: List<GitLabMergeRequestInfo>, contributors: MutableMap<String, Int>): Map<String, Int> {

        for (mergeRequest in mergedMRsInPeriod) {
            val author = mergeRequest.author.username

            if (author != null) {
                contributors[author] = contributors.getOrDefault(author, 0) + 1
            }
        }

        return contributors
    }

    override fun cleanCache() {
        mergeRequestCache.clear()
    }

    fun generateMetricInfo(period: Period, mrsMergedInPeriod: List<GitLabMergeRequestInfo>, mergedMRsByContributorInPeriod : Map<String, Int> ): String{

        var metricInfo = "<strong> [ "+ dateUtil.format(period.init, "dd/MM/yyyy" ) +" to "+ dateUtil.format(period.end, "dd/MM/yyyy") + "] </strong> <br> "+
                "  - Approved Merge Requests : "+mrsMergedInPeriod.size+" <br>"

        for ((contributor, value) in mergedMRsByContributorInPeriod) {
            val data = "  - Approved merge requests authored by " + contributor + ": " + value+ " <br>"

            metricInfo += data
        }

        return metricInfo
    }

}