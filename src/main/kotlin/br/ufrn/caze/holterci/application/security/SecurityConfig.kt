package br.ufrn.caze.holterci.application.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SecurityConfig {

    /**
     * Spring Security provides the BCryptPasswordEncoder,
     * which is a good choice for password hashing.
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    /**
     * Ignore spring security, we are not using it
     */
    @Bean
    open fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            securityMatcher("/holter/api/**")
            authorizeRequests {
                authorize(anyRequest, permitAll)
            }
            csrf { disable() }
        }
        return http.build()
    }

}