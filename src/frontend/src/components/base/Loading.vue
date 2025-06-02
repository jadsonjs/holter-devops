<!--
  --  Universidade Federal do Rio Grande do Norte
  --  Instituto Metrópole Digital
  --  Diretoria de Tecnologia da Informação - DTI
  --
  --  <loading show:"true">
  --
  --  @author Jadson Santos - jadson.santos@ufrn.br
  --  @since 19/08/2024
-->
<template>
	<div class="text-center" v-if="loading">
		Aguarde ...
		<ProgressSpinner style="width: 30px; height: 30px" strokeWidth="4" fill="transparent"
    animationDuration=".7s" aria-label="Aguarde ..." />
	</div>
</template>

<script setup>

defineProps({
  loading: Boolean,
})

</script>

<style lang="scss" scoped>

</style>