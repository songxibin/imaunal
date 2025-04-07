import request from '@/utils/request'

export const documentsApi = {
  uploadDocument(data) {
    return request({
      url: '/api/v1/documents/upload',
      method: 'post',
      data,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  getDocuments(params) {
    return request({
      url: '/api/v1/documents',
      method: 'get',
      params
    })
  },

  getPublicDocuments(params) {
    return request({
      url: '/public/documents',
      method: 'get',
      params
    })
  },
  
  getPublicDocumentById(id) {
    return request({
      url: `/public/documents/${id}`,
      method: 'get'
    })
  },

  getDocumentById(id) {
    return request({
      url: `/api/v1/documents/${id}`,
      method: 'get'
    })
  },



  updateDocument(id, data) {
    return request({
      url: `/api/v1/documents/${id}`,
      method: 'put',
      data
    })
  },

  deleteDocument(id) {
    return request({
      url: `/api/v1/documents/${id}`,
      method: 'delete'
    })
  },
  
  publishDocument(id) {
    return request({
      url: `/api/v1/documents/${id}/publish`,
      method: 'post'
    })
  },
  
  unpublishDocument(id) {
    return request({
      url: `/api/v1/documents/${id}/unpublish`,
      method: 'post'
    })
  },

  getDashboardStats() {
    return request({
      url: '/api/v1/documents/stats',
      method: 'get'
    })
  }
} 