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
import br.ufrn.caze.holterci.converters.PeriodDtoConverter
import br.ufrn.caze.holterci.domain.dtos.PaginationDTO
import br.ufrn.caze.holterci.domain.dtos.crud.PeriodDto
import br.ufrn.caze.holterci.domain.ports.repositories.crud.PeriodRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/period")
class PeriodRestController  (
    private val periodRepository : PeriodRepository,
    private val periodDtoConverter : PeriodDtoConverter,
)
    : AbstractRestController()
{

    /**
     * Return the list of period of a project
     */
    @GetMapping("/list/{idProject}")
    fun getAll(@PathVariable idProject: Long,
               @RequestParam(value = "page", required = true, defaultValue = "1")  page : Int,
               @RequestParam( value = "size", required = true, defaultValue = "5")  size : Int
    ) : ResponseEntity<PaginationDTO<PeriodDto>> {
        var periodsDto = periodDtoConverter.toDtoList(periodRepository.findPeriodsOfProject(idProject, page, size));
        return ResponseEntity( PaginationDTO<PeriodDto>().setResults(periodsDto, page, size, periodRepository.countPeriodsOfProject(idProject)), HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long) : PeriodDto{
        return periodDtoConverter.toDto(periodRepository.findById(id))
    }
}