package br.ufrn.caze.holterci.domain.models.metric

import jakarta.persistence.*


@Entity
@Table(name="metric_segment", schema = "holter")
data class MetricSegment (

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    var id:Long? = null,

    @Column(name = "type", nullable = false)
    var type: String = "",

    @Column(name = "identifier", nullable = false)
    var identifier: String = "",

    @Column(name = "extra_info")
    var extraInfo: String? = null



)