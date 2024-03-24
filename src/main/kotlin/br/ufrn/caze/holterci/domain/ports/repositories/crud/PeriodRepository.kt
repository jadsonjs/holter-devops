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
package br.ufrn.caze.holterci.domain.ports.repositories.crud

import br.ufrn.caze.holterci.domain.models.metric.Period
import java.time.LocalDateTime

interface PeriodRepository {

    /**
     * Return a period of a database by data to the project, if it exists
     */
    fun findPeriod(init: LocalDateTime, end: LocalDateTime, idProject: Long): Period?

    fun findById(id: Long): Period

    /**
     * Return the amount of periods for this project
     */
    fun countPeriodsOfProject(idProject: Long): Long

    /**
     * Return the list all period of a project with pagination
     */
    fun findPeriodsOfProject(idProject: Long, page : Int, size : Int ): List<Period>

    /**
     * Find the last index of period for a specific project
     */
    fun findLastIndex(idProject: Long): Int

    /**
     * Find the last period of execution for the project
     */
    fun findLastPeriod(idProject: Long): Period

    /**
     * delete all period of the project
     */
    fun deleteAllBYProject(idProject: Long)
}