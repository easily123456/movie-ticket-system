import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

function normalizeAvatar(url) {
  if (!url) return ''
  if (url.startsWith('/')) return API_BASE + url
  return url
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token'))
  const loading = ref(false)

  // 计算属性
  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const currentUser = computed(() => user.value)

  // 登录
  const login = async (credentials) => {
    loading.value = true
    try {
      const response = await authApi.login(credentials)
      if (response.success) {
        const { token: newToken, ...userData } = response.data
        token.value = newToken
        user.value = userData

        // 存储到localStorage
        localStorage.setItem('token', newToken)
        localStorage.setItem('user', JSON.stringify(userData))

        return response
      } else {
        throw new Error(response.message)
      }
    } catch (error) {
      console.error('登录失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 注册
  const register = async (userData) => {
    loading.value = true
    try {
      const response = await authApi.register(userData)
      if (response.success) {
        const { token: newToken, ...userInfo } = response.data
        token.value = newToken
        user.value = userInfo

        // 存储到localStorage
        localStorage.setItem('token', newToken)
        localStorage.setItem('user', JSON.stringify(userInfo))

        return response
      } else {
        throw new Error(response.message)
      }
    } catch (error) {
      console.error('注册失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 登出
  const logout = () => {
    user.value = null
    token.value = null

    // 清除localStorage
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  // 检查用户名是否存在
  const checkUsername = async (username) => {
    try {
      const response = await authApi.checkUsername(username)
      return response.data
    } catch (error) {
      console.error('检查用户名失败:', error)
      throw error
    }
  }

  // 检查邮箱是否存在
  const checkEmail = async (email) => {
    try {
      const response = await authApi.checkEmail(email)
      return response.data
    } catch (error) {
      console.error('检查邮箱失败:', error)
      throw error
    }
  }

  // 刷新token
  const refreshToken = async () => {
    try {
      const response = await authApi.refreshToken()
      if (response.success) {
        const { token: newToken, ...userData } = response.data
        token.value = newToken
        user.value = userData

        // 更新localStorage
        localStorage.setItem('token', newToken)
        localStorage.setItem('user', JSON.stringify(userData))

        return response
      }
    } catch (error) {
      console.error('刷新token失败:', error)
      logout() // 刷新失败则登出
      throw error
    }
  }

  // 初始化用户状态（从localStorage恢复）
  const initAuth = () => {
    const savedToken = localStorage.getItem('token')
    const savedUser = localStorage.getItem('user')

    if (savedToken && savedUser) {
      token.value = savedToken
      try {
        const u = JSON.parse(savedUser)
        if (u && u.avatar) {
          u.avatar = normalizeAvatar(u.avatar)
        }
        user.value = u
      } catch {
        user.value = JSON.parse(savedUser)
      }
    }
  }

  return {
    user,
    token,
    loading,
    isAuthenticated,
    isAdmin,
    currentUser,
    login,
    register,
    logout,
    checkUsername,
    checkEmail,
    refreshToken,
    initAuth
  }
})
