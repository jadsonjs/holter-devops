package br.ufrn.caze.holterci.repositories.crud


import br.ufrn.caze.holterci.domain.models.metric.MainRepository
import br.ufrn.caze.holterci.domain.ports.repositories.crud.MainRepositoryRepository

class MainRepositoryImpl(
    private val jpaRepository: MainRepositoryConfigurationJPARepository,
) : MainRepositoryRepository {

    override fun save(entity: MainRepository): MainRepository {
        return jpaRepository.save(entity)
    }

    override fun delete(entity: MainRepository) {
        return jpaRepository.delete(entity)
    }

    override fun findById(id: Long): MainRepository {
        return jpaRepository.findById(id).get()
    }

    override fun findAll(): List<MainRepository> {
        return jpaRepository.findAll()
    }
}