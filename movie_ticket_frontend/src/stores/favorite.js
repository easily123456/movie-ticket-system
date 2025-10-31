import { defineStore } from 'pinia'
import { ref } from 'vue'
import { favoriteApi } from '@/api'

export const useFavoriteStore = defineStore('favorite', () => {
  const favorites = ref([])
  const hotFavorites = ref([])
  const loading = ref(false)

  // 分页信息
  const pagination = ref({
    page: 1,
    size: 10,
    total: 0,
    totalPages: 0
  })

  // 添加收藏
  const addFavorite = async (movieId) => {
    try {
      await favoriteApi.addFavorite(movieId)
      return true
    } catch (error) {
      const errorMessage = error.response?.data?.message || '收藏失败'
      throw new Error(errorMessage)
    }
  }

  // 取消收藏
  const removeFavorite = async (movieId) => {
    try {
      await favoriteApi.removeFavorite(movieId)
      return true
    } catch (error) {
      const errorMessage = error.response?.data?.message || '取消收藏失败'
      throw new Error(errorMessage)
    }
  }

  // 获取用户收藏
  const getFavorites = async (params = {}) => {
    loading.value = true
    try {
      const response = await favoriteApi.getFavorites({
        ...params,
        page: pagination.value.page - 1,
        size: pagination.value.size
      })
      favorites.value = response.data.content || response.data
      pagination.value = {
        page: response.data.number + 1 || 1,
        size: response.data.size || pagination.value.size,
        total: response.data.totalElements || response.data.length || 0,
        totalPages: response.data.totalPages || 1
      }
      return response
    } catch (error) {
      console.error('获取收藏列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 检查收藏状态
  const checkFavorite = async (movieId) => {
    try {
      const response = await favoriteApi.checkFavorite(movieId)
      return response.data
    } catch (error) {
      console.error('检查收藏状态失败:', error)
      return false
    }
  }

  // 获取收藏数量
  const getFavoriteCount = async (movieId) => {
    try {
      const response = await favoriteApi.getFavoriteCount(movieId)
      return response.data
    } catch (error) {
      console.error('获取收藏数量失败:', error)
      return 0
    }
  }

  // 获取用户收藏数量
  const getUserFavoriteCount = async () => {
    try {
      const response = await favoriteApi.getUserFavoriteCount()
      return response.data
    } catch (error) {
      console.error('获取用户收藏数量失败:', error)
      return 0
    }
  }

  // 获取热门收藏
  const getHotFavorites = async (limit = 10) => {
    try {
      const response = await favoriteApi.getHotFavorites(limit)
      hotFavorites.value = response.data
      return response
    } catch (error) {
      console.error('获取热门收藏失败:', error)
      throw error
    }
  }

  // 加载更多
  const loadMore = async () => {
    if (pagination.value.page >= pagination.value.totalPages) return

    pagination.value.page += 1
    try {
      const response = await favoriteApi.getFavorites({
        page: pagination.value.page - 1,
        size: pagination.value.size
      })
      const newFavorites = response.data.content || response.data
      favorites.value = [...favorites.value, ...newFavorites]
    } catch (error) {
      console.error('加载更多收藏失败:', error)
      pagination.value.page -= 1
      throw error
    }
  }

  return {
    // 状态
    favorites,
    hotFavorites,
    loading,
    pagination,

    // 方法
    addFavorite,
    removeFavorite,
    getFavorites,
    checkFavorite,
    getFavoriteCount,
    getUserFavoriteCount,
    getHotFavorites,
    loadMore
  }
})