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
package br.ufrn.caze.holterci.repositories.results

import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.ports.repositories.results.MetricEvolutionRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import java.time.LocalDateTime

class MetricEvolutionRepositoryImpl : MetricEvolutionRepository {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun findMetricEvolution(init: LocalDateTime?, end: LocalDateTime?, metric: Metric?, projectId: Long): MutableList<Array<Any>> {

        var hql = StringBuilder(" SELECT new Period(per.init, per.end, per.index), mHistory.value, mHistory.metric, mHistory.metricInfo " +
                " FROM MetricHistory mHistory " +
                " INNER JOIN mHistory.period per " +
                " INNER JOIN per.project pro " +
                " WHERE pro.id = :idProject ")

        if(metric != null)
            hql.append(" AND mHistory.metric = :metric ")
        if(init != null)
            hql.append(" AND per.init >= :init ")
        if(end != null)
            hql.append(" AND per.end <= :end ")

        hql.append(" ORDER BY mHistory.metric, per.index ")

        // p.init <= :end AND p.end >= :init AND m.measure = :mesuare and
        val query =  entityManager.createQuery(hql.toString())
            .setParameter("idProject", projectId)

        if(metric != null)
            query.setParameter("metric", metric)
        if(init != null)
            query.setParameter("init", init)
        if(end != null)
            query.setParameter("end", end)

        return query.resultList as MutableList<Array<Any>>
    }
}