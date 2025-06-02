<template>
  <div class="card">
    <div class="font-semibold text-xl mb-4">Generate Report</div>

    <!-- ajuda da página -->
    <p class="text-ajuda">
      Select up to {{ SELECTION_LIMIT }} projects to generate a security report manually. <br>
    </p>
  </div>

  <div class="card min-h-screen">
    <loading :loading="loading" />

    <div v-if="!loading">
      
      <Toolbar class="mb-4">
        <template #start>
          <div class="flex gap-2 items-center">
            <Button :label="`Select ${SELECTION_LIMIT} Projects`" icon="fa fa-forward"
              @click="selectNextProjects" />
            <Button label="Clear Selection" icon="fa fa-times" @click="clearSelection" />
          </div>
        </template>
      </Toolbar>

      <!-- Projects table -->
      <DataTable ref="dt" :value="projectsList" dataKey="name">
        <template #header>
          <div class="flex flex-wrap gap-2 items-center justify-between">
            <h4 class="m-0">Active Projects</h4>
          </div>
        </template>

        <!-- Customized checkbox for project selection -->
        <Column style="width: 3em">
          <template #body="slotProps">
            <Checkbox :value="slotProps.data" v-model="selectedProjects" :disabled="disableCheckbox(slotProps.data)" />
          </template>
        </Column>

        <Column field="name" header="Name" sortable />
        <Column field="organization" header="Organization" sortable />
      </DataTable>
    </div>


    <!-- Button to download the report -->
    <div class="flex justify-end mr-12 mt-6">
      <Button label="Generate Vulnerability Report" icon="fas fa-download" @click="generateReport" :disabled="loading"
        class="md:w-50" />
    </div>

    <Button label="Cancel" icon="fa-solid fa-times" class="p-button-outlined p-button-dark" @click="cancel" />
  </div>
</template>


<script setup>
import { ref, onBeforeMount } from 'vue'
import { useMessage } from '@/utils/message';
const { message } = useMessage();

import Loading from '@/components/base/Loading.vue';

// rotas
import { useRouter } from 'vue-router'

// axios
import { useHttp } from '@/utils/axiosUtils';
const { http } = useHttp();

// Keep the list of projects returned from back-end
const projectsList = ref([])
const selectedProjects = ref([])  // to hold selected projects

const loading = ref(false)
const router = useRouter()

// Limit of projects that can be selected to generate a report
const SELECTION_LIMIT = 5

// Verifica se determinado projeto está selecionado
const isSelected = (project) => {
  return selectedProjects.value.find((p) => p.name === project.name)
}


/**
 * Identifies if the checkbox for a project should be disabled.
 * Returns true if the project hasn't been selected and if the selection limit has been reached.
 * @param project the project of the checkbox
 */
const disableCheckbox = (project) => {
  return !isSelected(project) && selectedProjects.value.length >= SELECTION_LIMIT
}

// Select next group of projects
let currentStartIndex = ref(0)
const selectNextProjects = () => {
  const start = currentStartIndex.value
  const end = start + SELECTION_LIMIT
  const nextSet = projectsList.value.slice(start, end)
  selectedProjects.value = nextSet
  currentStartIndex.value = end >= projectsList.value.length ? 0 : end
}

// Clear selected projects
const clearSelection = () => {
  selectedProjects.value = []
  currentStartIndex.value = 0
}

/**
 * Load all projects.
 */
const loadProjects = async () => {
  loading.value = true
  http
    .get('/api/project?active=true')
    .then((result) => {
      projectsList.value = result.data
    }).catch((error) => {
      message.error('Error', error.data?.messageList)
    }).finally(() => (loading.value = false))
}

/**
 * Generate a report for the selected projects.
 */
const generateReport = async () => {
  if (selectedProjects.value.length === 0) {
    message.info('No project selected', 'Please select at least one project before generating the report.')
    return
  }

  loading.value = true
  try {
    const result = await http.post('/api/report/download', selectedProjects.value, { responseType: 'arraybuffer' })

    // Convert the byte array to a Blob and create a download link
    const blob = new Blob([result.data], { type: 'application/pdf' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = 'Vulnerability_Report.pdf'
    link.click()

  } catch (error) {
    //Gateway timed out
    if (error.status === 504) {
      message.error('Error', 'Timed out. Please try again later.')
      return;
    }

    //Convert arraybuffer back to JSON to get the error messages
    try {
      const decoder = new TextDecoder('utf-8');
      const errorText = decoder.decode(new Uint8Array(error.data));
      const parsedError = JSON.parse(errorText);

      message.error('Error', parsedError?.messageList)
    }
    catch (e) {
      message.error('Error', 'Something went wrong while processing the error response. Please try again later.');
    }

  } finally {
    loading.value = false
  }
}

const cancel = () => {
  router.push('/')
}

onBeforeMount(() => {
  loadProjects()
})
</script>

<style lang="scss" scoped></style>
