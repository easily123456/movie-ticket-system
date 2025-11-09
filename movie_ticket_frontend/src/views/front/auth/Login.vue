<template>
  <div class="login-container">
    <div class="login-background">
      <div class="background-overlay"></div>
      <div class="background-image"></div>
    </div>

    <div class="login-card">
      <div class="login-header">
        <h1 class="login-title">电影票务系统</h1>
        <p class="login-subtitle">欢迎回来，请登录您的账户</p>
      </div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <div class="login-options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <el-link type="primary" :underline="false">忘记密码？</el-link>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
        <el-form-item>
          <div class="login-divider">
            <span class="divider-text">或者</span>
          </div>
        </el-form-item>
        <el-form-item>
          <div class="register-link">
            还没有账户？
            <el-link type="primary" :underline="false" @click="goToRegister">
              立即注册
            </el-link>
          </div>
        </el-form-item>
      </el-form>
      <!-- 管理员登录提示 -->
      <div class="admin-tip">
        <el-alert
          title="管理员登录提示"
          description="管理员账号：admin / 密码：admin123"
          type="info"
          :closable="false"
          show-icon
        />
      </div>
    </div>
    <!-- 加载遮罩 -->
    <div v-if="loading" class="loading-mask">
      <el-icon class="loading-icon"><Loading /></el-icon>
    </div>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Loading } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

defineOptions({
  name: 'LoginPage'
})

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const loginFormRef = ref()
const loading = ref(false)
const rememberMe = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在 3 到 50 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 个字符', trigger: 'blur' }
  ]
}

onMounted(() => {
  // 检查是否有记住的登录信息
  const savedUsername = localStorage.getItem('rememberedUsername')
  if (savedUsername) {
    loginForm.username = savedUsername
    rememberMe.value = true
  }
})

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  const valid = await loginFormRef.value.validate()
  if (!valid) return

  loading.value = true
  try {
    await authStore.login(loginForm)
    ElMessage.success('登录成功')

    // 记住用户名
    if (rememberMe.value) {
      localStorage.setItem('rememberedUsername', loginForm.username)
    } else {
      localStorage.removeItem('rememberedUsername')
    }

    // 登录后优先处理回跳（redirect query），否则根据角色跳转
    const redirect = route.query.redirect
    if (redirect && typeof redirect === 'string' && redirect.startsWith('/')) {
      router.push(redirect)
      return
    }

    if (authStore.isAdmin) {
      router.push('/admin/dashboard')
    } else {
      router.push('/')
    }
  } catch (error) {
    // 只显示后端返回的具体错误信息，不显示通用错误信息
    if (error.response && error.response.data && error.response.data.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('登录失败，请检查用户名和密码')
    }
  } finally {
    loading.value = false
  }
}

// 跳转到注册页面
const goToRegister = () => {
  router.push('/register')
}

// 演示账号快速登录（开发环境使用）
// const quickLogin = (username, password) => {
//   loginForm.username = username
//   loginForm.password = password
//   handleLogin()
// }
</script>
<style scoped lang="scss">
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;

  .background-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(
      180deg,
      rgba($primary-color, 0.06) 0%,
      rgba($primary-color, 0.12) 100%
    );
    z-index: 1;
  }

  .background-image {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(180deg, $bg-color 0%, $bg-white 100%);
    opacity: 1;
    z-index: 0;
  }
}

.login-card {
  width: 480px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;

  .login-title {
    font-size: 28px;
    font-weight: 700;
    color: $text-primary;
    margin: 0 0 8px 0;
    background: linear-gradient(135deg, $primary-color 0%, $primary-hover 100%);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  .login-subtitle {
    color: $text-secondary;
    font-size: 14px;
    margin: 0;
  }
}

.login-form {
  .el-form-item {
    margin-bottom: 24px;
  }

  :deep(.el-input) {
    .el-input__wrapper {
      border-radius: 8px;
      padding: 12px 16px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

      &:hover {
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
      }

      &.is-focus {
        box-shadow: 0 0 0 2px rgba($primary-color, 0.2);
      }
    }
  }
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.login-button {
  width: 100%;
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, $primary-color 0%, $primary-hover 100%);
  border: none;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba($primary-color, 0.4);
  }

  &:active {
    transform: translateY(0);
  }
}

.login-divider {
  position: relative;
  text-align: center;
  margin: 20px 0;

  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 0;
    right: 0;
    height: 1px;
    background: $border-light;
  }

  .divider-text {
    background: white;
    padding: 0 16px;
    color: $text-secondary;
    font-size: 14px;
    position: relative;
    z-index: 1;
  }
}

.register-link {
  text-align: center;
  color: $text-secondary;
  font-size: 14px;

  .el-link {
    font-weight: 600;
  }
}

.admin-tip {
  margin-top: 24px;

  :deep(.el-alert) {
    border-radius: 8px;

    .el-alert__description {
      font-size: 12px;
    }
  }
}

.loading-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;

  .loading-icon {
    font-size: 48px;
    color: $primary-color;
    animation: spin 1s linear infinite;
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

// 响应式设计
@media (max-width: $breakpoint-sm) {
  .login-container {
    padding: 20px;
  }

  .login-card {
    width: 100%;
    max-width: 400px;
    padding: 30px 24px;
  }

  .login-header {
    margin-bottom: 30px;

    .login-title {
      font-size: 24px;
    }
  }
}

@media (max-width: $breakpoint-xs) {
  .login-card {
    padding: 24px 20px;
  }

  .login-options {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>
