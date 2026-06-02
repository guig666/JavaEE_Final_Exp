import request from '@/utils/request'

export default {
  getOrders() {
    return request({
      url: '/client/orders',
      method: 'get'
    })
  },

  getOrder(orderId) {
    return request({
      url: `/client/orders/${orderId}`,
      method: 'get'
    })
  },

  createOrder(navLocationId) {
    return request({
      url: '/client/orders',
      method: 'put',
      params: { navLocationId }
    })
  },

  returnOrder(orderId, navLocationId) {
    return request({
      url: `/client/orders/${orderId}`,
      method: 'post',
      params: { navLocationId }
    })
  }
}
