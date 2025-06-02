package br.ufrn.caze.holterci.domain.services.crud

import br.ufrn.caze.holterci.domain.models.metric.MainRepository
import br.ufrn.caze.holterci.domain.ports.repositories.crud.MainRepositoryRepository
import br.ufrn.caze.holterci.domain.ports.repositories.crud.ProjectRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class MainRepositoryService(
    private val mainRepositoryRepository: MainRepositoryRepository,
    private val projectRepository: ProjectRepository,
    ) {

    /**
     * Save a new configuration on database
     */
    @Transactional
    fun save(c: MainRepository): MainRepository {
        return mainRepositoryRepository.save(c)
    }

    /**
     * Delete a reference value
     */
    @Transactional
    fun delete(id: Long) {
        projectRepository.deleteConfigurationByMainRepository(id)
        mainRepositoryRepository.delete(MainRepository(id))
    }
}