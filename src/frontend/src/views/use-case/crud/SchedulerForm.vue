<template>
  <div class="card">
    <div class="font-semibold text-xl mb-4">Scheduler</div>

    <!-- ajuda da página -->
    <p class="text-ajuda">
      Configure the Scheduler Data for 
			<b v-if="scheduler && scheduler.project"> {{ scheduler.project.organization }} / {{scheduler.project.name }} </b>
    </p>

  </div>

  <div class="card min-h-screen">
    <Fluid>
      <Panel>

        <loading :loading="loading" />

        <div class="flex flex-col md:flex-row gap-8">

          <div class="md:w-1/2" v-if="!loading">
            <div class="font-semibold text-xl">Project Data</div>
            <div class="card flex flex-col gap-4">

              <div class="flex flex-col gap-2">
                <label for="frequency" description="Select Frequency of Execution">Select Frequency of Execution</label>
                <Select name="frequency" v-model="scheduler.frequencyOfExecution" :options="['WEEK', 'MONTH', 'YEAR']"
                  :disabled="scheduler.id > 0">
                </Select>
              </div>

              <div class="flex flex-col gap-2">
                <label for="start" description="Write the start of execution for the scheduler">Start Execution
                  At:</label>
                <DatePicker name="start" v-model="scheduler.startExecution" dateFormat="dd/mm/yy" :showTime="false"
                  :disabled="scheduler.id > 0" placeholder="dd/mm/yyyy">
                </DatePicker>
              </div>


              <div class="flex flex-col gap-2">

                <label for="collectors" description="Enable or disable metrics collection for the project">Select a
                  Collector</label>
                <Select name="collectors" v-model="selectedCollectorId" :options="collectorList"
                  placeholder="Select a collector" @change="addCollectorToScheduler" filter
                  :filterFields="['repository.name', 'metric.stage', 'metric.denomination']">
                  <template #option="{ option }">
                    [ {{ option.repository.name }} ] [ {{ option.metric.stage }} ] - {{
                      option.metric.denomination }} ( {{ option.description }} )
                  </template>
                </Select>
              </div>

              <!-- Collectors -->
              <DataTable stripedRows class="mt-1 mb-3" ref="dt" :value="scheduler.collectorsId.map(id => ({ id }))">
                <template #header>
                  <div class="flex flex-wrap gap-2 items-center">
                    <h4 class="m-0">Collectors:</h4>
                  </div>
                </template>

                <Column field="#" header="#" sortable style="min-width: 6rem">
                  <template #body="slotProps">
                    {{ slotProps.index + 1 }}
                  </template>
                </Column>

                <Column field="collector" header="Collector" style="min-width: 12rem">
                  <template #body="slotProps">
                    {{ getCollectorName(slotProps.data.id) }}
                  </template>
                </Column>

                <Column field="action" header="Actions" style="min-width: 6rem">
                  <template #body="slotProps">
                    <button class="btn btn-default" type="button"
                      @click="removeCollectorFromScheduler(slotProps.data.id)">
                      <i class="fas fa-minus-circle"></i>
                    </button>
                  </template>
                </Column>
              </DataTable>

              <div class="mt-1 flex flex-col gap-2">
                <div class="flex items-center gap-2">

                  <label for="switch1" class="mr-2" description="Enable automatic collection?">Enable automatic
                    collection?</label>
                  <Ajuda
                    texto="Leave the toggler on if Holter should collect metrics automatically for this project." />

                </div>

                <ToggleSwitch name="switch1" id="switch1" v-model="scheduler.automatic" />
              </div>

            </div>

            <div class="flex justify-end space-x-4">
              <Button label="Cancel" icon="fa-solid fa-xmark" text @click="cancel" :style="{ width: '10em' }" />
              <Button label="Save" icon="fa-solid fa-floppy-disk" @click="save" :disabled="loading"
                :style="{ width: '10em' }" />
            </div>

          </div>

        </div>
      </Panel>
    </Fluid>
  </div>

</template>

<script setup>
import { ref, onBeforeMount, computed } from 'vue'

import Loading from '@/components/base/Loading.vue';

// rotas
import { useRouter, useRoute } from 'vue-router'
const router = useRouter()
const route = useRoute();

// axios
import { useHttp } from '@/utils/axiosUtils';
const { http } = useHttp();

// mensagens
import { useMessage } from '@/utils/message';
const { message } = useMessage();

// Object we are creating
const scheduler = ref({
  frequencyOfExecution: 'MONTH',
  automatic: false,
  collectorsId: [],
})

