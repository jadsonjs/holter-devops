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
package br.ufrn.caze.holterci.controllers

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Carrega informações do ambiente da aplicação.
 *
 * @author Jadson Santos - jadson.santos@ufrn.br
 */
@RestController
@RequestMapping(path = ["/api/parameter"])
class ParameterRestController {

    @Value("\${application.url}")
    var applicationUrl: String? = null

    @Value("\${application.name}")
    var nomeAplicacao: String? = null

    /**
     * Se o sistema irá habilitar o auto cadastro do usuário.
     * Pode ser que o sistema deve ser acessível apenas por determinadas pessoas que o admin cadastre
     * Não será permitido o usuário se cadatrar e entrar na aplicação.
     * Por padrão permite.
     */
    @Value("\${application.allow.self.registration:false}")
    var permiteAutoCadastro: Boolean = false

    @Value("\${application.public.page:true}")
    var paginaPublica: Boolean = false

    @Value("\${lock.no-ci-metrics:false}")
    var lockNoCIMetrics: Boolean = false

    @Value("\${enable.login-page:false}")
    var enableLoginPage: Boolean = false

    /**
     * Retorna algumas variáveis de ambiente para o front-end para não precisa ficar armazendo nos 2 lados
     *
     * ESSE SERVIÇO É PÚBLICO, ENTÃO NÃO ADICIONE PARAMETROS SENSIVEIS, COMO SENHAS, NELE
     *
     * NÃO REMOVA ESSE SERVIÇO, ELE É NECESSÁRIO PARA O FRONT_END FUNCIONAR ADEQUADAMENTE
     *
     *
     * @return
     */
    @GetMapping(path = ["/load"])
    fun loadEnvironment(): ResponseEntity<Map<String, Any?>> {
        val data: MutableMap<String, Any?> = HashMap()
        data["appUrl"] = applicationUrl
        data["appName"] = nomeAplicacao
        data["permiteAutoCadastro"] = permiteAutoCadastro
        data["permitePaginaPublica"] = paginaPublica
        data["lockNoCIMetrics"] = lockNoCIMetrics
        data["enableLoginPage"] = enableLoginPage

        return ResponseEntity(data, HttpStatus.OK)
    }
}