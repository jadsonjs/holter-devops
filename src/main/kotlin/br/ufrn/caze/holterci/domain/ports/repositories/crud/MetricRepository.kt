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
 * MetricRepository
 * 07/05/21
 */
package br.ufrn.caze.holterci.domain.ports.repositories.crud

import br.ufrn.caze.holterci.domain.models.metric.Metric

/**
 * Define the queries of the domain class
 *
 * @author bruno batista - brunokaike@ufrn.edu.br
 */
interface MetricRepository {

    fun save(toEntity: Metric): Metric

    fun delete(projectEntity: Metric)

    fun findMetricById(id: Long): Metric

    fun findAll(): List<Metric>

    /**
     * The function receives a metric to be saved in the
     * database and verifies that the metric has been registered
     * previously.
     *
     * Returns the number of duplicates
     */
    fun countMetricSameName(metricEntity: Metric) : Long{

        // errado !!! busque no banco se tem
        //var allMetrics : List<Metric> = findAll();
        var counter : Long = 0;

//        allMetrics.forEach{
//
//            if(metricEntity.name.equals(it.name) && metricEntity.id != it.id)
//                counter++;
//        }

        return counter;

    }

}