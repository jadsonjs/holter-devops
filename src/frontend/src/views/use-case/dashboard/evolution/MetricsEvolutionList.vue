<template>
  <div class="card">
    <div class="font-semibold text-xl mb-4">Projects</div>

    <!-- ajuda da página -->
    <p class="text-ajuda">
      Select a Project to show its Metric Evolution
    </p>

  </div>
  <div class="card min-h-screen">
    <loading :loading="loading" />

    <!-- Projects table -->
    <DataTable ref="dt" :value="projectsList">
      <template #header>
        <div class="flex flex-wrap gap-2 items-center justify-between">
          <h4 class="m-0">Projects</h4>
        </div>
      </template>

      <Column field="name" header="Name" sortable />
      <Column field="organization" header="Organization" sortable />
      <Column field="active" header="Active" sortable>
        <template #body="slotProps">
            <i v-if="slotProps.data.active" class="fa-solid fa-check"></i>
        </template>
      </Column>
      
      <!-- Action Buttons for each project -->
      <Column field="actions" header="Actions">
        <template #body="slotProps">
          <Button icon="fa-solid fa-arrow-right" outlined rounded class="mr-2" @click="select(slotProps.data)"
            title="View the Scheduler" />
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

const props = defineProps({
  context: {
    type: String,
    default: 'default',
  },
})

/**
 * Load all projects.
 * This method will call the ProjectController#getAll in the back-end that will return all projects salved in database.
 */
const loadProjects = async () => {
  loading.value = true
  http
    .get('/api/project')
    .then((result) => {
      projectsList.value = result.data
    }).catch((error) => {
      message.error('Error', error.data?.messageList)
    }).finally(() => (loading.value = false))
}

// Função de navegação baseada na prop 'mode'
const select = (p) => {
  const basePath =
    props.context === 'repository'
      ? '/repository/evolution/dashboard/'
      : '/metrics/evolution/dashboard/'

  const organizationEncoded = p.organization.replace(/\//g, '___')
  router.push(`${basePath}${organizationEncoded}/${p.name}`)
}

const cancel = () => {
  router.push('/')
}

onBeforeMount(() => {
  loadProjects()
})

</script>

<style lang="scss" scoped></style>