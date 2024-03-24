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
package br.ufrn.caze.holterci.domain.models.log

import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.models.user.User
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * this class save information about the access to dashboard screen.
 */
@Entity
@Table(name = "access_log", schema = "holter")
class AccessLog(user: User?, dateTime: LocalDateTime, init: LocalDate, end: LocalDate, project: Project, userSelectedData: User? = null) {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null;

    /**
     * Who access the dashboard (can be null if there is not logged user)
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true, unique = false)
    var user : User? = null

    /**
     * The date where the access to the dashboard has made
     */
    @Column(name = "date_time", nullable = false)
    var dateTime : LocalDateTime = LocalDateTime.now()

    /**
     * initial date selected in dashboard
     */
    @Column(name = "init_p", nullable = false)
    var init : LocalDate = LocalDate.now()

    /**
     * end date selected in dashboard
     */
    @Column(name = "end_p", nullable = false)
    var end : LocalDate = LocalDate.now()


    /**
     * The project of the dashboard
     */
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false, unique = false)
    var project : Project = Project()

    /**
     * Who the "user" select to see the data (null if it was to whole team)
     */
    @ManyToOne
    @JoinColumn(name = "user_selected_data_id", nullable = true, unique = false)
    var userSelectedData : User? = null

    // initializer block
    init {
        this.user = user
        this.project = project
        this.dateTime = dateTime
        this.init = init
        this.end = end
        this.userSelectedData = userSelectedData
    }

}