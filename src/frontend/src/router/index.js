/**
 * Universidade Federal do Rio Grande do Norte
 * Instituto Metrópole Digital
 * Diretoria de Tecnologia da Informação
 *
 * Aquivo de configuracao das rotas.
 * 
 * @author Jadson Santos - jadson.santos@ufrn.br
 * @since 14/08/2024
 */
import { createRouter, createWebHistory } from 'vue-router';

import { useLoginStore } from "@/stores/login"
const loginStore = useLoginStore()

import { useParametrosStore } from "@/stores/parametros"
const parametrosStore = useParametrosStore()


// mensagens
import { useMessage } from '@/utils/message';
const { message } = useMessage();

import InternalLayout from '@/layout/InternalLayout.vue';
import PublicLayout from '@/layout/public/PublicLayout.vue';
import NaoEncontrado from "@/views/base/auth/NaoEncontrado.vue"
import AcessoNegado from "@/views/base/auth/AcessoNegado.vue"


import { hasRoles } from "@/utils/permissoes"


const router = createRouter({
	history: createWebHistory(import.meta.env.VITE_APP_PUBLIC_PATH),
	routes: [
		{
			path: '/',
			component: InternalLayout,
			// internal pages of app
			children: [

				// pagina principal, colocar o conteudo desejado nessa tela
				{
					path: '/home', name: 'home', component: () => import('@/views/base/Home.vue')
				},

				// paginas da administração do sistema

				{ path: '/acesso-negado', name: 'acessoNegado', component: AcessoNegado },


				{
					path: '/admin/usuarios/list',
					name: 'listUsers',
					component: () => import('@/views/use-case/crud/UsersList.vue'),
					meta: {
						papeis: ["ADMIN"],                                                       // permissoes para acessar a rota
						searchQuery: { titulo: "Users List", keywords: "List User Lista Usuário" }, // dados para pesquisa
					}
				},

				{
					path: '/admin/usuarios/form/',
					name: 'formUser',
					component: () => import('@/views/use-case/crud/UsersForm.vue')
				},

				{
					path: '/admin/usuarios/form/:id',
					name: 'editUser',
					component: () => import('@/views/use-case/crud/UsersForm.vue')
				},


				{
					path: "/notificacoes",
					name: "notificacoes",
					component: () => import("@/views/base/Notificacoes.vue"),
					meta: {
						searchQuery: { titulo: "Notificações", keywords: "notificação Notificações" }
					},
				},




				{
					path: '/metric/list',
					name: 'listMetrics',
					component: () => import('@/views/use-case/crud/MetricsList.vue')
				},
				{
					path: '/project/list',
					name: 'listProjects',
					component: () => import('@/views/use-case/crud/ProjectList.vue')
				},
				{
					path: '/project/form',
					name: 'formProject',
					meta: { requiresSecurity: true, papeis: ["ADMIN"]},
					component: () => import('@/views/use-case/crud/ProjectForm.vue')
				},
				{
					path: '/project/form/:id',
					name: 'editProject',
					meta: { requiresSecurity: true, papeis: ["ADMIN"]},
					component: () => import('@/views/use-case/crud/ProjectForm.vue')
				},
				{
					path: '/main-repository/form',
					name: 'formMainRepository',
					meta: { requiresSecurity : true, papeis: ['ADMIN']},
					component: () => import('@/views/use-case/crud/MainRepositoryForm.vue')
				},
				{
					path: '/main-repository/form/:id',
					name: 'editMainRepository',
					meta: { requiresSecurity : true, papeis: ['ADMIN']},
					component: () => import('@/views/use-case/crud/MainRepositoryForm.vue')
				},
				{
					path: '/main-repository/list',
					name: 'listMainRepository',
					meta: { requiresSecurity : true, papeis: ['ADMIN']},
					component: () => import('@/views/use-case/crud/MainRepositoryList.vue')
				},
				{
					path: '/configuration/form/:id',
					name: 'formConfiguration',
					meta: { requiresSecurity : true, papeis: ['ADMIN']},
					component: () => import('@/views/use-case/crud/ProjectConfigurationForm.vue')
				},
				{
					path: '/scheduler/view/:idProjeto',
					name: 'viewScheduler',
					component: () => import('@/views/use-case/crud/SchedulerView.vue')
				},
				{
					path: '/scheduler/form/:idProjeto',
					name: 'formScheduler',
					meta: { requiresSecurity : true, papeis: ['ADMIN', 'MANAGER']},
					component: () => import('@/views/use-case/crud/SchedulerForm.vue')
				},
				{
					path: '/scheduler/edit/:id',
					name: 'editScheduler',
					meta: { requiresSecurity : true, papeis: ['ADMIN', 'MANAGER']},
					component: () => import('@/views/use-case/crud/SchedulerForm.vue')
				},
				{
					path: '/ci/references/list/:projectId',
					name: 'listReferences',
					component: () => import('@/views/use-case/crud/ReferenceValuesList.vue')
				},
				{
					path: '/ci/references/form/project/:projectId',
					name: 'formReference',
					meta: { requiresSecurity : true, papeis: ['ADMIN', 'MANAGER'] },
					component: () => import('@/views/use-case/crud/ReferenceValuesForm.vue')
				},
				{
					path: '/ci/references/form/:id',
					name: 'editReference',
					meta: { requiresSecurity : true, papeis: ['ADMIN', 'MANAGER'] },
					component: () => import('@/views/use-case/crud/ReferenceValuesForm.vue')
				},
				// list the projects 
				{
					path: '/collect/listprojects',
					name: 'selectProjectsToCollect',
					component: () => import('@/views/use-case/miner/ConfigureManualMiner.vue')
				},
				// execute the miner manually
				{
					path: '/collect/mine/:id',
					name: 'executeManualMiner',
					meta: { requiresSecurity : true, papeis: ['ADMIN', 'MANAGER'] },
					component: () => import('@/views/use-case/miner/ExecuteManualMiner.vue')
				},
				{
					path: '/metrics/evolution/list',
					name: 'listMetricsEvolution',
					component: () => import('@/views/use-case/dashboard/evolution/MetricsEvolutionList.vue')
				},
				{
					path: '/metrics/evolution/dashboard/:owner/:name',
					name: 'dashboardMetricsEvolution',
					component: () => import('@/views/use-case/dashboard/evolution/MetricsEvolutionDashboard.vue')
				},
				{
					path: '/report/list',
					name: 'selectProjectForReport',
					component: () => import('@/views/use-case/security/ConfigureProjectsReport.vue')
				},
				
				// uses case repository list
				{
					path: '/repository/evolution/list',
					name: 'repositoryEvolutionDashboard',
					props: { context: 'repository' },
					component: () => import('@/views/use-case/dashboard/evolution/MetricsEvolutionList.vue')
				},
				{
					path: '/repository/evolution/dashboard/:owner/:name',
					name: 'repositoryDashboardMetricsEvolution',
					component: () => import('@/views/use-case/dashboard/evolution/RepositoryMetricsEvolutionDashboard.vue')
				},
				/**
				 * ***********************************************************
				 * 
				 * FIM DAS ROTAS DOS CASOS DE USO DA APLICAÇÃO
				 *
				 * Caso não encontre uma rota, ir para página 404
				 * 
				 * ***********************************************************
				 */
				{ path: "/:pathMatch(.*)", component: NaoEncontrado },
			]
		}, // end internal layout pages



		// public pages in vue
		{
			path: '/',
			component: PublicLayout,
			// internal pages of app
			children: [
				{
					path: '/login',
					name: 'login',
					component: () => import('@/views/base/auth/Login.vue')
				},
/* 				{
					path:'/code',
					name: 'login2fa',
					component: () => import('@/views/base/auth/Login2fa.vue')
				} */
			]
		},


	]
});

