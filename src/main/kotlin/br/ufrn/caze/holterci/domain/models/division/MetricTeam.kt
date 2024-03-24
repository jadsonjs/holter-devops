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
 */
package br.ufrn.caze.holterci.domain.models.division

enum class MetricTeam (val value: Int, var description: String, var highlightColor: String){

    NEUTRAL(1, "", "#EEEEEE"),
    DEV(1, "Development", "LightSeaGreen"),
    OPS(2, "Operation", "LimeGreen"),

    ;

    // Kotlin allows us to create objects that are common to all instances of a class
    companion object {
        fun getByValue(value: Int): MetricCategory {

            var categories: Array<MetricCategory> = MetricCategory.values()
            for (c in categories) {
                if( c.value == value) {
                    return c
                }
            }
            throw IllegalArgumentException("Invalid Category Value")
        }

        fun getByName(name : String): MetricCategory {

            var categories: Array<MetricCategory> = MetricCategory.values()
            for (c in categories) {
                if (c.name == name) {
                    return c
                }
            }
            throw IllegalArgumentException("Invalid Category Name")
        }
    }
}