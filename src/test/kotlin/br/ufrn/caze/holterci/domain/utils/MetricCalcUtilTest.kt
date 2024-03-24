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
 * CalcUtilTest
 * 04/07/21
 */
package br.ufrn.caze.holterci.domain.utils;

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.test.context.SpringBootTest

/**
 * Jadson Santos - jadsonjs@gmail.com
 */
@ExtendWith(MockitoExtension::class)
@SpringBootTest
class MetricCalcUtilTest {

    @Spy
    var dateUnit: DateUtil? = null

    @Spy
    var mathUtil: MathUtil? = null

    @InjectMocks
    var metricCalc: MetricCalcUtil? = null

    @BeforeEach
    fun initMocks() {
        MockitoAnnotations.openMocks(this)
    }

//    @Test
//    fun calcTimeFixBrokenBuildsTest() {
//        val buildsInfo: MutableList<TravisBuildsInfo> = ArrayList()
//        val b1 = TravisBuildsInfo()
//        b1.finished_at = "2021-01-10T01:00:00Z"
//        b1.state = "passed"
//        val b2 = TravisBuildsInfo()
//        b2.finished_at = "2021-01-10T02:00:00Z"
//        b2.state = "failed"
//        val b3 = TravisBuildsInfo()
//        b3.finished_at = "2021-01-10T02:30:00Z"
//        b3.state = "failed"
//        val b4 = TravisBuildsInfo()
//        b4.finished_at = "2021-01-10T02:40:00Z"
//        b4.state = "failed"
//
//        // 50 min = 3.000 seconds
//        val b5 = TravisBuildsInfo()
//        b5.finished_at = "2021-01-10T02:50:00Z"
//        b5.state = "passed"
//        val b6 = TravisBuildsInfo()
//        b6.finished_at = "2021-01-11T01:00:00Z"
//        b6.state = "failed"
//
//        // 24 h = 86.400 seconds
//        val b7 = TravisBuildsInfo()
//        b7.finished_at = "2021-01-12T01:00:00Z"
//        b7.state = "passed"
//
//        // median of 44.700 seconds to fix broken build
//        buildsInfo.add(b1)
//        buildsInfo.add(b2)
//        buildsInfo.add(b3)
//        buildsInfo.add(b4)
//        buildsInfo.add(b5)
//        buildsInfo.add(b6)
//        buildsInfo.add(b7)
//        val startReleaseDate = LocalDateTime.of(2021, 1, 10, 10, 0, 0)
//        val endReleaseDate = LocalDateTime.of(2021, 1, 10, 9, 0, 0)
//        Assertions.assertEquals(BigDecimal("44700.0000000000"), metricCalc!!.calcTimeFixBrokenBuilds(buildsInfo))
//    }
//
//    @Test
//    fun calcBuildsActivity100Percent() {
//        val buildsInfo: MutableList<TravisBuildsInfo> = java.util.ArrayList()
//        val b1 = TravisBuildsInfo()
//        b1.started_at = "2021-01-01T01:00:00Z"
//        val b2 = TravisBuildsInfo()
//        b2.started_at = "2021-01-02T01:00:00Z"
//        val b3 = TravisBuildsInfo()
//        b3.started_at = "2021-01-03T01:00:00Z"
//        val b4 = TravisBuildsInfo()
//        b4.started_at = "2021-01-04T01:00:00Z"
//        val b5 = TravisBuildsInfo()
//        b5.started_at = "2021-01-05T01:00:00Z"
//        val b6 = TravisBuildsInfo()
//        b6.started_at = "2021-01-06T01:00:00Z"
//        val b7 = TravisBuildsInfo()
//        b7.started_at = "2021-01-07T01:00:00Z"
//        val b8 = TravisBuildsInfo()
//        b8.started_at = "2021-01-08T01:00:00Z"
//        val b9 = TravisBuildsInfo()
//        b9.started_at = "2021-01-09T01:00:00Z"
//        val b10 = TravisBuildsInfo()
//        b10.started_at = "2021-01-10T01:00:00Z"
//        buildsInfo.add(b1)
//        buildsInfo.add(b2)
//        buildsInfo.add(b3)
//        buildsInfo.add(b4)
//        buildsInfo.add(b5)
//        buildsInfo.add(b6)
//        buildsInfo.add(b7)
//        buildsInfo.add(b8)
//        buildsInfo.add(b9)
//        buildsInfo.add(b10)
//        val startReleaseDate = LocalDateTime.of(2021, 1, 1, 10, 0, 0)
//        val endReleaseDate = LocalDateTime.of(2021, 1, 10, 9, 0, 0)
//        Assertions.assertEquals(
//            BigDecimal("1.0000000000"),
//            metricCalc!!.calcBuildsActivity(buildsInfo, startReleaseDate, endReleaseDate)
//        )
//    }
//
//    @Test
//    fun calcBuildsActivity50Percent() {
//        val buildsInfo: MutableList<TravisBuildsInfo> = java.util.ArrayList()
//        val b1 = TravisBuildsInfo()
//        b1.started_at = "2021-01-02T01:00:00Z"
//        val b2 = TravisBuildsInfo()
//        b2.started_at = "2021-01-03T01:00:00Z"
//        val b3 = TravisBuildsInfo()
//        b3.started_at = "2021-01-04T01:00:00Z"
//        val b4 = TravisBuildsInfo()
//        b4.started_at = "2021-01-06T01:00:00Z"
//        val b5 = TravisBuildsInfo()
//        b5.started_at = "2021-01-07T01:00:00Z"
//        buildsInfo.add(b1)
//        buildsInfo.add(b2)
//        buildsInfo.add(b3)
//        buildsInfo.add(b4)
//        buildsInfo.add(b5)
//        val startReleaseDate = LocalDateTime.of(2021, 1, 1, 10, 0, 0)
//        val endReleaseDate = LocalDateTime.of(2021, 1, 10, 9, 0, 0)
//        Assertions.assertEquals(
//            BigDecimal("0.5000000000"),
//            metricCalc!!.calcBuildsActivity(buildsInfo, startReleaseDate, endReleaseDate)
//        )
//    }
//
//
//    @Test
//    fun calcBuildsActivity10Percent() {
//        val buildsInfo: MutableList<TravisBuildsInfo> = java.util.ArrayList()
//        val b1 = TravisBuildsInfo()
//        b1.started_at = "2021-01-01T01:00:00Z"
//        buildsInfo.add(b1)
//        val startReleaseDate = LocalDateTime.of(2021, 1, 1, 10, 0, 0)
//        val endReleaseDate = LocalDateTime.of(2021, 1, 10, 9, 0, 0)
//        Assertions.assertEquals(
//            BigDecimal("0.1000000000"),
//            metricCalc!!.calcBuildsActivity(buildsInfo, startReleaseDate, endReleaseDate)
//        )
//    }
//
//
////    @Test
////    fun calcBuildsActivity0Percent() {
////        val buildsInfo: List<TravisBuildsInfo> = java.util.ArrayList()
////        val startReleaseDate = LocalDateTime.of(2021, 1, 1, 10, 0, 0)
////        val endReleaseDate = LocalDateTime.of(2021, 1, 10, 9, 0, 0)
////        Assertions.assertEquals(
////            BigDecimal("0.0000000000"),
////            metricCalc!!.calcBuildsActivity(buildsInfo, startReleaseDate, endReleaseDate)
////        )
////    }
//
//    @Test
//    fun calcBuildsActivityReleaseStartEndSameDay() {
//        val buildsInfo: MutableList<TravisBuildsInfo> = java.util.ArrayList()
//        val b1 = TravisBuildsInfo()
//        b1.started_at = "2021-01-10T01:00:00Z"
//        buildsInfo.add(b1)
//        val startReleaseDate = LocalDateTime.of(2021, 1, 10, 10, 0, 0)
//        val endReleaseDate = LocalDateTime.of(2021, 1, 10, 9, 0, 0)
//        Assertions.assertEquals(
//            BigDecimal("1.0000000000"),
//            metricCalc!!.calcBuildsActivity(buildsInfo, startReleaseDate, endReleaseDate)
//        )
//    }
//
//
//    @Test
//    @Throws(ParseException::class)
//    fun calcCommitsActivity100Percent() {
//        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
//        val commitsInfo: MutableList<GitHubCommitInfo> = java.util.ArrayList()
//        val c1 = GitHubCommitInfo()
//        c1.commit = Commit()
//        c1.commit.author = Author()
//        c1.commit.author.date = format.parse("2021-01-01T01:00:00Z")
//        println(c1.commit.author.date)
//        val c2 = GitHubCommitInfo()
//        c2.commit = Commit()
//        c2.commit.author = Author()
//        c2.commit.author.date = format.parse("2021-01-02T01:00:00Z")
//        val c3 = GitHubCommitInfo()
//        c3.commit = Commit()
//        c3.commit.author = Author()
//        c3.commit.author.date = format.parse("2021-01-03T01:00:00Z")
//        val c4 = GitHubCommitInfo()
//        c4.commit = Commit()
//        c4.commit.author = Author()
//        c4.commit.author.date = format.parse("2021-01-04T01:00:00Z")
//        val c5 = GitHubCommitInfo()
//        c5.commit = Commit()
//        c5.commit.author = Author()
//        c5.commit.author.date = format.parse("2021-01-05T01:00:00Z")
//        commitsInfo.add(c1)
//        commitsInfo.add(c2)
//        commitsInfo.add(c3)
//        commitsInfo.add(c4)
//        commitsInfo.add(c5)
//        val startReleaseDate = LocalDateTime.of(2021, 1, 1, 10, 0, 0)
//        val endReleaseDate = LocalDateTime.of(2021, 1, 5, 9, 0, 0)
//        Assertions.assertEquals(
//            BigDecimal("1.0000000000"),
//            metricCalc!!.calcGitHubCommitsActivity(commitsInfo, startReleaseDate, endReleaseDate)
//        )
//    }
//
//
//    @Test
//    @Throws(ParseException::class)
//    fun calcCommitsActivity50Percent() {
//        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
//        val commitsInfo: MutableList<GitHubCommitInfo> = java.util.ArrayList()
//        val c1 = GitHubCommitInfo()
//        c1.commit = Commit()
//        c1.commit.author = Author()
//        c1.commit.author.date = format.parse("2021-01-02T01:00:00Z")
//        println(c1.commit.author.date)
//        val c2 = GitHubCommitInfo()
//        c2.commit = Commit()
//        c2.commit.author = Author()
//        c2.commit.author.date = format.parse("2021-01-03T01:00:00Z")
//        val c3 = GitHubCommitInfo()
//        c3.commit = Commit()
//        c3.commit.author = Author()
//        c3.commit.author.date = format.parse("2021-01-04T01:00:00Z")
//        val c4 = GitHubCommitInfo()
//        c4.commit = Commit()
//        c4.commit.author = Author()
//        c4.commit.author.date = format.parse("2021-01-06T01:00:00Z")
//        val c5 = GitHubCommitInfo()
//        c5.commit = Commit()
//        c5.commit.author = Author()
//        c5.commit.author.date = format.parse("2021-01-07T01:00:00Z")
//        commitsInfo.add(c1)
//        commitsInfo.add(c2)
//        commitsInfo.add(c3)
//        commitsInfo.add(c4)
//        commitsInfo.add(c5)
//        val startReleaseDate = LocalDateTime.of(2021, 1, 1, 10, 0, 0)
//        val endReleaseDate = LocalDateTime.of(2021, 1, 10, 9, 0, 0)
//        Assertions.assertEquals(
//            BigDecimal("0.5000000000"),
//            metricCalc!!.calcGitHubCommitsActivity(commitsInfo, startReleaseDate, endReleaseDate)
//        )
//    }
//
//
//    @Test
//    @Throws(ParseException::class)
//    fun calcCommitsActivity00Percent() {
//        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
//        val commitsInfo: MutableList<GitHubCommitInfo> = java.util.ArrayList()
//        val c1 = GitHubCommitInfo()
//        c1.commit = Commit()
//        c1.commit.author = Author()
//        c1.commit.author.date = format.parse("2021-01-12T01:00:00Z")
//        println(c1.commit.author.date)
//        val c2 = GitHubCommitInfo()
//        c2.commit = Commit()
//        c2.commit.author = Author()
//        c2.commit.author.date = format.parse("2021-01-13T01:00:00Z")
//        val c3 = GitHubCommitInfo()
//        c3.commit = Commit()
//        c3.commit.author = Author()
//        c3.commit.author.date = format.parse("2021-01-14T01:00:00Z")
//        val c4 = GitHubCommitInfo()
//        c4.commit = Commit()
//        c4.commit.author = Author()
//        c4.commit.author.date = format.parse("2021-01-16T01:00:00Z")
//        val c5 = GitHubCommitInfo()
//        c5.commit = Commit()
//        c5.commit.author = Author()
//        c5.commit.author.date = format.parse("2021-01-17T01:00:00Z")
//        commitsInfo.add(c1)
//        commitsInfo.add(c2)
//        commitsInfo.add(c3)
//        commitsInfo.add(c4)
//        commitsInfo.add(c5)
//        val startReleaseDate = LocalDateTime.of(2021, 1, 1, 10, 0, 0)
//        val endReleaseDate = LocalDateTime.of(2021, 1, 10, 9, 0, 0)
//        Assertions.assertEquals(
//            BigDecimal("0.0000000000"),
//            metricCalc!!.calcGitHubCommitsActivity(commitsInfo, startReleaseDate, endReleaseDate)
//        )
//    }
//
//    @Test
//    @Throws(ParseException::class)
//    fun calcCommitsActivity100Percent() {
//        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
//        val commitsInfo: MutableList<GitLabCommitInfo> = java.util.ArrayList()
//        val c1 = GitLabCommitInfo()
//        c1.committed_date = format.parse("2021-01-01T01:00:00Z")
//        println(c1.committed_date)
//        val c2 = GitLabCommitInfo()
//        c2.committed_date = format.parse("2021-01-02T01:00:00Z")
//        val c3 = GitLabCommitInfo()
//        c3.committed_date = format.parse("2021-01-03T01:00:00Z")
//        val c4 = GitLabCommitInfo()
//        c4.committed_date = format.parse("2021-01-04T01:00:00Z")
//        val c5 = GitLabCommitInfo()
//        c5.committed_date  = format.parse("2021-01-05T01:00:00Z")
//        commitsInfo.add(c1)
//        commitsInfo.add(c2)
//        commitsInfo.add(c3)
//        commitsInfo.add(c4)
//        commitsInfo.add(c5)
//        val startReleaseDate = LocalDateTime.of(2021, 1, 1, 10, 0, 0)
//        val endReleaseDate = LocalDateTime.of(2021, 1, 5, 9, 0, 0)
//        Assertions.assertEquals(
//            BigDecimal("1.0000000000"),
//            metricCalc!!.calcGitLabCommitsActivity(commitsInfo, startReleaseDate, endReleaseDate)
//        )
//    }
//
//
//    @Test
//    @Throws(ParseException::class)
//    fun calcCommitsActivity50Percent() {
//        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
//        val commitsInfo: MutableList<GitLabCommitInfo> = java.util.ArrayList()
//        val c1 = GitLabCommitInfo()
//        c1.committed_date = format.parse("2021-01-02T01:00:00Z")
//        println(c1.committed_date)
//        val c2 = GitLabCommitInfo()
//        c2.committed_date = format.parse("2021-01-03T01:00:00Z")
//        val c3 = GitLabCommitInfo()
//        c3.committed_date = format.parse("2021-01-04T01:00:00Z")
//        val c4 = GitLabCommitInfo()
//        c4.committed_date = format.parse("2021-01-06T01:00:00Z")
//        val c5 = GitLabCommitInfo()
//        c5.committed_date = format.parse("2021-01-07T01:00:00Z")
//        commitsInfo.add(c1)
//        commitsInfo.add(c2)
//        commitsInfo.add(c3)
//        commitsInfo.add(c4)
//        commitsInfo.add(c5)
//        val startReleaseDate = LocalDateTime.of(2021, 1, 1, 10, 0, 0)
//        val endReleaseDate = LocalDateTime.of(2021, 1, 10, 9, 0, 0)
//        Assertions.assertEquals(
//            BigDecimal("0.5000000000"),
//            metricCalc!!.calcGitLabCommitsActivity(commitsInfo, startReleaseDate, endReleaseDate)
//        )
//    }
//
//
//    @Test
//    @Throws(ParseException::class)
//    fun calcCommitsActivity00Percent() {
//        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
//        val commitsInfo: MutableList<GitLabCommitInfo> = java.util.ArrayList()
//        val c1 = GitLabCommitInfo()
//        c1.committed_date = format.parse("2021-01-12T01:00:00Z")
//        println(c1.committed_date)
//        val c2 = GitLabCommitInfo()
//        c2.committed_date = format.parse("2021-01-13T01:00:00Z")
//        val c3 = GitLabCommitInfo()
//        c3.committed_date = format.parse("2021-01-14T01:00:00Z")
//        val c4 = GitLabCommitInfo()
//        c4.committed_date = format.parse("2021-01-16T01:00:00Z")
//        val c5 = GitLabCommitInfo()
//        c5.committed_date = format.parse("2021-01-17T01:00:00Z")
//        commitsInfo.add(c1)
//        commitsInfo.add(c2)
//        commitsInfo.add(c3)
//        commitsInfo.add(c4)
//        commitsInfo.add(c5)
//        val startReleaseDate = LocalDateTime.of(2021, 1, 1, 10, 0, 0)
//        val endReleaseDate = LocalDateTime.of(2021, 1, 10, 9, 0, 0)
//        Assertions.assertEquals(
//            BigDecimal("0.0000000000"),
//            metricCalc!!.calcGitLabCommitsActivity(commitsInfo, startReleaseDate, endReleaseDate)
//        )
//    }

}