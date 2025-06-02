<!--
  --  Universidade Federal do Rio Grande do Norte
  --
  --
  --  Tela de login da aplicação, possui 2 opções: 1 - login pela api da ufrn, 2 - form de login local
  --
  --  @author Jadson Santos - jadson.santos@ufrn.br
  --  @since 08/08/2024
-->


<template>

	<!-- 
		== Parte de Login pelo SSO da UFRN 
		-->
<div class="flex flex-col items-center justify-center">
  
	<div class="flex space-x-4">

	<div class="flex flex-col items-center justify-center" v-if="! showFormLoginLocal && allowFormLoginUnificado">
		<div style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, var(--primary-color) 10%, rgba(33, 150, 243, 0) 30%)">
			<div class="w-full bg-surface-0 dark:bg-surface-900 py-5 px-8 sm:px-20" style="border-radius: 53px">
				<div class="text-center mb-8">				
					<div class="text-surface-900 dark:text-surface-0 text-3xl font-medium mb-4">Bem-vindo ao {{appName}} </div>
				</div>
				<div class="card-simple mt-4">
						<div class="card-body p-0 m-0 d-flex align-items-center my-2 mx-2">
								<a class="py-2 px-2 link-auth font-weight-semibold" :href="isUserBlocked ? 'javascript:void(0)' : urlSSOUFRN" :class="{ 'disabled-link': isUserBlocked }">
										<span class="text-primary">Usar as credenciais da</span>
										<svg style="display: inline; margin: 5px;" data-name="Grupo 4478" height="18.844" id="Grupo_4478" viewBox="0 0 64.969 18.844" width="64.969" xmlns="http://www.w3.org/2000/svg">
											<path class="logo-icone" d="M17.074,36.465a7.806,7.806,0,0,1-2.251,5.123c-1.478,1.31-3.8,1.965-6.97,1.965-3.208,0-5.408-.588-6.584-1.78Q-.368,40.135.06,36.482l.974-8.448H5.871L4.9,36.482a4.188,4.188,0,0,0,.6,3.074,2.9,2.9,0,0,0,2.486,1.108,4,4,0,0,0,2.7-.957,4.836,4.836,0,0,0,1.545-3.242l.974-8.448h4.938l-1.075,8.448Zm47.867-8.448L63.177,43.166H56.61L50.916,32.9,49.724,43.166H40.05l.134-4.367a2.1,2.1,0,0,0-.286-.823,2.469,2.469,0,0,0-.689-.6.239.239,0,0,0-.1-.034q-.731,0-2.368-.05-1.713-.05-1.814-.05l-.689,5.912H29.452L31.333,28h9.859a6.429,6.429,0,0,1,3.544.957A3.044,3.044,0,0,1,46.3,32.031a3.583,3.583,0,0,1-1.243,2.3,5.989,5.989,0,0,1-2.5,1.276l-.017.185a4.52,4.52,0,0,1,1.142.269c1.176.437,1.444,1.176,1.562,2.435l.034,4.3,1.713-14.78h6.886l5.408,10.027,1.159-10.027h4.5ZM40.5,31.342a2.693,2.693,0,0,0-1.629-.521H35.633l-.42,3.678h3.275A3.194,3.194,0,0,0,40.3,34a1.9,1.9,0,0,0,.84-1.411A1.286,1.286,0,0,0,40.5,31.342Z" data-name="Caminho 338" fill="#0095db" fill-rule="evenodd" id="Caminho_338" transform="translate(0.029 -26.791)"></path>
											<path class="logo-icone-dark" d="M101.127,31.314l-.672,5.862H95.7l-.3,2.469h13.151L110.869,20.8H97.718l-.151,1.226h13.151l-.386,3.04h-8.465L101.5,28.24h7.591l-.353,3.074Z" data-name="Caminho 339" fill="#164194" fill-rule="evenodd" id="Caminho_339" transform="translate(-79.372 -20.8)"></path>
										</svg>
								</a>
						</div>
				</div>
			</div>
		</div>
	</div>


	<!--
		== Formulario de Login no banco local da aplicação
	 -->

	<div v-focustrap class="flex flex-col items-center justify-center" v-if="showFormLoginLocal && allowFormLoginLocal">
			<div style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, var(--primary-color) 10%, rgba(33, 150, 243, 0) 30%)">
					<div class="w-full bg-surface-0 dark:bg-surface-900 py-5 px-8 sm:px-20" style="border-radius: 53px">
							<div class="text-center mb-8">
									<!-- <img :src="appIcon" alt="logo" class="mb-8 w-16 shrink-0 mx-auto" /> -->
									<div class="text-surface-900 dark:text-surface-0 text-3xl font-medium mb-4">Bem-vindo ao {{appName}} </div>
									<!-- <span class="text-muted-color font-medium">Entre para Continuar</span> -->
							</div>

							<div>
									<label for="email1" class="block text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">Login</label>
									<InputText id="email1" type="text" placeholder="Login, E-mail ou CPF" class="w-full md:w-[30rem] mb-8" v-model="usuario.email" />

									<label for="password1" class="block text-surface-900 dark:text-surface-0 font-medium text-xl mb-2">Senha</label>
									<Password id="password1" placeholder="Senha" :toggleMask="true" class="w-full md:w-[30rem] mb-4" fluid :feedback="false" v-model="usuario.password" @keyup.enter="logar()" ></Password>

									<div class="flex items-center justify-between mt-2 mb-8 gap-8">
											<div class="flex items-center">
											<!--
													<Checkbox v-model="checked" id="rememberme1" binary class="mr-2"></Checkbox>
													<label for="rememberme1">Remember me</label>
											-->
											</div>
