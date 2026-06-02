import request from '@/utils/request'

export default {
  getOrders(params) {
    return request({
      url: '/manage/orders',
      method: 'get',
      params
    })
  },

  getOrderDetail(order_id) {
    return request({
      url: `/manage/orders/${order_id}`,
      method: 'get'
    })
  }
}
