<template>
	<ul class="layout-menu">
			<template v-for="(item, i) in model" :key="item">
					<app-menu-item v-if="!item.separator" :item="item" :index="i"></app-menu-item>
					<li v-if="item.separator" class="menu-separator"></li>
			</template>
	</ul>
</template>

<script setup>
import { ref } from 'vue';

import AppMenuItem from './AppMenuItem.vue';

import { hasRoles } from '@/utils/permissoes'

const model = ref([
    {
        label: 'Home',
        items: [{ label: 'Home', icon: 'fa-solid fa-house', to: '/home' }]
    },
	{
        label: 'Administração',
        to: '/admin',
        /**
         * mostra o menu apenas se o usuário tiver o papel
         * pode ser colocado aqui ou nos itens filhos
         */
        show: hasRoles(['ADMIN']),  
        items: [
            { label: 'Users', icon: 'fa-solid fa-users', to: '/admin/usuarios/list'},
        ]
    },
    {
        label: 'Metrics',
        to: '/admin',
        items: [
            { label: 'List', icon: 'fa-solid fa-tachometer-alt', to: '/metric/list'},
        ]
    },
    {
        label: 'Projects',
        items: [
						{
                label: 'Projects',
                icon: 'fa-solid fa-briefcase',
                to: '/project/list'
            },
			{
                label: 'Main Repositories',
                icon: 'fa-solid fa-gear',
                to: '/main-repository/list',
                show: hasRoles(['ADMIN']),
            },
        ],
    },
    {
        label: 'Manually Collect',
        to: '/admin',
        items: [
            {
                label: 'Project Metrics', icon: 'fa-solid fa-funnel-dollar', to: '/collect/listprojects'
            },
        ]
    },
		/* use cases related to collecting DevOps metrics */
    {
        label: 'DevOps',
        items: [
            {
                label: 'Monitoring Dashboard',
                icon: 'fa-solid fa-chart-line',
                to: '/metrics/evolution/list'
            },
        ],

    },


	/* use cases related to collecting repository metrics */
	{
        label: 'Repository',
        items: [
            // {
            //     label: 'Collect Metrics',
            //     icon: 'fa-solid fa-funnel-dollar',
            //     to: '/repository/collect/listprojects'
            // },
            {
                label: 'Monitoring Dashboard',
                icon: 'fa-solid fa-chart-line',
                to: '/repository/evolution/list'
            },
        ],

    },

    /* use cases related to security reports */
	{
        label: 'Security',
        items: [
            {
                label: 'Security Report',
                icon: 'fa-solid fa-shield-halved',
                to: '/report/list'
            },
        ],

    },


		
    /* Examples of Menus
    {
        label: 'Pages',
        to: '/pages',
        items: [
            {
                label: 'Não Encontrada',
                icon: 'fa-solid fa-file-circle-exclamation',
                to: '/2DOmwe209jwfm23'
            },
						{
                label: 'Acesso Negado',
                icon: 'fa-solid fa-ban',
                to: '/secret'
            },
            {
                label: 'Empty',
                icon: 'fa-regular fa-file',
                to: '/empty'
            },
            {
                label: 'Primevue',
                icon: 'fa-solid fa-code',
                url: 'https://primevue.org/introduction/',
                target: '_blank'
            },
            {
                label: 'Tailwind css',
                icon: 'fa-solid fa-code',
                url: 'https://tailwindcss.com/docs/installation',
                target: '_blank'
            }
        ]
    },
    {
        label: 'Hierarchy',
        items: [
            {
                label: 'Submenu 1',
                icon: 'fa-solid fa-list',
                items: [
                    {
                        label: 'Submenu 1.1',
                        icon: 'fa-regular fa-bookmark',
                        items: [
                            { label: 'Submenu 1.1.1', icon: 'fa-solid fa-cicle', to: '/'},
                            { label: 'Submenu 1.1.2', icon: 'fa-solid fa-cicle', to: '/' },
                            { label: 'Submenu 1.1.3', icon: 'fa-solid fa-cicle', to: '/' }
                        ]
                    },
                    {
                        label: 'Submenu 1.2',
                        icon: 'fa-regular fa-bookmark',
                        items: [
													{ label: 'Submenu 1.2.1', icon: 'fa-solid fa-cicle', to: '/' }
												]
                    }
                ]
            },
						{
                label: 'Submenu 2',
                icon: 'fa-regular fa-bookmark',
                items: [
                    {
                        label: 'Submenu 2.1',
                        icon: 'fa-solid fa-cicle',
                        to: '/'
                    }
                        
                ]
            }
               
        ]
    },
	*/
    
]);
</script>



<style lang="scss" scoped></style>