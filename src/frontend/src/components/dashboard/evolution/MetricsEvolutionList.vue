<template>
  <div>
    <h1 class="mt-4">Project</h1>

    <article class="descricao-ajuda">
      <p> Select a Project to show its Metric Evolution </p>
    </article>

    <b-container class="bv-example-row">

       <table class="table table-striped table-hover">
          <caption style="caption-side: top;"> List of Projects </caption>
          <thead>
              <tr>
                  <th>Name</th>
                  <th>Organization</th>
                  <th>Active</th>
                  <th>Actions</th>
              </tr>
          </thead>
          <tbody v-if="projectsList.length > 0">
             <tr v-for="(project) in projectsList" :key="project.id">
                              
                <td> {{project.name}}</td>
                <td> {{project.organization}}</td>
                <td> {{project.active}}</td>
                <td>
                    <b-link title="Select" v-on:click="select(project)" class="icon">
                        <i class="fas fa-arrow-circle-right"></i>
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

  name: 'MetricEvolutionSelect',

  components: {
        Loading
    },

  computed:{
    appName(){ return process.env.VUE_APP_NAME },
  }, 

  data() {
      return {
          
          // keep the list of projects returned from back-end
          projectsList : [],

          loading : false,
      }
  },

  methods: {

      /**
       * Load all projects.
       * This method will call the ProjectController#getAll in the back-end that will return all projects salved in database.
       */
      loadProjects(){
      
          this.loading = true
          
          this.$http.get('/project')
          .then(result => { 
              this.projectsList = result.data;
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

      select(p){
        // this.$router.push({ name: 'MetricsEvolutionDashBoard', params: { projectId: p.id } });
        // encode the url, because the organition can have '/'' caracter
        this.$router.push("/metrics/evolution/dashboard/"+(p.organization.replace(/\//g, "___"))+"/"+p.name)
      },

      cancel(){
        this.$router.push("/")
      },

  },

  beforeMount(){
     this.loadProjects()
  }
  
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>