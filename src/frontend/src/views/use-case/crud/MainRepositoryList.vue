<template>
  <div class="card">
    <div class="font-semibold text-xl mb-4">Main Repositories</div>

    <p class="text-ajuda">
      List of main repositories.
    </p>
  </div>
  <div class="card min-h-screen">

    <!-- New Repository Config button -->
    <Toolbar v-if="!isSelecting" class="mb-4">
      <template #start>
        <Button label="New Repository" icon="fa-solid fa-plus" severity="primary"
          @click="createRepositoryConfig" />
      </template>
    </Toolbar>

    <loading :loading="loading" />

    <!-- Repository table -->
    <DataTable ref="dt" :value="repositoryConfigsList">
      <template #header>
        <div class="flex flex-wrap gap-2 items-center justify-between">
          <h4 class="m-0">Main Repository</h4>
        </div>
      </template>

      <Column field="id" header="#" sortable :pt="{ columnHeaderContent: 'justify-center', }">
        <template #body="slotProps">
          <div class="flex justify-center items-center">
            {{ slotProps.data.id }}
          </div>
        </template>
      </Column>
      <Column field="name" header="Name" />
			<Column field="url" header="URL" />


      <!-- Action Buttons for each repository configuration -->
      <Column field="actions" header="Actions">
        <template #body="slotProps">
          <Button v-if="!isSelecting" icon="fa-solid fa-edit" outlined rounded class="mr-2"
            @click="edit(slotProps.data)" title="Edit" />
          <Button v-if="!isSelecting" icon="fa-solid fa-trash" outlined rounded severity="danger"
            @click="confirmDelete(slotProps.data, $event)" title="Delete Repository Config" />
					<!-- Save in store to recovery in other component -->
					<Button v-if="isSelecting" icon="fa-solid fa-arrow-right" label="Select" outlined rounded
            @click="selectRepository(slotProps.data)" />
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
import { ref, onBeforeMount, computed } from 'vue'
import Loading from '@/components/base/Loading.vue';
import DeleteDialog from '@/components/base/DeleteDialog.vue';
import { hasRoles } from '@/utils/permissoes'
import { useRouter, useRoute } from 'vue-router'
import { useHttp } from '@/utils/axiosUtils';
import { useMessage } from '@/utils/message';

const { http } = useHttp();
const { message } = useMessage();
const repositoryConfigsList = ref()
const loading = ref(false)
const router = useRouter()
const route = useRoute()

import { useMainRepositoryStore } from '@/stores/mainRepository'

const mainRepositoryStore = useMainRepositoryStore()


// Check if we're selecting a config from the project configuration form
const isSelecting = computed(() => !!route.query.selecting);

const selectRepository = async (config) => {
  mainRepositoryStore.setRepository(config)
  message.success('Success!', 'Main Repository selected with Success')
  cancel() // Navigate back to project config
}


const loadRepositoryConfigs = async () => {
  loading.value = true
  http.get('/api/main-repository')
    .then((result) => {
      repositoryConfigsList.value = result.data
    })
    .catch((error) => {
      message.error('Error', error.data?.messageList)
    })
    .finally(() => (loading.value = false))
}

const createRepositoryConfig = () => {
  router.push('/main-repository/form')
}

const delDialogRef = ref(null);
const confirmDelete = (data) => {
  if (!hasRoles(['ADMIN'])) {
    message.error('Error', 'User has no permission: ADMIN')
    return
  }
  if (delDialogRef.value) {
    delDialogRef.value.showDialog(data, `Confirm remove repository configuration "${data.id}"?`);
  }
};

const handleConfirmDelete = (payload) => {
  remove(payload.data)
};

const remove = (config) => {
  loading.value = true
  http.delete(`/api/main-repository/${config.id}`)
    .then(() => {
      message.success('Configuration removed', `Configuration removed successfully`)
      loadRepositoryConfigs()
    })
    .catch((error) => {
      message.error('Error', error.data?.messageList)
    })
    .finally(() => (loading.value = false))
};

const edit = (config) => {
  router.push(`/main-repository/form/${config.id}`)
}

const cancel = () => {
  router.back()
}

onBeforeMount(() => {
  loadRepositoryConfigs()
})
</script>

<style scoped></style>