package br.ufrn.caze.holterci.application.security

import br.ufrn.caze.holterci.domain.exceptions.TooManyRequestsException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.util.concurrent.TimeUnit

/**
 * intercept the execution of requests and responses in your application.
 * It provides a way to perform operations before a request is handled by
 * a controller and after the request has been handled
 */
@Component
class RateLimitingInterceptor :  HandlerInterceptor {

    // use this class to controle the rate limit
    // 240 request per minute
    private val rateLimiter = TokenBucketRateLimiter(240, 1, TimeUnit.MINUTES)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (rateLimiter.tryAcquire()) {
            return true // Continue with the request processing
        } else {
            // catch by AbstractRestController
            throw TooManyRequestsException("Too many requests. Please try again in few minutes.");
        }
    }
}