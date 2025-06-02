<template>
  <div>
    <div class="flex items-center mb-3">
      <Ajuda
          texto="Repository metrics aim to provide insights into developer activity and collaboration patterns within the project."
          titulo="Repository Performance Metrics"
          severity="secondary"
          icon="fa-solid fa-circle-info fa-lg"
          enableHover
      />
      <p class="font-bold ml-1">Repository Performance Metrics</p>
      <i v-if="lockNoCIMetrics" class="fa-solid fa-lock ml-3"></i>
    </div>

    <div v-if="!lockNoCIMetrics">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div
            v-for="metric in performanceMetricsList"
            :key="metric.metric.id"
            class="col-span-1"
        >
      <span
          class="metric_box"
          v-bind:style="{ 'background-color': boxBackgroundColor(metric) }"
          style="max-width: 100%;"
      >
        <div class="box_title truncate text-center font-bold text-white underline underline-offset-4">
          {{ metric.metric.denomination }}
        </div>
        <div class="p-2 flex justify-center items-center gap-4">
          <Button
              class="data-button"
              icon="fa-solid fa-chart-line"
              severity="info"
              text
              size="sm"
              rounded
              v-on:click="showMetricEvolution(metric.metric.id)"
          />
          <Ajuda
              :texto="metric.metric.description"
              :titulo="metric.metric.denomination"
              :rotulo="metric.metric.formula"
              :isAlternateStyle="true"
              :alternateBackgroundColor="boxBackgroundColor(metric)"
              enableHover
          />
        </div>
      </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import Ajuda from '@/components/base/Ajuda.vue';

const props = defineProps({
  performanceMetricsList: {},

  highlight: {
    type: String,
    required: false
  },
});

import {storeToRefs} from 'pinia';
import {useParametrosStore} from '@/stores/parametros';

const parametrosStore = useParametrosStore();
const {lockNoCIMetrics} = storeToRefs(parametrosStore);

const emit = defineEmits(['showMetricEvolution']);

const showMetricEvolution = (metricId) => {
  emit('showMetricEvolution', metricId);
};

const boxBackgroundColor = (metric) => {
  return props.highlight === 'category'
    ? metric.metric.categoryHighlightColor
    : (props.highlight === 'stage'
      ? metric.metric.stageHighlightColor
      : (props.highlight === 'team'
        ? metric.metric.teamHighlightColor
        : ''));
};

</script>

<style lang="scss" scoped></style>
