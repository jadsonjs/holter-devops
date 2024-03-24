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
 *
 * publicano
 * br.ufrn.caze.publicano
 * StringUtil
 * 28/03/21
 */
package br.ufrn.caze.holterci.domain.utils

import org.springframework.stereotype.Component
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Utilities classes to String manipulation
 *
 * @author jadson santos - jadson.santos@ufrn.br
 */
@Component
class StringUtil {

    // Checks if a spring is empty
    fun isEmpty(s : String) : Boolean{
        return s.isEmpty();
    }

    // Checks if a spring is not empty
    fun isNotEmpty(s : String) : Boolean{
        return ! isEmpty(s)
    }

    fun formatMetricName(s : String) : String{
        return s.replace("_", " ")
    }

    /**
     * Receive the ufrn___inms___monitoring-plan-2.0 and
     * return ufrn/inms/monitoring-plan-2.0 because spring boot API endpoints do not allow '/' caracter
     */
    fun decodeURL(s : String) : String{
        return s.replace("___", "/")
    }

    /**
     * This method extract the issues number of this format:
     * -------------------------------------------------
     * FIX: #123456 Reference values remove CI
     *
     * Signed-off-by: Jadson Santos <jadson.santos@com>
     * -------------------------------------------------
     *
     * where 123456 is the issues number
     */
    fun extractIssuesNumber(message : String) : Int{
        val regex = "#(\\d+)"

        val pattern: Pattern = Pattern.compile(regex)
        val matcher: Matcher = pattern.matcher(message)

        if (matcher.find()) {
            val extractedNumber: Int = (matcher.group(1)).toInt()
            return extractedNumber
        }

        return -1 // not find
    }
}