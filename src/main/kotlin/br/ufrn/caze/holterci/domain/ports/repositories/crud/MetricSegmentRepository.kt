package br.ufrn.caze.holterci.domain.ports.repositories.crud

import br.ufrn.caze.holterci.domain.models.metric.MetricSegment

interface MetricSegmentRepository {

    fun save(entity: MetricSegment): MetricSegment

    fun delete(entity: MetricSegment)

    fun findAll(): List<MetricSegment>

    fun findById(id: Long): MetricSegment?

    fun findByIdentifier(identifier: String): MetricSegment?
}