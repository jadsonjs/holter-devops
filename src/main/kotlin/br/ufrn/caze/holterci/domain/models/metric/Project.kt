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
 * Project
 * 18/04/21
 */
package br.ufrn.caze.holterci.domain.models.metric

import br.ufrn.caze.holterci.domain.utils.StringUtil
import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty

/**
 * Class for object Project
 *
 * @author jadson santos - jadson.santos@ufrn.br
 * @author bruno batista - brunokaike@ufrn.edu.br
 */
@Entity
@Table(name = "project", schema = "holter")
class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null;

    /**
     * The name of project, usually this field is used as
     * an external project ID
     */
    @NotEmpty
    @Column(name = "name", nullable = false)
    var name : String = "";

    /**
     * The author responsible for the project
     * usually, this field is used as a project ID together with the
     * name
     */
    @NotEmpty
    @Column(name = "organization", nullable = false)
    var organization : String = "";

    /**
     * Say if the project is active or not. A disabled project the
     * tool will not collect metrics any more.
     *
     * The project can be removed, is this case all information of
     * project is deleted from our database. If the project if
     * disabled, the data remain in our database, but we will not
     * collect new information.
     */
    @Column(name = "active", nullable = false)
    var active : Boolean = true;


    constructor(){
    }

    constructor(id : Long){
        this.id = id
    }

    constructor(id : Long, name : String, organization : String, active : Boolean){
        this.id = id
        this.name = name
        this.organization = organization
        this.active = active
    }

    fun valid() : Boolean{
        return ! (StringUtil().isEmpty(name) || StringUtil().isEmpty(organization))
    }

}