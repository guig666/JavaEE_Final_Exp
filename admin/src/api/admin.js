import request from '@/utils/request'

export default {
  login(account, password) {
    return request({
      url: `/manage/administrators/account/${account}/actions/login`,
      method: 'get',
      params: { password }
    })
  },

  logout() {
    return request({
      url: '/manage/administrators/actions/logout',
      method: 'post'
    })
  }
}
