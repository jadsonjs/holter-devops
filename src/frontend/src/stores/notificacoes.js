/**
 * Universidade Federal do Rio Grande do Norte
 * Instituto Metrópole Digital
 * Diretoria de Tecnologia da Informação
 *
 *
 * Guarda um cache das notificações da aplicação.
 *
 * @author Jadson Santos - jadson.santos@ufrn.br
 * @since  28/08/2024
 */
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

const storeID = 'notificacoes'
const appName = import.meta.env.VITE_APP_PUBLIC_PATH.replace(/[/]+/g, '')
const storeKey = `${appName}-${storeID}-store`

export const useNotificacaoStore = defineStore(storeKey, () => {
  const notificacoes = ref([])
  const dataProximaAtualizacao = ref(null)

  const isCacheExpirado = computed(() => {
    return (
      dataProximaAtualizacao.value === null ||
      new Date(dataProximaAtualizacao.value).getTime() < new Date().getTime()
    )
  })

  function setNotificacoes(newNotificacoes) {
    notificacoes.value = newNotificacoes
    updateCache()
  }

  function updateCache() {
    dataProximaAtualizacao.value = new Date(new Date().getTime() + 1 * 60 * 1000)
  }

  return {
    notificacoes,
    dataProximaAtualizacao,
    isCacheExpirado,
    setNotificacoes,
    updateCache,
  }
}, 
{
  persist: {
    key: storeKey,
  },
})