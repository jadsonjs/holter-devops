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
 * SchedulerRepositoryImpl
 * 23/05/21
 */
package br.ufrn.caze.holterci.repositories.crud

import br.ufrn.caze.holterci.domain.exceptions.BusinessException
import br.ufrn.caze.holterci.domain.models.metric.Scheduler
import br.ufrn.caze.holterci.domain.ports.repositories.crud.SchedulerRepository
import org.springframework.dao.EmptyResultDataAccessException

/**
 * A concrete implementation of repository.
 *
 * This class should receive the JPA repository and a convert to convert model classes to entities.
 *
 * @author bruno batista - brunokaike@ufrn.edu.br
 */
class SchedulerRepositoryImpl
    (
        // construtor injection. All repository should receive the JPA Repository and a convert
    private val jpaRepository : SchedulerJPARepository,
    )  : SchedulerRepository
    {

    override fun save(s: Scheduler): Scheduler {
        return jpaRepository.save(s)
    }

    override fun delete(id: Long) {
        jpaRepository.deleteById(id)
    }

    override fun updateLastExecution(scheduler: Scheduler): Int {
        return jpaRepository.updateLastExecution(scheduler.id, scheduler.lastExecution)
    }


    ////// queries ///////


    override fun findSchedulerById(id: Long): Scheduler {
        return jpaRepository.findSchedulerById(id)
    }

    override fun findSchedulerByProject(projectId: Long): Scheduler? {
        try {
            return jpaRepository.findSchedulerByProjectId(projectId)
        }catch (e : EmptyResultDataAccessException){
            throw BusinessException("Schedule not register to this project")
        }
    }

    override fun countSchedulesOfProject(scheduler: Scheduler): Long {
        return jpaRepository.countSchedulesOfProject((if (scheduler.id != null) scheduler.id else 0L), scheduler.project.id)
    }




}