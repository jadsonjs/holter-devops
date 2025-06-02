<template>
		<!-- Abre o menu lateral de configuracoes da aplicacao -->
		<Button class="layout-topbar-action" type="button" text raised  @click="configPanelTransition()">
			<i class="fa-solid fa-screwdriver-wrench"></i>
			<span>Configurações</span>
		</Button>


		<!-- System configuration side bar --> 
  	<app-configurator v-model:visible="configPanelVisible" />

</template>

<script setup>

import { ref } from 'vue';

import AppConfigurator from '@/components/base/AppConfigurator.vue';

/*
 * Controla o menu lateral de configurações
 */
const configPanelVisible = ref(false);

const configPanelTransition = () => {
	configPanelVisible.value = true
}

</script>

<style lang="scss" scoped>

</style>