package br.ufrn.caze.holterci.collectors.impl.github.dora

import br.com.jadson.snooper.github.data.commit.GitHubCommitInfo
import br.com.jadson.snooper.github.operations.CommitQueryExecutor
import br.com.jadson.snooper.github.operations.PullRequestQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.collectors.dtos.CollectResult
import br.ufrn.caze.holterci.domain.models.metric.*
import org.springframework.stereotype.Component
import java.util.*

/**
 * Lead time for changes is the amount of time it takes a code change to get into production.
 *
 * Number of seconds to successfully deliver a commit into production
 *
 * Lead time for changes base on the number of seconds to successfully deliver a commit into production -
 * from code committed to code successfully running in production.
 *
 * We will calc the mean of commints by period, since the commit is created, until be merged to "production" branch.
 * How production branch can change. The name of the production branch must be informed to the tool
 *
 * @author jadson santos - jadson.santos@ufrn.br
 */
@Component
class LeadTimeForChangesGitHubCollector : Collector(UUID.fromString("250c6d7c-910e-41bc-b3c6-6f5955a41051"),  Metric.LEAD_TIME_FOR_CHANGES, "Lead time for changes at Github", MetricRepository.GITHUB) {



    /**
     *    Author: The author of a commit refers to the person who originally created the changes that are being committed.
     *    It represents the individual who made the changes to the code or files. The author's information typically
     *    includes their name and email address.
     *
     *    Committer: The committer, on the other hand, is the person who applies the changes to the repository.
     *    They might not necessarily be the original author of the changes. The committer's information usually
     *    includes their name and email address as well.
     */
    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): CollectResult {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

        val executor = CommitQueryExecutor()
        executor.setGithubToken(projectConfiguration.mainRepository.token)
        executor.setQueryParameters(arrayOf("since=" + dateUtil.toIso8601(period.init), "until=" + dateUtil.toIso8601(period.end)))
        executor.setPageSize(100)

        val executorPulls = PullRequestQueryExecutor()
        executorPulls.setGithubToken(projectConfiguration.mainRepository.token)
        executorPulls.setPageSize(100)


        var commitsOfPeriod: List<GitHubCommitInfo>

        commitsOfPeriod = executor.getCommits(project.organization + "/" + project.name)

        // commit merge production bacnehc - commit create date
        val commitsLeadTime : MutableList<Long> = arrayListOf()

        var index : Int = 1

        for (commit in commitsOfPeriod) { // for all commits in the period

            // get the time when the pull request associated with this commit was merged in "prodution" branch
            val pullList =
                 executorPulls.listPullRequestsAssociatedwithCommit(project.organization + "/" + project.name, commit.sha)

            pullRequest@
            for (pullRequest in pullList) {

                if(pullRequest.base.ref.equals(projectConfiguration.mainRepository.produtionBranch.trim(), true)) { // if the pull request was to the production
                    if (pullRequest.merged_at != null && commit.commit.author.date != null) {
                        var duration = dateUtil.hoursBetween(dateUtil.toLocalDateTime(commit.commit.author.date), dateUtil.toLocalDateTime(pullRequest.merged_at) )
                        // println("Lead Time ( commit: "+commit.sha+") "+duration)
                        commitsLeadTime.add( duration  )
                        break@pullRequest
                    }
                }
            }

            index++

        }

        var mean = mathUtil.meanOfLongValues(commitsLeadTime)
        return CollectResult(mean, generateMetricInfo(period , commitsOfPeriod, commitsLeadTime), null)
    }

    override fun cleanCache() {

    }

    fun generateMetricInfo(period: Period, commitsOfPeriod : List<GitHubCommitInfo>, commitsLeadTime : MutableList<Long> ): String{

        val authourCountMap = mutableMapOf<String, Int>()


        for (commit in commitsOfPeriod) {
            // Count occurrences of each status
            authourCountMap[commit.committer.email] = authourCountMap.getOrDefault(commit.committer.email, 0) + 1
        }

        return  "<strong> [ "+ dateUtil.format(period.init, "dd/MM/yyyy" ) +" to "+ dateUtil.format(period.end, "dd/MM/yyyy") + "] </strong> <br> "+
                "  - Commits: "+commitsOfPeriod.size+" <br>"+
                "  - Commiters: "+authourCountMap+" <br> "+
                "  - Commit Merge (in days): "+commitsLeadTime.joinToString("; ")+" <br>"
    }
}