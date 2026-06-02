import request from '@/utils/request'

export default {
  getUsers(params) {
    return request({
      url: '/manage/users',
      method: 'get',
      params
    })
  },

  rechargeUser(userId, money) {
    return request({
      url: `/manage/users/${userId}/balance`,
      method: 'post',
      params: { money }
    })
  },

  deleteUser(userId) {
    return request({
      url: `/manage/users/${userId}`,
      method: 'delete'
    })
  }
}
