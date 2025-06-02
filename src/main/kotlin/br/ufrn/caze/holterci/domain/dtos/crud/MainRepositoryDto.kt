package br.ufrn.caze.holterci.domain.dtos.crud


import jakarta.validation.constraints.NotBlank

data class MainRepositoryDto(
    var id: Long? = null,

    @get:NotBlank
    var name: String = "",
    @get:NotBlank
    var url: String = "",
    @get:NotBlank
    var token: String = "",
    @get:NotBlank
    var produtionBranch: String,
    @get:NotBlank
    var issuesErrosLabels: String? = "",
    )