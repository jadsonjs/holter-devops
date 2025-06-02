<template>
  <div class="card">
    <div class="font-semibold text-xl mb-4">Repository Monitoring Dashboard</div>
  </div>

  <loading :loading="loading" />
  <div class="card">
    <!-- dashboard head -->
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
            <InputText class="w-full" type="date" id="init-date" v-model="init" />
          </div>
        </div>

        <!-- End Date Picker -->
        <div class="w-full md:w-1/6 md:ml-6">
          <div class="flex flex-col">
            <label for="end-date" class="font-semibold">End:</label>
            <InputText class="w-full" type="date" id="end-date" v-model="end" />
          </div>
        </div>

        <!-- Refresh Button -->
        <div class="w-full md:w-1/6 md:mt-6 md:ml-6 text-center md:text-left">
          <Button class="mt-2 md:mt-0" variant="primary" size="large" rounded @click="executeQueries()" title="Refresh" :disabled="loading">
            <i class="fa-solid fa-sync-alt"></i>
          </Button>
        </div>
      </div>
    </h1>

    <!-- TOP of dashboard -->
    <div class="mt-8 p-2">
      <RepositoryMetrics :performanceMetricsList="performanceMetricsList" :highlight="highlight" @showMetricEvolution="handleShowMetricEvolution" />
    </div>

    <div class="mt-4 p-2 grid grid-cols-1 md:grid-cols-2 gap-4">

      <div class="flex mt-4">
        <Button label="Cancel" icon="fa-solid fa-times" class="p-button-outlined p-button-dark" @click="cancel()" />
      </div>

      <Dialog v-model:visible="chartEvolutionModal" modal header="Metric Data" style="width: 70%; height: 100%" class="flex justify-center items-center">
        <div v-if="Object.keys(selectedMetricEvolution).length" class="evolution_info_box p-4 rounded-md">
          <h2 class="box_title text-xl font-bold underline mb-2">{{ selectedMetricEvolution.metric.denomination }}</h2>
          <p class="italic">{{ selectedMetricEvolution.metric.description }}</p>
          <p>Unit: {{ selectedMetricEvolution.metric.unit }}</p>
          <p>Formula: <span class="font-mono">{{ selectedMetricEvolution.metric.formula }}</span></p>
          <p>Stage: <span :style="{ color: selectedMetricEvolution.metric.stageHighlightColor }" class="bg-white px-2 rounded">{{ selectedMetricEvolution.metric.stage }}</span></p>
          <p>Team: <span :style="{ color: selectedMetricEvolution.metric.teamHighlightColor }" class="bg-white px-2 rounded">{{ selectedMetricEvolution.metric.team }}</span></p>
          <p>Category: <span :style="{ color: selectedMetricEvolution.metric.categoryHighlightColor }" class="bg-white px-2 rounded">{{ selectedMetricEvolution.metric.category }}</span></p>
        </div>

        <div class="card flex justify-center items-center">
          <Chart class="chart-container" type="bar" :data="selectedMetricEvolution.chart" :options="chartMetricEvolutionOptionsZoom" v-if="selectedMetricEvolution" />
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

import Loading from '@/components/base/Loading.vue'
import { useRouter, useRoute } from 'vue-router'
import { useHttp } from '@/utils/axiosUtils'
import { useMessage } from '@/utils/message'

import PerformanceMetrics from './include/PerformanceMetrics.vue'
import RepositoryMetrics from './include/RepositoryMetrics.vue'

const loading = ref(false)
const project = ref({})
const nameWithOwner = ref('')
const init = ref('')
const end = ref('')
const highlight = ref('stage')

const performanceMetricsList = ref([])
const metricsEvolutionList = ref([])
const selectedMetricEvolution = ref({})
const chartMetricEvolutionOptionsZoom = {
  maintainAspectRatio: false,
  aspectRatio: 2,
  indexAxis: 'y',
  plugins: { title: { display: true, text: 'Repository Metrics Evolution' }, legend: { position: 'bottom' } },
  scales: { x: { ticks: { maxTicksLimit: 10 }, beginAtZero: true } }
}
const chartEvolutionModal = ref(false)

const { http } = useHttp()
const { message } = useMessage()
const router = useRouter()
const route = useRoute()

const executeQueries = async () => {
  loading.value = true
  await loadProject()
  await loadRepositoryMetrics()
  await loadMetricEvolution()
  message.success('Success', 'Metrics retrieved successfully')
  loading.value = false
}

const loadProject = async () => {
  loading.value = true
  http.get(`/api/project/findByFullName/${nameWithOwner.value}`)
    .then(res => project.value = res.data)
    .catch(err => message.error('Error', err.data?.messageList))
    .finally(() => loading.value = false)
}


async function loadRepositoryMetrics() {
  loading.value = true
  const ctrlMetricJson = new URLSearchParams(JSON.stringify([])).toString()
  await http.get(`/api/repository-metric-dashboard/${nameWithOwner.value}?init=${init.value}&end=${end.value}&normalize=false&control-metric=${ctrlMetricJson}`)
    .then(res => performanceMetricsList.value = res.data)
    .catch(err => message.error('Error', err.data?.messageList))
    .finally(() => loading.value = false)
}


async function loadMetricEvolution() {
  loading.value = true
  await http.get(`/api/repository-evolution-dashboard/${nameWithOwner.value}?init=${init.value}&end=${end.value}`)
    .then(res => {
      metricsEvolutionList.value = res.data.map(e => {
        const parsed = eval(e.chart)
        return { ...e, chart: { labels: parsed.slice(1).map(r=>r[0]), datasets: [{ label: parsed[0][1], data: parsed.slice(1).map(r=>r[1]), fill:false, tension:0.4 }] } }
      })
    })
    .catch(err => message.error('Error', err.data?.messageList))
    .finally(() => loading.value = false)
}

const handleShowMetricEvolution = id => {
  const evo = metricsEvolutionList.value.find(x=>x.metric.id===id)
  evo ? (selectedMetricEvolution.value = evo, chartEvolutionModal.value = true) : message.info('', 'Uncollected metric evolution')
}
const hideModalMetricEvolution = () => chartEvolutionModal.value = false
const cancel = () => router.push('/repository/evolution/list')

onBeforeMount(() => {
  if (route.params.owner && route.params.name) {
    init.value = new Date(new Date().setMonth(new Date().getMonth()-6)).toISOString().split('T')[0]
    end.value = new Date().toISOString().split('T')[0]
    nameWithOwner.value = `${route.params.owner}/${route.params.name}`
    executeQueries()
  } else {
    message.error('Error', 'Select the Project')
    cancel()
  }
})
</script>

<style lang="scss" scoped>
.p-accordionheader { justify-content: center; }
.chart-container { position: relative; margin: auto; height: 55vh; width: 60vw; }
</style>