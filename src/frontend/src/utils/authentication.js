/**
 *  Universidade Federal do Rio Grande do Norte
 *  Instituto Metrópole Digital
 *  Diretoria de Tecnologia da Informação
 *
 *  Arquivo com funções para authenticacao do usuario
 *
 *  @author Jadson Santos - jadson.santos@ufrn.br
 *  @since  02/08/2024
 */

import { computed, ref } from 'vue';
import { storeToRefs } from 'pinia';
import Cookies from 'js-cookie';

const publicPath = import.meta.env.VITE_APP_PUBLIC_PATH;
const appName = publicPath.replace(/[/]+/g, '')

// store
import { useLoginStore } from '@/stores/login';
const loginStore = useLoginStore();
const { usuarioLogado } = storeToRefs(loginStore);
const setAutenticado = loginStore.setAutenticado;
const autenticado = loginStore.autenticado;

import { lockTimeLoginStore } from '@/stores/lockTimeLogin.js';

import { useParametrosStore } from '@/stores/parametros';
const parametrosStore = useParametrosStore();

// axios
import { useHttp } from '@/utils/axiosUtils';
const { http } = useHttp();

// message
import { useMessage } from '@/utils/message';
const { message } = useMessage();


/* ***** FUNCOES DO LOGIN  ***** */

/**
  * Realiza o login no banco local da aplicação.
  * Se tudo for ok, salva os dados do usuário logado e redireciona para a página principal da aplicação
  */
export const logarLocal = (usuario) => {
	console.log(usuario)
	http.post('/api/login', usuario, {
		headers: { 'Content-Type': 'application/json' },
	}).then((response) => {
		console.log(response)
		loginStore.login(response.data);

		lockTimeLoginStore.resetFailedAttempts()
		redirectAfterLogin();

	}).catch((error) => {
		if(error.data)
			message.error('Erro na Autenticação', error.data.messageList)
		else{
			message.error('Erro na Autenticação', error)
			console.log(error)
		}
		lockTimeLoginStore.registerFailedAttempt();
	})
}

/**
 * Realize o login usando o SSO da UFRN.
 * O usuario já foi autenticado no SSO, agora usando o código retornado pelo SSO, carregamos os dados do usuario.
 */
/* export const logarUnificado = (code, redirectURL) => {

	const requestData = {
		code: code,
		redirectURL: redirectURL.value
	};

	http.post('/api/login/logar-unificado', requestData, {
		headers: { 'Content-Type': 'application/json' },
	})
		.then((response) => {

			loginStore.login(response.data[0], response.data[1]);

			lockTimeLoginStore.resetFailedAttempts()
			redirectAfterLogin();

		})
		.catch((error) => {
			console.log('error: ' + error)
			lockTimeLoginStore.registerFailedAttempt();
			message.error('Erro na Autenticação', error.data.messageList)
		})
} */




/**
 * Relaliza o logout no sistema,
 * removendo as informações do store e redirecionando para a tela de login
 *
 * Ps.: Chama o backend para gerar no log a informação que o usuário deslogou no sistema
 */
export const logout = () => {

	http.post('/api/logout', usuarioLogado)
	 .then(() => {
		loginStore.logout() // clear login store
	}).catch((error) => {
		loginStore.logout() // clear login store
	 }).finally(() => {
		redirectToLogin();
	})
};


/**
 * PARTE DO 2FA
 *
 * As condicoes relevantes são: user já está autenticado; login2FA está ativado; Cookie de dispositivo confiável foi guardado.
 */

// recupera o nome do cookie de forma que seja unico por usuario
function getCookieName() {
	return `${appName}a486${usuarioLogado.value.id}4243c51d397a2de5`;;
}

const isLoadingParametros = ref(true);


// logica que verifica se deve pedir o codigo 2FA
// deve solicitar o codigo se parametro login2Fa = true && cookie nao existe cookie
export const isExecutarLogin2Fa = computed(() => {

	const cookieName = getCookieName(); // Initialize `cookieName` before using it
	const cookie = Cookies.get(cookieName);

	if (autenticado)
		return false;
	else
		return parametrosStore.login2Fa === true && cookie === undefined
});





// Guarda um cookie informando que o usuário confia no dispositivo
export const setDispositivoConfiavel = (v) => {
	const cookieName = getCookieName();
  Cookies.set(cookieName, JSON.stringify(v), { path: '/' });
}



/**
 * Verifica se tem que ir para a tela de validação do codigo
 */
export const redirectAfterLogin = async () => {

	// Garante que os parâmetros sejam carregados antes de verificar a propriedade computada
	if (isLoadingParametros.value) {
    await loadParametros();
  }

	window.location.replace(publicPath + 'home')

/* 	if ( ! isExecutarLogin2Fa.value ) {
		window.location.replace(publicPath + 'home')
	} else {
		setAutenticado(false);
		window.location.replace(publicPath + 'code')
	} */
};

// chama a função load parameter assincronicamente
async function loadParametros() {
  await parametrosStore.loadParametros();
  isLoadingParametros.value = false;
}

/**
 * Redireciona para a pagina de login
 */
export const redirectToLogin = () => {
	window.location.replace(publicPath + 'login')
};

