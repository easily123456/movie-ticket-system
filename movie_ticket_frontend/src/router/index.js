import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/front/Home.vue'),
    meta: {
      title: '星光影城 - 在线电影票务平台',
      keepAlive: true,//用于启用 Vue 的 <keep-alive> 组件缓存机制
    },
  },
  {
    path: '/movies',
    name: 'MovieList',
    component: () => import('@/views/front/movies/MovieList.vue'),
    meta: {
      title: '电影列表 - 星光影城',
      keepAlive: true,
    },
  },
  {
    path: '/movie/:id',
    name: 'MovieDetail',
    component: () => import('@/views/front/movies/MovieDetail.vue'),
    meta: {
      title: '电影详情 - 星光影城',
    },
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/front/auth/Login.vue'),
    meta: {
      title: '用户登录 - 星光影城',
      requiresGuest: true,
    },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/front/auth/Register.vue'),
    meta: {
      title: '用户注册 - 星光影城',
      requiresGuest: true,
    },
  },
  {
    path: '/search',
    name: 'MovieSearch',
    component: () => import('@/views/front/movies/MovieSearch.vue'),
    meta: {
      title: '电影搜索 - 星光影城',
    },
  },
  {
    path: '/:pathMatch(.*)*', // 匹配所有未定义的路由路径，使用 pathMatch 捕获任意路径
    name: 'NotFound', // 路由名称为 NotFound
    component: () => import('@/views/front/NotFound.vue'), // 动态导入 404 页面组件
    meta: {
      // 路由元信息配置
      title: '页面未找到 - 星光影城', // 设置页面标题
    },
  },
]

const router = createRouter({
  // 创建一个新的Vue Router实例
  history: createWebHistory(), // 配置路由历史模式为HTML5 History模式，使用真实的URL路径
  routes, // 注入之前定义的路由配置数组routes
  scrollBehavior(to, from, savedPosition) {
    // 定义路由切换时的滚动行为处理函数
    //to - 要进入的目标路由对象
    //from - 当前正在离开的路由对象
    //savedPosition - 如果存在，则表示用户在当前页面滚动了
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0, behavior: 'smooth' }
      //top: 0 - 滚动到页面顶部
      //behavior: 'smooth' - 使用平滑滚动效果
    }
  },
})

// 路由守卫主要作用是在每次路由切换时自动更新页面标题
router.beforeEach((to, from, next) => {
  // 注册全局前置路由守卫，在每次路由跳转前执行
  // 设置页面标题
  if (to.meta.title) {
    // 检查目标路由的元信息中是否包含标题配置
    document.title = to.meta.title // 如果有标题配置，则设置浏览器标签页标题为该路由的标题
  }
  next() // 调用next函数，放行路由跳转，允许导航继续进行
})

export default router
// 导出路由实例，供其他模块使用
// 允许其他文件通过 import router from './router/index.ts' 的方式导入这个路由器实例
