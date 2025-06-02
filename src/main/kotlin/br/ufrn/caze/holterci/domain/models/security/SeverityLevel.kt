package br.ufrn.caze.holterci.domain.models.security

enum class SeverityLevel(val label: String, val order: Int) {
    Critical("Critical", 5),
    High("High", 4),
    Medium("Medium", 3),
    Low("Low", 2),
    Negligible("Negligible", 1),
    Unknown("Unknown", 0);

    companion object {
        fun fromString(severity: String?): SeverityLevel {
            return entries.find { it.label.equals(severity, ignoreCase = true) } ?: Unknown
        }
    }
}
