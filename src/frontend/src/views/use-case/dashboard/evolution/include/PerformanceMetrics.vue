<template>
  <div>
    <div class="flex items-center mb-3">
      <Ajuda texto="Basic Metric related to Productivity and Resilience of the project"
        titulo="Basic Performance Metrics" severity="secondary" icon="fa-solid fa-circle-info fa-lg" enableHover>
      </Ajuda>
      <p class="font-bold ml-1">Basic Performance Metrics</p>
      <i v-if="lockNoCIMetrics" class="fa-solid fa-lock ml-3"></i>
    </div>

    <div v-if="!lockNoCIMetrics">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
        <div v-for="metric in performanceMetricsList" :key="metric.metric.id" class="col-span-1">
          <span class="metric_box" v-bind:style="{ 'background-color': boxBackgroundColor(metric) }">
            <div class="box_title">
              {{ metric.metric.denomination }}
            </div>
            <div>
              <span class="box_value"
                v-bind:style="{ 'color': metric.overReferenceValue ? '' : 'red', 'background-color': metric.overReferenceValue ? '' : 'var(--surface-ground)' }">
                Current Value: {{ metric.lastValue }} <span v-if="isStringValida(metric.metric.unit)" style="font-size: small;"> ( {{ metric.metric.unit }}
                  )</span>
              </span>
              <br>
              <span class="box_ref_value">
                Reference Value: {{ metric.valueReference }} <span v-if="isStringValida(metric.metric.unit)" style="font-size: small;"> ( {{ metric.metric.unit }}
                  )</span>
              </span>
            </div>
            <div class="p-1 flex">
              <div class="mb-auto mt-auto">
                <Button class="data-button" icon="fa-solid fa-chart-line" severity="info" text size="sm" rounded
                  v-on:click="showMetricEvolution(metric.metric.id)">
                </Button>
              </div>
              <div class="mt-1 ml-auto">
                <Ajuda :texto="metric.metric.description" :titulo="metric.metric.denomination"
                  :rotulo="metric.metric.formula" :isAlternateStyle="true"
                  :alternateBackgroundColor="boxBackgroundColor(metric)" enableHover />
              </div>
            </div>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import Ajuda from '@/components/base/Ajuda.vue';
import { isStringValida } from "@/utils/funcoes.js";

const props = defineProps({
  performanceMetricsList: {},

  highlight: {
    type: String,
    required: false
  },

})

// stores
import { storeToRefs } from 'pinia'
import { useParametrosStore } from '@/stores/parametros'
const parametrosStore = useParametrosStore()
const { lockNoCIMetrics } = storeToRefs(parametrosStore)

const emit = defineEmits(['showMetricEvolution'])

const showMetricEvolution = (metricId) => {
  // Emit an event to notify the parent about the change
  emit('showMetricEvolution', metricId);
}

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