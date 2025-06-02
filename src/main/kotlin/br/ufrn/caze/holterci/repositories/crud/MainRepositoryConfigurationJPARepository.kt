package br.ufrn.caze.holterci.repositories.crud

import br.ufrn.caze.holterci.domain.models.metric.MainRepository
import org.springframework.data.jpa.repository.JpaRepository

interface MainRepositoryConfigurationJPARepository : JpaRepository<MainRepository, Long> {
}