<template>
    <div>
      <b-alert show variant="light" class="col-md-12"> <b>CI Metrics
        <help id="741df8371667" title="Continuous Integration Metrics" text="Measurements and indicators that provide insights into the efficiency, quality, and effectiveness of the integration process" color="grey" style="float: left !important;" /> 
        </b>
      </b-alert>
      
      
      
      <span v-for="metric in ciMetricsList" :key="metric.metric.id">
        
          <span class="metric_box" 
                    v-bind:style="{'background-color' :  
                          highlight == 'category' ? metric.metric.categoryHighlightColor : 
                              ( highlight == 'stage' ? metric.metric.stageHighlightColor : 
                                  ( highlight == 'team' ? metric.metric.teamHighlightColor : '' ) )   }">
              <div class="box_title">
                {{metric.metric.denomination}}
              </div> 
              <div>
                  <span class="box_value" v-bind:style="{ 'color' : metric.overReferenceValue ? '' : 'red', 'background-color' : metric.overReferenceValue ? '' : 'white'}">Current Value:  {{metric.lastValue}} <span style="font-size: small;"> ( {{metric.metric.unit}} )</span> </span>
                  <br>
                  <span class="box_ref_value"> Reference Value: {{metric.valueReference}}  <span style="font-size: small;"> ( {{metric.metric.unit}} )</span> </span>
              </div>
              <div class="p-1 d-flex">
                <div class="float-start" style="font-size: 30px;">        
                  <b-button variant="light" size="sm" class="rounded-circle" v-on:click="showMetricEvolution(metric.metric.id)">
                      <i class="fas fa-chart-line"></i>
                  </b-button>
                 </div>
                <div class="ml-auto" style="font-size: 30px;">
                  <help :title="metric.metric.denomination" :text="metric.metric.description + '<br><br>' + '<i>'+ metric.metric.formula + '</i>' " :id="metric.metric.id.toString()" />
                </div>
              </div>
        </span>
      
      </span>
      
      <!-- Degree of CI/CD 
      <div class="row">
        <GChart :settings="{ packages: ['corechart', 'gauge'] }" type="Gauge" :data="chartDegreeCIData" :options="chartDegreeCIOptions"/>
      </div>
      -->
  </div>
</template>

<script>

import Help from '@/components/common/Help.vue'

export default {

  name: 'CIMetrics',

  components: {
    Help
  },
  
  props:  {
    
    ciMetricsList : { },

    highlight : {
      type: String,
      required: false
    },
    
    chartDegreeCIData : {
      type: Array,
      required: false
    },

    chartDegreeCIOptions : {}

  },

  data() {
    return {

    }
  },

  methods: {

    showMetricEvolution(metricId) {
        // Emit an event to notify the parent about the change
        this.$emit('showMetricEvolution', metricId);
    },

  },
  
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
