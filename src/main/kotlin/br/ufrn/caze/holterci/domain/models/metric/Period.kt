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
 * CollectionPeriod
 * 24/06/21
 */
package br.ufrn.caze.holterci.domain.models.metric;

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Represent a period when the metric was collect to a project.
 * All CI metrics and Quality attributes are associate with periods of collection
 *
 * Jadson Santos - jadsonjs@gmail.com
 */
@Entity
@Table(name = "period", schema = "holter")
class Period(){

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null

    /**
     * initial date of analysis
     */
    @Column(name = "init_p", nullable = false)
    var init : LocalDateTime = LocalDateTime.now()

    /**
     * end date of analysis
     */
    @Column(name = "end_p", nullable = false)
    var end : LocalDateTime = LocalDateTime.now()

    /**
     * The project of the metric was collected
     */
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    var project : Project = Project()

    /***
     * Index os period, just to facilitate the organization
     */
    @Column(name = "index", nullable = false)
    var index : Int = 0

    constructor(init: LocalDateTime, end: LocalDateTime, project: Project) : this() {
        this.init = init
        this.end = end
        this.project = project
    }

    /**
     * Contructor used in the evolution char, do not change it.
     */
    constructor(init: LocalDateTime, end: LocalDateTime, index : Int) : this() {
        this.init = init
        this.end = end
        this.index = index;
    }

    constructor(init: LocalDateTime, end: LocalDateTime, project: Project, index : Int) : this(init, end, index) {
        this.project = project
    }
}