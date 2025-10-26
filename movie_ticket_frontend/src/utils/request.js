import axios from 'axios'// 导入 axios 库，用于发送 HTTP 请求，是这个请求工具的基础核心库
import { ElMessage } from 'element-plus'// 从 element-plus UI 库中导入 ElMessage 组件，用于显示消息提示框（如错误提示、成功提示等）
import { useAuthStore } from '@/stores/auth'

const request = axios.create({
  baseURL: '/api', // 设置请求的基础URL路径，所有请求都会自动拼接此前缀
  timeout: 15000, // 设置请求超时时间为15000毫秒(15秒)，超过时间会中断请求
  headers: {
    'Content-Type': 'application/json' // 设置请求头的默认Content-Type为application/json，表示发送JSON格式的数据
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {// 检查是否存在token
      config.headers.Authorization = `Bearer ${authStore.token}`
      //Bearer ${authStore.token}赋值给请求的 Authorization 头，以作后端的JWT验证
    }
    return config
  },
  (error) => {
    return Promise.reject(error)//创建一个被拒绝（rejected）的 Promise 对象的方法，内容为错误信息
  }
)


// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { data } = response // 等价于data = response.data
    // 根据后端API结构调整
    if (data.success || data.code === 200) {
      return data.data || data
    } else {
      ElMessage.error(data.message || '请求失败')
      // 调用 Element Plus 的消息组件显示错误提示，优先显示后端返回的错误信息，如果没有则显示默认提示"请求失败"
      return Promise.reject(new Error(data.message || '请求失败'))
      // 创建一个被拒绝（rejected）的 Promise 对象，返回一个包含错误信息的 Promise 对象，用于后续处理
    }
  },
  (error) => {
    const { status, data } = error.response || {}
    //从 error.response 中提取 status（HTTP状态码）和 data（响应数据），如果 error.response 不存在则赋值为空对象以避免报错
    switch (status) {
      case 401:{
        // 未授权，清除token并跳转到登录页
        const authStore = useAuthStore()
        authStore.logout()
        if (!window.location.pathname.includes('/login')) {// 如果当前路径不包含'/login'，则执行跳转
          window.location.href = '/login' // 通过设置 window.location.href 实现页面跳转到登录页，重定向到登录页面
        }
        ElMessage.error('登录已过期，请重新登录')
        break
      }
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
          // 优先显示后端返回的错误信息，若无则显示请求库自带的错误信息，最后兜底显示"网络错误"提示
        }
    }

    return Promise.reject(error)
  }
)

export default request
