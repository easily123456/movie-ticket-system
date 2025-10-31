import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/front/Home.vue'),
    meta: { title: '首页' }
  },
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
    path: '/movie/:id/sessions',
    name: 'MovieSessions',
    component: () => import('@/views/front/movies/MovieSessions.vue')
  },
  {
    path: '/search',
    name: 'MovieSearch',
    component: () => import('@/views/front/movies/MovieSearch.vue')
  },
  {
    path: '/booking/:sessionId',
    name: 'booking',
    component: () => import('@/views/front/orders/SelectSeats.vue'),
    meta: {
      title: '选座购票',
      requiresAuth: true // 需要登录
    }
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('@/views/front/user/Profile.vue'),
    meta: {
      title: '个人中心',
      requiresAuth: true
    }
  },
  {
    path: '/orders',
    name: 'orders',
    component: () => import('@/views/front/orders/OrderList.vue'),
    meta: {
      title: '我的订单',
      requiresAuth: true
    }
  },
  // {
  //   path: '/movie/:id/sessions',
  //   name: 'MovieSessions',
  //   component: () => import('@/views/front/MovieSessions.vue')
  // },
  {
    path: '/session/:sessionId/select-seats',
    name: 'SelectSeats',
    component: () => import('@/views/front/SelectSeats.vue')
  },
  {
    path: '/order/create',
    name: 'CreateOrder',
    component: () => import('@/views/front/CreateOrder.vue')
  },
  {
    path: '/order/:orderId',
    name: 'OrderDetail',
    component: () => import('@/views/front/OrderDetail.vue')
  },
  
  // 用户相关路由
  {
    path: '/user',
    name: 'UserCenter',
    component: () => import('@/views/front/UserCenter.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/comments',
    name: 'UserComments',
    component: () => import('@/views/front/user/Comments.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/favorites',
    name: 'UserFavorites',
    component: () => import('@/views/front/user/Favorites.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/orders',
    name: 'OrderList',
    component: () => import('@/views/front/OrderList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/profile',
    name: 'UserProfile',
    component: () => import('@/views/front/user/Profile.vue'),
    meta: { requiresAuth: true }
  },

  // 管理员路由
  {
    path: '/admin',
    name: 'admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: {
      requiresAuth: true,
      requiresAdmin: true // 需要管理员权限
    },
    children: [
      {
        path: 'dashboard',
        name: 'admin-dashboard',
        component: () => import('@/views/admin/AdminDashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'users',
        name: 'user-management',
        component: () => import('@/views/admin/UserManagement.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'movies',
        name: 'movie-management',
        component: () => import('@/views/admin/MovieManagement.vue'),
        meta: { title: '电影管理' }
      },
      {
        path: 'sessions',
        name: 'session-management',
        component: () => import('@/views/admin/SessionManagement.vue'),
        meta: { title: '场次管理' }
      },
      {
        path: 'orders',
        name: 'order-management',
        component: () => import('@/views/admin/OrderManagement.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'news',
        name: 'news-management',
        component: () => import('@/views/admin/NewsManagement.vue'),
        meta: { title: '资讯管理' }
      }
    ]
  },
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

  next()
})

// 路由错误处理
router.onError((error) => {
  console.error('路由错误:', error)
})

export default router