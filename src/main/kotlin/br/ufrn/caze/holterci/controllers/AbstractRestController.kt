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
 * AbstractController
 * 28/03/21
 */
package br.ufrn.caze.holterci.controllers

import br.ufrn.caze.holterci.converters.UserDtoConverter
import br.ufrn.caze.holterci.domain.dtos.MessageDto
import br.ufrn.caze.holterci.domain.dtos.user.UserDto
import br.ufrn.caze.holterci.domain.exceptions.BusinessException
import br.ufrn.caze.holterci.domain.exceptions.TooManyRequestsException
import br.ufrn.caze.holterci.domain.models.user.User
import br.ufrn.caze.holterci.domain.utils.StringUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus


/**
 * Common methods of controller
 *
 * @author jadson santos - jadson.santos@ufrn.br
 */
abstract class AbstractRestController{

    @Autowired
    lateinit var userDtoConverter: UserDtoConverter

    @ExceptionHandler(BusinessException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    open fun handleBusinessException(bex: BusinessException): MessageDto {
        return MessageDto(bex.m)
    }

    @ExceptionHandler(TooManyRequestsException::class)
    @ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
    @ResponseBody
    open fun handleTooManyRequestsException(ex: TooManyRequestsException): MessageDto {
        return MessageDto(ex.m)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    open fun handleException(ex: Exception): MessageDto {
        return MessageDto((if (ex.message != null) ex.message else "Internal Server Error")!!)
    }

    /**
     * Bean validation messages
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    open fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): MessageDto {
        val errors: MutableList<String> = ArrayList()
        for (error in e.bindingResult.fieldErrors) {
            errors.add(error.field + ": " + error.defaultMessage)
        }
        for (error in e.bindingResult.globalErrors) {
            errors.add(error.objectName + ": " + error.defaultMessage)
        }
        return MessageDto(errors)
    }


    fun getLoggedInUser(request: HttpServletRequest): User ? {
        val mapper: ObjectMapper = ObjectMapper()
            .registerModule(ParameterNamesModule())
            .registerModule(Jdk8Module())
            .registerModule(JavaTimeModule())
        val headerUser = request.getHeader("logged-in-user")
        return if (StringUtil().isNotEmpty(headerUser)) {
            userDtoConverter.toModel(mapper.readValue(headerUser, UserDto::class.java))
        } else {
            null
        }
    }


}