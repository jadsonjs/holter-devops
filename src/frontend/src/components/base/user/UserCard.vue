<template>
	
	<div class="flex justify-center w-full">
		<Card style="width: 50rem; overflow: hidden" v-if="usuario">
				<template #header>
						<img alt="user header" src="@/assets/img/no-user.png" style=" width: 50px; height: 50px;" />
				</template>
				<template #title>Usuário Selecionado</template>
				
				<template #content>
						<p class="mb-2">
							<label class="mr-8 ">Nome: </label>
							<span class=""> {{usuario.pessoa?.nome}} </span>
						</p>
						<p class="mb-2">
							<label class="mr-8 ">CPF: </label>
							<span class=""> {{ocultarCPF(usuario.pessoa?.cpf)}} </span>
						</p>
						<p class="mb-2">
							<label class=" mr-8 ">E-mail: </label>
							<span class=""> {{usuario.pessoa?.email}} </span>
						</p>
						<p class="mb-2">
							<label class="mr-8 ">Data de Nascimento: </label>
							<span class=""> {{ ocultarDataNascimento(usuario.pessoa?.dataNascimento)}} </span>
						</p>
						<p class="mb-2">
							<label class="mr-8 ">Login: </label>
							<span class=""> {{usuario.login ? usuario.login : usuario.loginUnificado }} </span>
						</p>
				</template>
		</Card>
	</div>

</template>

<script setup>

import { ocultarCPF, ocultarDataNascimento} from '@/utils/funcoes'

const props = defineProps({
  usuario: {
    type: Object,
    required: true
  }
});

</script>

<style lang="scss" scoped>


</style>