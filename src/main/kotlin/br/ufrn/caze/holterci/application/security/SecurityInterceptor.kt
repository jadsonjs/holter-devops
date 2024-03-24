package br.ufrn.caze.holterci.application.security

import br.ufrn.caze.holterci.domain.dtos.user.UserDto
import br.ufrn.caze.holterci.domain.utils.NetUtil
import br.ufrn.caze.holterci.domain.utils.StringUtil
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.io.IOException

/**
 * Check security of application
 * ONLY if the application uses login
 */
@Component
class SecurityInterceptor (
    private val jwtManager: JwtManager,
    private val stringUtil : StringUtil,
    private val netUtil: NetUtil,
) : HandlerInterceptor {


    val USER_HEADER_LABEL = "logged-in-user"

    @Value("\${enable.login-page}")
    val enableLoginPage : Boolean = false


    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

        // check token only if the app use login
        if(enableLoginPage && ! isOptionsRequest(request) && ! isPublicPage(request) ) {

            val user: UserDto? = extractUser(request)

            if(user != null) {
                val jwtToken: String = user.jwtToken

                if ( ! jwtManager.isValid(jwtToken, user.email, netUtil.getClientIpAddress(request), netUtil.getClientUserAgent(request)) ) {
                    System.err.println("Access expired invalid JWT Token")
                    return unauthorized(response, "Access expired")
                }

            }else{
                System.err.println("Access expired no loggedInUser passed ")
                return unauthorized(response, "Access expired")
            }
        }

        return true // allow
    }


    @Throws(JsonProcessingException::class)
    fun extractUser(request: HttpServletRequest) : UserDto? {
        val mapper = ObjectMapper()
            .registerModule(ParameterNamesModule())
            .registerModule(Jdk8Module())
            .registerModule(JavaTimeModule())
        try {
            val headerUser = request.getHeader(USER_HEADER_LABEL)
            return if (stringUtil.isNotEmpty(headerUser)) {
                mapper.readValue(headerUser, UserDto::class.java)
            } else {
                null
            }
        }catch (ex : Exception ){
            return null
        }
    }

    fun unauthorized(response: HttpServletResponse, msg: String?): Boolean {
        try {
            val mapper = ObjectMapper()

            response.setHeader("Access-Control-Allow-Origin", "*")
            response.setHeader("Access-Control-Allow-Headers", "Content-Type")
            response.setHeader("Access-Control-Allow-Headers", "Content-Length")
            response.setHeader("Access-Control-Allow-Headers", "Date")

            response.contentType = "application/json"
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write(mapper.writeValueAsString(msg))
        } catch (e: IOException) {
            response.status = HttpStatus.UNAUTHORIZED.value()
        }
        return false
    }

    fun isOptionsRequest(request: HttpServletRequest): Boolean {
        return request.method == "OPTIONS"
    }

    @Value("\${server.servlet.context-path}")
    var contextPath: String? = null

    fun isPublicPage(request: HttpServletRequest): Boolean {
       return netUtil.isPublicPage(request.requestURI, contextPath)
    }

}