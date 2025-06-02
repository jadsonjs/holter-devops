<template>
	<div class="card">
		<div class="font-semibold text-xl mb-4"> Notificações </div>

  </div>

	<div class="card min-h-screen">
		<Fluid>
			<Panel>
				<DataTable :value="notificaoStore.notificacoes" v-if="notificaoStore.notificacoes.length > 0" tableStyle="min-width: 50rem">
						<Column field="texto" header="Notificação"></Column>
						<Column style="min-width: 12rem">
							<template #body="slotProps">
									<Button icon="fa-solid fa-trash" outlined rounded severity="danger" @click="removerNotificacao(slotProps.data)" />
							</template>
					</Column>
				</DataTable>
				
				<Message icon="fa-solid fa-envelope-open" v-if="notificaoStore.notificacoes.length === 0" severity="info">Não existem notificações</Message>

				<div class="flex justify-end space-x-4">
					<Button label="Cancelar" icon="fa-solid fa-xmark" text @click="cancelar" :style="{ width: '10em' }" />
				</div>

			</Panel>
		</Fluid>	
  </div>

</template>

<script setup>

// stores
import { useNotificacaoStore } from '@/stores/notificacoes'
const notificaoStore = useNotificacaoStore()

// rotas
import { useRouter } from 'vue-router'
const router = useRouter()

/**
 * remove uma notificação da memoria
 * @param notificacao 
 */
const removerNotificacao = (notificacao) => {
	let i = notificaoStore.notificacoes.indexOf(notificacao)
	notificaoStore.notificacoes.splice(i, 1)
}

const cancelar = () => {
		router.push('/home')
}

</script>

<style lang="scss" scoped>

</style>