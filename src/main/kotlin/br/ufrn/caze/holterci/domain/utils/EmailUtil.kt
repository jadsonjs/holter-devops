package br.ufrn.caze.holterci.domain.utils

import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalTime
import java.time.format.DateTimeFormatter


/**
 * Class to send e-mail of alert
 */
@Component
class EmailUtil {

    @Autowired
    private val mailSender: JavaMailSender? = null

    /**
     * Send email by spring mail API
     */
    fun sendMail(project: Project, period: Period, underReferenceMetrics: List<Triple<Metric, BigDecimal, BigDecimal>>, emailList: List<String>, appAdress: String): String {
        return try {
            val mail = mailSender!!.createMimeMessage()
            val helper = MimeMessageHelper(mail)
            for( email in emailList ) {
                helper.addBcc(email)
            }
            helper.setSubject("[Holter DevOps] Monitoring Report for Project: "+project.name)

            if(underReferenceMetrics.size == 0) {
                helper.setText(getCongratulationsText(project, period, appAdress), true)
            }else{
                helper.setText(getAlertText(project, period, underReferenceMetrics, appAdress), true)
            }
            mailSender.send(mail)
            "Email sent with success"
        } catch (e: Exception) {
            e.printStackTrace()
            "Error to send email"
        }
    }

    private fun getCongratulationsText(project: Project, period: Period, appAdress: String): String {
        return """
            <table style="border-collapse: collapse; width: 100%;">
                <caption style="background-color: green; padding: 10px; text-align: center; color: white;">
                    <strong>Congratulations</strong>
                </caption>
                <tr>
                    <td style="border: 1px solid black; padding: 10px;"> All practices for your project are with appropriate values </td>
                </tr>
            </table>
            ${getInfoText(project, period, appAdress)}
        """.trimIndent()
    }

    private fun getAlertText(project: Project, period: Period, underReferenceMetrics: List<Triple<Metric, BigDecimal, BigDecimal>>, appAdress: String): String {

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
         ${getInfoText(project, period, appAdress)}    
        """.trimIndent()

    }

    private fun getInfoText(project: Project, period: Period, appAdress: String): String {
        return """
        <br><br>    
        <div style="border: 2px solid #000; padding: 10px; text-align: justify; width: 100%; background-color: #f0f0f0;">
            New monitoring has just been run for the ${project.name} project in the period 
            ${DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(period.init.with(LocalTime.MIN))} 
            to 
            ${DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(period.end.with(LocalTime.MAX))}
        </div>
        <br>
        <div style="text-align: center; width: 100%; font-weight: bold;">
            <a href="${appAdress}" target="_blank"> Access Holter DevOps App: ${appAdress} </a>
        </div>
    """.trimIndent()
    }

    ///////////////////////////////////////////////////////////////

    /**
     * checks if the email is valid
     */
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"

        val pattern = Regex(emailRegex)
        return pattern.matches(email)
    }

}