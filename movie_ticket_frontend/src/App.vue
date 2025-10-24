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

    <!-- 全局加载状态 -->
    <el-loading-indicator
      v-if="appStore.isLoading"
      class="global-loading"
    />
  </div>
</template>
<script setup>
import { onMounted } from 'vue'
import { useAppStore } from '@/stores/app'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'

const appStore = useAppStore()

onMounted(() => {
  // 应用初始化逻辑
  console.log('星光影城前端应用已启动')
})
</script>
<style lang="scss">
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-main {
  flex: 1;
  min-height: calc(100vh - 64px - 200px); // 减去header和footer高度
}

// 全局过渡动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

// 全局加载指示器
.global-loading {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, $primary-color, $accent-color);
  z-index: $z-index-toast;
  animation: loading 1.5s infinite;
}

@keyframes loading {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

// 响应式调整
@media (max-width: $breakpoint-sm) {
  .app-main {
    min-height: calc(100vh - 56px - 150px);
  }
}
</style>
