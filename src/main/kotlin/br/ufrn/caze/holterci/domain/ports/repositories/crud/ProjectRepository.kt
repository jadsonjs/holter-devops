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
 * ProjectRepository
 * 18/04/21
 */
package br.ufrn.caze.holterci.domain.ports.repositories.crud

import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.models.metric.ProjectConfiguration

/**
 * Define the queries of the domain class
 *
 * @author bruno batista - brunokaike@ufrn.edu.br
 */
interface ProjectRepository {

    fun save(toEntity: Project): Project

    fun delete(projectEntity: Project)

    fun findProjectById(id: Long): Project

    fun findProjectByName(name: String): Project

    fun findProjectByNameAndOrganization(organization: String, name: String): Project

    fun findProjectIDByOrganizationAndName(organization: String, name: String): Project

    fun findAll(): List<Project>

    fun findAllActive(): List<Project>

    /**
     * The function receives a project to be saved in the
     * database and verifies that the project has been registered
     * previously.
     *
     * Returns the number of duplicates
     */
    fun countProjectSameNameAndOrganization(projectEntity: Project) : Long

    ////////////////////// Configuration //////////////////////////////////////

    /**
     * Saves configuration of a project
     */
    fun saveConfiguration(configuration: ProjectConfiguration): ProjectConfiguration

    /**
     * Return the configuration of a project, or null if not created yet
     */
    fun findConfigurationByIdProject(projectId: Long): ProjectConfiguration

    fun deleteConfigurationByProject(projectId: Long)


}