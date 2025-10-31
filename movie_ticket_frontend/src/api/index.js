import request from '@/utils/request'

// 认证相关API
export const authApi = {
  login(data) {
    return request.post('/auth/login', data)// 发送POST请求到'/auth/login'端点，data为请求体
  },
  register(data) {
    return request.post('/auth/register', data)
  },
  checkUsername(username) {
    return request.get('/auth/check-username', { params: { username } })
    // 发送GET请求到'/auth/check-username'端点，携带查询参数username
    // params选项用于指定查询参数，会被序列化并附加到URL中，如：/auth/check-username?username=someUsername
  },
  checkEmail(email) {
    return request.get('/auth/check-email', { params: { email } })
  },
  logout() {
    return request.post('/auth/logout')
  }
}

// 用户相关API
export const userApi = {
  getProfile() {
    return request.get('/user/profile')
  },
  updateProfile(data) {
    return request.put('/user/profile', data)
  },
  changePassword(data) {
    return request.post('/user/change-password', data)
  },
  getOrders(params = {}) {
    return request.get('/orders/user', { params })
  },
  getFavorites(params = {}) {
    return request.get('/favorites', { params })
  },
  getComments(params = {}) {
    return request.get('/comments/user', { params })
  }
}

// 电影相关API
export const movieApi = {
  // 获取电影列表（带筛选）
  getMovies(params) {
    return request.get('/api/movies', { params })
  },

  // 获取热门电影
  getHotMovies(limit = 8) {
    return request.get(`/api/movies/hot?limit=${limit}`)
  },

  // 获取最新电影
  getNewMovies(limit = 8) {
    return request.get(`/api/movies/new?limit=${limit}`)
  },

  // 获取高分电影
  getTopRatedMovies(limit = 8) {
    return request.get(`/api/movies/top-rated?limit=${limit}`)
  },

  // 搜索电影
  searchMovies(keyword, page = 0, size = 12) {
    return request.get(`/api/movies/search?keyword=${keyword}&page=${page}&size=${size}`)
  },

  // 根据类型获取电影
  getMoviesByGenre(genreId, page = 0, size = 12) {
    return request.get(`/api/movies/genre/${genreId}?page=${page}&size=${size}`)
  },

  // 获取电影详情
  getMovieDetail(movieId) {
    return request.get(`/api/movies/${movieId}`)
  },

  // 获取电影类型
  getMovieGenres() {
    return request.get('/api/movies/genres')
  },

  // 获取电影场次
  getMovieSessions(movieId) {
    return request.get(`/api/sessions/movie/${movieId}`)
  }
}

// 场次相关API
export const sessionApi = {
  getSessionsByMovie(movieId) {
    return request.get(`/sessions/movie/${movieId}`)
  },
    // 获取场次详情（包含座位信息）
  getSessionDetail(sessionId) {
    return request.get(`/api/sessions/${sessionId}/detail`)
  },

  // 检查座位可用性
  checkSeatAvailability(sessionId, seatNumbers) {
    return request.post(`/api/sessions/${sessionId}/check-seats`, seatNumbers)
  },

  // 获取电影场次
  getMovieSessions(movieId) {
    return request.get(`/api/movie_sessions/movie/${movieId}`)
  },

  // 获取日期场次
  getSessionsByDate(date) {
    return request.get(`/api/sessions/date/${date}`)
  }
}

//订单相关API
export const orderApi = {
  createOrder(data) {
    return request.post('/orders', data)
  },
  getOrderStats() {
    return request.get('/orders/stats')
  },
    // 创建选座订单
  createSeatOrder(data) {
    return request.post('/api/orders/seat', data)
  },

  // 支付订单
  payOrder(orderId) {
    return request.post(`/api/orders/${orderId}/pay`)
  },

  // 取消订单
  cancelOrder(orderId) {
    return request.post(`/api/orders/${orderId}/cancel`)
  },

  // 获取订单详情
  getOrderDetail(orderId) {
    return request.get(`/api/orders/${orderId}`)
  },

  // 获取用户订单
  getUserOrders(params) {
    return request.get('/api/orders/user', { params })
  }
}

// 评论相关API
export const commentApi = {
  updateComment(id, data) {
    return request.put(`/comments/${id}`, data)
  },
    // 获取评论列表
  getComments(params) {
    return request.get('/api/comments', { params })
  },

  // 创建评论
  createComment(data) {
    return request.post('/api/comments', data)
  },

  // 获取电影评论
  getMovieComments(movieId, params) {
    return request.get(`/api/comments/movie/${movieId}`, { params })
  },

  // 获取用户评论
  getUserComments(params) {
    return request.get('/api/comments/user', { params })
  },

  // 获取评论统计
  getCommentStats(movieId) {
    return request.get(`/api/comments/movie/${movieId}/stats`)
  },

  // 获取热门评论
  getHotComments(movieId, limit = 5) {
    return request.get(`/api/comments/movie/${movieId}/hot?limit=${limit}`)
  },

  // 获取最新评论
  getLatestComments(movieId, limit = 5) {
    return request.get(`/api/comments/movie/${movieId}/latest?limit=${limit}`)
  },

  // 点赞评论
  likeComment(commentId) {
    return request.post(`/api/comments/${commentId}/like`)
  },

  // 取消点赞评论
  unlikeComment(commentId) {
    return request.post(`/api/comments/${commentId}/unlike`)
  },

  // 删除评论
  deleteComment(commentId) {
    return request.delete(`/api/comments/${commentId}`)
  }
}

