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

import br.com.jadson.gaugeci.gauges.CommentsPerChangeGauge
import br.com.jadson.gaugeci.model.CommentOfAnalysis
import br.com.jadson.gaugeci.model.StatisticalMeasure
import br.com.jadson.snooper.gitlab.data.merge.GitLabMergeRequestInfo
import br.com.jadson.snooper.gitlab.operations.GitLabCommentsQueryExecutor
import br.com.jadson.snooper.gitlab.operations.GitLabMergeRequestQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.collectors.dtos.CollectResult
import br.ufrn.caze.holterci.domain.models.metric.*
import br.ufrn.caze.holterci.domain.utils.GitLabUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class CommentByMergeRequestGitlabCollector
    : Collector(UUID.fromString("8844bf2d-fd3b-47ec-ac67-dd0a3e3a2cb2"), Metric.COMMENTS_PER_CHANGE, "Comments Per Merge Request at Gitlab", MetricRepository.GITLAB){

    /** cache because de API do not have filter, so we always have to get all information, and it is very slow */
    var cache = listOf<GitLabMergeRequestInfo>()

    @Autowired
    lateinit var gitLabUtils: GitLabUtil

    override fun calcMetricValue(period: Period, @SuppressWarnings("unused") globalPeriod: Period, project: Project): CollectResult {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)


        val executorMergs = GitLabMergeRequestQueryExecutor()
        executorMergs.setGitlabURL(projectConfiguration.mainRepository.url)
        executorMergs.setGitlabToken(projectConfiguration.mainRepository.token)
        executorMergs.setDisableSslVerification(disableSslVerification)
        executorMergs.setQueryParameters(arrayOf("state=all"))
        executorMergs.setPageSize(100)

        val executor = GitLabCommentsQueryExecutor()
        executor.setGitlabURL(projectConfiguration.mainRepository.url)
        executor.setGitlabToken(projectConfiguration.mainRepository.token)
        executor.setDisableSslVerification(disableSslVerification)
        executor.setPageSize(100)

        if(cache.isEmpty()){
            var all = executorMergs.mergeRequests(project.organization + "/" + project.name)
            cache = all
        }

        val mergeInPeriod = gitLabUtils.getMRsInPeriod(cache, period.init, period.end)

        var commentsOfAnalysis : MutableList<CommentOfAnalysis> = ArrayList()

        for (mergeRequest in mergeInPeriod) {
            val allCommentsList =  executor.getMergeRequestDiscussion(project.organization + "/" + project.name, mergeRequest.iid)

            // split the qtd of comments by PR
            for (c in allCommentsList) {
                for (n in c.notes) {
                    val ca = CommentOfAnalysis()
                    ca.commentId = n.id.toLong()
                    ca.createdAt = dateUtil.toLocalDateTime(n.created_at)
                    ca.changeNumber = mergeRequest.id.toLong()
                    commentsOfAnalysis.add(ca)
                }
            }
        }


        return CollectResult( CommentsPerChangeGauge()
            .calcCommentsPerChange(commentsOfAnalysis, period.init, period.end, StatisticalMeasure.MEAN).value, generateMetricInfo(period, commentsOfAnalysis, mergeInPeriod), null)
    }


    override fun cleanCache() {
        cache = emptyList()
    }

    fun generateMetricInfo(period: Period, commentsOfAnalysis : List<CommentOfAnalysis>, mergeInPeriod : List<GitLabMergeRequestInfo> ): String{

        val mergeStateCountMap = mutableMapOf<String, Int>()
        val issueNumberCountMap = mutableMapOf<Int, Int>()


        for (mr in mergeInPeriod) {
            // Count occurrences of each status
            mergeStateCountMap[mr.state] = mergeStateCountMap.getOrDefault(mr.state, 0) + 1
            issueNumberCountMap[mr.iid] = issueNumberCountMap.getOrDefault(mr.iid, 0) + 1
        }

        val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        return  "<strong> [ "+ dtf.format(period.init ) +" to "+ dtf.format(period.end) + "] </strong> <br> "+
                "  - Comments: "+commentsOfAnalysis.size+" <br>"+
                "  - Merges Requests: "+mergeInPeriod.size+" <br>"+
                "  - Merge Requests States: "+mergeStateCountMap+" <br>"+
                "  - Merge Requests Numbers: "+issueNumberCountMap+" <br>"
    }

}