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
 * br.ufrn.caze.publicano.controllers
 * MetricController
 * 18/04/21
 */
package br.ufrn.caze.holterci.controllers.crud

import br.ufrn.caze.holterci.controllers.AbstractRestController
import br.ufrn.caze.holterci.domain.dtos.crud.MetricDto
import br.ufrn.caze.holterci.domain.models.metric.Metric
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller to list supported metrics.
 *
 * As each metric has it own way to be calculated, the list of metric is hardcoded.
 * They can not be created dynamically.
 *
 * @author bruno batista - brunokaike@ufrn.edu.br
 */
@RestController
@RequestMapping("/api/metric")
class MetricRestController : AbstractRestController() {


    /**
     * Return a list of metric supported by this tool.
     */
    @GetMapping
    fun get() : ResponseEntity<List<MetricDto>> {
        return getAll()
    }

    /**
     * Return a list of CI metrics supported by this tool.
     */
    @GetMapping("/all")
    fun getAll() : ResponseEntity<List<MetricDto>> {
        val metrics = ArrayList<MetricDto>()
        Metric.getAll().forEach {
            metrics.add(Metric.toDto(it))
        }
        return ResponseEntity(metrics, HttpStatus.OK)
    }


//    /**
//     * Return a list of metrics that have reference values ????
//     */
//    @GetMapping("/references")
//    fun getAllMetrics() : ResponseEntity<List<MetricDto>> {
//        val metrics = ArrayList<MetricDto>()
//
//        Metric.getAll().forEach{
//            metrics.add(Metric.toDto(it))
//        }
//
//        return ResponseEntity(metrics, HttpStatus.OK)
//    }


}