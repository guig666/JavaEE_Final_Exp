import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  withCredentials: true
})

request.interceptors.request.use(
  config => {
    const adminAccount = localStorage.getItem('adminAccount')
    if (adminAccount) {
      config.headers['X-Admin-Account'] = adminAccount
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 1) {
      return res
    } else if (res.code === -1) {
      ElMessage.error(res.msg || '参数错误')
      return Promise.reject(new Error(res.msg || '参数错误'))
    } else {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
  },
  error => {
    if (error.response) {
      const { status } = error.response
      if (status === 401 || status === 403) {
        ElMessage.error('登录已过期，请重新登录')
        // 避免循环依赖，使用 window.location
        window.location.href = '/login'
      } else {
        ElMessage.error('网络错误，请稍后重试')
      }
    } else {
      ElMessage.error('网络连接失败')
    }
    return Promise.reject(error)
  }
)

export default request
