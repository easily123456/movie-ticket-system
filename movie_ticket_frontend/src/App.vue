<template>
  <div id="app">
    <AppHeader />
    <main class="app-main">
      <router-view v-slot="{ Component }">
        <!-- <router-view> 是 Vue Router 提供的组件，用于渲染当前路由匹配的组件 -->
        <!-- v-slot用于接收父组件传递给子组件的内容，解构赋值语法，从插槽作用域中提取 Component 属性 -->
        <transition name="fade" mode="out-in">
          <!-- <transition>：Vue 的过渡组件，用于包装需要添加过渡效果的元素 -->
          <!-- name="fade"：指定过渡类名前缀为 "fade"，会自动应用 .fade-enter-from、.fade-enter-active 等 CSS 类-->
          <!-- mode="out-in"：指定过渡模式为"先出后入"，即先执行离开动画，再执行进入动画-->
          <component :is="Component" />
          <!-- <component>：Vue 内置的动态组件标签-->
          <!-- :is="Component"：这里的 Component 来自于 <router-view v-slot="{ Component }"> 中解构出来的路由组件实例-->
        </transition>
      </router-view>  <!-- 结束路由视图容器 -->
    </main>
    <AppFooter />

    <!-- 全局加载状态 -->
    <el-loading-indicator
      v-if="appStore.isLoading"
      class="global-loading"
    />
    <!-- el-loading-indicator：Element Plus 提供的加载指示器组件，用于显示全局加载状态 -->
  </div>
</template>
<script setup>
import { onMounted } from 'vue' //在组件挂载完成后执行特定逻辑，用来在应用启动时执行初始化代码（如输出启动日志）
import { useAppStore } from '@/stores/app'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'

const appStore = useAppStore()

onMounted(() => { /* 当 App.vue 组件被挂载到 DOM 后，这个函数会自动执行 */
  // 应用初始化逻辑
  console.log('星光影城前端应用已启动')
})
</script>
<style lang="scss">
#app {
  min-height: 100vh; /* 确保应用容器至少占满整个视口高度，防止内容过少时页面出现空白区域 */
  display: flex; /* 启用弹性盒子布局，使子元素可以使用flex属性进行布局控制 */
  flex-direction: column; /* 设置主轴方向为垂直方向，让header、main、footer按从上到下排列 */
}

.app-main {
  flex: 1; /* 填充剩余空间，让main元素填充剩余的页面高度 */
  min-height: calc(100vh - 64px - 200px); // 减去header和footer高度
}

// 全局过渡动画
.fade-enter-active,  /* 应用于路由切换时新页面元素进入的过渡动画过程，控制进入动画的持续时间和缓动效果 */
.fade-leave-active { /* 应用于路由切换时旧页面元素离开的过渡动画过程，控制离开动画的持续时间和缓动效果 */
  transition: opacity 0.3s ease; /* 定义透明度变化的过渡效果，持续0.3秒，使用ease缓动函数 */
}

.fade-enter-from,  /* 定义新页面元素开始进入时的初始状态，透明度为0，实现淡入效果的起始点 */
.fade-leave-to {   /* 定义旧页面元素离开结束时的最终状态，透明度为0，实现淡出效果的终点 */
  opacity: 0;      /* 设置元素完全透明，配合.fade-enter-active和.fade-leave-active实现平滑的淡入淡出过渡动画 */
}

// 全局加载指示器
.global-loading { /* 它是一个全局加载状态指示器的样式类，
  当 appStore.isLoading 为 true 时，会显示这个加载动画具体实现是一个位于页面顶部的细长进度条动画效果 */
  position: fixed; /* 固定在页面顶部 */
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, $primary-color, $accent-color); /* 渐变背景色 */
  z-index: $z-index-toast;
  animation: loading 1.5s infinite; /* 定义加载动画的动画效果，持续1.5秒，无限循环 */
}

@keyframes loading {  /* 定义名为loading的关键帧动画，用于实现全局加载指示器的移动效果 */
  0% {               /* 动画开始状态（0%时刻） */
    transform: translateX(-100%);  /* 将加载条向左平移自身宽度的100%，使其完全隐藏在屏幕左侧 */
  }
  100% {             /* 动画结束状态（100%时刻） */
    transform: translateX(100%);   /* 将加载条向右平移自身宽度的100%，使其完全移出屏幕右侧 */
  }
}

// 响应式调整
@media (max-width: $breakpoint-sm) {  /* 媒体查询，当屏幕宽度小于或等于$breakpoint-sm断点值时生效，用于适配移动端设备 */
  .app-main {                         /* 针对.app-main元素在小屏幕设备上的样式调整 */
    min-height: calc(100vh - 56px - 150px);
    /* 重新计算main区域的最小高度，减去移动端header(56px)和footer(150px)的高度，确保在小屏幕设备上布局正确 */
  }
}
</style>