import { redirectToLogin } from "@/utils/authentication"

/**
 * NAVIGATION GUARDS
 * Seguranca: verifica se o usuário tem permissao de acessar a rota
 */
router.beforeEach(async (to) => {

	/**
	 * Se o login é obrigatório e o usuário não está autenticado nem se autenticando (2 fatores), redireciona para a tela de login
	 */
	if (parametrosStore.enableLoginPage && ! loginStore.autenticado && (to.name !== "login" && to.name !== "login2fa")) {
		redirectToLogin()
		return false
	}

	/**
	 * Se o usuário já está autenticado e tenta acessar a autenticação 2FA, redireciona para a home.
	 * Se o usuário tenta acessar a autenticação 2FA sem ter iniciado o login, ou após ter sido deslogado, redireciona para a tela de login.
	 */
/* 	if (to.name == "login2fa") {
		if (loginStore.autenticado == true) {
			return { name: "home" }
		}
	} */

	/*
	 * se o usuario solicitou a troca da senha, deve ir para essa tela ate trocar a senha
	 */
	if (loginStore.usuarioLogado.senhaEsquecida) {
		if (to.name !== "formSenhaUsuario") {
			message.warn("Alterar senha", "É necessário realizar a troca da senha para o seu usuário")
			return { name: "formSenhaUsuario" }
		}
	}


	/**
	 * Se o campo meta da rota exige segurança e o usuário não está logado, redireciona para o login.
	 */
	if (to.meta.requiresSecurity ) {
		if(! loginStore.autenticado ){  
			alert('User not authenticated, please log in')
			return { name: "login" }
		}
	}

	/**
	 * Se o campo meta da rota tem papeis, verifica se o usuario possui esse papeis para acessar a tela
	 * Se não mostra tela de acesso negado ao sistema.
	 */
	if (to.meta?.papeis?.length > 0) {
		if (!hasRoles(to.meta.papeis)) {
			return { name: "acessoNegado" }
		}
	}

	/**
	 * não existe "/", se por acaso o usuario digiar isso, vai para o componente home
	 */
	if ( to.path === "/") {
		return { name: "home" }
	}

})

export default router;