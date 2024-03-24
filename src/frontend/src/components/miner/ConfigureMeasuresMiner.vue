<template>
  <div>
    <h1 class="mt-4">Collect Metrics</h1>

    <article class="descricao-ajuda">
      <p> Select the project to collect metrics manually.</p>
      <p> For performance reasons, metrics are previously calculated and stored in the database before viewing them on the dashboard. </p>
      <br>
      <p>Collection is only actually performed if a new collection cycle is complete</p>
    </article>

    <b-container class="bv-example-row">

       <table class="table table-striped table-hover">
          <caption style="caption-side: top;"> List of Active Projects </caption>
          <thead>
              <tr>
                  <th>Name</th>
                  <th>Organization</th>
                  <th> </th>
              </tr>
          </thead>
          <tbody v-if="projects.length > 0">
             <tr v-for="(project) in projects" :key="project.id">
                <td> {{project.name}} </td>
                <td> {{project.organization}} </td>
                
                <td> 
                  <b-link title="Select Project" v-on:click="configure(project)" class="icon">
                       <i class="fas fa-arrow-right"></i>
                    </b-link>
                </td>
             </tr>                 
          </tbody>
       </table>                  

       <b-row>
            <b-col>
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

  name: 'PreCollectMeasures',

  components: {
        Loading
    },

  computed:{
    appName(){ return process.env.VUE_APP_NAME },
  }, 

  // coloque seus dados aqui 
  data() {
      return {
          
          // keep the list of metrics returned from back-end
          projects : [],

          loading : false,
      }
  },

  methods: {

      /**
       * Load all metrics.
       * This method will call the MetricController#getAll in the back-end that will return all metrics salved in database.
       */
      load(){

          this.loading = true
          
          this.$http.get('/project?active=true')
          .then(result => { 
              this.projects = result.data;
            }  
          )
          .catch(
              error => {
                this.$toastw.error(error.data.messageList)
              }
          ).finally(()=>{
              this.loading = false
          })
      },

      configure(p){
        this.$router.push("/measures/miner/"+p.id)
      },

      cancel(){
        this.$router.push("/")
      },

  },

  beforeMount(){
     this.load()
  }
  
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>