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
import jakarta.validation.constraints.NotEmpty

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

    /**
     * Repository API URl. used to collect the data.
     * No necessary if the project is in the GitHub, bacause the GitHub has just one URL.
     * But is necessary, for example, if the project are in local gitlab repository
     */
    @NotEmpty
    @Column(name = "main_repository_url", nullable = false)
    var mainRepositoryURL: String = ""

    /**
     * Security token used by the most of the repository to give access to API.
     */
    @NotEmpty
    @Column(name = "main_repository_token", nullable = false)
    var mainRepositoryToken: String = ""

    /**
     * The name of the branch that contains the production code.
     * By default, the branch "master" is the projection branch
     */
    @NotEmpty
    @Column(name = "prodution_branch", nullable = false)
    var produtionBranch: String = ""

    /**
     * List Labels separated by comma that indicate in the project that an issue is an error issue
     * Normally if this way we identify an error issue, by the label.
     */
    @Column(name = "issues_erros_labels", nullable = true)
    var issuesErrosLabels: String? = ""

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