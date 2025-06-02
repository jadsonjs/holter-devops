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
 * holter-ci
 * br.ufrn.caze.publicano.domain.collectors.github
 * NClosedMergeRequestsGitabCollector
 * 20/07/21
 */
package br.ufrn.caze.holterci.collectors.impl.gitlab.performance;

import br.com.jadson.snooper.gitlab.data.merge.GitLabMergeRequestInfo
import br.com.jadson.snooper.gitlab.operations.GitLabMergeRequestQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.collectors.dtos.CollectResult
import br.ufrn.caze.holterci.domain.models.metric.*
import br.ufrn.caze.holterci.domain.utils.GitLabUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*


/**
 * This class is a collector of merge requests in Git Lab
 *
 * @author bruno batista - brunokaike@ufrn.edu.br
 */
@Component
class NClosedMergeRequestsGitabCollector
    : Collector(UUID.fromString("c171211d-8409-4cb3-9a67-74b5cb889175"), Metric.NUMBER_OF_CHANGES_DELIVERED, "Number of Closed Merge Requests at GitLab", MetricRepository.GITLAB){

    @Autowired
    lateinit var gitLabUtils: GitLabUtil

    var mergeRequestCache = mutableListOf<GitLabMergeRequestInfo>()

    /**
     * GET ALL MERGE REQUESTS closed in period
     *
     */
    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): CollectResult {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

        val executor = GitLabMergeRequestQueryExecutor()
        executor.setGitlabURL(projectConfiguration.mainRepository.url)
        executor.setGitlabToken(projectConfiguration.mainRepository.token)
        executor.setDisableSslVerification(disableSslVerification)
        executor.setQueryParameters(arrayOf("state=closed"))
        executor.setPageSize(100)

        // bring all issues first time to memory
        if(mergeRequestCache.isEmpty()){
            var allMerges = executor.mergeRequestsInPeriod(project.organization + "/" +project.name, globalPeriod.init, globalPeriod.end)
            mergeRequestCache = allMerges
        }

        // get from the list of global PR, that in the period selected
        // why?  because the github API, there is no a filter by date, so we have to bing all form the API and filter manually.
        // this is time consuming
        var mergeRequestClosedOfPeriod = gitLabUtils.getMRsClosedInPeriod(mergeRequestCache, period.init, period.end);
        return CollectResult(BigDecimal(mergeRequestClosedOfPeriod.size), generateMetricInfo(period , mergeRequestClosedOfPeriod), null)

    }

    override fun cleanCache() {
        mergeRequestCache.clear()
    }

    fun generateMetricInfo(period: Period, mergeRequestClosedOfPeriod : List<GitLabMergeRequestInfo> ): String{

        val statusCountMap = mutableMapOf<String, Int>()
        var issueUniqueNumbers = mutableSetOf<Int>()

        for (issue in mergeRequestClosedOfPeriod) {
            // Count occurrences of each status
            statusCountMap[issue.state] = statusCountMap.getOrDefault(issue.state, 0) + 1
            issueUniqueNumbers.add(issue.iid)
        }

        return  "<strong> [ "+ dateUtil.format(period.init, "dd/MM/yyyy" ) +" to "+ dateUtil.format(period.end, "dd/MM/yyyy") + "] </strong> <br> "+
                "  - Closed Merge Requests: "+mergeRequestClosedOfPeriod.size+" <br>"+
                "  - Closed Merge Requests Status: "+statusCountMap+" <br>"+
                "  - Closed Merge Requests Numbers: "+issueUniqueNumbers+" <br>"
    }

}