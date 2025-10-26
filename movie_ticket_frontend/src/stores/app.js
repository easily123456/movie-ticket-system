import { defineStore } from 'pinia'
import { ref } from 'vue'
//defineStore是创建状态存储的核心函数，接收 store 名称和配置选项作为参数
//ref用于创建响应式的引用数据,它将普通数据包装成响应式数据，使得数据变化时能触发视图更新

export const useAppStore = defineStore('app', () => {
  const isLoading = ref(false)        // 创建一个响应式的加载状态变量，初始值为false，用于控制页面或组件的加载状态显示
  const globalError = ref(null)       // 创建一个响应式的全局错误变量，初始值为null，用于存储和管理全局错误信息
  const sidebarCollapsed = ref(false) // 创建一个响应式的侧边栏折叠状态变量，初始值为false，用于控制侧边栏的展开/收起状态
  const theme = ref('light')          // 创建一个响应式的主题变量，初始值为'light'，用于管理应用的主题模式（如浅色/深色主题）
  const setLoading = (loading) => {
    isLoading.value = loading
  }

  const setError = (error) => {
    globalError.value = error
  }

  const clearError = () => {
    globalError.value = null
  }

  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  const setTheme = (newTheme) => {
    theme.value = newTheme
    document.documentElement.setAttribute('data-theme', newTheme)  // 设置HTML根元素的data-theme属性为新的主题值，用于CSS主题切换
  }

  return {
    isLoading,
    globalError,
    sidebarCollapsed,
    theme,
    setLoading,
    setError,
    clearError,
    toggleSidebar,
    setTheme
  }
})
