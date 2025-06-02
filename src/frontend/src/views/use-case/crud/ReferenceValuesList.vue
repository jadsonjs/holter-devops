<template>
  <div class="card">
    <div class="font-semibold text-xl mb-4">Reference Values</div>
    <article class="text-ajuda">
      <p>Metric Reference Values are values used to verify DevOps maturity in your team.</p>
      <p class="mt-2">The Reference Values can change according to each team's characteristics. Each team is allowed to
        define its most suitable values.</p>
      <p class="mt-4">
        For Continuous Integration metrics, you can base Reference Values on the
        <a class="text-primary" href="https://arxiv.org/abs/1907.01602" target="_blank">Continuous Integration
          Theater</a> paper.
      </p>
      <ol class="ml-10 mt-2" style="list-style: decimal;">
        <li>With infrequent commits, <b>2.36</b> commits per weekday</li>
        <li>In a software project with poor test coverage, Java projects have <b>63%</b> of test coverage on average
        </li>
        <li>Builds that stay broken for long periods: 85% of the analyzed projects have at least one build that took
          more than <b>4 days</b> to be fixed</li>
        <li>Builds that take too long to run should ideally complete in under <b>10 minutes</b></li>
      </ol>
      <br>
      <p>For DORA metrics, you can use Google's classifications in the
        <a class="text-primary"
          href="https://cloud.google.com/blog/products/devops-sre/dora-2022-accelerate-state-of-devops-report-now-out"
          target="_blank">DORA 2022 Accelerate State of DevOps Report</a>.
      </p>
      <table class="mt-4">
        <thead>
          <tr>
            <th style="padding: 0.4rem;">Metric</th>
            <th style="padding: 0.4rem;">Description</th>
            <th style="padding: 0.4rem;">High</th>
            <th style="padding: 0.4rem;">Medium</th>
            <th style="padding: 0.4rem;">Low</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td style="padding: 0.4rem;">Deployment frequency</td>
            <td style="padding: 0.4rem;">The number of deploys to production per day</td>
            <td style="padding: 0.4rem;"><b>≥ 1</b><br><span style="font-size: small;">(multiple deploys a day)</span>
            </td>
            <td style="padding: 0.4rem;"><b>0.033 - 0.13</b><br><span style="font-size: small;">(between one per month
                and one per week)</span></td>
            <td style="padding: 0.4rem;"><b>&lt; 0.033</b><br><span style="font-size: small;">(less than one per
                month)</span></td>
          </tr>
          <tr>
            <td style="padding: 0.4rem;">Lead time for changes</td>
            <td style="padding: 0.4rem;">Days from code committed to code running in production</td>
            <td style="padding: 0.4rem;"><b>≤ 7</b><br><span style="font-size: small;">(less than one week)</span></td>
            <td style="padding: 0.4rem;"><b>8 - 29</b><br><span style="font-size: small;">(between one week and one
                month)</span></td>
            <td style="padding: 0.4rem;"><b>≥ 30</b><br><span style="font-size: small;">(more than one month)</span>
            </td>
          </tr>
          <tr>
            <td style="padding: 0.4rem;">Mean time to Recovery</td>
            <td style="padding: 0.4rem;">Days to restore service after an incident</td>
            <td style="padding: 0.4rem;"><b>≤ 1</b><br><span style="font-size: small;">(less than one day)</span></td>
            <td style="padding: 0.4rem;"><b>2 - 6</b><br><span style="font-size: small;">(between one day and one
                week)</span></td>
            <td style="padding: 0.4rem;"><b>≥ 7</b><br><span style="font-size: small;">(more than one week)</span></td>
          </tr>
          <tr>
            <td style="padding: 0.4rem;">Change failure rate</td>
            <td style="padding: 0.4rem;">The percentage of changes to production that result in degraded service</td>
            <td style="padding: 0.4rem;"><b>≤ 15%</b></td>
            <td style="padding: 0.4rem;"><b>16% - 30%</b></td>
            <td style="padding: 0.4rem;"><b>46% - 60%</b></td>
          </tr>
        </tbody>
      </table>

    </article>
  </div>

  <div class="card min-h-screen">

		<p>
		    Reference Values for <b v-if="selectedProject"> {{ selectedProject.organization }} / {{selectedProject.name }} </b>
		</p>

    <!-- New Reference Value button -->
    <Toolbar class="mb-4">
      <template #start>
        <Button label="New Reference Value" icon="fa-solid fa-plus-circle" severity="primary"
          @click="newReferenceValue" />
        <Button label="Copy Values from Another Project" class="ml-5" icon="fa-solid fa-copy" severity="secondary"
          @click="showCopyDialog" />
      </template>
    </Toolbar>

    <loading :loading="loading" />

    <!-- Reference Values table -->
    <DataTable stripedRows ref="dt" :value="measureReferenceList">
      <template #header>
        <div class="flex flex-wrap gap-2 items-center justify-between">
          <h4 class="m-0">Reference Values</h4>
        </div>
      </template>

      <Column field="metric.denomination" header="Metric" sortable />
      <Column field="value" header="Reference Value (Unit)" sortable>
        <template #body="slotProps">
          {{ slotProps.data.value }} {{ slotProps.data.metric.unit.trim() != '' ? '(' + slotProps.data.metric.unit + ')' : '' }}
        </template>
      </Column>

      <!-- Action Buttons for each reference value -->
      <Column field="actions" header="Actions">
        <template #body="slotProps">
          <Button icon="fa-solid fa-edit" outlined rounded class="mr-2" @click="edit(slotProps.data)" title="Edit" />
          <Button icon="fa-solid fa-trash" outlined rounded severity="danger"
            @click="confirmDelete(slotProps.data, $event)" title="Delete" />
        </template>
      </Column>
    </DataTable>

    <!-- Delete confirmation dialog -->
    <delete-dialog ref="delDialogRef" @confirm-delete="handleConfirmDelete" />

    <div>
      <Dialog header="Copy Reference Values" v-model:visible="copyDialogVisible" :style="{ width: '450px' }"
        :modal="true">

        <div class="flex flex-col gap-2 ">
          <label for="project" class="font-semibold text-sm">From source project:</label>
          <Select name="project" v-model="selectedProject" :options="projects" optionLabel="name" optionValue="id"
            placeholder="Select Project" />
        </div>

        <template #footer>
          <Button label="Cancel" icon="fa-solid fa-xmark" @click="copyDialogVisible = false" text
            severity="secondary" />
          <Button label="Copy" icon="fa-solid fa-copy" @click="copyReferenceValues" :disabled="!selectedProject"
            severity="primary" outlined autofocus />
        </template>
      </Dialog>
    </div>

    <!-- Cancel button -->
    <div class="flex mt-4">
      <Button label="Cancel" icon="fa-solid fa-times" class="p-button-outlined p-button-dark" @click="cancel()" />
    </div>
  </div>
