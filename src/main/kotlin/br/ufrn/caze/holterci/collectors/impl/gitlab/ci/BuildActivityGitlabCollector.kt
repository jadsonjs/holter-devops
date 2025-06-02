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

import br.com.jadson.gaugeci.gauges.BuildActivityGauge
import br.com.jadson.gaugeci.model.BuildOfAnalysis
import br.com.jadson.gaugeci.utils.GaugeMathUtils
import br.com.jadson.snooper.gitlab.data.pipeline.GitLabPipelineInfo
import br.com.jadson.snooper.gitlab.operations.GitlabPipelineQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.collectors.dtos.CollectResult
import br.ufrn.caze.holterci.domain.models.metric.*
import br.ufrn.caze.holterci.domain.utils.GitLabUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class BuildActivityGitlabCollector
    : Collector(UUID.fromString("183c57c3-7227-4325-958a-95a2710bea7f"), Metric.BUILD_ACTIVITY, "Build Activity at Gitlab", MetricRepository.GITLAB){

    /** cache because de API do not have filter, so we always have to get all information, and it is very slow */
    var cache = listOf<GitLabPipelineInfo>()

    @Autowired
    lateinit var gitLabUtils: GitLabUtil

    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): CollectResult {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

        val executor = GitlabPipelineQueryExecutor()
        executor.setGitlabURL(projectConfiguration.mainRepository.url)
        executor.setGitlabToken(projectConfiguration.mainRepository.token)
        executor.setDisableSslVerification(disableSslVerification)
        executor.setPageSize(100)

        if(cache.isEmpty()){
            var all : List<GitLabPipelineInfo> = executor.getPipeLines(project.organization + "/" +project.name)
            cache = all
        }else{
            println("$$$$$$$$$$ reusing the cache $$$$$$$$$")
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
            buildsOfAnalysis.add(b1);
        }

        return CollectResult( BuildActivityGauge()
            .calcBuildActivity(buildsOfAnalysis, period.init, period.end).value, generateMetricInfo(period, pipesInPeriod), null)
    }

    /**
     *  Between collection cycle, we need to clean the cache.
     *  The cache was made just to be used inside the same collection cycle.
     */
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
                "  - Builds: "+pipesInPeriod.size+" <br>"+
                "  - Branches: "+branches.joinToString("; ")+" <br>"+
                "  - Builds Status: "+statusCountMap+" <br>"
    }


    fun calcBuildActivityValues(builds: List<BuildOfAnalysis>, init: LocalDateTime, end: LocalDateTime): BigDecimal? {

        val qtdTotalDays = dateUtil.daysBetweenInclusiveWithOutWeekends(init, end)  // include the start and end.

        var currentReleaseDate = init
        var qtdDaysWithBuilds: Long = 0

        for (index in 0 until qtdTotalDays) {
            buildsFor@
            for (build in builds) {
                if (build.startedAt != null) {

                    // there is a build in this day
                    if (currentReleaseDate.dayOfMonth == build.startedAt.dayOfMonth && currentReleaseDate.monthValue == build.startedAt.monthValue && currentReleaseDate.year == build.startedAt.year) {
                        qtdDaysWithBuilds++
                        break@buildsFor
                    }
                }
            }
            currentReleaseDate = currentReleaseDate.plusDays(1)
        }

        return BigDecimal(qtdDaysWithBuilds).divide(BigDecimal(qtdTotalDays), GaugeMathUtils.SCALE, RoundingMode.HALF_UP)
    }
}