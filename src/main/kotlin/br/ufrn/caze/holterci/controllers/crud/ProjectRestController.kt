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
 * br.ufrn.caze.publicano.controllers
 * ProjectController
 * 18/04/21
 */
package br.ufrn.caze.holterci.controllers.crud

import br.ufrn.caze.holterci.controllers.AbstractRestController
import br.ufrn.caze.holterci.converters.ProjectConfigurationDtoConverter
import br.ufrn.caze.holterci.domain.dtos.MessageDto
import br.ufrn.caze.holterci.domain.dtos.crud.ProjectConfigurationDto
import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.ports.repositories.crud.ProjectRepository
import br.ufrn.caze.holterci.domain.services.crud.ProjectService
import br.ufrn.caze.holterci.domain.utils.StringUtil
import jakarta.validation.Valid
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Project CRUD controller
 *
 * @author bruno batista - brunokaike@ufrn.edu.br
 */
@RestController
@RequestMapping("/api/project")
class ProjectRestController
     // construtor injections of kotlin
     (
    private val projectRepository : ProjectRepository,
    private val projectService : ProjectService,
    private val projectConfigurationDtoConverter : ProjectConfigurationDtoConverter,
    private val stringUtil: StringUtil,
     )
    : AbstractRestController()
{


    /**
     * Return all projects
     */
    @GetMapping
    fun getAll(@RequestParam(required = false) active : Boolean = false) : ResponseEntity<List<Project>> {
        if(active)
            return ResponseEntity(projectRepository.findAllActive(), HttpStatus.OK)
        else
            return ResponseEntity(projectRepository.findAll(), HttpStatus.OK)
    }


    /**
     * Get a specific project by id
     */
    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long) : Project {
        return projectRepository.findProjectById(id)
    }

    /**
     * Get a specific project by name
     */
    @GetMapping("/findByName/{name}")
    fun findByName(@PathVariable name: String) : Project {
        return projectRepository.findProjectByName(name)
    }

    @GetMapping("/findByFullName/{organization}/{name}")
    fun findByNameAndOrganization(@PathVariable organization: String, @PathVariable name: String) : Project {
        return projectRepository.findProjectByNameAndOrganization(stringUtil.decodeURL(organization), name)
    }

    /**
     * Save a new project
     */
    @PostMapping("/save")
    fun save(@Valid @RequestBody project: Project) : ResponseEntity<Project>{
        return ResponseEntity(projectService.save(project), HttpStatus.OK)
    }

    /**
     * Delete a project
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) : ResponseEntity<MessageDto>{
        projectService.delete(id)
        return ResponseEntity( MessageDto("Project Removed with Success!") , HttpStatus.OK)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////// Configuration of the Project ///////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/configuration/{projectId}")
    fun getByPeriod(@PathVariable("projectId") projectId: Long) : ResponseEntity<ProjectConfigurationDto> {
        try {
            var configuration = projectService.findConfigurationByIdProject(projectId)
            if(configuration.project.id == null)
                configuration.project = findById(projectId)
            return ResponseEntity( projectConfigurationDtoConverter.toDto(configuration), HttpStatus.OK)
        } catch (e : EmptyResultDataAccessException) {
            return ResponseEntity(null, HttpStatus.OK)
        }
    }

    /**
     * Save or update new project configuration
     */
    @PostMapping("/configuration/save")
    fun save(@Valid @RequestBody c: ProjectConfigurationDto) : ResponseEntity<ProjectConfigurationDto> {
        return ResponseEntity( projectConfigurationDtoConverter.toDto( projectService.saveConfiguration( projectConfigurationDtoConverter.toModel(c) ) ), HttpStatus.OK)
    }

}