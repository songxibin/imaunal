import request from './request'

export const authApi = {
  login(data) {
    return request({
      url: '/auth/login',
      method: 'post',
      data
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
    return request({
      url: '/users/current',
      method: 'get'
    })
  },

  updateUser(data) {
    return request({
      url: '/users/current',
      method: 'put',
      data
    })
  }
} 