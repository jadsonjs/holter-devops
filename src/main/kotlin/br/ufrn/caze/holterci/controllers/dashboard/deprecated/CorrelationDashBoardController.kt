package br.ufrn.caze.holterci.controllers.dashboard.deprecated

import br.ufrn.caze.holterci.domain.dtos.results.LinearRegressionDto
import br.ufrn.caze.holterci.domain.models.division.MetricStage
import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.ports.repositories.crud.ProjectRepository
import br.ufrn.caze.holterci.domain.ports.repositories.results.MetricEvolutionRepository
import br.ufrn.caze.holterci.domain.utils.ScatterChartUtil
import br.ufrn.caze.holterci.domain.utils.StringUtil
import org.apache.commons.math3.stat.regression.SimpleRegression
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.util.*

/**
 * This dashboard return the correlation between metrics
 */
//@RestController
//@RequestMapping("/api/correlation-dashboard")
class CorrelationDashBoardController (
    private val metricEvolutionRepository : MetricEvolutionRepository,
    private val projectRepository : ProjectRepository,
    private val scatterChartUtil : ScatterChartUtil,
){


    /**
     * Study the correlation between CI metric and PRODUCTIVITY AND QUALITY metrics
     */
    @GetMapping(path = ["/{organization}/{name}"])
    fun getCIMetricCorrelation(@PathVariable(value = "organization") organization: String, @PathVariable(value = "name") name: String,
                               @RequestParam(name = "init") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) init : LocalDate,
                               @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) end : LocalDate
    ): ResponseEntity<Array<Array<LinearRegressionDto?>>>? {

        var p = projectRepository.findProjectIDByOrganizationAndName(organization, name);

        var metrics = Metric.getByStage(MetricStage.CI, MetricStage.CD)
        var qualities = Metric.getById(Metric.NUMBER_OF_CHANGES_DELIVERED.id, Metric.CYCLE_TIME.id, Metric.LEAD_TIME.id, Metric.NUMBER_OF_BUGS.id, Metric.NUMBER_OF_CLOSED_ISSUES.id, Metric.BUGS_RATE.id)

        /*
         * Return a matrix of linear regressions:
         *           | quality 1  |    quality 2  | quality 3  |
         * metric 1  |  dto       |    dto        |  dto       |
         * metric 2  |  dto       |    dto        |  dto       |
         * ...
         */
        val result = Array(metrics.size) { arrayOfNulls<LinearRegressionDto>(qualities.size) }


        /***
         * Calculate correlation of all qualities attributes (Closed PR, BUgs, Tec Debt) with all CI metrics (coverage, commit activity, etc.. )
         */
        for ((i, m) in metrics.withIndex()){
            for ((j, q) in qualities.withIndex()){

                val dataMetric : MutableList<Array<Any>> =
                    metricEvolutionRepository.findMetricEvolution(init.atTime(0,0, 0), end.atTime(23,59, 59), m, p.id!!)
                val dataQuality : MutableList<Array<Any>> =
                    metricEvolutionRepository.findMetricEvolution(init.atTime(0,0, 0), end.atTime(23,59, 59), q, p.id!!)

                /**
                 * [0] per.index,
                 * [1] mHistory.value,    (we will take all values to calculate linear regressions)
                 * [2] mHistory.measure
                 */
                if(dataMetric.size > 0 && dataQuality.size > 0) {
                    var allValues1 = ArrayList<BigDecimal>()
                    var allValues2 = ArrayList<BigDecimal>()

                    for (d in dataMetric) {
                        allValues1.add((d.get(1) as BigDecimal))
                    }

                    for (d in dataQuality) {
                        allValues2.add((d.get(1) as BigDecimal))
                    }

                    result[i][j] = calcRegression(m, q, allValues1, allValues2);

                }else{
                    println(" --------- no data for: "+m.name+" x "+q.name+" ---------------")
                    println("metric history size: "+dataMetric.size)
                    println("quality history size: "+dataQuality.size)
                    result[i][j] = LinearRegressionDto(
                        UUID.randomUUID().toString(), StringUtil().formatMetricName(q.name), StringUtil().formatMetricName(m.name),
                        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, m.direct, false)
                }
            }
        }

        return ResponseEntity(result, HttpStatus.OK)
    }



    //////////////////////////////////////////////////////////////////////


    /**
     * calc liner regression of metrics values and the chart
     */
    fun  calcRegression(m : Metric, q : Metric, allValues1 : ArrayList<BigDecimal>, allValues2 : ArrayList<BigDecimal>) : LinearRegressionDto {


        var size = if( allValues1.size < allValues2.size) allValues1.size else allValues2.size

        val regression = SimpleRegression()
        for (index in 0..size-1)
            regression.addData(allValues1.get(index).toDouble(), allValues2.get(index).toDouble())

        val regressionResults = regression.regress()

        var rightDirection = ( m.direct && regression.slope > 0 ) || ( ! m.direct && regression.slope < 0 )
        var relevant = ! regressionResults.rSquared.equals(Double.NaN) && regressionResults.rSquared.compareTo(0.50) > 0 && regression.significance < 0.05

        val format = DecimalFormat("#.##", DecimalFormatSymbols(Locale.ENGLISH))
        val format2 = DecimalFormat("#.####", DecimalFormatSymbols(Locale.ENGLISH))

        var lrDto =
            LinearRegressionDto(
                UUID.randomUUID().toString(),
                StringUtil().formatMetricName(q.name), StringUtil().formatMetricName(m.name),
                (if (! regression.slope.equals(Double.NaN) )                   regression.slope                   else 0.0),
                (if (! regression.slopeConfidenceInterval.equals(Double.NaN) ) regression.slopeConfidenceInterval else 0.0),
                (if (! regression.intercept.equals(Double.NaN) )               regression.intercept               else 0.0),
                (if (! regression.meanSquareError.equals(Double.NaN) )         regression.meanSquareError         else 0.0),
                format.format(if(! regressionResults.rSquared.equals(Double.NaN))         regressionResults.rSquared         else 0.0 * 100).toDouble(),
                format.format(if(! regressionResults.adjustedRSquared.equals(Double.NaN)) regressionResults.adjustedRSquared else 0.0).toDouble(),
                format2.format(if(! regression.significance.equals(Double.NaN))           regression.significance            else 0.0).toDouble(),
                rightDirection,
                relevant)

        lrDto.chartData = scatterChartUtil.generateScatterData(m.name, allValues1, q.name, allValues2)
        lrDto.chartOptions = scatterChartUtil.generateScatterChartOption(m.name, q.name)

        return lrDto

    }
}