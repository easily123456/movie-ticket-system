import request from '@/utils/request'

export const authApi = {
  // 用户登录
  login(credentials) {
    return request({
      url: '/api/auth/login',
      method: 'post',
      data: credentials
    })
  },

  // 用户注册
  register(userData) {
    return request({
      url: '/api/auth/register',
      method: 'post',
      data: userData
    })
  },

  // 检查用户名是否存在
  checkUsername(username) {
    return request({
      url: '/api/auth/check-username',
      method: 'get',
      params: { username }
    })
  },

  // 检查邮箱是否存在
  checkEmail(email) {
    return request({
      url: '/api/auth/check-email',
      method: 'get',
      params: { email }
    })
  },

  // 刷新token
  refreshToken() {
    return request({
      url: '/api/auth/refresh',
      method: 'post'
    })
  },

  // 登出
  logout() {
    return request({
      url: '/api/auth/logout',
      method: 'post'
    })
  }
}
