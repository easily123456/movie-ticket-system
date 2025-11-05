<template>
  <div class="user-center-page">
    <div class="container">
      <div class="user-center-content">
        <!-- 侧边栏菜单 -->
        <aside class="sidebar">
          <div class="user-profile">
            <el-avatar :size="80" :src="userInfo.avatar" class="user-avatar">
              {{ userInfo.username?.charAt(0) }}
            </el-avatar>
            <div class="user-info">
              <h3 class="username">{{ userInfo.username }}</h3>
              <p class="user-email">{{ userInfo.email }}</p>
              <el-tag :type="userInfo.role === 'ADMIN' ? 'danger' : 'primary'">
                {{ userInfo.role === 'ADMIN' ? '管理员' : '普通用户' }}
              </el-tag>
            </div>
          </div>
          <el-menu
            :default-active="activeMenu"
            class="side-menu"
            @select="handleMenuSelect"
          >
            <el-menu-item index="profile">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </el-menu-item>
            <el-menu-item index="orders">
              <el-icon><Document /></el-icon>
              <span>我的订单</span>
            </el-menu-item>
            <el-menu-item index="favorites">
              <el-icon><Star /></el-icon>
              <span>我的收藏</span>
            </el-menu-item>
            <el-menu-item index="comments">
              <el-icon><ChatDotRound /></el-icon>
              <span>我的评论</span>
            </el-menu-item>
            <el-menu-item index="security">
              <el-icon><Lock /></el-icon>
              <span>安全设置</span>
            </el-menu-item>
          </el-menu>
        </aside>
        <!-- 主内容区 -->
        <main class="main-content">
          <router-view />
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { User, Document, Star, ChatDotRound, Lock } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
// import { formatDate } from '@/utils'

const route = useRoute()
const authStore = useAuthStore()

// 用户信息
const userInfo = ref({})

// 根据当前路由激活菜单项
const activeMenu = ref('profile')

onMounted(() => {
  loadUserInfo()
  updateActiveMenu()
})

const loadUserInfo = () => {
  userInfo.value = authStore.userInfo || {}
}

const updateActiveMenu = () => {
  // 根据当前路由路径确定应该激活哪个菜单项
  const path = route.path
  if (path.includes('/user/orders')) {
    activeMenu.value = 'orders'
  } else if (path.includes('/user/favorites')) {
    activeMenu.value = 'favorites'
  } else if (path.includes('/user/comments')) {
    activeMenu.value = 'comments'
  } else {
    activeMenu.value = 'profile'
  }
}

const handleMenuSelect = (index) => {
  activeMenu.value = index
  // 根据选择的菜单项导航到相应路由
  switch (index) {
    case 'profile':
      window.location.hash = '#/user'
      break
    case 'orders':
      window.location.hash = '#/user/orders'
      break
    case 'favorites':
      window.location.hash = '#/user/favorites'
      break
    case 'comments':
      window.location.hash = '#/user/comments'
      break
    case 'security':
      // 安全设置保留在当前页面
      break
  }
}
</script>

<style scoped lang="scss">
.user-center-page {
  padding: $spacing-lg 0 $spacing-xxl;
  min-height: 100vh;
  background: $bg-gray;
}

.user-center-content {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: $spacing-xl;

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.sidebar {
  background: $bg-white;
  border-radius: $border-radius-base;
  box-shadow: $shadow-base;
  overflow: hidden;
  height: fit-content;
  position: sticky;
  top: $spacing-xl;
}

.user-profile {
  padding: $spacing-xl;
  text-align: center;
  border-bottom: 1px solid $border-light;

  .user-avatar {
    margin-bottom: $spacing-md;
  }

  .user-info {
    .username {
      font-size: 18px;
      font-weight: 700;
      margin-bottom: $spacing-xs;
      color: $text-primary;
    }

    .user-email {
      color: $text-secondary;
      margin-bottom: $spacing-sm;
    }
  }
}

.side-menu {
  border: none;

  :deep(.el-menu-item) {
    height: 60px;
    line-height: 60px;

    &.is-active {
      background: $primary-color;
      color: white;

      .el-icon {
        color: white;
      }
    }
  }
}

.main-content {
  background: $bg-white;
  border-radius: $border-radius-base;
  box-shadow: $shadow-base;
  overflow: hidden;
  min-height: 600px;
}

.loading-container {
  padding: $spacing-xl 0;
}

.empty-state {
  padding: $spacing-xxl 0;
  text-align: center;
}
</style>
