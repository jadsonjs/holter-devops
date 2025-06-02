<template>
  <div class="card">
    <div class="font-semibold text-xl mb-4">DevOps Monitoring Dashboard</div>

    <!-- ajuda da página 
    <p class="text-ajuda">
      Ajuda da página: Pode-se colocar muitos linhas aqui para ajudar os usuário a entender a funcionalidade presente na
      página<br>
      Lembre-se de novos usuários, que não conhecem o sistema ainda <br>
      Não descreva a tela em si, use esse espaço para descrever as regras de negócio envolvidas no caso de uso<br>
    </p>-->

  </div>

  <loading :loading="loading" />
  <div class="card">
    <!-- 
      dashboard head
    -->
    <h1 class="text-2xl">
      <div class="flex flex-wrap items-center gap-4">
        <!-- Project Information -->
        <div class="w-full md:w-1/3 font-bold">
          Project: <br>
          <u>{{ project.organization }} / {{ project.name }}</u>
        </div>

        <!-- Init Date Picker -->
        <div class="w-full md:w-1/6">
          <div class="flex flex-col">
            <label for="init-date" class="font-semibold">Init:</label>
            <InputText class="w-full " type="date" id="init-date" v-model="init" />
          </div>
        </div>

        <!-- End Date Picker -->
        <div class="w-full md:w-1/6 md:ml-6">
          <div class="flex flex-col">
            <label for="end-date" class="font-semibold">End:</label>
            <InputText class="w-full  " type="date" id="end-date" v-model="end" />
          </div>
        </div>

        <!-- Refresh Button -->
        <div class="w-full md:w-1/6 md:mt-6 md:ml-6 text-center md:text-left">
          <Button class="mt-2 md:mt-0" variant="primary" size="large" rounded v-on:click="executeQueries()"
            title="Refresh" :disabled="loading">
            <i class="fa-solid fa-sync-alt"></i>
          </Button>
        </div>
      </div>
    </h1>

    <MaturityBar :ciMetricsList="ciMetricsList" :doraMetricsList="doraMetricsList"
      :performanceMetricsList="performanceMetricsList" :operationMetricsList="operationMetricsList"
      :maturity="maturity" />

    <HighlightBar :highlight="highlight" @updateHighlightProp="updateHighlightProp" />

    <!-- 
      == dashboard 
    -->



    <!--
         == TOP of dashboard
    -->
    <div class="mt-8 p-2">
      <PerformanceMetrics :performanceMetricsList="performanceMetricsList" :highlight="highlight"
        @showMetricEvolution="handleShowMetricEvolution" />
    </div>

    <div class="mt-4 p-2 grid grid-cols-1 md:grid-cols-2 gap-4">
      <div class="col-span-1">
        <CIMetrics :ciMetricsList="ciMetricsList" :highlight="highlight" :chartDegreeCIData="chartDegreeCIData"
          :chartDegreeCIOptions="chartDegreeCIOptions" @showMetricEvolution="handleShowMetricEvolution"> </CiMetrics>
      </div>

      <div class="col-span-1">
        <div class="space-y-4">
          <DoraMetrics :doraMetricsList="doraMetricsList" :highlight="highlight"
            @showMetricEvolution="handleShowMetricEvolution"> </DoraMetrics>
          <OperationMetrics :operationMetricsList="operationMetricsList" :highlight="highlight"
            @showMetricEvolution="handleShowMetricEvolution"> </OperationMetrics>
        </div>
      </div>

      <div class="flex mt-4">
        <Button label="Cancel" icon="fa-solid fa-times" class="p-button-outlined p-button-dark" @click="cancel()" />
      </div>


      <Dialog class="flex justify-center items-center" v-model:visible="chartEvolutionModal" modal header="Metric Data"
        style="width: 70%; height: 100%">
        <div v-if="Object.keys(selectedMetricEvolution).length > 0" class="evolution_info_box p-4 rounded-md">
          <h2 class="box_title text-xl font-bold underline mb-2">{{ selectedMetricEvolution.metric.denomination }}</h2>
          <p class="italic">{{ selectedMetricEvolution.metric.description }}</p>
          <p>Unit: {{ selectedMetricEvolution.metric.unit }}</p>
          <p>
            Formula: <span class="font-mono">{{ selectedMetricEvolution.metric.formula }}</span>
          </p>
          <p>
            Stage:
            <span :style="{ color: selectedMetricEvolution.metric.stageHighlightColor }" class="bg-white px-2 rounded">
              {{ selectedMetricEvolution.metric.stage }}
            </span>
          </p>
          <p>
            Team:
            <span :style="{ color: selectedMetricEvolution.metric.teamHighlightColor }" class="bg-white px-2 rounded">
              {{ selectedMetricEvolution.metric.team }}
            </span>
          </p>
          <p>
            Category:
            <span :style="{ color: selectedMetricEvolution.metric.categoryHighlightColor }"
              class="bg-white px-2 rounded">
              {{ selectedMetricEvolution.metric.category }}
            </span>
          </p>

          <div class="mt-4">
            <p class="box_value">Current Value: {{ selectedMetricEvolution.trend.currentValue }}</p>
            <p class="box_value">Mean Value: {{ selectedMetricEvolution.trend.meanValue }}</p>
            <p class="flex justify-center items-center ">
              Trend:
              <span :class="selectedMetricEvolution.trend.good ? 'bg-green-500' : 'bg-red-500'"
                class="ml-1 px-2 py-1 text-white rounded">
                {{ selectedMetricEvolution.trend.percentage }}%
              </span>
            </p>
            <div class="flex justify-center items-center mt-2 space-x-2">
              <img v-if="selectedMetricEvolution.trend.trend === 'UP' && selectedMetricEvolution.trend.good"
                src="@/assets/img/trend/up_green.png" alt="Trend Up Green" class="w-5" />
              <img v-if="selectedMetricEvolution.trend.trend === 'UP' && !selectedMetricEvolution.trend.good"
                src="@/assets/img/trend/up_red.png" alt="Trend Up Red" class="w-5" />
              <img v-if="selectedMetricEvolution.trend.trend === 'DOWN' && selectedMetricEvolution.trend.good"
                src="@/assets/img/trend/down_green.png" alt="Trend Down Green" class="w-5" />
              <img v-if="selectedMetricEvolution.trend.trend === 'DOWN' && !selectedMetricEvolution.trend.good"
                src="@/assets/img/trend/down_red.png" alt="Trend Down Red" class="w-5" />
              <img v-if="selectedMetricEvolution.trend.stability === 'SUNNY'" src="@/assets/img/stability/sunny.png"
                alt="Sunny" class="w-7" />
              <img v-if="selectedMetricEvolution.trend.stability === 'CLOUDY'" src="@/assets/img/stability/cloudy.png"
                alt="Cloudy" class="w-7" />
              <img v-if="selectedMetricEvolution.trend.stability === 'RAINING'" src="@/assets/img/stability/raining.png"
                alt="Raining" class="w-7" />
            </div>
            <p class="">Last Values: {{ selectedMetricEvolution.trend.lastValues }}</p>
          </div>
        </div>

        <div class="card flex justify-center items-center">
          <Chart class="chart-container" type="line" :data="selectedMetricEvolution.chart"
            :options="chartMetricEvolutionOptionsZoom" v-if="selectedMetricEvolution">
          </Chart>
        </div>

        <Accordion class="mt-6 mb-3">
          <AccordionPanel value="0">
            <AccordionHeader><span class="mr-3">Information Detail</span></AccordionHeader>
            <AccordionContent class="mt-2">
              <div v-html="selectedMetricEvolution.info" class="border-2 border-black p-4 text-base"></div>
            </AccordionContent>
          </AccordionPanel>
        </Accordion>

        <Button class="mt-3" label="Close Me" @click="hideModalMetricEvolution" />
      </Dialog>

    </div>
  </div>

