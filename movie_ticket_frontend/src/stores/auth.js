import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
//defineStore是创建状态存储的核心函数，接收 store 名称和配置选项作为参数
//ref用于创建响应式的引用数据,它将普通数据包装成响应式数据，使得数据变化时能触发视图更新
//computed用于创建计算属性，接收一个getter函数，返回一个计算属性对象

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('movie_token'))
  // 从localStorage中获取token数据再包装成响应式引用
  const user = ref(JSON.parse(localStorage.getItem('movie_user') || 'null'))
  // 从localStorage中获取movie_user数据，若无则默认为'null'字符串，再解析成对象并包装成响应式引用

  const isAuthenticated = computed(() => !!token.value)//表示当前用户是否已认证
  //这是计算属性的 getter 函数，token.value 访问响应式引用 token 的当前值，!! 用于将值转换为布尔类型
  const userInfo = computed(() => user.value)//返回当前用户信息的计算属性
  const isAdmin = computed(() => user.value?.role === 'ADMIN')//检查当前用户是否具有管理员权限的计算属性

  const login = (loginData) => {// 接收一个登录数据对象，包含 token 和 user 属性
    token.value = loginData.token; // 将登录数据中的token赋值给响应式引用token
    user.value = loginData.user; // 将登录数据中的用户信息赋值给响应式引用user

    localStorage.setItem('movie_token', loginData.token); // 将token存储到浏览器本地存储中
    localStorage.setItem('movie_user', JSON.stringify(loginData.user)); // 将用户信息序列化后存储到浏览器本地存储中
  }

  const logout = () => {
    token.value = null
    user.value = null

    localStorage.removeItem('movie_token')
    localStorage.removeItem('movie_user')
  }

  const updateUser = (userData) => {
    user.value = { ...user.value, ...userData }
    // 使用展开运算符(...)创建一个新的对象
    // ...user.value: 将原有的用户信息对象的所有属性展开
    // ...userData: 将传入的新用户数据对象的所有属性展开
    // 后面的属性会覆盖前面同名的属性，实现对象的合并更新
    localStorage.setItem('movie_user', JSON.stringify(user.value))
    // 将更新后的用户信息序列化为字符串并保存到浏览器本地存储中
  }

  const updateToken = (newToken) => {
    token.value = newToken // 更新响应式引用 token 的值
    localStorage.setItem('movie_token', newToken) // 将新的 token 保存到浏览器本地存储中
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
