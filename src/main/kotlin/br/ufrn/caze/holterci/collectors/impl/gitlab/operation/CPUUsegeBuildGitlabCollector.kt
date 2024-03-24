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
package br.ufrn.caze.holterci.collectors.impl.gitlab.operation

import br.com.jadson.gaugeci.model.BuildOfAnalysis
import br.com.jadson.snooper.gitlab.data.pipeline.GitLabPipelineInfo
import br.com.jadson.snooper.gitlab.operations.GitlabPipelineQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricRepository
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.utils.GitLabUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
class CPUUsegeBuildGitlabCollector
    : Collector(UUID.fromString("fced48c3-31f2-438e-9c1a-460912757ee9"), Metric.INFRASTRUCTURE_COSTS, "Total of PipeLine Duration at Gitlab", MetricRepository.GITLAB){


    /** cache because de API do not have filter, so we always have to get all information, and it is very slow */
    var cache = listOf<GitLabPipelineInfo>()

    @Autowired
    lateinit var gitLabUtils: GitLabUtil

    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): Pair<BigDecimal, String>  {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

        val executor = GitlabPipelineQueryExecutor()
        executor.setGitlabURL(projectConfiguration.mainRepositoryURL)
        executor.setGitlabToken(projectConfiguration.mainRepositoryToken)
        executor.setDisableSslVerification(disableSslVerification)
        executor.setPageSize(100)

        if(cache.isEmpty()){
            var all : List<GitLabPipelineInfo> = executor.getPipeLines(project.organization + "/" +project.name)
            cache = all
        }

        var pipesOfPeriod = gitLabUtils.getPipeLinesInPeriod(cache, period.init, period.end)

        var totalCPUTime : BigDecimal = BigDecimal.ZERO

        for (pipe in pipesOfPeriod) {
            val b1 = BuildOfAnalysis()
            /*
             * created and updated is almost the started and finished
             * the list method of API do not return all pipelines information
             */
            b1.startedAt = dateUtil.toLocalDateTime(pipe.created_at)
            b1.finishedAt = dateUtil.toLocalDateTime(pipe.updated_at)

            var durationInSeconds = dateUtil.secondsBetween(dateUtil.toLocalDateTime(pipe.created_at), dateUtil.toLocalDateTime(pipe.updated_at) )
            totalCPUTime = totalCPUTime.add(BigDecimal(durationInSeconds))
        }

        return Pair(dateUtil.secondsToHours(totalCPUTime), generateMetricInfo(period, pipesOfPeriod, totalCPUTime))

    }

    override fun cleanCache() {
        cache = emptyList()
    }

    fun generateMetricInfo(period: Period, pipesOfPeriod : List<GitLabPipelineInfo>, totalCPUTime : BigDecimal): String{

        return  "<strong> [ "+ dateUtil.format(period.init, "dd/MM/yyyy" ) +" to "+ dateUtil.format(period.end, "dd/MM/yyyy") + "] </strong> <br> "+
        "  - Builds: "+pipesOfPeriod.size+" <br>"
        "  - Builds Duration SUM: "+totalCPUTime+" <br>"
    }
}