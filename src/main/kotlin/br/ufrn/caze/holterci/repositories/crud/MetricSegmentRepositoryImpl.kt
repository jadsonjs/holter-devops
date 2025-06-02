package br.ufrn.caze.holterci.repositories.crud

import br.ufrn.caze.holterci.domain.models.metric.MetricSegment
import br.ufrn.caze.holterci.domain.ports.repositories.crud.MetricSegmentRepository

class MetricSegmentRepositoryImpl (private val jpaRepository : MetricSegmentJPARepository)  : MetricSegmentRepository {

    override fun save(entity: MetricSegment): MetricSegment {
        return jpaRepository.save(entity)
    }

    override fun delete(entity: MetricSegment) {
        jpaRepository.delete(entity)
    }

    override fun findAll(): List<MetricSegment> {
        return jpaRepository.findAll()
    }

    override fun findById(id: Long): MetricSegment {
        return jpaRepository.findMetricSegmentById(id)
    }

    override fun findByIdentifier(identifier: String): MetricSegment? {
        return jpaRepository.findByIdentifier(identifier)
    }
}