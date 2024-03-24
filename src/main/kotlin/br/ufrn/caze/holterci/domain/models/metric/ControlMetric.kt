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
 * The values of general metric.
 *
 * For now, they will not be collected, these metrics will be registered by the user to simplify
 */
@Entity
@Table(name = "control_metric", schema = "holter")
class ControlMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null

    /**
     *  N of develoerps
     */
    @Column(name = "n_developers", nullable = false)
    var nDevelopers : BigDecimal = BigDecimal.ZERO

    /**
     *  LOC of the project
     */
    @Column(name = "project_size", nullable = false)
    var projectSize : BigDecimal = BigDecimal.ZERO

    /**
     *  cyclomatic complexity of the project
     */
    @Column(name = "project_complexity", nullable = false)
    var projectComplexity : BigDecimal = BigDecimal.ZERO

    /**
     *  value of TD
     */
    @Column(name = "technical_debt", nullable = false)
    var technicalDebt : BigDecimal = BigDecimal.ZERO

    /**
     * This values will be valid for a period of analysis.
     */
    @ManyToOne
    @JoinColumn(name = "period_id", nullable = false)
    var period : Period = Period()

    constructor(){
    }

    constructor(id : Long){
        this.id = id
    }
}