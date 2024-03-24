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
package br.ufrn.caze.holterci.domain.models.metric

import jakarta.persistence.*
import java.math.BigDecimal

/**
 * Reference values of each metric
 *
 * Reference values of Continuous Integration Theater
 *
 * (1) with infrequent commits,
 * 2.36 commits per weekday.
 *
 * (2) in a software project with poor test coverage,
 * On average, Java projects have 63% of test coverage,
 *
 * (3) with builds that stay broken for long periods, and
 * We observed that 85% of the analyzed projects have at least one build that took more than 4 days to be fixed
 *
 * (4) with builds that take too long to run.
 * Be executed under 10 minutes
 */
@Entity
@Table(name = "metric_reference_values", schema = "holter")
class MetricReferenceValues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null

    /**
     * the mesasure that are being collected.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "metric", nullable = false)
    var metric : Metric = Metric.COVERAGE

    /**
     *  Value of reference to the metric
     */
    @Column(name = "metric_value", nullable = false)
    var value : BigDecimal = BigDecimal.ZERO

    constructor(){
    }

    constructor(id : Long){
        this.id = id
    }
}