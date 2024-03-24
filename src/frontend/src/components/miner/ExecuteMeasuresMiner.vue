<template>
  <div>
    <h1 class="mt-4">Execute Metric Collection Manually</h1>

    <article class="descricao-ajuda">
      <p> This operation execute manually a new collection of all metrics scheduled to the project</p>
    </article>

    <div class="mb-3">
        <b-card title="Scheduler">
          <b-card-text>
            Execution for the Project <b > {{project.organization}} / {{ project.name }} </b>   
          </b-card-text>

          <b-list-group flush>
            <b-list-group-item> <h5 class="mb-1">Fequency: </h5>             {{scheduler.frequencyOfExecution}} </b-list-group-item>
            <b-list-group-item> <h5 class="mb-1">Start Execution at: </h5>   {{scheduler.startExecution}} </b-list-group-item> 
            <b-list-group-item> <h5 class="mb-1">Last Execution at: </h5>    {{scheduler.lastExecution != null ? scheduler.lastExecution : ' - - - '}}  <span class="text-danger font-italic">{{ cycleNoCompleted ? '  (Next Collection Cycle is not completed yet)  ' : ''}} </span> </b-list-group-item>
            <b-list-group-item> <h5 class="mb-1">Automatic: </h5>            {{scheduler.automatic}} </b-list-group-item>
            
            <b-list-group-item v-for="(uuid, index) in this.scheduler.collectorsId" v-bind:key="uuid"> 
              <article v-bind:class="{'text-muted' : isCollectorExecuted(uuid) }"> 
                <b> Collector {{index+1}}: </b>     {{ getCollectorName(uuid) }}  <br>
                <span class="text-muted font-italic text-sm-center"> {{ printCollectorStatus(uuid) }} </span>
              </article>
            </b-list-group-item>

            <div class="text-light bg-dark">  <h5 class="mb-1"> Next collection Cycle: </h5> </div>
            <b-list-group-item variant="dark" class="d-flex justify-content-around"> 
              <span class="d-flex justify-content-between"> 
                <h5 class="mb-1"> From : </h5>  {{period.init}}   
              </span>
              <span class="d-flex justify-content-between">  
                <h5 class="mb-1">To : </h5> {{period.end}} 
              </span>
            </b-list-group-item>

          </b-list-group>

          <b-list-group flush>
              
          </b-list-group>
          
        </b-card>
      </div>                


    <b-container class="bv-example-row">

       <b-row>
            <b-col>
              <b-button variant="primary" class="m-2" v-on:click="mine()" :disabled="loading"> Mine <i class="far fa-play-circle"></i> </b-button>
              <b-button variant="outline-dark" class="m-2" v-on:click="cancel()"> Cancel </b-button>
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

  name: 'CollectMeasures',

  components: {
        Loading
    },

  computed:{

    appName(){ return process.env.VUE_APP_NAME },

    collectorsList(){   return this.$store.state.collectors.collectorsList   },
  }, 

  // coloque seus dados aqui 
  data() {
      return {
          
          // project
          project : {},
          // scheduler
          scheduler : {},
          // period of collection
          period : {},

          cycleNoCompleted : false,

          loading : false,

          collectorsStatus : [],
      }
  },

  methods: {


    /**
     * Calculate and Load next mine period
     */
    loadNextMinerPeriod(){

      if( this.$route.params.id ){

          this.loading = true

          this.$http.get('/miner-measure/load/'+this.$route.params.id)
            .then(result => { 
                this.project = result.data[0];
                this.scheduler = result.data[1];
                this.period = result.data[2];
                this.cycleNoCompleted = result.data[3];

                if(this.cycleNoCompleted){
                  this.$toastw.alert('Next Collection Cycle is not completed yet.')
                }

                this.loadCollectorStatus(this.project.id, this.scheduler.collectorsId)
              }  
            )
          .catch(
              error => {
                this.$toastw.error(error.data.messageList)
                this.cancel()
              }
          ).finally(()=>{
            this.loading = false
          })
        }else{
            this.$toastw.error('Select a project')
        }

    },

      
    /**
     * Execute the mine of metrics for the project
     */  
    mine(){

      this.loading = true

      this.$http.post('/miner-measure/mine', this.period)
        .then(result => { 
            this.$toastw.success(result.data.messageList)
            this.$router.push("/")
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


    /** Go to backend to get the status of collector, if it was alredy executed (has a HistoryMetric) */
    loadCollectorStatus(projectId, uuids){
        this.$http.get('/collector/status/'+projectId+'/'+uuids)
          .then(result => { 
              this.collectorsStatus = result.data
            }  
          ).catch(
              error => { console.log(error.data.messageList) }   
          )
    },

    /** print the status message of individual collector (if executed or not) */
    printCollectorStatus(uuid){
      for(var i=0; i < this.collectorsStatus.length ; i++){
        if(this.collectorsStatus[i].uuid === uuid)
        return '('+this.collectorsStatus[i].message+')'
      }
      return ''
    },
    
    /** checks if we have an execution of this collection */
    isCollectorExecuted(uuid){
      for(var i=0; i < this.collectorsStatus.length ; i++){
        if(this.collectorsStatus[i].uuid === uuid)
        return this.collectorsStatus[i].executed
      }
      return false
    },

    /**
     * Load collectors of the project to the cache in front-end
     */
    loadCollectors(){
      if(this.collectorsList.length == 0){
        this.$http.get('/collector/')
            .then(result => { 
                this.$store.commit('collectors/setCollectorsList'      , result.data )
              }  
            ).catch(
                error => { console.log(error.data.messageList) }
            )
      }
    },

    cancel(){
      this.$router.push("/measures/list")
    },

  },

  beforeMount(){
    this.loadCollectors() 
    this.loadNextMinerPeriod()
  }
  
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>