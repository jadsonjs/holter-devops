import axios from 'axios'

class AxiosHandler {

  _store = null
  _router = null
  _axios = null

  /**
    * Construtor
    */
  constructor() {

    if ( ! AxiosHandler.instance) {  

      this._axios = axios.create ({
        baseURL: process.env.VUE_APP_BACKEND_BASE_URL,
        headers: this._headers()
      });

      this._interceptorsConfig()

      AxiosHandler.instance = this;
    }

    return AxiosHandler.instance;
  }

  axios = () => this._axios

  setStore = (store) => {
    this._store = store;
  }

  setRouter = (router) => {
    this._router = router;
  }

  _headers = () => {
    const header = {
       'Accept': 'application/json',
       'Content-Type': 'application/json',
    }
    return header
  }

  _headersComStore = () => {
    const headers = {
      'logged-in-user'       : this._store.state.login.loggedInUser      ? JSON.stringify(this._store.state.login.loggedInUser)  : null,
    }
    return headers
  }


  _interceptorsConfig() {

    this._axios.interceptors.request.use( (config) => {
       // Each request to the backend, add the defined headers
       config.headers.common = Object.assign( config.headers.common, this._headersComStore() )
       return config
    }, () => {
      // return Promise.reject(error)
    })


    this._axios.interceptors.response.use( (response) => {
       return response
    }, error => {

        // internal error
        if (error.response && error.response.status === 500) {
          if (error.response) {
            return Promise.reject(error.response)
          } else {
            return Promise.reject(error)
          }
        }

        // UNAUTHORIZED
        if (error.response && error.response.status === 401) {
          this._store.dispatch('login/reset') 
          localStorage.clear();
          alert(error.response.data)
          window.location.replace(process.env.VUE_APP_PUBLIC_PATH+'login')
          return false;
        }


        // TOO_MANY_REQUESTS
        if (error.response && error.response.status === 429) {
          if (error.response) {
            return Promise.reject(error.response)
          } else {
            return Promise.reject(error)
          }
        }

        // business exception
        if (error.response && error.response.status === 400) {
            if (error.response) {
              return Promise.reject(error.response)
            } else {
              return Promise.reject(error)
            }
        }
    })

  }

  /*******************************************************
   *  The wrapper methods of the axial methods that must be called 
   *******************************************************/

  get(url, obj) {
    return this._axios.get(url, obj)
  }

  post (url, obj) {
    return this._axios.post(url, obj)
  }

  put(url, obj) {
    return this._axios.put(url, obj)
  }

  delete(url, obj) {
    return this._axios.delete(url, { data: obj })
  }
  
}

export default new AxiosHandler()