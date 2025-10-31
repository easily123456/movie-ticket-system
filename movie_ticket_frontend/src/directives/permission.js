import { useAuthStore } from '@/stores/auth'

// 权限指令
export const permissionDirective = {
  mounted(el, binding) {
    const authStore = useAuthStore()
    const { value } = binding

    if (value && Array.isArray(value)) {
      const hasPermission = value.some(role => {
        if (role === 'admin') {
          return authStore.isAdmin
        }
        if (role === 'user') {
          return authStore.isAuthenticated
        }
        if (role === 'guest') {
          return !authStore.isAuthenticated
        }
        return false
      })

      if (!hasPermission) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error(`需要权限! 使用方式: v-permission="['admin']"`)
    }
  }
}

// 角色指令
export const roleDirective = {
  mounted(el, binding) {
    const authStore = useAuthStore()
    const { value } = binding

    if (value) {
      const hasRole = authStore.user?.role === value
      if (!hasRole) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error('需要角色! 使用方式: v-role="admin"')
    }
  }
}

// 认证指令
export const authDirective = {
  mounted(el, binding) {
    const authStore = useAuthStore()
    const { value } = binding

    if (value === true && !authStore.isAuthenticated) {
      el.parentNode && el.parentNode.removeChild(el)
    } else if (value === false && authStore.isAuthenticated) {
      el.parentNode && el.parentNode.removeChild(el)
    }
  }
}
