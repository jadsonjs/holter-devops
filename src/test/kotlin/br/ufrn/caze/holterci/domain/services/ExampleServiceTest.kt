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
 * br.ufrn.caze.publicano
 * ExampleServiceTest
 * 28/03/21
 */
package br.ufrn.caze.holterci.domain.services

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.test.context.SpringBootTest

/**
 * Example of unit test of a service
 *
 * @author jadson santos - jadson.santos@ufrn.br
 */
@ExtendWith(MockitoExtension::class)
@SpringBootTest
internal class ExampleServiceTest {

//    @Autowired
//    @InjectMocks
//    val exampleService : ExampleService? = null
//
//    @MockBean
//    val exampleRepository : ExampleRepository? = null
//
//    @BeforeEach
//    fun setUp() {
//        MockitoAnnotations.openMocks(this)
//    }
//
//    @Test
//    fun save() {
//        var e = Example(0, "ok")
//        Mockito.`when`(exampleRepository?.save(e)).thenReturn(e)
//        Assertions.assertEquals(e, exampleService?.save(e))
//    }
}