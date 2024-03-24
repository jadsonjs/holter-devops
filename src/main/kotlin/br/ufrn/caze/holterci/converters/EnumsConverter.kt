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
 * br.ufrn.caze.publicano.persistence.converters
 * EnumsConverter
 * 08/06/21
 */
package br.ufrn.caze.holterci.converters;

import br.ufrn.caze.holterci.domain.dtos.crud.MetricDto
import br.ufrn.caze.holterci.domain.dtos.crud.MetricRepositoryDto
import br.ufrn.caze.holterci.domain.dtos.user.RoleDto
import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricRepository
import br.ufrn.caze.holterci.domain.models.user.Role
import org.mapstruct.Named
import org.springframework.stereotype.Component
/**
 * Converte Enum data.
 * Jadson Santos - jadsonjs@gmail.com
 */
@Component
@Named("EnumsConverter")
class EnumsConverter {

    @Named("toMetricDTO")
    fun metricConverter(m: Metric): MetricDto {
        return Metric.toDto(m)
    }

    @Named("toMetricModel")
    fun metricConverter(dto: MetricDto): Metric {
        return Metric.getById(dto.id).get(0)
    }


    @Named("toRepositoryDTO")
    fun repositoryConverter(m: MetricRepository?): MetricRepositoryDto? {
        return if (m != null) MetricRepositoryDto(m.name) else null
    }

    @Named("toRepositoryModel")
    fun repositoryConverter(dto: MetricRepositoryDto?): MetricRepository? {
        return if (dto != null) MetricRepository.getByName(dto.name) else null
    }


    @Named("toRoleDTO")
    fun roleConverter(r: Role?): RoleDto? {
        return if (r != null) RoleDto(r.name) else null
    }

    @Named("toRoleModel")
    fun roleConverter(dto: RoleDto?): Role? {
        return if (dto != null) Role.getByName(dto.name) else null
    }

}