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
package br.ufrn.caze.holterci.collectors.impl.gitlab.ci

import br.com.jadson.snooper.gitlab.data.commit.GitLabCommitInfo
import br.com.jadson.snooper.gitlab.operations.GitLabCommitQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.collectors.dtos.CollectResult
import br.ufrn.caze.holterci.domain.models.metric.*
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class CommitPerDayGitlabCollector
    : Collector(UUID.fromString("0af18a6d-85a2-4b35-8cee-8181248a1684"), Metric.COMMIT_PER_DAY, "Commit Activity Per Day at Gitlab", MetricRepository.GITLAB){

    override fun calcMetricValue(period: Period, @SuppressWarnings("unused") globalPeriod: Period, project: Project): CollectResult {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

        val executor = GitLabCommitQueryExecutor()
        executor.setGitlabURL(projectConfiguration.mainRepository.url)
        executor.setGitlabToken(projectConfiguration.mainRepository.token)
        executor.setDisableSslVerification(disableSslVerification)
        executor.setQueryParameters(arrayOf("since=" + dateUtil.toIso8601(period.init), "until=" + dateUtil.toIso8601(period.end)))
        executor.setPageSize(100)

        var commitsOfPeriod: List<GitLabCommitInfo>

        commitsOfPeriod = executor.getCommits(project.organization + "/" +project.name)

        val qtdTotalDays = dateUtil.daysBetweenInclusiveWithOutWeekends(period.init, period.end)  // include the start and end.

        val commitsPerDayList = arrayOfNulls<Long>(qtdTotalDays)

        // init the array with zero
        for (i in commitsPerDayList.indices) {
            commitsPerDayList[i] = 0L
        }

        var currentReleaseDate: LocalDateTime = period.init

        for (index in 0 until qtdTotalDays) {
            for (commit in commitsOfPeriod) {
                if (commit.committed_date != null) {

                    // check if there is a commit in current day
                    if (   currentReleaseDate.dayOfMonth == dateUtil.toLocalDateTime(commit.committed_date).getDayOfMonth() // same day
                        && currentReleaseDate.monthValue == dateUtil.toLocalDateTime(commit.committed_date).getMonthValue() // same month
                        && currentReleaseDate.year == dateUtil.toLocalDateTime(commit.committed_date).getYear()             // same year
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

    fun generateMetricInfo(period: Period, commitsOfPeriod: List<GitLabCommitInfo>, qtdTotalDays : Int): String{

        val authourCountMap = mutableMapOf<String, Int>()


        for (commit in commitsOfPeriod) {
            // Count occurrences of each status
            authourCountMap[commit.committer_email] = authourCountMap.getOrDefault(commit.committer_email, 0) + 1
        }

        val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        return  "<strong> [ "+ dtf.format(period.init ) +" to "+ dtf.format(period.end) + "] </strong> <br> "+
                "  - Commits: "+commitsOfPeriod.size+" <br>"+
                "  - Total of Days: "+qtdTotalDays+" <br>"+
                "  - Commiters: "+authourCountMap+" <br>"
    }

}