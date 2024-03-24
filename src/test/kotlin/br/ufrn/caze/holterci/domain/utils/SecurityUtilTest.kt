package br.ufrn.caze.holterci.domain.utils

import org.junit.jupiter.api.Test
import java.util.*

class SecurityUtilTest {

    /**
     * Generate the encode token
     */
    @Test
    fun testEncode() {
        println(Base64.getEncoder().encodeToString(("put your token here" + ":").toByteArray())  )
    }
}