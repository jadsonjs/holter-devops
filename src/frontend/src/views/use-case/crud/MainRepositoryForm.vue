<template>
  <div class="card">
    <div class="font-semibold text-xl mb-4">Main Repository</div>

    <p class="text-ajuda">
      <div>Configure the main repository settings </div>
			<div>
				Main Repository is usually the repository where the project's source code is located. The place where
				the pipeline execution occurs and where we extract the build information.
			</div>
    </p>

  </div>

  <div class="card min-h-screen">
    <Fluid>
      <Panel>
        <loading :loading="loading" />

        <VForm class="flex flex-col gap-8" v-slot="{ validate }">

          <div class="md:w-1/2"  v-if="!loading">

            <div class="card flex flex-col gap-4">

							<div class="flex flex-col gap-2">
                <div class="flex items-center gap-2 justify-between">
                  <label for="mainRepoUrl">Name:</label>
                </div>
                <VField name="mainRepositoryName" type="text" v-model="config.name" rules="required" v-slot="{ field, errors }">
                  <InputText id="mainRepoUrl" type="text" v-bind="field" placeholder="main repo name" :disabled="isReadOnly" :invalid="errors.length > 0" />
                  <Message severity="error" icon="fa-solid fa-circle-exclamation" v-if="errors.length > 0">{{ errors[0]}}</Message>
                </VField>
              </div>	

              <!-- Main Repository URL -->
              <div class="flex flex-col gap-2">
                <div class="flex items-center gap-2 justify-between">
                  <label for="mainRepoUrl">URL:</label>
                  <Ajuda texto="API URL used to collect the metrics data." />
                </div>
                <VField name="mainRepositoryURL" type="text" v-model="config.url" rules="required"
                  v-slot="{ field, errors }">
                  <InputText id="mainRepoUrl" type="text" v-bind="field" placeholder="https://gitlab.com" :disabled="isReadOnly" :invalid="errors.length > 0" />
                  <Message severity="error" icon="fa-solid fa-circle-exclamation" v-if="errors.length > 0">{{ errors[0]}} </Message>
                </VField>
              </div>

              <!-- Main Repository Token -->
              <div class="flex flex-col gap-2">
                <div class="flex items-center gap-2 justify-between">
                  <label for="mainRepoToken">Token:</label>
                  <Ajuda texto="Security token used by the most of the repository to give access to API." />
                </div>
                <VField name="mainRepositoryToken" type="text" v-model="config.token"
                  rules="required" v-slot="{ field, errors }">
                  <InputText id="mainRepoToken" type="text" v-bind="field" placeholder="xxxxxxxxxxxxxxxxxxxxxxx"
                  :disabled="isReadOnly" :invalid="errors.length > 0" />
                  <Message severity="error" icon="fa-solid fa-circle-exclamation" v-if="errors.length > 0">{{ errors[0]
                    }}</Message>
                </VField>
              </div>

              <!-- Issues Error Labels -->
              <div class="flex flex-col gap-2">
                <div class="flex items-center gap-2 justify-between">
                  <label for="issueErrosLabels">Issues Errors Labels:</label>
                  <Ajuda texto="Labels for errors, bugs, and issues in the production branch." />
                </div>
                <VField name="issuesErrosLabels" type="text" v-model="config.issuesErrosLabels"
                  v-slot="{ field, errors }">
                  <InputText id="issueErrosLabels" type="text" v-bind="field" placeholder="error, bug, issue, mistake"
                  :disabled="isReadOnly" :invalid="errors.length > 0" />
                  <Message severity="error" icon="fa-solid fa-circle-exclamation" v-if="errors.length > 0">{{ errors[0]
                    }}</Message>
                </VField>
              </div>

              <!-- Production Branch -->
              <div class="flex flex-col gap-2">
                <div class="flex items-center gap-2 justify-between">
                  <label for="prodBranch">Production Branch:</label>
                  <Ajuda texto="The name of the branch that contains the production code." />
                </div>
                <VField name="produtionBranch" type="text" v-model="config.produtionBranch" rules="required"
                  v-slot="{ field, errors }">
                  <InputText id="prodBranch" type="text" v-bind="field" placeholder="master"
                  :disabled="isReadOnly" :invalid="errors.length > 0" />
                  <Message severity="error" icon="fa-solid fa-circle-exclamation" v-if="errors.length > 0">{{ errors[0]
                    }}</Message>
                </VField>
              </div>


            </div>

            <div class="flex justify-end space-x-4">
              <Button label="Cancel" icon="fa-solid fa-xmark" text @click="cancel" :style="{ width: '10em' }" />
              <Button v-if="!isReadOnly" label="Save" icon="fa-solid fa-floppy-disk" @click="save(validate)" :disabled="loading"
                :style="{ width: '10em' }" />            </div>
          </div>
        </VForm>
      </Panel>
    </Fluid>
  </div>
</template>

<script setup>
import { computed, ref, onBeforeMount } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useHttp } from '@/utils/axiosUtils';
import { useMessage } from '@/utils/message';
import Loading from '@/components/base/Loading.vue';

const router = useRouter();
const route = useRoute();
const { http } = useHttp();
const { message } = useMessage();

const config = ref({});
const loading = ref(false);

// Disable editing if only viewing
const isReadOnly = computed(() => !!route.query.viewOnly);

const save = async (validate) => {
  if (isReadOnly.value) return; // Prevent saving in read-only 

  const validator = await validate();
  if (validator.valid) {
    loading.value = true;
    http.post('/api/main-repository/save', config.value)
      .then(() => {
        message.success('Saved', 'Main Repository configuration saved successfully!');
        cancel();
      })
      .catch((error) => {
        message.error('Error', error.data?.messageList);
      })
      .finally(() => (loading.value = false));
  } else {
    message.error('Error', 'Form contains invalid information');
  }
};

const cancel = () => {
  router.back()
/*   router.push('/main-repository/list');
 */};

onBeforeMount(() => {
  if (route.params.id) {
    loading.value = true;
    http.get(`/api/main-repository/${route.params.id}`)
      .then((response) => {
        config.value = response.data;
      })
      .catch((error) => {
        message.error('Error', error.data?.messageList);
      })
      .finally(() => (loading.value = false));
  }
});
</script>

<style lang="scss" scoped></style>
