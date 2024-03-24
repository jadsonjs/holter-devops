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
 * ProjectService
 * 18/04/21
 */
package br.ufrn.caze.holterci.domain.services.crud

import br.ufrn.caze.holterci.domain.exceptions.BusinessException
import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.models.metric.ProjectConfiguration
import br.ufrn.caze.holterci.domain.ports.repositories.crud.PeriodRepository
import br.ufrn.caze.holterci.domain.ports.repositories.crud.ProjectRepository
import br.ufrn.caze.holterci.domain.ports.repositories.crud.SchedulerRepository
import br.ufrn.caze.holterci.repositories.log.AccessLogJPARepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

/**
 * Should contain the business rules of application
 *
 * @author bruno batista - brunokaike@ufrn.edu.br
 */
@Service
class ProjectService
    // construtor injections of kotlin
    (
    private val projectRepository : ProjectRepository,
    private val schedulerRepository : SchedulerRepository,
    private val schedulerService : SchedulerService,
    private val periodRepository : PeriodRepository,
    private val accessLogJPARepository : AccessLogJPARepository
    )
{

    /**
     * Save a new project on database
     */
    @Transactional
    fun save(p: Project): Project {
        // Business rule
        if(existProject (p))
            throw BusinessException("Project already registered with same name and organization")

        return projectRepository.save(p)
    }

    /**
     * Delete a project from database.
     */
    @Transactional
    fun delete(idProject: Long) {

        try {
            var s = schedulerRepository.findSchedulerByProject(idProject)
            if (s != null)
                schedulerService.delete(s.id!!)
        }catch (bex: BusinessException){}

        periodRepository.deleteAllBYProject(idProject)
        projectRepository.deleteConfigurationByProject(idProject)
        accessLogJPARepository.deleteAllBYProject(idProject)

        projectRepository.delete(Project(idProject))
    }

    /**
     * Checks if the project already exists.
     */
    private fun existProject (p: Project) : Boolean {
        return projectRepository.countProjectSameNameAndOrganization(p) > 0
    }

    @Transactional
    fun saveConfiguration(configuration: ProjectConfiguration): ProjectConfiguration {
        if(configuration.issuesErrosLabels != null) {
            configuration.issuesErrosLabels = configuration.issuesErrosLabels!!.replace(" ", "")
        }
        return projectRepository.saveConfiguration(configuration)
    }

    fun findConfigurationByIdProject(projectId: Long): ProjectConfiguration {
        return projectRepository.findConfigurationByIdProject(projectId)
    }
}