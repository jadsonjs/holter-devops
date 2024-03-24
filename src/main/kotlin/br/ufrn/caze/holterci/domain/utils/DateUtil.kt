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
 * DateUtil2
 * 04/07/21
 */
package br.ufrn.caze.holterci.domain.utils;

import br.ufrn.caze.holterci.domain.models.metric.Frequency
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * Date methods
 * Jadson Santos - jadsonjs@gmail.com
 */
@Component
class DateUtil {

    fun toLocalDateTime(dateToConvert: Date): LocalDateTime {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    fun toLocalDate(localDateTime: LocalDateTime): LocalDate {
        return localDateTime.toLocalDate()
    }

    fun toString(currentDateTime: LocalDateTime): String {
        return currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    fun convertToLocalDateViaInstant(dateToConvert: Date): LocalDate {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun toDate(dateToConvert: LocalDateTime): Date {
        return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant())
    }

    fun toLocalDateTime(dataStr: String): LocalDateTime {
        return toLocalDateTime(dataStr, "yyyy-MM-dd HH:mm:ss")
    }

    fun toLocalDateTime(dataStr: String, pattern: String): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return LocalDateTime.parse(dataStr, formatter)
    }

    fun isSameDay(date1: LocalDateTime, date2: LocalDateTime): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        return formatter.format(date1) == formatter.format(date2)
    }

    fun toIso8601(date: LocalDateTime?): String? {
        return fromLocalDateTime(date, "yyyy-MM-dd'T'HH:mm:ss'Z'")
    }

    fun fromLocalDateTime(date: LocalDateTime?, pattern: String?): String? {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return formatter.format(date)
    }

    /**
     * Increment the period by the frequency value
     */
    fun incrementPeriod(frequency : Frequency, baseDate: LocalDateTime) : LocalDateTime{
        var end : LocalDateTime
        when (frequency) {
            // include the int and end dates
            Frequency.WEEK  ->  end = baseDate.plusDays(7).minusDays(1)
            Frequency.MONTH ->  end =  baseDate.plusMonths(1).minusDays(1)
            Frequency.YEAR  ->  end = baseDate.plusYears(1).minusDays(1)
        }
        return end
    }

    /**
     * Generate a sequence of periods between int and end dates
     *
     * @param closed_init
     * @param closed_end
     * @return
     */
    fun generatePeriodDates(init: LocalDateTime, end: LocalDateTime, frequency: Frequency, project: Project): List<Period> {

        val periods: MutableList<Period> = ArrayList<Period>()

        var ptr1: LocalDateTime = init                            .with(LocalTime.of(0, 0, 0))

        var ptr2: LocalDateTime = incrementPeriod(frequency, ptr1).with(LocalTime.of(23, 59, 59))


        while ( ptr1.isBefore(end) &&   (   ptr2.isBefore(end) || ptr2.equals(end.with(LocalTime.of(23, 59, 59)) )    )   ) {

            periods.add(Period(ptr1, ptr2, project))

            // next period
            ptr1 = ptr2.plusDays(1).with(LocalTime.of(0, 0, 0))
            ptr2 = incrementPeriod(frequency, ptr1).with(LocalTime.of(23, 59, 59))
        }


        return periods
    }

    /**
     * Valid if the period is complete to execute the miner.
     */
    fun isMinerCycleCompleted(init : LocalDateTime, end : LocalDateTime, frequency : Frequency) : Boolean {

        if(init.isAfter(end))
            return false

        if( end.isAfter(LocalDateTime.now()) ){
            return false
        }

        // https://stackoverflow.com/questions/47753105/count-the-number-of-days-including-the-two-given-dates
        // The javadoc is clear that the end date is not included in the calculation. So you can simply add 1:
        val daysBetween: Long = (Duration.between(init, end).toDays() + 1)

        when (frequency) {
            Frequency.WEEK  -> if(daysBetween < 7) return false
            Frequency.MONTH -> if(daysBetween < 30) return false
            Frequency.YEAR  -> if(daysBetween < 365) return false
        }

        return true // period valid
    }


    fun isBetweenDates(date: LocalDateTime, start: LocalDateTime, end: LocalDateTime): Boolean {
        if (date == start) return true
        if (date == end) return true
        return if (date.isAfter(start) && date.isBefore(end)) {
            true
        } else {
            false
        }
    }

    fun daysBetween(init : LocalDateTime, end : LocalDateTime): Int{
        return Duration.between(init, end).toDays().toInt()
    }

    fun daysBetweenInclusive(init : LocalDateTime, end : LocalDateTime): Int {
        return (init.until(end, ChronoUnit.DAYS) + 1).toInt() // include the start and end.
    }

    fun daysBetweenInclusiveWithOutWeekends(init : LocalDateTime, end : LocalDateTime): Int {
        return daysBetweenInclusive(init, end) - weekendDaysBetween(init, end)
    }

    fun hoursBetween(init : LocalDateTime, end : LocalDateTime): Long{
        return Duration.between(init, end).toHours()
    }

    fun secondsBetween(init : LocalDateTime, end : LocalDateTime): Long{
        return Duration.between(init, end).toSeconds()
    }

    fun secondsToHours(hours: BigDecimal): BigDecimal {
        val secondsInHour = BigDecimal(3600)
        return hours.divide(secondsInHour, 2, RoundingMode.HALF_UP)
    }

    fun weekendDaysBetween(init: LocalDateTime, end: LocalDateTime): Int {
        var currentDate = init
        var weekenddaysCount = 0

        while (currentDate.isBefore(end) || currentDate.isEqual(end)) {
            if (currentDate.dayOfWeek == DayOfWeek.SATURDAY || currentDate.dayOfWeek == DayOfWeek.SUNDAY) {
                weekenddaysCount++
            }
            currentDate = currentDate.plus(1, ChronoUnit.DAYS)
        }

        return weekenddaysCount
    }

    fun format(date : LocalDateTime, pattern : String): String {
        return DateTimeFormatter.ofPattern(pattern).format(date)
    }

}