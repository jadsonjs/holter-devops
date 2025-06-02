/*
 * Universidade Federal do Rio Grande do Norte
 * Instituto Metrópole Digital
 * Diretoria de Tecnologia da Informação
 *
 * holter-ci
 * NetUtilTest
 * @since 19/12/2023
 */
package br.ufrn.caze.holterci.domain.utils

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


/**
 *
 * @author Jadson Santos - jadson.santos@ufrn.br
 */
class NetUtilTest {


    val contextPath = "/holter"


    /////////////////// pages ///////////////////
    @Test
    fun isHomePublicPage() {
        assertTrue(NetUtil().isPublicPage("/holter/", contextPath))
    }

    @Test
    fun isLoginPublicPage() {
        assertTrue(NetUtil().isPublicPage("/holter/login", contextPath))
    }

    @Test
    fun isIndexPagePublicPage() {
        assertTrue(NetUtil().isPublicPage("/holter/index.html", contextPath))
    }

    @Test
    fun isLoginPagePublicPage() {
        assertTrue(NetUtil().isPublicPage("/holter/login.html", contextPath))
    }

    @Test
    fun isInternalPublicPage() {
        assertFalse(NetUtil().isPublicPage("/holter/metrics/list", contextPath))
    }


    /////////////////// resources  ///////////////////
    @Test
    fun isJSPublicResource() {
        assertTrue(NetUtil().isPublicPage("/holter/js/chunk-vendors.js", contextPath))
    }

    @Test
    fun isFontPublicResource() {
        assertTrue(NetUtil().isPublicPage("/holter/fonts/fa-solid-900.8e1ed89b.woff2", contextPath))
    }

    @Test
    fun isH2ConsolePublicResource() {
        assertTrue(NetUtil().isPublicPage("/holter/h2-console/20slf230303", contextPath))
    }

    /////////////////// api ///////////////////
    @Test
    fun isLoginApiPublicPage() {
        assertTrue(NetUtil().isPublicPage("/holter/api/login/all", contextPath))
    }

    @Test
    fun isInternalApiPublicPage() {
        assertFalse(NetUtil().isPublicPage("/holter/api/metrics/all", contextPath))
    }
}
