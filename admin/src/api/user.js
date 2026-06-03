import request from '@/utils/request'

/**
 * 封装用户管理 API（列表、充值、删除）
 */
export default {
  // 获取用户列表（支持分页、搜索）
  getUsers(params) {
    return request({
      url: '/manage/users',
      method: 'get',
      params
    })
  },
  // 充值用户（指定用户ID和充值金额）
  rechargeUser(userId, money) {
    return request({
      url: `/manage/users/${userId}/balance`,
      method: 'post',
      params: { money }
    })
  },
  // 删除用户（指定用户ID）
  deleteUser(userId) {
    return request({
      url: `/manage/users/${userId}`,
      method: 'delete'
    })
  }
}
