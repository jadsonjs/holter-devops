<template>
  <div>
    <h1 class="mt-4">List of Metric</h1>

    <article class="descricao-ajuda">
      <p> List of All supported metrics</p>
    </article>

    <div style="width: 90%;">

       <table class="table table-striped table-hover">
          <caption style="caption-side: top;"> Metrics </caption>
          <thead>
              <tr>
                  <th>Name</th>
                  <th>Stage</th>
                  <th>Category</th>
                  <th>Team</th>
                  <th>Unit</th>
                  <th>Description</th>
                  <th>Formula</th>
              </tr>
          </thead>
          <tbody v-if="measureList.length > 0">
             <tr v-for="(metric) in measureList" :key="metric.id">
                <td> {{metric.denomination}} </td>
                <td v-bind:style="{'color' : metric.stageHighlightColor}"> {{metric.stage}} </td>
                <td v-bind:style="{'color' : metric.categoryHighlightColor}"> {{metric.category}} </td>
                <td v-bind:style="{'color' : metric.teamHighlightColor}"> {{metric.team}} </td>
                <td> {{metric.unit}} </td>
                <td> <i> {{metric.description}} </i> </td>
                <td style="font-variant: small-caps; "> {{metric.formula}}  </td>
             </tr>                 
          </tbody>
       </table>                  

       <b-row>
            <b-col>
              <b-button variant="outline-dark" v-on:click="cancel()"> Cancel </b-button>
            </b-col>
       </b-row>

      <div class="w-100" style="height: 50px;"></div>

    </div>


    <loading :show="loading"></loading>

    
  </div>
</template>

<script>

import Loading from '@/components/common/Loading.vue'


export default {

  name: 'MetricList',

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
          measureList : [],

          loading : false,
      }
  },

  methods: {

      /**
       * Load all metrics.
       * This method will call the MetricController#getAll in the back-end that will return all metrics salved in database.
       */
      loadMetrics(){
      
          this.loading = true
          
          this.$http.get('/metric/all')
          .then(result => { 
              this.measureList = result.data;
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
        this.$router.push("/")
      },

  },

  beforeMount(){
     this.loadMetrics()
  }
  
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>