</template>

<script setup>
import { ref, onBeforeMount } from 'vue'

import Loading from '@/components/base/Loading.vue';
const loading = ref(false)

// rotas
import { useRouter, useRoute } from 'vue-router'
const router = useRouter()
const route = useRoute();

// axios
import { useHttp } from '@/utils/axiosUtils';
const { http } = useHttp();

// messages
import { useMessage } from '@/utils/message';
const { message } = useMessage();

import MaturityBar from './include/MaturityBar.vue'
import HighlightBar from './include/HighlightBar.vue'
import CIMetrics from './include/CIMetrics.vue'
import DoraMetrics from './include/DoraMetrics.vue'
import PerformanceMetrics from './include/PerformanceMetrics.vue'
import OperationMetrics from './include/OperationMetrics.vue'


// the selected project information
const project = ref({})
const nameWithOwner = ref('')

// the period of dashboard
const init = ref('')
const end = ref('')

const normalize = ref(false)

const highlight = ref('stage')

///////////////// Metrics List (one list for each type) ////////////////

const projectMetrics = ref([])
const ciMetricsList = ref([])
const doraMetricsList = ref([])
const performanceMetricsList = ref([])
const operationMetricsList = ref([])

////////////////////////////////////////////////////////////////////////

const metricsEvolutionList = ref([])