<!--
											<span class="font-medium no-underline ml-2 text-right cursor-pointer text-primary">
												<Button as="a" link label="Esqueceu a senha?" :href="`${backendURL}/public/usuario/init/novasenha`"/>
											</span>
-->
									</div>
									<Button ref="loginButton" label="Entrar" class="w-full" @click="logar()" :disabled="isUserBlocked || isSubmiting"></Button>
							</div>
							<br>
							<div v-if="permiteAutoCadastro === true" class="d-block text-center text-sm w-full">
							Ainda não tem cadastro?  <Button as="a" link label="CADASTRE-SE" :href="`${backendURL}/public/usuario/cadastro/init`" /> <br>
							</div>
							<div v-if="permiteAutoCadastro === true" class="d-block text-center text-sm w-full">
							Não recebeu o e-mail de confirmação?
							<Button as="a" link label="SOLICITE UM NOVO"  :href="`${backendURL}/public/usuario/solicitar/reenvio`" /> <br>
							</div>

					</div>
			</div>
	</div>

	</div>

	<div class="flex flex-col text-center text-sm w-full" v-if="loginMode === 'UL'">
		<span class="font-medium no-underline ml-2 cursor-pointer text-primary" @click="showFormLoginLocal = !showFormLoginLocal">
		{{!showFormLoginLocal ? 'Não possui conta da UFRN? Use a Login Local' : 'Usar Login da UFRN' }}
			<i class="fa-solid fa-angle-right" v-if="showFormLoginLocal === false "> </i>
			<i class="fa-solid fa-angle-left" v-if="showFormLoginLocal === true "> </i>
		</span>
	</div>

	<div class="flex flex-col text-center text-sm w-full" v-if="!enableLoginPage">
		<span class="font-medium no-underline ml-2 cursor-pointer text-primary" @click="redirectHome">
		{{'Acessar o Sistema sem Login'}}
		<i class="fa-solid fa-angle-right"> </i>
		</span>
	</div>

	<!-- wrong login attempts area -->
	<div class="flex flex-col text-center text-sm w-full mt-8">
		<p v-if="isUserBlocked">
			Tentativa inválida de login. Por favor, espere  <Tag severity="primary" :value="countdownText"></Tag> para tentar novamente.
		</p>
	</div>

	<!-- secury alert area -->
	<div class="flex flex-col text-center text-sm w-full mt-8">
		<div v-if="showSecurityAlert">
			Espere, verificando a segurança da conexão... <ProgressSpinner style="width: 20px; height: 20px" strokeWidth="8" />
		</div>
		<div v-if="!showSecurityAlert && !isSecure">
			<Message severity="error" icon="fa-solid fa-triangle-exclamation">A conexão não é segura. Sua senha pode ser exposta</Message>
		</div>
	</div>

