import request from '@/api/request'

export const documentsApi = {
  uploadDocument(data) {
    return request({
      url: '/documents/upload',
      method: 'post',
      data,
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
  },
  
  publishDocument(id) {
    return request({
      url: `/documents/${id}/publish`,
      method: 'post'
    })
  },
  
  unpublishDocument(id) {
    return request({
      url: `/documents/${id}/unpublish`,
      method: 'post'
    })
  },

  getDashboardStats() {
    return request({
      url: '/documents/stats',
      method: 'get'
    })
  },
  
  translateDocument: (documentId, translateParams) => {
    return request({
      url: `/documents/${documentId}/translate`,
      method: 'post',
      data: translateParams
    })
  }
}