package br.ufrn.caze.holterci.domain.services.crud

import br.ufrn.caze.holterci.domain.exceptions.BusinessException
import br.ufrn.caze.holterci.domain.models.metric.MetricReferenceValues
import br.ufrn.caze.holterci.domain.ports.repositories.crud.MetricReferenceValueRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class MetricReferenceService // construtor injections of kotlin
    (
    private val metricReferenceValueRepository : MetricReferenceValueRepository
    ){

    /**
     * Save a new project on database
     */
    @Transactional
    fun save(m: MetricReferenceValues): MetricReferenceValues {

        if(metricReferenceValueRepository.containsReferenceValue(m))
            throw BusinessException("Reference Values for metric: \""+m.metric.denomination+"\" already defined")

        return metricReferenceValueRepository.save(m)
    }

    /**
     * Delete a reference value
     */
    @Transactional
    fun delete(id: Long) {
        metricReferenceValueRepository.delete(MetricReferenceValues(id))
    }
}