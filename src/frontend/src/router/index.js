import Vue from 'vue'
import VueRouter from 'vue-router'

import store from '@/store';

Vue.use(VueRouter)

let router = new VueRouter({
    mode: 'history',
    
    base: process.env.VUE_APP_PUBLIC_PATH,
    
    routes: [
    
      { path: '/',                              name: 'Home',                                      component: () => import('@/components/Home.vue') }, 
      { path: '/login',                         name: 'Login',                                     component: () => import('@/Login.vue') }, 

      { path: '/users/list',                    name: 'ListUser',                                  component: () => import('@/components/crud/UsersList.vue') }, 
      { path: '/users/form',                    name: 'FormUser',                                  component: () => import('@/components/crud/UsersForm.vue'),                                        meta: { requiresSecurity : true, roles: ['ADMIN'] } },
      { path: '/users/form/:id',                name: 'EditUser',                                  component: () => import('@/components/crud/UsersForm.vue'),                                        meta: { requiresSecurity : true, roles: ['ADMIN'] } },
      
      { path: '/project/list',                  name: 'ListProject',                               component: () => import('@/components/crud/ProjectList.vue') }, 
      { path: '/project/form',                  name: 'FormProject',                               component: () => import('@/components/crud/ProjectForm.vue'),                                      meta: { requiresSecurity : true, roles: ['ADMIN'] } },
      { path: '/project/form/:id',              name: 'EditProject',                               component: () => import('@/components/crud/ProjectForm.vue'),                                      meta: { requiresSecurity : true, roles: ['ADMIN'] } },
      { path: '/project/findByName/:name',      name: 'GetProjectByName',                          component: () => import('@/components/crud/SchedulerForm.vue') },

      { path: '/metric/list',                   name: 'ListMetric',                                component: () => import('@/components/crud/MetricList.vue') },

      { path: '/configuration/form/:id',        name: 'ConfigurationForm',                         component: () => import('@/components/crud/ProjectConfigurationForm.vue'),                         meta: { requiresSecurity : true, roles: ['ADMIN'] } }, 


      { path: '/scheduler/view/:idProjeto',     name: 'ViewScheduler',                             component: () => import('@/components/crud/SchedulerView.vue') },
      { path: '/scheduler/form/:idProjeto',     name: 'FormScheduler',                             component: () => import('@/components/crud/SchedulerForm.vue') ,                                   meta: { requiresSecurity : true, roles: ['ADMIN', 'MANAGER'] }},
      { path: '/scheduler/edit/:id',            name: 'EditScheduler',                             component: () => import('@/components/crud/SchedulerForm.vue') ,                                   meta: { requiresSecurity : true, roles: ['ADMIN', 'MANAGER'] }},

      { path: '/ci/references/list',            name: 'ReferencesList',                            component: () => import('@/components/crud/ReferenceValuesList.vue') }, 
      { path: '/ci/references/form',            name: 'ReferencesForm',                            component: () => import('@/components/crud/ReferenceValuesForm.vue'),                              meta: { requiresSecurity : true, roles: ['ADMIN', 'MANAGER'] } }, 

      { path: '/control/list',                  name: 'ControlMetricList',                         component: () => import('@/components/crud/ControlMetricList.vue') }, 
      { path: '/control/form/:id',              name: 'ControlMetricForm',                         component: () => import('@/components/crud/ControlMetricForm.vue') }, 


      { path: '/measures/list',                 name: 'PreMeasureSelect',                          component: () => import('@/components/miner/ConfigureMeasuresMiner.vue') }, 
      { path: '/measures/miner/:id',            name: 'MeasureCollect',                            component: () => import('@/components/miner/ExecuteMeasuresMiner.vue') ,                           meta: { requiresSecurity : true, roles: ['ADMIN', 'MANAGER'] } }, 
      

      { path: '/metrics/evolution/list',                     name: 'MetricsEvolutionList',         component: () => import('@/components/dashboard/evolution/MetricsEvolutionList.vue') }, 
      { path: '/metrics/evolution/dashboard/:owner/:name',   name: 'MetricsEvolutionDashBoard',    component: () => import('@/components/dashboard/evolution/MetricsEvolutionDashBoard.vue') },  
      

    ]
})


/**
 * Router guards with security rules for the application
 */
router.beforeEach(function (to, from, next) {
  if ( ! to.meta.requiresSecurity ) {
    next();
  } else { // requiresSecurity = true
    if(! store.state.login.authenticated ){  
      alert('User not authenticated, please log in')
      window.location.replace(process.env.VUE_APP_PUBLIC_PATH+'login')
    }else{ // if authenticated check roles
      if (to.meta.roles && to.meta.roles.length > 0) {
        // router.app refers to the root Vue instance associated with the router
        if (router.app.hasRole(to.meta.roles)) {
          next();
        } else {
          alert('user has not permission: '+to.meta.roles)
          window.location.replace(process.env.VUE_APP_PUBLIC_PATH)
        }    
      }else{ // anyone can access
        next();
      }
    }
  }
  
});


export default router;      