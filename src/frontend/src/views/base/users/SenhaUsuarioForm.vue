<template>
	<div class="card">
			<div class="font-semibold text-xl mb-4"> Trocar a senha do Usuário </div>
	</div>
	<div class="card min-h-screen">
	
		<Fluid>
				<Panel>
					
					<VForm class="flex" v-slot="{ validate }">
						
						<div class="card flex flex-col gap-4 lg:w-2/5 md:w-1/2">
							<div class="font-semibold text-xl">Informação da Senha</div>
							
							<div class="flex flex-col gap-2">
								<label for="password">Senha Atual</label>
								
								<VField name="password" v-model="senhaAtual" rules="required|maxLength:40" v-slot="{ field, errors }">
										<Password v-bind="field" v-model="senhaAtual" :toggleMask="true" :feedback="false"  placeholder="Digite a senha atual" class="w-full md:w-[30rem] mb-4" fluid />
										<Message severity="error" icon="fa-solid fa-circle-exclamation" v-if="errors.length > 0">{{ errors[0] }}</Message>
								</VField>
							</div>
						
							<div class="flex flex-col gap-2">
								<label for="password2">Nova Senha</label>
								<VField name="password2" v-model="novaSenha" rules="required|maxLength:40" v-slot="{ field, errors }">
										<Password v-bind="field" v-model="novaSenha" :toggleMask="true" placeholder="Digite a nova senha" class="w-full md:w-[30rem] mb-4" fluid />
										<Message severity="error" icon="fa-solid fa-circle-exclamation" v-if="errors.length > 0">{{ errors[0] }}</Message>
								</VField>
							</div>

							<div class="flex flex-col gap-2">
								<label for="password3">Confirmar Senha</label>
								<VField name="password3" v-model="confirmacaoSenha" rules="required|maxLength:40|confirmed:password2" v-slot="{ field, errors }">
										<Password v-bind="field" v-model="confirmacaoSenha" :toggleMask="true" placeholder="Confirma a sua senha" class="w-full md:w-[30rem] mb-4" fluid />
										<Message severity="error" icon="fa-solid fa-circle-exclamation" v-if="errors.length > 0">{{ errors[0] }}</Message>
								</VField>
							</div>
	
							<div class="flex justify-end space-x-4">
								<Button label="Cancelar" icon="fa-solid fa-xmark" text @click="router.push('/home')" :style="{ width: '10em' }" />
								<Button label="Alterar Senha" icon="fa-solid fa-check" @click="showConfirm(validate)" :style="{ width: '15em' }"/>
							</div>
	
						</div>
	
					</VForm>	

					<!-- dialogo de confirmacao de remocao -->
					<confirm-dialog ref="confirmDialogRef" texto="Confirma a alteração da senha? Será necessário logar novamente" @confirm-event="handleConfirm" />
					
				</Panel>
			</Fluid>			
	
	</div>
	
</template>
	
<script setup>

	import { ref, computed, onBeforeMount } from 'vue'

	import ConfirmDialog from '@/components/base/ConfirmDialog.vue';

	// store
	import { useLoginStore } from '@/stores/login';
	const loginStore = useLoginStore();

	// rotas
	import { useRouter } from 'vue-router'
	const router = useRouter()
	
	// axios
	import { useHttp } from '@/utils/axiosUtils';
	const { http } = useHttp();

	// mensagens
	import { useMessage } from '@/utils/message';
	const { message } = useMessage();
	
	import { logout } from "@/utils/authentication"
	

	const usuarioLogado = computed(() => loginStore.usuarioLogado);

	/**
	 * Dados do formulario
	 */
	const senhaAtual = ref('')
	const novaSenha = ref('')
	const confirmacaoSenha = ref('')


	const confirmDialogRef = ref(null);

	/**
	* Abre o menu para o usuário confirmar a acao
	* @param prod 
	*/
	const showConfirm = (validate) => {
		if (confirmDialogRef.value) {
			confirmDialogRef.value.showDialog(validate);
		}
	};

	/**
	* O usuario confirmou a ação
	* @param payload o aluno selecionado
	*/
	const handleConfirm = (payload) => {
		if(payload.confirm){ // se o usuario confirmou
			salvar(payload.data)
		}
	};

	/**
	 * Altera as informações da senha no backend
	 * @param validate objeto do vee-validate para valizar os campos
	 */
	const salvar = async (validate) => {
		
		const validator = await validate()
	
		if (validator.valid) {
			http
				.post('/api/usuario/alterar-senha', {
					senhaAnterior: senhaAtual.value,
					senha: novaSenha.value,
					confirmacaoSenha: confirmacaoSenha.value,
				})
				.then(() => {
					message.success('Senha Alterada!', 'A senha foi alterada com sucesso.')
					setTimeout(() => { logout()}, 1000)
				}).catch((error) => {
					message.error('Erro', error.data.messageList)
				})
		}else{
			message.error('Erro', 'Existem informações inválidas no formulário')
		}		
	}

	const cancelar = () => {
		router.push('/home')
	}

	onBeforeMount(() => {
		if (! usuarioLogado.value.logouLocal) {
			message.warn('Alerta', 'Não é possível alterar a senha do usuário.');
			cancelar()
		}
	})
	
</script>
	
<style lang="scss" scoped>
	
</style>