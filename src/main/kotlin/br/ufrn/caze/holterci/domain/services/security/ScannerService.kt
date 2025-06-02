package br.ufrn.caze.holterci.domain.services.security

import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.models.security.ProjectVulnerabilities
import br.ufrn.caze.holterci.domain.models.security.Vulnerability
import org.springframework.beans.factory.annotation.Value

/**
 * Abstract base class for security vulnerability scanners.
 *
 * @param projectsPath Path to the directory where projects are locally stored.
 */
abstract class ScannerService(
    @Value("\${projects.path}") private val projectsPath: String
) {

    /**
     * The name of the scanner implementation (e.g., Grype, Trivy).
     */
    abstract val scannerName: String

    /**
     * Returns the command used to update the scanner's vulnerability database.
     */
    protected open fun getUpdateCommand(): List<String>? = null

    /**
     * Scans a list of projects for vulnerabilities.
     *
     * @param projects The list of projects to scan.
     * @return A list of projects with its associated vulnerabilities.
     */
    open fun scanProjectsForVulnerabilities(projects: List<Project>): List<ProjectVulnerabilities> {
        getUpdateCommand()?.let { command ->
            println("Updating $scannerName database...")
            val updateProcess = ProcessBuilder(command).start()
            val exitCode = updateProcess.waitFor()
            if (exitCode != 0) {
                println("$scannerName database update failed with exit code: $exitCode")
            } else {
                println("$scannerName database updated successfully.")
            }
        }
        return projects.map { scanProject(it) }
    }

    /**
     * Scans a single project for vulnerabilities.
     *
     * @param project The project to scan.
     * @return A pair containing the project and its list of detected vulnerabilities.
     */
    private fun scanProject(project: Project): ProjectVulnerabilities {
        val localRepo = "$projectsPath/${project.name}"
        val output = executeScanner(localRepo)
        val vulnerabilities = extractVulnerabilitiesFromJson(output)
        return ProjectVulnerabilities(project, vulnerabilities)
    }

    /**
     * Executes the scanner tool and returns its output.
     *
     * @param localRepo The local path of the project.
     * @return The scanner's output.
     */
    protected abstract fun executeScanner(localRepo: String): String

    protected abstract fun extractVulnerabilitiesFromJson(json: String): List<Vulnerability>
}
