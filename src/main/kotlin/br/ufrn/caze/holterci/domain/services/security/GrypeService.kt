package br.ufrn.caze.holterci.domain.services.security

import br.ufrn.caze.holterci.domain.models.security.SeverityLevel
import br.ufrn.caze.holterci.domain.models.security.Vulnerability
import br.ufrn.caze.holterci.domain.utils.CommandExecutorUtil
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * Service responsible for scanning projects for security vulnerabilities using Grype.
 */
@Service
class GrypeService(
    @Value("\${projects.path}") projectsPath: String,
    private val commandExecutor: CommandExecutorUtil
) : ScannerService(projectsPath) {

    override val scannerName = "Grype"

    override fun getUpdateCommand(): List<String> = listOf("grype", "db", "update")

    /**
     * Executes a Grype security scan on a local repository.
     *
     * @param localRepo The local path of the repository to scan.
     * @return The JSON output from the scan.
     */
    override fun executeScanner(localRepo: String): String {
        println("Running $scannerName scan for $localRepo")
        return commandExecutor.runCommand("grype", "dir:$localRepo", "-o", "json", "--by-cve")
    }

    /**
     * Extracts vulnerabilities from the JSON output of a Grype scan.
     *
     * @param json The JSON string containing the scan results.
     * @return A list of unique vulnerabilities, sorted by severity (highest first).
     */
    override fun extractVulnerabilitiesFromJson(json: String): List<Vulnerability> {
        val objectMapper = ObjectMapper()
        val rootNode: JsonNode = objectMapper.readTree(json)
        val vulnerabilitiesSeen = mutableSetOf<String>()

        return rootNode["matches"]?.mapNotNull { item ->
            val vulnerabilityId = item["vulnerability"]["id"].asText()
            val packageName = item["artifact"]["name"].asText()
            val currentVersion = item["artifact"]["version"].asText()
            val severityRaw = item["vulnerability"]["severity"].asText()
            val severity = SeverityLevel.fromString(severityRaw)

            // Create a unique key for each vulnerability to avoid unnecessary duplicates
            val key = "$vulnerabilityId-$packageName-$currentVersion"

            // Return the vulnerability object if it's not already seen
            return@mapNotNull if (!vulnerabilitiesSeen.contains(key)) {
                vulnerabilitiesSeen.add(key)
                Vulnerability(
                    id = vulnerabilityId,
                    packageName = packageName,
                    type = item["artifact"]["type"].asText(),
                    severity = severity,
                    description = item["vulnerability"]["description"].asText(),
                    link = item["vulnerability"]["dataSource"].asText(),
                    currentVersion = currentVersion,
                    fixedInVersion = item["vulnerability"]["fix"]?.get("versions")
                        ?.takeIf { it.isArray && it.size() > 0 }
                        ?.joinToString(", ") { it.asText() },
                )
            } else null
        } ?: emptyList()

    }

}