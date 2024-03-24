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
import br.ufrn.caze.holterci.converters.ControlMetricDtoConverter
import br.ufrn.caze.holterci.domain.dtos.crud.ControlMetricDto
import br.ufrn.caze.holterci.domain.ports.repositories.crud.ControlMetricRepository
import br.ufrn.caze.holterci.domain.services.crud.ControlMetricService
import jakarta.validation.Valid
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * General metric are basic metric about the project.
 * They are used as a *** control variables ***
 *
 * In this first version, this values will be created by the user, not calculated.
 */
@RestController
@RequestMapping("/api/control-metric")
class ControlMetricRestController  (
    private val controlMetricRepository : ControlMetricRepository,
    private val controlMetricDtoConverter : ControlMetricDtoConverter,
    private val controlMetricService : ControlMetricService,
)
    : AbstractRestController()
{


    /**
     * return the general metric of this period
     */
    @GetMapping("/{periodId}")
    fun getByPeriod(@PathVariable("periodId") periodId: Long) : ResponseEntity<ControlMetricDto> {
        try {
            var controlMetric = controlMetricRepository.findByIdPeriod(periodId)
            return ResponseEntity( controlMetricDtoConverter.toDto(controlMetric), HttpStatus.OK)
        } catch (e : EmptyResultDataAccessException) {
            return ResponseEntity(null, HttpStatus.OK)
        }
    }

    /**
     * Save or update new metric values for the period
     */
    @PostMapping("/save")
    fun save(@Valid @RequestBody m: ControlMetricDto) : ResponseEntity<ControlMetricDto> {
        return ResponseEntity( controlMetricDtoConverter.toDto( controlMetricService.save( controlMetricDtoConverter.toModel(m) ) ), HttpStatus.OK)
    }


}