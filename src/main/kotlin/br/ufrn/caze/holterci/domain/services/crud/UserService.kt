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
 * br.ufrn.caze.holterci.domain.services.crud
 * UserService
 * 18/11/23
 */
package br.ufrn.caze.holterci.domain.services.crud

import br.ufrn.caze.holterci.domain.exceptions.BusinessException
import br.ufrn.caze.holterci.domain.models.user.Role
import br.ufrn.caze.holterci.domain.models.user.User
import br.ufrn.caze.holterci.domain.utils.EmailUtil
import br.ufrn.caze.holterci.domain.utils.PermissionUtil
import br.ufrn.caze.holterci.repositories.UserJPARepository
import br.ufrn.caze.holterci.repositories.log.AccessLogJPARepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * Busyness rules to create user in the system.
 * Only ADMIN users can create new users
 */
@Service
class UserService
    (
    private val userJPARepository : UserJPARepository,
    private val accessLogJPARepository : AccessLogJPARepository,
    private val passwordEncoder : PasswordEncoder,
    private val permissionUtil : PermissionUtil,
    private val emailUtil: EmailUtil,
)
{

    @Value("\${enable.login-page}")
    val enableLoginPage : Boolean = false

    /**
     * Save a new project on database
     */
    @Transactional
    fun save(user: User, loggedUser: User?): User {
        if(enableLoginPage) {
            if (loggedUser == null || !permissionUtil.isUserInRole(loggedUser, Role.ADMIN))
                throw BusinessException("Only ADMIN users can create new users")
        }

        // Business rule
        if(userAlreadExists(user))
            throw BusinessException("User with email: "+user.email+" alread exists.")

        if(! emailUtil.isValidEmail(user.email))
            throw BusinessException("User email: \""+user.email+"\" is not valid.")

        user.permission.forEach { it.user = user }

        /**
         * Encode the password of the user before save
         */
        if(! isBCryptEncodedPassword(user.password)) {
            user.password = passwordEncoder.encode(user.password)
        }

        return userJPARepository.save(user)
    }

    fun isBCryptEncodedPassword(candidate: String): Boolean {
        return candidate.startsWith("\$2a\$")
    }

    /**
     * Delete a project from database.
     */
    @Transactional
    fun delete(idUser: Long) {
        accessLogJPARepository.deleteAllBYUser(idUser)
        userJPARepository.delete(User(idUser))
    }

    ////////////////////////////////////////////////////////

    /**
     * Checks if the user already exists.
     */
    private fun userAlreadExists (user: User) : Boolean {
        return userJPARepository.countUsersSameEmail(user) > 0
    }

}