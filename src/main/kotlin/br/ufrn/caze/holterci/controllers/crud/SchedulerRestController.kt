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
 * br.ufrn.caze.publicano.controllers
 * ProjectController
 * 24/05/21
 */
package br.ufrn.caze.holterci.controllers.crud

import br.ufrn.caze.holterci.controllers.AbstractRestController
import br.ufrn.caze.holterci.converters.SchedulerDtoConverter
import br.ufrn.caze.holterci.domain.dtos.MessageDto
import br.ufrn.caze.holterci.domain.dtos.crud.SchedulerDto
import br.ufrn.caze.holterci.domain.models.metric.Scheduler
import br.ufrn.caze.holterci.domain.ports.repositories.crud.SchedulerRepository
import br.ufrn.caze.holterci.domain.services.crud.SchedulerService
import jakarta.validation.Valid
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Scheduler CRUD controller
 *
 * @author bruno batista - brunokaike@ufrn.edu.br
 */
@RestController
@RequestMapping("/api/scheduler")
class SchedulerRestController
     (
    private val schedulerRepository : SchedulerRepository,
    private val schedulerService : SchedulerService,
    private val schedulerDtoConverter: SchedulerDtoConverter,
     )
    : AbstractRestController()
{

    /**
     * return all schedulers of a project
     */
    @GetMapping("/project/{projectId}")
    fun getByProject(@PathVariable("projectId") projectId: Long) : ResponseEntity<Scheduler> {
        try {
            var scheduler = schedulerRepository.findSchedulerByProject(projectId)
            return ResponseEntity(scheduler, HttpStatus.OK)
        } catch (e : EmptyResultDataAccessException) {
            return ResponseEntity(null, HttpStatus.OK)
        }
    }

    /**
     * Get all data of specific scheduler
     */
    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long) : SchedulerDto {
        return schedulerDtoConverter.toDto(schedulerRepository.findSchedulerById(id))
    }

    /**
     * Save a new scheduler
     */
    @PostMapping("/save")
    fun save(@Valid @RequestBody dto: SchedulerDto) : ResponseEntity<SchedulerDto>{

        var scheduler : Scheduler = schedulerDtoConverter.toModel(dto)

        return ResponseEntity( schedulerDtoConverter.toDto( schedulerService.save(scheduler) ), HttpStatus.OK)
    }

    /**
     * Clear the collected data by the Scheduler
     */
    @DeleteMapping("/clear/{id}")
    fun clear(@PathVariable("id") idScheduler: Long) : ResponseEntity<MessageDto>{
        schedulerService.clearCollectedData(idScheduler)
        return ResponseEntity( MessageDto("Scheduler data data successfully cleaned!"), HttpStatus.OK)
    }

    /**
     * Delete a scheduler
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") idScheduler: Long) : ResponseEntity<MessageDto>{
        schedulerService.delete(idScheduler)
        return ResponseEntity( MessageDto("Scheduler Removed with Success!") , HttpStatus.OK)
    }
}