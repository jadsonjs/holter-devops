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
 * br.ufrn.caze.publicano.domain.services.miner
 * MinerMeasureService
 * 01/07/21
 */
package br.ufrn.caze.holterci.domain.services.miner

import br.ufrn.caze.holterci.collectors.CollectorFactory
import br.ufrn.caze.holterci.domain.exceptions.BusinessException
import br.ufrn.caze.holterci.domain.models.division.MetricStage
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.models.metric.Scheduler
import br.ufrn.caze.holterci.domain.ports.repositories.crud.MetricHistoryRepository
import br.ufrn.caze.holterci.domain.ports.repositories.crud.PeriodRepository
import br.ufrn.caze.holterci.domain.ports.repositories.crud.ProjectRepository
import br.ufrn.caze.holterci.domain.ports.repositories.crud.SchedulerRepository
import br.ufrn.caze.holterci.domain.utils.DateUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Service with rule to miner the metric information
 *
 * Jadson Santos - jadson.santos@ufrn.br
 */
@Service
class MinerMetricsService
    (
    private val projectRepository : ProjectRepository,
    private val schedulerRepository : SchedulerRepository,
    private val periodRepository : PeriodRepository,
    private val collectorFactory: CollectorFactory,
    private val metricHistoryRepository: MetricHistoryRepository,
    private val dateUtil: DateUtil,
){



    /**
     * THIS IS A VERY IMPORTANT METHOD
     *
     * It has the logic to calculate the next period of miner.
     * Every metric in the system have to be insider a period
     *
     * @return Array[0] = project
     *         Array[1] = scheduler
     *         Array[2] = period
     *
     */
    fun defineMinePeriod(idProject: Long) : Array<Any> {

        val project : Project = projectRepository.findProjectById(idProject)
        val scheduler : Scheduler = schedulerRepository.findSchedulerByProject(idProject)
            ?: throw IllegalArgumentException("Scheduler not definied to the project: "+project.name)


        val returnData = Array<Any>(3){ }

        val init: LocalDateTime
        val end : LocalDateTime


        if( scheduler.lastExecution != null ) {
            init = getNextExecutionDates(scheduler, project)[0]
            end = getNextExecutionDates(scheduler, project)[1]
        }else{
            init = getFirstExecutionDates(scheduler)[0]
            end = getFirstExecutionDates(scheduler)[1]
        }


        val period = Period(init, end, project)

        returnData[0] = project
        returnData[1] = scheduler
        returnData[2] = period

        return returnData
    }


    /**
     * Executes metrics mining for the past period
     *
     *
     * @param minerPeriod the general period to miner.
     * If the metric was not miner yet, this period starts at startExecution scheduler data.
     * If the miner already execute, this periods starts at last execution date.
     */
    @Transactional
    fun mine(minerPeriod: Period) : Project {

        val project   : Project = projectRepository.findProjectById(minerPeriod.project.id!!)
        val scheduler : Scheduler = schedulerRepository.findSchedulerByProject(minerPeriod.project.id!!)
            ?: throw IllegalArgumentException("Scheduler not definied to the project: "+project.name)

        /**
         * for all metrics configured for the project
         *
         * returns which collectors are configured for the project and perform mining
         */

        val collectors = collectorFactory.get(scheduler.collectorsId)

        var collectorIndex = 1

        for(collector in collectors){
            try {

                /**
                 * How we can add new metric to be collected, this will check is the specific metric has been collected
                 * If true, will start form the next period,  define by the method  {@link #defineMinePeriod() DefineMinePeriod}
                 * if false, will start from the startExecution defined in the scheduler
                 *
                 * This will be the global period, this global period will be broken in local periods (week, month ou year)
                 */
                var init: LocalDateTime
                var end: LocalDateTime

                if (metricHistoryRepository.isContainsMinerData(collector.metric, project.id!!)) {
                    init = minerPeriod.init
                    end = minerPeriod.end
                } else {
                    init = getFirstExecutionDates(scheduler)[0]
                    end = getFirstExecutionDates(scheduler)[1]
                }

                println("\n----------------------------------------------------------------------------\n")
                println("\n[" + collectorIndex + "] Mining: " + collector.metric + " from: " + init + " to: " + end + "")
                println("\n----------------------------------------------------------------------------\n")


                /** *******************************************************************************************
                 * IMPORTANT: WE NEED TO CLEAN THE CACHE BEFORE A NEW EXECUTION
                 * ********************************************************************************************/
                collector.cleanCache()


                /**
                 * Now checks if we have a whole cycle.
                 * IF is 1 month, we should have an interval between init and end at least one month to be executed.
                 * if we have 15 days, the mine will not be executed for this metric.
                 */
                if (dateUtil.isMinerCycleCompleted(init, end, scheduler.frequencyOfExecution)) {
                    collector.collect(project, scheduler, Period(init, end, project, 0))
                } else {
                    println(
                        ("\t Period from " + DateTimeFormatter.ofPattern("yyyy/MM/dd")
                            .format(init) + " to " + DateTimeFormatter.ofPattern("yyyy/MM/dd")
                            .format(end) + " do not complete a whole cycle.")
                    )
                }

                collectorIndex++
            }catch (ex : Exception){
                ex.printStackTrace()
                throw BusinessException(" Error to Collect Metric: "+collector.metric.denomination+"in ["+collector.description+"]"+" : "+ex.message)
            }

        }

        println("\n============================================================================\n")
        println("\nMETRICS COLLECTION COMPLETED")
        println("\n============================================================================\n")

        return project
    }


    /**
     * there is no execution so
     * collect from the initial data choose by the user until today.
     * simple like that !!!
     *
     * init = startExecution                                             end
     *            |-----|    |-----|       |-----|      |-----|    |-----|
     *
     * 5 news periods have to be colleted
     */
    fun getFirstExecutionDates(scheduler: Scheduler) : Array<LocalDateTime> {
        return arrayOf(scheduler.startExecution, LocalDateTime.now() )
    }

    /**
     * if there is already data, init from next period not collect yet,
     * until today
     *               lastPeriod.end       init = lastPeriod.end +1                          end
     * |XXXXX|     |XXXXX|                 |-----|                   |-----|           |-----|
     *
     * 3 news periods have to be collected
     */
    fun getNextExecutionDates(scheduler: Scheduler, project : Project) : Array<LocalDateTime> {

        val init: LocalDateTime
        var end : LocalDateTime
        var temp : LocalDateTime

        val lastPeriod = periodRepository.findLastPeriod(project.id!!)

        init = lastPeriod.end.plusDays(1)
        end = init
        temp = init

        do {
            temp = dateUtil.incrementPeriod(scheduler.frequencyOfExecution, temp)
            if(temp.isBefore(LocalDateTime.now()))
                end = temp
        } while(temp.isBefore(LocalDateTime.now()))

        return arrayOf(init, end )
    }







}