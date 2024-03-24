package br.ufrn.caze.holterci.application.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.net.InetAddress

/**
 * This class not work when is running inside a docker
 */
@Configuration
class WebContextConfig {

    @Bean
    fun webAppInfo(environment: Environment): WebAppInfo {
        return WebAppInfo( environment)
    }

    class WebAppInfo(private val environment: Environment) {

        fun getWebAppAddress(): String {
            val protocol    = try { getProtocol()  }   catch (ex: Exception) { "http" }
            val host        = try { getHost()      }   catch (ex: Exception) { "localhost" }
            val port        = try { getPort()      }   catch (ex: Exception) { "8080" }
            val contextPath = try { getContext()   }   catch (ex: Exception) { "/holter" }


            return String.format("%s://%s:%s%s", protocol, host, port, contextPath)
        }

        private fun getProtocol(): String {
            // Use the server.ssl.enabled property to determine whether HTTPS is enabled
            return if (environment.getProperty("server.ssl.enabled", Boolean::class.java, false)) "https" else "http"
        }

        private fun getPort(): String {
            // Use the server.port property to determine the port
            return if ( environment.getProperty("server.port") != null ) environment.getProperty("server.port")!! else "8080"
        }

        private fun getContext(): String {
            // Use the server.servlet.context-path property to determine the context
            return environment.getProperty("server.servlet.context-path")!!
        }

        private fun getHost() : String{
            return if( InetAddress.getLocalHost().canonicalHostName.contains(".") ) InetAddress.getLocalHost().canonicalHostName else InetAddress.getLocalHost().hostAddress
        }
    }
}