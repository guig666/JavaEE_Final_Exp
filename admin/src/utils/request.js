import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api',  // 请求基础路径，所有请求自动添加 /api 前缀
  timeout: 15000,  // 请求超时时间：15秒
  withCredentials: true  // 允许携带跨域凭证（如 cookie）
})

// 1. 请求拦截器：添加请求头
request.interceptors.request.use(
  config => {
    // 从 localStorage 获取管理员账号
    const adminAccount = localStorage.getItem('adminAccount')
    // 如果有管理员账号，添加到请求头
    if (adminAccount) {
      config.headers['X-Admin-Account'] = adminAccount
    }
    return config
  },
  error => {
    // 请求配置错误时的处理
    return Promise.reject(error)
  }
)

// 2. 响应拦截器：处理响应数据
request.interceptors.response.use(
  response => {
    const res = response.data
    // code = 1 表示成功
    if (res.code === 1) {
      return res
    } else if (res.code === -1) {
      // code = -1 表示参数错误
      ElMessage.error(res.msg || '参数错误')
      return Promise.reject(new Error(res.msg || '参数错误'))
    } else {
      // 其他错误码
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
  },
  // 响应拦截器：处理错误响应
  error => {
    // 有响应但状态码异常
    if (error.response) {
      const { status } = error.response
      if (status === 401 || status === 403) {
        ElMessage.error('登录已过期，请重新登录')
        // 重定向到登录页
        window.location.href = '/login'
      } else {
        ElMessage.error('网络错误，请稍后重试')
      }
    } else {
      // 无响应
      ElMessage.error('网络连接失败')
    }
    return Promise.reject(error)
  }
)

// 导出配置好的 axios 实例
export default request
