import request from './request'

export const authApi = {
  login(data) {
    console.log('Sending login request with data:', data)
    return request({
      url: '/auth/login',
      method: 'post',
      data
    }).then(response => {
      console.log('Login response:', response)
      return response
    }).catch(error => {
      console.error('Login API error:', error)
      throw error
    })
  },

  register(data) {
    return request({
      url: '/auth/register',
      method: 'post',
      data
    })
  },

  getCurrentUser() {
    console.log('Fetching current user')
    return request({
      url: '/users/current',
      method: 'get'
    }).then(response => {
      console.log('Current user response:', response)
      return response
    }).catch(error => {
      console.error('Get current user error:', error)
      throw error
    })
  },

  updateUser(data) {
    return request({
      url: '/users/current',
      method: 'put',
      data
    })
  },

  getUserStats: (userId) => {
    return request({
      url: `/users/${userId}/stats`,
      method: 'get'
    })
  }
}