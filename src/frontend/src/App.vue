<template>
  
  <div id="app" v-if=" ! enableLoginPage || authenticated ">

    <div v-bind:class="[ sideBarToggled ? 'toggled' : '', 'd-flex']" id="wrapper">

    <app-side-bar/>

    <!-- Page Content -->
    <div id="page-content-wrapper">

      <app-header/>

      <div class="container-fluid">
        <router-view> </router-view>
      </div>

      <app-footer/>

    </div>
    <!-- /#page-content-wrapper -->

  </div>
  <!-- /#wrapper -->

  </div> <!-- /#app -->

</template>

<script>
import AppHeader   from './layout/AppHeader.vue'
import AppFooter   from './layout/AppFooter.vue'
import AppSideBar   from './layout/AppSideBar.vue'


export default {
  name: 'App',
  components: {
    AppHeader, AppFooter, AppSideBar
  },

  computed:{
    sideBarToggled(){   return this.$store.state.general.sideBarToggled   },

    collectorsList(){   return this.$store.state.collectors.collectorsList   },

    publicPath(){ return process.env.VUE_APP_PUBLIC_PATH },

    authenticated() { return this.$store.state.login.authenticated;    },

    enableLoginPage() { return this.$store.state.login.enableLoginPage;    },

  },
  
  methods: {
        async getEnableLoginPage(){

            this.$http.get('/parameter/login-enable')
            .then(
                (result) => {
                  this.$store.commit('login/setEnableLoginPage' , result.data)
                }
            )
        },

        async loadParameters(){

            this.$http.get('/parameter/lock-no-ci-metrics')
            .then(
                (result) => {
                  this.$store.commit('parameter/setLockNoCIMetrics' , result.data)
                }
            )
        },

         // checks if login is enable
         checkLoginRequired(){
          if ( this.enableLoginPage && ! this.authenticated  ) {
            window.location.replace(this.publicPath+'login')
          }
        },

    },

    async beforeMount(){

      

      await this.loadParameters()

      await this.getEnableLoginPage()

      setTimeout(() => {

        this.checkLoginRequired();

				if(this.collectorsList.length == 0){
          this.$http.get('/collector/')
            .then(result => { 
                this.$store.commit('collectors/setCollectorsList'      , result.data )
              }  
            ).catch(
                error => { console.log(error.data.messageList) }
            )
        }

      }, 1000);

  }

}

</script>

<style scoped>

</style>
