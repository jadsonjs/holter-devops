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
 * br.ufrn.caze.publicano.domain.utils
 * MathUtil2
 * 04/07/21
 */
package br.ufrn.caze.holterci.domain.utils;

import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*


/**
 * Math and Statistical Functions.
 *
 * Jadson Santos - jadsonjs@gmail.com
 */
@Component
class MathUtil {

    val SCALE = 2

    /**
     * Divide 2 BigDecimal with scale
     */
    fun divide(dividend: BigDecimal, divisor: BigDecimal): BigDecimal {
        // precondition
        if (divisor.equals(BigDecimal.ZERO)) {
            return dividend
        }
        return dividend.divide(divisor, SCALE, RoundingMode.HALF_UP)
    }

    /**
     * Calc the sum of a list of values.
     * @param values
     * @return
     */
    fun sumOfValues(values: List<BigDecimal?>): BigDecimal {
        var total = BigDecimal.ZERO
        for (count in values.indices) {
            total = total.add(values[count])
        }
        return total
    }

    /**
     * Calculate the average of values of specific record
     *
     * @return
     */
    fun meanOfValues(values: List<BigDecimal>): BigDecimal {

        // precondition
        if (values.size == 0) {
            return BigDecimal.ZERO
        }
        val sum = sumOfValues(values)
        return sum.divide(BigDecimal(values.size), SCALE, RoundingMode.HALF_UP)
    }

    /**
     * Median just for specific values
     * @param values
     * @return
     */
    fun medianOfValues(values: List<BigDecimal>): BigDecimal {

        // precondition
        if (values.size == 0) {
            return BigDecimal.ZERO
        }
        val tempList: MutableList<BigDecimal> = LinkedList()
        for (count in values.indices) {
            tempList.add(values[count])
        }
        Collections.sort(tempList)
        var median : BigDecimal
        // if old, it the the average of the 2 in the middle
        median = if (tempList.size % 2 == 0) tempList[tempList.size / 2]
            .add(tempList[tempList.size / 2 - 1]).divide(BigDecimal(2), SCALE, RoundingMode.HALF_UP) else {
            // If even, it is the middle element
            tempList[tempList.size / 2]
        }
        return median
    }

    ////////////////////////////////////////////////////////



    fun medianOfLongValues(times: List<Long>): BigDecimal {
        val metricList: MutableList<BigDecimal> = ArrayList()
        for (time in times) {
            metricList.add(BigDecimal(time))
        }
        return medianOfValues(metricList)
    }


    fun meanOfLongValues(times: List<Long>): BigDecimal {
        // convert to list of BigDecimal
        val metricList: MutableList<BigDecimal> = ArrayList()
        for (time in times) {
            metricList.add(BigDecimal(time))
        }
        return meanOfValues(metricList)
    }


    /**
     *
     * Normalize using Min-Max:
     *
     *
     * (X - Xmin) / (Xmax - Xmin)
     */
    fun normalize(values: List<BigDecimal>): MutableList<BigDecimal> {

        val normalizedValues: MutableList<BigDecimal> = ArrayList()

        var Xmin: BigDecimal? = null
        var Xmax: BigDecimal? = null
        for (value in values) {
            val X = value
            if (Xmin == null || X.compareTo(Xmin) <= 0) Xmin = X
            if (Xmax == null || X.compareTo(Xmax) >= 0) Xmax = X
        }

        for (value in values) {
            if (Xmin!!.compareTo(Xmax) != 0) {
                val X = value
                normalizedValues.add( (X.subtract(Xmin)).divide( Xmax!!.subtract(Xmin ), 5, RoundingMode.HALF_UP))
            } else {
                normalizedValues.add(BigDecimal(1.00000))
            }
        }
        return normalizedValues
    }


    fun scale(values: List<BigDecimal>, height: BigDecimal): MutableList<BigDecimal> {
        val scaleedValues: MutableList<BigDecimal> = ArrayList()
        for (value in values) {
            scaleedValues.add(value.multiply(height))
        }
        return scaleedValues
    }


}