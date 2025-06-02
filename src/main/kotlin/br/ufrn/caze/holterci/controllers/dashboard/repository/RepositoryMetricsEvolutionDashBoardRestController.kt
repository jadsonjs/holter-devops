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
package br.ufrn.caze.holterci.controllers.dashboard.repository

import br.ufrn.caze.holterci.controllers.AbstractRestController
import br.ufrn.caze.holterci.converters.EnumsConverter
import br.ufrn.caze.holterci.domain.dtos.results.EvolutionDto
import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricSegment
import br.ufrn.caze.holterci.domain.ports.repositories.crud.ProjectRepository
import br.ufrn.caze.holterci.domain.ports.repositories.results.MetricEvolutionRepository
import br.ufrn.caze.holterci.domain.utils.LineChartUtil
import br.ufrn.caze.holterci.domain.utils.MetricEvolutionUtil
import br.ufrn.caze.holterci.domain.utils.StringUtil
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

/**
 * This controller return information about the metric evolution over the time
 */
@RestController
@RequestMapping("/api/repository-evolution-dashboard")
class RepositoryMetricsEvolutionDashBoardRestController (
    private val metricEvolutionRepository : MetricEvolutionRepository,
    private val projectRepository : ProjectRepository,
    private val lineChartUtil : LineChartUtil,
    private val metricEvolutionUtil: MetricEvolutionUtil,
    private val enumsConverter : EnumsConverter,
    private val stringUtil: StringUtil,
) : AbstractRestController() {


    /**
     * This method generated the evolution of all CI sub-practices of the period analysed.
     * Genereate the evolution charts for the dashboard
     */
    @GetMapping(value = ["/{organization}/{name}"])
    fun getMetricsEvolution(@PathVariable organization: String, @PathVariable name: String,
                             @RequestParam(name = "init") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) init : LocalDate,
                            @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) end : LocalDate
    ): ResponseEntity<List<EvolutionDto>> {


        var p = projectRepository.findProjectIDByOrganizationAndName(stringUtil.decodeURL(organization), name);

        val listEvolutions: MutableList<EvolutionDto> = ArrayList<EvolutionDto>()

        // all repository metrics
        var metrics = Metric.getAllRepository()

        val allIdentifiers : ArrayList<String> = ArrayList()
        val allValues : MutableList<BigDecimal> = ArrayList()
        var allInfo : ArrayList<String> = ArrayList()

        for (m in metrics){

            allIdentifiers.clear()
            allValues.clear()
            allInfo.clear()

            /**
             * The history of evolution
             * [0] Period(),
             * [1] mHistory.value,
             * [2] mHistory.metric
             * [3] mHistory.metricInfo
             * [4] mHistory.metricSegment
             */
            val data : MutableList<Array<Any>> = metricEvolutionRepository.findMetricEvolution(init.atTime(0,0, 0), end.atTime(23,59, 59), m, p.id!!)

            if(data.size ==0)
                continue

            // organize the list with all periods index and values to the chart
            for (d in data){
                var ms : MetricSegment = d.get(4) as MetricSegment
                allIdentifiers.add(""+" "+ ms.identifier+": ")
                allValues.add(d.get(1) as BigDecimal)
                allInfo.add(if(d.get(3) == null) "" else d.get(3) as String)
            }

            val dto = EvolutionDto()
            dto.id = UUID.randomUUID().toString()
            dto.metric = enumsConverter.metricConverter(m)
            dto.trend = metricEvolutionUtil.calcMetricTrend(m, allValues) // if up or down
            val metricList: List<String> = arrayOf(m.denomination).toList()
            // the chart data for the metric have this format
            // [
            //   ['Identifier', 'Commits per developer'],
            //   ['pedroferreira',  52],
            //   ['fernandooliveira2',  34],
            //   ['henry',  10],
            //   ['oliverph',  103]
            // ],
            //
            // metricList = ['Commits per developer']
            // allIdentifiers = [pedroferreira, fernandooliveira2, henry, oliverph, ...]
            // allValues = [52, 34, 10, 103, ...]
            //
            val aggregatedValuesByIdentifier = (allIdentifiers zip allValues).groupingBy { it.first }
                .fold(BigDecimal.ZERO) { acc, pair -> acc + pair.second }
                .toList()
                .sortedByDescending { it.second }
                .toMap()
            val identifiers = ArrayList(aggregatedValuesByIdentifier.keys)
            val values = aggregatedValuesByIdentifier.values.map { it as BigDecimal }.toMutableList()
            dto.chart = lineChartUtil.generateLinerData("IDENTIFIER", metricList, identifiers, values)

            if( allInfo.all { it.isEmpty() }) {
                dto.info = " ";
            }else{
                dto.info = allInfo.distinct().joinToString(" <br> ");
            }

            listEvolutions.add(dto);

        }
        // val lineChartData = lineChart(list)
        return ResponseEntity(listEvolutions, HttpStatus.OK)
    }
}