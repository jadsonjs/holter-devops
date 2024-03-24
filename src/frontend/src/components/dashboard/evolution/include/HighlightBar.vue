<template>
    <div>
      
        <div class="row">
        <h5 style="padding-right: 6px;"> Highlight Metrics: </h5>
        <b-form-group v-slot="{ ariaDescribedby }">
          <b-form-radio-group id="radio-group-2" v-model="highlight" :aria-describedby="ariaDescribedby" name="radio-sub-component" v-on:change="updateValue">
          <b-form-radio value="stage">Stage</b-form-radio>
          <b-form-radio value="category">Category</b-form-radio>
          <!-- <b-form-radio value="team">Team</b-form-radio> -->
          </b-form-radio-group>
        </b-form-group>
        </div>
        <div class="row">
        <div v-if="highlight == 'stage'">    <span class="metric_legend" style="background-color: #17a2b8;">Continuous Integration</span> <span class="metric_legend" style="background-color: #28a745;">Continuous Delivery</span> <!--  <span class="metric_legend" style="background-color: MediumPurple;">Continuous Monitoring</span> <span class="metric_legend" style="background-color: IndianRed;">Continuous Security</span> --> </div>
        <div v-if="highlight == 'category'"> <span class="metric_legend" style="background-color: #097969;">Productivity</span>         <span class="metric_legend" style="background-color: #D22B2B;">Resilience</span>          </div>
        <!--  <div v-if="highlight == 'team'">     <span class="metric_legend" style="background-color: LightSeaGreen;">Development</span>      <span class="metric_legend" style="background-color: LimeGreen;">Operation</span>      </div> -->
        </div>
        <br>
        <br>
        
    </div>
  </template>
  
  <script>
  
  export default {
  
    name: 'HighlightBar',
  
    components: {
      
    },
    
    props:  {
      
        highlight : {
          type: String, // This defines the data type of the prop
          default: '',  // This sets the default value to an empty string
        },
  
    },
  
    data() {
      return {
  
      }
    },
  
    methods: {
      updateValue(value) {
        // Emit an event to notify the parent about the change
        this.$emit('updateHighlightProp', value);
      },
    },
    
  }
  </script>
  
  <!-- Add "scoped" attribute to limit CSS to this component only -->
  <style scoped>
  
  </style>
  