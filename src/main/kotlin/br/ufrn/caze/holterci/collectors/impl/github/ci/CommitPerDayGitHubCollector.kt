package br.ufrn.caze.holterci.collectors.impl.github.ci

import br.com.jadson.snooper.github.data.commit.GitHubCommitInfo
import br.com.jadson.snooper.github.operations.CommitQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.collectors.dtos.CollectResult
import br.ufrn.caze.holterci.domain.models.metric.*
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class CommitPerDayGitHubCollector
    : Collector(UUID.fromString("52997999-4c39-4643-8725-69afea8b33e5"), Metric.COMMIT_PER_DAY, "Commit Activity Per Day at Github", MetricRepository.GITHUB){



    override fun calcMetricValue(period: Period, @SuppressWarnings("unused") globalPeriod: Period, project: Project): CollectResult {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

        val executor = CommitQueryExecutor()
        executor.setGithubToken(projectConfiguration.mainRepository.token)
        executor.setQueryParameters(arrayOf("since=" + dateUtil.toIso8601(period.init), "until=" + dateUtil.toIso8601(period.end)))
        executor.setPageSize(100)

        var commitsOfPeriod: List<GitHubCommitInfo>

        commitsOfPeriod = executor.getCommits(project.organization + "/" + project.name)

        val qtdTotalDays = dateUtil.daysBetweenInclusiveWithOutWeekends(period.init, period.end)  // include the start and end.

        val commitsPerDayList = arrayOfNulls<Long>(qtdTotalDays)

        // init the array with zero
        for (i in commitsPerDayList.indices) {
            commitsPerDayList[i] = 0L
        }

        var currentReleaseDate: LocalDateTime = period.init

        for (index in 0 until qtdTotalDays) {
            for (commit in commitsOfPeriod) {
                if (commit.commit.author.date != null) {

                    // check if there is a commit in current day
                    if (   currentReleaseDate.dayOfMonth == dateUtil.toLocalDateTime(commit.commit.author.date).getDayOfMonth() // same day
                        && currentReleaseDate.monthValue == dateUtil.toLocalDateTime(commit.commit.author.date).getMonthValue() // same month
                        && currentReleaseDate.year == dateUtil.toLocalDateTime(commit.commit.author.date).getYear()             // same year
                    ) {
                        commitsPerDayList[index] = commitsPerDayList[index]!! + 1
                    }
                }
            }
            currentReleaseDate = currentReleaseDate.plusDays(1) // next day
        }

        // here not work median, because we can have a lot of day with zero commits, that generate commitPerDay = 0 and is not thrue
        var meanCommitsPerWeekDay = mathUtil.meanOfLongValues(commitsPerDayList.toList() as List<Long>)
        return CollectResult(meanCommitsPerWeekDay, generateMetricInfo(period, commitsOfPeriod, qtdTotalDays), null)
    }

    override fun cleanCache() {

    }

    fun generateMetricInfo(period: Period, commitsOfPeriod: List<GitHubCommitInfo>, qtdTotalDays : Int): String{

        val authourCountMap = mutableMapOf<String, Int>()


        for (commit in commitsOfPeriod) {
            // Count occurrences of each status
            authourCountMap[commit.committer.email] = authourCountMap.getOrDefault(commit.committer.email, 0) + 1
        }

        val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        return  "<strong> [ "+ dtf.format(period.init ) +" to "+ dtf.format(period.end) + "] </strong> <br> "+
                "  - Commits: "+commitsOfPeriod.size+" <br>"+
                "  - Total of Days: "+qtdTotalDays+" <br>"+
                "  - Commiters: "+authourCountMap+" <br>"
    }

}