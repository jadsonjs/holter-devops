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
 * br.ufrn.caze.publicano.domain.collectors.travisci
 * TimeToFixBrokenBuildCollector
 * 09/07/21
 */
package br.ufrn.caze.holterci.collectors.impl.travis;

import br.com.jadson.snooper.travisci.data.builds.TravisBuildsInfo
import br.com.jadson.snooper.travisci.operations.TravisCIBuildsQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricRepository
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.utils.TravisCIUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*


/**
 * This class is a collector of time to fix broken build in travis CI
 *
 * @author bruno batista - brunokaike@ufrn.edu.br
 */
@Component
class TimeToFixBrokenBuildTravisCICollector
    : Collector(UUID.fromString("2576711d-9367-4f66-b4e6-f83425091ba5"),  Metric.TIME_TO_FIX_BROKEN_BUILD, "Time to fix broken build at Travis CI", MetricRepository.TRAVIS){


    @Autowired
    lateinit var travisCIUtil : TravisCIUtil

    var buildsCache = mutableListOf<TravisBuildsInfo>()


    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): Pair<BigDecimal, String> {

        val executor = TravisCIBuildsQueryExecutor()

        // bring all issues first time to memory
        if(buildsCache.isEmpty()){
            var allBuilds = executor.getBuilds(project.organization+"/"+project.name, globalPeriod.init, globalPeriod.end)
            buildsCache = allBuilds
        }

        return Pair( metricCalc.calcTimeFixBrokenBuilds( travisCIUtil.getBuildsOfPeriod(buildsCache, period.init, period.end)), "")

    }

    override fun cleanCache() {

    }

}