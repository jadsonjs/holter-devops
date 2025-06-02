/**
 * Util script to axios library
 * Use:
 * import { useHttp } from '@/utils/axiosUtils';
 * const { http } = useHttp();
 *
 * http.get('/parameter/login-enable').then((result) => {
 *    ...
 * });
 *
 * const headers = { 'Content-Type': 'application/json' };
 *
 * http.post('/login', user.value, { headers })
 *   .then((result) => {
 *       ....
 *   }).catch((error) => {
 *       ....
 *   });
 *
 * @Author Jadson Santos - jadsonjs@gmail.com (codigo cedido a UFRN)
 */
import axios from 'axios';

import { createPinia, storeToRefs } from 'pinia';
const pinia = createPinia();

const baseURL = import.meta.env.VITE_APP_BACKEND_BASE_URL;
const publicPath = import.meta.env.VITE_APP_PUBLIC_PATH;

import { useLoginStore } from "@/stores/login"
const loginStore = useLoginStore(pinia)

// Using storeToRefs is optional but recommended for better reactivity with the store's state.
const { usuarioLogado, tokenAcesso } = storeToRefs(loginStore);


/*
 * Create an axios instance with a base backend URL
 * Adding default headers
 */
const axiosInstance = axios.create({
	// default headers
	headers: {
		Accept: 'application/json',
		'Content-Type': 'application/json',
	},
  baseURL: baseURL
});

// Add a request interceptor to add headers
axiosInstance.interceptors.request.use(
    (config) => {
        // Add headers before send request
        config.headers = {
						// headers passados pelo proprio usuario 
						// exemplo ao enviar arquivos: http.put('url', data, { headers: { 'Content-Type': 'multipart/form-data' } })
						...config.headers, 
            "usuario-logado": loginStore.isUsuarioLogado() ? JSON.stringify(usuarioLogado.value) : null,
						Authorization: tokenAcesso ? `Bearer  ${tokenAcesso.value}` : null,
        }
        return config;
    },
    (error) => {
        // Handle request errors
        return Promise.reject(error);
    }
);

// Add a response interceptor to handle common status codes
axiosInstance.interceptors.response.use(
    (response) => {
        // Add any logic you need for successful responses
        return response;
    },
    (error) => {
        // Handle errors based on status codes
        // internal error
        // 400 = BUSINESS EXCEPTION
        // 429 = TOO_MANY_REQUESTS
        // 500 = INTERNAL_SERVE_ERROR
        // 504 = GATEWAY_TIMEOUT
        if (error.response && (error.response.status === 500 || error.response.status === 400 || error.response.status === 429 || error.response.status === 504 )  ) {
            if (error.response) {
                return Promise.reject(error.response)
            } else {
                return Promise.reject(error)
            }
        }

        // UNAUTHORIZED redirect to login
        if (error.response && error.response.status === 401) {
					alert(error.response.data.messageList)
					loginStore.logout()
          window.location.replace(publicPath + 'login/')
          return false;
        }
    }
);

// Define wrapper methods for common HTTP methods
export function useHttp() {
    const http = {
        get: (url, config) => {
            return axiosInstance.get(url, config)
        },

        post: (url, data, config) => {
            return axiosInstance.post(url, data, config)
        },

        put: (url, data, config) => {
            return axiosInstance.put(url, data, config)
        },

        delete: (url, config) => {
            return axiosInstance.delete(url, config)
        },
    }

    return { http }
}