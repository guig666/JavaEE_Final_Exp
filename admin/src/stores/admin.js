import { defineStore } from 'pinia'
import request from '@/utils/request'

export const useAdminStore = defineStore('admin', {
  state: () => ({
    adminInfo: null,
    isLoggedIn: false
  }),

  actions: {
    async login(account, password) {
      try {
        const res = await request({
          url: `/manage/administrators/account/${account}/actions/login`,
          method: 'get',
          params: { password }
        })

        if (res.code === 1) {
          this.adminInfo = { admin_account: account }
          this.isLoggedIn = true
          localStorage.setItem('adminLoggedIn', 'true')
          localStorage.setItem('adminAccount', account)
          return res
        } else {
          throw new Error(res.msg || '登录失败')
        }
      } catch (error) {
        console.error('登录失败:', error)
        throw error
      }
    },

    loadFromStorage() {
      const loggedIn = localStorage.getItem('adminLoggedIn')
      const account = localStorage.getItem('adminAccount')
      
      if (loggedIn === 'true' && account) {
        this.isLoggedIn = true
        this.adminInfo = { admin_account: account }
      } else {
        this.isLoggedIn = false
        this.adminInfo = null
      }
      return this.isLoggedIn
    },

    logout() {
      this.adminInfo = null
      this.isLoggedIn = false
      localStorage.removeItem('adminLoggedIn')
      localStorage.removeItem('adminAccount')
    },

    checkLogin() {
      const loggedIn = localStorage.getItem('adminLoggedIn')
      this.isLoggedIn = loggedIn === 'true'
      return this.isLoggedIn
    }
  }
})