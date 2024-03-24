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

import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.domain.dtos.crud.CollectorDto
import org.mapstruct.*

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = arrayOf(PeriodDtoConverter::class, EnumsConverter::class, UUIDConverter::class) )
interface CollectorDtoConverter {

//    @Mappings(
//        Mapping(source = "measure", target = "measure", qualifiedByName = ["EnumsConverter", "toMeasureModel"]),
//    )
//    fun toModel(dto: CollectorDto) : Collector
//
    @Mappings(
        Mapping(source = "metric", target = "metric", qualifiedByName = ["EnumsConverter", "toMetricDTO"]),
        Mapping(source = "repository", target = "repository", qualifiedByName = ["EnumsConverter", "toRepositoryDTO"]),
    )
    @InheritInverseConfiguration
    fun toDto(model: Collector) : CollectorDto

    fun toDtoList(all: List<Collector>): List<CollectorDto>
}