import request from '@/utils/request'

export default {
  login(userPhone, userPassword) {
    return request({
      url: `/client/users/phone/${userPhone}/actions/login`,
      method: 'post',
      params: { userPassword }
    })
  },

  register(userPhone, userPassword, userAlias) {
    return request({
      url: '/client/users',
      method: 'put',
      params: { userPhone, userPassword, userAlias }
    })
  },

  logout(userId) {
    return request({
      url: `/client/users/${userId}/actions/logout`,
      method: 'post'
    })
  },

  getUserInfo(userId) {
    return request({
      url: `/client/users/${userId}`,
      method: 'get'
    })
  },

  recharge(userId, money) {
    return request({
      url: `/client/users/${userId}/balance`,
      method: 'post',
      params: { money }
    })
  },

  updateAlias(userId, userAlias) {
    return request({
      url: `/client/users/${userId}/alias`,
      method: 'post',
      params: { userAlias }
    })
  },

  updatePhone(userId, phone) {
    return request({
      url: `/client/users/${userId}/phone`,
      method: 'post',
      params: { phone }
    })
  },

  updatePassword(userId, oldPassword, newPassword) {
    return request({
      url: `/client/users/${userId}/password`,
      method: 'post',
      params: { oldPassword, newPassword }
    })
  }
}
