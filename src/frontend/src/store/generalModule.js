const getDefaultState = () => {

  return {
      sideBarToggled: false,      
  }
}


export default {

  namespaced: true,

 
  state: getDefaultState(),

  /**
   * Para acesso aos dados desse modulo
   */
  getters:{
      sideBarToggled(state) {
        return state.sideBarToggled
      },
  },

  mutations: {
      setSideBarToggled(state, sideBarToggled) {
        state.sideBarToggled = sideBarToggled
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