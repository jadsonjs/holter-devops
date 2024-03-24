const getDefaultState = () => {

    return {
      loggedInUser: {},
      authenticated: false,
      enableLoginPage: true
    }
  }
  
  
  export default {
  
    namespaced: true,
  
    state: getDefaultState(),
  
    getters:{
      loggedInUser(state) {
        return state.loggedInUser
      },
      authenticated(state) {
        return state.authenticated
      },
      enableLoginPage(state) {
        return state.enableLoginPage
      }, 
    },
  
    mutations: {
      setLoggedInUser(state, loggedInUser) {
        state.loggedInUser = loggedInUser
      },
      setAuthenticated(state, authenticated) {
        state.authenticated = authenticated
      },
      setEnableLoginPage(state, enableLoginPage) {
        state.enableLoginPage = enableLoginPage
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
  