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
 * ProjectRepositoryImpl
 * 18/04/21
 */
package br.ufrn.caze.holterci.repositories.crud

import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.models.metric.ProjectConfiguration
import br.ufrn.caze.holterci.domain.ports.repositories.crud.ProjectRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import jakarta.persistence.PersistenceContext

/**
 * A concrete implementation of repository.
 *
 * This class should receive the JPA repository and a convert to convert model classes to entities.
 *
 * @author bruno batista - brunokaike@ufrn.edu.br
 */
class ProjectRepositoryImpl
    (
    // construtor injection. All repository should receive the JPA Repository and a convert
    private val jpaRepository : ProjectJPARepository,
    )  : ProjectRepository
{

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun save(p: Project): Project {
        return jpaRepository.save(p)
    }

    override fun delete(p: Project) {
        jpaRepository.delete(p)
    }

    ///////// queries  //////////

    override fun findProjectById(id: Long): Project {
        return jpaRepository.findProjectById(id)
    }

    override fun findProjectByName(name: String): Project {
        return jpaRepository.findProjectByName(name)
    }

    override fun findProjectByNameAndOrganization(organization: String, name: String): Project {
        return jpaRepository.findProjectByNameAndOrganization(organization, name)
    }

    override fun findProjectIDByOrganizationAndName(organization: String, name: String): Project {
        return jpaRepository.findProjectIDByOrganizationAndName(organization, name)
    }



    override fun findAll(): List<Project> {
        return jpaRepository.findAll()
    }

    override fun findAllActive(): List<Project> {
        return jpaRepository.findByActive(true)
    }

    override fun countProjectSameNameAndOrganization(project: Project): Long {
        return jpaRepository.countProjectSameNameAndOrganization(  (if (project.id != null) project.id else 0L) , project.name.trim(), project.organization.trim())
    }


    /////////////////////////////////////// Configuration ////////////////////////////////////////////////////

    override fun findConfigurationByIdProject(projectId: Long): ProjectConfiguration {
        val query =  entityManager.createQuery(
            " SELECT p  " +
                    " FROM ProjectConfiguration p " +
                    " INNER JOIN FETCH p.project proj " +
                    " WHERE  proj.id = :idProject ")
            .setParameter("idProject", projectId)

        try{
            return query.singleResult as ProjectConfiguration
        }catch (nre : NoResultException){
            return ProjectConfiguration()
        }
    }


    override fun saveConfiguration(configuration: ProjectConfiguration): ProjectConfiguration {
        if(configuration.id == null)
            entityManager.persist(configuration)
        else
            entityManager.merge(configuration)
        return configuration
    }


    override fun deleteConfigurationByProject(projectId: Long) {
        jpaRepository.deleteConfigurationByProject(projectId)
    }



}