// collector selected to be add to a scheduler, collector define what metric will be collected
const selectedCollectorId = ref()

/** List of collectors that can be selected in the Select component */
const collectorList = ref([])

const loading = ref(false)


// Collectors
import { storeToRefs } from 'pinia'
import { useCollectorsStore } from '@/stores/collectors'
const collectorsStore = useCollectorsStore()
/** Original all  collector list */
const { getCollectorsList: allCollectorList } = storeToRefs(collectorsStore)


/**
* Load the Scheduler Data
*/
const loadScheduler = async () => {

  // if exist a param "idProjeto" we are creating a new scheduler 
  if (route.params.idProjeto) {

    loading.value = true

    http
      .get(`/api/project/${route.params.idProjeto}`)
      .then((result) => {
        scheduler.value.project = result.data
        loadCollectors()
      }).catch((error) => {
        message.error('Erro', error.data?.messageList)
        scheduler.value = []
      }).finally(() => (loading.value = false))
  }
  else {
    // editing a existent scheduler

    loading.value = true

    http
      .get(`/api/scheduler/${route.params.id}`)
      .then((result) => {
        scheduler.value = result.data
        loadCollectors()
      }).catch((error) => {
        message.error('Erro', error.data?.messageList)
      }).finally(() => (loading.value = false))

  }
}

/**
 * Load all collectors supported by the tool
 *
 */
const loadCollectors = async () => {
  collectorList.value = allCollectorList.value

  // remove collectors already added to the scheduler 
  for (let i = 0; i < scheduler.value.collectorsId.length; i++) {
    removeCollectorFromSelectList(scheduler.value.collectorsId[i])
  }
}

/**
 * Call the back-end to save scheduler data.
 *
 */
const save = async () => {
  loading.value = true
  http
    .post('/api/scheduler/save', scheduler.value)
    .then(() => {
      message.success('Saved', 'Scheduler saved successfully!')
      cancel()
    }).catch((error) => {
      message.error('Error', error.data?.messageList)
    }).finally(() => (loading.value = false))
}

const cancel = () => {
  router.push("/scheduler/view/" + scheduler.value.project.id)
}

/**
 * Add a collector to scheduler by selecting it
 * 
 */
const addCollectorToScheduler = () => {
  if (selectedCollectorId.value) {
    if (!containsCollector(selectedCollectorId.value.id)) {
      scheduler.value.collectorsId.push(selectedCollectorId.value.id);
      removeCollectorFromSelectList(selectedCollectorId.value.id);
      selectedCollectorId.value = null
    }
  }
}

/**
 * Remove a collector from scheduler
 * @param id the collectors's id
 */
const removeCollectorFromScheduler = (id => {
  if (scheduler.value.collectorsId) {
    scheduler.value.collectorsId = scheduler.value.collectorsId.filter(colId => colId !== id);
  }
  addCollectorToSelectList(id);
})


/**
 * Remove a collector that was already added to scheduler from the list of selectable collectors (in the Select component)
 * @param id the collector's id
 */
const removeCollectorFromSelectList = (id => {
  collectorList.value = collectorList.value.filter(collector => collector.id !== id);
  sortCollectorList();
})

/**
 * Add again a collector to the list of selectionable collectors when it's removed from the scheduler
 * @param id  the collector's id
 */

const addCollectorToSelectList = (id => {
  const collector = allCollectorList.value.find(col => col.id === id);
  if (collector) {
    collectorList.value.push(collector);
    sortCollectorList();
  }
})

const sortCollectorList = () => {
  collectorList.value.sort((a, b) => {
    const aKey = `${a.repository.name.toLowerCase()}${a.metric.stage.toLowerCase()}${a.metric.denomination.toLowerCase()}`;
    const bKey = `${b.repository.name.toLowerCase()}${b.metric.stage.toLowerCase()}${b.metric.denomination.toLowerCase()}`;
    return aKey.localeCompare(bKey);
  });
}

/**
 * Check if the scheduler has a collector
 * @param id the collector's id
 */
const containsCollector = (id => {
  return scheduler.value.collectorsId.includes(id);
})

const getCollectorName = (id => {
  const collector = allCollectorList.value.find(col => col.id === id);
  if (collector) {
    return `[${collector.repository.name}] [${collector.metric.stage}] ${collector.metric.denomination} (${collector.description})`;
  }
  return '';
})

onBeforeMount(() => {
  loadScheduler()
})

</script>

<style lang="scss" scoped></style>