// 收藏相关API
export const favoriteApi = {
  // 添加收藏
  addFavorite(movieId) {
    return request.post(`/api/favorites/movie/${movieId}`)
  },

  // 取消收藏
  removeFavorite(movieId) {
    return request.delete(`/api/favorites/movie/${movieId}`)
  },

  // 获取用户收藏
  getFavorites(params) {
    return request.get('/api/favorites', { params })
  },

  // 检查收藏状态
  checkFavorite(movieId) {
    return request.get(`/api/favorites/check/movie/${movieId}`)
  },

  // 获取收藏数量
  getFavoriteCount(movieId) {
    return request.get(`/api/favorites/count/movie/${movieId}`)
  },

  // 获取用户收藏数量
  getUserFavoriteCount() {
    return request.get('/api/favorites/count/user')
  },

  // 获取热门收藏
  getHotFavorites(limit = 10) {
    return request.get(`/api/favorites/hot?limit=${limit}`)
  }
}

// 资讯相关API
export const newsApi = {
  getNewsList(params = {}) {
    return request.get('/news', { params })
  },
  getNewsDetail(id) {
    return request.get(`/news/${id}`)
  },
  getTopNews() {
    return request.get('/news/top')
  }
}

// 类型相关API
export const genreApi = {
  getGenres() {
    return request.get('/genres')
  }
}

// 管理员API
export const adminApi = {
  // 用户管理
  getUsers(params = {}) {
    return request.get('/admin/users', { params })
  },
  updateUserStatus(id, status) {
    return request.put(`/admin/users/${id}/status`, null, {
      params: { status }
    })
  },
  deleteUser(id) {
    return request.delete(`/admin/users/${id}`)
  },

  // 电影管理
  createMovie(data) {
    return request.post('/admin/movies', data)
  },
  updateMovie(id, data) {
    return request.put(`/admin/movies/${id}`, data)
  },
  updateMovieStatus(id, status) {
    return request.put(`/admin/movies/${id}/status`, null, {
      params: { status }
    })
  },
  toggleHotMovie(id, isHot) {
    return request.put(`/admin/movies/${id}/hot`, null, {
      params: { isHot }
    })
  },

  // 场次管理
  createSession(data) {
    return request.post('/admin/sessions', data)
  },
  updateSession(id, data) {
    return request.put(`/admin/sessions/${id}`, data)
  },
  deleteSession(id) {
    return request.delete(`/admin/sessions/${id}`)
  },
  // 订单管理
  getAdminOrders(params = {}) {
    return request.get('/admin/orders', { params })
  },
  // 资讯管理
  createNews(data) {
    return request.post('/admin/news', data)
  },
  updateNews(id, data) {
    return request.put(`/admin/news/${id}`, data)
  },
  updateNewsStatus(id, status) {
    return request.put(`/admin/news/${id}/status`, null, {
      params: { status }
    })
  },
  toggleTopNews(id, isTop) {
    return request.put(`/admin/news/${id}/top`, null, {
      params: { isTop }
    })
  },

  // 仪表盘
  getDashboardStats() {
    return request.get('/admin/dashboard/stats')
  }
}

export default {
  auth: authApi,     // 将认证相关API集合映射为auth属性，可通过api.auth访问认证接口
  movie: movieApi,   // 将电影相关API集合映射为movie属性，可通过api.movie访问电影接口
  session: sessionApi, // 将场次相关API集合映射为session属性，可通过api.session访问场次接口
  user: userApi,     // 将用户相关API集合映射为user属性，可通过api.user访问用户接口
  comment: commentApi, // 将评论相关API集合映射为comment属性，可通过api.comment访问评论接口
  favorite: favoriteApi, // 将收藏相关API集合映射为favorite属性，可通过api.favorite访问收藏接口
  news: newsApi,       // 将资讯相关API集合映射为news属性，可通过api.news访问资讯接口
  genre: genreApi,     // 将类型相关API集合映射为genre属性，可通过api.genre访问类型接口
  admin: adminApi,      // 将管理员API集合映射为admin属性，可通过api.admin访问管理员接口
  order: orderApi       // 将订单相关API集合映射为order属性，可通过api.order访问订单接口
}
