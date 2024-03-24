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

import java.math.BigDecimal

/**
 * Generated information about the trend of CI/CD subpractice, if UP or DOWN
 *
 */
data class TrendDto(
    var percentage : BigDecimal,     // percentage of increase or decrease of the metric
    var trend: String,               // if the trend of metric is UP or DOWN
    var good: Boolean,               // if the trand is good or bad.  for example if percentage is + 10% for converage is "good", but for build duration is bad. - 10% percentage for build duration is considered a good "value"
    var currentValue: BigDecimal,    // the last metric value collect
    var meanValue: BigDecimal,       // The mean value of the whole evolution
    var stability: String,           // the level os instability of last values (Figure)
    var lastValues: List<BigDecimal> // last 5 values
)