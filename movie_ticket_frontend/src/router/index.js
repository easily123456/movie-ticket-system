import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import adminRoutes, { adminRouteGuard } from './admin.js'
import { ElMessage } from 'element-plus'

const routes = [
  // 首页路由
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/front/Home.vue'),
    meta: { title: '首页' }
  },

  // 认证路由
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/front/auth/Login.vue'),
    meta: {
      title: '登录',
      guestOnly: true // 仅游客可访问
    }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/front/auth/Register.vue'),
    meta: {
      title: '注册',
      guestOnly: true
    }
  },

  // 前台公共路由
  {
    path: '/movies',
    name: 'movies',
    component: () => import('@/views/front/movies/MovieList.vue'),
    meta: { title: '电影列表' }
  },
  {
    path: '/movies/:id',
    name: 'movie-detail',
    component: () => import('@/views/front/movies/MovieDetail.vue'),
    meta: { title: '电影详情' }
  },
  {
    path: '/search',
    name: 'MovieSearch',
    component: () => import('@/views/front/movies/MovieSearch.vue')
  },
  {
    path: '/movie/:id/sessions',
    name: 'MovieSessions',
    component: () => import('@/views/front/movies/MovieSessions.vue')
  },

  // 用户中心路由
  {
    // 定义路由访问路径，当用户访问 '/user' 时会匹配此路由
    path: '/user',
    // 为路由定义唯一名称，便于在代码中通过名称进行导航（如 router.push({ name: 'UserCenter' })）
    name: 'UserCenter',
    // 指定该路由对应的组件，使用动态导入（懒加载）方式加载 UserCenter.vue 组件
    component: () => import('@/views/front/user/UserCenter.vue'),
    // 定义路由元信息，这里表示访问此路由需要用户已认证（已在 router.beforeEach 路由守卫中处理）
    meta: { requiresAuth: true },
    // 定义重定向规则，当用户直接访问 '/user' 时，会自动重定向到 '/user/profile' 路径
    redirect: '/user/profile',
    children: [
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/front/user/Profile.vue'),
        meta: {
          title: '个人资料',
          requiresAuth: true
        }
      },
      {
        path: 'orders',
        name: 'OrderList',
        component: () => import('@/views/front/orders/OrderList.vue'),
        meta: {
          title: '我的订单',
          requiresAuth: true
        }
      },
      {
        path: 'comments',
        name: 'UserComments',
        component: () => import('@/views/front/user/Comments.vue'),
        meta: {
          title: '我的评论',
          requiresAuth: true
        }
      },
      {
        path: 'favorites',
        name: 'UserFavorites',
        component: () => import('@/views/front/user/Favorites.vue'),
        meta: {
          title: '我的收藏',
          requiresAuth: true
        }
      }
    ]
  },

  // 订单相关路由
  {
    path: '/booking/:sessionId',
    name: 'booking',
    component: () => import('@/views/front/orders/SeatSelection.vue'),
    meta: {
      title: '选座购票',
      requiresAuth: true // 需要登录
    }
  },
  // {
  //   path: '/session/:sessionId/select-seats',
  //   name: 'SelectSeats',
  //   component: () => import('@/views/front/SelectSeats.vue')
  // },
  {
    path: '/order/create',
    name: 'CreateOrder',
    component: () => import('@/views/front/orders/CreateOrder.vue'),
    meta: {
      title: '创建订单',
      requiresAuth: true
    }
  },
  // {
  //   path: '/orders',
  //   name: 'orders',
  //   component: () => import('@/views/front/orders/OrderList.vue'),
  //   meta: {
  //     title: '我的订单',
  //     requiresAuth: true
  //   }
  // },
  {
    path: '/order/:orderId',
    name: 'OrderDetail',
    component: () => import('@/views/front/orders/OrderDetail.vue'),
    meta: {
      title: '订单详情',
      requiresAuth: true
    }
  },

  // 管理员路由
  ...adminRoutes,

  // 404 页面
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/views/front/NotFound.vue'),
    meta: { title: '页面未找到' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    // 优先：如果有 savedPosition（浏览器前进/后退），返回它以恢复之前的滚动位置
    if (savedPosition) {
      return savedPosition
    }

    // 如果路由包含 hash（锚点），尝试滚动到该元素（若存在）。
    // 返回对象也可以包含 behavior: 'smooth' 以启用平滑滚动。
    if (to.hash) {
      return {
        el: to.hash,
        top: 0,
        behavior: 'smooth'
      }
    }

    // 默认行为：滚动到页面顶部（平滑）
    return { left: 0, top: 0, behavior: 'smooth' }
  }
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()

  // 初始化认证状态
  if (!authStore.isAuthenticated) {
    authStore.initAuth()
  }

  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 电影票务系统`
  }

  // 检查是否需要认证
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
    return
  }

  // 检查是否仅游客可访问（已登录用户不能访问）
  if (to.meta.guestOnly && authStore.isAuthenticated) {
    // 已登录用户尝试访问登录/注册页，重定向到首页
    next('/')
    return
  }

  // 检查管理员权限
  if (to.meta.requiresAdmin && !authStore.isAdmin) {
    next('/')
    return
  }

  // 管理员路由守卫
  adminRouteGuard(to, from, next)
})

// 路由错误处理
router.onError((error) => {
  console.error('路由错误:', error)
  ElMessage.error('页面加载失败，请刷新页面重试')
})

export default router
