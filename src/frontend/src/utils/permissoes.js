/**
 *  Universidade Federal do Rio Grande do Norte
 *  Instituto Metrópole Digital
 *  Diretoria de Tecnologia da Informação
 *
 *
 *  Arquivo com funções de utilidades para permissões dos usuarios.
 *
 *  @author Jadson Santos - jadson.santos@ufrn.br
 *  @since  21/08/2024
 */

/**
 * Verifica se o usuário logado tem o papel ou um dos papeis desejados
 *
 * Exemplo de Uso:
 *   v-if="hasUserAnyOfRoles(loginStore.usuarioLogado, 'ADMIN')"
 *   v-if="hasUserAnyOfRoles(loginStore.usuarioLogado, ['ADMIN', 'COORDENADOR'])"
 *
 * Equivalente a anotação do spring @PreAuthorize("hasRole('ADMIN')")
 *
 * @param {string|string[]} papeis - String ou array de strings
 */

import { useLoginStore } from "@/stores/login"
const loginStore = useLoginStore()

import { objectIsEmpty } from '@/utils/funcoes'

/**
 * Verifica se o usuario logado tem os papeis passados
 * @param {*} papeis 
 * @returns 
 */ 
export function hasRoles(papeis) {
	if (!objectIsEmpty(loginStore.usuarioLogado) && !objectIsEmpty(loginStore.usuarioLogado.permission)) {
		papeis = [].concat(papeis)
		let userRoles = loginStore.usuarioLogado.permission.map((perm) => perm.role.name)
		for (const papel of papeis) {
			if (userRoles.includes(papel)) {
				return true
			}
		}
	}
	return false
}
