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
 * br.ufrn.caze.publicano.domain.ports.repositories
 * MetricHistoryRepository
 * 08/06/21
 */
package br.ufrn.caze.holterci.domain.ports.repositories.crud;

import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricHistory
import br.ufrn.caze.holterci.domain.models.metric.Period
import java.time.LocalDateTime

/**
 * Define the queries of the domain class
 *
 * Jadson Santos - jadsonjs@gmail.com
 */
interface MetricHistoryRepository {

    fun save(toEntity: MetricHistory): MetricHistory

    fun findAll(): List<MetricHistory>

    fun findMetricHistoryById(id: Long): MetricHistory

    /**
     * Find the first and last date of execution on a mining to a specific metric
     */
    fun findInfoPeriodRegistered(idProject: Long, metric: Metric) : Period

    /**
     * Checks if there is a conflicting period of analysis in the database.
     * In other words, is period was already being collected.
     */
    fun isContainsMinerData(metric: Metric, idProject: Long): Boolean

    /**
     * checks if exists data miner inside the period
     */
    fun isContainsMinerDataInPeriod(init: LocalDateTime, end: LocalDateTime, metric: Metric, idProject: Long): Boolean

    /**
     * Clear of history collected by project
     */
    fun deleteByProject(idScheduler: Long)

}