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
 * SchedulerJPARepository
 * 19/05/21
 */
package br.ufrn.caze.holterci.repositories.crud

import br.ufrn.caze.holterci.domain.models.metric.Scheduler
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

/**
 * Basic Repository of Spring Data framework
 * @author bruno batista - brunokaike@ufrn.edu.br
 */
interface SchedulerJPARepository : JpaRepository<Scheduler, Long> {

    @Query(" SELECT s FROM Scheduler s WHERE s.id = ?1")
    fun findSchedulerById(id: Long) : Scheduler

    @Query(" SELECT s FROM Scheduler s WHERE s.project.id = ?1")
    fun findSchedulerByProjectId(id: Long) : Scheduler

    @Query(" SELECT COUNT(*) FROM Scheduler s WHERE s.id <> ?1 AND s.project.id = ?2 ")
    fun countSchedulesOfProject(id: Long?, idProject: Long?) : Long

    @Modifying
    @Query("UPDATE Scheduler s set s.lastExecution = ?2 WHERE s.id = ?1 ")
    fun updateLastExecution(id: Long?, lastExecution: LocalDateTime?): Int

}