<template>
  <div class="card">
    <div class="font-semibold text-xl mb-4">Scheduler</div>

    <!-- ajuda da página -->
    <p class="text-ajuda">
      Scheduler are the entities that run the miner of specific metric for the project<br>
    </p>

  </div>


  <div class="card min-h-screen">
    <loading :loading="loading" />

    <!-- New Scheduler button -->
    <Toolbar class="mb-4" v-if="!scheduler?.id">
      <template #start>
        <Button label="New Scheduler" icon="fa-solid fa-plus" severity="primary" @click="create" />
      </template>
    </Toolbar>


    <div v-if="!loading && scheduler.id">

      <div class="font-semibold text-xl">Scheduler of the Project: {{ scheduler && scheduler.project ?
        scheduler.project.organization + " / " + scheduler.project.name : "" }}
      </div>

      <div class="card flex flex-col gap-4">

        <SchedulerDetails :scheduler="scheduler" />
        <!-- COLLECTORS -->
        <DataTable ref="dt" :value="schedulerCollectorsList">
          <template #header>
            <div class="flex flex-wrap gap-2 items-center justify-between">
              <h4 class="m-0">Collectors:</h4>
            </div>
          </template>

          <Column header="#" style="min-width: 12rem">
            <template #body="slotProps">
              {{ slotProps.index + 1 }}
            </template>
          </Column>

          <Column field="repository.name" header="Repository" sortable style="min-width: 12rem">
          </Column>

          <Column field="metric.stage" header="Stage" sortable style="min-width: 12rem">
          </Column>

          <Column field="description" header="Name" sortable style="min-width: 12rem">
            <template #body="slotProps">
              {{ slotProps.data.metric.denomination }} ({{ slotProps.data.description }})
            </template>
          </Column>
        </DataTable>
      </div>

      <div class="flex justify-end space-x-4">
        <Button label="Edit" icon="fa-solid fa-edit" text @click="edit(scheduler)" :style="{ width: '10em' }"
          v-if="scheduler" />
        <Button label="Clear Collected Data" icon="fa-solid fa-broom" severity="danger"
          @click="confirmDeleteData(scheduler, $event)" :disabled="loading" :style="{ width: '10em' }"
          v-if="scheduler" />
        <Button label="Delete Scheduler" icon="fa-solid fa-trash" severity="danger"
          @click="confirmDelete(scheduler, $event)" :disabled="loading" :style="{ width: '10em' }" v-if="scheduler" />
      </div>
    </div>

    <!-- Delete confirmation dialog -->
    <delete-dialog ref="delDialogRef" @confirm-delete="handleConfirmDelete" />

    <!-- Delete confirmation dialog -->
    <delete-dialog ref="delDialogDataRef" @confirm-delete="handleConfirmDeleteData" />

    <!-- Cancel button -->
    <div class="flex mt-4">
      <Button label="Cancel" icon="fa-solid fa-times" class="p-button-outlined p-button-dark" @click="cancel()" />
    </div>

  </div>
</template>

<script setup>
import { ref, onBeforeMount, computed } from 'vue'

import Loading from '@/components/base/Loading.vue';
import { hasRoles } from '@/utils/permissoes'

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

// se o processamento esta ocorrendo ao salvar; os dados estão sendo carregados
const loading = ref(false);

// The scheduler being viewed
import SchedulerDetails from '@/components/use-case/SchedulerDetails.vue';
const scheduler = ref(null)

// Collectors
import { storeToRefs } from 'pinia'
import { useCollectorsStore } from '@/stores/collectors'
const collectorsStore = useCollectorsStore()
const { getCollectorsList } = storeToRefs(collectorsStore)

// all collectors belonging to this scheduler
const schedulerCollectorsList = computed(() =>
  getCollectorsList.value.filter(collector => scheduler.value.collectorsId.includes(collector.id))
);

/**
* Load all schedulers of the selected project.
*/
const loadScheduler = async () => {
  if (route.params.idProjeto) {
    loading.value = true
    http
      .get(`/api/scheduler/project/${route.params.idProjeto}`)
      .then((result) => {
        scheduler.value = result.data
      }).catch((error) => {
        if (error.status === 400) {
          message.info('No schedulers found', error.data?.messageList)
        }
        else {
          message.error('Error', error.data?.messageList)
        }
        scheduler.value = []
      }).finally(() => (loading.value = false))
  }
}

const create = () => {
  router.push("/scheduler/form/" + route.params.idProjeto)
}

const edit = (scheduler) => {
  router.push("/scheduler/edit/" + scheduler.id)
}

/**************************************************
 *   DELETE SCHEDULER
 **************************************************/

const delDialogRef = ref(null);

/**
 * Abre o menu para o usuário confirmar se o scheduler deve ser removido
 * @param reference 
 */
const confirmDelete = (data) => {
  if(!hasRoles(['ADMIN'])) {
    message.error('Error', 'User has no permission: ADMIN')
    return
  }
  if (delDialogRef.value) {
    delDialogRef.value.showDialog(data, `Confirm remove scheduler?`);
  }
};

/**
 * O usuario confirmou a remoção do scheduler, chama o metodo para remover
 * @param payload o scheduler selecionado
 */
const handleConfirmDelete = (payload) => {
  remove(payload.data)
};

const remove = (scheduler) => {
  loading.value = true
  http
    .delete(`/api/scheduler/${scheduler.id}`)
    .then(() => {
      message.success('Scheduler removed', `Scheduler removed successfully`)
      loadScheduler()
    }).catch((error) => {
      message.error('Error', error.data?.messageList)
    }).finally(() => (loading.value = false))
};

/**************************************************
 *   DELETE COLLECTED DATA
 **************************************************/

const delDialogDataRef = ref(null);

/**
 * Abre o menu para o usuário confirmar se os dados coletados devem ser removidos
 * @param reference 
 */
const confirmDeleteData = (data) => {
  if(!hasRoles(['ADMIN', 'MANAGER'])) {
    message.error('Error', 'User has no permission: ADMIN')
    return
  }
  if (delDialogDataRef.value) {
    delDialogDataRef.value.showDialog(data, `Confirm remove scheduler collected data?`);
  }
};

/**
 * O usuario confirmou a remoção dos dados coletados, chama o metodo para remover
 * @param payload o scheduler selecionado
 */
const handleConfirmDeleteData = (payload) => {
  clearData(payload.data)
};

const clearData = (scheduler) => {
  loading.value = true
  http
    .delete(`/api/scheduler/clear/${scheduler.id}`)
    .then(() => {
      message.success('Data removed', `Data collected by Scheduler cleared sucessfully!`)
      loadScheduler()
    }).catch((error) => {
      message.error('Error', error.data?.messageList)
    }).finally(() => (loading.value = false))
};

const cancel = () => {
  router.push('/project/list')
}




onBeforeMount(() => {
  loadScheduler()
})
</script>

<style lang="scss" scoped></style>