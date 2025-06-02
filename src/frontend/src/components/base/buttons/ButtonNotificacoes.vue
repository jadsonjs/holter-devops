<template>
	<OverlayBadge :value="qtyNotificacoes" severity="danger">
		<Button type="button" class="layout-topbar-action" text raised @click="notificacoes()">
			<i class="fa-regular fa-bell"></i>
			<!-- So mostra no menu popup (tela mobile) -->
			<Badge :value="qtyNotificacoes" severity="danger"></Badge>
			<span>Notificações</span>
		</Button>
	</OverlayBadge>
</template>

<script setup>
	import { computed } from 'vue';

	import { useNotificacaoStore } from '@/stores/notificacoes'
	const notificacaoStore = useNotificacaoStore()

	// rotas
	import { useRouter } from 'vue-router'
	const router = useRouter()


	const qtyNotificacoes = computed(() => notificacaoStore.notificacoes.length);
	
	// Dados fictícios para teste (Isso tem que vim do backend)
	if ( notificacaoStore.isCacheExpirado) {
		let notificacoes = [
			{ texto: 'Novos e-mails na sua caixa de entrada'},
			{ texto: 'Novos projetos para aprovar'},
			{ texto: 'Novos alunos para avaliar'},
			{ texto: 'Novas mensagens para ler' },
			{ texto: 'Novas solicitações de cadastro' },
		]

		notificacaoStore.setNotificacoes(notificacoes)
	}


	const notificacoes = () => {
		router.push('/notificacoes');
	}

</script>

<style lang="scss" scoped>

</style>