package br.ufrn.caze.holterci.domain.models.security

import br.ufrn.caze.holterci.domain.models.metric.Project

data class ProjectVulnerabilities(
    val project: Project,
    val vulnerabilities: List<Vulnerability>
)
