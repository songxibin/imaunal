import request from './request'

export const documentsApi = {
  uploadDocument(formData) {
    return request({
      url: '/documents/upload',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  getDocuments(params) {
    return request({
      url: '/documents',
      method: 'get',
      params
    })
  },

  getDocumentById(id) {
    return request({
      url: `/documents/${id}`,
      method: 'get'
    })
  },

  updateDocument(id, data) {
    return request({
      url: `/documents/${id}`,
      method: 'put',
      data
    })
  },

  deleteDocument(id) {
    return request({
      url: `/documents/${id}`,
      method: 'delete'
    })
  }
} 