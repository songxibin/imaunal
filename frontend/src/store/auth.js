import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'
import { useRouter } from 'vue-router'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))
  const router = useRouter()

  const isAuthenticated = computed(() => !!token.value)

  async function login(credentials) {
    try {
      const response = await authApi.login(credentials)
      token.value = response.data.token
      user.value = {
        userId: response.data.userId,
        username: response.data.username
      }
      localStorage.setItem('token', token.value)
      localStorage.setItem('user', JSON.stringify(user.value))
      router.push('/')
    } catch (error) {
      throw error
    }
  }

  async function register(userData) {
    try {
      const response = await authApi.register(userData)
      return response
    } catch (error) {
      throw error
    }
  }

  function logout() {
    token.value = ''
    user.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    router.push('/login')
  }

  return {
    token,
    user,
    isAuthenticated,
    login,
    register,
    logout
  }
}) 