<template>

  <!-- Popup user menu on Header -->
  <Menu ref="userMenu" id="overlay_menu" :model="userMenuItems" :popup="true">

     <!-- Top of menu, system info -->
     <template #start>
        <span class="inline-flex align-items-center gap-1 px-2 py-2">
          <img :src="appIcon" width="25" height="25" alt="logo" />
          <span class="font-medium text-xl font-semibold"> {{appName}} </span>
        </span>
     </template>

     <!-- menu itens -->
     <template #item="{ item, props }">
       <a v-ripple class="flex align-items-center" v-bind="props.action">
         <span :class="item.icon" />
         <span class="ml-2">{{ item.label }} </span>
         <Badge v-if="item.badge" class="ml-auto" :value="item.badge" />
         <span v-if="item.shortcut" class="ml-auto border-1 surface-border border-round surface-100 text-xs p-1">{{ item.shortcut }}</span>
       </a>
     </template>

     <!-- Show the used logggin -->
     <template #end>
       <button v-ripple class="relative overflow-hidden w-full p-link flex align-items-center p-2 pl-3 text-color hover:surface-200 border-noround">
         <Avatar :image="userIcon" class="mr-2" shape="circle" />
         <span class=""> <!-- inline-flex flex-column -->
           <span class="font-bold">{{usuarioLogado?.email || 'CONVIDADO'}}</span>
					 <br/>
           <span class="text-sm">{{ usuarioLogado?.permission ? usuarioLogado.permission[0]?.role?.name : '' }} </span>
         </span>
       </button>
     </template>
		 
  </Menu>

</template>

<script setup>
import { ref, computed, toRaw } from 'vue';
import { storeToRefs } from 'pinia';

import appIcon from '/icon.svg';
import noUserImage from '@/assets/img/no-user.png';
import { logout } from "@/utils/authentication"

const appName = import.meta.env.VITE_APP_PUBLIC_PATH.replace(/[/]+/g, '').toUpperCase()

// router
import { useRouter } from 'vue-router'
const router = useRouter()

// store
import { useLoginStore } from '@/stores/login';
const loginStore = useLoginStore();
const { usuarioLogado } = storeToRefs(loginStore);

const userIcon = computed(() => {
  const urlFoto = loginStore.usuarioLogado?.pessoa?.urlFoto;
  return urlFoto ? urlFoto : noUserImage;
});

///////////////  user itens  //////////////////



const userMenu = ref();

/*
 *  itens gerais do menu
 */ 
const userMenuItems = ref([
  {
    separator: true
  },
  {
    label: '',
    items: [
/* 			{
        label: 'Meus Dados Pessoais',
        icon: 'fa-regular fa-address-card',
				visible: loginStore.isUsuarioLogado(),
				command: () => {
          router.push('/admin/usuarios/form')
        }
      },
			{
        label: 'Alterar Senha',
        icon: 'fa-solid fa-key',
				visible: loginStore.isUsuarioLogado() && usuarioLogado.value.logouLocal,
				command: () => {
          router.push('/admin/usuarios/senha')
        }
      }, */
      {
        label: 'Sair',
        icon: 'fa-solid fa-right-from-bracket',
				visible: loginStore.isUsuarioLogado(),
        // The command property defines the callback to run when an item is activated by click or a key event.
        command: () => {
          logout()
        }
      },
      {
        label: 'Entrar',
        icon: 'fa-solid fa-right-from-bracket',
				visible: !loginStore.isUsuarioLogado(),
        // The command property defines the callback to run when an item is activated by click or a key event.
        command: () => {
          router.push('/login')
        }
      },
    ]
  },
  {
    separator: true
  }
]);

// mostra o menu do usuario, chamado do component pai HeaderLayout.vue
const toggleUserMenu = (event) => {
  userMenu.value.toggle(event);
};

// Expose the method to the parent
defineExpose({ toggleUserMenu });


</script>



<style scoped>

</style>
