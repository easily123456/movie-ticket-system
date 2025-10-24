import request from '@/utils/request'

// 认证相关API
export const authApi = {
  login(data) {
    return request.post('/auth/login', data)
  },
  register(data) {
    return request.post('/auth/register', data)
  },
  checkUsername(username) {
    return request.get('/auth/check-username', { params: { username } })
  },
  checkEmail(email) {
    return request.get('/auth/check-email', { params: { email } })
  }
}

// 电影相关API
export const movieApi = {
  getMovies(params = {}) {
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
      params: { keyword, ...params }
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
  auth: authApi,
  movie: movieApi,
  session: sessionApi,
  user: userApi
}
