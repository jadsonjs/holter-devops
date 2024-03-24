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
package br.ufrn.caze.holterci.domain.models.user

enum class Role {

    ADMIN,
    /** See metric of all developers */
    MANAGER,
    /** See just his own metrics */
    DEVELOPER;

    // Kotlin allows us to create objects that are common to all instances of a class
    companion object {

        fun getByName(name: String): Role {

            var role: Array<Role> = Role.values()
            for (r in role) {
                if (r.name == name) {
                    return r;
                }
            }
            throw IllegalArgumentException("Invalid MetricRepository name")
        }

        /**
         * Return all roles
         */
        fun getAll(): List<Role> {

            var list: ArrayList<Role> = ArrayList()

            var roles: Array<Role> = Role.values()
            for (m in roles) {
                list.add(m);
            }
            return list
        }

    }
}