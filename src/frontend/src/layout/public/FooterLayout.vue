<template>
  <div class="layout-footer bg-sky-800 dark:bg-surface-950">
	 	<div class="left">
			<span style="color: white;">CASE - Collaborative & Automated Software Engineering - <a href="http://caseufrn.github.io" target="_blank">http://caseufrn.github.io</a> </span>
			<br>
		</div>	


		<div class="right">
			
			<div class="creditos text-center mt-5 mb-4 my-sm-0">
				<div class="text-wrapper text-white">
					
				</div>
			</div>
			
			<!-- Permite recarregar parametros caso o backend mude os valores, usado em testes e desenvolvimento -->
			<span style="font-size: xx-small; padding-left: 10px;"  v-if="environment !== ''">  <a href="#" @click.prevent="reload">Recarregar Parâmetros</a> </span>

		</div>


  </div>

</template>

<script setup>

import { useParametrosStore } from '@/stores/parametros'
const parametrosStore = useParametrosStore()

import { computed } from 'vue';

const currentYear = computed(() => new Date().getFullYear() );

const environment =  import.meta.env.VITE_APP_ENVIRONMENT


const reload = () => {
  parametrosStore.reload();
};

</script>

<style lang="scss" scoped>
.left {
  /* Align to the left */
  text-align: left;
  padding: 0 1rem 0 1rem;
	width: 100%;
}

.right {
  /* Align to the right  */
  text-align: right;
  padding: 0 1rem 0 1rem;
	width: 100%;
}

.left a{
	display: inline-block;
}

.right a{
	display: inline-block;
}


// estido do projeto antigo


	.ufrn-assinatura {
		align-items: center;
		display: flex;
		opacity: 0.85;

		img {
			width: 100px;
			height: auto;

			@media (min-width: 1200px) {
				width: 176px;
			}
		}

		.logo-a-direita {
			height: 20px;
			width: auto;

			@media (min-width: 1200px) {
				height: 38px;
			}
		}

		.text-assinatura {
			font-family: 'Liberation Sans';
			font-size: 1.3rem;
			font-weight: bold;
			opacity: 0.85;

			@media (min-width: 1200px) {
				font-size: 1.9rem;
			}
		}
	}

	.creditos {
		.logos-wrapper {
			img {
				width: 96px;
				height: auto;

				@media (min-width: 1200px) {
					width: 120px;
				}
			}
		}

		.text-wrapper {
			opacity: 0.85;
			font-size: 0.65rem;

			@media (min-width: 1200px) {
				font-size: 1rem;
			}
		}
	}

	.copyright-text {
		font-size: 0.65rem;
		opacity: 0.25;
		text-align: center;

		@media (min-width: 576px) {
			position: absolute;
			bottom: -4px;
			left: 12px;
		}

		@media (min-width: 1200px) {
			font-size: 1rem;
		}
	}


</style>


