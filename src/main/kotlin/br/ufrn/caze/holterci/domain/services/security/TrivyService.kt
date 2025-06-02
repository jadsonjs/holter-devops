package br.ufrn.caze.holterci.domain.services.security

import br.ufrn.caze.holterci.domain.models.security.SeverityLevel
import br.ufrn.caze.holterci.domain.models.security.Vulnerability
import br.ufrn.caze.holterci.domain.utils.CloneGitLabUtil
import br.ufrn.caze.holterci.domain.utils.CommandExecutorUtil
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TrivyService(
    cloneGitLabUtil: CloneGitLabUtil,
    @Value("\${projects.path}") projectsPath: String,
    private val commandExecutor: CommandExecutorUtil
) : ScannerService(projectsPath) {

    override val scannerName = "Trivy"

    override fun getUpdateCommand(): List<String> = listOf("trivy", "image", "--download-db-only")

    /**
     * Executes a Trivy security scan on a local repository.
     *
     * @param localRepo The local path of the repository to scan.
     * @return The JSON output from the scan.
     */
    override fun executeScanner(localRepo: String): String {
        println("Running $scannerName scan for $localRepo")
        return commandExecutor.runCommand("trivy", "--scanners", "vuln", "repo", localRepo, "--format", "json", "--skip-db-update")
    }

    /**
     * Extracts vulnerabilities from the JSON output of a Trivy scan.
     *
     * @param json The JSON string containing the scan results.
     * @return A list of unique vulnerabilities, sorted by severity (highest first).
     */
    override fun extractVulnerabilitiesFromJson(json: String): List<Vulnerability> {
        val objectMapper = ObjectMapper()
        val rootNode: JsonNode = objectMapper.readTree(json)
        val vulnerabilitiesSeen = mutableSetOf<String>()

        val resultsNode = rootNode["Results"] ?: return emptyList()

        return resultsNode.flatMap { result ->
            val type = result["Type"]?.asText() ?: "-"

            result["Vulnerabilities"]?.mapNotNull { vuln ->
                val id = vuln["VulnerabilityID"].asText()
                val packageName = vuln["PkgName"].asText().split(":").last()
                // Trivy uses UPPERCASE
                val severityRaw = vuln["Severity"].asText().lowercase().replaceFirstChar { it.uppercaseChar() }
                val severity = SeverityLevel.fromString(severityRaw)
                val description = vuln["Title"]?.asText()
                val link = vuln["PrimaryURL"]?.asText()
                val currentVersion = vuln["InstalledVersion"].asText()
                val fixedVersion = vuln["FixedVersion"]?.asText()

                val key = "$id-$packageName-$currentVersion"
                if (vulnerabilitiesSeen.add(key)) {
                    Vulnerability(
                        id = id,
                        packageName = packageName,
                        type = type,
                        severity = severity,
                        description = description,
                        link = link,
                        currentVersion = currentVersion,
                        fixedInVersion = fixedVersion,
                    )
                } else null
            } ?: emptyList()
        }


    }
}
