// 导入封装好的 axios 实例
import request from '@/utils/request'
/**
 * 封装管理员登录和退出登录的 API 调用
 */
// 管理员登录接口
export default {
  login(account, password) {
    return request({
      // 请求URL：GET方式，账号通过URL路径传递
      url: `/manage/administrators/account/${account}/actions/login`,
      method: 'get',
      // 密码通过查询参数传递
      params: { password }
    })
  },

  logout() {
    return request({
      // 请求URL：POST方式，无参数
      url: '/manage/administrators/actions/logout',
      method: 'post'
    })
  }
}
