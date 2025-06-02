<template>
  <div class="card">
    <div class="font-semibold text-xl mb-4">Execute Metric Collection Manually</div>

    <!-- ajuda da página -->
    <p class="text-ajuda">
      This operation execute manually a new collection of all metrics scheduled to the project
    </p>

  </div>
  <div class="card min-h-screen">
    <loading :loading="loading" />


    <div v-if="!loading && scheduler?.id">

      <div class="font-semibold text-xl">Scheduler of the Project: {{ scheduler && scheduler.project ?
        scheduler.project.organization + " / " + scheduler.project.name : "" }}
      </div>

      <div class="card flex flex-col gap-4">
        <Panel>

          <SchedulerDetails :scheduler="scheduler" />
          <!-- COLLECTORS -->
          <DataTable ref="dt" :value="schedulerCollectorsList">
            <template #header>
              <div class="flex flex-wrap gap-2 items-center justify-between">
                <h4 class="m-0">Collectors:</h4>
              </div>
            </template>

            <Column header="#">
              <template #body="slotProps">
                <div :class="{ 'text-gray-500': !isCollectorExecuted(slotProps.data.id) }">

                  {{ slotProps.index + 1 }}
                </div>
              </template>
            </Column>

            <Column field="repository.name" header="Repository" sortable>
              <template #body="slotProps">
                <div :class="{ 'text-gray-500': !isCollectorExecuted(slotProps.data.id) }">
                  {{ slotProps.data.repository.name }}
                </div>
              </template>

            </Column>

            <Column field="metric.stage" header="Stage" sortable>
              <template #body="slotProps">
                <div :class="{ 'text-gray-500': !isCollectorExecuted(slotProps.data.id) }">
                  {{ slotProps.data.metric.stage }}
                </div>
              </template>

            </Column>

            <Column field="description" header="Name" sortable>
              <template #body="slotProps">
                <div :class="{ 'text-gray-500': !isCollectorExecuted(slotProps.data.id) }">
                  {{ slotProps.data.metric.denomination }} ({{ slotProps.data.description }})
                </div>
              </template>
            </Column>

            <Column header="Status">
              <template #body="slotProps">
                <div :class="{ 'text-gray-500': !isCollectorExecuted(slotProps.data.id) }">
                  <span class="text-muted font-italic text-sm-center"> {{ printCollectorStatus(slotProps.data.id) }}
                  </span>
                </div>
              </template>
            </Column>
          </DataTable>

          <div class="px-4 mt-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
            <div class="font-semibold text-lg">Next collection Cycle:</div>
          </div>
          <Panel class="mt-1">
            <div class="flex justify-around ">
              <span class="flex justify-between">
                <p class="text-xl font-semibold mr-2"> From: </p> {{ period.init }}
              </span>
              <span class="flex justify-between">
                <p class="text-xl font-semibold mr-2">To: </p> {{ period.end }}
              </span>
            </div>
          </Panel>

        </Panel>
      </div>
    </div>


    <div class="flex justify-end mr-12">
      <Button label="Mine" icon="fas fa-play-circle" @click="mine()" :disabled="loading" :style="{ width: '10rem' }"
        v-if="scheduler" />
    </div>

    <div class="flex">
      <Button label="Cancel" icon="fa-solid fa-times" class="p-button-outlined p-button-dark" @click="cancel()" />
    </div>


  </div>
</template>

<script setup>
import { ref, onBeforeMount, computed } from 'vue'

import Loading from '@/components/base/Loading.vue';
// se o processamento esta ocorrendo ao salvar; os dados estão sendo carregados
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

// project
const project = ref(null)

// scheduler
import SchedulerDetails from '@/components/use-case/SchedulerDetails.vue';
const scheduler = ref()

// period of collection
const period = ref(null)

const cycleNoCompleted = ref(false)

const collectorsStatus = ref([])

// collectors
import { storeToRefs } from 'pinia'
import { useCollectorsStore } from '@/stores/collectors'
const collectorsStore = useCollectorsStore()
const { getCollectorsList } = storeToRefs(collectorsStore)


// all collectors belonging to this scheduler
const schedulerCollectorsList = computed(() => {
  // Filtra os collectors que pertencem ao scheduler
  return getCollectorsList.value.filter(collector => scheduler.value.collectorsId.includes(collector.id))
});


/**
 * Calculate and Load next mine period
 */
const loadNextMinerPeriod = async () => {
  if (route.params.id) {
    loading.value = true;

    try {
      const result = await http.get(`/api/manual-mine/load/${route.params.id}`);
      project.value = result.data[0];
      scheduler.value = result.data[1];
      period.value = result.data[2];
      cycleNoCompleted.value = result.data[3];

      if (cycleNoCompleted.value) {
        message.warn('Next Collection Cycle is not completed yet', 'Please wait before trying again.');
      }

      loadCollectorStatus(project.value.id, scheduler.value.collectorsId);
    } catch (error) {
      if (error.status === 400) {
        message.info('No schedulers found', error.data?.messageList);
      } else {
        message.error('Error', error.data?.messageList);
      }
    } finally {
      loading.value = false;
    }
  }
};


/**
  * Execute the mine of metrics for the project
  */
  const mine = () => {
    loading.value = true;

    http
      .post('/api/manual-mine/mine', period.value)
      .then((result) => {
        message.success('Success', 'Metrics are being mined for the project');
        router.push("/");
      })
      .catch((error) => {
        message.error('Error', error.data?.messageList);
      })
      .finally(() => {
        loading.value = false;
      });
  };

/** Go to backend to get the status of collector, if it was alredy executed (has a HistoryMetric) */
const loadCollectorStatus = async (projectId, uuids) => {
  loading.value = true
  http
    .get('api/collector/status/' + projectId + '/' + uuids)
    .then((result) => {
      collectorsStatus.value = result.data
    }).catch((error) => {
      message.error('Error', error.data?.messageList)
    }).finally(() => (loading.value = false))
}

/** print the status message of individual collector (if executed or not) */
const printCollectorStatus = (uuid) => {
  const collector = collectorsStatus.value.find((item) => item.uuid === uuid);
  return collector ? `(${collector.message})` : "";
};

/** checks if we have an execution of this collection */
const isCollectorExecuted = (uuid) => {
  const collector = collectorsStatus.value.find((item) => item.uuid === uuid);
  return collector ? collector.executed : false;
};

const cancel = () => {
  router.push('/collect/listprojects')
}

onBeforeMount(() => {
  loadNextMinerPeriod()

})
</script>

<style lang="scss" scoped></style>