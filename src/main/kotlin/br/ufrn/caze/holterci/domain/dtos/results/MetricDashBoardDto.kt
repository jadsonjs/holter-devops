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

import br.ufrn.caze.holterci.domain.dtos.crud.MetricDto
import java.math.BigDecimal

/**
 * This class represent a visualization of a metric in the final dashborad
 */
data class MetricDashBoardDto(

    /**
     * Information about the metric, name description, category, etc.
     */
    var metric : MetricDto,
    /**
     * The value of the metric calculated
     */
    var lastValue : BigDecimal = BigDecimal.ZERO,
    /**
     * The reference value (ideal value for the metric)
     */
    var valueReference : BigDecimal = BigDecimal.ZERO,

    /**
     * if the value of the metric is good, tolerable, satisfactory compared with reference value
     */
    var overReferenceValue: Boolean,

    /**
     * if the metric was collected
     * when the metric was not collected, we return all values with 0, [0, 0, 0, 0 ,0 ]
     * but it can be collected, and have all values zero also. so this boolean shows the diferences
     */
    var wasCollected: Boolean,

    /**
     * if the trend of metric is go up or go down
     */
    var trend: String,

    )
