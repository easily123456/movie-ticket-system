import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const isLoading = ref(false)
  const globalError = ref(null)
  const sidebarCollapsed = ref(false)
  const theme = ref('light')

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
    document.documentElement.setAttribute('data-theme', newTheme)
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
