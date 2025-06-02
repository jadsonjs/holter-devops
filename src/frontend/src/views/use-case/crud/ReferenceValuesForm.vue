<template>
  <div class="card">
    <div class="font-semibold text-xl mb-4"> Reference Values</div>

    <!-- ajuda da página -->
    <p class="text-ajuda">
      Define Reference Values to Alert the users
    </p>

  </div>

  <div class="card min-h-screen">
    <Fluid>
      <Panel>

        <loading :loading="loading" />

        <VForm class="flex flex-col md:flex-row gap-8" v-slot="{ validate }">

          <div class="md:w-1/2" v-if="!loading">

            <div class="font-semibold text-xl">Reference Value</div>

            <div class="card flex flex-col gap-4">

              <div class="flex flex-col gap-2">
                <label for="input-metric">Select a Metric</label>
                <Select id="input-metric" v-model="metricReference.metric" :options="metricList"
                  optionLabel="denomination" placeholder="Select One" class="w-full"></Select>
              </div>

              <div class="flex flex-col gap-2">
                <label for="input-value">Reference Value:</label>
                <VField name="input-value" v-model="metricReference.value" rules="required||decimal"
                  v-slot="{ field, errors }">
                  <InputText v-bind="field" id="input-value" type="number" placeholder="Enter a Reference Value" />
                  <Message size="small" severity="secondary" variant="simple"
                    v-if="metricReference.metric && metricReference.metric.unit && metricReference.metric.unit != ' '">
                    (measured in {{ metricReference.metric.unit }})
                  </Message>
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
import { ref, onBeforeMount } from 'vue'

import Loading from '@/components/base/Loading.vue';

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

// The metric reference created/edited
const metricReference = ref({})

const metricList = ref([])

// os dados estão sendo carregados
const loading = ref(false);

/**
   * Call the back-end to save a new measure reference value
   * 
   */
const save = async (validate) => {
  const validator = await validate()

  if (validator.valid) {
    if (route.params.projectId) {
      metricReference.value.project = route.params.projectId
    }
    loading.value = true

    http
      .post('/api/ci-metric-reference/save', metricReference.value)
      .then(() => {
        message.success('Saved', 'Reference value for: ' + metricReference.value.metric.denomination + ' saved successfully!')
        cancel()
      }).catch((error) => {
        if (error.data.messageList[0].includes('JSON parse error')) {
          message.error('Error', 'Select a Valid Metric')
        }
        else {
          message.error('Error', error.data?.messageList)
        }
      }).finally(() => (loading.value = false))
  }
  else {
    message.error('Erro', 'Form contains invalid information')
  }
}


const cancel = () => {
  router.back()
}

const loadMetrics = async () => {
  loading.value = true
  http
    .get('api/metric/references')
    .then((result) => {
      metricList.value = result.data
    }).catch((error) => {
      message.error('Erro', error.data?.messageList)
    }).finally(() => (loading.value = false))
}

onBeforeMount(() => {
  loadMetrics()

  // if send a "id" in the router is because is editing the metric reference
  if (route.params.id) {
    loading.value = true
    http.get(`api/ci-metric-reference/${route.params.id}`)
      .then((response) => {
        metricReference.value = response.data
      }).catch((error) => {
        message.error('Error', error.data?.messageList)
      }).finally(() => (loading.value = false))
  } else {
    // creating a new one
  }
})
</script>

<style lang="scss" scoped></style>
