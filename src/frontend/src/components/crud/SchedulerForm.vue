<template>
  <div>
    <h1 class="mt-4">Scheduler</h1>

    <article class="descricao-ajuda">
      <p> Configure the Scheduler Data for <b v-if="scheduler.project"> {{scheduler.project.organization }} / {{ scheduler.project.name}} </b> </p>
    </article>

    <b-container class="bv-example-row">

      <b-row>
        <b-col xl="12">
          <b-form v-if="! loading">

              <b-row>
                <b-col sm="12" md="10" lg="8">
                  <b-form-group id="input-group-2" label="Select Frequency of Execution" label-for="input-2" description="Set the frequency where mesuares will be collected">
                    <b-form-radio-group v-model="scheduler.frequencyOfExecution" class="pt-2" :options="['WEEK', 'MONTH', 'YEAR']"  :disabled="scheduler.id > 0"></b-form-radio-group>
                  </b-form-group>
                </b-col>
              </b-row>

              
              <b-row>
                <b-col sm="12" md="6" lg="4">
                  <b-form-group label="Start Execution at:"  label-for="startDate">
                     <b-form-input type="date" name="startExecutionDate" id="startDate" v-model="scheduler.startExecution" :disabled="scheduler.id > 0">
                  </b-form-input>
                </b-form-group>
                </b-col>
              </b-row>  
              

              <b-row>
                <b-col sm="12" md="12" lg="10">
                  <b-form-group id="input-group-4" label="Select a collector" label-for="input-4" description="Set a Collector">
                  <select class="form-control" v-model="selectedCollectorId" v-on:change="addCollectorToScheduler">
                    <option v-for="collector in collectorList" :key="collector.id" :value="collector.id">
                      [ {{collector.repository.name}} ]  [ {{collector.metric.stage}} ] -  {{ collector.metric.denomination }}   ( {{collector.description}}  )
                    </option>
                  </select>
                  </b-form-group>
                </b-col>
              </b-row>

              <b-row>
                  <b-col sm="12" md="12" lg="12">
                      
                    <table class="table table-striped table-hover" v-if="this.scheduler.collectorsId.length > 0">
                        <thead>
                            <tr>
                                <th>Collectors</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                          
                          <tr v-for="(uuid, index) in this.scheduler.collectorsId" v-bind:key="uuid">
                              <td> {{ index+1 }} </td>
															<td> {{ getCollectorName(uuid) }} </td>
                              <td>
                                  <button class="btn btn-default" type="button" v-on:click="removeCollectorFromScheduler(uuid)">
                                      <i class="fas fa-minus-circle"></i>
                                  </button>
                              </td>
                          </tr>
                            
                        </tbody>
                    </table>
                              
                  </b-col>
              </b-row>

              <b-row>
                <b-col sm="12" md="10" lg="8">
                  <b-form-group id="input-group-1" label="Enable automatic collection?" label-for="input-1" description="Set the status of automatic metric collection">
                    <b-form-checkbox switch size="lg" id="input-1" v-model="scheduler.automatic"></b-form-checkbox>
                  </b-form-group>
                </b-col>
              </b-row>

          </b-form>
        </b-col>
      </b-row>  

      <b-row>
            <b-col>
               <b-button variant="primary" v-on:click="save()" class="m-1"> Save </b-button>
               <b-button variant="outline-dark" v-on:click="cancel()" class="m-1"> Cancel </b-button>
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

  name: 'ScheduleForm',

  components: {
        Loading
    },

  computed:{
    appName(){ return process.env.VUE_APP_NAME },
  }, 

  data() {
      return {

          // Object we are creating
          scheduler : {
            frequencyOfExecution : 'MONTH',
            automatic : false,
            collectorsId : [],
          },

          // collector selected to be add to a scheduler, collector define what metric will be collected
          selectedCollectorId : '',

          /** List in the select item */
          collectorList : [],

          /** Original all  collector list */
          allCollectorList : [],

          loading : false,
      }
  },

  methods: {


      /** Load the Scheduler data */
      loadScheduler(){
        
        // if exist a param "idProjeto" we are creating a new scheduler 
        if( this.$route.params.idProjeto ){


          this.$http.get('/project/'+this.$route.params.idProjeto)
            .then(result => { 
                this.scheduler.project = result.data;
                this.$forceUpdate(); // force update the name of project
                this.loadCollectors()
              }  
            )
            .catch(
              error => {
                this.$toastw.error(error.data.messageList)
              }
            )

        }else{
          // editing a existent scheduler 

          this.loading = true
          
          this.$http.get('/scheduler/'+this.$route.params.id)
            .then(result => { 
                this.scheduler = result.data;
                this.loadCollectors()
              }  
            )
            .catch(
              error => {
                this.$toastw.error(error.data.messageList)
              }
          ).finally(()=>{
              this.loading = false
          })

        }
      },


      /**
       * Load all collectors supported by the tool
       *
       */
      loadCollectors(){
          this.loading = true

          this.$http.get('/collector/')
          .then(result => { 
              this.collectorList = result.data;
              this.allCollectorList = result.data.slice();

              // remove collectors already added to the scheduler 
              for(var i=0; i < this.scheduler.collectorsId.length; i++){
                this.removeCollectorToSelectList(this.scheduler.collectorsId[i]);
              }  

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
       * Call the back-end to save scheduler data.
       *
       */
       save(){
            this.loading = true
            
            this.$http.post('/scheduler/save', this.scheduler)
            .then( () => { 
                this.$toastw.success('Scheduler saved with success!')
                this.success(this.scheduler.project.id) 
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





      /** add a collector to scheduler */
      // addCollectorToScheduler(){
      //     if(this.selectedCollectorId !== '' && ! this.containsCollector(this.selectedCollectorId) ){
      //       this.scheduler.collectorsId.push(this.selectedCollectorId)

      //       this.removeCollectorToSelectList(this.selectedCollectorId)

      //     }else{
      //       this.$toastw.error('Collector already added to the scheduler.')
      //     }
      // },

      /** add a collector to scheduler by selecting it */
      addCollectorToScheduler(){
          if (this.selectedCollectorId != undefined) { // this method is calling 2 times bause use change the list when order it
            if(this.selectedCollectorId !== '' && ! this.containsCollector(this.selectedCollectorId) ){
              this.scheduler.collectorsId.push(this.selectedCollectorId)
              this.removeCollectorToSelectList(this.selectedCollectorId)
            }else{
              this.$toastw.error('Collector already added to the scheduler.')
            }
          }
      },

      /** remove a collector from scheduler */
      removeCollectorFromScheduler(id){
        this.scheduler.collectorsId.splice(this.scheduler.collectorsId.indexOf(id), 1);
        this.addCollectorToSelectList(id)
      },


      /** remove a collector added to scheduler to select list */
      removeCollectorToSelectList(id){
        for(var i=0; i < this.collectorList.length; i++){
          if( this.collectorList[i].id === id){
            this.collectorList.splice(this.collectorList.indexOf(this.collectorList[i]), 1);
          }
        } 
        this.sortcollectorList();
      },


       /** add again a collector to selet list when we remove from scheduler */
       addCollectorToSelectList(id){
        for(var i=0; i < this.allCollectorList.length; i++){
          if( this.allCollectorList[i].id === id){
            this.collectorList.splice(i, 0, this.allCollectorList[i]);
          }
        }
        this.sortcollectorList();
      },

      sortcollectorList(){
        this.collectorList.sort((a, b) => 
          ( a.repository.name.toLowerCase()+""+a.metric.stage.toLowerCase()+""+a.metric.denomination.toLowerCase() ) < (b.repository.name.toLowerCase()+""+b.metric.stage.toLowerCase()+""+b.metric.denomination.toLowerCase()) ? -1 : 1); 
      },

      /** check if  scheduler have collector */
      containsCollector(id){
        for(var i=0; i < this.scheduler.collectorsId.length; i++){
           if( this.scheduler.collectorsId[i] === id){
             return true
           }
        }  
        return false
      },

      getCollectorName(id){
        for(var i=0; i < this.allCollectorList.length ; i++){
           if( this.allCollectorList[i].id === id){
             return '['+this.allCollectorList[i].repository.name+'] ' + ' [' + this.allCollectorList[i].metric.stage + '] ' + this.allCollectorList[i].metric.denomination  + ' ('+ this.allCollectorList[i].description +')'
           }
        }
        return '' 
      },


      success(idProject){
        this.$router.push("/scheduler/view/"+idProject)
      },

      cancel(){
        this.$router.push("/scheduler/view/"+this.scheduler.project.id)
      },


    }, // methods

    beforeMount(){
      // this.loadCollectors()
      this.loadScheduler()
    }
  
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>