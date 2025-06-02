<template>
  <div class="border-t border-gray-100">
    <dl class="divide-y divide-gray-100">
      <div class="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
        <div class="font-semibold text-lg">Frequency</div>
        <div>{{ scheduler.frequencyOfExecution }}</div>
      </div>
      <div class="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
        <div class="font-semibold text-lg">Start Execution at:</div>
        <div>{{ scheduler.startExecution }}</div>
      </div>
      <div class="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
        <div class="font-semibold text-lg">Last Execution at:</div>
        <div>{{ scheduler.lastExecution != null ? scheduler.lastExecution : ' - - - ' }}</div>
      </div>
      <div class="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
        <div class="font-semibold text-lg">Automatic:</div>
        <div>{{ scheduler.automatic }}</div>
      </div>
    </dl>
  </div>
</template>

<script setup>
defineProps({
  scheduler: {
    type: Object,
    required: true,
  },
});
</script>