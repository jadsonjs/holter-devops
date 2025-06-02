/**
 * Universidade Federal do Rio Grande do Norte
 * Instituto Metrópole Digital
 * Diretoria de Tecnologia da Informação
 *
 *
 * Guarda os dados do usuario logado para autenticação.
 *
 * @author Jadson Santos - jadson.santos@ufrn.br
 * @since  29/07/2024
 */
import { ref, computed} from 'vue';
import { defineStore } from 'pinia'
import Cookies from 'js-cookie';
import { objectIsEmpty } from "@/utils/funcoes.js";

const storeID = 'login'
const appName = import.meta.env.VITE_APP_PUBLIC_PATH.replace(/[/]+/g, '')
const storeKey = `${appName}-${storeID}-store`

const cookieNames = {
	usuarioLogado: `${storeKey}-usuarioLogado`,
	autenticado: `${storeKey}-autenticado`,
	tokenAcesso: `${storeKey}-tokenAcesso`
};

export const useLoginStore = defineStore(storeKey, () => {

    // state properties
    const usuarioLogadoState = ref({})
    const autenticadoState = ref(false)
		const tokenAcessoState = ref('')
    
    /**
     * safari do not allow cookies. I dont know why ... is safari new IE?
     */
    const safari = isSafari()
    
    // loading data to store
    // Check if the authentication cookie exists on initialization
    if(! safari){
			autenticadoState.value   = Cookies.get(cookieNames.autenticado) === 'true' || false;
			tokenAcessoState.value   = Cookies.get(cookieNames.tokenAcesso) || '';
      usuarioLogadoState.value = JSON.parse(Cookies.get(cookieNames.usuarioLogado) || '{}');
    }else{
			autenticadoState.value   = localStorage.getItem(cookieNames.autenticado) === 'true'  || false;
			tokenAcessoState.value   = localStorage.getItem(cookieNames.tokenAcesso) || '';
      usuarioLogadoState.value = JSON.parse( localStorage.getItem(cookieNames.usuarioLogado) || '{}');
    }


    // getters
    const usuarioLogado = computed(() => usuarioLogadoState.value)
    const autenticado  = computed(() => autenticadoState.value)
		const tokenAcesso = computed(() => tokenAcessoState.value)

    function isUsuarioLogado() {
      return ! objectIsEmpty(usuarioLogado.value)
    }
    
    // actions
    function setUsuarioLogado(v) {
			usuarioLogadoState.value = v;
      // Set or clear the cookie based on the authenticated state
      // secure: true This option ensures that the cookie is only sent to the server over secure HTTPS
      // sameSite: 'strict' estricts how cookies are sent with cross-site requests. When set to 'strict',
      // it means the cookie will only be sent in a first-party context and not sent along with requests
      // initiated by third-party websites. This helps mitigate the risk of Cross-Site Request Forgery (CSRF) attacks.
      
      if(safari){
        localStorage.setItem(cookieNames.usuarioLogado, JSON.stringify(v));
      }else{
        Cookies.set(cookieNames.usuarioLogado, JSON.stringify(v), { secure: true, sameSite: 'Strict' });
      }
    }

    function setAutenticado(v) {
			autenticadoState.value = Boolean(v);
      // Set or clear the cookie based on the authenticated state
      if(safari){
        localStorage.setItem(cookieNames.autenticado, autenticadoState.value);
      }else{
        Cookies.set(cookieNames.autenticado, autenticadoState.value, { secure: true, sameSite: 'Strict' });
      }
    }
		
		function setTokenAcesso(v) {
			tokenAcessoState.value = v;
			// Set or clear the cookie based on the authenticated state
      if(safari){
        localStorage.setItem(cookieNames.tokenAcesso, v);
      }else{
        Cookies.set(cookieNames.tokenAcesso, v, { secure: true, sameSite: 'Strict' });
      }

		}



    // detect if the user is using Safari by checking the user agent string
    function isSafari() {
      return /^((?!chrome|android).)*safari/i.test(navigator.userAgent);
    }

		
		function updateDadosPessoais(pessoa) {
			usuarioLogadoState.value.pessoa = pessoa;
			setUsuarioLogado(usuarioLogadoState.value)
		}



		/**
     * Save information on store
     */
		function login(user) {
      console.log(user)
			setUsuarioLogado(user);
			setTokenAcesso(user.jwtToken);
			setAutenticado(true);
		}


    /**
     *  Clear store and localSorage information
     */
    function logout() {
			setUsuarioLogado({});
      setAutenticado(false);
			setTokenAcesso('');
			Cookies.remove(cookieNames.usuarioLogado);
			Cookies.remove(cookieNames.tokenAcesso);
			Cookies.remove(cookieNames.autenticado);
			localStorage.clear()
    }

    return {
			usuarioLogado,
			autenticado,
			tokenAcesso,
      isUsuarioLogado,
			updateDadosPessoais,
			setTokenAcesso,
			login,
			logout,
			setAutenticado,
			isSafari}
})