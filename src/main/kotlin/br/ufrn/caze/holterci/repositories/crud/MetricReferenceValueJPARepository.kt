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
package br.ufrn.caze.holterci.repositories.crud

import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricReferenceValues
import br.ufrn.caze.holterci.domain.models.metric.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface MetricReferenceValueJPARepository : JpaRepository<MetricReferenceValues, Long> {

    @Query("SELECT COUNT(*) FROM MetricReferenceValues rv WHERE rv.metric= ?1 AND rv.project = ?2")
    fun countReferenceValue(m: Metric, p: Project): Long

    @Query("SELECT COUNT(*) FROM MetricReferenceValues rv WHERE rv.metric= ?1 AND rv.id <> ?2 AND rv.project = ?3")
    fun countReferenceValue(m: Metric, id: Long?, p: Project): Long

    @Query("SELECT rv FROM MetricReferenceValues rv WHERE rv.project.id = ?1")
    fun findAllByProject(projectId: Long?): List<MetricReferenceValues>

    @Modifying
    @Query("DELETE FROM MetricReferenceValues rv WHERE rv.project.id = ?1")
    fun deleteAllByProject(idProject: Long?)
}