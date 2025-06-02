package br.ufrn.caze.holterci.repositories.crud

import br.ufrn.caze.holterci.domain.models.metric.MetricSegment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MetricSegmentJPARepository : JpaRepository<MetricSegment, Long> {

    @Query(" SELECT ms FROM MetricSegment ms WHERE ms.id = ?1")
    fun findMetricSegmentById(id: Long): MetricSegment

    fun findByIdentifier(identifier: String): MetricSegment?

}