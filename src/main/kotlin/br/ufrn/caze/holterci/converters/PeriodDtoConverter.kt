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
package br.ufrn.caze.holterci.converters

import br.ufrn.caze.holterci.domain.dtos.crud.PeriodDto
import br.ufrn.caze.holterci.domain.models.metric.Period
import org.mapstruct.*

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = arrayOf(DateConverter::class) )
interface PeriodDtoConverter {

    @Mappings(
        Mapping(source = "init", target = "init", qualifiedByName = ["DateConverter", "toLocalDate"]),
        Mapping(source = "end", target = "end", qualifiedByName = ["DateConverter", "toLocalDate"])
    )
    fun toModel(dto: PeriodDto) : Period

    @Mappings(
        Mapping(source = "init", target = "init", qualifiedByName = ["DateConverter", "fromLocalDate"]),
        Mapping(source = "end", target = "end", qualifiedByName = ["DateConverter", "fromLocalDate"])
    )
    @InheritInverseConfiguration
    fun toDto(model: Period) : PeriodDto

    fun toDtoList(findAll: List<Period>): List<PeriodDto>
}