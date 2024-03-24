package br.ufrn.caze.holterci.domain.utils

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component


@Component
class NetUtil {

    fun getClientIpAddress(request: HttpServletRequest): String {
        // Retrieve the client's IP address from the request headers
        val xForwardedForHeader = request.getHeader("X-Forwarded-For")
        return if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty()) {
            // If the request went through proxy servers, the original IP is in the X-Forwarded-For header
            xForwardedForHeader.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].trim { it <= ' ' }
        } else request.remoteAddr
    }

    fun getClientUserAgent(request: HttpServletRequest): String {
        // Get the User-Agent header from the request
        return if (request.getHeader("User-Agent") != null ) request.getHeader("User-Agent") else ""
    }

    fun isPublicPage(uri : String, contextPath : String?): Boolean {
        return if (
                // pages
                ( uri.equals(contextPath) || uri.equals(contextPath+"/") )
                || ( uri.equals(contextPath+"/login") || uri.equals(contextPath+"/login/") )
                || ( uri.equals(contextPath+"/index.html") || uri.equals(contextPath+"/login.html") )
                // resources
                || ( uri.startsWith(contextPath+"/h2-console") )
                || ( uri.startsWith(contextPath+"/fonts") )
                || ( uri.startsWith(contextPath+"/js")  )
                || ( uri.startsWith(contextPath+"/img") )
                || ( uri.equals(contextPath+"/favicon.ico") )
                // api
                || ( uri.startsWith(contextPath+"/api"+"/parameter") )
                || ( uri.startsWith(contextPath+"/api"+"/login") )
            ) {
            return true
        } else false
    }
}