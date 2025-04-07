import request from '@/utils/request'

export const publicApi = {
  searchDocuments(params) {
    return request({
      url: '/public/documents',
      method: 'get',
      params
    })
  },

  getDocumentDetail(id) {
    return request({
      url: `/public/documents/${id}`,
      method: 'get'
    })
  }
}