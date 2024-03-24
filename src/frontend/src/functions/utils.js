import Vue from 'vue'
import store from '../store'

const GlobalFunctions = {

    install (Vue) {
      
      /**
       * Checks whether the step object is empty, null or has not been declared
       *
       * To access do v-if="objIsEmpty(obj)" in the HTML or this.objIsEmpty(obj) in the vue methods
       * 
       * @param {obj} obj 
       */
      Vue.prototype.objIsEmpty = function (obj) {
        return obj === null || obj === undefined || Object.keys( obj ).length === 0 
      }


      /** 
       * Check if logged user has the role 
       * 
       * To access do v-if="hasRole('ROLE')" in the HTML or this.hasRole('ROLE') in the vue methods
       */
      Vue.prototype.hasRole = function(input) {
        if( ! this.objIsEmpty(store.state.login.loggedInUser) ){
          for(let p of store.state.login.loggedInUser.permission){ // forearch user permission
            const roles = [];
            if (typeof input === 'string'){
              roles.push(input);
            }else{
              roles.push(...input); // is a array, push all elements of input to roles
            }
            for (const r of roles) {  // forearch role passed as argument
              if (p.role.name === r){
                return true;
              }
            }
          }
        }
        return false;
      }

    }

}
  
Vue.use(GlobalFunctions);

export default GlobalFunctions
