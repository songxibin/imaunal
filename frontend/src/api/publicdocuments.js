import request from '@/api/request'

export const documentsApi = {

  getPublicDocuments(params) {
    return request({
      url: '/public/pubdocuments',
      method: 'get',
      params
    })
  },
  
  getPublicDocumentById(id) {
    return request({
      url: `/public/pubdocuments/${id}`,
      method: 'get'
    })
  },



} 