<template>
  <div class="card">
    <div class="font-semibold text-xl mb-4">List of Metrics</div>
    <p class="text-ajuda">
      A list of all supported metrics
    </p>

  </div>


  <div class="card min-h-screen">

    <loading :loading="loading" />

    <!-- Table of metrics -->
    <DataTable :value="measureList">
      <template #header>
        <div class="flex flex-wrap gap-2 items-center justify-between">
          <h4 class="m-0">Metrics</h4>
        </div>
      </template>

      <Column field="denomination" header="Name" sortable style="min-width: 8rem"></Column>
      <Column field="stage" header="Stage" sortable style="min-width: 1rem">
        <template #body="slotProps">
          <span :style="{ color: slotProps.data.stageHighlightColor }">{{ slotProps.data.stage }}</span>
        </template>
      </Column>
      <Column field="category" header="Category" sortable style="min-width: 8rem">
        <template #body="slotProps">
          <span :style="{ color: slotProps.data.categoryHighlightColor }">{{ slotProps.data.category }}</span>
        </template>
      </Column>
      <Column field="team" header="Team" sortable style="min-width: 8rem">
        <template #body="slotProps">
          <span :style="{ color: slotProps.data.teamHighlightColor }">{{ slotProps.data.team }}</span>
        </template>
      </Column>
      <Column field="unit" header="Unit" sortable style="min-width: 1rem"></Column>
      <Column field="description" header="Description" style="min-width: 10rem">
        <template #body="slotProps">
          <i>{{ slotProps.data.description }}</i>
        </template>
      </Column>
      <Column field="formula" header="Formula" sortable style="min-width: 12rem; font-variant: small-caps"></Column>
    </DataTable>

    <div class="flex mt-4">
      <Button label="Cancel" icon="fa-solid fa-times" class="p-button-outlined p-button-dark" @click="cancel()" />
    </div>

  </div>
</template>

<script setup>
import { ref, onBeforeMount, watch } from 'vue'
import { useRouter } from 'vue-router'
import Loading from '@/components/base/Loading.vue';

// axios
import { useHttp } from '@/utils/axiosUtils';
const { http } = useHttp();

// mensagens
import { useMessage } from '@/utils/message';
const { message } = useMessage();

// keep the list of metrics returned from back-end
const measureList = ref([])

const loading = ref(false)
const router = useRouter()

/**
 * Load all metrics.
 * This method will call the MetricController#getAll in the back-end that will return all metrics salved in database.
 */
 const loadMetrics = async () => {
  loading.value = true;
  try {
    const result = await http.get('/api/metric/all');
    measureList.value = result.data;
  } catch (error) {
    message.error('Erro', error.data?.messageList || 'Erro ao carregar métricas');
  } finally {
    loading.value = false;
  }
}

const cancel = () => {
  router.push('/')
}

onBeforeMount(() => {
  loadMetrics()
})

// watch(() => props.context, () => {
//   loadMetrics();
// });

</script>

<style scoped></style>