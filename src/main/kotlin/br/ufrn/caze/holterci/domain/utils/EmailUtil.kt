package br.ufrn.caze.holterci.domain.utils

import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import jakarta.mail.util.ByteArrayDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Class to send e-mail of alert
 */
@Component
class EmailUtil {

    @Autowired
    private val mailSender: JavaMailSender? = null


    ///////////////////////// DevOps Metrics Email  //////////////////////////////

    /**
     * Send metrics email
     */
    fun sendDevOpsMetrcsAlertMail(project: Project, period: Period, underReferenceMetrics: List<Triple<Metric, BigDecimal, BigDecimal>>, emailList: List<String>, appAddress: String): String {
        val subject = "[Holter DevOps] Monitoring Report for Project: ${project.name}"
        val content = if (underReferenceMetrics.isEmpty()) {
            getCongratulationsDevOpsMetricsText(project, period, appAddress)
        } else {
            getAlertDevOpsMetricsText(project, period, underReferenceMetrics, appAddress)
        }
        return prepareAndSendMail(emailList, subject, content)
    }



    private fun getCongratulationsDevOpsMetricsText(project: Project, period: Period, appAdress: String): String {

        val successfulMessage = "All practices for your project are within appropriate values.";

        return """
        <table style="border-collapse: collapse; width: 100%;">
            <caption style="background-color: green; padding: 10px; text-align: center; color: white;">
                <strong>Congratulations</strong>
            </caption>
            $successfulMessage
        </table>
         ${getDevOpsMonitoringPeriodInfoText(project, period)}    
         ${generateFooterInfoText(appAdress)}
        """.trimIndent()

    }

    private fun getAlertDevOpsMetricsText(project: Project, period: Period, underReferenceMetrics: List<Triple<Metric, BigDecimal, BigDecimal>>, appAdress: String): String {

        val itemRows = underReferenceMetrics.joinToString("\n") { item ->
            """
            <tr>
                <td style="border: 1px solid black; padding: 10px;"> ${item.first.metricStage.description} practice <strong> ''${item.first.denomination}''</strong> is not with the appropriate value for your project. Current value: <strong>${item.second}</strong>. Reference value: <strong>${item.third}</strong> </td>
            </tr>
        """.trimIndent()
        }

        return """
        <table style="border-collapse: collapse; width: 100%;">
            <caption style="background-color: red; padding: 10px; text-align: center; color: white;">
                <strong>Alert</strong>
            </caption>
            $itemRows
        </table>
         ${getDevOpsMonitoringPeriodInfoText(project, period)}    
         ${generateFooterInfoText(appAdress)}
        """.trimIndent()

    }




    private fun getDevOpsMonitoringPeriodInfoText(project: Project, period: Period): String {
        val periodText = """
             <br><br>    
                <div style="border: 2px solid #000; padding: 10px; text-align: justify; width: 100%; background-color: #f0f0f0;">
                    New monitoring has just been run for the ${project.name} project in the period 
                    ${DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(period.init.with(LocalTime.MIN))} 
                    to 
                    ${DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(period.end.with(LocalTime.MAX))}
                </div>
            <br>
        """.trimIndent()

        return periodText;
    }



    ///////////////////////// Vulnerabilities Report  Email  //////////////////////////////


    /**
     * Send security mail.
     * The mail contains a vulnerability report attached (pdf)
     */
    fun sendVulnerabilityAlertMail(projects: List<Project>, vulnerabilityReportFile: ByteArray, emailList: List<String>, hasNoVulnerabilities: Boolean, appAdress: String): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("MMMM/yyyy") // "MMMM" gives full month name
        val formattedDate = currentDate.format(formatter);
        val subject = "[Holter DevOps] 🚨 Vulnerability Report ("+formattedDate+") "

        val content: String

        if (hasNoVulnerabilities) {
            content = getNoVulnerabilityCongratulationsText(projects, appAdress)
        }else {
            content = getVulnerabilityAlertText(projects, appAdress)
        }

        println(content)

        val attachmentDataSource = ByteArrayDataSource(vulnerabilityReportFile, "application/pdf")
        val attachmentName = "Vulnerability_Report_$formattedDate.pdf"

        return prepareAndSendMail(emailList, subject, content, attachmentName, attachmentDataSource)

    }





    private fun getNoVulnerabilityCongratulationsText(projects: List<Project>, appAdress: String) =
        generateVulnerabilityEmailTemplate(
            title = "Congratulations",
            titleColor = "green",
            message = "No vulnerabilities were found in your projects.",
            projectScanInfo = getProjectsScanInfoText(projects),
            footerInfo = generateFooterInfoText(appAdress)
        )




    private fun getVulnerabilityAlertText(projects: List<Project>, appAdress: String) =
        generateVulnerabilityEmailTemplate(
            title = "Vulnerability Alert",
            titleColor = "red",
            message = "Your projects have vulnerabilities. Please check the attached vulnerability report for more details.",
            projectScanInfo = getProjectsScanInfoText(projects),
            footerInfo = generateFooterInfoText(appAdress)
        )


    private fun generateVulnerabilityEmailTemplate(title: String, titleColor: String, message: String, projectScanInfo: String, footerInfo: String): String {
        return """
        <table style="border-collapse: collapse; width: 60%; margin: auto;">
            <caption style="background-color: $titleColor; padding: 10px; text-align: center; color: white;">
                <strong>$title</strong>
            </caption>
            <tr>
                <td style="border: 1px solid black; padding: 10px;"> $message </td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 10px;"> $projectScanInfo </td>
            </tr>
        </table>
        $footerInfo
    """.trimIndent()
    }


    private fun getProjectsScanInfoText(projects: List<Project>): String {

        val projectList = projects.joinToString("") { "<li style='text-align: left;'> <b>${it.name}</b></li>" }

        return " <p>New scan has just been run for the projects: </p>" +
                " <br>"+
                "<ul>"+
                    projectList+
                "</ul>"
    }











    ///////////////////////////////////////////////////////////////


    private fun generateFooterInfoText(appAddress: String): String {
        return """
            <br>   
            <br>
            <div style="text-align: center; width: 100%; font-weight: bold;">
                <a href="$appAddress" target="_blank"> Access Holter DevOps App: $appAddress </a>
            </div>
        """.trimIndent()
    }

    /**
     * Send email by spring mail API
     */
    fun prepareAndSendMail(emailList: List<String>, subject: String, content: String, attachmentName: String? = null, attachmentDataSource: ByteArrayDataSource? = null): String {
        return try {
            val mail = mailSender!!.createMimeMessage()
            val helper = MimeMessageHelper(mail, attachmentDataSource != null)

            emailList.forEach { email -> helper.addBcc(email) }
            helper.setSubject(subject)
            helper.setText(content, true)

            if (attachmentName != null && attachmentDataSource != null) {
                helper.addAttachment(attachmentName, attachmentDataSource)
            }

            mailSender.send(mail)
            "Email sent with success"
        } catch (e: Exception) {
            e.printStackTrace()
            "Error to send email"
        }
    }

    /**
     * checks if the email is valid
     */
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"

        val pattern = Regex(emailRegex)
        return pattern.matches(email)
    }

}