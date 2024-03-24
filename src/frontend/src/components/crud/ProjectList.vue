<template>
  <div>
    <h1 class="mt-4">Project</h1>

    <article class="descricao-ajuda">
      <p> List of Projects save in the tool to collect DevOps metric information. </p>
    </article>

    <b-container class="bv-example-row">

      <b-link  title="Create a New Project" to="/project/form">
            <i class="fas fa-plus-circle"></i>  
            <button type="button" class="btn btn-link">New Project</button>
      </b-link>

       <table class="table table-striped table-hover">
          <caption style="caption-side: top;"> List of Projects </caption>
          <thead>
              <tr>
                  <th>Name</th>
                  <th>Organization</th>
                  <th>Enable metrics collection</th>
                  <th colspan="4">Actions</th>
              </tr>
          </thead>
          <tbody v-if="projectsList.length > 0">
             <tr v-for="(project) in projectsList" :key="project.id">
                <td> {{project.name}}</td>
                <td> {{project.organization}}</td>
                <td> {{project.active}}</td>
                <td>
                    <b-link title="Edit" v-on:click="edit(project)" class="icon">
                        <i class="fas fa-edit"></i>
                    </b-link>
                </td>
                <td>
                    <b-link title="Project Configurations" v-on:click="viewConfigurations(project)" class="icon">
                         <i class="fas fa-cogs"></i>
                    </b-link>
                </td>
                <td>
                    <b-link title="View the Scheduler" v-on:click="viewScheduler(project)" class="icon">
                        <i class="fas fa-stopwatch"></i>
                    </b-link>
                </td>
                <td>
                  <b-button variant="danger" v-on:click="remove(project)" class="icon" v-if="project.id > 0" title="Delete the Project and All Collected Data">
                      <i class="fas fa-trash"></i> Delete Project
                  </b-button>
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

  name: 'ProjectList',

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

      edit(p){
        this.$router.push("/project/form/"+p.id)
      },

      viewConfigurations(p){
        this.$router.push("/configuration/form/"+p.id)
      },

      viewScheduler(p){
        this.$router.push("/scheduler/view/"+p.id)
      },

      remove(p){
        if(this.hasRole('ADMIN')){
          if(confirm('Confirm remove project '+p.name+' ? ')){
            this.$http.delete('/project/'+p.id)
              .then( (result) => {
                  this.$toastw.success(result.data.messageList)
                  this.loadProjects()
                }  
              )
              .catch( error => {   this.$toastw.error(error.data.messageList)  })
          }
        }else{
          this.$toastw.error('User has no permission: ADMIN')
        }
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