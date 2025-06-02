<!--
  --  Universidade Federal do Rio Grande do Norte
  --
  --
  --  Cabecalho da parte publica
  --
  --  @author Jadson Santos - jadson.santos@ufrn.br
  --  @since 07/08/2024
-->


<template>
  <div class="layout-topbar">

			<!-- 
			== Lado esquerdo do menu (Icone e nome do sistema) 
			-->
	 		<div class="layout-topbar-logo-container">

				<router-link to="/" class="layout-topbar-logo">
						<!-- O icone da aplicacao -->
						<div class="app-icon"></div>

						<!-- O nome da aplicação -->
						<span>{{appName}}</span>
				</router-link>

			</div> <!--layout-topbar-logo-container -->






			<!-- 
			== Botoes de acao no lado direito do menu
			-->
			<div class="layout-topbar-actions">

				<!-- 
					== Botão dos 3 pontos "..." aparece quando a tela está reduzida
				-->	
				<Button id="buttomMobile" class="layout-topbar-menu-button layout-topbar-action" text raised @click="toggleVisibilityToolBarMenu">
          <i class="fa-solid fa-ellipsis-vertical"></i>
        </Button>

				
			
				<!-- 
				== Botão no cabecalho que aparecem quando a tela está grande e somem quando pequena 
				-->
				<div
					:class="{'layout-topbar-menu hidden lg:block': true, 'hidden-topbar-menu': isHiddenToolBarMenu}">
					
					<div class="layout-topbar-menu-content">

							<button-dark-mode/>

					</div> <!-- layout-topbar-menu-content -->

				</div> <!-- layout-topbar-menu -->

			</div> <!-- layout-topbar-actions -->
			

	</div> <!-- layout-topbar -->

	 
</template>


<script setup>
import { ref, computed, onMounted, onBeforeUnmount  } from 'vue';
import appIcon from '/icon.svg';

import ButtonDarkMode from '@/components/base/buttons/ButtonDarkMode.vue'


const appName = import.meta.env.VITE_APP_PUBLIC_PATH.replace(/[/]+/g, '').toUpperCase()


/*
 * Tool bar menu é o menu onde ficam os botoes. 
 * quando a tela ta grande, ele eh sempre visivel, quando tá pequena, eh controlado pelo click do botao
 * hidden-topbar-menu diplay flex em tela grande e none em pequenas, ver _topbar.scss
 */
const isHiddenToolBarMenu = ref(true);

const toggleVisibilityToolBarMenu = () => {
  isHiddenToolBarMenu.value = !isHiddenToolBarMenu.value;
}


// Click outside Mobile menu, close the menu is it is open
const handleClickOutside = (event) => {
	if(! isHiddenToolBarMenu.value ){
		const buttomMobile = document.querySelector('#buttomMobile');
		if ( ! (event.target === buttomMobile )  ) {
			isHiddenToolBarMenu.value = true;
		}
	}
};

// add a event on mouse click
onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});


</script>


<style lang="scss" scoped >
 
 // icone da aplicação no lado esquedo do header
 .app-icon {
    width: 30px;
    height: 30px;
    background: url('/icon.svg') no-repeat center;
    -webkit-mask: url('/icon.svg') no-repeat center;
    -webkit-mask-size: contain;
    mask: url('/icon.svg') no-repeat center;
    mask-size: contain;
    background-color: var(--primary-color);
  }

</style>