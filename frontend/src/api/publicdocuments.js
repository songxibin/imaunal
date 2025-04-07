import request from '@/utils/request'

export const documentsApi = {

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



} 