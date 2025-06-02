<template>
  <div class="card">
    <div class="font-semibold text-xl mb-4">Projects</div>

    <p class="text-ajuda">
      List of projects saved in the tool to collect DevOps metric and security information
    </p>
  </div>
  <div class="card min-h-screen">

    <!-- New Project button -->
    <Toolbar class="mb-4">
      <template #start>
        <Button label="New Project" icon="fa-solid fa-plus" severity="primary" @click="createProject" />
      </template>
    </Toolbar>

    <loading :loading="loading" />

    <!-- Projects table -->
    <DataTable ref="dt" :value="projectsList">
      <template #header>
        <div class="flex flex-wrap gap-2 items-center justify-between">
          <h4 class="m-0">Projects</h4>
        </div>
      </template>

      <Column field="name" header="Name" sortable />
      <Column field="organization" header="Organization" />
      <Column field="active" header="Enable Metrics Collection" sortable
        :pt="{ columnHeaderContent: 'justify-center', }">

        <template #body="slotProps">
          <div class="flex justify-center items-center">
            <i v-if="slotProps.data.active" class="fa-solid fa-check"></i>
          </div>
        </template>

      </Column>
      
      <!-- Action Buttons for each project -->
      <Column field="actions" header="Actions">
        <template #body="slotProps">
          <Button icon="fa-solid fa-edit" outlined rounded class="mr-2" @click="edit(slotProps.data)" title="Edit" />
          <Button icon="fa-solid fa-cogs" outlined rounded class="mr-2" @click="viewConfigurations(slotProps.data)"
            title="Project Configurations" />
          <Button icon="fa-solid fa-thermometer-half" outlined rounded class="mr-2" @click="viewReferenceMetrics(slotProps.data)"
            title="View the Reference Metrics" />
          <Button icon="fa-solid fa-stopwatch" outlined rounded class="mr-2" @click="viewScheduler(slotProps.data)"
            title="View the Scheduler" />
          <Button icon="fa-solid fa-trash" outlined rounded severity="danger"
            @click="confirmDelete(slotProps.data, $event)" title="Delete the Project and All Collected Data" />
        </template>
      </Column>
    </DataTable>

    <!-- Cancel button -->
    <div class="flex mt-4">
      <Button label="Cancel" icon="fa-solid fa-times" class="p-button-outlined p-button-dark" @click="cancel()" />
    </div>

    <!-- Delete confirmation dialog -->
    <delete-dialog ref="delDialogRef" @confirm-delete="handleConfirmDelete" />
  </div>
</template>

<script setup>
import { ref, onBeforeMount } from 'vue'

import Loading from '@/components/base/Loading.vue';
import DeleteDialog from '@/components/base/DeleteDialog.vue';
import { hasRoles } from '@/utils/permissoes'

// rotas
import { useRouter } from 'vue-router'

// axios
import { useHttp } from '@/utils/axiosUtils';
const { http } = useHttp();

// mensagens
import { useMessage } from '@/utils/message';
const { message } = useMessage();

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
    .get('/api/project')
    .then((result) => {
      projectsList.value = result.data
    }).catch((error) => {
      message.error('Erro', error.data?.messageList)
    }).finally(() => (loading.value = false))
}


const createProject = () => {
  router.push('/project/form')
}

/**************************************************
 *   DELETE PROJECT
 **************************************************/

const delDialogRef = ref(null);

/**
 * Abre o menu para o usuário confirmar se o projeto deve ser removido
 * @param project 
 */
const confirmDelete = (data) => {
  if (!hasRoles(['ADMIN'])) {
    message.error('Error', 'User has no permission: ADMIN')
    return
  }
  if (delDialogRef.value) {
    delDialogRef.value.showDialog(data, `Confirm remove project "${data.name}?"`);
  }
};

/**
 * O usuario confirmou a remoção do projeto, chama o metodo para remover
 * @param payload o projeto selecionado
 */
const handleConfirmDelete = (payload) => {
  remove(payload.data)
};


const remove = (p) => {
  loading.value = true
  http
    .delete(`/api/project/${p.id}`)
    .then(() => {
      message.success('Project removed', `Project ${p.name} removed successfully`)
      loadProjects() // atualiza do backend a listagem
    }).catch((error) => {
      message.error('Error', error.data?.messageList)
    }).finally(() => (loading.value = false))
};


const viewConfigurations = (p) => {
  router.push(`/configuration/form/${p.id}`)
}

const viewScheduler = (p) => {
  router.push(`/scheduler/view/${p.id}`)
}

const viewReferenceMetrics = (p) => {
  router.push(`/ci/references/list/${p.id}`)
}

const edit = (p) => {
  router.push(`/project/form/${p.id}`)
}

const cancel = () => {
  router.push('/')
}

onBeforeMount(() => {
  loadProjects()
})
</script>

<style scoped></style>