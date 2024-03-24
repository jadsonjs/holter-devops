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
package br.ufrn.caze.holterci.repositories.crud

import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.ports.repositories.crud.PeriodRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import jakarta.persistence.PersistenceContext
import java.time.LocalDateTime

class PeriodRepositoryImpl
    (
    private val jpaRepository : PeriodJPARepository,
    )
    : PeriodRepository {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun findPeriod(init: LocalDateTime, end: LocalDateTime, idProject: Long): Period? {

        val query =  entityManager.createQuery(
            " SELECT p  FROM Period p " +
                    " INNER JOIN p.project proj " +
                    " WHERE p.init = :init AND p.end = :end AND proj.id = :idProject ")
            .setParameter("init", init)
            .setParameter("end", end)
            .setParameter("idProject", idProject)

        try{
            return query.singleResult as Period
        }catch (nre : NoResultException){
            return null
        }
    }

    override fun findById(id: Long): Period {
        return jpaRepository.findById(id).get()
    }

    override fun countPeriodsOfProject(idProject: Long): Long {
        val query =  entityManager.createQuery(
            " SELECT COUNT(*) FROM Period p " +
                    " INNER JOIN p.project pro " +
                    " WHERE pro.id = :idProject ")
            .setParameter("idProject", idProject)

        return (query.singleResult as Long)
    }

    override fun findPeriodsOfProject(idProject: Long, page : Int, size : Int ): List<Period>{

        val query =  entityManager.createQuery(
            " SELECT p " +
                    " FROM Period p " +
                    " INNER JOIN p.project pro " +
                    " WHERE pro.id = :idProject "+
                    " ORDER BY p.end DESC ")
            .setParameter("idProject", idProject)

        query.setFirstResult((page-1) * size);
        query.setMaxResults(size)

        return query.resultList as MutableList<Period>

    }

    override fun findLastIndex(idProject: Long): Int {
        val query =  entityManager.createQuery(
            " SELECT p.index FROM Period p " +
                    " INNER JOIN p.project pro " +
                    " WHERE pro.id = :idProject "+
                    " ORDER BY p.index DESC ")
            .setParameter("idProject", idProject)
            .setMaxResults(1)
        try {
            return query.singleResult as Int
        }catch (nre : NoResultException){
            return 0;
        }
    }

    override fun findLastPeriod(idProject: Long): Period {
        val query =  entityManager.createQuery(
            " SELECT p FROM Period p " +
                    " INNER JOIN p.project pro " +
                    " WHERE pro.id = :idProject "+
                    " ORDER BY p.index DESC ")
            .setParameter("idProject", idProject)
            .setMaxResults(1)
        try {
            return query.singleResult as Period
        }catch (nre : NoResultException){
            return Period(LocalDateTime.now(), LocalDateTime.now(), Project(idProject) )
        }
    }

    override fun deleteAllBYProject(idProject: Long) {
        jpaRepository.deleteAllBYProject(idProject)
    }

}