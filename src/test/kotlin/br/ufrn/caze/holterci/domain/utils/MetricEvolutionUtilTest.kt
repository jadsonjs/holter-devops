package br.ufrn.caze.holterci.domain.utils

import br.ufrn.caze.holterci.domain.models.metric.MetricTrend
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Test the metric evolution
 */
class MetricEvolutionUtilTest {

    @Test
    fun calcMetricTrendCoverageUP() {

        val numbers = mutableListOf(
            BigDecimal("60"),
            BigDecimal("70"),
            BigDecimal("75"),
            BigDecimal("85"),
            BigDecimal("90")
        )

        var (trendPercentage, trendLabel) = MetricEvolutionUtil(MathUtil()).calcTrend(numbers)
        assertEquals(BigDecimal(8.34).setScale(2, RoundingMode.UP), trendPercentage)
        assertEquals(MetricTrend.UP, trendLabel)
    }


    @Test
    fun calcMetricTrendCoverageDOWN() {

        val numbers = mutableListOf(
            BigDecimal("60"),
            BigDecimal("70"),
            BigDecimal("75"),
            BigDecimal("90"),
            BigDecimal("80")
        )

        var (trendPercentage, trendLabel) = MetricEvolutionUtil(MathUtil()).calcTrend(numbers)
        assertEquals(BigDecimal(-16.66).setScale(2, RoundingMode.UP), trendPercentage)
        assertEquals(MetricTrend.DOWN, trendLabel)
    }



    @Test
    fun calculateMAD() {
        val numbers = mutableListOf(
                BigDecimal("10"),
        BigDecimal("20"),
        BigDecimal("30"),
        BigDecimal("40"),
        BigDecimal("50")
        )

        var mad = MetricEvolutionUtil(MathUtil()).calculateMAD(numbers)
        assertEquals(BigDecimal(12), mad)
    }


    /**
     * Example from:
     * https://www.khanacademy.org/math/statistics-probability/summarizing-quantitative-data/other-measures-of-spread/a/mean-absolute-deviation-mad-review
     */
    @Test
    fun calculateMAD2Test() {
        val numbers = mutableListOf(
            BigDecimal("10"),
            BigDecimal("15"),
            BigDecimal("15"),
            BigDecimal("17"),
            BigDecimal("18"),
            BigDecimal("21")
        )

        var mad = MetricEvolutionUtil(MathUtil()).calculateMAD(numbers)
        assertEquals(BigDecimal(2.67).setScale(2, RoundingMode.UP), mad)
    }


    @Test
    fun calculateQuartilesOddData() {

        // 5, 7, 4, 4, 6, 2, 8

        val numbers = mutableListOf(
            BigDecimal("5"),
            BigDecimal("7"),
            BigDecimal("4"),
            BigDecimal("4"),
            BigDecimal("6"),
            BigDecimal("2"),
            BigDecimal("8")
        )

        var quartiles = MetricEvolutionUtil(MathUtil()).calculateQuartiles(numbers)


        //                       q1    q2    q3
        // Put them in order: 2, 4, 4, 5, 6, 7, 8
        val q1 = quartiles[0]
        val q2 = quartiles[1]
        val q3 = quartiles[2]

        assertEquals(BigDecimal(4), q1)
        assertEquals(BigDecimal(5), q2)
        assertEquals(BigDecimal(7), q3)
    }


    @Test
    fun calculateQuartilesOddData2() {

        // 2, 5, 7, 4, 4, 6, 2, 8, 10

        val numbers = mutableListOf(
            BigDecimal("2"),
            BigDecimal("5"),
            BigDecimal("7"),
            BigDecimal("4"),
            BigDecimal("4"),
            BigDecimal("6"),
            BigDecimal("2"),
            BigDecimal("8"),
            BigDecimal("10")
        )

        var quartiles = MetricEvolutionUtil(MathUtil()).calculateQuartiles(numbers)



        //                        q1         q2       q3
        // Put them in order: 2, 2, 4, 4,    5,   6, 7, 8, 10
        val q1 = quartiles[0]
        val q2 = quartiles[1]
        val q3 = quartiles[2]

        assertEquals(BigDecimal(3), q1)
        assertEquals(BigDecimal(5), q2)
        assertEquals(BigDecimal(7.5), q3)
    }


    @Test
    fun calculateQuartilesEvenData() {

        // 1, 3, 3, 4, 5, 6, 6, 7, 8, 8

        val numbers = mutableListOf(
            BigDecimal("1"),
            BigDecimal("3"),
            BigDecimal("3"),
            BigDecimal("4"),
            BigDecimal("5"),
            BigDecimal("6"),
            BigDecimal("6"),
            BigDecimal("7"),
            BigDecimal("8"),
            BigDecimal("8")
        )

        var quartiles = MetricEvolutionUtil(MathUtil()).calculateQuartiles(numbers)



        //                          q1        q2        q3
        // Put them in order: 1, 3, 3, 4, 5,      6, 6, 7, 8, 8
        val q1 = quartiles[0]
        val q2 = quartiles[1]
        val q3 = quartiles[2]

        assertEquals( BigDecimal(3), q1)
        assertEquals( BigDecimal(5.5), q2)
        assertEquals( BigDecimal(7), q3)
    }

    @Test
    fun calculateQuartilesEvenData2() {

        // 1, 1, 3, 3, 4, 5, 6, 6, 7, 8, 8, 9

        val numbers = mutableListOf(
            BigDecimal("1"),
            BigDecimal("1"),
            BigDecimal("3"),
            BigDecimal("3"),
            BigDecimal("4"),
            BigDecimal("5"),
            BigDecimal("6"),
            BigDecimal("6"),
            BigDecimal("7"),
            BigDecimal("8"),
            BigDecimal("8"),
            BigDecimal("9")
        )

        var quartiles = MetricEvolutionUtil(MathUtil()).calculateQuartiles(numbers)



        //                           q1          q2         q3
        // Put them in order: 1, 1, 3, 3, 4, 5,      6, 6, 7, 8, 8, 9
        val q1 = quartiles[0]
        val q2 = quartiles[1]
        val q3 = quartiles[2]

        assertEquals( BigDecimal(3), q1)
        assertEquals( BigDecimal(5.5), q2)
        assertEquals( BigDecimal(7.5), q3)
    }
}