package br.ufrn.caze.holterci.domain.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StringUtilTest {

    @Test
    fun extractIssuesNumber() {
        assertEquals(123456, StringUtil().extractIssuesNumber("FIX: #123456 Reference values remove CI\n" +
                "     *\n" +
                "     * Signed-off-by: Jadson Santos <jadson.santos@com>"))

    }

    @Test
    fun extractIssuesNumberNotFound() {
        assertEquals(-1, StringUtil().extractIssuesNumber("FIX: Reference values remove CI\n" +
                "     *\n" +
                "     * Signed-off-by: Jadson Santos <jadson.santos@com>"))

    }

    @Test
    fun extractIssuesNumberComplex() {
        assertEquals(12345, StringUtil().extractIssuesNumber("Issue #12345. Reference values remove CI\n" +
                "     *\n" +
                "     * Signed-off-by: Jadson Santos <jadson.santos@com>"))

    }

    @Test
    fun decodeURLTest(){
        assertEquals("ufrn/inms/monitoring-plan-2.0", StringUtil().decodeURL("ufrn___inms___monitoring-plan-2.0") )
    }

    @Test
    fun decodeURLTest2(){
        assertEquals("nome_composto", StringUtil().decodeURL("nome_composto") )
    }
}