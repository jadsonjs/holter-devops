<template>
  <div class="layout-topbar">

			<!-- 
			== Lado esquerdo do menu (Icone e nome do sistema) 
			-->
	 		<div class="layout-topbar-logo-container">

				<!-- Botao que abre a fecha o menu lateral -->
				<Button class="layout-menu-button layout-topbar-action" text raised @click="onMenuToggle">
            		<i class="fa-solid fa-bars"></i>
        		</Button>

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
					
							<!-- Butao de  Busca nos casos de uso do sistema
							<button-busca />
							-->

							<!-- Butao de ativar o modo escuro -->
							<button-dark-mode />

							<!-- notificacoes 
							<button-notificacoes />
							-->

							<!-- Configuracoes do sistema 
							<button-configuracoes />
							-->	

					</div> <!-- layout-topbar-menu-content -->

				</div> <!-- layout-topbar-menu -->

			</div> <!-- layout-topbar-actions -->


			<!-- Menu do Usuário fica fora do div anterior, pois fica sempre fixo na tela -->
		<div id="avatar" class="layout-topbar-menu-content" style="margin: 1em;">
			<Avatar :image="userIcon" @click="handleToggleUserMenu($event);" class="p-link layout-topbar-action"
                  style="width: 2.5em; height: 2.5em;" shape="circle" />
    	</div>
			

	</div> <!-- layout-topbar -->


	


	<!-- user menu -->
	<user-menu ref="userMenuRef"/>


	
	 
</template>


<script setup>
import { ref, computed, onMounted  } from 'vue';
import { useLayout } from '@/layout/composables/layout';
import noUserImage from '@/assets/img/no-user.png';

const { onMenuToggle, toggleDarkMode, isDarkTheme } = useLayout();

// componentes in the header
import UserMenu from '@/components/base/menus/UserMenu.vue';

// import ButtonBusca         from '@/components/base/buttons/ButtonBusca.vue';
import ButtonDarkMode      from '@/components/base/buttons/ButtonDarkMode.vue';
import ButtonNotificacoes  from '@/components/base/buttons/ButtonNotificacoes.vue';
// import ButtonConfiguracoes from '@/components/base/buttons/ButtonConfiguracoes.vue';

const appName = import.meta.env.VITE_APP_PUBLIC_PATH.replace(/[/]+/g, '').toUpperCase()


// store
import { useLoginStore } from '@/stores/login';
const loginStore = useLoginStore();


const userIcon = computed(() => {
  const urlFoto = loginStore.usuarioLogado?.pessoa?.urlFoto;
  return urlFoto ? urlFoto : noUserImage;
});





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

///////////////  Controla o menu do usuario  //////////////////

const userMenuRef = ref(null);

// chama a funcao toggleUserMenu do component UserMenu
const handleToggleUserMenu = (event) => {
  userMenuRef.value.toggleUserMenu(event);
};



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