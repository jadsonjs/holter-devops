import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { useHttp } from '@/utils/axiosUtils'
import { useMessage } from '@/utils/message';

const storeID = 'collectors'
const appName = import.meta.env.VITE_APP_PUBLIC_PATH.replace(/[/]+/g, '')
const storeKey = `${appName}-${storeID}-store`

export const useCollectorsStore = defineStore(storeKey, () => {
	const collectorsList = ref([])
	
	// getters
	const getCollectorsList = computed(() => collectorsList.value)

	// actions
	function setCollectorsList(value) {
		collectorsList.value = value
	}

	const loading = ref(false);

async function loadCollectors() {
  const { http } = useHttp();

    loading.value = true
    await http
      .get('/api/collector')
      .then((result) => {
        setCollectorsList(result.data)
      })
      .catch((error) => {
        const { message } = useMessage();
        message.error('Erro ao carregar os coletores do sistema', error.data.messageList)
      })
      .finally(() => (loading.value = false))
}
  
	return {
		setCollectorsList,
		getCollectorsList,
    loadCollectors
	}

},
{
	persist: {
    key: storeKey,
  },
})