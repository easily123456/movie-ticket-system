import request from '@/utils/request'

export const adminApi = {
  // 获取仪表盘统计数据
  getDashboardStats() {
    return request({
      url: '/api/admin/dashboard/stats',
      method: 'get'
    })
  },

  // 获取收入数据
  getRevenueData(range) {
    return request({
      url: `/api/admin/dashboard/revenue`,
      method: 'get',
      params: { range }
    })
  },

  // // 获取订单统计
  // getOrderStats() {
  //   return request({
  //     url: '/api/admin/dashboard/order-stats',
  //     method: 'get'
  //   })
  // },

  // 获取热门电影
  getHotMovies(params) {
    return request({
      url: '/api/admin/dashboard/hot-movies',
      method: 'get',
      params
    })
  },

  // 获取最近订单
  getRecentOrders(params) {
    return request({
      url: '/api/admin/dashboard/recent-orders',
      method: 'get',
      params
    })
  },

    // 用户管理API
  getUsers(params) {
    return request({
      url: '/api/admin/users',
      method: 'get',
      params
    })
  },

  disableUser(userId) {
    return request({
      url: `/api/admin/users/${userId}/status`,
      method: 'put',
      params: { status: false }
    })
  },

  enableUser(userId) {
    return request({
      url: `/api/admin/users/${userId}/status`,
      method: 'put',
      params: { status: true }
    })
  },

  deleteUser(userId) {
    return request({
      url: `/api/admin/users/${userId}`,
      method: 'delete'
    })
  },

  batchDeleteUsers(userIds) {
    return request({
      url: '/api/admin/users/batch',
      method: 'delete',
      data: userIds
    })
  },

  batchDisableUsers(userIds) {
    return request({
      url: '/api/admin/users/batch/disable',
      method: 'put',
      data: userIds
    })
  },

  batchEnableUsers(userIds) {
    return request({
      url: '/api/admin/users/batch/enable',
      method: 'put',
      data: userIds
    })
  },

  getUserStats(userId) {
    return request({
      url: `/api/admin/users/${userId}/stats`,
      method: 'get'
    })
  },

  getUserActivities(userId) {
    return request({
      url: `/api/admin/users/${userId}/activities`,
      method: 'get'
    })
  },

  // 电影类型管理API
  getGenres(params) {
    return request({
      url: '/api/admin/genres',
      method: 'get',
      params
    })
  },

  createGenre(genreData) {
    return request({
      url: '/api/admin/genres',
      method: 'post',
      data: genreData
    })
  },

  updateGenre(id, genreData) {
    return request({
      url: `/api/admin/genres/${id}`,
      method: 'put',
      data: genreData
    })
  },

  disableGenre(genreId) {
    return request({
      url: `/api/admin/genres/${genreId}/status`,
      method: 'put',
      params: { status: false }
    })
  },

  enableGenre(genreId) {
    return request({
      url: `/api/admin/genres/${genreId}/status`,
      method: 'put',
      params: { status: true }
    })
  },

  deleteGenre(genreId) {
    return request({
      url: `/api/admin/genres/${genreId}`,
      method: 'delete'
    })
  },

  batchDeleteGenres(genreIds) {
    return request({
      url: '/api/admin/genres/batch',
      method: 'delete',
      data: genreIds
    })
  },

  batchDisableGenres(genreIds) {
    return request({
      url: '/api/admin/genres/batch/disable',
      method: 'put',
      data: genreIds
    })
  },

  batchEnableGenres(genreIds) {
    return request({
      url: '/api/admin/genres/batch/enable',
      method: 'put',
      data: genreIds
    })
  },

    // 电影管理API
  getMovies(params) {
    return request({
      url: '/api/admin/movies',
      method: 'get',
      params
    })
  },

  createMovie(movieData) {
    return request({
      url: '/api/admin/movies',
      method: 'post',
      data: movieData
    })
  },

  updateMovie(id, movieData) {
    return request({
      url: `/api/admin/movies/${id}`,
      method: 'put',
      data: movieData
    })
  },

  disableMovie(movieId) {
    return request({
      url: `/api/admin/movies/${movieId}/status`,
      method: 'put',
      params: { status: false }
    })
  },

  enableMovie(movieId) {
    return request({
      url: `/api/admin/movies/${movieId}/status`,
      method: 'put',
      params: { status: true }
    })
  },

  setMovieHot(movieId, isHot) {
    return request({
      url: `/api/admin/movies/${movieId}/hot`,
      method: 'put',
      params: { isHot }
    })
  },

  deleteMovie(movieId) {
    return request({
      url: `/api/admin/movies/${movieId}`,
      method: 'delete'
    })
  },

  batchDeleteMovies(movieIds) {
    return request({
      url: '/api/admin/movies/batch',
      method: 'delete',
      data: movieIds
    })
  },

  batchDisableMovies(movieIds) {
    return request({
      url: '/api/admin/movies/batch/disable',
      method: 'put',
      data: movieIds
    })
  },

  batchEnableMovies(movieIds) {
    return request({
      url: '/api/admin/movies/batch/enable',
      method: 'put',
      data: movieIds
    })
  },

  batchSetMoviesHot(movieIds, isHot) {
    return request({
      url: '/api/admin/movies/batch/hot',
      method: 'put',
      data: movieIds,
      params: { isHot }
    })
  },

  getMovieStats(movieId) {
    return request({
      url: `/api/admin/movies/${movieId}/stats`,
      method: 'get'
    })
  },


  // 场次管理API
  getSessions(params) {
    return request({
      url: '/api/admin/sessions',
      method: 'get',
      params
    })
  },

  getHalls() {
    return request({
      url: '/api/admin/halls',
      method: 'get'
    })
  },

  createSession(sessionData) {
    return request({
      url: '/api/admin/sessions',
      method: 'post',
      data: sessionData
    })
  },

  updateSession(id, sessionData) {
    return request({
      url: `/api/admin/sessions/${id}`,
      method: 'put',
      data: sessionData
    })
  },

  cancelSession(sessionId) {
    return request({
      url: `/api/admin/sessions/${sessionId}/status`,
      method: 'put',
      params: { status: false }
    })
  },

  enableSession(sessionId) {
    return request({
      url: `/api/admin/sessions/${sessionId}/status`,
      method: 'put',
      params: { status: true }
    })
  },

  deleteSession(sessionId) {
    return request({
      url: `/api/admin/sessions/${sessionId}`,
      method: 'delete'
    })
  },

  batchDeleteSessions(sessionIds) {
    return request({
      url: '/api/admin/sessions/batch',
      method: 'delete',
      data: sessionIds
    })
  },

  batchCancelSessions(sessionIds) {
    return request({

      url: '/api/admin/sessions/batch/cancel',
      method: 'put',
      data: sessionIds
    })
  },

  batchEnableSessions(sessionIds) {
    return request({
      url: '/api/admin/sessions/batch/enable',
      method: 'put',
      data: sessionIds
    })
  },

  getSessionSeats(sessionId) {
    return request({
      url: `/api/admin/sessions/${sessionId}/seats`,
      method: 'get'
    })
  },

  updateSessionSeats(sessionId, seatData) {
    return request({
      url: `/api/admin/sessions/${sessionId}/seats`,
      method: 'put',
      data: seatData
    })
  },

  getSessionOrders(sessionId) {
    return request({
      url: `/api/admin/sessions/${sessionId}/orders`,
      method: 'get'
    })
  },
    // 订单管理API
  getOrders(params) {
    return request({
      url: '/api/admin/orders',
      method: 'get',
      params
    })
  },
  getOrderDetail(orderId) {
    return request({
      url: `/api/admin/orders/${orderId}`,
      method: 'get'
    })
  },

  updateOrderStatus(orderId, status) {
    return request({
      url: `/api/admin/orders/${orderId}/status`,
      method: 'put',
      params: { status }
    })
  },

  deleteOrder(orderId) {
    return request({
      url: `/api/admin/orders/${orderId}`,
      method: 'delete'
    })
  },

  batchDeleteOrders(orderIds) {
    return request({
      url: '/api/admin/orders/batch',
      method: 'delete',
      data: orderIds
    })
  },

  batchUpdateOrderStatus(orderIds, status) {
    return request({
      url: '/api/admin/orders/batch/status',
      method: 'put',
      data: {
        orderIds,
        status
      }
    })
  },

  exportOrders(searchParams) {
    return request({
      url: '/api/admin/orders/export',
      method: 'post',
      data: searchParams,
      responseType: 'blob'
    })
  },

  getOrderStats(params) {
    return request({
      url: '/api/admin/orders/stats',
      method: 'get',
      params
    })
  },

  
    // 资讯管理API
  getNewsList(params) {
    return request({
      url: '/api/admin/news',
      method: 'get',
      params
    })
  },

  getNewsDetail(newsId) {
    return request({
      url: `/api/admin/news/${newsId}`,
      method: 'get'
    })
  },

  createNews(newsData) {
    return request({
      url: '/api/admin/news',
      method: 'post',
      data: newsData
    })
  },

  updateNews(newsId, newsData) {
    return request({
      url: `/api/admin/news/${newsId}`,
      method: 'put',
      data: newsData
    })
  },

  publishNews(newsId) {
    return request({
      url: `/api/admin/news/${newsId}/publish`,
      method: 'put'
    })
  },

  unpublishNews(newsId) {
    return request({
      url: `/api/admin/news/${newsId}/unpublish`,
      method: 'put'
    })
  },

  setNewsTop(newsId, isTop) {
    return request({
      url: `/api/admin/news/${newsId}/top`,
      method: 'put',
      params: { isTop }
    })
  },

  deleteNews(newsId) {
    return request({
      url: `/api/admin/news/${newsId}`,
      method: 'delete'
    })
  },

  batchDeleteNews(newsIds) {
    return request({
      url: '/api/admin/news/batch',
      method: 'delete',
      data: newsIds
    })
  },

  batchPublishNews(newsIds) {
    return request({
      url: '/api/admin/news/batch/publish',
      method: 'put',
      data: newsIds
    })
  },

  batchSetNewsTop(newsIds, isTop) {
    return request({
      url: '/api/admin/news/batch/top',
      method: 'put',
      data: {
        newsIds,
        isTop
      }
    })
  },

  uploadImage(formData) {
    return request({
      url: '/api/admin/upload',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }

}
