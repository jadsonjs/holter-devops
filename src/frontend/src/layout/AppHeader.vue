<template>
        
    <nav class="navbar navbar-expand-lg navbar-light bg-primary text-white border-bottom">
      
      <!-- navbar left itens -->
      <div class="float-start">
        <b-button variant="primary" v-on:click="showSideBar()"> <i class="fas fa-bars"></i> </b-button>
        <router-link class="navbar-brand text-white" to="/" > {{appName}} </router-link>
      </div>

      <!-- navbar right itens -->
      <div class="ml-auto">
        <b-dropdown id="dropdown-1" variant="primary" class="m-md-2">
          <template #button-content>
            <i class="fas fa-ellipsis-h"></i>
          </template>
          <b-dropdown-item v-on:click="listUsers()" v-if="( authenticated && hasRole('ADMIN') ) "> <i class="fas fa-users"></i> Users </b-dropdown-item>
          <b-dropdown-item v-on:click="logout()"    v-if="authenticated">    <i class="fas fa-sign-out-alt"></i> Logout </b-dropdown-item>
        </b-dropdown>
      </div>

    </nav>
    
    
    <!--
    <nav class="navbar navbar-expand-lg navbar-light bg-light border-bottom">
        <button class="btn btn-primary" id="menu-toggle">Toggle Menu</button>

        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
          <ul class="navbar-nav ml-auto mt-2 mt-lg-0">
            <li class="nav-item active">
              <a class="nav-link" href="#">Home <span class="sr-only">(current)</span></a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#">Link</a>
            </li>
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Dropdown
              </a>
              <div class="dropdown-menu dropdown-menu-right" aria-labelledby="navbarDropdown">
                <a class="dropdown-item" href="#">Action</a>
                <a class="dropdown-item" href="#">Another action</a>
                <div class="dropdown-divider"></div>
                <a class="dropdown-item" href="#">Something else here</a>
              </div>
            </li>
          </ul>
        </div>
      </nav>
      -->

      
   
</template>
 
<script>

export default {

  name: 'AppHeader',
  
  components: {
    
  },

  computed:{
    appName(){ return process.env.VUE_APP_NAME },

    sideBarToggled(){   return this.$store.state.general.sideBarToggled   },

    publicPath(){ return process.env.VUE_APP_PUBLIC_PATH },

		enableLoginPage() { return this.$store.state.login.enableLoginPage;    },

    authenticated() { return this.$store.state.login.authenticated;    },
  },  


  data() {
    return {
      
    }
  },

  methods: {
    showSideBar(){
      this.$store.commit('general/setSideBarToggled', ! this.sideBarToggled )
    },

    // list the tools users
    listUsers(){
      this.$router.push("/users/list")
    },

    logout(){
      this.$store.dispatch('login/reset') 
      localStorage.clear();
      window.location.replace(this.publicPath+'login')
    },
  },

  beforeMount(){
    
  }

}

</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

icon-user {
  border: 1px solid white;
  width: 40px;
  height: 40px;
  border-radius: 50% !important;
  background-color: #f8f9fa;
}

</style>