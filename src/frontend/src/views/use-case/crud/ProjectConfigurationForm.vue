<template>
  <div class="card">
    <div class="font-semibold text-xl mb-4">Project Configuration</div>

    <!-- ajuda da página -->
    <p class="text-ajuda" v-if="configuration.project">
      Enter with the project configuration values for the project: <b>{{ configuration.project.organization + '/' + configuration.project.name }} </b>
    </p>

  </div>

  <div class="card min-h-screen">
    <Fluid>
      <Panel>

        <loading :loading="loading" />

        <VForm class="flex flex-col md:flex-row gap-8" v-slot="{ validate }">

          <div class="md:w-1/2" v-if="!loading">
            
            <div class="card flex flex-col gap-4">

							<div class="font-semibold text-xl" >Main Repository:</div>

              <!-- Main Repository Section -->
              <div class="flex flex-col gap-2">
                <p class="descricao-ajuda">
                  Main Repository is usually the repository where the project's source code is located. The place where
                  the pipeline execution occurs and where we extract the build information.
                </p>
              </div>

						
							<!-- Combo to select the main repository -->
							<div class="flex items-center gap-2">
								<Dropdown v-if="repositoryCount > 0" v-model="configuration.mainRepository" :options="mainRepositories" optionLabel="name"
									placeholder="Select a repository" class="w-full md:w-20rem"/>

								<router-link to="/main-repository/list">
									<Button icon="fa-solid fa-plus" label="Create"
									class="mt-2 bg-primary text-primary-foreground font-bold shadow-md" />
								</router-link>
							</div>
							

							
              <div v-if="configuration.mainRepository"
									class="bg-background p-3 rounded-lg shadow flex flex-col gap-2 border-l-4 border-primary">

									<div class="p-2 rounded-md ">
										<span class="text-sm">Name:</span>
										<span class="text-sm opacity-90 block truncate">
											{{ configuration.mainRepository.name }}
										</span>
									</div>

									<div class="p-2 rounded-md ">
										<span class="text-sm ">Repository URL:</span>
										<span class="text-sm opacity-90 block truncate">
											{{ configuration.mainRepository.url }}
										</span>
									</div>
               
              </div>  <!-- Main repository div -->

							

							<div class="font-semibold text-xl">Secondary Repository:</div>

              <!-- Secondary Repository Section -->
              <div class="mt-4 flex flex-col gap-2">
                <p class="descricao-ajuda">
                  Secondary Repository is a secondary tool used to monitor project metrics, like <a
                    href="https://www.sonarsource.com/products/sonarqube/" target="_blank">Sonarqube</a>, used to
                  monitor test coverage, Code Smells, etc.
                </p>
              </div>

              <!-- Secondary Repository URL -->
              <div class="flex flex-col gap-2">
                <div class="flex items-center gap-2 justify-between">
                  <label for="secRepoUrl">Secondary Repository URL:</label>
                  <Ajuda texto="API URL of secondary repository (often used to collect coverage data)." />
                </div>
                <VField name="secondaryRepositoryURL" type="text" v-model="configuration.secondaryRepositoryURL"
                  v-slot="{ field, errors }">
                  <InputText id="secRepoUrl" type="text" v-bind="field" placeholder="https://codecov.io/api/v2/"
                    :invalid="errors.length > 0" />
                  <Message severity="error" icon="fa-solid fa-circle-exclamation" v-if="errors.length > 0">{{ errors[0]
                  }}</Message>
                </VField>
              </div>

              <!-- Secondary Repository Project Organization -->
              <div class="flex flex-col gap-2">
                <div class="flex items-center gap-alert2 justify-between">
                  <label for="secRepoOrg">Secondary Repository Project Organization:</label>
                  <Ajuda texto="Organization for the secondary repository." />
                </div>
                <VField name="secondaryRepositoryOrganization" type="text"
                  v-model="configuration.secondaryRepositoryOrganization" v-slot="{ field, errors }">
                  <InputText id="secRepoOrg" type="text" v-bind="field" placeholder="br.com"
                    :invalid="errors.length > 0" />
                  <Message severity="error" icon="fa-solid fa-circle-exclamation" v-if="errors.length > 0">{{ errors[0]
                  }}</Message>
                </VField>
              </div>

              <!-- Secondary Repository Project Name -->
              <div class="flex flex-col gap-2">
                <div class="flex items-center gap-2 justify-between">
                  <label for="secRepoName">Secondary Repository Project Name:</label>
                  <Ajuda texto="Name of the project in the secondary repository." />
                </div>
                <VField name="secondaryRepositoryName" type="text" v-model="configuration.secondaryRepositoryName"
                  v-slot="{ field, errors }">
                  <InputText id="secRepoName" type="text" v-bind="field" placeholder="my-project"
                    :invalid="errors.length > 0" />
                  <Message severity="error" icon="fa-solid fa-circle-exclamation" v-if="errors.length > 0">{{ errors[0]
                  }}</Message>
                </VField>
              </div>

              <!-- Secondary Repository Token -->
              <div class="flex flex-col gap-2">
                <div class="flex items-center gap-2 justify-between">
                  <label for="secRepoToken">Secondary Repository Token:</label>
                  <Ajuda texto="Security token used by the most of the repository to give access to API." />
                </div>
                <VField name="secondaryRepositoryToken" type="text" v-model="configuration.secondaryRepositoryToken"
                  v-slot="{ field, errors }">
                  <InputText id="secRepoToken" type="text" v-bind="field" placeholder="xxxxxxxxxxxxxxxxxxxxxxx"
                    :invalid="errors.length > 0" />
                  <Message severity="error" icon="fa-solid fa-circle-exclamation" v-if="errors.length > 0">{{ errors[0]
                  }}</Message>
                </VField>
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
import { ref, onBeforeMount, computed } from 'vue'

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

// The project configuration being created/edited
const configuration = ref({})

// se o processamento esta ocorrendo ao salvar; os dados estão sendo carregados
const loading = ref(false);


/**
* Call the back-end to save a new measure reference value
* 
*/
const save = async (validate) => {
  const validator = await validate()

	if(configuration.value.mainRepository == null){
		message.error('Error', 'The Main Repository is mandatory')
		return;
	}

  if (validator.valid) {
    loading.value = true
    http
      .post('/api/project/configuration/save', configuration.value)
      .then(() => {
        message.success('Saved', 'Configuration of the Project ' + configuration.value.project.name + ' saved successfully!')
        cancel()
      }).catch((error) => {
        message.error('Error', error.data?.messageList)
      }).finally(() => (loading.value = false))
  }else {
    message.error('Error', 'Form contains invalid information')
  }
}

const cancel = () => {
  router.push('/project/list')
}


onBeforeMount(() => {
  
	loadMainRepositories()

	// if send a "id" in the router is because is editing the project
  if (route.params.id) {
    loading.value = true
    http.get(`/api/project/configuration/${route.params.id}`)
      .then((response) => {
        configuration.value = response.data
      }).catch((error) => {
        message.error('Error', error.data?.messageList)
      }).finally(() => (loading.value = false))
  }

})


const mainRepositories = ref([])

/**
 * Load all main repositories from backend
 */
const loadMainRepositories = async () => {
	http.get('/api/main-repository').then((response) => {
		mainRepositories.value = response.data
	})
}

// on the use select a main repository, set to project configuration
const onSelectRepository = () => {
};

const repositoryCount = computed(() => mainRepositories.value.length);

</script>

<style lang="scss" scoped></style>