import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from '@/layouts/AdminLayout.vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'dashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'users',
        name: 'user-management',
        component: () => import('@/views/admin/UserManagement.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'genres',
        name: 'genre-management',
        component: () => import('@/views/admin/GenreManagement.vue'),
        meta: { title: '类型管理' }
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
      },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 管理员权限检查
router.beforeEach((to, from, next) => {
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
})

export default router
