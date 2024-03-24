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
 * Scheduler
 * 19/05/21
 */
package br.ufrn.caze.holterci.domain.models.metric

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

/**
 * Class for object Scheduler
 *
 * @author jadson santos - jadson.santos@ufrn.br
 * @author bruno batista - brunokaike@ufrn.edu.br
 */
@Entity
@Table(name = "scheduler", schema = "holter")
class Scheduler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null;

    /**
     * The project to the scheduler will execute
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    var project : Project = Project()

    /**
     * Date of last data collection run.
     *
     */
    @Column(name = "last_execution", nullable = true)
    var lastExecution : LocalDateTime? = null

    /**
     * Initial data collect will execute at the first time.
     *
     */
    @Column(name = "start_execution", nullable = true)
    var startExecution : LocalDateTime = LocalDateTime.now()

    /**
     * Period of time in which automatic periodic collections will be active.
     *
     */
    @Column(name = "frequency", nullable = false)
    var frequencyOfExecution : Frequency = Frequency.MONTH

    /**
     * Say whether the collection of automatic metric is active or not.
     * If automatic collection is disabled, the tool will not collect metrics periodically automatically.
     *
     */
    @Column(name = "automatic", nullable = false)
    var automatic : Boolean = false


    /**
     * List of collectors the this scheduler will execute.
     * If a list of IDs, because collectors are fix in code. We can not create one without create it implementation.
     */
    @ElementCollection
    @CollectionTable(name="scheduler_collectors", schema = "holter", joinColumns = [JoinColumn(name = "scheduler_id")])
    @Column(name = "collector_id")
    var collectorsId : List<UUID> = ArrayList()

    /**
     * Collector to be used for automatic metric collection.
     *
     * Still lack create the Collector class
     *
     * var collector : Collector? = null;
     *
     */
    constructor(){}

    constructor(id : Long){
        this.id = id
    }

    constructor(id : Long, automatic : Boolean, lastExecution : LocalDateTime?, startExecution : LocalDateTime, frequencyOfExecution : Frequency, project : Project, collectorsId : List<UUID> ){
        this.id = id
        this.project = project
        this.startExecution = startExecution
        this.lastExecution = lastExecution
        this.frequencyOfExecution = frequencyOfExecution
        this.automatic = automatic
        this.collectorsId = collectorsId;
    }

}