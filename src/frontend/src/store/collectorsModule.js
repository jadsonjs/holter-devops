

const getDefaultState = () => {
    return {
        // keep the list of collectors supported by the tool
        collectorsList: [],      
    }
  }
  
  
  export default {
  
    namespaced: true,
   
    state: getDefaultState(),
  
    /**
     * Para acesso aos dados desse modulo
     */
    getters:{
        collectorsList(state) {
          return state.collectors
        },
    },
  
    mutations: {
        setCollectorsList(state, collectorsList) {
          state.collectorsList = collectorsList
        },
        resetState (state) {
          Object.assign(state, getDefaultState())
        }
    },
  
    actions: {
        // default action that clean de state //
        reset ({ commit }) {
            commit('resetState')
        }
    }
  
  }