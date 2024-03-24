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

import br.com.jadson.gaugeci.gauges.TimeToFixBrokenBuildGauge
import br.com.jadson.gaugeci.model.BuildOfAnalysis
import br.com.jadson.gaugeci.model.StatisticalMeasure
import br.com.jadson.gaugeci.model.UnitOfTime
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
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class TimeToFixBrokenBuildGitlabCollector
    : Collector(UUID.fromString("a671ed57-2637-4d25-b2ef-e1c33bd733fe"), Metric.TIME_TO_FIX_BROKEN_BUILD, "Time to Fix Broken Build at Gitlab", MetricRepository.GITLAB){

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

        var pipesInPeriod = gitLabUtils.getPipeLinesInPeriod(cache, period.init, period.end)

        val buildsOfAnalysis: MutableList<BuildOfAnalysis> = ArrayList()

        for (pipe in pipesInPeriod) {
            val b1 = BuildOfAnalysis()
            /*
             * created and updated is almost the started finished
             * the list method of API do not return all pipelines information
             */
            b1.startedAt = dateUtil.toLocalDateTime(pipe.created_at)
            b1.finishedAt = dateUtil.toLocalDateTime(pipe.updated_at)
            b1.state = pipe.status // build status success or  failed
            buildsOfAnalysis.add(b1);
        }


        return Pair(TimeToFixBrokenBuildGauge("success", "failed")
                .calcTimeToFixBrokenBuild(buildsOfAnalysis, StatisticalMeasure.MEAN, UnitOfTime.HOURS).value, generateMetricInfo(period, pipesInPeriod))

    }

    override fun cleanCache() {
        cache = emptyList()
    }

    fun generateMetricInfo(period: Period, pipesInPeriod: List<GitLabPipelineInfo>): String{

        var branches = mutableSetOf<String>()

        val statusCountMap = mutableMapOf<String, Int>()


        for (pipe in pipesInPeriod) {
            branches.add(pipe.ref)
            // Count occurrences of each status
            statusCountMap[pipe.status] = statusCountMap.getOrDefault(pipe.status, 0) + 1
        }

        val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        return  "<strong> [ "+ dtf.format(period.init ) +" to "+ dtf.format(period.end) + "] </strong> <br> "+
                " - Builds: "+pipesInPeriod.size+" <br>"+
                " - Branches: "+branches.joinToString("; ")+" <br>"+
                " - Builds Status: "+statusCountMap+" <br>"
    }


}