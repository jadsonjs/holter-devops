/**
 * Universidade Federal do Rio Grande do Norte
 * Instituto Metrópole Digital
 * Diretoria de Tecnologia da Informação
 *
 *
 * Guarda as configuraçoes da aplicacao
 *
 * @author Jadson Santos - jadson.santos@ufrn.br
 * @since  28/08/2024
 */

import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

const storeID = 'configuracoes'
const appName = import.meta.env.VITE_APP_PUBLIC_PATH.replace(/[/]+/g, '')
const storeKey = `${appName}-${storeID}-store`

export const useConfiguracoesStore = defineStore(storeKey, () => {
  
	// state
	const primary = ref('imd') // default primary color
	const surface = ref('slate')
	const darkTheme= ref(false)
	const menuMode = ref('static')

	// getters
	const getPrimary = computed(() => primary.value)
	const getSurface = computed(() => surface.value)
  const getDarkTheme = computed(() => darkTheme.value)
  const getMenuMode = computed(() => menuMode.value)

	// actions
  function setPrimary(color) {
		primary.value = color;
  }

	function setSurface(value) {
    surface.value = value
  }

  function setDarkTheme(value) {
    darkTheme.value = value
  }

  function setMenuMode(value) {
    menuMode.value = value
  }

  return {
		primary,
    surface,
    darkTheme,
    menuMode,
    setPrimary,
    setSurface,
    setDarkTheme,
    setMenuMode,
    getPrimary,
    getSurface,
    getDarkTheme,
    getMenuMode,
  }
}, 
{
  persist: {
    key: storeKey,
  },
})