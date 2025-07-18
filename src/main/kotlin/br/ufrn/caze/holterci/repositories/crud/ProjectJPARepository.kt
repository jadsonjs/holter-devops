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
 * holter-devops
 * br.ufrn.caze.publicano
 * ProjectJPARepository
 * 18/04/21
 */
package br.ufrn.caze.holterci.repositories.crud

import br.ufrn.caze.holterci.domain.models.metric.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

/**
 * Basic Repository of Spring Data framework
 * @author bruno batista - brunokaike@ufrn.edu.br
 */
interface ProjectJPARepository : JpaRepository<Project, Long> {

    @Query(" SELECT p FROM Project p WHERE p.id = ?1")
    fun findProjectById(id: Long) : Project

    @Query(" SELECT p FROM Project p WHERE p.name = ?1")
    fun findProjectByName(name: String) : Project

    @Query(" SELECT p FROM Project p WHERE p.organization = ?1 ANd p.name = ?2")
    fun findProjectByNameAndOrganization(organization: String, name: String) : Project

    @Query(" SELECT new Project(p.id) FROM Project p WHERE p.organization = ?1 ANd p.name = ?2")
    fun findProjectIDByOrganizationAndName(organization: String, name: String) : Project

    @Query(" SELECT COUNT(*) FROM Project p WHERE p.id <> ?1 AND p.name = ?2 AND p.organization = ?3 ")
    fun countProjectSameNameAndOrganization(id: Long?, name: String, organization: String): Long

    fun findByActive(active : Boolean) : List<Project>

    @Modifying
    @Query(" DELETE FROM ProjectConfiguration c WHERE c.project.id = ?1")
    fun deleteConfigurationByProject(projectId: Long)

    @Modifying
    @Query(" DELETE FROM ProjectConfiguration c WHERE c.mainRepository.id = ?1")
    fun deleteConfigurationByMainRepository(mainRepositoryId: Long)

}