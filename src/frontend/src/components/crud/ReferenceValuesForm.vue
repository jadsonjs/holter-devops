<template>
  <div>
    <h1 class="mt-4">Reference Values</h1>

    <article class="descricao-ajuda">
      <p> Define Reference Values to Alert the users </p>
    </article>

    <b-container class="bv-example-row">

      <b-row>
         <b-form v-if="! loading">
             
             <b-form-group id="input-group-4" label="Select a Metric" label-for="input-4" description="Select a Metric">
                  <select class="form-control" v-model="metricReference.metric">
                    <option v-for="metric in metricList" :key="metric.id" :value="metric">
                      {{metric.denomination}} ( {{metric.stage}} )
                    </option>
                  </select>
            </b-form-group>

            <b-form-group id="input-group-1" label="Reference Value:" label-for="input-1" :description="'Enter a Reference Value for the Metric: '+(metricReference.metric ? metricReference.metric.denomination : ' ???????? ' )+' ' ">
               <b-form-input id="input-1" v-model="metricReference.value" type="number" placeholder="Enter a Reference Value" required> </b-form-input>
               {{metricReference.metric && metricReference.metric.unit ? ('in '+metricReference.metric.unit) : ''}}
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

  name: 'metricReferenceForm',

  components: {
        Loading
    },

  computed:{
    appName(){ return process.env.VUE_APP_NAME },
  }, 

  data() {
      return {
          
          metricReference : {
            metric : {
              id : null,
              denomination : ' Select a Valid Metric ',
              stage: 'CI'
            }
          },

          metricList : [],

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
          
          this.$http.post('/ci-metric-reference/save', this.metricReference)
          .then( () => { 
              this.$toastw.success('Reference value for: '+ this.metricReference.metric.denomination+' saved with success!')
              this.cancel()
            }  
          ).catch(
              error => {
                if(error.data.messageList[0].includes('JSON parse error'))
                  this.$toastw.error('Select a Valid Metric')
                else
                  this.$toastw.error(error.data.messageList)
              }
          ).finally(()=>{
              this.loading = false
          })
        
      },

      onChange(event) {
        this.selectedMetric = event.target.value
      },

      cancel(){
        this.$router.push("/ci/references/list")
      },


      loadMetrics(){
          this.loading = true          
          this.$http.get('/metric/references')
          .then(result => { 
              this.metricList = result.data;
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

  },

  beforeMount(){

    this.loadMetrics()
    
    // if send a "id" in the router is because is editing the project
    if( this.$route.params.id ){

       this.$http.get('/ci-metric-reference/'+this.$route.params.id)
          .then(result => { 
              this.metricReference = result.data;
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