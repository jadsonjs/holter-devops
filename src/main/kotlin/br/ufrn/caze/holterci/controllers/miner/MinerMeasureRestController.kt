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
 * CollectMeasureController
 * 01/07/21
 */
package br.ufrn.caze.holterci.controllers.miner;

import br.ufrn.caze.holterci.controllers.AbstractRestController
import br.ufrn.caze.holterci.converters.PeriodDtoConverter
import br.ufrn.caze.holterci.converters.SchedulerDtoConverter
import br.ufrn.caze.holterci.domain.dtos.MessageDto
import br.ufrn.caze.holterci.domain.dtos.crud.PeriodDto
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.models.metric.Scheduler
import br.ufrn.caze.holterci.domain.services.miner.MinerMetricsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Start the miner of metric for a project.
 *
 * Jadson Santos - jadsonjs@gmail.com
 */
@RestController
@RequestMapping("/api/miner-measure")
class MinerMeasureRestController
     (
    private val service : MinerMetricsService,
    private val schedulerConverter: SchedulerDtoConverter,
    private val periodConverter: PeriodDtoConverter,
     )
    : AbstractRestController()
{

    /**
     * load information about miner execution to selected project
     */
    @GetMapping("/load/{idProject}")
    fun loadNextMinerPeriod(@PathVariable idProject: Long) : ResponseEntity<Array<Any>> {

        var data = service.defineMinePeriod(idProject);

        var d1 : Project = data[0] as Project
        var d2 : Scheduler = data[1] as Scheduler
        var d3 : Period = data[2] as Period

        var cycleNoCompleted =  d3.init.equals(d3.end) // if the new cycle of collection is complete.

        return ResponseEntity(arrayOf(d1, schedulerConverter.toDto(d2), periodConverter.toDto(d3), cycleNoCompleted), HttpStatus.OK)
    }


    /**
     * This method execute the mine of metric !!!!!!!!!!!
     * @param the period to the mining, defined in previous step
     */
    @PostMapping("/mine")
    fun mine(@RequestBody period: PeriodDto) : ResponseEntity<MessageDto> {
        var project = service.mine(periodConverter.toModel(period))
        return ResponseEntity(MessageDto("Data collected to the project " + project.name + " with success."), HttpStatus.OK)
    }




}