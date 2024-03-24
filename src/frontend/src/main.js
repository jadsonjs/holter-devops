import Vue from 'vue'
import App from './App.vue'

import router from './router';
import store from './store';

// imports all plugins used in applications (see plugins directory)
import './plugins'
import './functions/utils'


import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

import '@fortawesome/fontawesome-free/css/all.css'
import '@fortawesome/fontawesome-free/js/all.js'

Vue.config.productionTip = false

require('./assets/css/base.css')
require('./assets/css/dashboard.css')

new Vue({
  router, 
  store,
  render: h => h(App),
}).$mount('#app')