// to show in modal
const selectedMetricEvolution = ref({})

const chartMetricEvolutionOptionsZoom = {
  maintainAspectRatio: false,
  aspectRatio: 2,
  plugins: {
    title: {
      display: true,
      text: 'CI Metrics Evolution'
    },
    legend: {
      position: 'bottom',
    },
    layout: {
    },
  },
  scales: {
    x: {
      ticks: {
        maxTicksLimit: 3,
      },
      offset: true, // Adds spacing to ensure ticks fit the graph's width
    }
  },
  /* Property originally specified in the original graph
  colors: ['#0066ff', '#009933', '#6600cc', '#ff9933', '#ff0000',
    '#00cc66', '#1aa3ff', '#666633', '#660033', '#ff99ff',
    '#b30000', '#ff1a1a', '#ff0066', '#ffff00', '#666699'],
    */
}

const chartEvolutionModal = ref(false)

//////////////////// DevOps maturity Information /////////////////////////
const maturity = ref({
  ci: 0,
  dora: 0,
  performance: 0,
  operation: 0,
})

/////////////////   for the gauge of CI degree  //////////////////////// 
const chartDegreeCIData = ref([
  ['Label', 'Value'],
])

const chartDegreeCIOptions = ref({
  width: 300, height: 240,
  redFrom: 0, redTo: 0,
  yellowFrom: 0, yellowTo: 0,
  greenFrom: 0, greenTo: 0,
  minorTicks: 10, max: 0,
  majorTicks: ['BASE', 'BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT']
})


/**
 * Execute the dashborad query in the backend (5 requests)
 */
const executeQueries = async () => {
  loading.value = true
  // clear maturity data

  maturity.value = { ci: 0, dora: 0, performance: 0, operation: 0 }
  await loadProject(nameWithOwner.value)

  // 5 request to backend
  await loadCIMetrics(nameWithOwner.value)
  await loadDoraMetrics(nameWithOwner.value)
  await loadPerformanceMetrics(nameWithOwner.value)
  await loadOperationMetrics(nameWithOwner.value)

  // now its evolution
  await loadMetricEvolution(nameWithOwner.value)

  message.success("Success", "Metrics retrieved successfully")

  loading.value = false


}


/**
 * Load project information by name
 */
const loadProject = async (nameWithOwner) => {
  loading.value = true
  http
    .get('/api/project/findByFullName/' + nameWithOwner)
    .then((result) => {
      project.value = result.data
    }).catch((error) => {
      message.error('Error', error.data?.messageList)
    }).finally(() => (loading.value = false))
}


/**
 * Load CI metrics current values
 */
const loadCIMetrics = async (nameWithOwner) => {
  loading.value = true
  http
    .get('/api/ci-metric-dashboard/' + nameWithOwner + "?init=" + init.value + "&end=" + end.value)
    .then((result) => {
      ciMetricsList.value = result.data;
      calcDevOpsMaturity(ciMetricsList.value, 'CI');

    }).catch((error) => {
      message.error('Error', error.data?.messageList)
    }).finally(() => (loading.value = false))
}

const loadDoraMetrics = async (nameWithOwner) => {
  loading.value = true
  http
    .get('/api/dora-metric-dashboard/' + nameWithOwner + "?init=" + init.value + "&end=" + end.value)
    .then((result) => {
      doraMetricsList.value = result.data;
      calcDevOpsMaturity(doraMetricsList.value, 'DORA');

    }).catch((error) => {
      message.error('Error', error.data?.messageList)
    }).finally(() => (loading.value = false))
}


const loadPerformanceMetrics = async (nameWithOwner) => {

  const ctrlMetricJson = new URLSearchParams(JSON.stringify(projectMetrics.value)).toString(); // JSON object with controMetric values

  loading.value = true
  http
    .get('/api/performance-metric-dashboard/' + nameWithOwner + "?init=" + init.value + "&end=" + end.value + "&normalize=" + normalize.value + "&control-metric=" + ctrlMetricJson)
    .then((result) => {
      performanceMetricsList.value = result.data;
      calcDevOpsMaturity(performanceMetricsList.value, 'PERFORMANCE');

    }).catch((error) => {
      message.error('Error', error.data?.messageList)
    }).finally(() => (loading.value = false))
}


const loadOperationMetrics = async (nameWithOwner) => {
  loading.value = true
  http
    .get('/api/operation-metric-dashboard/' + nameWithOwner + "?init=" + init.value + "&end=" + end.value)
    .then((result) => {
      operationMetricsList.value = result.data;
      calcDevOpsMaturity(operationMetricsList.value, 'OPERATION');

    }).catch((error) => {
      message.error('Error', error.data?.messageList)
    }).finally(() => (loading.value = false))
}


