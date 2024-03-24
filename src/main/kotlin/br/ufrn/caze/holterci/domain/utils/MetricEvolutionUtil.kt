package br.ufrn.caze.holterci.domain.utils

import br.ufrn.caze.holterci.domain.dtos.results.TrendDto
import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricStability
import br.ufrn.caze.holterci.domain.models.metric.MetricTrend
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class MetricEvolutionUtil (
    var mathUtil: MathUtil
){


    /**
     * Check if the metric is over the reference value.
     */
    fun checkOverReferenceValue(metric : Metric, currentValue : BigDecimal, referenceValue : BigDecimal) : Boolean {

        if(BigDecimal.ZERO.equals(referenceValue))
            return true

        if(metric.direct && currentValue.compareTo(referenceValue) >= 0)
            return true
        if( ! metric.direct && currentValue.compareTo(referenceValue) <= 0)
            return true
        return false
    }

    /**
     *
     * https://circleci.com/docs/insights-glossary/
     *
     * Trends
     * Raw metric values are often difficult to interpret on their own. Trends provide additional context to Insights metrics by presenting a relative benchmark against previous performance.
     * For instance, on the last 7-day view, trends will display the change in value or delta compared to the prior 7-day window.
     *
     * Trends displayed in the CircleCI UI are calculated as 100 * (current value - previous value) / prior-value.
     *
     * Trends received from the CircleCI API
     *  are calculated as a ratio instead of a percentage with the following formula: (current-value / prior-value). These trends are 1-based and not 0-based.
     *
     * A ratio of 1.0 indicates no change.
     * A value less than 1.0 indicates a negative trend, and a value greater than 1.0 indicates a positive trend.
     * A value of -1.0 is an infinite trend.
     * This also applies to the following API endpoints:
     *
     * getOrgSummaryData
     * getWorkflowSummary
     * .
     * Despite the trend being reported as a ratio via the API, the result returned is still effectively equivalent to the percentage that is shown in the UI.
     * To compare the ratio from the API with the percentage reported in the UI, you can compute | trend-value - 1| (vertical line indicates absolute value).
     * For example, if the API returns a ratio of 3.33, in the UI it will be shown as | 3.33 - 1 | = 2.33 which is equivalent to +233%.
     *
     * Trends are available only for 24-hour, 7-day, and 30-day time windows.
     */
    fun calcMetricTrend(metric : Metric, allValues : MutableList<BigDecimal>) : TrendDto {

        if(allValues.size == 0){
            return TrendDto(BigDecimal.ZERO, "", true, BigDecimal.ZERO, BigDecimal.ZERO, "", ArrayList())
        }

        var (trendPercentage, trendLabel) = calcTrend(allValues)

        var stability: MetricStability = calcValuesStability(allValues)

        var lastValues = getLastValues(allValues)

        val good = if (metric.direct && trendPercentage.compareTo(BigDecimal.ZERO) >= 0 ) true else ( if (! metric.direct && trendPercentage.compareTo(BigDecimal.ZERO) <= 0 ) true else false )

        return TrendDto(trendPercentage, trendLabel.name, good, allValues.get(allValues.size-1), mathUtil.meanOfValues(allValues), stability.name, lastValues)
    }


    /**
     * calc If the values trend to be up or down.
     * https://circleci.com/docs/insights-glossary/
     * Trends displayed in the CircleCI UI are calculated as 100 * (current value - previous value) / prior-value.
     *
     * Ex.: Coverage
     *
     * 100 * (90  - 85) / 60 = + 8.3%  UP
     * 100 * (80 - 90) / 60 = - 16.66%  DOWN
     */
    fun calcTrend(values: MutableList<BigDecimal>): Pair<BigDecimal, MetricTrend> {

        if(values.size == 0)
            return Pair(BigDecimal.ZERO, MetricTrend.NEUTRAL)

        var previous : BigDecimal = BigDecimal.ZERO
        var prior : BigDecimal = BigDecimal(Long.MAX_VALUE)
        var current : BigDecimal = values.get(values.size-1)

        // get previous value not zero
        if(values.size > 1) {
            outerLoop@ for (i in values.size - 2 downTo 0) {
                previous = values.get(i)
                if( previous.compareTo(BigDecimal.ZERO) != 0 )
                    break@outerLoop
            }
        }


        // get the prior value not ZERO
        for ( v in values){
            if(v.compareTo(prior) < 0 && v.compareTo(BigDecimal.ZERO) > 0)
                prior = v
        }

        if(prior.stripTrailingZeros().equals(BigDecimal.ZERO) || prior.stripTrailingZeros().equals(BigDecimal(Long.MAX_VALUE))  )
            return Pair(BigDecimal.ZERO, MetricTrend.NEUTRAL)

        val percentage = BigDecimal(100).multiply (        (   current.minus(previous)  ).divide(prior,4, RoundingMode.UP)   ).setScale(2)
        return Pair(
            percentage,
            if( percentage.compareTo(BigDecimal.ZERO) >= 0) MetricTrend.UP else MetricTrend.DOWN
        )
    }


    /**
     * calc the number of changes in data history
     * Compare MAD (Mean Absolute Deviation) values with quartiles of your data to classify the MAD value as high, medium, or low.
     * This approach can provide a contextually relevant way to understand the variability of your data.
     */
    fun calcValuesStability(allValues : MutableList<BigDecimal>): MetricStability {

        //  **** A lower MAD suggests higher stability ***
        var mad : BigDecimal = calculateMAD(allValues)

        val quartiles = calculateQuartiles(allValues)

        val q1 = quartiles[0]
        val q3 = quartiles[2]

        val classification =
            when {
                mad.compareTo(q1) < 0                            -> MetricStability.SUNNY    // low variation
                mad.compareTo(q1) >= 0 && mad.compareTo(q3) <= 0 -> MetricStability.CLOUDY   // medium variation
                else                                             -> MetricStability.RAINING  // high variation
            }

       return classification
    }


    /**
     * Mean Absolute Deviation (MAD): Calculate the mean absolute deviation of the sequence.
     * MAD measures the average absolute difference between each value and the mean of the sequence.
     * **** A lower MAD suggests higher stability ***
     */
    fun calculateMAD(list: List<BigDecimal>): BigDecimal {
        val mean = mathUtil.meanOfValues(list)
        val absoluteDifferences = list.map { value -> (value - mean).abs() }
        // SUM (absoluteDifferences) / QTY = MEAN
        var mad = mathUtil.meanOfValues(absoluteDifferences).setScale(2, RoundingMode.UP)
        if(mad.remainder(BigDecimal.ONE).toLong().equals(0L))
            mad = mad.stripTrailingZeros()
        return mad
    }


    /**
     * Quantiles are statistical measures used to divide a dataset into equal-sized intervals or segments.
     * They provide insights into the distribution of data and help identify values that fall within certain percentiles.
     * The most commonly known quantiles are quartiles, which divide the data into four segments, but you can also have
     * quintiles (five segments), deciles (ten segments), and percentiles (100 segments).
     *
     * Here's a breakdown of the common quantiles:
     *
     * Quartiles (Q1, Q2, Q3): Quartiles divide the dataset into four parts, with
     * Q1 being the value below which 25% of the data falls,
     * Q2 (also known as the median) being the value below which 50% of the data falls, and
     * Q3 being the value below which 75% of the data falls.
     *
     *
     */
    fun calculateQuartiles(list: List<BigDecimal>): List<BigDecimal> {
        val sortedNumbers = list.sorted()
        val size = sortedNumbers.size
        val quartiles = mutableListOf<BigDecimal>()

        var median : BigDecimal; var q1: BigDecimal; var  q3: BigDecimal

        median = getMiddleElement(sortedNumbers)

        // odd (impar)
        //                       q1    q2    q3
        //                    2, 4, 4, 5, 6, 7, 8
        if( size % 2 != 0 ){
            if(size == 1) {
                q1 = sortedNumbers.get(0)
                q3 = sortedNumbers.get(0)
            }else {
                q1 = getMiddleElement(sortedNumbers.subList(0, (size / 2)))
                q3 = getMiddleElement(sortedNumbers.subList((size / 2) + 1, size))
            }
        } else {
            // even (par)
            //                       q1     q2    q3
            //                    2, 4, 4,     6, 7, 8
            q1 = getMiddleElement(sortedNumbers.subList(0, (size/2)))
            q3 = getMiddleElement(sortedNumbers.subList((size/2), size))
        }

        quartiles.add(q1)           // First quartile (Q1)
        quartiles.add(median)       // Second quartile (Q2 or median)
        quartiles.add(q3)           // Third quartile (Q3)


        return quartiles
    }


    //////////////////////////////////////////////////////////////////////////

    /**
     * Return the middle element of a list
     */
    private fun getMiddleElement(sortedNumbers: List<BigDecimal>): BigDecimal {
        val size = sortedNumbers.size
        var middle : BigDecimal

        if (size % 2 != 0) {// odd = there is a middle element
            middle = sortedNumbers.get( size / 2)
        } else {  // even = the middle element if the mean of 2 central elements
            middle = (sortedNumbers.get(size / 2).plus(sortedNumbers.get(size / 2 - 1)).divide(BigDecimal(2)))
        }
        return middle
    }

    private fun getLastValues(allValues: List<BigDecimal>): List<BigDecimal> {
        // get the 5 last values
        return allValues.takeLast(5)
//        for (i in (if (allValues.size >= qty) allValues.size - qty else allValues.size) until allValues.size) {
//            lastValues.add(allValues.get(i))
//        }
        //return lastValues
    }
}