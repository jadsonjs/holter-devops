<!--
  --  Universidade Federal do Rio Grande do Norte
  --  Instituto Metrópole Digital
  --  Diretoria de Tecnologia da Informação - DTI
  --
  --
  --  Componente que gera o botão dos sitemas do IMD
  --
  --  @author Jadson Santos - jadson.santos@ufrn.br
  --  @since 07/08/2024
-->


<template>

	<Button type="button" class="layout-topbar-action" text raised @click="toggleDarkMode">
		<i :class="['fa-regular ', { 'fa-moon': isDarkTheme, 'fa-sun': !isDarkTheme }]"></i>
		<span>Modo Escuro</span>
	</Button>

</template>

<script setup>
import { useLayout } from '@/layout/composables/layout';

const { toggleDarkMode, isDarkTheme } = useLayout();

</script>


<style lang="scss" scoped>

</style>
