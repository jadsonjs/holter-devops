<template>
	<div class="card">
			<div class="font-semibold text-xl mb-4"> Gerenciamento das Permissões do Usuario no Sistema</div>
			<p class="text-ajuda">
				Adicione um ou mais papéis ao usuário selecionado, para liberar acesso a ele às funcionalidades do sistema.<br>
			</p>
	</div>
	<div class="card min-h-screen">
	
			<Fluid>
				<Panel>
					
					<loading :loading="loading"/>

				
						
						<div class="md:w-1/2">
							<div class="font-semibold text-xl">Permissões</div>
							
							<div class="card flex flex-col gap-4">
								
								<user-card :usuario="usuario" />

							</div>

							<div class="card flex flex-col gap-4">
								<div class="font-semibold text-xl">Lista de Papeis do Usuário</div>
								<div class="grid grid-cols-12 gap-2" v-for="papel of papeis" :key="papel.codigo">
									<div class="col-span-2 md:col-span-2">
										<ToggleSwitch v-model="papel.checked" />
									</div>
									<label class="flex items-center col-span-12 mb-2 md:col-span-10 md:mb-0">{{papel.descricao}}</label>
								</div>
              </div>
	
							<div class="flex justify-end space-x-4">
								<Button label="Cancelar" icon="fa-solid fa-xmark" text @click="cancelar()" :style="{ width: '10em' }" />
								<Button label="Salvar" icon="fa-solid fa-floppy-disk" @click="salvar()" :style="{ width: '15em' }"/>
							</div>
	
						</div>
	
						
					
				</Panel>
			</Fluid>
	
		</div>
	
</template>
	
<script setup>
	
import { ref, onBeforeMount } from 'vue'

// componentes
import Loading from '@/components/base/Loading.vue';
import UserCard from '@/components/base/user/UserCard.vue';

// rotas
import { useRouter, useRoute } from 'vue-router'
const router = useRouter()
const route = useRoute();

// axios
import { useHttp } from '@/utils/axiosUtils';
const { http } = useHttp();

// mensagens
import { useMessage } from '@/utils/message';
const { message } = useMessage();


///////////////  Dados do Formulario ///////////////
const usuario = ref({ ativo: true })
const isSubmitting = ref(false)
const loading = ref(false);
const papeis = ref([])
////////////////////////////////////////////////////

/** Atualiza as permissoes do usuario  */
const salvar = () => {
	isSubmitting.value = true

	// envia apenas os papeis marcados como sendo os novos papeis do usuario
	usuario.value.papeis = papeis.value.filter(papel => papel.checked === true);

	http
		.post('/api/permissoes/update', usuario.value)
		.then(() => {
			message.success('Sucesso', 'Permissões salvas com sucesso!')
			cancelar()
		}).catch((error) => {
			message.error('Erro', error.data?.messageList)
		}).finally(() => (isSubmitting.value = false))
}

const cancelar = () => {
	router.push('/admin/usuarios/list');
}

/**
 * Carrega todos os papeis do sistema
 */
const loadPapeis = async () => {
	http.get('/api/papeis/all').then((response) => {
		papeis.value = response.data
	})
}

/**
 * carrega o usuario selecionado no passo anterior
 * com a lista de papeis
 */ 
onBeforeMount( async () => {
	if (route.params.id) {
		loading.value = true
		
		await loadPapeis()	/// Todos os papeis do sistema

		http.get(`/api/usuario/get/${route.params.id}`)
			.then((response) => {
				usuario.value = response.data

				// todos os papeis do usuario sao marcado true no checkbox
				papeis.value.forEach(papel => {
					const match = usuario.value.papeis.find(uPapel => uPapel.codigo === papel.codigo);
					if (match) {
							papel.checked = true;
					}
				});

				if(! usuario.value.ativo){
					message.error('Erro', 'Usuário não está mais ativo no sistema')
					cancelar()			
				}
			}).catch((error) => {
				message.error('Erro', error.data.messageList)
			}).finally(() => (loading.value = false))

	}else{
		message.error('Erro', 'Usuário não selecionado corretamente')
		cancelar()
	}
})
	
</script>

<style lang="scss" scoped>

</style>