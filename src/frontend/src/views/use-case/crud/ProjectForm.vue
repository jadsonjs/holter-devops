<template>
  <div class="card">
    <div class="font-semibold text-xl mb-4">Project</div>

    <!-- ajuda da página -->
    <p class="text-ajuda">
      Create a new Project to monitor its Metrics
    </p>

  </div>

  <div class="card min-h-screen">
    <Fluid>
      <Panel>

        <loading :loading="loading" />

        <VForm class="flex flex-col md:flex-row gap-8" v-slot="{ validate }">

          <div class="md:w-1/2" v-if="!loading">
            <div class="font-semibold text-xl">Project Data</div>
            <div class="card flex flex-col gap-4">

              <div class="flex flex-col gap-2">
                <label for="name1" description="Write a name for the project">Name</label>
                <VField name="name" type="text" v-model="project.name" rules="required|maxLength:256"
                  v-slot="{ field, errors }">
                  <InputText id="name1" type="text" v-bind="field" :invalid="errors.length > 0" />
                  <Message severity="error" icon="fa-solid fa-circle-exclamation" v-if="errors.length > 0">{{ errors[0]
                  }}</Message>
                </VField>
              </div>

              <div class="flex flex-col gap-2">
                <label for="org" description="Write the owner's name">Organization</label>
                <VField name="organization" type="text" v-model="project.organization" rules="required|maxLength:256"
                  v-slot="{ field, errors }">
                  <InputText id="org" type="text" v-bind="field" :invalid="errors.length > 0" />
                  <Message severity="error" icon="fa-solid fa-circle-exclamation" v-if="errors.length > 0"> {{ errors[0]
                  }} </Message>
                </VField>
              </div>


              <div class="flex flex-col gap-2">
                <div class="flex items-center gap-2">

                  <label for="switch1" description="Enable or disable metrics collection for the project">Enable metrics
                    collection?</label>
                  <Ajuda texto="Leave the toggler on if Holter should collect metrics for this project." />

                </div>

                <ToggleSwitch id="switch1" v-model="project.active" />
              </div>
            </div>
            <div class="flex justify-end space-x-4">
              <Button label="Cancel" icon="fa-solid fa-xmark" text @click="cancel" :style="{ width: '10em' }" />
              <Button label="Save" icon="fa-solid fa-floppy-disk" @click="save(validate)" :disabled="loading"
                :style="{ width: '10em' }" />
            </div>
          </div>
        </VForm>
      </Panel>
    </Fluid>


  </div>
</template>


<script setup>
import { ref, onBeforeMount } from 'vue'

import Loading from '@/components/base/Loading.vue';
import Ajuda from '@/components/base/Ajuda.vue';

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

// The project being created/edited
const project = ref({ active: true, securityReportEnabled: true })

// se o processamento esta ocorrendo ao salvar; os dados estão sendo carregados
const loading = ref(false);

/**
 * Call the back-end to save a new project
 * 
 */
const save = async (validate) => {
  const validator = await validate()

  if (validator.valid) {
    loading.value = true

    if (project.lastCollect == null) {
      project.lastCollect = new Date();
      project.lastCollect = null;
    }

    http
      .post('/api/project/save', project.value)
      .then(() => {
        message.success('Saved', 'Project saved successfully!')
        cancel()
      }).catch((error) => {
        message.error('Error', error.data?.messageList)
      }).finally(() => (loading.value = false))
  }
  else {
    message.error('Error', 'Form contains invalid information')

  }
}

const cancel = () => {
  router.push('/project/list')
}

onBeforeMount(() => {
  // if send a "id" in the router is because is editing the project
  if (route.params.id) {
    loading.value = true
    http.get(`/api/project/${route.params.id}`)
      .then((response) => {
        project.value = response.data
      }).catch((error) => {
        message.error('Error', error.data?.messageList)
      }).finally(() => (loading.value = false))
  } else {
    // else it's creating a new one
    project.value = { active: true, securityReportEnabled: true };
  }
})

</script>

<style lang="scss" scoped></style>