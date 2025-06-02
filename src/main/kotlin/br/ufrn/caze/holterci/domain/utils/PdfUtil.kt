package br.ufrn.caze.holterci.domain.utils

import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.models.security.ProjectVulnerabilities
import br.ufrn.caze.holterci.domain.models.security.Vulnerability
import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import org.springframework.stereotype.Component
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for generating PDF reports of vulnerabilities in projects.
 */
@Component
class PdfUtil {

    /**
     * Generates a PDF report for the vulnerabilities found in the provided projects.
     *
     * @param projectsWithVulnerabilities List of projects along with their respective vulnerabilities.
     * @return ByteArray containing the generated PDF document.
     */
    fun generateVulnerabilityPdf(projectsWithVulnerabilities: List<ProjectVulnerabilities>): ByteArray {
        val document = Document(PageSize.A4)
        val outputStream = ByteArrayOutputStream()
        PdfWriter.getInstance(document, outputStream)

        document.open()

        // Add the general report generation timestamp
        addTimestamp(document)

        // Add title for the report
        addTitle(document)

        // Select only the projects that have at least one associated vulnerability
        val vulnerableProjects = projectsWithVulnerabilities.filter { it.vulnerabilities.isNotEmpty() }

        // If no vulnerabilities were found, add a different message and return the .pdf
        if (vulnerableProjects.isEmpty()) {
            addNoVulnerabilitiesFoundMessage(document)
            document.close()
            return outputStream.toByteArray()
        }

        // Add summary of critical vulnerabilities - xx critical vulnerabilies in yy projects
        addCriticalSummary(document, projectsWithVulnerabilities)

        document.add(Chunk.NEWLINE)

        val vulnerableProjectsSortedByName = vulnerableProjects.sortedBy { it.project.name }
        addProjectList(document, vulnerableProjectsSortedByName)

        document.add(Chunk.NEWLINE)

        // Add project-specific reports
        var index = 1
        for ((project, vulnerabilities) in vulnerableProjectsSortedByName) {
            addProjectReport(document, project, vulnerabilities, index)
            index++
            document.newPage()
        }

        document.close()
        return outputStream.toByteArray()
    }

    /**
     * Adds a message indicating that no vulnerabilities were found in the report.
     *
     * @param document The PDF document to add the message to.
     */
    private fun addNoVulnerabilitiesFoundMessage(document: Document) {
        val noVulnerabilitiesFont = Font(Font.HELVETICA, 18f, Font.BOLD)

        val header = Paragraph("No vulnerabilities found", noVulnerabilitiesFont)
        header.alignment = Element.ALIGN_CENTER
        header.spacingBefore = 10f
        document.add(header)
    }

    /**
     * Adds a timestamp of when the report was generated to the document.
     *
     * @param document The PDF document to add the timestamp to.
     */
    private fun addTimestamp(document: Document) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dateGenerated = Date()

