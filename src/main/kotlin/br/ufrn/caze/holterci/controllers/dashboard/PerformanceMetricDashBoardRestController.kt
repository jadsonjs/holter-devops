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
package br.ufrn.caze.holterci.controllers.dashboard

import br.ufrn.caze.holterci.controllers.AbstractRestController
import br.ufrn.caze.holterci.domain.dtos.results.MetricDashBoardDto
import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricReferenceValues
import br.ufrn.caze.holterci.domain.ports.repositories.crud.MetricReferenceValueRepository
import br.ufrn.caze.holterci.domain.ports.repositories.crud.ProjectRepository
import br.ufrn.caze.holterci.domain.ports.repositories.results.MetricEvolutionRepository
import br.ufrn.caze.holterci.domain.utils.MetricEvolutionUtil
import br.ufrn.caze.holterci.domain.utils.StringUtil
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDate

@RestController
@RequestMapping("/api/performance-metric-dashboard")
class PerformanceMetricDashBoardRestController (
    private val metricEvolutionRepository : MetricEvolutionRepository,
    private val metricReferenceValueRepository : MetricReferenceValueRepository,
    private val projectRepository : ProjectRepository,
    private val metricEvolutionUtil : MetricEvolutionUtil,
    private val stringUtil: StringUtil,
) : AbstractRestController() {


    @GetMapping(value = ["/{organization}/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPerformanceMetrics(
        @PathVariable organization: String,
        @PathVariable name: String,
        @RequestParam(name = "init") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) init : LocalDate,
        @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) end : LocalDate,
        @RequestParam(name = "normalize", required = false) normalize : Boolean?,
        @RequestParam(name = "control-metric", required = false)  projectMetricDtoStr : String?
    ): ResponseEntity<List<MetricDashBoardDto>> {

        var p = projectRepository.findProjectIDByOrganizationAndName(stringUtil.decodeURL(organization), name)

        var list: MutableList<MetricDashBoardDto> = mutableListOf()

        var metricRefereceValues =  metricReferenceValueRepository.findAll()

        var metrics = Metric.getById(Metric.NUMBER_OF_CHANGES_DELIVERED.id, Metric.NUMBER_OF_CLOSED_ISSUES.id,  Metric.CYCLE_TIME.id, Metric.LEAD_TIME.id, Metric.NUMBER_OF_BUGS.id, Metric.BUGS_RATE.id)


        for (m in metrics){

            val dataMetric : MutableList<Array<Any>> =
                metricEvolutionRepository.findMetricEvolution(init.atTime(0,0, 0), end.atTime(23,59, 59), m, p.id!!)

            if(dataMetric.size ==0){
                list.add(MetricDashBoardDto(Metric.toDto(m),  BigDecimal.ZERO, BigDecimal.ZERO, true, false, ""))
            }else{


                var allValues1 = java.util.ArrayList<BigDecimal>()
                for (d in dataMetric) {
                    allValues1.add((d.get(1) as BigDecimal))
                }

                var lastValue = allValues1.last() // mathUtil.meanOfValues(allValues1)  // mean values of all period selected
                var referenceValue = getReferenceValue(metricRefereceValues, m)

                var overReferenceValue : Boolean = metricEvolutionUtil.checkOverReferenceValue(m, lastValue, referenceValue);

                list.add(MetricDashBoardDto(Metric.toDto(m), lastValue, referenceValue, overReferenceValue, true,""))

            }

        }

        return ResponseEntity(list, HttpStatus.OK)

    }

//    private fun normalizeMetricValue(projectMetricDtoStr: String, m: Metric, median: BigDecimal): BigDecimal {
//
//        var medianTemp = median
//        var projectMetricDto: ProjectMetricValuesDto = ObjectMapper().readValue(projectMetricDtoStr, ProjectMetricValuesDto::class.java)
//
//        if (projectMetricDto.nDevelopers.compareTo(BigDecimal.ZERO) > 0 && projectMetricDto.projectSize.compareTo(BigDecimal.ZERO) > 0) {
//
//            when (m) {
//
//                // changes delivered and issues closed by the number of developers
//                Metric.NUMBER_OF_CHANGES_DELIVERED        -> medianTemp = mathUtil.divide(medianTemp, projectMetricDto.nDevelopers)
//                Metric.NUMBER_OF_CLOSED_ISSUES  -> medianTemp = mathUtil.divide(medianTemp, projectMetricDto.nDevelopers)
//                // number of bugs by project size
//                Metric.NUMBER_OF_BUGS           -> medianTemp = mathUtil.divide(medianTemp, projectMetricDto.projectSize)
//
//                // return normal value
//                else -> {
//                    medianTemp = medianTemp
//                }
//            }
//        }
//        return medianTemp
//    }

    private fun getReferenceValue(measureRefereceValues: List<MetricReferenceValues>, metric : Metric): BigDecimal {
        for (m in measureRefereceValues) {
            if( m.metric == metric)
                return m.value
        }
        return BigDecimal.ZERO
    }


}