const getDefaultState = () => {

    return {
      lockNoCIMetrics: true
    }
  }
  
  
  export default {
  
    namespaced: true,
  
    state: getDefaultState(),
  
    getters:{
      lockNoCIMetrics(state) {
        return state.lockNoCIMetrics
      }, 
    },
  
    mutations: {
      setLockNoCIMetrics(state, lockNoCIMetrics) {
        state.lockNoCIMetrics = lockNoCIMetrics
      },
      resetState (state) {
        Object.assign(state, getDefaultState())
      }
    },
  
    actions: {
        reset ({ commit }) {
            commit('resetState')
        }
    }
  
  }
  