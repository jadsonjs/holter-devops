<!--
	 Cria um menu popUP para confirmar a remoção de um item
-->
<template>
	<div>
		<Dialog header="Confirmação" v-model:visible="display" :style="{ width: '400px' }" :modal="true">
			<div class="flex items-center justify-left">
					<i class="fa-solid fa-triangle-exclamation mr-4" style="font-size: 2rem"></i>
					<span>{{ message }}</span>
			</div>
			<template #footer>
					<Button label="Não" icon="fa-solid fa-xmark" @click="closeDialog" text severity="secondary" />
					<Button label="Sim" icon="fa-solid fa-check" @click="emitEvent" severity="danger" outlined autofocus />
			</template>
		</Dialog>
	</div>
</template>

<script setup>
import { ref } from 'vue';

const display = ref(false)

// Mensagem padrão de confirmação
const message = ref('Confirma a Remoção?')

/**
 * Abre o menu para o usuário confirmar se o usuario deve ser removido
 * @param object
 * @param customMessage (opcional) A mensagem de confirmação personalizada 
 */
const showDialog = (object, customMessage = message.value) => {
	selectedObject.value = object;
	message.value = customMessage;
  display.value = true;
};

const closeDialog = () => {
	display.value = false;
};


/**
 * O objeto que foi selecionado para ser removido no component PAI
 */
const selectedObject = ref({});

const emit = defineEmits(['confirm-delete']);

// diz ao pai que o usuario confirmou a remoção
const emitEvent = () => {
	closeDialog()
  emit('confirm-delete', { data: selectedObject.value });
};

defineExpose({
  showDialog
});

</script>

<style lang="scss" scoped>

</style>