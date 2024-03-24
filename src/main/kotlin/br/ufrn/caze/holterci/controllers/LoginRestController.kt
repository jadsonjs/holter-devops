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
package br.ufrn.caze.holterci.controllers

import br.ufrn.caze.holterci.application.security.JwtManager
import br.ufrn.caze.holterci.domain.dtos.user.UserDto
import br.ufrn.caze.holterci.domain.exceptions.BusinessException
import br.ufrn.caze.holterci.domain.models.user.User
import br.ufrn.caze.holterci.domain.utils.NetUtil
import br.ufrn.caze.holterci.repositories.UserJPARepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/login")
class LoginRestController (
    private val userJPARepository: UserJPARepository,
    private val passwordEncoder : PasswordEncoder,
    private val jwtManager: JwtManager,
    private val netUtil: NetUtil,
) : AbstractRestController() {

    /**
     * Loggin in the system
     */
    @PostMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun logging(@Valid @RequestBody userDto : UserDto, request: HttpServletRequest): ResponseEntity<UserDto> {

        try {
            var user: User = userJPARepository.findByEmail(userDto.email)

            if ( !passwordEncoder.matches(userDto.password, user.password) )
                throw EmptyResultDataAccessException(1)

            var dto: UserDto = userDtoConverter.toDto(user)
            dto.jwtToken = jwtManager.generateToken(user.email, netUtil.getClientIpAddress(request), netUtil.getClientUserAgent(request))

            return ResponseEntity(dto, HttpStatus.OK)
        }catch (empty : EmptyResultDataAccessException ){
            throw BusinessException("User not found")
        }

    }

}