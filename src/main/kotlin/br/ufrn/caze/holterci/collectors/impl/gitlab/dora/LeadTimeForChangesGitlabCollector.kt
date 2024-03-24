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

import br.com.jadson.snooper.gitlab.data.commit.GitLabCommitInfo
import br.com.jadson.snooper.gitlab.operations.GitLabCommitQueryExecutor
import br.com.jadson.snooper.gitlab.operations.GitLabMergeRequestQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricRepository
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

/**
 * Lead time for changes is the amount of time it takes a code change to get into production.
 *
 * In GitLab, Lead time for changes is measure by the Median time it takes for a merge request to get merged into production (from master).
 *
 * GitLab calculates Lead time for changes base on the number of seconds to successfully deliver a commit into production - from code committed
 * to code successfully running in production, without adding the coding_time to the calculation.
 *
 * We will calcuted as the time the commit was created until it be merge in the production branch.
 *
 * Lead Time For Changes  =  Mean of ALL ( mergeRequest_in_prodution_branch.merged_at - commit.created_at  )
 *
 * @author jadson santos - jadson.santos@ufrn.br
 */
@Component
class LeadTimeForChangesGitlabCollector
    : Collector(UUID.fromString("9133f323-0b64-47db-ac80-db42a07ceefd"), Metric.LEAD_TIME_FOR_CHANGES, "Lead time for changes at Gitlab", MetricRepository.GITLAB) {


    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): Pair<BigDecimal, String>  {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

        val executor = GitLabCommitQueryExecutor()
        executor.setGitlabURL(projectConfiguration.mainRepositoryURL)
        executor.setGitlabToken(projectConfiguration.mainRepositoryToken)
        executor.setDisableSslVerification(disableSslVerification)
        executor.setPageSize(100)
        executor.setQueryParameters(arrayOf("since=" + dateUtil.toIso8601(period.init), "until=" + dateUtil.toIso8601(period.end)))

        val executorMergs = GitLabMergeRequestQueryExecutor()
        executorMergs.setGitlabURL(projectConfiguration.mainRepositoryURL)
        executorMergs.setGitlabToken(projectConfiguration.mainRepositoryToken)
        executorMergs.setDisableSslVerification(disableSslVerification)
        executorMergs.setPageSize(100)


        var commitsOfPeriod : List<GitLabCommitInfo>

        commitsOfPeriod = executor.getCommits(project.organization + "/" + project.name)

        // commit merge production branch - commit create date
        val commitsLeadTime : MutableList<Long> = arrayListOf()

        var index = 1


        /**
         * In GitLab, Lead time for changes is measure by the Median time it takes for a merge request to get merged into production (from master).
         */
        for (commit in commitsOfPeriod) { // for all commits in the period

            // get the time when the merge request associated with this commit was merged in "prodution" branch
            val mergeList =
                executorMergs.listMergeRequestsAssociatedwithCommit(project.organization + "/" + project.name, commit.id)

            mergeRequests@
            for (mergeRequest in mergeList) {

                if(mergeRequest.target_branch.equals(projectConfiguration.produtionBranch, true)) { // if the pull request was to the production
                    if (mergeRequest.merged_at != null && commit.created_at != null) {
                        var duration = dateUtil.daysBetweenInclusive(dateUtil.toLocalDateTime(commit.created_at), dateUtil.toLocalDateTime(mergeRequest.merged_at) )
                        commitsLeadTime.add( duration.toLong()  )
                        break@mergeRequests
                    }
                }
            }

            index++

        }

        var mean = mathUtil.meanOfLongValues(commitsLeadTime)
        return Pair(mean, generateMetricInfo(period , commitsOfPeriod, commitsLeadTime))
    }

    override fun cleanCache() {

    }

    fun generateMetricInfo(period: Period, commitsOfPeriod : List<GitLabCommitInfo>, commitsLeadTime : MutableList<Long> ): String{

        val authourCountMap = mutableMapOf<String, Int>()


        for (commit in commitsOfPeriod) {
            // Count occurrences of each status
            authourCountMap[commit.committer_email] = authourCountMap.getOrDefault(commit.committer_email, 0) + 1
        }

        return  "<strong> [ "+ dateUtil.format(period.init, "dd/MM/yyyy" ) +" to "+ dateUtil.format(period.end, "dd/MM/yyyy") + "] </strong> <br> "+
                "  - Commits: "+commitsOfPeriod.size+" <br>"+
                "  - Commiters: "+authourCountMap+" <br> "+
                "  - Commit Merge Durations (in days): "+commitsLeadTime.joinToString("; ")+" <br>"
    }
}