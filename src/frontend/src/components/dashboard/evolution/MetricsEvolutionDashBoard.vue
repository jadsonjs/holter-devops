<template>
  <div>
    <h1 class="mt-4">DevOps Monitoring Dashboard</h1>
    
    <loading :show="loading"></loading>

    <!-- 
      dashboard head
    -->
    <h4> 
      <div class="row">
        <div class="col-md-4"> Project: <br> <b><u> {{project.organization}} / {{project.name}} </u></b>  </div>
        <div class="col-md-3"><label for="init-date"> Init: </label>  <b-form-datepicker id="init-date" v-model="init" class="mb-2"></b-form-datepicker> </div>
        <div class="col-md-3"><label for="end-date"> End: </label>  <b-form-datepicker id="end-date" v-model="end" class="mb-2"></b-form-datepicker> </div>
        <div class="col-md-2"> <br> <b-button pill variant="primary" v-on:click="executeQueries()" title="Refresh" :disabled="loading">  <i class="fas fa-sync-alt"></i>  </b-button> </div>
      </div>
    </h4>


    <maturity-bar  :ciMetricsList="ciMetricsList" :doraMetricsList="doraMetricsList" :performanceMetricsList="performanceMetricsList" :operationMetricsList="operationMetricsList" :maturity="maturity"  />

    <highlight-bar  :highlight="highlight" @updateHighlightProp="updateHighlightProp"  />



    <!-- 
      == dashboard 
    -->



    <!--
         == TOP of dashboard
    -->
    <div class="row">

      <div class="col-md-12">
        <performance-metrics :performanceMetricsList="performanceMetricsList"  :highlight="highlight" @showMetricEvolution="handleShowMetricEvolution" />
      </div>

    </div>  



    <!--
      == Middle of dashboard (CI and DORA metric)
    -->
    <div class="row">
      <div class="col-md-6">
          <CIMetrics :ciMetricsList="ciMetricsList" :highlight="highlight" :chartDegreeCIData="chartDegreeCIData" :chartDegreeCIOptions="chartDegreeCIOptions" @showMetricEvolution="handleShowMetricEvolution" />
      </div>
       
      <div class="col-md-6">
        
        <div class="row">
          <!-- deprecated 
          <project-metrics     :projectMetrics="projectMetrics"                   @normalizeMetricEvent="handleNormalizeMetric" :highlight="highlight" />
          -->
          <dora-metrics        :doraMetricsList="doraMetricsList"  :highlight="highlight" @showMetricEvolution="handleShowMetricEvolution" />
          
          <operation-metrics   :operationMetricsList="operationMetricsList" :highlight="highlight"  @showMetricEvolution="handleShowMetricEvolution" />
          
        </div>
      
      </div>

    </div>


     
 
     
    <div class="w-100" style="height: 100px;"></div> 
        <b-button variant="outline-dark" class="m-2" v-on:click="cancel()"> Cancel </b-button>

        <b-button variant="primary" class="m-2" v-on:click="exportAccess()" v-if="enableLoginPage && authenticated && hasRole('ADMIN')"> <i class="fas fa-download"></i> Export Access </b-button>

    <div class="w-100" style="height: 50px;"></div>
    






     <!--    metric evolution details -->
     <b-modal id="bv-modal-chart" ref="chart-evolution-modal" size="xl" hide-header hide-footer>
              
          <!--
            <template #modal-title>
            Using <code>$bvModal</code> Methods
          </template>
          -->
          
          <div class="evolution_info_box" v-if=" ! ( Object.keys(selectedMetricEvolution).length === 0 ) ">
            <div class="box_title"> {{selectedMetricEvolution.metric.denomination}}  </div>  
            
            <span style="font-style: italic;"> {{selectedMetricEvolution.metric.description}}  </span> 
            <br>
            <span>Unit: {{selectedMetricEvolution.metric.unit}} </span> 
            <br>
            Formula: <span style="font-variant: small-caps; font-family: 'Courier New', Courier, monospace;"> {{selectedMetricEvolution.metric.formula}}  </span> 
            <br>

            Stage:    <span v-bind:style="{ 'color' : selectedMetricEvolution.metric.stageHighlightColor   , 'background-color' : 'white'}"> {{selectedMetricEvolution.metric.stage}}  </span> 
            <br>
            Team:     <span v-bind:style="{ 'color' : selectedMetricEvolution.metric.teamHighlightColor    , 'background-color' : 'white'} "> {{selectedMetricEvolution.metric.team}}  </span>    
            <br>
            Category: <span v-bind:style="{ 'color' : selectedMetricEvolution.metric.categoryHighlightColor, 'background-color' : 'white'}"> {{selectedMetricEvolution.metric.category}}  </span>
            <br>
            <br>

            <span class="box_value">  Current Value: {{selectedMetricEvolution.trend.currentValue}} </span> 
            <br>

            <span class="box_value">  Mean Value: {{selectedMetricEvolution.trend.meanValue}} </span> 
            <br>

            Trend: <span v-bind:style="{ 'background-color' : selectedMetricEvolution.trend.good ? 'green' : 'red'}"> {{selectedMetricEvolution.trend.percentage}} %  </span>
            <br>

            <span>
                <img :src="require('@/assets/img/trend/up_green.png')"    width="20px" v-if="selectedMetricEvolution.trend.trend == 'UP'   && selectedMetricEvolution.trend.good "/>
                <img :src="require('@/assets/img/trend/up_red.png')"      width="20px" v-if="selectedMetricEvolution.trend.trend == 'UP'   && ! selectedMetricEvolution.trend.good "/>
                <img :src="require('@/assets/img/trend/down_green.png')"  width="20px" v-if="selectedMetricEvolution.trend.trend == 'DOWN' && selectedMetricEvolution.trend.good "/>
                <img :src="require('@/assets/img/trend/down_red.png')"    width="20px" v-if="selectedMetricEvolution.trend.trend == 'DOWN' && ! selectedMetricEvolution.trend.good "/>

            </span> 
            <span> 
                <img :src="require('@/assets/img/stability/sunny.png')"   width="30px" v-if="selectedMetricEvolution.trend.stability  == 'SUNNY' "/>
                <img :src="require('@/assets/img/stability/cloudy.png')"  width="30px" v-if="selectedMetricEvolution.trend.stability  == 'CLOUDY' "/>
                <img :src="require('@/assets/img/stability/raining.png')" width="30px" v-if="selectedMetricEvolution.trend.stability  == 'RAINING' "/>
            </span>
            <br> 
            <span> Last Values: {{selectedMetricEvolution.trend.lastValues}}  </span> 
            
          </div>

          
          <div>
              <GChart type="LineChart" :data="selectedMetricEvolution.chart" v-if="selectedMetricEvolution" :options="chartMetricEvolutionOptionsZoom"/>
          </div>

          <!-- Information how the metric was create -->
          <b-card no-body class="mb-1">
            <b-card-header header-tag="header" class="p-1" role="tab">
              <b-button block v-b-toggle.accordion-1 variant="dark">Information Detail</b-button>
            </b-card-header>
            <b-collapse id="accordion-1" accordion="my-accordion" role="tabpanel">
              <b-card-body>
                <div v-html="selectedMetricEvolution.info" style="border: 2px solid #000; padding: 10px; text-align: left; font-size: small; width: 100%; background-color: #f0f0f0;">
            
                </div>
              </b-card-body>
            </b-collapse>
          </b-card>

          
          
          

          <b-button class="mt-3" block @click="hideModalMetricEvolution">Close Me</b-button>

    </b-modal> 

  </div>