        val dateTable = PdfPTable(1)
        dateTable.widthPercentage = 100f
        val dateCell = PdfPCell(Paragraph("Report generated at: ${dateFormat.format(dateGenerated)}", Font(Font.HELVETICA, 10f, Font.NORMAL)))
        dateCell.border = 0  // No border for the cell
        dateCell.horizontalAlignment = Element.ALIGN_RIGHT
        dateCell.verticalAlignment = Element.ALIGN_TOP
        dateCell.setPadding(0f)
        dateTable.addCell(dateCell)
        document.add(dateTable)
    }

    /**
     * Adds the title for the report to the document.
     *
     * @param document The PDF document to add the title to.
     */
    private fun addTitle(document: Document) {
        val titleFont = Font(Font.HELVETICA, 24f, Font.BOLD)
        val title = Paragraph("Vulnerability Report", titleFont)
        title.alignment = Element.ALIGN_CENTER
        document.add(title)
    }

    /**
     * Adds a summary of the number of critical vulnerabilities found across the projects to the document.
     *
     * @param document The PDF document to add the summary to.
     * @param projectsWithVulnerabilities List of projects and their vulnerabilities.
     */
    private fun addCriticalSummary(document: Document, projectsWithVulnerabilities: List<ProjectVulnerabilities>) {
        val criticalVulnerabilities = projectsWithVulnerabilities
            .flatMap { it.vulnerabilities }
            .count { it.severity.label.equals("critical", ignoreCase = true) }
        val totalProjectsWithVulnerabilities = projectsWithVulnerabilities.count { it.vulnerabilities.isNotEmpty() }
        val regularFont = Font(Font.HELVETICA, 18f, Font.BOLD)
        val redFont = Font(Font.HELVETICA, 18f, Font.BOLD, Color(217, 83, 79))
        val paragraph = Paragraph().apply {
            alignment = Element.ALIGN_CENTER
            add(Phrase("$criticalVulnerabilities ", regularFont))
            add(Phrase("critical ", redFont))
            val vulnLabel = if (criticalVulnerabilities == 1) "vulnerability" else "vulnerabilities"
            val projectLabel = if (totalProjectsWithVulnerabilities == 1) "project" else "projects"
            add(Phrase("$vulnLabel found in $totalProjectsWithVulnerabilities $projectLabel", regularFont))
            spacingBefore = 10f
        }
        document.add(paragraph)
    }

    /**
     * Adds a list of scanned projects below the title.
     *
     * @param document The PDF document to add the list to.
     * @param projectsWithVulnerabilities List of projects and their vulnerabilities.
     */
    private fun addProjectList(document: Document, projectsWithVulnerabilities: List<ProjectVulnerabilities>) {
        val sectionTitleFont = Font(Font.HELVETICA, 14f, Font.BOLD)
        val projectFont = Font(Font.HELVETICA, 12f, Font.NORMAL)

        val sectionTitle = Paragraph("Scanned projects:", sectionTitleFont)
        sectionTitle.spacingBefore = 10f
        sectionTitle.spacingAfter = 5f
        document.add(sectionTitle)

        val list = com.lowagie.text.List(com.lowagie.text.List.ORDERED)
        val projectNames = projectsWithVulnerabilities.map { it.project.name }

        for (name in projectNames) {
            list.add(ListItem(name, projectFont))
        }

        document.add(list)
    }

    /**
     * Adds a report for a single project, including a table of its vulnerabilities and a summary.
     *
     * @param document The PDF document to add the project report to.
     * @param project The project to generate a report for.
     * @param vulnerabilities The vulnerabilities associated with the project.
     * @param index The index of the project in the list (used for numbering the reports).
     */
    private fun addProjectReport(document: Document, project: Project, vulnerabilities: List<Vulnerability>, index: Int) {
        val projectTitleFont = Font(Font.HELVETICA, 18f, Font.BOLD)
        val projectTitle = Paragraph("$index. ${project.name}", projectTitleFont)
        projectTitle.alignment = Element.ALIGN_CENTER
        document.add(projectTitle)

        // Add a summary of severities for this project
        val severityCount = mutableMapOf<String, Int>()
        for (vulnerability in vulnerabilities) {
            val severityLabel = vulnerability.severity.label
            severityCount[severityLabel] = severityCount.getOrDefault(severityLabel, 0) + 1
        }
        val severitySummary = severityCount.entries.joinToString("\n") { "${it.key}: ${it.value} vulnerabilities" }
        document.add(Paragraph("Project Severity Summary:\n$severitySummary", Font(Font.HELVETICA, 12f, Font.NORMAL)))

        document.add(Chunk.NEWLINE)

        // Add table of vulnerabilities for the current project
        val table = generateVulnerabilityTable(vulnerabilities)
        document.add(table)
    }

    /**
     * Generates a table displaying the vulnerabilities of a project.
     *
     * @param vulnerabilities List of vulnerabilities to be displayed in the table.
     * @return The generated PdfPTable containing the vulnerabilities.
     */
    private fun generateVulnerabilityTable(vulnerabilities: List<Vulnerability>): PdfPTable {
        val table = PdfPTable(6)
        table.widthPercentage = 100f
        // PACKAGE, VERSION, TYPE, VULNERABILITY, SEVERITY, FIXED IN
        table.setWidths(floatArrayOf(3f, 3.3f, 3f, 4.5f, 2.7f, 3.3f))

        // Add table header
        addTableHeader(table)

        // Add vulnerability data for this project
        for (vulnerability in vulnerabilities) {
            addVulnerabilityRow(table, vulnerability)
        }

        return table
    }

    /**
     * Adds a header row to the vulnerability table.
     *
     * @param table The PdfPTable to add the header to.
     */
    private fun addTableHeader(table: PdfPTable) {
        val headerFont = Font(Font.HELVETICA, 12f, Font.BOLD)
        table.addCell(createCell("PACKAGE", headerFont, true))
        table.addCell(createCell("VERSION", headerFont, true))
        table.addCell(createCell("TYPE", headerFont, true))
        table.addCell(createCell("VULNERABILITY", headerFont, true))
        table.addCell(createCell("SEVERITY", headerFont, true))
        table.addCell(createCell("FIXED IN", headerFont, true))
    }

    /**
     * Adds a row to the vulnerability table for a given vulnerability.
     *
     * @param table The PdfPTable to add the row to.
     * @param vulnerability The vulnerability whose data will be displayed in the row.
     */
    private fun addVulnerabilityRow(table: PdfPTable, vulnerability: Vulnerability) {
        val contentFont = Font(Font.HELVETICA, 10f, Font.NORMAL)
        table.addCell(createCell(vulnerability.packageName, contentFont))
        table.addCell(createCell(vulnerability.currentVersion, contentFont))

        table.addCell(createCell(vulnerability.type ?: "-", contentFont))

        // Create a clickable link for the vulnerability ID
        val link = Anchor(vulnerability.id, Font(Font.HELVETICA, 10f, Font.NORMAL))
        link.reference = vulnerability.link ?: "#"
        link.font.color = Color(0, 0, 255)  // Blue
        link.font.style = Font.UNDERLINE
        table.addCell(createCellWithLink(link))

        table.addCell(createCellWithSeverity(vulnerability.severity.label, contentFont))
        table.addCell(createCell(vulnerability.fixedInVersion ?: "-", contentFont))
    }

    /**
     * Creates a cell for the table with the given content, font, and border style.
     *
     * @param content The text content to display in the cell.
     * @param font The font to use for the cell's text.
     * @param isHeader Whether the cell is a header cell (applies special formatting); defaults false.
     * @return The created PdfPCell.
     */
    private fun createCell(content: String, font: Font, isHeader: Boolean = false): PdfPCell {
        val cell = PdfPCell(Phrase(content, font))
        cell.border = if (isHeader) Rectangle.BOX else Rectangle.NO_BORDER
        cell.setPadding(if (isHeader) 5f else 7f)
        cell.horizontalAlignment = Element.ALIGN_CENTER
        return cell
    }

    /**
     * Creates a cell for the table containing a clickable link.
     *
     * @param link The clickable link to display in the cell.
     * @return The created PdfPCell containing the link.
     */
    private fun createCellWithLink(link: Anchor): PdfPCell {
        val phrase = Phrase()
        phrase.add(link)
        val cell = PdfPCell(phrase)
        cell.border = Rectangle.NO_BORDER
        cell.setPadding(7f)
        cell.horizontalAlignment = Element.ALIGN_CENTER
        return cell
    }

    /**
     * Creates a cell for the table displaying the severity of a vulnerability, with color coding based on severity level.
     *
     * @param severity The severity level of the vulnerability (critical, high, medium or low).
     * @param font The font to use for the severity text.
     * @return The created PdfPCell with appropriate background color for the severity level.
     */
    private fun createCellWithSeverity(severity: String, font: Font): PdfPCell {
        val cell = PdfPCell(Phrase(severity, font))
        when (severity.lowercase()) {
            "critical" -> cell.backgroundColor = Color(217, 83, 79) // Red
            "high" -> cell.backgroundColor = Color(240, 173, 78) // Orange
            "medium" -> cell.backgroundColor = Color(91, 192, 222) // Yellow
            "low" -> cell.backgroundColor = Color(92, 184, 92) // Green
            else -> cell.backgroundColor = Color(119, 119, 119) // Grey
        }
        cell.setPadding(5f)
        cell.horizontalAlignment = Element.ALIGN_CENTER
        return cell
    }

}

