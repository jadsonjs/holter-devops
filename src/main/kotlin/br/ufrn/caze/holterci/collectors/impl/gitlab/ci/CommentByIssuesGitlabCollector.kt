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
import br.com.jadson.snooper.gitlab.data.issue.GitLabIssueInfo
import br.com.jadson.snooper.gitlab.operations.GitLabCommentsQueryExecutor
import br.com.jadson.snooper.gitlab.operations.GitLabIssueQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricRepository
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.utils.GitLabUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class CommentByIssuesGitlabCollector
    : Collector(UUID.fromString("6e3cfcdb-a58b-4ac9-9a92-47670d989cb3"), Metric.COMMENTS_PER_CHANGE, "Comments Per Issues at Gitlab", MetricRepository.GITLAB){

    /** cache because de API do not have filter, so we always have to get all information, and it is very slow */
    var cache = listOf<GitLabIssueInfo>()

    @Autowired
    lateinit var gitLabUtils: GitLabUtil

    override fun calcMetricValue(period: Period, @SuppressWarnings("unused") globalPeriod: Period, project: Project): Pair<BigDecimal, String>  {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)


        val executorIssues = GitLabIssueQueryExecutor()
        executorIssues.setGitlabURL(projectConfiguration.mainRepositoryURL)
        executorIssues.setGitlabToken(projectConfiguration.mainRepositoryToken)
        executorIssues.setDisableSslVerification(disableSslVerification)
        executorIssues.setQueryParameters(arrayOf("scope=all"))
        executorIssues.setPageSize(100)

        val executor = GitLabCommentsQueryExecutor()
        executor.setGitlabURL(projectConfiguration.mainRepositoryURL)
        executor.setGitlabToken(projectConfiguration.mainRepositoryToken)
        executor.setDisableSslVerification(disableSslVerification)
        executor.setPageSize(100)

        if(cache.isEmpty()){
            var all = executorIssues.issues(project.organization + "/" + project.name)
            cache = all
        }


        val issueInPeriod = gitLabUtils.getIssueInPeriod(cache, period.init, period.end)

        var commentsOfAnalysis : MutableList<CommentOfAnalysis> = ArrayList()

        for (issue in issueInPeriod) {
            val allCommentsList =  executor.getIssueDiscussion(project.organization + "/" + project.name, issue.iid)

            // split the qtd of comments by PR
            for (c in allCommentsList) {
                for (n in c.notes) {
                    val ca = CommentOfAnalysis()
                    ca.commentId = n.id.toLong()
                    ca.createdAt = dateUtil.toLocalDateTime(n.created_at)
                    ca.changeNumber = issue.id.toLong()
                    commentsOfAnalysis.add(ca)
                }
            }
        }


        return Pair( CommentsPerChangeGauge()
            .calcCommentsPerChange(commentsOfAnalysis, period.init, period.end, StatisticalMeasure.MEAN).value, generateMetricInfo(period, commentsOfAnalysis, issueInPeriod) )
    }

    override fun cleanCache() {
        cache = emptyList()
    }

    fun generateMetricInfo(period: Period, commentsOfAnalysis : List<CommentOfAnalysis>, issueInPeriod : List<GitLabIssueInfo> ): String{

        val issueStateCountMap = mutableMapOf<String, Int>()

        var issueUniqueNumbers = mutableSetOf<Int>()


        for (issue in issueInPeriod) {
            // Count occurrences of each status
            issueStateCountMap[issue.state] = issueStateCountMap.getOrDefault(issue.state, 0) + 1
            issueUniqueNumbers.add(issue.iid)
        }

        val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        return  "<strong> [ "+ dtf.format(period.init ) +" to "+ dtf.format(period.end) + "] </strong> <br> "+
                "  - Comments: "+commentsOfAnalysis.size+" <br>"+
                "  - Issues: "+issueInPeriod.size+" <br>"+
                "  - Issues States: "+issueStateCountMap+" <br>"+
                "  - Issues Numbers: "+issueUniqueNumbers+" <br>"
    }

}