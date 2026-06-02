import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import userApi from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)

  const isLoggedIn = computed(() => user.value !== null)

  const userId = computed(() => user.value?.user_id)

  async function login(phone, password) {
    const res = await userApi.login(phone, password)
    if (res.data && res.data.length > 0) {
      user.value = res.data[0]
      localStorage.setItem('user', JSON.stringify(res.data[0]))
    }
    return res
  }

  async function register(userPhone, userPassword, userAlias) {
    return await userApi.register(userPhone, userPassword, userAlias)
  }

  async function logout() {
    const userId = user.value?.user_id
    
    user.value = null
    localStorage.removeItem('user')
    
    if (userId) {
      try {
        await userApi.logout(userId)
      } catch (error) {
        console.log('Logout API call failed (may be normal):', error.message)
      }
    }
  }

  async function getUserInfo(userId) {
    const res = await userApi.getUserInfo(userId)
    if (res.data && res.data.length > 0) {
      user.value = res.data[0]
      localStorage.setItem('user', JSON.stringify(res.data[0]))
    }
    return res
  }

  async function recharge(userId, money) {
    const res = await userApi.recharge(userId, money)
    if (res.data && res.data.length > 0) {
      user.value = res.data[0]
      localStorage.setItem('user', JSON.stringify(res.data[0]))
    }
    return res
  }

  async function updateAlias(userId, userAlias) {
    const res = await userApi.updateAlias(userId, userAlias)
    if (res.data && res.data.length > 0) {
      user.value = res.data[0]
      localStorage.setItem('user', JSON.stringify(res.data[0]))
    }
    return res
  }

  async function updatePhone(userId, phone) {
    const res = await userApi.updatePhone(userId, phone)
    if (res.data && res.data.length > 0) {
      user.value = res.data[0]
      localStorage.setItem('user', JSON.stringify(res.data[0]))
    }
    return res
  }

  async function updatePassword(userId, oldPassword, newPassword) {
    const res = await userApi.updatePassword(userId, oldPassword, newPassword)
    if (res.data && res.data.length > 0) {
      user.value = res.data[0]
      localStorage.setItem('user', JSON.stringify(res.data[0]))
    }
    return res
  }

  function loadFromStorage() {
    try {
      const savedUser = localStorage.getItem('user')
      if (savedUser) {
        user.value = JSON.parse(savedUser)
      } else {
        user.value = null
      }
    } catch (e) {
      console.error('Failed to load user from storage:', e)
      user.value = null
      localStorage.removeItem('user')
    }
  }

  return {
    user,
    isLoggedIn,
    userId,
    login,
    register,
    logout,
    getUserInfo,
    recharge,
    updateAlias,
    updatePhone,
    updatePassword,
    loadFromStorage
  }
})
