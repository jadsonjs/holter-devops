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
 * CollectorController
 * 03/06/21
 */
package br.ufrn.caze.holterci.controllers.crud

import br.ufrn.caze.holterci.collectors.CollectorFactory
import br.ufrn.caze.holterci.controllers.AbstractRestController
import br.ufrn.caze.holterci.converters.CollectorDtoConverter
import br.ufrn.caze.holterci.domain.dtos.crud.CollectorDto
import br.ufrn.caze.holterci.domain.ports.repositories.crud.MetricHistoryRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*


/**
 * Collector controller. Collector are fix in code, we not create it.
 * We need to collect a new time of CI metric, we need to implemente a new collector.
 *
 * @author bruno batista - brunokaike@ufrn.edu.br
 * @author Jadson Santos - jadson.santos@ufrn.br
 */

@RestController
@RequestMapping("/api/collector")
class CollectorRestController
    (
    private val factory: CollectorFactory,
    private val collectorDtoConverter: CollectorDtoConverter,
    private val metricHistoryRepository : MetricHistoryRepository
    )
    : AbstractRestController()
{


    /**
     * return all collectors
     */
    @GetMapping
    fun getAll( ) : List<CollectorDto> {
        return collectorDtoConverter.toDtoList(factory.getAll())
    }

    @GetMapping("/")
    fun getAllSlack( ) : List<CollectorDto> {
        return getAll()
    }

    /**
     * Return the status of a specific collector, if it was executed.
     */
    @GetMapping("/status/{idProject}/{uuids}")
    fun loadCollectoExecutionData(@PathVariable idProject: Long, @PathVariable uuids: List<String>) : ResponseEntity<List<CollectorStatusDto>> {

        var list : MutableList<CollectorStatusDto> = mutableListOf()

        for (uuid in uuids) {
            val collector = factory.get(listOf(UUID.fromString(uuid))).get(0)

            try {
                var period = metricHistoryRepository.findInfoPeriodRegistered(idProject, collector.metric)

                list.add(CollectorStatusDto(uuid, "Collector Already Executed from: " + period.init + " to: " + period.end, true ) )

            } catch (ex: Exception) {
                list.add(CollectorStatusDto(uuid, "Collector not executed yet", false) )
            }
        }

        return ResponseEntity(list, HttpStatus.OK)
    }

    class CollectorStatusDto (
        val uuid: String = "",
        val message: String = "",
        val executed: Boolean = false
    )

}