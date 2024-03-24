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
 * br.ufrn.caze.holterci.controllers.crud
 * UserController
 * 18/11/23
 */
package br.ufrn.caze.holterci.controllers.crud

import br.ufrn.caze.holterci.controllers.AbstractRestController
import br.ufrn.caze.holterci.domain.dtos.MessageDto
import br.ufrn.caze.holterci.domain.dtos.user.RoleDto
import br.ufrn.caze.holterci.domain.dtos.user.UserDto
import br.ufrn.caze.holterci.domain.models.user.Role
import br.ufrn.caze.holterci.domain.services.crud.UserService
import br.ufrn.caze.holterci.repositories.UserJPARepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Simple Crud of users
 *
 * @author Jadson Santos - jadson.santos@ufrn.br
 */
@RestController
@RequestMapping("/api/user")
class UserRestController  (
    private val userJPARepository : UserJPARepository,
    private val userService : UserService,
)
    : AbstractRestController()

{

    /**
     * list all users
     */
    @GetMapping
    fun list() : ResponseEntity<List<UserDto>> {
        return ResponseEntity( userDtoConverter.toDtoList(userJPARepository.findAll()), HttpStatus.OK)
    }


    /**
     * Get a specific project by id
     */
    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long) : UserDto {
        return userDtoConverter.toDto(userJPARepository.findById(id).get())
    }


    /**
     * Save a new user
     */
    @PostMapping("/save")
    fun save(@Valid @RequestBody user: UserDto, request: HttpServletRequest) : ResponseEntity<UserDto> {
        return ResponseEntity(userDtoConverter.toDto(userService.save(userDtoConverter.toModel(user), getLoggedInUser(request) )), HttpStatus.OK)
    }

    /**
     * Delete a user
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) : ResponseEntity<MessageDto> {
        userService.delete(id)
        return ResponseEntity( MessageDto("User Removed with Success!") , HttpStatus.OK)
    }


    @GetMapping("/roles/all")
    fun getAllRoles() : ResponseEntity<List<RoleDto>> {
        val list = ArrayList<RoleDto>()
        Role.getAll().forEach {
            list.add(RoleDto().toDto(it))
        }
        return ResponseEntity(list, HttpStatus.OK)
    }
}