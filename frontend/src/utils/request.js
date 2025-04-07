import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// Create axios instance
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 15000
})

// Request interceptor
service.interceptors.request.use(
  config => {
    // Get token from localStorage
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // If the response code is not 200, show error message
    if (res.code !== 200) {
      ElMessage({
        message: res.message || '请求失败',
        type: 'error',
        duration: 5 * 1000
      })
      
      // Handle 401 Unauthorized
      if (res.code === 401) {
        // Clear token and redirect to login page
        localStorage.removeItem('token')
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res
    }
  },
  error => {
    console.error('Response error:', error)
    
    // Handle network errors
    if (error.response) {
      const status = error.response.status
      
      if (status === 401) {
        // Unauthorized, clear token and redirect to login
        localStorage.removeItem('token')
        router.push('/login')
      } else if (status === 403) {
        ElMessage({
          message: '没有权限执行此操作',
          type: 'error',
          duration: 5 * 1000
        })
      } else if (status === 404) {
        ElMessage({
          message: '请求的资源不存在',
          type: 'error',
          duration: 5 * 1000
        })
      } else if (status === 500) {
        ElMessage({
          message: '服务器错误，请稍后再试',
          type: 'error',
          duration: 5 * 1000
        })
      } else {
        ElMessage({
          message: error.response.data?.message || '请求失败',
          type: 'error',
          duration: 5 * 1000
        })
      }
    } else {
      ElMessage({
        message: '网络错误，请检查您的网络连接',
        type: 'error',
        duration: 5 * 1000
      })
    }
    
    return Promise.reject(error)
  }
)

export default service 