<!--
	 Cria um menu popUP para confirmar alguma ação
-->
<template>
	<div>
		<Dialog header="Confirmação" v-model:visible="display" :style="{ width: '400px' }" :modal="true">
			<div class="flex items-left justify-left">
					<i class="fa-solid fa-circle-info" style="font-size: 2rem; padding: 1em;"></i>
					<span>{{texto}}</span>
			</div>
			<template #footer>
					<Button label="Não" icon="fa-solid fa-xmark" @click="closeDialog" text severity="secondary" />
					<Button label="Sim" icon="fa-solid fa-check" @click="emitEvent" severity="info" outlined autofocus />
			</template>
		</Dialog>
	</div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  texto: {
    type: String,
    required: true
  }
});

const display = ref(false)

/**
 * Guarda alguma informacao passada
 */
 const data = ref({});

/**
 * Abre o menu para o usuário confirmar se o usuario deve ser removido
 * @param prod 
 */
const showDialog = (d) => {
	data.value = d;
  display.value = true;
};

const closeDialog = () => {
	display.value = false;
};

const emit = defineEmits(['confirm-event']);

// diz ao pai que o usuario confirmou a remoção
const emitEvent = () => {
	closeDialog()
  emit('confirm-event', { 
  	confirm: true, 
  	data: data.value 
	}); 
};

defineExpose({
  showDialog
});

</script>

<style lang="scss" scoped>

</style>