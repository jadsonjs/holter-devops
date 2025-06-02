import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

import PrimeVue from 'primevue/config';
import Aura from '@primevue/themes/aura';
import ToastService from 'primevue/toastservice';

import '@fortawesome/fontawesome-free/css/all.min.css' // fontawesome css
import '@/assets/scss/style.scss'
import '@/assets/tailwind.css'; // used on login page and configuartion menu

import App from './App.vue'

import router from './router';

import { Field, Form } from 'vee-validate'; // importa os componentes principais do vee-validate 
import '@/utils/validators'; // Importa as regras do vee-validate

import { vMaska } from "maska/vue"

const app = createApp(App)

app.use(ToastService);

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

app.use(router)
app.use(pinia)

app.use(PrimeVue, {
	theme: {
			preset: Aura,
			options: {
				darkModeSelector: '.app-dark'
			}
	}
});

// Registra os componentes do vee-validate globalmente
app.component('VField', Field);
app.component('VForm', Form);

// Registrar a diretiva de formatacao globalmente
app.directive('maska', vMaska)

app.mount('#app')



