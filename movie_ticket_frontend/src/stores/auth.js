import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('movie_token'))
  const user = ref(JSON.parse(localStorage.getItem('movie_user') || 'null'))

  const isAuthenticated = computed(() => !!token.value)
  const userInfo = computed(() => user.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  const login = (loginData) => {
    token.value = loginData.token
    user.value = loginData.user

    localStorage.setItem('movie_token', loginData.token)
    localStorage.setItem('movie_user', JSON.stringify(loginData.user))
  }

  const logout = () => {
    token.value = null
    user.value = null

    localStorage.removeItem('movie_token')
    localStorage.removeItem('movie_user')
  }

  const updateUser = (userData) => {
    user.value = { ...user.value, ...userData }
    localStorage.setItem('movie_user', JSON.stringify(user.value))
  }

  const updateToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('movie_token', newToken)
  }

  return {
    token,
    user,
    isAuthenticated,
    userInfo,
    isAdmin,
    login,
    logout,
    updateUser,
    updateToken
  }
})
