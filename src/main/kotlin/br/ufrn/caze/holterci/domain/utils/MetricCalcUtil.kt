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
 * publicano
 * br.ufrn.caze.publicano.domain.utils
 * MetricCalcUtil
 * 04/07/21
 */
package br.ufrn.caze.holterci.domain.utils;

import br.com.jadson.snooper.github.data.commit.GitHubCommitInfo
import br.com.jadson.snooper.gitlab.data.commit.GitLabCommitInfo
import br.com.jadson.snooper.travisci.data.builds.TravisBuildsInfo
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * Class to perform some calculations about CI metrics
 * Jadson Santos - jadsonjs@gmail.com
 */
@Component
class MetricCalcUtil (
    var dateUtil: DateUtil,
    var mathUtil: MathUtil
        ){


    /**
     *  Receives a given build list and returns the median of its duration values.
     *
     */
     fun calcBuildDuration(builds : List<TravisBuildsInfo>) : BigDecimal{
        val metricList: MutableList<BigDecimal> = ArrayList()
        for (buildInfo in builds) {
            metricList.add(BigDecimal( /* IN SECONDS */ buildInfo.duration  ).divide(BigDecimal(60))) // CONVERT TO MINUTES
        }
        return mathUtil.medianOfValues(metricList)
    }


    /**
     * Calc the percentage of days with builds in the period.
     *
     * @param builds
     * @param start
     * @param end
     * @return 0 to 100.  0 no builds in period, 100 all days with build in period
     */
    fun calcBuildsActivity(builds: List<TravisBuildsInfo>, start: LocalDateTime, end: LocalDateTime): BigDecimal {

        var currentReleaseDate = start
        var qtdDaysWithBuilds: Long = 0
        val qtdTotalDays = start.until(end, ChronoUnit.DAYS) + if (dateUtil.isSameDay(start, end)) 1 else 2 // include the start and end.

        if(qtdTotalDays == 0L)
            return BigDecimal.ZERO

        for (index in 0 until qtdTotalDays) {

            buildsFor@
            for (build in builds) {
                if (build.started_at != null) {

                    // there is a build in this day
                    if (currentReleaseDate.dayOfMonth    == dateUtil.toLocalDateTime(build.started_at, "yyyy-MM-dd'T'HH:mm:ss'Z'").dayOfMonth
                        && currentReleaseDate.monthValue == dateUtil.toLocalDateTime(build.started_at, "yyyy-MM-dd'T'HH:mm:ss'Z'").monthValue
                        && currentReleaseDate.year      == dateUtil.toLocalDateTime(build.started_at, "yyyy-MM-dd'T'HH:mm:ss'Z'").year
                    ) {
                        qtdDaysWithBuilds++
                        break@buildsFor
                    }
                }
            }
            currentReleaseDate = currentReleaseDate.plusDays(1)
        }
        return BigDecimal(qtdDaysWithBuilds).divide(BigDecimal(qtdTotalDays), 10, RoundingMode.HALF_UP)
    }


    /**
     * Calc the medians of intervals between broken and not broken builds.
     *
     * @param buildOfPeriod
     * @return
     */
    fun calcTimeFixBrokenBuilds(buildOfPeriod: List<TravisBuildsInfo>): BigDecimal {

        Collections.sort(buildOfPeriod) { o1: TravisBuildsInfo, o2: TravisBuildsInfo ->
            if (o1.finished_at == null && o2.finished_at == null) return@sort 0
            if (o1.finished_at == null) return@sort -1
            if (o2.finished_at == null) return@sort 1
            dateUtil.toLocalDateTime(o1.finished_at, "yyyy-MM-dd'T'HH:mm:ss'Z'").compareTo(
                dateUtil.toLocalDateTime(o2.finished_at, "yyyy-MM-dd'T'HH:mm:ss'Z'")
            )
        }

        val timesToFix: MutableList<Long> = ArrayList()
        var failed = false
        var timeOfFailed: LocalDateTime? = null
        var lastBuildInfo: TravisBuildsInfo? = null

        for (buildInfo in buildOfPeriod) {
            if (buildInfo.state == "failed" && !failed) { // failed for first time
                failed = true
                timeOfFailed = dateUtil.toLocalDateTime(buildInfo.finished_at, "yyyy-MM-dd'T'HH:mm:ss'Z'")
            }
            if (buildInfo.state == "passed" && failed) { // came back to work
                val fixTime = dateUtil.toLocalDateTime(buildInfo.finished_at, "yyyy-MM-dd'T'HH:mm:ss'Z'")
                val hoursToFixBuild = timeOfFailed!!.until(fixTime, ChronoUnit.HOURS)
                timesToFix.add(hoursToFixBuild)
                failed = false
            }
            lastBuildInfo = buildInfo
        }

        // end the period and continuous broken
        // so considers the end of period as a time to fix
        if (lastBuildInfo != null) {
            if (lastBuildInfo.state == "failed" && failed) {
                val fixTime = dateUtil.toLocalDateTime(lastBuildInfo.finished_at, "yyyy-MM-dd'T'HH:mm:ss'Z'")
                val hoursToFixBuild = timeOfFailed!!.until(fixTime, ChronoUnit.HOURS)
                if (hoursToFixBuild > 0)
                    timesToFix.add(hoursToFixBuild)
            }
        }
        return mathUtil.medianOfLongValues(timesToFix)
    }


    /**
     * Calc the percentage of days with commits in the period.
     *
     * @param commitsOfPeriod
     * @param start
     * @param end
     * @return 0 to 100.  0 no commits in period, 100 all days with commits in period
     */
    fun calcGitHubCommitsActivity(commitsOfPeriod: List<GitHubCommitInfo>, start: LocalDateTime, end: LocalDateTime): BigDecimal? {
        var currentReleaseDate = start
        var qtdDaysWithCommits: Long = 0
        val qtdTotalDays = start.until(end, ChronoUnit.DAYS) + if (dateUtil.isSameDay(start, end)) 1 else 2 // include the start and end.

        for (index in 0 until qtdTotalDays) {
            //val containsBuild = false
            buildsFor@ for (commit in commitsOfPeriod) {
                if (commit.commit.author.date != null) {

                    // there is a commit in this day
                    if (currentReleaseDate.dayOfMonth == dateUtil.toLocalDateTime(commit.commit.author.date).dayOfMonth && currentReleaseDate.monthValue == dateUtil.toLocalDateTime(
                            commit.commit.author.date
                        ).monthValue && currentReleaseDate.year == dateUtil.toLocalDateTime(commit.commit.author.date).year
                    ) {
                        qtdDaysWithCommits++
                        break@buildsFor
                    }
                }
            }
            currentReleaseDate = currentReleaseDate.plusDays(1)
        }
        return BigDecimal(qtdDaysWithCommits).divide(BigDecimal(qtdTotalDays), 10, RoundingMode.HALF_UP)
    }

    /**
     * Calc the percentage of days with commits in the period.
     *
     * @param commitsOfPeriod
     * @param start
     * @param end
     * @return 0 to 100.  0 no commits in period, 100 all days with commits in period
     */
    fun calcGitLabCommitsActivity(commitsOfPeriod: List<GitLabCommitInfo>, start: LocalDateTime, end: LocalDateTime): BigDecimal? {
        var currentReleaseDate = start
        var qtdDaysWithCommits: Long = 0
        val qtdTotalDays = start.until(end, ChronoUnit.DAYS) + if (dateUtil.isSameDay(start, end)) 1 else 2 // include the start and end.

        for (index in 0 until qtdTotalDays) {
            //val containsBuild = false
            buildsFor@ for (commit in commitsOfPeriod) {
                if (commit.committed_date != null) {

                    // there is a commit in this day
                    if (currentReleaseDate.dayOfMonth == dateUtil.toLocalDateTime(commit.committed_date).dayOfMonth && currentReleaseDate.monthValue == dateUtil.toLocalDateTime(
                            commit.committed_date
                        ).monthValue && currentReleaseDate.year == dateUtil.toLocalDateTime(commit.committed_date).year
                    ) {
                        qtdDaysWithCommits++
                        break@buildsFor
                    }
                }
            }
            currentReleaseDate = currentReleaseDate.plusDays(1)
        }
        return BigDecimal(qtdDaysWithCommits).divide(BigDecimal(qtdTotalDays), 10, RoundingMode.HALF_UP)
    }

}