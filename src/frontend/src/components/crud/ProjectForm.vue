<template>
  <div>
    <h1 class="mt-4">Project</h1>

    <article class="descricao-ajuda">
      <p> Create a new Project to monitoring its Metrics</p>
    </article>

    <b-container class="bv-example-row">

      <b-row>
         <b-form v-if="! loading">
            <b-form-group id="input-group-1" label="Name:" label-for="input-1" description="Write a name">
               <b-form-input id="input-1" v-model="project.name" type="text" placeholder="Enter a name" required></b-form-input>
            </b-form-group>
            <b-form-group id="input-group-1" label="Organization:" label-for="input-2" description="Write the onwer's name">
               <b-form-input id="input-2" v-model="project.organization" type="text" placeholder="Enter a name" required></b-form-input>
            </b-form-group>
            <b-form-group id="input-group-1" label="Enable metrics collection?" label-for="input-3" description="Enable or disable metrics collection for the project">
               <b-form-checkbox switch size="lg" id="input-3" v-model="project.active"></b-form-checkbox>
            </b-form-group>
            
         </b-form>
      </b-row>  

      <b-row>
            <b-col>
               <b-button variant="primary" v-on:click="save()"> Save </b-button>
               <b-button variant="outline-dark" v-on:click="cancel()"> Cancel </b-button>
            </b-col>
      </b-row>

      <div class="w-100" style="height: 50px;"></div>

    </b-container>

    <loading :show="loading"></loading>

    
  </div>
</template>

<script>

import Loading from '@/components/common/Loading.vue'


export default {

  name: 'ProjectForm',

  components: {
        Loading
    },

  computed:{
    appName(){ return process.env.VUE_APP_NAME },
  }, 

  data() {
      return {
          
          project : {},

          loading : false,
      }
  },

  methods: {

      /**
       * Call the back-end to save a new project
       * 
       */
      save(){
      
          this.loading = true

          if(this.project.lastCollect == null){
            this.project.lastCollect = new Date();
            this.project.lastCollect = null;
          }
          
          this.$http.post('/project/save', this.project)
          .then( () => { 
              this.$toastw.success('Project saved with success!')
              this.cancel()
            }  
          ).catch(
              error => {
                this.$toastw.error(error.data.messageList)
              }
          ).finally(()=>{
              this.loading = false
          })
        
      },

      cancel(){
        this.$router.push("/project/list")
      },

  },

  beforeMount(){
    
    // if send a "id" in the router is because is editing the project
    if( this.$route.params.id ){

       this.$http.get('/project/'+this.$route.params.id)
          .then(result => { 
              this.project = result.data;
            }  
          )
          .catch(
              error => {
                this.$toastw.error(error.data.messageList)
              }
          )
    }else{
        // creatig a new one
    }
     
  }
  
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>