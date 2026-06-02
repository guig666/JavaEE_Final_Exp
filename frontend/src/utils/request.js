import axios from 'axios'
import { showToast } from 'vant'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  withCredentials: true
})

request.interceptors.request.use(
  config => {
    console.log('[Request]', config.method?.toUpperCase(), config.url)
    return config
  },
  error => {
    console.error('[Request Error]', error)
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    console.log('[Response]', response.config.url, response.status)
    const res = response.data
    if (res.code === 1) {
      return res
    } else if (res.code === -1) {
      if (res.msg && (res.msg.includes('登录') || res.msg.includes('请先'))) {
        console.warn('[Auth Error]', res.msg)
        showToast(res.msg)
        localStorage.removeItem('user')
        setTimeout(() => {
          router.push('/login')
        }, 1500)
      } else {
        showToast(res.msg || '参数错误')
      }
      return Promise.reject(new Error(res.msg || '参数错误'))
    } else {
      showToast(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
  },
  error => {
    console.error('[Response Error]', error.message)
    if (error.response) {
      const { status, data } = error.response
      console.error('[Response Error Status]', status)
      console.error('[Response Error Data]', data)
      if (status === 401 || status === 403) {
        showToast('登录已过期，请重新登录')
        localStorage.removeItem('user')
        router.push('/login')
      } else {
        showToast('网络错误，请稍后重试')
      }
    } else {
      showToast('网络连接失败')
    }
    return Promise.reject(error)
  }
)

export default request