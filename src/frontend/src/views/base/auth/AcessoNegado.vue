<template>
   
	<div class="flex flex-col items-center justify-center">
			
			<div style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, color-mix(in srgb, var(--primary-color), transparent 60%) 10%, var(--surface-ground) 30%)">
					<div class="w-full bg-surface-0 dark:bg-surface-900 py-20 px-8 sm:px-20 flex flex-col items-center" style="border-radius: 53px">
						<img src="/icon.svg" alt="Icon" style=" width: 50px; height: 50px;">
							<span class="text-primary font-bold text-3xl">403</span>
							<h1 class="text-surface-900 dark:text-surface-0 font-bold text-3xl lg:text-5xl mb-2"> Acesso Negado</h1>
							<div class="text-surface-600 dark:text-surface-200 mb-8"> Você não tem permissão para acessar essa operação </div>
							<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 448 512" width="100px" height="100px" fill="#EF4444"><!--!Font Awesome Free 6.6.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.--><path d="M144 144l0 48 160 0 0-48c0-44.2-35.8-80-80-80s-80 35.8-80 80zM80 192l0-48C80 64.5 144.5 0 224 0s144 64.5 144 144l0 48 16 0c35.3 0 64 28.7 64 64l0 192c0 35.3-28.7 64-64 64L64 512c-35.3 0-64-28.7-64-64L0 256c0-35.3 28.7-64 64-64l16 0z"/></svg>
							<br><br><br><br><br>
							<Button as="router-link" label="Voltar para a página inicial" icon="fa-solid fa-house" to="/home" > </Button>
					</div>
			</div>
	</div>
    
</template>

<script setup>

</script>

<style lang="scss" scoped>

</style>