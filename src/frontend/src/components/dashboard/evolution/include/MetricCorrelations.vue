<template>
    <!-- project general information  -->
    <div class="col-md-12"> 

      <b-alert show variant="light" class="col-md-12"> <b>Metrics Correlations</b>    
        <help title="Correlations" text="In statistics it usually refers to the degree to which a pair of variables are linearly related. 
        These values try to indicate the existence of some relationship between the CI metrics and the quality attributes.
        <br/>
        <b>We are trying to answer the question: What is the impact of CI metrics on my project?</b>" id="586ba457" color="gray" />
      </b-alert>  
      
      <table class="table table-bordered table-condensed">
        <thead>
          <tr>
            <!-- Closed PRs, Bugs, Tec. Debt -->
            <th></th>
            <th scope="col" v-for="colum in metricsCorrelationMatrix[0]" :key="colum.id">{{colum.attributeName}}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="line in metricsCorrelationMatrix" :key="line.id">
              <th scope="row" v-if="line != null && line[0] != null"> {{line[0].metricName}} </th>
              <td v-for="regresionData in line" :key="regresionData.id" @click="showModal(regresionData)"
                  class="column_selected"
                  v-bind:class="regresionData.relevant && regresionData.rightDirection ? 'table-success' : ( regresionData.relevant && ! regresionData.rightDirection ? 'table-danger' : '') ">
                  {{regresionData.rsquared}} %  
              </td>
          </tr>
        </tbody>
    </table>


    <div>
          <!--    zoom in the evolution chart -->
          <b-modal id="bv-modal-regression" ref="regression-modal" size="xl" hide-header hide-footer>
                
              <div class="evolution_info_box" v-if=" ! ( Object.keys(selectedRegression).length === 0 ) ">
                <div class="box_title"> Correlation: {{selectedRegression.rsquared}} % </div> 
                <span class="box_value">  Significance: {{selectedRegression.significance}} ( &lt; 0.05 )</span> 
              </div>
              
              <div>
                <GChart type="ScatterChart" :data="selectedRegression.chartData" :options="selectedRegression.chartOptions" v-if=" ! ( Object.keys(selectedRegression).length === 0 ) && selectedRegression.chartData !== null "/>
              </div>

              <b-button class="mt-3" block @click="hideModal">Close Me</b-button>

          </b-modal> 
          
        </div> 
    </div>
</template>
  
<script>
  
  import { GChart } from 'vue-google-charts'
  import Help from '@/components/common/Help.vue'

  
  export default {
  
    name: 'MetricCorrelation',
  
    components: {
      GChart,
      Help
    },
    
    props:  {
       metricsCorrelationMatrix : { },
    },
  
    data() {
      return {
        selectedRegression : {},
      }
    },
  
    methods: {
      showModal(regression) {
        this.selectedRegression = regression
        this.$refs['regression-modal'].show()
      },
      hideModal() {
        this.$refs['regression-modal'].hide()
      },
  
    },
    
  }
  </script>
  
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

.table-condensed{
  font-size: 10px;
}
  
/* On mouse-over, add a deeper shadow */
.column_selected:hover {
  font-weight: bold;
  cursor: pointer;
}

</style>