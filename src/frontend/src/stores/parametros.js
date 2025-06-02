/**
 * Universidade Federal do Rio Grande do Norte
 * Instituto Metrópole Digital
 * Diretoria de Tecnologia da Informação
 *
 *
 * Variáveis de ambiente da aplicação compartilhadas entre o front e backend.
 * Recebidas do backend.
 *
 * @author Jadson Santos - jadson.santos@ufrn.br
 * @since  23/06/2021
 */
// import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { useHttp } from '@/utils/axiosUtils'
import { useMessage } from '@/utils/message';

const storeID = 'parametros'
const appName = import.meta.env.VITE_APP_PUBLIC_PATH.replace(/[/]+/g, '')
const storeKey = `${appName}-${storeID}-store`

export const useParametrosStore = defineStore(storeKey, {
	state: () => ({
/* 		loginMode: '',
 		login2Fa: true, */
 		appUrl: '',
/* 		authenticationURL: '',
		clientID: '',  */
		appName: '',
		environment: '',
		permiteAutoCadastro: false,
		permitePaginaPublica: false,
		loaded: false,
		isLoading: false,
		error: false,
		lockNoCIMetrics: false,
		enableLoginPage: false,
	}),

	actions: {

		async loadParametros() {

			// Introduce a small delay to ensure the store has rehydrated before checking loaded
			await this.$patch((state) => {
				const persistedState = localStorage.getItem(storeKey);
				if (persistedState) Object.assign(state, JSON.parse(persistedState));
			});


			if (! this.loaded ) {

				console.log(' Calling api/parameter/load ..............')

				const { http } = useHttp();

				this.isLoading = true
				await http
					.get('/api/parameter/load')
					.then((result) => {
						this.$patch({ ...result.data, loaded: true, error: false })
					})
					.catch(() => {
						this.error = true
						const { message } = useMessage();
						message.error('Erro ao acessar sistema', `Erro ao carregar as variáveis em ambiente do sistema. Verificar se o serviço está acessível.`)
					})
					.finally(() => (this.isLoading = false))
			}
		},

		reload() {
            this.loaded = false;
			localStorage.setItem(storeKey, JSON.stringify(this.$state));
			this.loadParametros();
        },

	},

	persist: {
		key: storeKey,
		storage: localStorage,
	},
})