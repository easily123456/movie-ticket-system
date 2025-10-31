import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { adminApi } from '@/api'

export const useAdminStore = defineStore('admin', () => {
  const dashboardStats = ref({})
  const loading = ref(false)

  const stats = computed(() => dashboardStats.value)
  const isLoading = computed(() => loading.value)

  // 获取仪表盘统计数据
  const getDashboardStats = async () => {
    loading.value = true
    try {
      const stats = await adminApi.getDashboardStats()
      dashboardStats.value = stats
      return stats
    } catch (error) {
      console.error('获取仪表盘统计失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取收入数据
  const getRevenueData = async (range = 'week') => {
    try {
      const data = await adminApi.getRevenueData(range)
      return data
    } catch (error) {
      console.error('获取收入数据失败:', error)
      throw error
    }
  }

  // // 获取订单统计
  // const getOrderStats = async () => {
  //   try {
  //     const stats = await adminApi.getOrderStats()
  //     return stats
  //   } catch (error) {
  //     console.error('获取订单统计失败:', error)
  //     throw error
  //   }
  // }

  // 获取热门电影
  const getHotMovies = async (params = {}) => {
    try {
      const movies = await adminApi.getHotMovies(params)
      return movies
    } catch (error) {
      console.error('获取热门电影失败:', error)
      throw error
    }
  }

  // 获取最近订单
  const getRecentOrders = async (params = {}) => {
    try {
      const orders = await adminApi.getRecentOrders(params)
      return orders
    } catch (error) {
      console.error('获取最近订单失败:', error)
      throw error
    }
  }

    const getUsers = async (params = {}) => {
    loading.value = true
    try {
      const users = await adminApi.getUsers(params)
      return users
    } catch (error) {
      console.error('获取用户列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const disableUser = async (userId) => {
    try {
      await adminApi.disableUser(userId)
    } catch (error) {
      console.error('禁用用户失败:', error)
      throw error
    }
  }

  const enableUser = async (userId) => {
    try {
      await adminApi.enableUser(userId)
    } catch (error) {
      console.error
('启用用户失败:', error)
      throw error
    }
  }

  const deleteUser = async (userId) => {
    try {
      await adminApi.deleteUser(userId)
    } catch (error) {
      console.error('删除用户失败:', error)
      throw error
    }
  }

  const batchDeleteUsers = async (userIds) => {
    try {
      await adminApi.batchDeleteUsers(userIds)
    } catch (error) {
      console.error('批量删除用户失败:', error)
      throw error
    }
  }

  const batchDisableUsers = async (userIds) => {
    try {
      await adminApi.batchDisableUsers(userIds)
    } catch (error) {
      console.error('批量禁用用户失败:', error)
      throw error
    }
  }

  const batchEnableUsers = async (userIds) => {
    try {
      await adminApi.batchEnableUsers(userIds)
    } catch (error) {
      console.error('批量启用用户失败:', error)
      throw error
    }
  }


    // 电影类型管理相关
  const getGenres = async (params = {}) => {
    loading.value = true
    try {
      const genres = await adminApi.getGenres(params)
      return genres
    } catch (error) {
      console.error('获取类型列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const createGenre = async (genreData) => {
    try {
      const genre = await adminApi.createGenre(genreData)
      return genre
    } catch (error) {
      console.error('创建类型失败:', error)
      throw error
    }
  }

  const updateGenre = async (id, genreData) => {
    try {
      const genre = await adminApi.updateGenre(id, genreData)
      return genre
    } catch (error) {
      console.error('更新类型失败:', error)
      throw error
    }
  }

  const disableGenre = async (genreId) => {
    try {
      await adminApi.disableGenre(genreId)
    } catch (error) {
      console.error('禁用类型失败:', error)
      throw error
    }
  }

  const enableGenre = async (genreId) => {
    try {
      await adminApi.enableGenre(genreId)
    } catch (error) {
      console.error('启用类型失败:', error)
      throw error
    }
  }
  const deleteGenre = async (genreId) => {
    try {
      await adminApi.deleteGenre(genreId)
    } catch (error) {
      console.error('删除类型失败:', error)
      throw error
    }
  }

  const batchDeleteGenres = async (genreIds) => {
    try {
      await adminApi.batchDeleteGenres(genreIds)
    } catch (error) {
      console.error('批量删除类型失败:', error)
      throw error
    }
  }

  const batchDisableGenres = async (genreIds) => {
    try {
      await adminApi.batchDisableGenres(genreIds)
    } catch (error) {
      console.error('批量禁用类型失败:', error)
      throw error
    }
  }

  const batchEnableGenres = async (genreIds) => {
    try {
      await adminApi.batchEnableGenres(genreIds)
    } catch (error) {
      console.error('批量启用类型失败:', error)
      throw error
    }
  }

    // 电影管理相关
  const getMovies = async (params = {}) => {
    loading.value = true
    try {
      const movies = await adminApi.getMovies(params)
      return movies
    } catch (error) {
      console.error('获取电影列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const createMovie = async (movieData) => {
    try {
      const movie = await adminApi.createMovie(movieData)
      return movie
    } catch (error) {
      console.error('创建电影失败:', error)
      throw error
    }
  }

  const updateMovie = async (id, movieData) => {
    try {
      const movie = await adminApi.updateMovie(id, movieData)
      return movie
    } catch (error) {
      console.error('更新电影失败:', error)
      throw error
    }
  }

  const disableMovie = async (movieId) => {
    try {
      await adminApi.disableMovie(movieId)
    } catch (error) {
      console.error('下架电影失败:', error)
      throw error
    }
  }

  const enableMovie = async (movieId) => {
    try {
      await adminApi.enableMovie(movieId)
    } catch (error) {
      console.error('上架电影失败:', error)
      throw error
    }
  }

  const setMovieHot = async (movieId, isHot) => {
    try {
      await adminApi.setMovieHot(movieId, isHot)
    } catch (error) {
      console.error('设置热门失败:', error)
      throw error
    }
  }

  const deleteMovie = async (movieId) => {
    try {
      await adminApi.deleteMovie(movieId)
    } catch (error) {
      console.error('删除电影失败:', error)
      throw error
    }
  }

  const batchDeleteMovies = async (movieIds) => {
    try {
      await adminApi.batchDeleteMovies(movieIds)
    } catch (error) {
      console.error('批量删除电影失败:', error)
      throw error
    }
  }

  const batchDisableMovies = async (movieIds) => {
    try {
      await adminApi.batchDisableMovies(movieIds)
    } catch (error) {
      console.error('批量下架电影失败:', error)
      throw error
    }
  }

  const batchEnableMovies = async (movieIds) => {
    try {
      await adminApi.batchEnableMovies(movieIds)
    } catch (error) {
      console.error('批量上架电影失败:', error)
      throw error
    }
  }

  const batchSetMoviesHot = async (movieIds, isHot) => {
    try {
      await adminApi.batchSetMoviesHot(movieIds, isHot)
    } catch (error) {
      console.error('批量设置热门失败:', error)
      throw error
    }
  }

    // 场次管理相关
  const getSessions = async (params = {}) => {
    loading.value = true
    try {
      const sessions = await adminApi.getSessions(params)
      return sessions
    } catch (error) {
      console.error('获取场次列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const getHalls = async () => {
    try {
      const halls = await adminApi.getHalls()
      return halls
    } catch (error) {
      console.error('获取放映厅列表失败:', error)
      throw error
    }
  }

  const createSession = async (sessionData) => {
    try {
      const session = await adminApi.createSession(sessionData)
      return session
    } catch (error) {
      console.error('创建场次失败:', error)
      throw error
    }
  }

  const updateSession = async (id, sessionData) => {
    try {
      const session = await adminApi.updateSession(id, sessionData)
      return session
    } catch (error) {
      console.error('更新场次失败:', error)
      throw error
    }
  }

  const cancelSession = async (sessionId) => {
    try {
      await adminApi.cancelSession(sessionId)
    } catch (error) {
      console.error('取消场次失败:', error)
      throw error
    }
  }

  const enableSession = async (sessionId) => {
    try {
      await adminApi.enableSession(sessionId)
    } catch (error) {
      console.error('启用场次失败:', error)
      throw error
    }
  }

  const deleteSession = async (sessionId) => {
    try {
      await adminApi.deleteSession(sessionId)
    } catch (error) {
      console.error('删除场次失败:', error)
      throw error
    }
  }

  const batchDeleteSessions = async (sessionIds) => {
    try {
      await adminApi.batchDeleteSessions(sessionIds)
    } catch (error) {
      console.error('批量删除场次失败:', error)
      throw error
    }
  }

  const batchCancelSessions = async (sessionIds) => {
    try {
      await adminApi.batchCancelSessions(sessionIds)
    } catch (error) {
      console.error('批量取消场次失败:', error)
      throw error
    }
  }

  const batchEnableSessions = async (sessionIds) => {
    try {
      await adminApi.batchEnableSessions(sessionIds)
    } catch (error) {
      console.error('批量启用场次失败:', error)
      throw error
    }
  }

  // 订单管理相关方法
  const getOrders = async (params = {}) => {
    loading.value = true
    try {
      const orders = await adminApi.getOrders(params)
      return orders
    } catch (error) {
      console.error('获取订单列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const getOrderDetail = async (orderId) => {
    try {
      const order = await adminApi.getOrderDetail(orderId)
      return order
    } catch (error) {
      console.error('获取订单详情失败:', error)
      throw error
    }
  }

  const updateOrderStatus = async (orderId, status) => {
    try {
      const order = await adminApi.updateOrderStatus(orderId, status)
      return order
    } catch (error) {
      console.error('更新订单状态失败:', error)
      throw error
    }
  }

  const deleteOrder = async (orderId) => {
    try {
      await adminApi.deleteOrder(orderId)
    } catch (error) {
      console.error('删除订单失败:', error)
      throw error
    }
  }

  const batchDeleteOrders = async (orderIds) => {
    try {
      await adminApi.batchDeleteOrders(orderIds)
    } catch (error) {
      console.error('批量删除订单失败:', error)
      throw error
    }
  }

  const batchUpdateOrderStatus = async (orderIds, status) => {
    try {
      await adminApi.batchUpdateOrderStatus(orderIds, status)
    } catch (error) {
      console.error('批量更新订单状态失败:', error)
      throw error
    }
  }

  const exportOrders = async (searchParams) => {
    try {
      const response = await adminApi.exportOrders(searchParams)
      return response
    } catch (error) {
      console.error('导出订单失败:', error)
      throw error
    }
  }

  const getOrderStats = async (params = {}) => {
    try {
      const stats = await adminApi.getOrderStats(params)
      return stats
    } catch (error) {
      console.error('获取订单统计失败:', error)
      throw error
    }
  }


    // 资讯管理相关方法
  const getNewsList = async (params = {}) => {
    loading.value = true
    try {
      const news = await adminApi.getNewsList(params)
      return news
    } catch (error) {
      console.error('获取资讯列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const getNewsDetail = async (newsId) => {
    try {
      const news = await adminApi.getNewsDetail(newsId)
      return news
    } catch (error) {
      console.error('获取资讯详情失败:', error)
      throw error
    }
  }

  const createNews = async (newsData) => {
    try {
      const news = await adminApi.createNews(newsData)
      return news
    } catch (error) {
      console.error('创建资讯失败:', error)
      throw error
    }
  }

  const updateNews = async (newsId, newsData) => {
    try {
      const news = await adminApi.updateNews(newsId, newsData)
      return news
    } catch (error) {
      console.error('更新资讯失败:', error)
      throw error
    }
  }

  const publishNews = async (newsId) => {
    try {
      const news = await adminApi.publishNews(newsId)
      return news
    } catch (error) {
      console.error('发布资讯失败:', error)
      throw error
    }
  }

  const unpublishNews = async (newsId) => {
    try {
      const news = await adminApi.unpublishNews(newsId)
      return news
    } catch (error) {
      console.error('取消发布资讯失败:', error)
      throw error
    }
  }

  const setNewsTop = async (newsId, isTop) => {
    try {
      const news = await adminApi.setNewsTop(newsId, isTop)
      return news
    } catch (error) {
      console.error('设置置顶失败:', error)
      throw error
    }
  }

  const deleteNews = async (newsId) => {
    try {
      await adminApi.deleteNews(newsId)
    } catch (error) {
      console.error('删除资讯失败:', error)
      throw error
    }
  }

  const batchDeleteNews = async (newsIds) => {
    try {
      await adminApi.batchDeleteNews(newsIds)
    } catch (error) {
      console.error('批量删除资讯失败:', error)
      throw error
    }
  }

  const batchPublishNews = async (newsIds) => {
    try {
      await adminApi.batchPublishNews(newsIds)
    } catch (error) {
      console.error('批量发布资讯失败:', error)
      throw error
    }
  }

  const batchSetNewsTop = async (newsIds, isTop) => {
    try {
      await adminApi.batchSetNewsTop(newsIds, isTop)
    } catch (error) {
      console.error('批量设置置顶失败:', error)
      throw error
    }
  }

  const uploadImage = async (file) => {
    try {
      const formData = new FormData()
      formData.append('file', file)
      const response = await adminApi.uploadImage(formData)
      return response
    } catch (error) {
      console.error('上传图片失败:', error)
      throw error
    }
  }

  return {
    dashboardStats,
    loading,
    stats,
    isLoading,
    getDashboardStats,
    getRevenueData,
    getHotMovies,
    getRecentOrders,
    getUsers,
    disableUser,
    enableUser,
    deleteUser,
    batchDeleteUsers,
    batchDisableUsers,
    batchEnableUsers,
    getGenres,
    createGenre,
    updateGenre,
    disableGenre,
    enableGenre,
    deleteGenre,
    batchDeleteGenres,
    batchDisableGenres,
    batchEnableGenres,
    getMovies,
    createMovie,
    updateMovie,
    disableMovie,
    enableMovie,
    setMovieHot,
    deleteMovie,
    batchDeleteMovies,
    batchDisableMovies,
    batchEnableMovies,
    batchSetMoviesHot,
    getSessions,
    getHalls,
    createSession,
    updateSession,
    cancelSession,
    enableSession,
    deleteSession,
    batchDeleteSessions,
    batchCancelSessions,
    batchEnableSessions,
    getOrders,
    getOrderDetail,
    updateOrderStatus,
    deleteOrder,
    batchDeleteOrders,
    batchUpdateOrderStatus,
    exportOrders,
    getOrderStats,
    getNewsList,
    getNewsDetail,
    createNews,
    updateNews,
    publishNews,
    unpublishNews,
    setNewsTop,
    deleteNews,
    batchDeleteNews,
    batchPublishNews,
    batchSetNewsTop,
    uploadImage
  }
})
