<template>
    <div>
      <h1 class="mt-4">Control Metric Form</h1>
  
      <article class="descricao-ajuda">
        <p> In this first version, this values will be created by the user, not calculated.</p>
      </article>
  
      <b-container class="bv-example-row">
  
        <h4 v-if="period.project"> Project: {{period.project.organization +'/'+ period.project.name}} </h4>
        <h5 v-if="period"> Period: {{period.init}} to: {{period.end}} </h5>

        <b-row>
           <b-form v-if="! loading">
              
            <b-form-group id="input-group-1" label="Number of Developers:" label-for="input-1">
                 <b-form-input id="input-1" v-model="controlMetric.ndevelopers" type="number" placeholder="0" required> </b-form-input>
              </b-form-group>

              <b-form-group id="input-group-2" label="Project Size (LOC):" label-for="input-2">
                 <b-form-input id="input-2" v-model="controlMetric.projectSize" type="number" placeholder="0" required> </b-form-input>
              </b-form-group>

              <b-form-group id="input-group-3" label="Project Complexity (cyclomatic complexity) :" label-for="input-3">
                 <b-form-input id="input-3" v-model="controlMetric.projectComplexity" type="number" placeholder="0" required> </b-form-input>
              </b-form-group>

              <b-form-group id="input-group-4" label="Technical Debt:" label-for="input-4">
                 <b-form-input id="input-4" v-model="controlMetric.technicalDebt" type="number" placeholder="0" required> </b-form-input>
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
  
    name: 'ControlMetricForm',
  
    components: {
          Loading
      },
  
    computed:{
      appName(){ return process.env.VUE_APP_NAME },
    }, 
  
    data() {
        return {
            
            // the object that will be created
            controlMetric : {},

            // the period of the metric will be created
            period : {},
  
            loading : false,
        }
    },
  
    methods: {
  
        /**
         * Call the back-end to save a new measure reference value
         * 
         */
        save(){
        
            this.loading = true
            
            this.$http.post('/control-metric/save', this.controlMetric)
            .then( () => { 
                this.$toastw.success('Control Metric Values saved with success!')
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

        loadPeriod(){
            this.loading = true          
            this.$http.get('/period/'+this.$route.params.id)
            .then(result => { 
                    this.period = result.data;
                    this.controlMetric.period = this.period
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
  
        cancel(){
          this.$router.push("/control/list")
        },
  
    },
  
    beforeMount(){
      
      // if send a "id" in the router is because is editing the project
      if( this.$route.params.id ){
  
         this.$http.get('/control-metric/'+this.$route.params.id)
            .then(result => { 
                this.controlMetric = result.data;
                // creatig a new one
                if( ! this.controlMetric.id > 0){
                    this.controlMetric = {}
                }
                this.loadPeriod()
              }  
            )
            .catch(
                error => {
                  this.$toastw.error(error.data.messageList)
                }
            )
      }
       
    }
    
  }
  </script>
  
  <!-- Add "scoped" attribute to limit CSS to this component only -->
  <style scoped>
  
  </style>