package br.ufrn.caze.holterci.domain.services.crud

import br.ufrn.caze.holterci.domain.exceptions.BusinessException
import br.ufrn.caze.holterci.domain.models.metric.MetricReferenceValues
import br.ufrn.caze.holterci.domain.models.metric.Project
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

    /**
     * Copy reference values from one project to another
     *
     * @param sourceProjectId the id of the project to copy reference values from
     * @param targetProjectId the id of the project to receive the new values
     */
    @Transactional
    fun copy(sourceProjectId: Long, targetProjectId: Long) {
        val sourceReferenceValues = metricReferenceValueRepository.findAllByProject(sourceProjectId)

        if (sourceReferenceValues.isEmpty()) {
            throw BusinessException("No reference values found in the source project")
        }

        // Fetch all reference values for the current project and extract the metrics
        val existingMetrics = metricReferenceValueRepository.findAllByProject(targetProjectId)
            .map { it.metric }
            .toSet()

        // Only copy reference values for metrics that don't already exist in the current project
        val newReferences = sourceReferenceValues
            .filter { it.metric !in existingMetrics }
            .map {
                MetricReferenceValues().apply {
                    metric = it.metric
                    value = it.value
                    project = Project(targetProjectId)
                }
            }

        if (newReferences.isNotEmpty()) {
            metricReferenceValueRepository.saveAll(newReferences)
        } else {
            throw BusinessException("Current project already contains all reference values from the source project")
        }
    }

}