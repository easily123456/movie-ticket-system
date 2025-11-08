import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { sessionApi } from '@/api/index'

export const useSessionStore = defineStore('session', () => {
  const sessions = ref([])
  const currentSession = ref(null)
  const loading = ref(false)

  const sessionList = computed(() => sessions.value)
  const sessionDetail = computed(() => currentSession.value)
  const isLoading = computed(() => loading.value)

  const getSessionsByMovie = async (movieId) => {
    loading.value = true
    try {
      const response = await sessionApi.getSessionsByMovie(movieId)
      // request 接口返回的 ApiResponse 包含 data 字段
      const sessionList = response && response.data ? response.data : response
      sessions.value = sessionList
      return sessionList
    } catch (error) {
      console.error('获取电影场次失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const getSessionDetail = async (id) => {
    loading.value = true
    try {
      const response = await sessionApi.getSessionDetail(id)
      const session = response && response.data ? response.data : response
      currentSession.value = session
      return session
    } catch (error) {
      console.error('获取场次详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const getSessionsByDate = async (date) => {
    loading.value = true
    try {
      const response = await sessionApi.getSessionsByDate(date)
      const sessionList = response && response.data ? response.data : response
      sessions.value = sessionList
      return sessionList
    } catch (error) {
      console.error('获取日期场次失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const clearCurrentSession = () => {
    currentSession.value = null
  }

  return {
    sessions,
    currentSession,
    loading,
    sessionList,
    sessionDetail,
    isLoading,
    getSessionsByMovie,
    getSessionDetail,
    getSessionsByDate,
    clearCurrentSession
  }
})
