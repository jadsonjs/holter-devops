/*
 * Federal University of Rio Grande do Norte
 * Department of Informatics and Applied Mathematics
 * Collaborative & Automated Software Engineering (CASE) Research Group
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package br.ufrn.caze.holterci.controllers.log

import br.com.jadson.csvdataset.CSVDataSet
import br.ufrn.caze.holterci.controllers.AbstractRestController
import br.ufrn.caze.holterci.domain.models.log.AccessLog
import br.ufrn.caze.holterci.domain.ports.repositories.crud.ProjectRepository
import br.ufrn.caze.holterci.domain.utils.StringUtil
import br.ufrn.caze.holterci.repositories.log.AccessLogJPARepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.FileInputStream
import java.nio.file.Files
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/api/log-dashboard")
class LogDashBoardAccessRestController (
    private val projectRepository : ProjectRepository,
    private val accessLogJPARepository : AccessLogJPARepository,
    private val stringUtil: StringUtil,
) : AbstractRestController() {


    @Value("\${log.dashboard-access}")
    val logDashBoardAccess : Boolean = false

    @PostMapping(value = ["/access/{organization}/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun logAccess(
                    @PathVariable organization: String,
                    @PathVariable name: String,
                    @RequestParam(name = "init") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) init : LocalDate,
                    @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) end : LocalDate,
                    request: HttpServletRequest
    ): ResponseEntity<String> {

        var user = getLoggedInUser(request)

        if(logDashBoardAccess && user != null) {

            var p = projectRepository.findProjectIDByOrganizationAndName(stringUtil.decodeURL(organization), name)

            var accessLog = AccessLog(user, LocalDateTime.now(), init, end, p)
            accessLogJPARepository.save(accessLog)
        }


        return ResponseEntity("OK", HttpStatus.OK)
    }


    /**
     * Return access to dashboard data
     */
    @GetMapping(value = ["/access/{organization}/{name}"], produces = ["text/csv"])
    fun get(@PathVariable(value = "organization") organization: String, @PathVariable(value = "name") name: String) : ResponseEntity<ByteArray>?{

        var csvBytes = byteArrayOf()

        if(logDashBoardAccess) {

            var p = projectRepository.findProjectIDByOrganizationAndName(stringUtil.decodeURL(organization), name)

            var accessLogsList : List<AccessLog> = accessLogJPARepository.findAllByProject(p.id)

            val tempFile = Files.createTempFile("access", ".csv").toFile()

            val dataSet = CSVDataSet(tempFile.absolutePath, false, false)
            dataSet.addRow(listOf("id", "user", "permission", "dataTime", "init", "end", "project"))


            for (a in accessLogsList){
                dataSet.addRow(
                    Arrays.asList(
                        *arrayOf(
                            "" + a.id,
                            "" + (a.user?.email ?: "email" ),
                            "" + (a.user?.permission?.joinToString(", ") { it.role.name }),
                            "" + a.dateTime,
                            "" + a.init,
                            "" + a.end,
                            "" + (a.project.organization+"/"+a.project.name),
                        )
                    )
                )
            }

            dataSet.storeData()

            val fis = FileInputStream(tempFile)
            csvBytes = fis.readBytes()
            fis.close()
        }




        val headers = HttpHeaders()
        headers.setContentType(MediaType.parseMediaType("text/csv"))
        headers.setContentDispositionFormData("attachment", "dashboard-access.csv")

        return ResponseEntity.ok()
            .headers(headers)
            .body(csvBytes)
    }

}