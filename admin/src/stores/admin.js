import { defineStore } from 'pinia'
import request from '@/utils/request'

export const useAdminStore = defineStore('admin', {
  state: () => ({
    adminInfo: null,  // 管理员信息
    adminAccount: null,  // 管理员账号
    adminPassword: null,  // 管理员密码
    isLoggedIn: false  // 是否登录状态
  }),

  actions: {
    // 登录
    async login(account, password) {
      try {
        // 调用后端登录接口
        const res = await request({
          url: `/manage/administrators/account/${account}/actions/login`,
          method: 'get',
          params: { password }
        })

        // 登录成功，更新状态和本地存储状态
        if (res.code === 1) {
          this.adminInfo = { admin_account: account }
          this.isLoggedIn = true
          // 1. 将登录状态持久化到 localStorage
          localStorage.setItem('adminLoggedIn', 'true')
          localStorage.setItem('adminAccount', account)
          return res
        } else {
          // 登录失败，抛出错误
          throw new Error(res.msg || '登录失败')
        }
      } catch (error) {
        console.error('登录失败:', error)
        throw error
      }
    },
    
    // 2. 刷新页面后从 localStorage 读取并恢复到 Pinia 状态中。
    // 从本地存储加载登录状态
    loadFromStorage() {
      const loggedIn = localStorage.getItem('adminLoggedIn')
      const account = localStorage.getItem('adminAccount')
      
      // 校验登录状态
      if (loggedIn === 'true' && account) {
        this.isLoggedIn = true
        this.adminInfo = { admin_account: account }
      } else {
        this.isLoggedIn = false
        this.adminInfo = null
      }
      return this.isLoggedIn
    },
    
    // 退出登录
    logout() {
      this.adminInfo = null
      this.isLoggedIn = false
      localStorage.removeItem('adminLoggedIn')
      localStorage.removeItem('adminAccount')
    },
    
    // 校验登录状态
    checkLogin() {
      const loggedIn = localStorage.getItem('adminLoggedIn')
      this.isLoggedIn = loggedIn === 'true'
      return this.isLoggedIn
    }
  }
})