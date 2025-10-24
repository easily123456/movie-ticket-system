<template>
  <header class="app-header">
    <div class="container">
      <div class="header-content">
        <!-- Logo -->
        <router-link to="/" class="logo">
          <div class="logo-content">
            <span class="logo-icon">🎬</span>
            <span class="logo-text">星光影城</span>
          </div>
        </router-link>
        <!-- 主导航 -->
        <nav class="main-nav">
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            class="nav-item"
            :class="{ active: $route.path === item.path }"
          >
            {{ item.name }}
          </router-link>
        </nav>
        <!-- 搜索框 -->
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索电影、演员、导演..."
            size="large"
            @keyup.enter="handleSearch"
            @focus="showSearchSuggestions = true"
            @blur="hideSuggestions"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>

          <!-- 搜索建议 -->
          <div
            v-if="showSearchSuggestions && searchKeyword"
            class="search-suggestions"
          >
            <div class="suggestion-item" @mousedown="handleSuggestionClick(searchKeyword)">
              搜索 "{{ searchKeyword }}"
            </div>
          </div>
        </div>
        <!-- 用户操作 -->
        <div class="user-actions">
          <template v-if="authStore.isAuthenticated">
            <el-dropdown trigger="click" @command="handleUserCommand">
              <div class="user-info">
                <el-avatar
                  :size="36"
                  :src="authStore.userInfo?.avatar"
                  class="user-avatar"
                >
                  {{ authStore.userInfo?.username?.charAt(0) }}
                </el-avatar>
                <span class="username hidden-xs">{{ authStore.userInfo?.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="orders">
                    <el-icon><Document /></el-icon>
                    我的订单
                  </el-dropdown-item>
                  <el-dropdown-item command="favorites" divided>
                    <el-icon><Star /></el-icon>
                    我的收藏
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-if="authStore.isAdmin"
                    command="admin"
                    divided
                  >
                    <el-icon><Monitor /></el-icon>
                    管理后台
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <div class="auth-buttons">
              <el-button link @click="$router.push('/login')">登录</el-button>
              <el-button type="primary" @click="$router.push('/register')">
                注册
              </el-button>
            </div>
          </template>
        </div>
      </div>
    </div>
  </header>
</template>
<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  Search,
  User,
  Document,
  Star,
  Monitor,
  SwitchButton,
  ArrowDown
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const searchKeyword = ref('')
const showSearchSuggestions = ref(false)

const navItems = computed(() => [
  { name: '首页', path: '/' },
  { name: '电影', path: '/movies' },
  { name: '影院', path: '/cinemas' },
  { name: '榜单', path: '/rankings' },
  { name: '热点', path: '/news' }
])

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      name: 'MovieSearch',
      query: { q: searchKeyword.value.trim() }
    })
    searchKeyword.value = ''
    showSearchSuggestions.value = false
  }
}

const handleSuggestionClick = (keyword) => {
  searchKeyword.value = keyword
  handleSearch()
}

const hideSuggestions = () => {
  setTimeout(() => {
    showSearchSuggestions.value = false
  }, 200)
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'orders':
      router.push('/orders')
      break
    case 'favorites':
      router.push('/favorites')
      break
    case 'admin':
      window.open('/admin', '_blank')
      break
    case 'logout':
      authStore.logout()
      router.push('/')
      break
  }
}
</script>
<style scoped lang="scss">
.app-header {
  background: $bg-white;
  box-shadow: $shadow-base;
  position: sticky;
  top: 0;
  z-index: $z-index-sticky;
  border-bottom: 1px solid $border-light;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  gap: $spacing-lg;
}

.logo {
  flex-shrink: 0;

  .logo-content {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
  }

  .logo-icon {
    font-size: 24px;
  }

  .logo-text {
    font-size: 20px;
    font-weight: 700;
    color: $primary-color;
    letter-spacing: 1px;
  }
}

.main-nav {
  display: flex;
  gap: $spacing-xs;
  flex: 1;
  justify-content: center;

  .nav-item {
    padding: $spacing-sm $spacing-md;
    color: $text-regular;
    font-weight: 500;
    border-radius: $border-radius-base;
    transition: $transition-base;
    white-space: nowrap;

    &:hover {
      color: $primary-color;
      background: rgba($primary-color, 0.1);
    }

    &.active {
      color: $primary-color;
      background: rgba($primary-color, 0.1);
    }
  }
}

.search-box {
  position: relative;
  width: 280px;
  flex-shrink: 0;

  :deep(.el-input__wrapper) {
    border-radius: $border-radius-round;
  }
}

.search-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: $bg-white;
  border: 1px solid $border-light;
  border-radius: $border-radius-base;
  box-shadow: $shadow-light;
  margin-top: $spacing-xs;
  z-index: $z-index-dropdown;

  .suggestion-item {
    padding: $spacing-sm $spacing-md;
    cursor: pointer;
    transition: $transition-base;

    &:hover {
      background: $bg-gray;
    }
  }
}

.user-actions {
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  padding: $spacing-xs $spacing-sm;
  border-radius: $border-radius-base;
  cursor: pointer;
  transition: $transition-base;

  &:hover {
    background: $bg-gray;
  }

  .user-avatar {
    background: $primary-color;
  }

  .username {
    font-weight: 500;
    color: $text-primary;
  }
}

.auth-buttons {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
}

// 响应式设计
@media (max-width: $breakpoint-md) {
  .header-content {
    gap: $spacing-md;
  }

  .main-nav {
    gap: 0;

    .nav-item {
      padding: $spacing-sm;
      font-size: $font-size-small;
    }
  }

  .search-box {
    width: 200px;
  }
}

@media (max-width: $breakpoint-sm) {
  .header-content {
    gap: $spacing-sm;
  }

  .logo .logo-text {
    display: none;
  }

  .main-nav {
    display: none;
  }

  .search-box {
    width: 160px;
  }
}
</style>
