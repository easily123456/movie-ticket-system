import AdminLayout from '@/layouts/AdminLayout.vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    meta: {
      title: '管理后台',
      requiresAuth: true
     },
    children: [
      {
        path: 'dashboard',
        name: 'dashboard',
        component: () => import('@/views/admin/AdminDashboard.vue'),
        meta: { title: '仪表盘' , requiresAuth: true }
      },
      {
        path: 'users',
        name: 'user-management',
        component: () => import('@/views/admin/UserManagement.vue'),
        meta: { title: '用户管理' , requiresAuth: true }
      },
      {
        path: 'genres',
        name: 'genre-management',
        component: () => import('@/views/admin/GenreManagement.vue'),
        meta: { title: '类型管理' , requiresAuth: true }
      },
      {
        path: 'movies',
        name: 'movie-management',
        component: () => import('@/views/admin/MovieManagement.vue'),
        meta: { title: '电影管理' , requiresAuth: true }
      },
      {
        path: 'sessions',
        name: 'session-management',
        component: () => import('@/views/admin/SessionManagement.vue'),
        meta: { title: '场次管理', requiresAuth: true }
      },
      {
        path: 'orders',
        name: 'order-management',
        component: () => import('@/views/admin/OrderManagement.vue'),
        meta: { title: '订单管理', requiresAuth: true }
      },
      {
        path: 'news',
        name: 'news-management',
        component: () => import('@/views/admin/NewsManagement.vue'),
        meta: { title: '资讯管理', requiresAuth: true }
      },
    ]
  }
]

// 路由守卫 - 管理员权限检查
const adminRouteGuard = (to, from, next) => {
  if (to.path.startsWith('/admin')) {
    // 检查用户是否登录且是管理员
    const authStore = useAuthStore()
    if (!authStore.isAuthenticated) {
      next('/login')
      return
    }

    if (authStore.userInfo?.role !== 'ADMIN') {
      ElMessage.error('无权限访问管理后台')
      next('/')
      return
    }
  }
  next()
}

export { routes as default, adminRouteGuard }
