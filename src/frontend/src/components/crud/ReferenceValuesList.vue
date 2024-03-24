<template>
  <div>
    <h1 class="mt-4">Reference Values</h1>

    <article class="descricao-ajuda">
      <p> Metric Reference Values are values can be used to verify DevOps maturity in your team.</p>
      <p> The Reference Values can change, according to the characteristics of each team. In this way, each team is allowed to define its most suitable values.</p>
      <br>
      <p> For Continuous Integration metrics, you can based Reference Values of <a href="https://arxiv.org/abs/1907.01602" target="_blank"> Continuous Integration Theater </a> paper</p>
      <p>
          <ol>
            <li> With infrequent commits, <b>2.36</b> commits per weekday</li>
            <li> In a software project with poor test coverage, On average, Java projects have <b>63% </b> of test coverage</li>
            <li> With builds that stay broken for long periods, and We observed that 85% of the analyzed projects have at least one build that took more than <b>4 days</b> to be fixed</li>
            <li> With builds that take too long to run.  Be executed under <b>10 minutes</b></li>
          </ol>
      </p>

      <br>
      <p>For DORA metrics, you can use the Google's classifications in the <a href="https://cloud.google.com/blog/products/devops-sre/dora-2022-accelerate-state-of-devops-report-now-out" target="_blank">DORA 2022 Accelerate State of DevOps Report</a>.</p>

      <table style="margin-top: 1rem; margin-bottom: 1.5em; border-collapse: collapse; width: 100%; line-height: 1.5;">
        <thead>
           <tr>
              <th>Metric</th><th>Description</th><th>High</th><th>Medium</th><th>Low</th></tr></thead>
        <tbody>
          <tr>
             <td>Deployment frequency</td><td>The number of deploys to production per day</td><td> <b> ≥ 1</b> <br> <span style="font-size: small;"> (multiple deploys a day) </span> </td>
             <td> <b>0.033 - 0.13</b> <br> <span style="font-size: small;">(between one per month and one per week)</span> </td><td> <b>&lt; 0.033</b> <br> <span style="font-size: small;">(less than one per month)</span></td>
          </tr>
          <tr>
             <td>Lead time for changes</td><td>The number of days to go from code committed to code successfully running in production</td>
             <td> <b>≤ 7</b> <br> <span style="font-size: small;"> (less than one week) </span> </td><td> <b>8 - 29</b> <br> <span style="font-size: small;">(between one week and one month)</span> </td> 
             <td> <b>≥ 30</b> <br> <span style="font-size: small;">(more than one month)</span> </td>
          </tr>
          <tr>
             <td>Mean time to Recovery</td><td>The number of days to restore service when a service incident or a defect that impacts users occurs</td> 
             <td> <b>≤ 1</b>  <br> <span style="font-size: small;"> (less than one day)</span></td><td> <b>2 - 6</b> <br> <span style="font-size: small;">(between one day and one week) </span> </td><td> <b>≥ 7</b> <br> <span style="font-size: small;">(more than one week)</span></td>
          </tr>
          <tr>
             <td>Change failure rate</td><td>The percentage of changes to production resulted in degraded service</td><td> <b>≤ 15%</b> </td><td> <b>16% - 30%</b> </td><td> <b>46% - 60%</b> </td>
          </tr>
        </tbody>
     </table>


    </article>

    <b-container class="bv-example-row">

      <b-link  title="Create a New Project" to="/ci/references/form">
            <i class="fas fa-plus-circle"></i>  
            <button type="button" class="btn btn-link">New Reference Value</button>
      </b-link>

       <table class="table table-striped table-hover">
          <caption style="caption-side: top;"> List of Reference Values Defined by the Team</caption>
          <thead>
              <tr>
                  <th>Metric</th>
                  <th>Reference Value</th>
                  <th colspan="2">Actions</th>
              </tr>
          </thead>
          <tbody v-if="measureReferenceList.length > 0">
             <tr v-for="(m) in measureReferenceList" :key="m.id">
                              
                <td> {{m.metric.denomination}}  </td>
                <td> {{m.value}} ({{ m.metric.unit}}) </td>
                <td>
                    <b-link title="Edit" v-on:click="edit(m)" class="icon">
                        <i class="fas fa-edit"></i>
                    </b-link>
                </td>
                <td>
                    <b-link title="Delete" v-on:click="remove(m)" class="icon">
                        <i class="fas fa-trash"></i>
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

  name: 'MeasureReferenceList',

  components: {
        Loading
    },

  computed:{
    appName(){ return process.env.VUE_APP_NAME },
  }, 

  data() {
      return {
          
          // keep the list of projects returned from back-end
          measureReferenceList : [],

          loading : false,
      }
  },

  methods: {

      /**
       * Load all references values saved in the tool.
       */
      loadMetricReferencesValues(){
      
          this.loading = true
          
          this.$http.get('/ci-metric-reference')
          .then(result => { 
              this.measureReferenceList = result.data;
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

      edit(m){
        // a way to pass parameter to component in router
        this.$router.push({ name: 'ReferencesForm', params: { id: m.id } });
      },


      remove(m){
        if(this.hasRole(['ADMIN', 'MANAGER'])){
          if(confirm('Confirm remove reference value of '+m.metric.denomination+' CI metric ? ')){
            this.$http.delete('/ci-metric-reference/'+m.id)
              .then( (result) => {
                  this.$toastw.success(result.data.messageList)
                  this.loadMetricReferencesValues()
                }  
              )
              .catch( error => {   this.$toastw.error(error.data.messageList)  })
          }
        }else{
          this.$toastw.error('User has no permission: ADMIN, MANAGER')
        }
      },

      cancel(){
        this.$router.push("/")
      },

  },

  beforeMount(){
     this.loadMetricReferencesValues()
  }
  
}
</script>


<style scoped>

</style>