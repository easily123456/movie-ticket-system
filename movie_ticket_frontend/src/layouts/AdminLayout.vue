<template>
  <div class="admin-layout">
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <h2 v-if="!sidebarCollapsed" class="logo">电影票务管理系统</h2>
        <el-icon v-else class="sidebar-icon"><Monitor /></el-icon>
        <el-button
          link
          class="collapse-btn"
          @click="toggleSidebar"
        >
          <el-icon>
            <Expand v-if="sidebarCollapsed" />
            <Fold v-else />
          </el-icon>
        </el-button>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="sidebarCollapsed"
        class="sidebar-menu"
        router
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>

        <el-sub-menu index="movie-management">
          <template #title>
            <el-icon><Film /></el-icon>
            <span>电影管理</span>
          </template>
          <el-menu-item index="/admin/movies">电影列表</el-menu-item>
          <el-menu-item index="/admin/movies/create">添加电影</el-menu-item>
          <el-menu-item index="/admin/genres">类型管理</el-menu-item>
          <el-menu-item index="/admin/sessions">场次管理</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/admin/orders">
          <el-icon><Document /></el-icon>
          <template #title>订单管理</template>
        </el-menu-item>

        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>

        <el-menu-item index="/admin/news">
          <el-icon><Reading /></el-icon>
          <template #title>资讯管理</template>
        </el-menu-item>

        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统设置</span>
          </template>
          <el-menu-item index="/admin/system-config">系统配置</el-menu-item>
          <el-menu-item index="/admin/admin-users">管理员</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </aside>

    <div class="main-container">
      <header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">
              管理后台
            </el-breadcrumb-item>
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="authStore.userInfo?.avatar">
                {{ authStore.userInfo?.username?.charAt(0) }}
              </el-avatar>
              <span class="username">{{ authStore.userInfo?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="front">
                  <el-icon><House /></el-icon>
                  返回前台
                </el-dropdown-item>
                <el-dropdown-item command="profile" divided>
                  <el-icon><User /></el-icon>
                  个人信息
                </el-dropdown-item>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Monitor, Expand, Fold, Odometer, Film,
  Document, User, Reading, Setting,
  ArrowDown, House, SwitchButton
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const appStore = useAppStore()

const sidebarCollapsed = ref(false)

const activeMenu = computed(() => route.path)

const breadcrumbs = computed(() => {
  const matched = route.matched.filter(record => record.meta && record.meta.title)
  return matched.map(record => ({
    path: record.path,
    title: record.meta.title.replace(' - 管理后台', '').replace(' - 星光影城', '')
  }))
})

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
  appStore.sidebarCollapsed = sidebarCollapsed.value
}

const handleCommand = async (command) => {
  switch (command) {
    case 'front':
      router.push('/')
      break
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      await handleLogout()
      break
  }
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      type: 'warning'
    })

    await authStore.logout()
    ElMessage.success('退出成功')
    router.push('/login')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退出失败:', error)
    }
  }
}

watch(() => appStore.sidebarCollapsed, (newVal) => {
  sidebarCollapsed.value = newVal
}, { immediate: true })
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss';

.admin-layout {
  display: flex;
  min-height: 100vh;
  background: variables.$bg-gray;
}

.sidebar {
  width: 240px;
  background: variables.$secondary-color;
  transition: variables.$transition-base;
  display: flex;
  flex-direction: column;
  z-index: variables.$z-index-sidebar;

  &.collapsed {
    width: 64px;
  }
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: variables.$spacing-lg;
  border-bottom: 1px solid rgba(variables.$bg-white, 0.1);

  .logo {
    color: variables.$bg-white;
    font-size: 18px;
    font-weight: 600;
    margin: 0;
    white-space: nowrap;
    overflow: hidden;
  }

  .sidebar-icon {
    color: variables.$bg-white;
    font-size: 24px;
  }

  .collapse-btn {
    color: variables.$bg-white;
    font-size: 16px;
    padding: 0;

    &:hover {
      background: rgba(variables.$bg-white, 0.1);
    }
  }
}

.sidebar-menu {
  flex: 1;
  border: none;
  background: transparent;

  :deep(.el-menu) {
    background: transparent;
    border: none;
  }

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    color: rgba(variables.$bg-white, 0.8);
    height: 48px;
    line-height: 48px;

    &:hover {
      background: rgba(variables.$bg-white, 0.1);
      color: variables.$bg-white;
    }

    &.is-active {
      background: variables.$primary-color;
      color: variables.$bg-white;
    }

    .el-icon {
      color: inherit;
    }
  }

  :deep(.el-sub-menu) {
    .el-menu {
      background: rgba(0, 0, 0, 0.2);
    }

    .el-menu-item {
      min-width: auto;
    }
  }
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.header {
  background: variables.$bg-white;
  padding: variables.$spacing-md variables.$spacing-lg;
  border-bottom: 1px solid variables.$border-light;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: variables.$shadow-base;
  z-index: variables.$z-index-header;

  .header-left {
    :deep(.el-breadcrumb) {
      font-size: variables.$font-size-base;

      .el-breadcrumb__inner {
        color: variables.$text-regular;

        &.is-link {
          color: variables.$text-secondary;
          font-weight: 400;

          &:hover {
            color: variables.$primary-color;
          }
        }
      }
    }
  }
}

.header-right {
  .user-info {
    display: flex;
    align-items: center;
    gap: variables.$spacing-sm;
    padding: variables.$spacing-xs variables.$spacing-sm;
    border-radius: variables.$border-radius-base;
    cursor: pointer;
    transition: variables.$transition-base;

    &:hover {
      background: variables.$bg-gray;
    }

    .username {
      font-weight: 500;
      color: variables.$text-primary;
    }
  }
}

.content {
  flex: 1;
  padding: variables.$spacing-lg;
  overflow: auto;
}

@media (max-width: variables.$breakpoint-md) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: variables.$z-index-fixed;

    &:not(.collapsed) {
      transform: translateX(0);
    }

    &.collapsed {
      transform: translateX(-100%);
    }
  }

  .main-container {
    margin-left: 0 !important;
  }

  .username {
    display: none;
  }
}
</style>
