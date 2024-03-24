import Vue from 'vue'
import Vuex from 'vuex'

import VuexPersistence from 'vuex-persist';

import createLogger from 'vuex/dist/logger'

Vue.use(Vuex)

import GeneralModule      from './generalModule'
import CollectorsModule   from './collectorsModule'
import LoginModule        from './loginModule'
import ParameterModule        from './parameterModule'

const isDebuging = process.env.NODE_ENV !== 'production'

const vuexPersistence = new VuexPersistence({
  key: process.env.VUE_APP_PUBLIC_PATH.toLowerCase().replace(/\//g, '') + '-storage',
  storage: window.localStorage,
  modules: ['general', 'collectors', 'login', 'parameter'],
});



export default new Vuex.Store({
    
    modules: {
      general:             GeneralModule,
      collectors:          CollectorsModule,
      login:               LoginModule,
      parameter:           ParameterModule,
    },
    
    strict: isDebuging,
  
    // Vuex stores accept the plugins option that exposes hooks for each mutation. 
    // A Vuex plugin is simply a function that receives the store as the only argument
    plugins: isDebuging ? [ createLogger(), vuexPersistence.plugin ] : [ vuexPersistence.plugin ]

    
  })