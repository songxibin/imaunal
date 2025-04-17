import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'
import { useRouter } from 'vue-router'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const refreshToken = ref(localStorage.getItem('refreshToken') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))
  const router = useRouter()

  const isAuthenticated = computed(() => !!token.value)

  const isLoggedIn = computed(() => {
    return user.username && user.username.length > 0
  })

  async function login(credentials) {
    try {
      console.log('Auth store login with credentials:', credentials)
      const response = await authApi.login(credentials)
      console.log('Auth store received response:', response)
      
      // Only update token if it's provided in the response
      if (response.token) {
        token.value = response.token
        localStorage.setItem('token', token.value)
      }
      
      if (response.refresh_token) {
        refreshToken.value = response.refresh_token
        localStorage.setItem('refreshToken', refreshToken.value)
      }
      
      // Update user information
      user.value = {
        userId: response.userid,
        username: response.username,
        email: response.email,
        fullName: response.fullName,
        roles: response.roles,
        createdAt: response.createdAt
      }
      
      console.log('Auth store updated state:', {
        token: token.value,
        refreshToken: refreshToken.value,
        user: user.value
      })
      
      localStorage.setItem('user', JSON.stringify(user.value))
      
      console.log('Redirecting to dashboard')
      router.push('/')
    } catch (error) {
      console.error('Auth store login error:', error)
      throw error
    }
  }

  // New function to update user information without changing token
  function updateUserInfo(userData) {
    user.value = {
      ...user.value,
      ...userData
    }
    localStorage.setItem('user', JSON.stringify(user.value))
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
    refreshToken.value = ''
    user.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('user')
    router.push('/login')
  }

  return {
    token,
    refreshToken,
    user,
    isAuthenticated,
    login,
    register,
    logout,
    updateUserInfo
  }
}) 