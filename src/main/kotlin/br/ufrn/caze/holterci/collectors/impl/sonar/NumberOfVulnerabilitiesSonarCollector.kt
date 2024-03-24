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
package br.ufrn.caze.holterci.collectors.impl.sonar

import br.com.jadson.snooper.sonarcloud.data.history.SonarHistoryEntry
import br.com.jadson.snooper.sonarcloud.operations.SonarMetricHistoryQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricRepository
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.utils.StringUtil
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Collec the history of vulnerabities on sonar
 */
@Component
class NumberOfVulnerabilitiesSonarCollector
    : Collector(UUID.fromString("045f654e-667e-4312-a246-837af9140073"), Metric.NUMBER_OF_VULNERABILITIES, "Number of Vulnerabilities on Sonar", MetricRepository.SONAR){

    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): Pair<BigDecimal, String>  {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

        val executor = SonarMetricHistoryQueryExecutor()
        executor.setSonarURL(projectConfiguration.secondaryRepositoryURL)
        executor.setSonarToken(projectConfiguration.secondaryRepositoryToken)
        executor.setDisableSslVerification(disableSslVerification)

        var values : MutableList<BigDecimal> = mutableListOf()

        var sonarProjectFullName =
            if(projectConfiguration.secondaryRepositoryOrganization != null && StringUtil().isNotEmpty(projectConfiguration.secondaryRepositoryOrganization!!))
                projectConfiguration.secondaryRepositoryOrganization + ":" +projectConfiguration.secondaryRepositoryName
            else
                projectConfiguration.secondaryRepositoryName


        // in sonar the project key is  ''br.ufrn.xxxx:xxxxx-xxxx''
        val historyEntries1 : List<SonarHistoryEntry> = executor.getProjectMetricHistory(sonarProjectFullName, "security_hotspots", period.init, period.end)

        // get the last values on period
        if (historyEntries1.size > 0){
            var lastValue = historyEntries1.get(historyEntries1.size-1).value
            values.add(BigDecimal(if(lastValue != null) lastValue else "0"))
        }

        // in sonar the project key is  ''br.ufrn.xxxx:xxx-xxxx''
        val historyEntries2 : List<SonarHistoryEntry> = executor.getProjectMetricHistory(sonarProjectFullName, "new_vulnerabilities", period.init, period.end)

        // get the last values on period
        if (historyEntries2.size > 0){
            var lastValue = historyEntries2.get(historyEntries2.size-1).value
            values.add(BigDecimal(if(lastValue != null) lastValue else "0"))
        }

        return Pair(mathUtil.sumOfValues(values), generateMetricInfo(period, historyEntries1+historyEntries2))
    }

    override fun cleanCache() {

    }

    fun generateMetricInfo(period: Period, historyEntries : List<SonarHistoryEntry> ): String{

        val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        return  "<strong> [ "+ dtf.format(period.init ) +" to "+ dtf.format(period.end) + "] </strong> <br> "+
            "  - Vulnerabilities Entries: "+historyEntries.size+" <br>"
    }
}