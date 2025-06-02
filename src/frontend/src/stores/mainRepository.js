import { defineStore } from 'pinia'
import { ref } from 'vue'

const storeID = 'main-repository'
const appName = import.meta.env.VITE_APP_PUBLIC_PATH.replace(/[/]+/g, '')

const storeKey = `${appName}-${storeID}-store`

export const useMainRepositoryStore = defineStore(storeKey, () => {
  const selectedRepository = ref(null)

  function setRepository(repo) {
    selectedRepository.value = repo
  }

  function clearRepository() {
    selectedRepository.value = null
  }

  return {
    selectedRepository,
    setRepository,
    clearRepository
  }
},
{
	persist: {
    key: storeKey,
  },
})