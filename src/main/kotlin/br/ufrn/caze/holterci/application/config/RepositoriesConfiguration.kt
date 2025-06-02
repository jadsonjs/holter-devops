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
 * RepositoriesConfiguration
 * 28/03/21
 */
package br.ufrn.caze.holterci.application.config

import br.ufrn.caze.holterci.domain.ports.repositories.crud.*
import br.ufrn.caze.holterci.domain.ports.repositories.results.MetricEvolutionRepository
import br.ufrn.caze.holterci.repositories.crud.*
import br.ufrn.caze.holterci.repositories.results.MetricEvolutionRepositoryImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Define the implementation of repositories
 *
 * @author jadson santos - jadson.santos@ufrn.br
 * @author bruno batista - brunokaike@ufrn.edu.br
 */
@Configuration
class RepositoriesConfiguration {


    @Bean
    fun projectRepository(jpaRepository: ProjectJPARepository) : ProjectRepository {
        return ProjectRepositoryImpl(jpaRepository)
    }

    @Bean
    fun mainRepositoryConfigurationRepository(jpaRepository: MainRepositoryConfigurationJPARepository) : MainRepositoryRepository {
        return MainRepositoryImpl(jpaRepository)
    }

    @Bean
    fun schedulerRepository(jpaRepository: SchedulerJPARepository) : SchedulerRepository {
        return SchedulerRepositoryImpl(jpaRepository)
    }

    @Bean
    fun metricHistoryRepository(jpaRepository: MetricHistoryJPARepository) : MetricHistoryRepository {
        return MetricHistoryRepositoryImpl(jpaRepository)
    }


    @Bean
    fun metricEvolutionRepository() : MetricEvolutionRepository {
        return MetricEvolutionRepositoryImpl()
    }



    @Bean
    fun periodRepository(jpaRepository: PeriodJPARepository) : PeriodRepository {
        return PeriodRepositoryImpl(jpaRepository)
    }


    @Bean
    fun metricReferenceRepository(jpaRepository: MetricReferenceValueJPARepository) : MetricReferenceValueRepository {
        return MetricReferenceValueRepositoryImpl(jpaRepository)
    }


    @Bean
    fun generalMetricRepository(jpaRepository: ControlMetricJPARepository) : ControlMetricRepository {
        return ControlMetricRepositoryImpl(jpaRepository)
    }

    @Bean
    fun  metricSegmentRepository(jpaRepository: MetricSegmentJPARepository) : MetricSegmentRepository {
        return MetricSegmentRepositoryImpl(jpaRepository)
    }
}