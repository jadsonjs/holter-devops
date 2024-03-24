

import Vue from 'vue';
import VueToastrWrapper from '@/plugins/vue-toast-notification';

/**
 * this.$toastw.sucess('This is a message')
 * this.$toastw.info('This is a message')
 * this.$toastw.alert('This is a message')
 * this.$toastw.error('This is a message')
 */
Vue.use({
  install(Vue) {
    Vue.prototype.$toastw = VueToastrWrapper;
  },
});