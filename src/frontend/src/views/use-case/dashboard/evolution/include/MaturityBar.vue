<template>
  <h5 class="font-semibold text-xl">DevOps Maturity</h5>


  <div class="card">

    <div class="flex flex-col  space-y-4">
      <div class="flex justify-between w-full">
        <img src="@/assets/img/baby.png" alt="Baby" class="w-9">
        <img src="@/assets/img/expert.png" alt="Expert" class="w-9">
      </div>
      <!-- Metrics -->
      <MeterGroup :value="metrics" class="text-lg"></MeterGroup>
    </div>
  </div>
</template>

<script setup>

// Parameters store
import { computed } from 'vue'
import { storeToRefs } from 'pinia'
import { useParametrosStore } from '@/stores/parametros';
const parametrosStore = useParametrosStore();
const { lockNoCIMetrics } = storeToRefs(parametrosStore);

const props = defineProps({
  ciMetricsList: {},
  doraMetricsList: {},
  performanceMetricsList: {},
  operationMetricsList: {},
  maturity: {},
});

const metrics = computed(() => {
  const baseMetrics = [
    {
      label: "CI Metrics: " + props.maturity.ci + " / " + props.ciMetricsList.length,
      value: (props.maturity.ci / props.ciMetricsList.length) * 100,
      color: "#17a2b8",
    },
  ];

  // Only add these metrics if lockNoCIMetric is false
  if (lockNoCIMetrics && !lockNoCIMetrics.value) {
    baseMetrics.push(
      {
        label: "DORA Metrics: " + props.maturity.dora + " / " + props.doraMetricsList.length,
        value: (props.maturity.dora / props.doraMetricsList.length) * 100,
        color: "#28a745",
      },
      {
        label: "Performance Metrics: " + props.maturity.performance + " / " + props.performanceMetricsList.length,
        value: (props.maturity.performance / props.performanceMetricsList.length) * 100,
        color: "#6c757d",
      },
      {
        label: "Operation Metrics: " + props.maturity.operation + " / " + props.operationMetricsList.length,
        value: (props.maturity.operation / props.operationMetricsList.length) * 100,
        color: "#dc3545",
      }
    );
  }

  return baseMetrics;
});

</script>


<style lang="scss" scoped>
.p-metergroup {
  --p-metergroup-meters-size: 1.2rem;
  --p-metergroup-label-marker-size: 1rem;
  --p-metergroup-label-icon-size: 1rem;
  --p-metergroup-label-list-horizontal-gap: 3rem;
}
</style>