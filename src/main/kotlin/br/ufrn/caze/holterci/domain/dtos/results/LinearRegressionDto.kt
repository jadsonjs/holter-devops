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
package br.ufrn.caze.holterci.domain.dtos.results

data class LinearRegressionDto
    (

    var id: String? = null,

    /**
     * related to:
     * Bugs, tec debt or Closed PRs*/
    var attributeName : String,

    /** CI metric: Commit Activity, Time to Fix, Build Health, etc..*/
    var metricName : String,

    var slope : Double,
    var slopeConfidenceInterval : Double,

    /** Coefficients  */
    var intercept : Double,

    var meanSquareError : Double,

    /** THE VALUE OF CORRELATION*/
    var rSquared : Double,
    var adjustedRSquared : Double,

    /** the t-statistic and the associated p-value, which defines the statistical significance of the beta coefficients */
    var significance : Double,

    /** if the correlation is in right direction,
     *
     * if the metric is direct, the bigger, the better, we should have a positive slope
     * if the metric is inverse, the smaller, the better, we should have a negative slope
     */
    var rightDirection: Boolean,

    /** If this is relevant, over the threshold (50%) and  significance < 0.05   */
    var relevant: Boolean? = null,

    //////////////// Correlation chat  ///////////////////////
    /** data for the chart */
    var chartData: String? = null,

    /** data for the chart */
    var chartOptions: String? = null

    //////////////////////////////////////////////////////////

   )