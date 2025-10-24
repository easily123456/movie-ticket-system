import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { data } = response
    // 根据后端API结构调整
    if (data.success || data.code === 200) {
      return data.data || data
    } else {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
  },
  (error) => {
    const { status, data } = error.response || {}

    switch (status) {
      case 401:
        // 未授权，清除token并跳转到登录页
        const authStore = useAuthStore()
        authStore.logout()
        if (!window.location.pathname.includes('/login')) {
          window.location.href = '/login'
        }
        ElMessage.error('登录已过期，请重新登录')
        break
      case 403:
        ElMessage.error('没有权限访问该资源')
        break
      case 404:
        ElMessage.error('请求的资源不存在')
        break
      case 500:
        ElMessage.error('服务器内部错误')
        break
      case 502:
        ElMessage.error('网关错误')
        break
      case 503:
        ElMessage.error('服务不可用')
        break
      default:
        if (error.code === 'ECONNABORTED') {
          ElMessage.error('请求超时')
        } else {
          ElMessage.error(data?.message || error.message || '网络错误')
        }
    }

    return Promise.reject(error)
  }
)

export default request
