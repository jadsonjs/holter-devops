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
package br.ufrn.caze.holterci.controllers.crud

import br.ufrn.caze.holterci.controllers.AbstractRestController
import br.ufrn.caze.holterci.converters.MetricReferenceDtoConverter
import br.ufrn.caze.holterci.domain.dtos.MessageDto
import br.ufrn.caze.holterci.domain.dtos.crud.MetricReferenceValuesDto
import br.ufrn.caze.holterci.domain.ports.repositories.crud.MetricReferenceValueRepository
import br.ufrn.caze.holterci.domain.services.crud.MetricReferenceService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ci-metric-reference")
class CIMetricReferenceRestController  // construtor injections of kotlin
    (
    private val MetricReferenceValueRepository : MetricReferenceValueRepository,
    private val metricReferenceService : MetricReferenceService,
    private val metricReferenceDtoConverter: MetricReferenceDtoConverter,
)
    : AbstractRestController()
{


    /**
     * Return all measure references
     */
    @GetMapping
    fun getAll() : ResponseEntity<List<MetricReferenceValuesDto>> {
        return ResponseEntity( metricReferenceDtoConverter.toDtoList(MetricReferenceValueRepository.findAll() ), HttpStatus.OK)
    }

    /**
     * Return all measure references for a project
     */
    @GetMapping("/project/{projectId}")
    fun getAllByProjectId(@PathVariable projectId: Long) : ResponseEntity<List<MetricReferenceValuesDto>> {
        return ResponseEntity( metricReferenceDtoConverter.toDtoList(MetricReferenceValueRepository.findAllByProject(projectId) ), HttpStatus.OK)
    }


    /**
     * Get a specific measure reference for edit
     */
    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long) : MetricReferenceValuesDto {
        return metricReferenceDtoConverter.toDto(MetricReferenceValueRepository.findById(id))
    }


    /**
     * Save a new measure reference
     */
    @PostMapping("/save")
    fun save(@Valid @RequestBody m: MetricReferenceValuesDto) : ResponseEntity<MetricReferenceValuesDto> {
        return ResponseEntity( metricReferenceDtoConverter.toDto( metricReferenceService.save( metricReferenceDtoConverter.toModel(m) ) ), HttpStatus.OK)
    }

    /**
     * Delete a measure reference
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) : ResponseEntity<MessageDto> {
        metricReferenceService.delete(id)
        return ResponseEntity( MessageDto("Reference Removed with Success!") , HttpStatus.OK)
    }

    /**
     * Copy measure references from one project to another
     */
    @PostMapping("/copy/{sourceProjectId}/{targetProjectId}")
    fun copyMetricReferences(@PathVariable sourceProjectId : Long, @PathVariable targetProjectId : Long): ResponseEntity<MessageDto> {
        metricReferenceService.copy(sourceProjectId, targetProjectId)
        return ResponseEntity(MessageDto("Reference values copied successfully!"), HttpStatus.OK)
    }
}