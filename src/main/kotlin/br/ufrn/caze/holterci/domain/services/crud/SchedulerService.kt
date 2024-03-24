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
 * br.ufrn.caze.publicano
 * SchedulerService
 * 24/05/21
 */
package br.ufrn.caze.holterci.domain.services.crud

import br.ufrn.caze.holterci.domain.exceptions.BusinessException
import br.ufrn.caze.holterci.domain.models.metric.Scheduler
import br.ufrn.caze.holterci.domain.ports.repositories.crud.MetricHistoryRepository
import br.ufrn.caze.holterci.domain.ports.repositories.crud.PeriodRepository
import br.ufrn.caze.holterci.domain.ports.repositories.crud.SchedulerRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalTime

/**
 * Should contain the business rules of application
 *
 * @author bruno batista - brunokaike@ufrn.edu.br
 */
@Service
class SchedulerService
    // construtor injections of kotlin
    (
    private val schedulerRepository : SchedulerRepository,
    private val metricHistoryRepository : MetricHistoryRepository,
    private val periodRepository: PeriodRepository
    )
{

    /**
     * Save a new project on database
     */
    @Transactional
    fun save(s: Scheduler): Scheduler {
        // Business rule
        if(existScheduler (s))
            throw BusinessException("Scheduler with same Project, Metric and Period already registered")

        s.startExecution = s.startExecution.with(LocalTime.MIDNIGHT) // start always 00:00:00

        if(s.collectorsId.size == 0)
            throw BusinessException("Scheduler should have at least one collector")

        return schedulerRepository.save(s)
    }

    @Transactional
    fun clearCollectedData(idScheduler: Long)  {
        var scheduler = schedulerRepository.findSchedulerById(idScheduler);
        if(scheduler.project.id != null) {
            metricHistoryRepository.deleteByProject(scheduler.project.id!!)
            periodRepository.deleteAllBYProject(scheduler.project.id!!)
        }

        schedulerRepository.updateLastExecution(Scheduler(idScheduler)) // update to null
    }

    /**
     * Delete a project from database.
     */
    @Transactional
    fun delete(idScheduler: Long) {
        clearCollectedData(idScheduler)
        schedulerRepository.delete(idScheduler)
    }


    /**
     * Checks if the scheduler already exists.
     */
    private fun existScheduler (s: Scheduler) : Boolean {
        return schedulerRepository.countSchedulesOfProject(s) > 0
    }



}