</template>


<script setup>
import { ref, onBeforeMount } from 'vue'

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

// os dados estão sendo carregados
const loading = ref(false);

// keep the list of reference values returned from back-end
const measureReferenceList = ref([])

/**************************************************
 *   COPY REFERENCE VALUES
 **************************************************/

const copyDialogVisible = ref(false)
const selectedProject = ref() // The selected project object
const projects = ref([]) // List of projects

/**
 * Load projects from backend
 */
const loadProjects = async () => {
  loading.value = true
  try {
    const response = await http.get('/api/project')
    // Save list of projects and exclude current project
    projects.value = response.data.filter(project => project.id !== Number(route.params.projectId));

    // Select first project for faster copying
    if (projects.value.length > 0) {
      selectedProject.value = projects.value[0].id;
    }

  } catch (error) {
    message.error('Error', error?.data?.messageList)
  } finally {
    loading.value = false
  }
}

/**
 * Show the dialog to copy metric reference values from another project.
 */
const showCopyDialog = () => {
  if(!hasRoles(['ADMIN'])) {
    message.error('Error', 'User has no permission: ADMIN')
    return
  }
  loadProjects()
  copyDialogVisible.value = true
}

/**
 * Copy reference values from the selected project
 */
const copyReferenceValues = async () => {

  if (!selectedProject.value) {
    message.error('Error', 'Please select a project')
    return
  }

  loading.value = true
  try {
    // Get metric reference values from the selected project
    await http.post(`/api/ci-metric-reference/copy/${selectedProject.value}/${route.params.projectId}`);
    message.success('Success', 'Reference values copied successfully!')
  } catch (error) {
    if (error?.status === 400) {
      message.info('No changes', error?.data?.messageList)
    }
    else {
      message.error('Error', error?.data?.messageList)
    }
  } finally {
    loading.value = false
    copyDialogVisible.value = false
    loadMetricReferencesValues()
  }
}


/**
 * Load all metric references values saved in the tool.
 */
const loadMetricReferencesValues = async () => {
  loading.value = true
  http
    .get(`/api/ci-metric-reference/project/${route.params.projectId}`)
    .then((result) => {
      measureReferenceList.value = result.data
			//selectedProject = 
    }).catch((error) => {
      message.error('Erro', error.data?.messageList)
    }).finally(() => (loading.value = false))

		http
      .get(`/api/project/${route.params.projectId}`)
      .then((result) => {
        selectedProject.value = result.data
      }).catch((error) => {
        message.error('Erro', error.data?.messageList)
        scheduler.value = []
      }).finally(() => (loading.value = false))
}

/**
 * Redirects to the form view of 'new reference value'.
 */
const newReferenceValue = () => {
  router.push(`/ci/references/form/project/${route.params.projectId}`)
}

/**
 * Redirects to the form view of 'edit reference value'.
 * @param m The metric that will have its reference value edited
 */
const edit = (m) => {
  router.push(`/ci/references/form/${m.id}`)
}

/**************************************************
 *   DELETE REFERENCE VALUE
 **************************************************/

const delDialogRef = ref(null);

/**
 * Abre o menu para o usuário confirmar se o valor de referência deve ser removido
 * @param reference 
 */
const confirmDelete = (data) => {
  if (!hasRoles(['ADMIN', 'MANAGER'])) {
    message.error('Error', 'User has no permission: ADMIN, MANAGER')
    return
  }
  if (delDialogRef.value) {
    delDialogRef.value.showDialog(data, `Confirm remove reference value of "${data.metric.denomination}" CI metric?`);
  }
};

/**
 * O usuario confirmou a remoção do projeto, chama o metodo para remover
 * @param payload o projeto selecionado
 */
const handleConfirmDelete = (payload) => {
  remove(payload.data)
};

const remove = (m) => {
  loading.value = true
  http
    .delete(`/api/ci-metric-reference/${m.id}`)
    .then(() => {
      message.success('Reference value removed', `Reference value of ${m.metric.denomination} removed successfully`)
      loadMetricReferencesValues()
    }).catch((error) => {
      message.error('Error', error.data?.messageList)
    }).finally(() => (loading.value = false))
};


const cancel = () => {
  router.back()
}


onBeforeMount(() => {
  loadMetricReferencesValues()
})
</script>

<style lang="scss" scoped></style>