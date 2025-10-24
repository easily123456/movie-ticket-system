import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/front/Home.vue'),
    meta: {
      title: '星光影城 - 在线电影票务平台',
      keepAlive: true
    }
  },
  {
    path: '/movies',
    name: 'MovieList',
    component: () => import('@/views/front/movies/MovieList.vue'),
    meta: {
      title: '电影列表 - 星光影城',
      keepAlive: true
    }
  },
  {
    path: '/movie/:id',
    name: 'MovieDetail',
    component: () => import('@/views/front/movies/MovieDetail.vue'),
    meta: {
      title: '电影详情 - 星光影城'
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/front/auth/Login.vue'),
    meta: {
      title: '用户登录 - 星光影城',
      requiresGuest: true
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/front/auth/Register.vue'),
    meta: {
      title: '用户注册 - 星光影城',
      requiresGuest: true
    }
  },
  {
    path: '/search',
    name: 'MovieSearch',
    component: () => import('@/views/front/movies/MovieSearch.vue'),
    meta: {
      title: '电影搜索 - 星光影城'
    }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/front/NotFound.vue'),
    meta: {
      title: '页面未找到 - 星光影城'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0, behavior: 'smooth' }
    }
  }
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title
  }

  next()
})

export default router
