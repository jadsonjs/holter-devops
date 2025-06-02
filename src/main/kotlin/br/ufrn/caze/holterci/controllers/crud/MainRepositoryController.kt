package br.ufrn.caze.holterci.controllers.crud

import br.ufrn.caze.holterci.controllers.AbstractRestController
import br.ufrn.caze.holterci.converters.MainRepositoryDtoConverter
import br.ufrn.caze.holterci.domain.dtos.MessageDto
import br.ufrn.caze.holterci.domain.dtos.crud.MainRepositoryDto
import br.ufrn.caze.holterci.domain.ports.repositories.crud.MainRepositoryRepository
import br.ufrn.caze.holterci.domain.services.crud.MainRepositoryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/main-repository")
class MainRepositoryController (
    private val mainRepositoryRepository: MainRepositoryRepository,
    private val mainRepositoryDtoConverter: MainRepositoryDtoConverter,
    private val mainRepositoryService: MainRepositoryService
)
    : AbstractRestController()
{

    /**
     * Return all main repository configs
     */
    @GetMapping
    fun getAll() : ResponseEntity<List<MainRepositoryDto>> {
        return ResponseEntity( mainRepositoryDtoConverter.toDtoList(mainRepositoryRepository.findAll() ), HttpStatus.OK)
    }

    /**
     * Get a specific main repository for edit
     */
    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long) : MainRepositoryDto {
        return mainRepositoryDtoConverter.toDto(mainRepositoryRepository.findById(id))
    }


    /**
     * Save a new main repository
     */
    @PostMapping("/save")
    fun save(@Valid @RequestBody m: MainRepositoryDto) : ResponseEntity<MainRepositoryDto> {
        return ResponseEntity( mainRepositoryDtoConverter.toDto( mainRepositoryService.save( mainRepositoryDtoConverter.toModel(m) ) ), HttpStatus.OK)
    }

    /**
     * Delete a main repository
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) : ResponseEntity<MessageDto> {
        mainRepositoryService.delete(id)
        return ResponseEntity( MessageDto("Repository Removed with Success!") , HttpStatus.OK)
    }
}