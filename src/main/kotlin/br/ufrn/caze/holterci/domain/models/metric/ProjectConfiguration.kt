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

/**
 * Configuration of the project necessary to collect some metric metrics
 *
 * @author jadson santos - jadson.santos@ufrn.br
 */
@Entity
@Table(name = "project_configuration", schema = "holter")
class ProjectConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null;

    @ManyToOne
    @JoinColumn(name = "main_repository_id", nullable = false)
    var mainRepository: MainRepository = MainRepository()

    /**
     * Some project use an assistant repository to coverage, like CodeCov or Jenkins.  This field keep this information
     */
    @Column(name = "secondary_repository_url", nullable = true)
    var secondaryRepositoryURL: String? = null

    /**
     * Security token used by the most of the repository to give access to API.
     */
    @Column(name = "secondary_repository_token", nullable = true)
    var secondaryRepositoryToken: String? = null

    /**
     * THe repository of coverage can have different organization to a project. Ex.: Sonar
     *
     */
    @Column(name = "secondary_repository_organization", nullable = true)
    var secondaryRepositoryOrganization: String? = null


    /**
     * The repository of coverage can have differentet name of the project in primary repository. Ex.: Sonar
     *
     * Ex. gestao-parque-vue (GITLAB)
     * Ex. gestao-parque (Sonar)
     *
     */
    @Column(name = "secondary_repository_name", nullable = true)
    var secondaryRepositoryName: String? = null



    /**
     * The project of the configuration
     */
    @OneToOne
    @JoinColumn(name = "project_id", nullable = false)
    var project : Project = Project()

}