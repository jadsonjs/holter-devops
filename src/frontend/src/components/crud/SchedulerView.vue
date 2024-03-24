<template>
  <div>
    <h1 class="mt-4">Scheduler</h1>

    <article class="descricao-ajuda">
      <p> Scheduler are the entities that run the miner of specific metric for the project </p>
    </article>

    <b-container class="bv-example-row">

      <b-link  title="Create a New Scheduler" v-on:click="create()" v-if="! scheduler.id">
            <i class="fas fa-plus-circle"></i>  
            <button type="button" class="btn btn-link">Create a Scheduler</button>
      </b-link>

      <div class="mb-3">
        <b-card title="Scheduler">
          <b-card-text>
            Scheduler of the Project <b > {{ scheduler && scheduler.project ? scheduler.project.organization+" / "+scheduler.project.name : " "}} </b>
          </b-card-text>

          <b-list-group flush>
            <b-list-group-item> <h5 class="mb-1">Fequency: </h5>             {{scheduler.frequencyOfExecution}} </b-list-group-item>
            <b-list-group-item> <h5 class="mb-1">Start Execution at: </h5>   {{scheduler.startExecution}}  </b-list-group-item>
            <b-list-group-item> <h5 class="mb-1">Last Execution at: </h5>    {{scheduler.lastExecution != null ? scheduler.lastExecution : ' - - - '}} </b-list-group-item>
            <b-list-group-item> <h5 class="mb-1">Automatic: </h5>            {{scheduler.automatic}} </b-list-group-item>
          </b-list-group>


          <b-list-group flush>
              <b-list-group-item v-for="(uuid, index) in this.scheduler.collectorsId" v-bind:key="uuid"> <b> Collector {{index+1}} : </b>    {{ getCollectorName(uuid) }} </b-list-group-item>
          </b-list-group>

          <b-card-text class="text-right">
            <b-link v-on:click="edit(scheduler)" class="icon m-3" v-if="scheduler.id > 0" title="Edit Scheduler">
                <i class="fas fa-edit"></i> Edit
            </b-link>

            <b-button variant="danger" v-on:click="clearData()" class="m-3" v-if="scheduler.id > 0" title="Clear All Collected Data">
              <i class="fas fa-broom"></i> Clear Collected Data
            </b-button>

            <b-button variant="danger" v-on:click="remove(scheduler)" class="m-3" v-if="scheduler.id > 0" title="Delete the Scheduler and All Collected Data">
              <i class="fas fa-trash"></i> Delete Scheduler
            </b-button>

          </b-card-text>

        </b-card>
      </div>                

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

  name: 'SchedulerList',

  components: {
        Loading
    },

  computed:{
    appName(){ return process.env.VUE_APP_NAME },

    collectorsList(){   return this.$store.state.collectors.collectorsList   },
  }, 

  data() {
      return {
          
          // keep the list of schedulers returned from back-end
          scheduler : {},

          // the project selected.
          idProject: 0,

          loading : false,

      }
  },

  methods: {

      /**
       * Load all schedulers of the selected project.
       */
      load(){
          this.loading = true
          
          if(this.$route.params.idProjeto !== undefined)
            this.idProject = this.$route.params.idProjeto
          
            this.$http.get('/scheduler/project/'+this.idProject)
            .then(result => { 
                this.scheduler = result.data;
              }  
            )
          .catch(
              error => {
                this.$toastw.alert(error.data.messageList)
              }
          ).finally(()=>{
              this.loading = false
          })
      },

      /**
       * Load all collectors supported by the tool
       *
       */
       loadCollectors(){
      
          this.loading = true

          this.$http.get('/collector/')
          .then(result => { 
              this.$store.commit('collectors/setCollectorsList'      , result.data )
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

      create(){
          this.$router.push("/scheduler/form/"+this.idProject)
      },

      edit(scheduler){
        this.$router.push("/scheduler/edit/"+scheduler.id)
      },


      clearData(){
        if(this.hasRole(['ADMIN', 'MANAGER'])){
          this.loading = true
              
          if(confirm('Confirm Clean Collected Data?')){
              this.$http.delete('/scheduler/clear/'+this.scheduler.id)
              .then( () => { 
                  this.$toastw.success('Data collected by Scheduler clear with success!')
                  this.scheduler = {}
                  this.load()
                }  
              ).catch(
                  error => {
                    this.$toastw.error(error.data.messageList)
                  }
              ).finally(()=>{
                  this.loading = false
              })
            }
        }else{
          this.$toastw.error('User has no permission: ADMIN, MANAGER')
        }
      },

      remove(scheduler){
        if(this.hasRole('ADMIN')){
          if(confirm('Confirm Removal of Scheduler?')){
            this.$http.delete('/scheduler/'+scheduler.id)
              .then( (result) => {
                  this.$toastw.success(result.data.messageList)
                  this.scheduler = {}
                  this.load()
                }  
              )
              .catch( error => {   this.$toastw.error(error.data.messageList)  })
          }
        }else{
          this.$toastw.error('User has no permission: ADMIN')
        }
      },

      cancel(){
        this.$router.push("/project/list")
      },

      /**
       * Load the name from store, given that collectors are static
       */
      getCollectorName(id){
        for(var i=0; i < this.collectorsList.length ; i++){
           if( this.collectorsList[i].id === id){
            return '['+this.collectorsList[i].repository.name+'] ' + ' [' + this.collectorsList[i].metric.stage + '] ' + this.collectorsList[i].metric.denomination + ' ('+this.collectorsList[i].description+')'
           }
        }
        return '' 
      },

  },

  beforeMount(){
    this.loadCollectors()
    this.load()
  }
  
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
