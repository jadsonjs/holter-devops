
import Vue from 'vue'

import store from '../store'

import router from '../router';

import AxiosHandler from './../functions/AxiosHandler'

/**
 * Instancia a classe AxiosHandler e passa o store para ela ter acesso.
 * 
 * podemos acessar essa classe a partir do this.#http.get, this.#http.post, etc... nos componente vue
 */
Vue.use({
  install(Vue) {
    Vue.prototype.$http = AxiosHandler
    Vue.prototype.$http.setStore(store)
    Vue.prototype.$http.setRouter(router)
  }
})