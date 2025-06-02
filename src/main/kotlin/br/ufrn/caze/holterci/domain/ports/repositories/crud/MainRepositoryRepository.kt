package br.ufrn.caze.holterci.domain.ports.repositories.crud

import br.ufrn.caze.holterci.domain.models.metric.MainRepository

interface MainRepositoryRepository {

    fun save(entity: MainRepository): MainRepository

    fun delete(entity: MainRepository)

    fun findById(id: Long): MainRepository

    fun findAll(): List<MainRepository>

}