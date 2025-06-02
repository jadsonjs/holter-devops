package br.ufrn.caze.holterci.collectors.dtos

import br.ufrn.caze.holterci.domain.models.metric.MetricSegment
import java.math.BigDecimal

data class CollectResult(
    val value: BigDecimal? = null,
    val description: String? = null,
    val segment: MetricSegment? = null,

    var values: List<CollectResult>? = null,
) {

    constructor(values: List<CollectResult>) : this() {
        this.values = values
    }

    fun isSingle(): Boolean = values == null

    fun isMultiple(): Boolean = !values.isNullOrEmpty()

}
