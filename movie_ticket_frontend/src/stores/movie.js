import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { movieApi } from '@/api'

export const useMovieStore = defineStore('movie', () => {
  const movies = ref([])
  const hotMovies = ref([])
  const newMovies = ref([])
  const topRatedMovies = ref([])
  const currentMovie = ref(null)
  const loading = ref(false)
  const searchLoading = ref(false)

  // 分页信息
  const pagination = ref({
    page: 1,
    size: 12,
    total: 0,
    totalPages: 0
  })

  // 筛选条件
  const filters = ref({
    keyword: '',
    genreId: null,
    isHot: null,
    releaseDateStart: null,
    releaseDateEnd: null,
    minRating: null,
    maxRating: null,
    sortBy: 'createTime',
    sortOrder: 'desc'
  })

  // 计算属性
  const hasMore = computed(() => {
    return pagination.value.page < pagination.value.totalPages
  })

  const isEmpty = computed(() => {
    return movies.value.length === 0 && !loading.value
  })

  // 获取电影列表
  const getMovies = async (params = {}) => {
    loading.value = true
    try {
      const queryParams = {
        ...filters.value,
        ...params,
        page: pagination.value.page - 1,
        size: pagination.value.size
      }

      // 清理空值参数
      Object.keys(queryParams).forEach(key => {
        if (queryParams[key] === null || queryParams[key] === '') {
          delete queryParams[key]
        }
      })

      const response = await movieApi.getMovies(queryParams)
      movies.value = response.data.content || response.data
      pagination.value = {
        page: response.data.number + 1 || 1,
        size: response.data.size || pagination.value.size,
        total: response.data.totalElements || response.data.length || 0,
        totalPages: response.data.totalPages || 1
      }

      return response
    } catch (error) {
      console.error('获取电影列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取热门电影
  const getHotMovies = async (limit = 8) => {
    try {
      const response = await movieApi.getHotMovies(limit)
      hotMovies.value = response.data
      return response
    } catch (error) {
      console.error('获取热门电影失败:', error)
      throw error
    }
  }

  // 获取最新电影
  const getNewMovies = async (limit = 8) => {
    try {
      const response = await movieApi.getNewMovies(limit)
      newMovies.value = response.data
      return response
    } catch (error) {
      console.error('获取最新电影失败:', error)
      throw error
    }
  }

  // 获取高分电影
  const getTopRatedMovies = async (limit = 8) => {
    try {
      const response = await movieApi.getTopRatedMovies(limit)
      topRatedMovies.value = response.data
      return response
    } catch (error) {
      console.error('获取高分电影失败:', error)
      throw error
    }
  }

  // 搜索电影
  const searchMovies = async (keyword, page = 0, size = 12) => {
    searchLoading.value = true
    try {
      const response = await movieApi.searchMovies(keyword, page, size)
      return response
    } catch (error) {
      console.error('搜索电影失败:', error)
      throw error
    } finally {
      searchLoading.value = false
    }
  }

  // 根据类型获取电影
  const getMoviesByGenre = async (genreId, page = 0, size = 12) => {
    loading.value = true
    try {
      const response = await movieApi.getMoviesByGenre(genreId, page, size)
      movies.value = response.data.content || response.data
      pagination.value = {
        page: response.data.number + 1 || 1,
        size: response.data.size || size,
        total: response.data.totalElements || response.data.length || 0,
        totalPages: response.data.totalPages || 1
      }
      return response
    } catch (error) {
      console.error('获取类型电影失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取电影详情
  const getMovieDetail = async (movieId) => {
    try {
      const response = await movieApi.getMovieDetail(movieId)
      currentMovie.value = response.data
      return response
    } catch (error) {
      console.error('获取电影详情失败:', error)
      throw error
    }
  }

  // 获取电影类型
  const getMovieGenres = async () => {
    try {
      const response = await movieApi.getMovieGenres()
      return response.data
    } catch (error) {
      console.error('获取电影类型失败:', error)
      throw error
    }
  }

  // 重置筛选条件
  const resetFilters = () => {
    filters.value = {
      keyword: '',
      genreId: null,
      isHot: null,
      releaseDateStart: null,
      releaseDateEnd: null,
      minRating: null,
      maxRating: null,
      sortBy: 'createTime',
      sortOrder: 'desc'
    }
  }

  // 更新筛选条件
  const updateFilters = (newFilters) => {
    filters.value = { ...filters.value, ...newFilters }
    pagination.value.page = 1 // 重置到第一页
  }

  // 加载更多
  const loadMore = async () => {
    if (!hasMore.value || loading.value) return

    pagination.value.page += 1
    try {
      const queryParams = {
        ...filters.value,
        page: pagination.value.page - 1,
        size: pagination.value.size
      }

      // 清理空值参数
      Object.keys(queryParams).forEach(key => {
        if (queryParams[key] === null || queryParams[key] === '') {
          delete queryParams[key]
        }
      })

      const response = await movieApi.getMovies(queryParams)
      const newMovies = response.data.content || response.data
      movies.value = [...movies.value, ...newMovies]

      pagination.value.totalPages = response.data.totalPages || 1
    } catch (error) {
      console.error('加载更多电影失败:', error)
      pagination.value.page -= 1 // 回滚页码
      throw error
    }
  }

  return {
    // 状态
    movies,
    hotMovies,
    newMovies,
    topRatedMovies,
    currentMovie,
    loading,
    searchLoading,
    pagination,
    filters,

    // 计算属性
    hasMore,
    isEmpty,

    // 方法
    getMovies,
    getHotMovies,
    getNewMovies,
    getTopRatedMovies,
    searchMovies,
    getMoviesByGenre,
    getMovieDetail,
    getMovieGenres,
    resetFilters,
    updateFilters,
    loadMore
  }
})
