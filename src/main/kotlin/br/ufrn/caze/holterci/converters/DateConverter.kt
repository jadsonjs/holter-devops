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
 * br.ufrn.caze.publicano.controllers.converters
 * DateConverter
 * 01/07/21
 */
package br.ufrn.caze.holterci.converters;

import org.mapstruct.Named
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Convert data to string and string to date
 * Jadson Santos - jadsonjs@gmail.com
 */
@Component("HolterDateConverter")
@Named("DateConverter")
class DateConverter {

    @Named("fromLocalDate")
    fun toString(date : LocalDateTime?) : String? {
        if(date != null)
            return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date);
        return null;
    }

    /**
     * Converts to LocalDateTime a String that is formatted either in the "yyyy-MM-ddTHH:mm:ssZ" format
     * or the "yyyy-MM-dd" format.
     */
    @Named("toLocalDate")
    fun toLocalDateTime( date : String? ): LocalDateTime? {
        if(!date.isNullOrEmpty()) {
            return try {
                val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
                val ld = LocalDate.parse(date, formatter)
                LocalDateTime.of(ld, LocalDateTime.now().toLocalTime())
            } catch (e: DateTimeParseException) {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val ld = LocalDate.parse(date, formatter)
                LocalDateTime.of(ld, LocalDateTime.now().toLocalTime())
            }
        }
        return null;
    }
}