package br.ufrn.caze.holterci.domain.utils

import br.com.jadson.snooper.gitlab.data.issue.GitLabIssueInfo
import br.com.jadson.snooper.gitlab.data.merge.GitLabMergeRequestInfo
import br.com.jadson.snooper.gitlab.data.pipeline.GitLabPipelineInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class GitLabUtil {

    @Autowired
    lateinit var dateUtil: DateUtil

    fun getIssueClosedInPeriod(list: List<GitLabIssueInfo>, initPeriod: LocalDateTime, endPeriod: LocalDateTime): List<GitLabIssueInfo> {
        val finalList: MutableList<GitLabIssueInfo> = ArrayList()
        for (element in list) {
            if (element.closed_at != null) {
                val date = dateUtil.toLocalDateTime(element.closed_at)
                if( dateUtil.isBetweenDates(date, initPeriod, endPeriod) ){
                    finalList.add(element)
                }
            }
        }
        return finalList
    }

    fun getIssueInPeriod(list: List<GitLabIssueInfo>, initPeriod: LocalDateTime, endPeriod: LocalDateTime): List<GitLabIssueInfo> {
        val finalList: MutableList<GitLabIssueInfo> = ArrayList()
        for (element in list) {
            if (element.created_at != null) {
                val date = dateUtil.toLocalDateTime(element.created_at)
                if( dateUtil.isBetweenDates(date, initPeriod, endPeriod) ){
                    finalList.add(element)
                }
            }
        }
        return finalList
    }


    fun getMRsClosedInPeriod(list: List<GitLabMergeRequestInfo>, initPeriod: LocalDateTime, endPeriod: LocalDateTime): List<GitLabMergeRequestInfo> {
        val finalList: MutableList<GitLabMergeRequestInfo> = ArrayList()
        for (element in list) {
            if (element.closed_at != null) {
                val date = dateUtil.toLocalDateTime(element.closed_at)
                if( dateUtil.isBetweenDates(date, initPeriod, endPeriod) ){
                    finalList.add(element)
                }
            }
        }
        return finalList
    }


    fun getMRsInPeriod(list: List<GitLabMergeRequestInfo>, initPeriod: LocalDateTime, endPeriod: LocalDateTime): List<GitLabMergeRequestInfo> {
        val finalList: MutableList<GitLabMergeRequestInfo> = ArrayList()
        for (element in list) {
            if (element.created_at != null) {
                val date = dateUtil.toLocalDateTime(element.created_at)
                if( dateUtil.isBetweenDates(date, initPeriod, endPeriod) ){
                    finalList.add(element)
                }
            }
        }
        return finalList
    }

    fun getPipeLinesInPeriod(list: List<GitLabPipelineInfo>, initPeriod: LocalDateTime, endPeriod: LocalDateTime): List<GitLabPipelineInfo> {
        val finalList : MutableList<GitLabPipelineInfo> = ArrayList()
        for (element in list) {
            if (element.created_at != null) {
                val date = dateUtil.toLocalDateTime(element.created_at)
                if( dateUtil.isBetweenDates(date, initPeriod, endPeriod) ){
                    finalList.add(element)
                }
            }
        }
        return finalList
    }
}

