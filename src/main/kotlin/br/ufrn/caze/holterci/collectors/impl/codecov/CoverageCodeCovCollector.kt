package br.ufrn.caze.holterci.collectors.impl.codecov

import br.com.jadson.snooper.codecov.data.CodeCovCommit
import br.com.jadson.snooper.codecov.operations.AbstractCodeCovQueryExecutor
import br.com.jadson.snooper.codecov.operations.CodeCovCommitsQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricRepository
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.*


/**
 * Collect metric of coverage in CodeCov tool
 *
 * @author Jadson Santos - jadson.santos@ufrn.br
 */
@Component
class CoverageCodeCovCollector
    : Collector(UUID.fromString("b1fae2bb-f3c0-4a3a-b421-b389f01a64b6"), Metric.COVERAGE,"Coverage at CodeCov", MetricRepository.CODECOV){



    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): Pair<BigDecimal, String> {
        val executor = CodeCovCommitsQueryExecutor()
        // executor.setQueryParameters("from=" + dateUtil.toString(period.init) + "&to=" + dateUtil.toString(period.end))
        // executor.setLimit(250)

        var commitsInPeriod : List<CodeCovCommit>  = executor.getCommits(project.organization + "/" +project.name, AbstractCodeCovQueryExecutor.CODE_COV_BASE.GITHUB, period.init, period.end)

        //val executor = CoverageCodeCovExecutor()
        //return executor.getCoverage(project.organization+"/"+project.name, period.init, period.end)

        var listCoverage : MutableList<BigDecimal> = mutableListOf()
        for (c in commitsInPeriod){
            // coverage information comes in this field "c"
            if(c.totals != null && c.totals.c != null)
                listCoverage.add(BigDecimal(c.totals.c))
        }

        return Pair(mathUtil.medianOfValues(listCoverage), generateMetricInfo(period, commitsInPeriod))
    }

    override fun cleanCache() {

    }

    fun generateMetricInfo(period: Period, commitsInPeriod : List<CodeCovCommit>): String{

        val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        return  "<strong> [ "+ dtf.format(period.init ) +" to "+ dtf.format(period.end) + "] </strong> <br> "+
        "  - Coverage Entries: "+commitsInPeriod.size+" <br>"
    }
}