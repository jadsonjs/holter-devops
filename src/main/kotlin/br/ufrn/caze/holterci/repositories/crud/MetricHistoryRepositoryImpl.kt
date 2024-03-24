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
 * br.ufrn.caze.publicano
 * MetricRepositoryImpl
 * 07/05/21
 */
package br.ufrn.caze.holterci.repositories.crud

import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricHistory
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.ports.repositories.crud.MetricHistoryRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import java.time.LocalDateTime

/**
 * A concrete implementation of repository.
 *
 * This class should receive the JPA repository and a convert to convert model classes to entities.
 *
 * @author bruno batista - brunokaike@ufrn.edu.br
 */
class MetricHistoryRepositoryImpl
    (
    private val jpaRepository : MetricHistoryJPARepository,
    )  : MetricHistoryRepository
    {

        @PersistenceContext
        private lateinit var entityManager: EntityManager


        override fun save(m: MetricHistory): MetricHistory {
            return jpaRepository.save(m)
        }

        ////// queries ///////

        override fun findAll(): List<MetricHistory> {
            return jpaRepository.findAll()
        }


        override fun findMetricHistoryById(id: Long) : MetricHistory {
            return jpaRepository.findMetricHistoryById(id)
        }

        /**
         * Find the first and last date of execution on a mining to a specific metric of a project
         */
        override fun findInfoPeriodRegistered(projectId: Long, metric: Metric): Period {

            // get the init date of the first metric value registered
            var hqlFirstPeriodDate = StringBuilder(" SELECT per.init " +
                    " FROM MetricHistory mHistory " +
                    " INNER JOIN mHistory.period per " +
                    " INNER JOIN per.project pro " +
                    " WHERE pro.id = :projectId " +
                    " AND mHistory.metric = :metric ")

            hqlFirstPeriodDate.append(" ORDER BY per.index ASC ")

            val queryFirst =  entityManager.createQuery(hqlFirstPeriodDate.toString())
                .setParameter("projectId", projectId)
                .setParameter("metric", metric)
                .setMaxResults(1)

            val initFirstPeriod = queryFirst.singleResult as LocalDateTime

            // get the end date of the last metric value registered
            var hqlLastPeriodDate = StringBuilder(" SELECT per.end " +
                    " FROM MetricHistory mHistory " +
                    " INNER JOIN mHistory.period per " +
                    " INNER JOIN per.project pro " +
                    " WHERE pro.id = :projectId " +
                    " AND mHistory.metric = :metric ")

            hqlLastPeriodDate.append(" ORDER BY per.index DESC")

            val query =  entityManager.createQuery(hqlLastPeriodDate.toString())
                .setParameter("projectId", projectId)
                .setParameter("metric", metric)
                .setMaxResults(1)

            val endLastPeriod = query.singleResult as LocalDateTime

            return Period(initFirstPeriod, endLastPeriod, Project(projectId))
        }


        override fun isContainsMinerData(metric: Metric, idProject: Long): Boolean {

            val query =  entityManager.createQuery(
                " SELECT COUNT(*) FROM MetricHistory m " +
                        " INNER JOIN m.period p " +
                        " INNER JOIN p.project pro " +
                        " WHERE m.metric = :metric and pro.id = :idProject ")
                .setParameter("metric", metric)
                .setParameter("idProject", idProject)

            val qtd = query.singleResult as Long

            return qtd > 0
        }

        override fun isContainsMinerDataInPeriod(init: LocalDateTime, end: LocalDateTime, metric: Metric, idProject: Long): Boolean {

            val query =  entityManager.createQuery(
                " SELECT COUNT(*) FROM MetricHistory m " +
                        " INNER JOIN m.period p " +
                        " INNER JOIN p.project pro " +
                        " WHERE p.init <= :end AND p.end >= :init AND m.metric = :metric and pro.id = :idProject ")
                .setParameter("init", init)
                .setParameter("end", end)
                .setParameter("metric", metric)
                .setParameter("idProject", idProject)

            val qtd = query.singleResult as Long

            return qtd > 0
        }

        override fun deleteByProject(idProject: Long) {
            return jpaRepository.deleteByProject(idProject)
        }

    }