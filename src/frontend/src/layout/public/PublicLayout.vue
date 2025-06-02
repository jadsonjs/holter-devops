<!--
  --  Universidade Federal do Rio Grande do Norte
  --  Instituto Metrópole Digital
  --  Diretoria de Tecnologia da Informação - DTI
  --
  --
  --  Layout das página publica em vue (Login, Cadastro do Usuário)
  --
  --  @author Jadson Santos - jadson.santos@ufrn.br
  --  @since 07/08/2024
-->

<template>
		<header-layout />
		
    <div class="bg-surface-50 dark:bg-surface-950 flex items-center justify-center min-h-[89vh] overflow-hidden">
        
				<!-- 
				== As paginas publicas da aplicação incluídas pela rotas
				-->
				<router-view></router-view>

    </div>
		
		<footer-layout />

</template>

<script setup>
import HeaderLayout from '@/layout/public/HeaderLayout.vue';
import FooterLayout from '@/layout/public/FooterLayout.vue'

</script>


<style scoped>

</style>
