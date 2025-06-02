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
import br.ufrn.caze.holterci.collectors.dtos.CollectResult
import br.ufrn.caze.holterci.domain.models.metric.*
import br.ufrn.caze.holterci.domain.utils.StringUtil
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Get project coverage in sonar
 *
 * @author Jadson Santos - jadson.santos@ufrn.br
 */
@Component
class CoverageSonarCollector
    : Collector(UUID.fromString("a91338f1-f2b3-4d7e-b2db-4614999ba999"), Metric.COVERAGE, "Coverage on Sonar", MetricRepository.SONAR){

    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): CollectResult {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

        val executor = SonarMetricHistoryQueryExecutor()
        executor.setSonarURL(projectConfiguration.secondaryRepositoryURL)
        executor.setSonarToken(projectConfiguration.secondaryRepositoryToken)
        executor.setDisableSslVerification(disableSslVerification)

        var sonarProjectFullName =
            if(projectConfiguration.secondaryRepositoryOrganization != null && StringUtil().isNotEmpty(projectConfiguration.secondaryRepositoryOrganization!!))
                projectConfiguration.secondaryRepositoryOrganization + ":" +projectConfiguration.secondaryRepositoryName
            else
                projectConfiguration.secondaryRepositoryName

        // in sonar the project key is  ''br.ufrn.imd:base-imd''
        val historyEntries : List<SonarHistoryEntry> = executor.getProjectMetricHistory(sonarProjectFullName, "coverage", period.init, period.end)


        var listCoverage : MutableList<BigDecimal> = mutableListOf()
        for (c in historyEntries){
            listCoverage.add(BigDecimal(c.value))
        }

        return CollectResult(mathUtil.meanOfValues(listCoverage), generateMetricInfo(period, listCoverage), null)
    }

    override fun cleanCache() {

    }

    fun generateMetricInfo(period: Period, listCoverage : List<BigDecimal> ): String{

        val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        return  "<strong> [ "+ dtf.format(period.init ) +" to "+ dtf.format(period.end) + "] </strong> <br> "+
            "  - Coverage Entries: "+listCoverage.size+" <br>"+
            "  - Coverage Values: "+listCoverage.joinToString(", ")+" <br>"
    }

}