/**
 * Load Evolution history of the CI metric overt the time
 */
const loadMetricEvolution = async (nameWithOwner) => {
  loading.value = true
  http
    .get('/api/evolution-dashboard/' + nameWithOwner + "?init=" + init.value + "&end=" + end.value)
    .then((result) => {
      metricsEvolutionList.value = result.data;


      if (metricsEvolutionList.value.length > 0) {

        // convert each json element in a array
        let temp = metricsEvolutionList.value
        temp.forEach(
          element => {
            let parsedData = eval(element.chart);
            // Extract labels and datasets
            const labels = parsedData.slice(1).map(row => row[0]); // Skip header row for labels
            const data = parsedData.slice(1).map(row => row[1]);   // Skip header row for values


            element.chart = {
              labels, // X-axis labels
              datasets: [
                {
                  label: parsedData[0][1], // Use the second column header as the dataset label
                  data: data, // Y-axis data points
                  fill: false,
                  tension: 0.4,
                },
              ],
            }
          }

        );
      }
      // else{
      //   this.$toastw.alert('No CI Evolution Data')
      // }
    }
    ).catch((error) => {
      message.error('Error', error.data?.messageList)
    }).finally(() => (loading.value = false))
}



/**
 * Go to backend to get the status of collector, if it was alredy executed (has a HistoryMetric)
 */
const loadCollectorStatus = async (projectId, uuids) => {
  loading.value = true

  // clear maturity data


  http
    .get('api/collector/status/' + projectId + '/' + uuids)
    .then((result) => {
      collectorsStatus.value = result.data
    }).catch((error) => {
      message.error('Error', error.data?.messageList)
    }).finally(() => (loading.value = false))
}


/**
 * calc quatity of metric with good values
 * @param {*} list 
 * @param {*} metricType 
 */
const calcDevOpsMaturity = (list, metricType) => {
  let totalDoraMetric = (list.length)

  for (var i = 0; i < totalDoraMetric; i++) {
    if (list[i].overReferenceValue && list[i].wasCollected) {
      switch (metricType) {
        case "CI":
          maturity.value.ci = maturity.value.ci + 1
          break;
        case "DORA":
          maturity.value.dora = maturity.value.dora + 1
          break;
        case "PERFORMANCE":
          maturity.value.performance = maturity.value.performance + 1
          break;
        case "OPERATION":
          maturity.value.operation = maturity.value.operation + 1
          break;
      }
    }
  }
}

/**
 * configure the default query date.
 * by default get metrics of last 6 month
 */
const configInitQueryDate = () => {
  end.value = new Date();
  init.value = new Date();
  init.value.setMonth(end.value.getMonth() - 6);

  // convert to spring to component
  init.value = init.value.toISOString().split('T')[0]
  end.value = end.value.toISOString().split('T')[0]
}

const logDashboardAccess = async (nameWithOwner) => {
  http
    .post('api/log-dashboard/access/' + nameWithOwner + "?init=" + init.value + "&end=" + end.value)
    .then(() => { })
    .catch((error) => {
      message.error('Error', error.data?.messageList)
    })
}

// Update the propValue in the parent when notified by the child
const updateHighlightProp = (newValue) => {
  highlight.value = newValue
}


/**
 *  Call when the use clic in a botton of show evolution of specific metric
 */
const handleShowMetricEvolution = (metricId) => {
  const evolution = metricsEvolutionList.value.find(evolution => evolution.metric.id === metricId);
  if (evolution) { // we find the evolution data for the selected metric, so show it
    showModalMetricEvolution(evolution);
  }
  else {
    message.info("", "Uncollected metric evolution")

  }
}

const showModalMetricEvolution = (evolution) => {
  selectedMetricEvolution.value = evolution
  chartEvolutionModal.value = true
}

const hideModalMetricEvolution = () => {
  chartEvolutionModal.value = false
}


const cancel = () => {
  router.push('/metrics/evolution/list')
}

onBeforeMount(() => {
  // acess the dashboard using the name of the project
  if (route.params.owner && route.params.name) {

    configInitQueryDate()
    nameWithOwner.value = route.params.owner + '/' + route.params.name

    executeQueries()

    // for the study log how many times this dashboard was access
    logDashboardAccess(nameWithOwner.value)
  }
  else {
    message.error('Error', 'Select the Project')
    cancel()
  }
})
</script>

<style lang="scss" scoped>
.p-accordionheader {
  justify-content: center;
}

.chart-container {
  position: relative;
  margin: auto;
  height: 55vh;
  width: 60vw;
}
</style>