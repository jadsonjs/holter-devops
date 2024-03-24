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
 * holter-devops
 * br.ufrn.caze.publicano
 * Collector
 * 21/05/21
 */
package br.ufrn.caze.holterci.collectors

import br.ufrn.caze.holterci.domain.models.metric.*
import br.ufrn.caze.holterci.domain.ports.repositories.crud.MetricHistoryRepository
import br.ufrn.caze.holterci.domain.ports.repositories.crud.PeriodRepository
import br.ufrn.caze.holterci.domain.ports.repositories.crud.ProjectRepository
import br.ufrn.caze.holterci.domain.ports.repositories.crud.SchedulerRepository
import br.ufrn.caze.holterci.domain.utils.DateUtil
import br.ufrn.caze.holterci.domain.utils.MathUtil
import br.ufrn.caze.holterci.domain.utils.MetricCalcUtil
import jakarta.validation.constraints.NotEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import java.math.BigDecimal
import java.util.*

/**
 * Abstraction of a Collector
 *
 * Collector is a component to that will collect the CI metric.
 *
 * @author bruno batista - brunokaike@ufrn.edu.br
 */
abstract class Collector
{

    var id : UUID? = null;

    /**
     * Metric to be collected.
     */
    var metric : Metric

    /**
     * The metric collector name, usually this field is used as
     * an external collector name ID.
     */
    @NotEmpty
    var description : String = "";

    /**
     * Repository where the Metric will be collected.
     */
    var repository : MetricRepository

    @Autowired
    lateinit var metricHistoryRepository : MetricHistoryRepository

    @Autowired
    lateinit var periodRepository : PeriodRepository

    @Autowired
    lateinit var schedulerRepository : SchedulerRepository

    @Autowired
    lateinit var dateUtil: DateUtil

    @Autowired
    lateinit var mathUtil: MathUtil

    @Autowired
    lateinit var metricCalc: MetricCalcUtil

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Value("\${disable.ssl-verification}")
    val disableSslVerification : Boolean = false

    constructor(id : UUID, metric : Metric, description : String, repository : MetricRepository) {
        this.id = id
        this.description = description
        this.metric = metric
        this.repository = repository
    }

    /**
     * Meta logic of how calc a metric for a period of analysis.
     * Common part for all collectors
     */
   // open fun collect(project : Project, scheduler : Scheduler, init : LocalDateTime, end : LocalDateTime) { }

    fun collect(project: Project, scheduler : Scheduler, globalPeriod: Period) {

        /**
         * Generate the individual periods, related with the scheduler frequency
         */
        val periodsOfAnalysis: List<Period> = dateUtil.generatePeriodDates(globalPeriod.init, globalPeriod.end, scheduler.frequencyOfExecution, project)

        if(periodsOfAnalysis.size == 0)
            println(">>>>>>>>>>>>>> zero perido of analysis <<<<<<<<<<<")

        var indexPeriodSaved = periodRepository.findLastIndex(project.id!!) + 1

        for (period in periodsOfAnalysis) {

            println("[" + indexPeriodSaved + "] collecting period from: " + period.init + " to: " + period.end )

            var periodSaved = periodRepository.findPeriod(period.init, period.end, project.id!!)

            var data = calcMetricValue(period, globalPeriod, project)

            // the period of the metric was collected
            var metricHistory = MetricHistory()
            if (periodSaved != null) {
                metricHistory.period = periodSaved
            }else{
                metricHistory.period = Period( period.init, period.end, project, indexPeriodSaved ) // create a new period of analysis for this project
            }

            metricHistory.metric     = this.metric
            metricHistory.value      = data.first
            metricHistory.metricInfo = data.second

            metricHistoryRepository.save(metricHistory)


            indexPeriodSaved++
        }

        // update the data of last execution
        scheduler.lastExecution = globalPeriod.end
        schedulerRepository.updateLastExecution(scheduler)

    }

    /**
     * This method should be overridden to calc spefic metric
     */
    abstract fun calcMetricValue(period: Period, globalPeriod: Period, project: Project):  Pair<BigDecimal, String>

    /**
     * Clean the cache
     */
    abstract fun cleanCache()

}