import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

// 创建axios实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()

    // 添加token到请求头
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

    // 如果是ApiResponse格式，直接返回data
    if (data && typeof data === 'object' && 'success' in data) {
      return data
    }

    return response
  },
  async (error) => {
    const { response } = error
    const authStore = useAuthStore()

    if (response) {
      const { status, data } = response

      switch (status) {
        case 401:
          // 未授权，清除token并跳转到登录页
          authStore.logout()
          if (!window.location.pathname.includes('/login')) {
            window.location.href = '/login'
          }
          break

        case 403:
          ElMessage.error('权限不足，无法访问该资源')
          break

        case 400:
          // 对于登录接口的400错误，不显示额外的错误消息
          if (response.config.url.includes('/api/auth/login')) {
            // 已经在登录页面显示了具体的错误信息，不需要重复提示
            break;
          }
          // 其他400错误显示具体消息
          if (data && data.message) {
            ElMessage.error(data.message) //弹出后端返回的错误信息

          } else {
            ElMessage.error('网络错误，请稍后重试')
          }
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break

        case 500:
          ElMessage.error('服务器内部错误，请稍后重试')
          break

        default:
          if (data && data.message) {
            ElMessage.error(data.message)
          } else {
            ElMessage.error('网络错误，请稍后重试')
          }
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接1')
    }

    return Promise.reject(error)
  }
)

export default request
