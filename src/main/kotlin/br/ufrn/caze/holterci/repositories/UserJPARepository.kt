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
package br.ufrn.caze.holterci.repositories

import br.ufrn.caze.holterci.domain.models.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserJPARepository : JpaRepository<User, Long> {

    @Query(" SELECT u FROM User u WHERE u.email = ?1 ")
    fun findByEmail(email: String): User

    @Query(" SELECT u FROM User u WHERE u.email = ?1 AND u.password = ?2")
    fun findByEmailAndPassword(email: String, password: String): User


    /**
     * Return all user's emails that has permission to a project or has no project and a flag alert is marked as ''true''
     */
    @Query( " SELECT u.email " +
            " FROM User u " +
            " LEFT JOIN u.permission per " +
            " WHERE u.alert = true AND ( per.project.id = ?1 OR per.project IS NULL ) ")
    fun findUsersEmailsSendAlert(idProject: Long): List<String>


    /**
     * it can not exist 2 users same email in the tool
     */
    @Query("SELECT COUNT(u) FROM User u WHERE ( u.id <> :#{#user.id} OR :#{#user.id} IS NULL ) AND u.email = :#{#user.email}")
    fun countUsersSameEmail(@Param("user") user: User): Long


}