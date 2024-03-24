package br.ufrn.caze.holterci.domain.utils

import br.com.jadson.snooper.github.data.issue.GitHubIssueInfo
import br.com.jadson.snooper.github.data.pull.GitHubPullRequestInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class GitHubUtil {

    @Autowired
    lateinit var dateUtil: DateUtil

    fun getIssueClosedInPeriod(issues: List<GitHubIssueInfo>, initPeriod: LocalDateTime, endPeriod: LocalDateTime): List<GitHubIssueInfo> {
        val issuesOfRelease: MutableList<GitHubIssueInfo> = ArrayList()
        for (issue in issues) {
            if (issue.closed_at != null) {
                val closeIssueDate = dateUtil.toLocalDateTime(issue.closed_at)
                if ( closeIssueDate.isAfter(initPeriod) && closeIssueDate.isBefore(endPeriod) ) { // this issue if of this release
                    issuesOfRelease.add(issue)
                }
            }
        }
        return issuesOfRelease
    }

    fun getPRsClosedInPeriod(pullRequest: MutableList<GitHubPullRequestInfo>, initPeriod: LocalDateTime, endPeriod: LocalDateTime): List<GitHubPullRequestInfo> {
        val prsRelease: MutableList<GitHubPullRequestInfo> = ArrayList()
        for (pr in pullRequest) {
            if (pr.closed_at != null) {
                val closeIssueDate = dateUtil.toLocalDateTime(pr.closed_at)
                if ( closeIssueDate.isAfter(initPeriod) && closeIssueDate.isBefore(endPeriod) ) { // this issue if of this release
                    prsRelease.add(pr)
                }
            }
        }
        return prsRelease
    }
}

