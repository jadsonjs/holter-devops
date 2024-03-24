<template>
  <div>
    
    <h5>DevOps Maturity</h5>
    <b-progress class="mt-3" max="17" show-value height="3rem">
        <b-progress-bar :value="ciMetricsList.length" variant="info">
          <span class="legend"> CI Metrics: {{ maturity.ci }} / {{ ciMetricsList.length }} </span>
        </b-progress-bar>
        <b-progress-bar :value="doraMetricsList.length " variant="success" v-if="! lockNoCIMetrics">
          <span class="legend"> DORA Metrics: {{ maturity.dora }} / {{ doraMetricsList.length }} </span>
        </b-progress-bar>
        <b-progress-bar :value="performanceMetricsList.length" variant="secondary" v-if="! lockNoCIMetrics">
          <span class="legend"> Peformance Metrics: {{ maturity.performance }} / {{ performanceMetricsList.length }} </span>
        </b-progress-bar>
        <b-progress-bar :value="operationMetricsList.length" variant="danger" v-if="! lockNoCIMetrics">
          <span class="legend"> Operation Metrics: {{ maturity.operation }} / {{ operationMetricsList.length }} </span>
        </b-progress-bar>
    </b-progress>
    <br>
    <div class="row">
      <div class="col-md-1" style="text-align: left;"> <img :src="require('@/assets/img/baby.png')"  width="35px"/> </div>
      <div class="col-md-10"></div>
      <div class="col-md-1" style="text-align: right;" > <img :src="require('@/assets/img/expert.png')"  width="35px"/> </div>
    </div>  
    <b-progress class="mt-3" max="17" show-value animated height="1rem">
        <b-progress-bar :value="maturity.ci" variant="info">
          <span class="legend"> {{ maturity.ci }} / {{ ciMetricsList.length }} </span>
        </b-progress-bar>
        <b-progress-bar :value="maturity.dora " variant="success" v-if="! lockNoCIMetrics">
          <span class="legend"> {{ maturity.dora }} / {{ doraMetricsList.length }} </span>
        </b-progress-bar>
        <b-progress-bar :value="maturity.performance" variant="secondary" v-if="! lockNoCIMetrics">
          <span class="legend"> {{ maturity.performance }} / {{ performanceMetricsList.length }} </span>
        </b-progress-bar>
        <b-progress-bar :value="maturity.operation" variant="danger" v-if="! lockNoCIMetrics">
          <span class="legend"> {{ maturity.operation }} / {{ operationMetricsList.length }} </span>
        </b-progress-bar>
    </b-progress>
    <br>
      
  </div>
</template>

<script>

export default {

  name: 'MaturityBar',

  components: {
    
  },
  
  props:  {
    
    ciMetricsList : { },

    doraMetricsList : { },

    performanceMetricsList : { },

    operationMetricsList : { },

    maturity : { },

  },

  data() {
    return {

    }
  },

  computed:{
      lockNoCIMetrics() { return this.$store.state.parameter.lockNoCIMetrics;    },
  },

  methods: {

  },
  
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
