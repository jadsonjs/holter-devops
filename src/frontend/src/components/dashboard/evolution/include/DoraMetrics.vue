<template>

<div> 

      <b-alert show variant="light" class="col-md-12"><b> DORA Metrics  <i  v-if="lockNoCIMetrics" class="fas fa-lock"></i> 
        <help id="69ebe3298e7e" title="DORA Metrics" text="These metrics are designed to assess the effectiveness of an organization's DevOps practices and their impact on software delivery and operational performance. Organizations that excel in these metrics are often more agile, deliver software more quickly, and have a higher level of reliability in their systems." color="grey" style="float: left !important;" />
      </b></b-alert>  

      <b-skeleton-img no-aspect animation width="600px" height="800px" v-if="lockNoCIMetrics"> </b-skeleton-img>  

      <div v-if="! lockNoCIMetrics">

        <span v-for="metric in doraMetricsList" :key="metric.metric.id">
    
          <span class="metric_box"
                        v-bind:style="{'background-color' :  
                              highlight == 'category' ? metric.metric.categoryHighlightColor : 
                                  ( highlight == 'stage' ? metric.metric.stageHighlightColor : 
                                      ( highlight == 'team' ? metric.metric.teamHighlightColor : '' ) )   }">
            <div class="box_title">
                  {{metric.metric.denomination}}
            </div> 
            <div>
                <span class="box_value" v-bind:style="{ 'color' : metric.overReferenceValue ? '' : 'red', 'background-color' : metric.overReferenceValue ? '' : 'white'}">Current Value:  {{metric.lastValue}}  <span style="font-size: small;"> ( {{metric.metric.unit}} )</span> </span>
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

      </div>

</div>

</template>
  
<script>
  
  import Help from '@/components/common/Help.vue'
  
  export default {
  
    name: 'DoraMetric',
  
    components: {
      Help
    },
    
    props:  {

      doraMetricsList : { },
      
      //productivityQualityMetrics : { },  

      highlight : {
        type: String,
        required: false
      },

    },
  
    data() {
      return {
      }
    },

    computed:{
      lockNoCIMetrics() { return this.$store.state.parameter.lockNoCIMetrics;    },
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