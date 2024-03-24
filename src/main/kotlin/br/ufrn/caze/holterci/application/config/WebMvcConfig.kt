package br.ufrn.caze.holterci.application.config

import br.ufrn.caze.holterci.application.security.RateLimitingInterceptor
import br.ufrn.caze.holterci.application.security.SecurityInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * class to register the interceptor.
 */
@Configuration
class WebMvcConfig (
    private val rateLimitingInterceptor: RateLimitingInterceptor,
    private val securityInterceptor: SecurityInterceptor)
    : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(rateLimitingInterceptor)
        registry.addInterceptor(securityInterceptor)
    }
}