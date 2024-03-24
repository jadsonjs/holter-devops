package br.ufrn.caze.holterci.domain.utils

import br.ufrn.caze.holterci.domain.models.metric.Frequency
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class DateUtilTest {

    @Test
    fun generatePeriodDates() {

        val util = DateUtil()

        val project : Project = Project()

        val periods: List<Period> = util.generatePeriodDates(
            LocalDateTime.of(2021, 1, 1, 0, 0, 0),
            LocalDateTime.of(2021, 5, 31, 23, 59, 59), Frequency.MONTH, project
        )

        assertEquals(5, periods.size)
        // first period 1 to 31 january
        assertEquals(LocalDateTime.of(2021, 1, 1, 0, 0, 0), periods.get(0).init)
        assertEquals(LocalDateTime.of(2021, 1, 31, 23, 59, 59), periods.get(0).end)
        // last period 1 to 31 of may
        assertEquals(LocalDateTime.of(2021, 5, 1, 0, 0, 0), periods.get(periods.size - 1).init)
        assertEquals(LocalDateTime.of(2021, 5, 31, 23, 59, 59), periods.get(periods.size - 1).end)
    }


    @Test
    fun generatePeriodDatesWeek() {

        val util = DateUtil()

        val project : Project = Project()

        val periods: List<Period> = util.generatePeriodDates(
            LocalDateTime.of(2023, 10, 1, 0, 0, 0),
            LocalDateTime.of(2023, 10, 31, 23, 59, 59), Frequency.WEEK, project
        )

        assertEquals(4, periods.size)
        // first week 1 to 31 october
        assertEquals(LocalDateTime.of(2023, 10, 1, 0, 0, 0), periods.get(0).init)
        assertEquals(LocalDateTime.of(2023, 10, 7, 23, 59, 59), periods.get(0).end)
        // last week complete 22 to 28 of october
        assertEquals(LocalDateTime.of(2023, 10, 22, 0, 0, 0), periods.get(periods.size - 1).init)
        assertEquals(LocalDateTime.of(2023, 10, 28, 23, 59, 59), periods.get(periods.size - 1).end)
    }

    @Test
    fun generatePeriodDatesComplex() {

        val util = DateUtil()

        val project : Project = Project()

        val periods: List<Period> = util.generatePeriodDates(
            LocalDateTime.of(2021, 1, 25, 14, 4, 39),
            LocalDateTime.of(2021, 5, 30, 15, 10, 32), Frequency.MONTH, project
        )

        assertEquals(4, periods.size)
        assertEquals(LocalDateTime.of(2021, 1, 25, 0, 0, 0), periods.get(0).init)
        assertEquals(LocalDateTime.of(2021, 5, 24, 23, 59, 59), periods.get(periods.size - 1).end)
    }


    /**
     * From :
     * 2021-11-17
     * To :
     * 2022-01-17
     */
    @Test
    fun generatePeriodDatesNewYearPeriod() {

        val util = DateUtil()

        val project : Project = Project()

        val periods: List<Period> = util.generatePeriodDates(
            LocalDateTime.of(2021, 11, 17, 0,0, 0, 0),
            LocalDateTime.of(2022, 1, 17, 23, 59, 59), Frequency.MONTH, project
        )

        assertEquals(2, periods.size)
        assertEquals(LocalDateTime.of(2021, 11, 17, 0, 0, 0), periods.get(0).init)
        assertEquals(LocalDateTime.of(2021, 12, 16, 23, 59, 59), periods.get(0).end)
        assertEquals(LocalDateTime.of(2021, 12, 17, 0, 0, 0), periods.get(1).init)
        assertEquals(LocalDateTime.of(2022, 1, 16, 23, 59, 59), periods.get(1).end)
    }


    @Test
    fun generatePeriodDatesLastMonthIncomplete() {

        val util = DateUtil()

        val project : Project = Project()

        val periods: List<Period> = util.generatePeriodDates(
            LocalDateTime.of(2021, 1, 25, 14, 4, 39),
            LocalDateTime.of(2021, 5, 15, 15, 10, 32), Frequency.MONTH, project
        )

        assertEquals(3, periods.size)
        assertEquals(LocalDateTime.of(2021, 1, 25, 0, 0, 0), periods.get(0).init)
        assertEquals(LocalDateTime.of(2021, 4, 24, 23, 59, 59), periods.get(periods.size - 1).end)
    }

    @Test
    fun dayBetweenZeroDays() {

        val util = DateUtil()

        assertEquals(0, util.daysBetween(
                LocalDateTime.of(2021, 1, 25, 8, 4, 39),
                LocalDateTime.of(2021, 1, 25, 18, 4, 39)  ) )

    }

    @Test
    fun dayBetweenOneDays() {

        val util = DateUtil()

        assertEquals(1, util.daysBetween(
            LocalDateTime.of(2021, 1, 25, 8, 4, 39),
            LocalDateTime.of(2021, 1, 26, 18, 4, 39)  ) )

    }

    @Test
    fun dayBetweenTenDays() {

        val util = DateUtil()

        assertEquals(10, util.daysBetween(
            LocalDateTime.of(2021, 1, 25, 8, 4, 39),
            LocalDateTime.of(2021, 2, 4, 18, 4, 39)  ) )

    }


    @Test
    fun hoursBetween() {

        val util = DateUtil()

        assertEquals(6, util.hoursBetween(
            LocalDateTime.of(2023, 1, 2, 23, 2, 36),
            LocalDateTime.of(2023, 1, 3, 5, 48, 45)
              )
        )

    }

    @Test
    fun hoursBetween2() {

        val util = DateUtil()

        assertEquals(5, util.hoursBetween(
            LocalDateTime.of(2023, 1, 2, 23, 48, 36),
            LocalDateTime.of(2023, 1, 3, 5, 42, 45)
        )
        )

    }



    /**
     * 22 days in out/2023 removing saturday and sunday
     */
    @Test
    fun daysBetweenInclusiveWithOutWeekends() {

        val util = DateUtil()

        assertEquals(22, util.daysBetweenInclusiveWithOutWeekends(
            LocalDateTime.of(2023, 10, 1, 0, 0, 0),
            LocalDateTime.of(2023, 10, 31, 23, 59, 59)  ) )

    }

    /**
     * 21 days in out/2023 removing saturday and sunday because it does not complete a whole day yet
     */
    @Test
    fun daysBetweenInclusiveWithOutWeekends2() {

        val util = DateUtil()

        assertEquals(21, util.daysBetweenInclusiveWithOutWeekends(
            LocalDateTime.of(2023, 10, 1, 8, 4, 39),
            LocalDateTime.of(2023, 10, 31, 7, 4, 39)  ) )

    }



    /**
     * Weekends days in out/2023 = 9
     */
    @Test
    fun weekendsBetween() {

        val util = DateUtil()

        assertEquals(9, util.weekendDaysBetween(
            LocalDateTime.of(2023, 10, 1, 8, 4, 39),
            LocalDateTime.of(2023, 10, 31, 18, 4, 39)  ) )

    }



    // 7 days 01/10 to 07/10
    @Test
    fun incrementPeriodWeekTest() {
        val util = DateUtil()
        var endPeriod : LocalDateTime = util.incrementPeriod(Frequency.WEEK, LocalDateTime.of(2023, 10, 1,0, 0,0))
        assertEquals(LocalDateTime.of(2023, 10, 7, 0, 0, 0), endPeriod)
    }

    // 1 month  01/10 to 31/10
    @Test
    fun incrementPeriodMonthTest() {
        val util = DateUtil()
        var endPeriod : LocalDateTime = util.incrementPeriod(Frequency.MONTH, LocalDateTime.of(2023, 10, 1,0, 0,0))
        assertEquals(LocalDateTime.of(2023, 10, 31, 0, 0, 0), endPeriod)
    }


    // 1 month  01/10/2023 to 30/09/2024
    @Test
    fun incrementPeriodYearTest() {
        val util = DateUtil()
        var endPeriod : LocalDateTime = util.incrementPeriod(Frequency.YEAR, LocalDateTime.of(2023, 10, 1,0, 0,0))
        assertEquals(LocalDateTime.of(2024, 9, 30, 0, 0, 0), endPeriod)
    }
}