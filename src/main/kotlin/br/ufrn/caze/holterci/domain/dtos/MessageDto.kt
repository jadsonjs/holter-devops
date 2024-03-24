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
 * MessageDTO
 * 28/03/21
 */
package br.ufrn.caze.holterci.domain.dtos

/**
 * Message return to front-end
 *
 * @author jadson santos - jadson.santos@ufrn.br
 */
class MessageDto{

    var  messageList = ArrayList<String>()

    constructor(m: String){
        addMessage(m)
    }

    constructor(ms: List<String>) {
        addMessages(ms)
    }


    private fun addMessage(m: String) {
        messageList.add(m)
    }

    /**
     * adicoina varias mensagems
     * @param ms
     */
    private fun addMessages(ms: List<String>) {
        messageList.addAll(ms)
    }

    fun containsMessage(message: String): Boolean {
        return messageList.contains(message)
    }
}