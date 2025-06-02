<template>
	<div class="card">
		<div class="font-semibold text-xl mb-4"> Users</div>

		<!-- ajuda da página -->
		<p class="text-ajuda">
			Users can be registered to log in to the tool (if enabled) or to receive alerts via email.
		</p>

	</div>

	<div class="card min-h-screen">
		<Fluid>
			<Panel>


				<div class="card flex flex-col p-1">
					<Panel>

						<div class="flex flex-wrap items-start">
							<Button label="New User" icon="fa-solid fa-plus" severity="primary" class="ml-2"
								:style="{ width: '15em' }" @click="newUsuarioLocal" v-if="allowFormLoginLocal" />
							<!--							
							<Button label="Novo Usuário UFRN" icon="fa-solid fa-plus" severity="primary" class="ml-2"
								:style="{ width: '15em' }" @click="newUsuarioUFRN" v-if="allowFormLoginUnificado" />
						</div>

						<div class="font-semibold text-xl">Filtros</div>
						<div class="grid grid-cols-12 gap-2">

							<div class="col-span-1 md:col-span-1">
								<ToggleSwitch v-model="ativoFiltro" @change="listUsuarios()" />
							</div>
							<label for="name3" class="flex col-span-12 md:col-span-2 md:mb-0">Apenas Ativos</label>
							<IconField iconPosition="left" class="sm:col-span-12 md:col-span-6">
								<InputText type="text" v-model="nomeFiltro" placeholder="Buscar..." @input="listUsuarios()" />
								<InputIcon class="fa-solid fa-magnifying-glass" />
							</IconField>
-->
						</div>

					</Panel>
				</div>


				<loading :loading="loading" />


				<!-- listagem de usuarios -->
				<DataTable ref="dtUsers" :value="usersList">
					<template #header>
						<div class="flex flex-wrap gap-2 items-center justify-between">
							<h4 class="m-0">Users</h4>
						</div>
					</template>

					<Column field="email" header="Email" sortable style="min-width: 14rem"></Column>
					<Column header="Permissions" style="min-width: 5rem">
						<template #body="{ data }">
							<ul>
								<li v-for="per in data.permission" :key="per.id">
									{{ per.role.name }}
								</li>
							</ul>
						</template>
					</Column>
					<Column field="alert" header="Receive Alerts?" sortable style="min-width: 5rem">
						<template #body="slotProps">
							<i v-if="slotProps.data.alert" class="fa-solid fa-check"></i>
						</template>
					</Column>


					<Column field="actions" header="Actions" style="min-width: 12rem">
						<template #body="slotProps">
							<Button icon="fa-solid fa-edit" outlined rounded class="mr-2" @click="edit(slotProps.data)"
								title="Edit" />
							<Button icon="fa-solid fa-trash" outlined rounded severity="danger"
								@click="confirmDelete(slotProps.data, $event)" title="Delete" />
						</template>
					</Column>
				</DataTable>
				<!-- dialogo de confirmacao da alteracao do acesso do usuario -->
				<!---
				<confirm-dialog ref="changeDialogRef" texto="Confirma a alteração no status do usuário?"
					@confirm-event="handleconfirmAlterarAcesso" />
-->
			</Panel>
		</Fluid>

		<!-- Delete confirmation dialog -->
		<delete-dialog ref="delDialogRef" @confirm-delete="handleConfirmDelete" />
	</div>

</template>

<script setup>
import { ref, onBeforeMount } from 'vue'
import { storeToRefs } from 'pinia';

import Loading from '@/components/base/Loading.vue';
/* import ConfirmDialog from '@/components/base/ConfirmDialog.vue';
 */
import { hasRoles } from '@/utils/permissoes'

import { useParametrosStore } from '@/stores/parametros'

const parametrosStore = useParametrosStore()
const { loginMode } = storeToRefs(parametrosStore);

/* const allowFormLoginLocal = (loginMode.value === 'L' || loginMode.value === 'UL') ? ref(true) : ref(false)

const allowFormLoginUnificado = (loginMode.value === 'U' || loginMode.value === 'UL') ? ref(true) : ref(false) */

const allowFormLoginLocal = true

// rotas
import { useRouter } from 'vue-router'
const router = useRouter()

// axios
import { useHttp } from '@/utils/axiosUtils';
const { http } = useHttp();

// mensagens
import { useMessage } from '@/utils/message';
const { message } = useMessage();



/**************************************************
 *   LISTAGEM DE USUARIOS
 **************************************************/


//////   Filtros da busca  ///////
/* const ativoFiltro = ref(true)
const nomeFiltro = ref('') */

//////////////////////////////////

/* const dtUsers = ref(); // referencia ao datatable de usuarios
 */

// keep the list of users returned from back-end

const usersList = ref()

/* const paginacao = ref({
	page: 1,
	size: 10,
	total: 0,
	usuarios: [],
}); */

const loading = ref(false);


////////////////////////////////////

const newUsuarioLocal = () => {
	router.push(`/admin/usuarios/form/`);
}

/* const newUsuarioUFRN = () => {
	router.push('/admin/usuarios/ufrn')
} */

/**
 * Realizar a busca do backend da aplicação para recuperar os usuários da aplicação
 */
const listUsuarios = () => {
	loading.value = true
	http
		.get(`/api/user`)
		.then((response) => {
			usersList.value = response.data
		}).catch((error) => {
			message.error('Erro', error.data.messageList)
		}).finally(() => {
			loading.value = false
		})
}


/* // quando mudar a paginacao no datatable
const onPageChange = (event) => {
	paginacao.value.page = event.page + 1; // PrimeVue DataTable pages are zero-indexed
	paginacao.value.size = event.rows;

	// Fetch new data based on the new pagination state
	// listUsuarios();
}; */


/**************************************************
 *   Permissoes
 **************************************************/

/**
 * Edita as permissoes do usuario selecionado
 * @param aluno
 */
/* const editPermissoes = (usuario) => {
	router.push({ name: 'fromPermissoesUsuarios', params: { id: usuario.id } });
} */

const edit = (u) => {
	router.push(`/admin/usuarios/form/${u.id}`)
}



/**************************************************
 *   DELETE USER
 **************************************************/

const delDialogRef = ref(null);

/**
 * Abre o menu para o usuário confirmar se o usuário deve ser removido
 * @param user 
 */
const confirmDelete = (data) => {
	if (!hasRoles(['ADMIN'])) {
		message.error('Error', 'User has no permission: ADMIN')
		return
	}
	if (delDialogRef.value) {
		delDialogRef.value.showDialog(data, `Confirm remove user "${data.email}?"`);
	}
};

/**
 * O usuario confirmou a remoção do usuário, chama o metodo para remover
 * @param payload o usuário selecionado
 */
const handleConfirmDelete = (payload) => {
	remove(payload.data)
};


const remove = (u) => {
	loading.value = true
	http
		.delete(`/api/user/${u.id}`)
		.then(() => {
			message.success('User removed', `User ${u.email} removed successfully`)
			listUsuarios() // atualiza do backend a listagem
		}).catch((error) => {
			message.error('Error', error.data?.messageList)
		}).finally(() => (loading.value = false))
};

onBeforeMount(() => {
	listUsuarios()
})

</script>

<style lang="scss" scoped></style>