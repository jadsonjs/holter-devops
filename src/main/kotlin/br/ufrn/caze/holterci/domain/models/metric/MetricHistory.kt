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
 * br.ufrn.caze.publicano.domain.models
 * MetricHistory
 * 08/06/21
 */
package br.ufrn.caze.holterci.domain.models.metric;

import jakarta.persistence.*
import java.math.BigDecimal

/**
 * Keep the history of CI Metrics Collected
 *
 * Jadson Santos - jadsonjs@gmail.com
 */
@Entity
@Table(name = "metric_history", schema = "holter")
class MetricHistory {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null

    /**
     * The period of the metric was collected
     */
    @ManyToOne(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "period_id", nullable = false)
    var period : Period = Period()

    /**
     * the mesasure that are being collected.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "metric", nullable = false)
    var metric : Metric = Metric.COVERAGE

    /**
     *  Value of the metric
     */
    @Column(name = "metric_value", nullable = false)
    var value : BigDecimal = BigDecimal.ZERO

    /**
     * Information how the metric os calculated.
     * e.i. number of buils, branches of builds, number of commits, user of commits, etc.
     *
     * it serves the user to understand the calculation and to debug the metric value
     */
    @Column(name = "metric_info", nullable = true, columnDefinition = "TEXT", length = 1000000000)
    var metricInfo: String = ""

    /**
     * If the metric have some division like "developer".
     * the history can be segmented, i.e., in a same period have many values, each one for a segment.
     */
    @ManyToOne
    @JoinColumn(name = "metric_segment_id", nullable = true)
    var metricSegment : MetricSegment? = null


    override fun toString(): String {
        return "MetricHistory(id=$id, period=${period.id}, metric=$metric, value=$value)"
    }


}