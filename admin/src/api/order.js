import request from '@/utils/request'
/**
 * 封装订单列表查询和订单详情查看的 API
 */
export default {
  // 获取订单列表（支持分页、搜索）
  getOrders(params) {
    return request({
      url: '/manage/orders',
      method: 'get',
      params
    })
  },

  getOrderDetail(order_id) {
    // 获取订单详情（根据订单ID）
    return request({
      url: `/manage/orders/${order_id}`,
      method: 'get'
    })
  }
}
