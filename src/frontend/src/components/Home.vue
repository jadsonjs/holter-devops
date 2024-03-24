<template>
  <div>
    <h1 class="mt-4">{{appName}} <span style="text-align: center;"> <img :src="require('@/assets/img/devops.png')" width="70px"> </span> </h1>
    <p>{{appName}} is a tool that collect and analyze <b>DevOps</b> Metrics. Trying to assess the maturity of DevOps adoption</p>

    <b-card>
      <b-card-text>This tool is part of a doctoral thesis and is still under development.</b-card-text>
      <b-card-text>Therefore, there are no guarantees about its perfect functioning; the methodology contained in it is under evaluation, still without any scientific proof of its efficiency.</b-card-text>
    </b-card>

    <br><br>
    
    <b-card title="License">
    <div>
      <p>Copyright (c) 2023 Jadson Santos - jadson.santos@ufrn.br</p>

      <p>
      Permission is hereby granted, free of charge, to any person obtaining a copy
      of this software and associated documentation files (the "Software"), to deal
      in the Software without restriction, including without limitation the rights
      to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
      copies of the Software, and to permit persons to whom the Software is
      furnished to do so, subject to the following conditions:
      </p>
      <p>
      The above copyright notice and this permission notice shall be included in
      all copies or substantial portions of the Software.
     </p>
      <p>
      THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
      IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
      FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
      AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
      LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
      OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
      THE SOFTWARE.
    </p>
    </div> 
    </b-card> 


     <!-- charts in home 

      <h4> <a href="https://developers-dot-devsite-v2-prod.appspot.com/chart/interactive/docs/gallery?hl=pt_br" target="_blank"> Google Chart Library  </a>  </h4>

      <div class="row">
        <div class="col-md-6">
          <GChart type="ColumnChart" :data="chartData" :options="chartOptions"/>
        </div>
        <div class="col-md-6">
          <GChart type="ScatterChart" :data="chartData2" :options="chartOptions2"/>
        </div>
      </div>

      <div class="row">
        <div class="col-md-12">
          <GChart type="ScatterChart" :data="chartData3" :options="chartOptions3"/>
        </div>
      </div>

       <div class="row">
        <div class="col-md-12">
          <GChart type="BubbleChart" :data="chartData4" :options="chartOptions4"/>
        </div>
      </div>

      -->
      



  </div>
</template>

<script>


// google charts library
//import { GChart } from 'vue-google-charts'


export default {

  name: 'Home',

  components: {
    //GChart,
  },

  computed:{
    appName(){ return process.env.VUE_APP_NAME },
  }, 

  data() {
    return {


        chartData: [
          ['Year', 'Sales', 'Expenses', 'Profit'],
          ['2014', 1000, 400, 200],
          ['2015', 1170, 460, 250],
          ['2016', 660, 1120, 300],
          ['2017', 1030, 540, 350]
        ],
        
        chartOptions: {
            title: 'Company Performance',
            subtitle: 'Sales, Expenses, and Profit: 2014-2017',
        },



        chartData2: [
          ['Age', 'Weight'],
          [ 8,      12],
          [ 4,      5.5],
          [ 11,     14],
          [ 4,      5],
          [ 3,      3.5],
          [ 6.5,    7]
        ],

        chartOptions2: {
            title: 'Age vs. Weight comparison',
            hAxis: {title: 'Age', minValue: 0, maxValue: 15},
            vAxis: {title: 'Weight', minValue: 0, maxValue: 15},
            legend: 'none'
        },


        // See https://developers.google.com/chart/interactive/docs/gallery/trendlines?hl=pt_br
        chartData3: [
          ['Diameter', 'Age'],
          [8, 37], [4, 19.5], [11, 52], [4, 22], [3, 16.5], [6.5, 32.8], [14, 72]
        ],

        chartOptions3: {
            title: 'Age of sugar maples vs. trunk diameter, in inches',
            hAxis: {title: 'Diameter'},
            vAxis: {title: 'Age'},
            legend: 'none',
            trendlines: { 0: {} }    // Draw a trendline for data series 0.
        },

        // See https://developers.google.com/chart/interactive/docs/gallery/bubblechart?hl=pt_br
        chartData4: [
         ['ID', 'Life Expectancy', 'Fertility Rate', 'Region',     'Population'],
         ['CAN',    80.66,              1.67,      'North America',  33739900],
         ['DEU',    79.84,              1.36,      'Europe',         81902307],
         ['DNK',    78.6,               1.84,      'Europe',         5523095],
         ['EGY',    72.73,              2.78,      'Middle East',    79716203],
         ['GBR',    80.05,              2,         'Europe',         61801570],
         ['IRN',    72.49,              1.7,       'Middle East',    73137148],
         ['IRQ',    68.09,              4.77,      'Middle East',    31090763],
         ['ISR',    81.55,              2.96,      'Middle East',    7485600],
         ['RUS',    68.6,               1.54,      'Europe',         141850000],
         ['USA',    78.09,              2.05,      'North America',  307007000]
        ],

        chartOptions4: {
          title: 'Correlation between life expectancy, fertility rate ' +
               'and population of some world countries (2010)',
          hAxis: {title: 'Life Expectancy'},
          vAxis: {title: 'Fertility Rate'},
          bubble: {textStyle: {fontSize: 11}}
        },

    }
  },

  methods: {

      /**
       * Load charts data
       */
      loadChartsData(){
          //this.$http.get('/dashboard/pizza').then(result => { this.pizzaChartData = result.data;}  ).catch(error => {console.log(error.data)})
          //this.$http.get('/dashboard/bar').then  (result => { this.barChartData   = result.data;}  ).catch(error => {console.log(error.data)})
          //this.$http.get('/dashboard/line').then (result => { this.lineChartData  = result.data;}  ).catch(error => {console.log(error.data)})
      },

     

  },

  beforeMount(){
     this.loadChartsData()
  }
  
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
