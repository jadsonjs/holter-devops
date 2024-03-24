package br.ufrn.caze.holterci.domain.services.crud

import br.ufrn.caze.holterci.domain.models.metric.ControlMetric
import br.ufrn.caze.holterci.domain.ports.repositories.crud.ControlMetricRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class ControlMetricService
    (
    private val measureReferenceRepository : ControlMetricRepository
    ){

        /**
         * Save a new project on database
         */
        @Transactional
        fun save(p: ControlMetric): ControlMetric {
            return measureReferenceRepository.save(p)
        }

        /**
         * Delete a reference value
         */
        @Transactional
        fun delete(id: Long) {
            measureReferenceRepository.delete(ControlMetric(id))
        }
}