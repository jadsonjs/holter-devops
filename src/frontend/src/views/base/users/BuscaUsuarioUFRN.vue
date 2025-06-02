<template>
	<div class="card">
			<div class="font-semibold text-xl mb-4"> Cadastro de usuário da UFRN </div>
			<p class="text-ajuda">
				Busque os usuários na base da UFRN.<br>
			</p>
	</div>
	<div class="card min-h-screen">
	
			<Fluid>
				<Panel>
	
					<div class="md:w-1/2">
						<div class="font-semibold text-xl">Dados do Usuário</div>
						
						<div class="flex flex-col gap-4">
							<label for="inputCPF">CPF:</label>
							<InputText id="inputCPF" v-model="usuario.pessoa.login" v-maska="'###.###.###-##'" placeholder="999.999.999-99"/>
						</div>
						<div class="flex flex-col gap-4">
							<label for="inputLogin">login:</label>
							<InputText id="inputLogin" v-model="usuario.login"  placeholder="joao123"/>
						</div>
						<div class="flex justify-end space-x-4 mt-4">
							<Button label="Cancelar" icon="fa-solid fa-xmark" text @click="cancelar()" :style="{ width: '10em' }" />
							<Button label="Buscar" icon="fa-solid fa-magnifying-glass" @click="buscar()" :disabled="loading" :style="{ width: '15em' }"/>
						</div>

					</div>

					<loading :loading="loading"/>

					<DataTable :value="usuarios" v-if="usuarios.length > 0" tableStyle="min-width: 50rem">
							<Column header="CPF">
								<template #body="slotProps">
									{{ ocultarCPF(slotProps.data.cpf) }}
        				</template>
							</Column>
							<Column field="login" header="Login"></Column>
							<Column field="nome" header="Nome"></Column>
							<Column field="email" header="E-mail"></Column>
							<Column :exportable="false" style="min-width: 12rem">
								<template #body="slotProps">
										<Button icon="fa-solid fa-floppy-disk" outlined rounded class="mr-2" @click="cadastrar(slotProps.data)" />
								</template>
							</Column>
					</DataTable>
					
				</Panel>
			</Fluid>
	
		</div>
	
</template>
	
<script setup>
	
	import { ref } from 'vue'
	import { ocultarCPF} from '@/utils/funcoes'
	
	import Loading from '@/components/base/Loading.vue';

	// rotas
	import { useRouter } from 'vue-router'
	const router = useRouter()

	// axios
	import { useHttp } from '@/utils/axiosUtils';
	const { http } = useHttp();

	// mensagens
	import { useMessage } from '@/utils/message';
	const { message } = useMessage();
	
	const usuario = ref({ pessoa: {} })
	const loading = ref(false);

	const usuarios = ref([]) // a lista de usuarios

	/**
	 * Busca o usuario na API da UFRN a partir do CPF ou login
	 */
	const buscar = () => {
		loading.value = true
		http.post('/api/usuario/buscar/api', usuario.value)
			.then((response) => {
				usuarios.value = response.data
				if (usuarios.value.length === 0){
					message.warn('Atenção', 'Nenhum usuário encontrado com esses dados')
				}
			}).catch((error) => {
				message.error('Erro', error.data.messageList)
			}).finally(() => {
				loading.value = false
			})
	}
	

	/**
	 * Cadastra o usuario na base local do sistema
	 * @param validate 
	 */
	const cadastrar = (usuario) => {
		loading.value = true
		http.post('/api/usuario/cadastrar/unificado', usuario)
			.then(() => {
				message.success('Sucesso', 'Usuário adicionado à base do sistema')
				cancelar()
			}).catch((error) => {
				message.error('Erro', error.data.messageList)
			}).finally(() => {
				loading.value = false
			})		
	}

	const cancelar = () => {
		router.push('/admin/usuarios/list')
	}
	
</script>
	
<style lang="scss" scoped>
	
</style>