</template>

<script>


import MaturityBar from './include/MaturityBar.vue'
import HighlightBar from './include/HighlightBar.vue'

import CIMetrics from './include/CIMetrics.vue'
import DoraMetrics from './include/DoraMetrics.vue'
import PerformanceMetrics from './include/PerformanceMetrics.vue'
import OperationMetrics from './include/OperationMetrics.vue'
// import MetricEvolution from './include/MetricEvolution.vue'
// import ProjectMetrics from './include/ProjectMetrics.vue'
// import MetricCorrelations from './include/MetricCorrelations.vue'


import Loading from '@/components/common/Loading.vue'



export default {

  name: 'MetricEvolutionDashBoard',

  components: {
    MaturityBar,
    HighlightBar,
    CIMetrics,
    DoraMetrics,
    PerformanceMetrics,
    OperationMetrics,
    Loading,
  },

  computed:{
    appName(){ return process.env.VUE_APP_NAME },

    enableLoginPage() { return this.$store.state.login.enableLoginPage;    },

    authenticated() { return this.$store.state.login.authenticated;    },

  }, 

  data() {
    return {

        // the selected project information
        project : {},
        nameWithOwner : '',

        // the period of dashboard
        init : '',
        end : '',

        // if the metric will be normalize by control variables
        normalize : false,

        //////////////////////////////////////////////

        highlight: 'stage',


        ///////////////// Metrics List (one list for each type) ////////////////
        
        projectMetrics : [],

        ciMetricsList : [],

        doraMetricsList : [],

        performanceMetricsList : [],

        operationMetricsList : [],

        ////////////////////////////////////////////////////////////////////////

        metricsEvolutionList : [],        

        metricsCorrelationMatrix : [],


        // to show in modal
        selectedMetricEvolution : {},

        chartMetricEvolutionOptionsZoom: {
          title: 'CI Metrics Evolution',
          curveType: 'function',
          legend: { position: 'bottom' },
          colors:['#0066ff', '#009933', '#6600cc', '#ff9933', '#ff0000',
          '#00cc66', '#1aa3ff', '#666633', '#660033', '#ff99ff',
          '#b30000','#ff1a1a', '#ff0066','#ffff00', '#666699'],
          height: 500,
        },

        //////////////////// DevOps maturity Information /////////////////////////

        maturity : {
          ci : 0,
          dora : 0,
          performance : 0,
          operation : 0,
        },


        /////////////////   for the gauge of CI degree  //////////////////////// 
        
        degreeOfCICount : 0,

        chartDegreeCIData : [
          ['Label', 'Value'],
        ],

        chartDegreeCIOptions : {width: 300, height: 240,
          redFrom: 0, redTo: 0,
          yellowFrom:0, yellowTo: 0,
          greenFrom: 0, greenTo: 0,
          minorTicks: 10, max: 0,
          majorTicks: ['BASE', 'BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT'] },


        //////////////////////////////////////////////


        loading : false,

    }
  },

  methods: {

      /**
       * Execute the dashborad query in the backend (5 requests)
       */
      async executeQueries(){
        this.loading = true
        // clear maturity data
				this.maturity = { ci : 0, dora : 0, performance : 0, operation : 0 };
				
        await this.loadProject(this.nameWithOwner)


        // 5 request to backend
        await this.loadCIMetrics(this.nameWithOwner)
        await this.loadDoraMetrics(this.nameWithOwner)
        await this.loadPerformanceMetrics(this.nameWithOwner)
        await this.loadOperationMetrics(this.nameWithOwner)
        
        // now its evolution
        await this.loadMetricEvolution(this.nameWithOwner)

        this.loading = false
      },


      /**
       * Load project information by name
       */
      loadProject(nameWithOwner){
        this.$http.get('/project/findByFullName/'+nameWithOwner)
          .then(result => { 
              this.project = result.data;
          }).catch(
              () => { this.$toastw.error('Select a Valid Project') }
          )
      },


      loadProjectMetric(nameWithOwner){
        this.$http.get('/control-metric-dashboard/'+nameWithOwner+"?init="+this.init+"&end="+this.end)
          .then(result => { 
              this.projectMetrics = result.data;
          }).catch(
              () => { this.$toastw.error('Select a Valid Project') }
          )
      },


      /**
       * Load CI metrics current values
       */
       loadCIMetrics(nameWithOwner){
  
          this.$http.get('/ci-metric-dashboard/'+nameWithOwner+"?init="+this.init+"&end="+this.end)
          .then(result => { 
              this.ciMetricsList = result.data;

              this.calcDevOpsMaturity(this.ciMetricsList, 'CI');

            }  
          ).catch(
              error => {
                this.$toastw.error(error.data.messageList)
              }
          )
        },



      loadDoraMetrics(nameWithOwner){
        this.$http.get('/dora-metric-dashboard/'+nameWithOwner+"?init="+this.init+"&end="+this.end)
          .then(result => { 
              this.doraMetricsList = result.data;
              this.calcDevOpsMaturity(this.doraMetricsList, 'DORA');
          }).catch(
              () => { this.$toastw.error('Select a Valid Project') }
          )
      },


      loadPerformanceMetrics(nameWithOwner){

          const ctrlMetricJson = new URLSearchParams(JSON.stringify(this.projectMetrics)).toString(); // JSON object with controMetric values

          this.$http.get('/performance-metric-dashboard/'+nameWithOwner+"?init="+this.init+"&end="+this.end+"&normalize="+this.normalize+"&control-metric="+ctrlMetricJson )
            .then(result => { 
                this.performanceMetricsList = result.data;
                this.calcDevOpsMaturity(this.performanceMetricsList, 'PERFORMANCE');
            }).catch(
                () => { this.$toastw.error('Select a Valid Project') }
            )
        },


      loadOperationMetrics(nameWithOwner){

          this.$http.get('/operation-metric-dashboard/'+nameWithOwner+"?init="+this.init+"&end="+this.end)
            .then(result => { 
                this.operationMetricsList = result.data;
                this.calcDevOpsMaturity(this.operationMetricsList, 'OPERATION');
            }).catch(
                () => { this.$toastw.error('Select a Valid Project') }
            )
      },



      


      /**
       * Load Evolution history of the CI metric overt the time
       */
       loadMetricEvolution(nameWithOwner){
          this.$http.get('/evolution-dashboard/'+nameWithOwner+"?init="+this.init+"&end="+this.end)
          .then(result => { 
              this.metricsEvolutionList = result.data;

              if(this.metricsEvolutionList.length > 0){

                // convert each json element in a array
                let temp = this.metricsEvolutionList
                temp.forEach(
                  element => {
                    let a = eval(element.chart);  
                    element.chart = a;
                  }
                );
              }
              // else{
              //   this.$toastw.alert('No CI Evolution Data')
              // }
            }  
          ).catch(
              error => {
                this.$toastw.error(error.data.messageList)
              }
          )
      },

      /**
       * Deprecated, we will not use this anymore
       * 
       * Load Correlation chart data between CI metrics and project quality attributes (n Pull requests and n of Bug related Issues) 
       *
       loadMetricCorrelation(nameWithOwner){
          this.$http.get('/correlation-dashboard/'+nameWithOwner+"?init="+this.init+"&end="+this.end)
          .then(result => { 
              this.metricsCorrelationMatrix = result.data;

              for (let i = 0; i < this.metricsCorrelationMatrix.length; i++) {
                  // get the size of the inner array
                  if(this.metricsCorrelationMatrix[i] !== null){
                    var innerArrayLength = this.metricsCorrelationMatrix[i].length;
                    // loop the inner array
                    for (let j = 0; j < innerArrayLength; j++) {
                      let element  = this.metricsCorrelationMatrix[i][j];

                      if(element != null){
                        // format the data to char
                        // convert string to array
                        let a = eval(element.chartData);  
                        element.chartData = a;
                        // convert string to json
                        let b = JSON.parse(element.chartOptions);  
                        element.chartOptions = b;
                      }
                    }
                 }
              }
            }  
          ).catch(
              () => {
                this.$toastw.error('Error get CI sub-practices correlation')
              }
          )
      }, */


      /**
       * calc quatity of metric with good values
       * @param {*} list 
       * @param {*} metricType 
       */
      calcDevOpsMaturity(list, metricType){

        let totalDoraMetric = (list.length)
        for( var i = 0; i < totalDoraMetric ; i++){
          if( list[i].overReferenceValue && list[i].wasCollected ){
            switch(metricType) {
              case "CI":
              this.maturity.ci = this.maturity.ci + 1
                break;
              case "DORA":
                this.maturity.dora = this.maturity.dora + 1
                break;
              case "PERFORMANCE":
                this.maturity.performance = this.maturity.performance + 1
                break;
             case "OPERATION":
                this.maturity.operation = this.maturity.operation + 1
                break;
            }
          }
        }
      },


      /**
       * configure the default query date.
       * by default get metrics of last 6 month
       */
      configInitQueryDate() {
        this.end = new Date();
        this.init = new Date();
        this.init.setMonth(this.end.getMonth() - 6);

        // convert to spring to component
        this.init = this.init.toISOString().split('T')[0]
        this.end = this.end.toISOString().split('T')[0]
      },

      /**
       * Should called the backend to calc the metric again, but now normalize by control variables
       */
      handleNormalizeMetric(normalizeValue){
        this.normalize = normalizeValue
        this.loadPerformanceMetrics(this.nameWithOwner)
      },


      /** Log who is accessing the dashboard */
      logDashboardAccess(nameWithOwner){
        this.$http.post('/log-dashboard/access/'+nameWithOwner+"?init="+this.init+"&end="+this.end)
          .then(() => { }  ).catch( )
      },

      updateHighlightProp(newValue) {
        // Update the propValue in the parent when notified by the child
        this.highlight = newValue;
      },
      
      /**
       *  Call when the use clic in a botton of show evolution of specific metric
       */
      handleShowMetricEvolution(metricId){
        var findEvolution = false
        for (const evolution of this.metricsEvolutionList) {
          if(evolution.metric.id === metricId){ // we find the evolution data for the selected metric, so show it
            this.showModalMetricEvolution(evolution);
            findEvolution = true
          }
        }
        if(! findEvolution){
          this.$toastw.info('Uncollected metric evolution')
        }
      },


      showModalMetricEvolution(evolution) {
        this.selectedMetricEvolution = evolution
        this.$refs['chart-evolution-modal'].show()
      },

      hideModalMetricEvolution() {
        this.$refs['chart-evolution-modal'].hide()
      },

      cancel() {
        this.$router.push('/metrics/evolution/list');
      },


      /** Check if logged user has the role */
      // hasRole(role){
      //   if(this.loggedInUser){
      //     for(let permission of this.loggedInUser.permission){
      //       if (permission.role.name === role){
      //         return true;
      //       }
      //     }
      //   }
      //   return false;
      // },

      /** get the access data to dashboard */
      exportAccess(){
        this.$http.get('/log-dashboard/access/'+this.nameWithOwner, 
          { 
            headers : {
              'Content-Type' : 'application/json', 'Accept' : 'text/csv; charset=utf-8'
            }, 
            responseType: 'blob'
          })
            .then(result => { 
              const url = window.URL.createObjectURL(new Blob([result.data], { type: 'text/csv' }));
              const link = document.createElement('a');
              link.href = url;
              link.setAttribute('download', 'dashboard-access.csv');
              document.body.appendChild(link);
              link.click();
            }).catch(
                () => { this.$toastw.error('Select a Valid Project') }
            )
      }

     

  },

  async beforeMount(){
      // acess the dashboard using the name of the project
      if( this.$route.params.owner && this.$route.params.name){

        this.configInitQueryDate()
        this.nameWithOwner = this.$route.params.owner+'/'+this.$route.params.name
        
        this.executeQueries()

        // for the study log how many times this dashboard was access
        this.logDashboardAccess(this.nameWithOwner)

      }else{
        this.$toastw.error('Select the Project')
        this.cancel()
      }
  },
  
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
