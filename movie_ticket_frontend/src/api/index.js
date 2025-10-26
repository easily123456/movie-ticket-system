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
  }
}

// 电影相关API
export const movieApi = {
  getMovies(params = {}) {// 默认参数params为空对象
    return request.get('/movies', { params })
  },
  getHotMovies() {
    return request.get('/movies/hot')
  },
  getUpcomingMovies() {
    return request.get('/movies/upcoming')
  },
  getMovieDetail(id) {
    return request.get(`/movies/${id}`)
  },
  searchMovies(keyword, params = {}) {
    return request.get('/movies/search', {
      params: { keyword, ...params }// 将keyword和其他查询参数合并为一个对象，并作为params选项的值
    })
  },
  getMoviesByGenre(genreId, params = {}) {
    return request.get(`/movies/genre/${genreId}`, { params })
  }
}

// 场次相关API
export const sessionApi = {
  getSessionsByMovie(movieId) {
    return request.get(`/sessions/movie/${movieId}`)
  },
  getSessionDetail(id) {
    return request.get(`/sessions/${id}`)
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
  }
}

export default {
  auth: authApi,     // 将认证相关API集合映射为auth属性，可通过api.auth访问认证接口
  movie: movieApi,   // 将电影相关API集合映射为movie属性，可通过api.movie访问电影接口
  session: sessionApi, // 将场次相关API集合映射为session属性，可通过api.session访问场次接口
  user: userApi      // 将用户相关API集合映射为user属性，可通过api.user访问用户接口
}
