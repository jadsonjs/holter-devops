package br.ufrn.caze.holterci.controllers.dashboard.deprecated

import br.ufrn.caze.holterci.controllers.AbstractRestController
import br.ufrn.caze.holterci.domain.dtos.results.ProjectMetricValuesDto
import br.ufrn.caze.holterci.domain.models.metric.ControlMetric
import br.ufrn.caze.holterci.domain.ports.repositories.crud.ControlMetricRepository
import br.ufrn.caze.holterci.domain.ports.repositories.crud.ProjectRepository
import br.ufrn.caze.holterci.domain.utils.MathUtil
import br.ufrn.caze.holterci.domain.utils.StringUtil
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.math.BigDecimal
import java.time.LocalDate

//@RestController
//@RequestMapping("/api/control-metric-dashboard")
class ControlMetricDashBoardRestController  (
    private val controlMetricRepository : ControlMetricRepository,
    private val projectRepository : ProjectRepository,
    private val mathUtil : MathUtil,
    private val stringUtil: StringUtil,
)
    : AbstractRestController() {


    /**
     * Return the mean of general metric between period of analysis
     */
    @GetMapping(value = ["/{organization}/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getGeneralMetric(@PathVariable organization: String,
                         @PathVariable name: String,
                         @RequestParam(name = "init") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) init : LocalDate,
                         @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) end : LocalDate
    ): ResponseEntity<ProjectMetricValuesDto> {

        var p = projectRepository.findProjectIDByOrganizationAndName(stringUtil.decodeURL(organization), name)

        var controlMetricList = controlMetricRepository.findControlMetricsBetweenPeriods(init.atTime(0,0, 0), end.atTime(23,59, 59), p.id!!)

        var dto = ProjectMetricValuesDto()

        getMetricValues(controlMetricList, dto)

        return ResponseEntity(dto, HttpStatus.OK)

    }

    private fun getMetricValues(list: List<ControlMetric>, dto : ProjectMetricValuesDto) {

        var allValuesNDevelopers    = ArrayList<BigDecimal>()
        var allValuesProjectSize      = ArrayList<BigDecimal>()
        var allValuesProjectComplexy  = ArrayList<BigDecimal>()
        var allValuesTechnicalDebt    = ArrayList<BigDecimal>()

        for (l in list) {
            allValuesNDevelopers.add(l.nDevelopers)
            allValuesProjectSize.add(l.projectSize)
            allValuesProjectComplexy.add(l.projectComplexity)
            allValuesTechnicalDebt.add(l.technicalDebt)
        }

        dto.nDevelopers         = mathUtil.meanOfValues(allValuesNDevelopers)
        dto.projectSize         = mathUtil.meanOfValues(allValuesProjectSize)
        dto.projectComplexity   = mathUtil.meanOfValues(allValuesProjectComplexy)
        dto.technicalDebt       = mathUtil.meanOfValues(allValuesTechnicalDebt)

    }

}