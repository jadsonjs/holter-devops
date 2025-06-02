<template>
    <div class="flex flex-col items-center justify-center">
        <div
            style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, var(--primary-color) 10%, rgba(33, 150, 243, 0) 30%)">
            <div class="card flex justify-center w-full bg-surface-0 dark:bg-surface-900 py-5 px-8 sm:px-20"
                style="border-radius: 53px;">
                <div class="flex flex-col items-center">
                    <div class="font-bold text-xl mb-2">Autentique Sua Conta</div>
                    <p class="text-surface-500 dark:text-surface-400 block mb-8">Por favor, insira o código enviado para
                        o seu e-mail: <b>{{ ocultarEmail(usuario.pessoa.email) }} </b>. </p>
                    <InputOtp v-model="codigo2FA" :length="6" style="gap: 0" integerOnly>
                        <template #default="{ attrs, events, index }">
                            <input type="text" v-bind="attrs" v-on="events" class="custom-otp-input" />
                        </template>
                    </InputOtp>
                    <div class="flex justify-between mt-8">
                        <Button @click="validarCodigo" :disabled="loading" label="Confirmar Código"
                            class="w-full"></Button>
                    </div>
                    <br>
                    <div class="d-block text-center text-sm w-full mt-3">
                        <Checkbox v-model="confiarDispositivo" id="rememberme1" :binary="true" class="mr-5"></Checkbox>
                        <label for="rememberme1"><b> <a href="#" @click.prevent="confiarDispositivo = ! confiarDispositivo"> Não solicitar o código novamente neste dispositivo!</a> </b></label>
                    </div>

                    <br>
                    <div class="d-block text-center text-sm w-full">
                        Não recebeu o e-mail com o código?
                        <Button as="a" @click="enviarCodigo"  v-if="! loading && ! buttonDisabled" link label="SOLICITE UM NOVO" />
                        <Button link label="SOLICITE UM NOVO" v-if="loading || buttonDisabled" style="color: lightgray;"/>
                        <div v-if="buttonDisabled">
                            <p>Solicitar um novo código em {{ countdown }}s</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
// Vue Imports
import { onBeforeMount, ref } from 'vue';
import {ocultarEmail} from '@/utils/funcoes'

// Pinia Store
import { storeToRefs } from 'pinia';
import { useLoginStore } from '@/stores/login';
const loginStore = useLoginStore();
const { usuarioLogado } = storeToRefs(loginStore);
const setAutenticado = loginStore.setAutenticado;
const usuario = usuarioLogado.value;

import { setDispositivoConfiavel } from '@/utils/authentication'


// Axios
import { useHttp } from '@/utils/axiosUtils';
const { http } = useHttp();


// Message
import { useMessage } from '@/utils/message';
const { message } = useMessage();


const loading = ref(false);
const codigo2FA = ref(null);
const confiarDispositivo = ref(false);

const buttonDisabled = ref(false); // desabilita o envio do codigo
const countdown  = ref(60);        // tempo para solicitar um novo codigo

const publicPath = import.meta.env.VITE_APP_PUBLIC_PATH;



/**
 * Chama o backend para reenviar o email com o código
 */
function enviarCodigo() {

    loading.value = true;
    buttonDisabled.value = true;

    // Inicia a contagem regressiva de 60 segundos para poder solicitar um novo codigo
    let timeLeft = countdown.value;
      const interval = setInterval(() => {
        if (timeLeft > 0) {
          timeLeft--;
          countdown.value = timeLeft;
        } else {
          clearInterval(interval);
          buttonDisabled.value = false;
          countdown.value = 60;
        }
      }, 1000);

    http.post('/api/two-factor-auth/send', usuario, {
        headers: { 'Content-Type': 'application/json' }
    })
		.then(() => {
				infoMessage(usuario.pessoa.email)
		})
		.catch((error) => {
				errorMessage(error.data.messageList);
		})
		.finally(() => {
				loading.value = false;
		});
}



/**
 * Valida o código 2FA enviado pelo usuário
 */
 function validarCodigo() {
    loading.value = true;

    const twoFactorAuthCodeDTO = {
        usuarioDto: usuario,
        codigo: codigo2FA.value,
    };

    http.post('/api/two-factor-auth/validate', twoFactorAuthCodeDTO, {
        headers: { 'Content-Type': 'application/json' }
    })
		.then((response) => {
				if (response.status != 200) {
						return;
				}

				if (confiarDispositivo.value) { // se o usuario marcou que deseja confiar nesse dispositivo
						setDispositivoConfiavel(true);
				}

				setAutenticado(true);
				window.location.replace(publicPath + 'home');
		})
		.catch((error) => {
				errorMessage(error.data.messageList);
		})
		.finally(() => {
				loading.value = false;
		});
}



/**
 * Chama o backend para enviar o email com o codigo quando o componente é montado
 */
onBeforeMount(() => {
	enviarCodigo();
});


/**
 * Funções auxiliares para retornar mensagens ao usuário
 */

function infoMessage(email) {
    message.info('Código enviado', 'Um código de autenticação foi enviado para a caixa de entrada do seu e-mail: '+ocultarEmail(email));
}

function errorMessage(mensagens) {
    message.error('Erro no código 2FA', mensagens);
}

</script>

<style scoped>
.custom-otp-input {
    width: 48px;
    height: 48px;
    font-size: 24px;
    appearance: none;
    text-align: center;
    transition: all 0.2s;
    border-radius: 0;
    border: 1px solid var(--p-inputtext-border-color);
    background: transparent;
    outline-offset: -2px;
    outline-color: transparent;
    border-right: 0 none;
    transition: outline-color 0.3s;
    color: var(--p-inputtext-color);
    --p-inputtext-border-color: var(--p-form-field-border-color);
}

.custom-otp-input:focus {
    outline: 2px solid var(--p-focus-ring-color);
}

.custom-otp-input:first-child,
.custom-otp-input:nth-child(1) {
    border-top-left-radius: 12px;
    border-bottom-left-radius: 12px;
}

.custom-otp-input:nth-child(6),
.custom-otp-input:last-child {
    border-top-right-radius: 12px;
    border-bottom-right-radius: 12px;
    border-right-width: 1px;
    border-right-style: solid;
    border-color: var(--p-inputtext-border-color);
}
</style>
