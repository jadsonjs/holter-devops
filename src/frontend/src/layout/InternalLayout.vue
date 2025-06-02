<!--
  --  Universidade Federal do Rio Grande do Norte
  --  Instituto Metrópole Digital
  --  Diretoria de Tecnologia da Informação - DTI
  --
  --
  --  Layout interno da aplicação (cabecalho, conteudo, menu lateral e rodape).
  --
  --  @author Jadson Santos - jadson.santos@ufrn.br
  --  @since 30/07/2024
-->
<template>

    <div class="layout-wrapper" :class="containerClass" v-if="autenticado || !enableLoginPage">

		<header-layout />

		<side-bar-layout />

		<main class="layout-main-container">

			<div class="layout-main">
				<!--
				== As paginas da aplicação incluídas pela rotas
				-->
				<router-view></router-view>
			</div>

			<footer-layout />

		</main>
		<div class="layout-mask animate-fadein"></div>
	</div>
</template>


<script setup>
import HeaderLayout from '@/layout/HeaderLayout.vue'
import SideBarLayout from '@/layout/SideBarLayout.vue'
import FooterLayout from '@/layout/FooterLayout.vue'

import { computed, ref, watch, onMounted, onBeforeMount } from 'vue';
import { storeToRefs } from "pinia"; // Optional, for better reactivity

import { useLayout } from '@/layout/composables/layout';
const { layoutConfig, layoutState, isSidebarActive, resetMenu } = useLayout();

import { logout } from "@/utils/authentication"

// axios
import { useHttp } from '@/utils/axiosUtils';
const { http } = useHttp();


// funcionado do clique fora do menu para fecha quando está no modulo mobile
const outsideClickListener = ref(null);

watch(isSidebarActive, (newVal) => {
    if (newVal) {
        bindOutsideClickListener();
    } else {
        unbindOutsideClickListener();
    }
});

// aplica o layout no conteudo dependendo de como esta o menu
const containerClass = computed(() => {
    return {
        'layout-overlay': layoutConfig.menuMode === 'overlay',
        'layout-static': layoutConfig.menuMode === 'static',
        'layout-static-inactive': layoutState.staticMenuDesktopInactive && layoutConfig.menuMode === 'static',
        'layout-overlay-active': layoutState.overlayMenuActive,
        'layout-mobile-active': layoutState.staticMenuMobileActive
    };
});

const bindOutsideClickListener = () => {
    if (!outsideClickListener.value) {
        outsideClickListener.value = (event) => {
            if (isOutsideClicked(event)) {
                resetMenu();
            }
        };
        document.addEventListener('click', outsideClickListener.value);
    }
};
const unbindOutsideClickListener = () => {
    if (outsideClickListener.value) {
        document.removeEventListener('click', outsideClickListener);
        outsideClickListener.value = null;
    }
};
const isOutsideClicked = (event) => {
    const sidebarEl = document.querySelector('.layout-sidebar');
    const topbarEl = document.querySelector('.layout-menu-button');

    return !(sidebarEl.isSameNode(event.target) || sidebarEl.contains(event.target) || topbarEl.isSameNode(event.target) || topbarEl.contains(event.target));
};

/*
 * Check if the user is logged in
 */

import { useLoginStore } from "@/stores/login"
import { useParametrosStore } from "@/stores/parametros"
const loginStore = useLoginStore()
const parametrosStore = useParametrosStore()

// Using storeToRefs is optional but recommended for better reactivity with the store's state.
const { autenticado } = storeToRefs(loginStore);
const { enableLoginPage } = storeToRefs(parametrosStore);
const { login2Fa } = storeToRefs(parametrosStore);

// Log out when login page is enabled (Holter) but user is not authenticated
if ( enableLoginPage.value && !autenticado.value ) {
  logout();
}
// Log out when login with two-factor authentication is required but the logged-in user is not authenticated
if ( loginStore.isUsuarioLogado() && login2Fa?.value && !autenticado.value ) {
  logout();
}


// solicita um novo token jwt ao backend e atualiza no store
/* async function refreshToken() {
	await http.get('/api/jwttoken/refresh')
	.then((result) => {
		const newToken = result.data;
		loginStore.setTokenAcesso(newToken);
	})
	.catch(() => {

	})
} */


/**
 * This approach uses setTimeout instead of setInterval.
 * By setting the next timeout only after the refreshToken() function has completed,
 * you avoid potential delays caused by overlapping intervals.
 */
/* function startRefreshTokenCycle() {
    const refreshCycle = () => {
        refreshToken().finally(() => {
            setTimeout(refreshCycle, 900000); // 900000 = 15 min
        });
    };
    refreshCycle();
} */

onMounted(() => {
/*     if (autenticado.value) {
        startRefreshTokenCycle();
    } */
});

</script>



<style scoped>

</style>
