package br.ufrn.caze.holterci.domain.services.alert

import br.ufrn.caze.holterci.domain.models.division.MetricStage
import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricReferenceValues
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.ports.repositories.crud.MetricReferenceValueRepository
import br.ufrn.caze.holterci.domain.ports.repositories.results.MetricEvolutionRepository
import br.ufrn.caze.holterci.domain.utils.EmailUtil
import br.ufrn.caze.holterci.domain.utils.MetricEvolutionUtil
import br.ufrn.caze.holterci.repositories.UserJPARepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalTime

/**
 * Check the metric under or over reference values, and it emails project users
 *
 * @author Jadson Santos - jadson.santos@ufrn.br
 */
@Service
class AlertService (
    private val metricEvolutionRepository : MetricEvolutionRepository,
    private val metricReferenceValueRepository : MetricReferenceValueRepository,
    private val metricEvolutionUtil : MetricEvolutionUtil,

    private val userJPARepository: UserJPARepository,
    private val emailUtil: EmailUtil,
){


    @Value("\${lock.no-ci-metrics}")
    val lockNoCIMetrics : Boolean = false


    /***
     * Send alert for a project
     */
     fun sendAlert(project: Project, periodExecution : Period, appAdress : String) {

        var metricRefereceValues =  metricReferenceValueRepository.findAll()

        var metrics : MutableList<Metric> = mutableListOf()

        metrics.addAll( Metric.getByStage(MetricStage.CI) )

        /**
         * Add other metrics to the list
         */
        if(!  lockNoCIMetrics ){
            metrics.addAll(Metric.getById(Metric.DEPLOYMENT_FREQUENCY.id, Metric.LEAD_TIME_FOR_CHANGES.id, Metric.MEAN_TIME_TO_RECOVERY.id, Metric.CHANGE_FAILURE_RATE.id) )
            metrics.addAll(Metric.getById(Metric.NUMBER_OF_CHANGES_DELIVERED.id, Metric.NUMBER_OF_CLOSED_ISSUES.id,  Metric.CYCLE_TIME.id, Metric.LEAD_TIME.id, Metric.NUMBER_OF_BUGS.id, Metric.BUGS_RATE.id) )
        }


        var underReferenceMetrics: MutableList<Triple<Metric, BigDecimal, BigDecimal>> = mutableListOf()

        for (m in metrics){

            println("recovering metric values from: "+periodExecution.init.with(LocalTime.MIN) +" to "+ periodExecution.end.with(LocalTime.MAX))

            val dataMetric : MutableList<Array<Any>> =
                metricEvolutionRepository.findMetricEvolution(periodExecution.init.with(LocalTime.MIN), periodExecution.end.with(LocalTime.MAX), m, project.id!!)

            if(dataMetric.size > 0) {

                var allValues1 = ArrayList<BigDecimal>()
                for (d in dataMetric) {
                    allValues1.add((d.get(1) as BigDecimal))
                }

                var lastValue =  allValues1.last()
                var referenceValue = getReferenceValue(metricRefereceValues, m)

                var overReferenceValue : Boolean = metricEvolutionUtil.checkOverReferenceValue(m, lastValue, referenceValue);


                if( ! overReferenceValue){
                    underReferenceMetrics.add(Triple(m, lastValue, referenceValue))
                }

            }

        }

        val alertEmailList: List<String> = userJPARepository.findUsersEmailsSendAlert(project.id!!)

        println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
        println(emailUtil.sendDevOpsMetrcsAlertMail(project, periodExecution, underReferenceMetrics, alertEmailList, appAdress))
        println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")

    }

    private fun getReferenceValue(measureRefereceValues: List<MetricReferenceValues>, metric : Metric): BigDecimal {
        for (m in measureRefereceValues) {
            if( m.metric == metric)
                return m.value
        }
        return BigDecimal.ZERO
    }
}