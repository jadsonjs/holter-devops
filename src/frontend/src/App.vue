<!--
  --  Universidade Federal do Rio Grande do Norte
  --  Instituto Metrópole Digital
  --  Diretoria de Tecnologia da Informação - DTI
  --
  --
  --  Componente publica da aplicação, pode ter as pagina publica do vue ou as paginas internas
  --
  --  @author Jadson Santos - jadson.santos@ufrn.br
  --  @since 30/07/2024
-->

<template>
 	
	<!-- 
		== Primevue toast message components
		== fica aqui para ser comum a parte interna como externa
	 -->
	<Toast />
	
	<router-view />
	
</template>


<script setup>
import { useLoginStore } from '@/stores/login'
import { useParametrosStore } from '@/stores/parametros'
import { useConfiguracoesStore } from '@/stores/configuracoes'
import { useCollectorsStore } from '@/stores/collectors'


import { primaryColors, surfaces, updateColors } from '@/layout/composables/useColors';

// Inicializa stores
useLoginStore()
const parametrosStore = useParametrosStore()
useConfiguracoesStore();
const collectorsStore = useCollectorsStore();

// Carrega o parametros do sistema
if(!parametrosStore.loaded){
	parametrosStore.loadParametros()
}

// Load collectors
collectorsStore.loadCollectors()

// Atualiza para o cor padrão primaria e background do store (porque a padrao do primevue is emerald)//
updateColors('primary', { name: primaryColors.value[0].name });
updateColors('surface', { name: surfaces.value[0].name});

</script>




<style scoped>
</style>
