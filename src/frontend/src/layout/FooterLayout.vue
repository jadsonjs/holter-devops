
<template>
  <div class="layout-footer">
	 	<div class="left">
			<span>CASE - Collaborative & Automated Software Engineering - <a href="http://caseufrn.github.io" target="_blank">http://caseufrn.github.io</a> </span>
			<br>
		</div>	


		<div class="right">
			
			
			
		</div>

		
  </div>	

</template>


<script setup>
import { computed } from 'vue';

const currentYear = computed(() => new Date().getFullYear() );

const environment =  import.meta.env.VITE_APP_ENVIRONMENT

</script>


<style scoped>
.left {
  /* Align to the left */
  text-align: left;
  padding: 0 1rem 0 1rem;
	width: 100%;
}

.right {
  /* Align to the right  */
  text-align: right;
  padding: 0 1rem 0 1rem;
	width: 100%;
}

.left a{
	display: inline-block;
}

.right a{
	display: inline-block;
}

</style>
