package br.ufrn.caze.holterci.converters

import br.ufrn.caze.holterci.domain.dtos.crud.MainRepositoryDto
import br.ufrn.caze.holterci.domain.models.metric.MainRepository
import org.mapstruct.InheritInverseConfiguration
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface MainRepositoryDtoConverter {

    fun toModel(dto: MainRepositoryDto) : MainRepository

    @InheritInverseConfiguration
    fun toDto(model: MainRepository) : MainRepositoryDto

    fun toDtoList(findAll: List<MainRepository>): List<MainRepositoryDto>
}