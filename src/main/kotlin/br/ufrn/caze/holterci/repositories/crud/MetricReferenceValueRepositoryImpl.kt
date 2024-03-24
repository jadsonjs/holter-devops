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

import br.ufrn.caze.holterci.domain.models.metric.MetricReferenceValues
import br.ufrn.caze.holterci.domain.ports.repositories.crud.MetricReferenceValueRepository

class MetricReferenceValueRepositoryImpl (private val jpaRepository : MetricReferenceValueJPARepository)  : MetricReferenceValueRepository {

    override fun save(entity: MetricReferenceValues): MetricReferenceValues {
        return jpaRepository.save(entity)
    }

    override fun delete(entity: MetricReferenceValues) {
        jpaRepository.delete(entity)
    }

    override fun findById(id: Long): MetricReferenceValues {
        return jpaRepository.findById(id).get()
    }

    override fun findAll(): List<MetricReferenceValues> {
        return jpaRepository.findAll()
    }

    override fun containsReferenceValue(rv: MetricReferenceValues): Boolean {
        return if (rv.id == null ) jpaRepository.countReferenceValue(rv.metric) > 0 else jpaRepository.countReferenceValue(rv.metric, rv.id) > 0
    }
}