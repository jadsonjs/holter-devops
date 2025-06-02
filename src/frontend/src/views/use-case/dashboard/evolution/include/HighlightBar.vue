<template>
  <div class="space-y-6 mb-4">

    <div class="flex items-center">
      <h5 class="mr-2 text-xl font-semibold">Highlight Metrics:</h5>
      <div class="flex items-center gap-3">
        <RadioButton :modelValue="props.highlight" id="stage" name="stage" value="stage"
          @update:modelValue="updateValue"></RadioButton>
        <label for="stage">Stage</label>
        <RadioButton :modelValue="props.highlight" id="category" name="category" value="category"
          @update:modelValue="updateValue"></RadioButton>
        <label for="category">Category</label>
      </div>
    </div>

    <div v-if="highlight === 'stage'" class="space-x-2">
      <span class="py-2 px-5 text-3xl font-bold text-white rounded-xl" style="background-color:#17a2b8">Continuous
        Integration</span>
      <span class="py-2 px-5 text-3xl font-bold text-white rounded-xl" style="background-color: #28a745;">Continuous Delivery</span>
      <!-- <span class="metric_legend bg-purple-600 text-white py-1 px-3 rounded">Continuous Monitoring</span>
      <span class="metric_legend bg-red-600 text-white py-1 px-3 rounded">Continuous Security</span> -->
    </div>

    <div v-if="highlight === 'category'" class="space-x-2">
      <span class="py-2 px-5 text-3xl font-bold text-white rounded-xl bg-teal-800" style="background-color: #097969;">Productivity</span>
      <span class="py-2 px-5 text-3xl font-bold text-white rounded-xl bg-red-700" style="background-color: #D22B2B;">Resilience</span>
    </div>

    <!-- <div v-if="highlight === 'team'" class="space-x-2">
      <span class="metric_legend bg-teal-400 text-white py-1 px-3 rounded">Development</span>
      <span class="metric_legend bg-lime-500 text-white py-1 px-3 rounded">Operation</span>
    </div> -->
  </div>
</template>

<script setup>

const props = defineProps({
  highlight: {
    type: String, // This defines the data type of the prop
    default: ''  // This sets the default value to an empty string
  },
});

const emit = defineEmits(['updateHighlightProp']);

// Emit an event to notify the parent about the change
const updateValue = (value) => {
  emit('updateHighlightProp', value);
}
</script>


<style lang="scss" scoped></style>