</div>

</template>

<script setup>
import { ref, computed, onBeforeMount } from 'vue';
import { storeToRefs } from 'pinia';

const appName = import.meta.env.VITE_APP_PUBLIC_PATH.replace(/[/]+/g, '').toUpperCase()

const backendURL = import.meta.env.VITE_APP_BACKEND_BASE_URL
const publicPath = import.meta.env.VITE_APP_PUBLIC_PATH;

import { logarLocal, /* logarUnificado */ } from '@/utils/authentication'

import { lockTimeLoginStore } from '@/stores/lockTimeLogin.js';

import { useParametrosStore } from '@/stores/parametros'
const parametrosStore = useParametrosStore()

const { loginMode } = storeToRefs(parametrosStore);
const { permiteAutoCadastro } = storeToRefs(parametrosStore);
const { enableLoginPage } = storeToRefs(parametrosStore);

const redirectURL = computed(() => `${parametrosStore.appUrl}${publicPath}login`)
const urlSSOUFRN =  computed(() => `${parametrosStore.authenticationURL}authz-server/oauth/authorize?client_id=${parametrosStore.clientID}&response_type=code&redirect_uri=${redirectURL.value}`)


/* const allowFormLoginLocal = ( loginMode.value === 'L' || loginMode.value === 'UL' ) ? ref(true) : ref(false)
const allowFormLoginUnificado = ( loginMode.value === 'U' || loginMode.value === 'UL' ) ? ref(true) : ref(false) */

const allowFormLoginLocal = true
const allowFormLoginUnificado = false

// se so tem o login local, aparece obrigatoriamente, não o login da UFRN eh predominante
/* const showFormLoginLocal = loginMode.value === 'L' ? ref(true) : ref(false)
 */
const showFormLoginLocal = true

const showSecurityAlert = ref(true);
const isSecure = window.location.protocol === 'https:';

// dados do usuario para login local
const usuario = ref({
  email: '',
  password: ''
});


// Ref to the button element
const isSubmiting = ref(false);  // Control the button state


// realiza o login usando a base local do sistema (sem usar o login unificado da API da UFRN)
const logar = async (event) => {
	 // Check if the button is disabled and Disable the button immediately after the first click
	if (isSubmiting.value) return;
	isSubmiting.value = true;

	try {
		await logarLocal(usuario.value)
	} finally {
    	setTimeout(() => { isSubmiting.value = false; }, 1000);  // timeout to avoid double-clicking too fast
  	}
}


// Computed property to check if the login is blocked
const isUserBlocked = computed(() => lockTimeLoginStore.lockTimeRemaining > 0);

// Computed property to format the countdown timer
const countdownText = computed(() => {
	const minutes = Math.floor(lockTimeLoginStore.lockTimeRemaining / 60);
	const seconds = lockTimeLoginStore.lockTimeRemaining % 60;
	return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
});


const redirectHome = () => {
	window.location.replace(publicPath + 'home')
}

onBeforeMount(() => {

	// validacao se esta vindo redirecionado do SSO da UFRN apos o login la
	// realiza o login unificado
/* 	if (window.location.toString().includes('code')) {
		const url = window.location.toString()
		const code = url.substring(url.lastIndexOf("=") + 1);
		logarUnificado(code, redirectURL);
	} */

	if(! parametrosStore.loaded && ! parametrosStore.error ){
		// Carrega o parametros do sistema
		parametrosStore.loadParametros()
		// Delay the redirect by 2 seconds
		setTimeout(() => { window.location.replace(redirectURL.value); }, 2000);
	}

	setTimeout(() => {showSecurityAlert.value = false;}, 500);

	// if the user if block in login: When the user press F5 and refresh the page we need to start again
	if (isUserBlocked) { lockTimeLoginStore.startCountDownLockTimer(); }

})

</script>

<style scoped>
.pi-eye {
    transform: scale(1.6);
    margin-right: 1rem;
}

.pi-eye-slash {
    transform: scale(1.6);
    margin-right: 1rem;
}

/* when user is block to login */
.disabled-link {
  color: gray;
  pointer-events: none; /* Disable interaction */
  cursor: not-allowed;  /* Change the cursor to indicate it's disabled */
}
</style>

