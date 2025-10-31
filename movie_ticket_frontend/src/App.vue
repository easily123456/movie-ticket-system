<template>
  <div id="app">
    <AppHeader />
    <main class="app-main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    <AppFooter />
  </div>
</template>
<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'

const authStore = useAuthStore()

onMounted(() => {
  // 初始化认证状态
  authStore.initAuth()
})
</script>
<style lang="scss">
#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 全局样式重置 */
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

html, body {
  height: 100%;
  background-color: $bg-color;
}

#app {
  min-height: 100vh;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: lighten($bg-gray, 2%);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: darken($border-base, 15%);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: darken($border-base, 25%);
}

/* 响应式图片 */
img {
  max-width: 100%;
  height: auto;
}

/* 链接样式重置 */
a {
  text-decoration: none;
  color: inherit;
}

/* 按钮点击效果 */
.el-button {
  transition: all 0.3s ease;
}

/* 输入框焦点效果 */
.el-input__wrapper.is-focus {
  box-shadow: 0 0 0 2px rgba(255, 95, 22, 0.2) !important;
}

/* 卡片悬停效果 */
.el-card {
  transition: all 0.3s ease;
}

.el-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
  transform: translateY(-2px);
}

/* 加载状态 */
.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

/* 空状态 */
.empty-container {
  text-align: center;
  padding: 40px;
  color: $text-secondary;
}

/* 错误状态 */
.error-container {
  text-align: center;
  padding: 40px;
  color: $danger-color;
}

/* 成功状态 */
.success-container {
  text-align: center;
  padding: 40px;
  color: $success-color;
}
</style>
