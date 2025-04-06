import axios from 'axios'
import { useAuthStore } from '@/store/auth'

const request = axios.create({
  baseURL: '/api/v1',
  timeout: 5000
})

request.interceptors.request.use(
  config => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          const authStore = useAuthStore()
          authStore.logout()
          break
        case 403:
          // Handle forbidden
          break
        case 404:
          // Handle not found
          break
        case 500:
          // Handle server error
          break
      }
    }
    return Promise.reject(error)
  }
)

export default request 