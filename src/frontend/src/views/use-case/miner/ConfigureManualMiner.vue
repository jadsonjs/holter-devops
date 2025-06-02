<template>
  <div class="card">
    <div class="font-semibold text-xl mb-4">Collect Metrics Manually</div>

    <!-- ajuda da página -->
    <p class="text-ajuda">
      Select the project to collect metrics <b>manually</b>. <br>
      For performance reasons, metrics are previously calculated and stored in the database before viewing them on the
      dashboard. <br> <br>
      Collection is only actually performed if a new collection cycle is complete.
    </p>

  </div>

  <div class="card min-h-screen">
    <loading :loading="loading" />

    <!-- Projects table -->
    <DataTable ref="dt" :value="projectsList">
      <template #header>
        <div class="flex flex-wrap gap-2 items-center justify-between">
          <h4 class="m-0">Active Projects</h4>
        </div>
      </template>

      <Column field="name" header="Name" sortable />
      <Column field="organization" header="Organization" sortable />

      <!-- Action Buttons for each project -->
      <Column field="actions" header="Actions">
        <template #body="slotProps">
          <Button icon="fa-solid fa-arrow-right" outlined rounded class="mr-2" @click="configure(slotProps.data)"
            title="Executes Manual Mine" />
        </template>
      </Column>
    </DataTable>

    <div class="flex mt-4">
      <Button label="Cancel" icon="fa-solid fa-times" class="p-button-outlined p-button-dark" @click="cancel()" />
    </div>

  </div>

</template>

<script setup>
import { ref, onBeforeMount } from 'vue'

import Loading from '@/components/base/Loading.vue';

// rotas
import { useRouter } from 'vue-router'

// axios
import { useHttp } from '@/utils/axiosUtils';
const { http } = useHttp();

// keep the list of projects returned from back-end
const projectsList = ref([])

const loading = ref(false)
const router = useRouter()


/**
 * Load all projects.
 * This method will call the ProjectController#getAll in the back-end that will return all projects salved in database.
 */
const loadProjects = async () => {
  loading.value = true
  http
    .get('/api/project?active=true')
    .then((result) => {
      projectsList.value = result.data
    }).catch((error) => {
      message.error('Erro', error.data?.messageList)
    }).finally(() => (loading.value = false))
}

const configure = (p) => {

  // if (props.context === 'repository') {
  //   router.push('/repository/collect/mine/' + p.id)  
  // } 
  // else {
    router.push('/collect/mine/' + p.id)
  //}

}

const cancel = () => {
  router.push('/')
}

onBeforeMount(() => {
  loadProjects()
})
</script>

<style lang="scss" scoped></style>