<template>
  <div class="register-container">
    <div class="register-background">
      <div class="background-overlay"></div>
      <div class="background-image"></div>
    </div>

    <div class="register-card">
      <div class="register-header">
        <h1 class="register-title">创建账户</h1>
        <p class="register-subtitle">注册新账户，开启电影之旅</p>
      </div>
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        @keyup.enter="handleRegister"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="用户名"
                size="large"
                :prefix-icon="User"
                @blur="checkUsername"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="email">
              <el-input
                v-model="registerForm.email"
                placeholder="邮箱地址"
                size="large"
                :prefix-icon="Message"
                @blur="checkEmail"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="手机号码（可选）"
            size="large"
            :prefix-icon="Phone"
          />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="密码"
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="确认密码"
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <div class="password-strength">
            <div class="strength-bar">
              <div
                class="strength-fill"
                :class="passwordStrengthClass"
                :style="{ width: passwordStrength + '%' }"
              ></div>
            </div>
            <div class="strength-text">{{ passwordStrengthText }}</div>
          </div>
        </el-form-item>
        <el-form-item prop="agreement">
          <el-checkbox v-model="registerForm.agreement">
            我已阅读并同意
            <el-link type="primary" :underline="false">《用户协议》</el-link>
            和
            <el-link type="primary" :underline="false">《隐私政策》</el-link>
          </el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-button"
            :loading="loading"
            @click="handleRegister"
          >
            {{ loading ? '注册中...' : '立即注册' }}
          </el-button>
        </el-form-item>
        <el-form-item>
          <div class="login-link">
            已有账户？
            <el-link type="primary" :underline="false" @click="goToLogin">
              立即登录
            </el-link>
          </div>
        </el-form-item>
      </el-form>
    </div>
    <!-- 加载遮罩 -->
    <div v-if="loading" class="loading-mask">
      <el-icon class="loading-icon"><Loading /></el-icon>
    </div>
  </div>
</template>
<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, Phone, Loading } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

defineOptions({
  name: 'RegisterPage'
})

const router = useRouter()
const authStore = useAuthStore()

const registerFormRef = ref()
const loading = ref(false)
const usernameChecking = ref(false)
const emailChecking = ref(false)

const registerForm = reactive({
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  agreement: false
})

// 密码强度计算
const passwordStrength = computed(() => {
  const password = registerForm.password
  if (!password) return 0

  let strength = 0

  // 长度检查
  if (password.length >= 6) strength += 25
  if (password.length >= 8) strength += 25

  // 复杂度检查
  if (/[a-z]/.test(password)) strength += 15
  if (/[A-Z]/.test(password)) strength += 15
  if (/[0-9]/.test(password)) strength += 10
  if (/[^a-zA-Z0-9]/.test(password)) strength += 10

  return Math.min(strength, 100)
})

const passwordStrengthClass = computed(() => {
  const strength = passwordStrength.value
  if (strength < 40) return 'strength-weak'
  if (strength < 70) return 'strength-medium'
  return 'strength-strong'
})

const passwordStrengthText = computed(() => {
  const strength = passwordStrength.value
  if (strength === 0) return '请输入密码'
  if (strength < 40) return '密码强度：弱'
  if (strength < 70) return '密码强度：中'
  return '密码强度：强'
})

// 表单验证规则
const validateUsername = async (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入用户名'))
  } else if (value.length < 3 || value.length > 50) {
    callback(new Error('用户名长度在 3 到 50 个字符'))
  } else {
    callback()
  }
}

const validateEmail = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入邮箱地址'))
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
    callback(new Error('邮箱格式不正确'))
  } else {
    callback()
  }
}

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度至少 6 个字符'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请确认密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateAgreement = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请同意用户协议和隐私政策'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, validator: validateUsername, trigger: 'blur' }
  ],
  email: [
    { required: true, validator: validateEmail, trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  agreement: [
    { required: true, validator: validateAgreement, trigger: 'change' }
  ]
}

// 监听密码变化
watch(() => registerForm.password, () => {
  if (registerForm.confirmPassword) {
    registerFormRef.value?.validateField('confirmPassword')
  }
})

// 检查用户名是否已存在
const checkUsername = async () => {
  if (!registerForm.username || registerForm.username.length < 3) return

  usernameChecking.value = true
  try {
    const exists = await authStore.checkUsername(registerForm.username)
    if (exists) {
      registerFormRef.value?.validateField('username', () => {
        throw new Error('用户名已存在')
      })
    }
  } catch (error) {
    console.error('检查用户名失败:', error)
  } finally {
    usernameChecking.value = false
  }
}

// 检查邮箱是否已存在
const checkEmail = async () => {
  if (!registerForm.email) return

  emailChecking.value = true
  try {
    const exists = await authStore.checkEmail(registerForm.email)
    if (exists) {
      registerFormRef.value?.validateField('email', () => {
        throw new Error('邮箱已被注册')
      })
    }
  } catch (error) {
    console.error('检查邮箱失败:', error)
  } finally {
    emailChecking.value = false
  }
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return

  const valid = await registerFormRef.value.validate()
  if (!valid) return

  loading.value = true
  try {
    const {  ...registerData } = registerForm
    // const { confirmPassword, agreement } = registerForm
    await authStore.register(registerData)

    ElMessage.success('注册成功！')
    router.push('/')
  } catch (error) {
    // 根据具体的错误信息显示相应的提示
    if (error.response && error.response.data && error.response.data.message) {
      if (error.response.data.message.includes('用户名')) {
        // ElMessage.error('用户名已存在')
      } else if (error.response.data.message.includes('邮箱')) {
        // ElMessage.error('邮箱名已存在')
      } else {
        ElMessage.error(error.response.data.message)
      }
    } else {
      ElMessage.error('注册失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/login')
}
</script>
<style scoped lang="scss">
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 40px 20px;
}

.register-background {
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

.register-card {
  width: 480px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
}

.register-header {
  text-align: center;
  margin-bottom: 40px;

  .register-title {
    font-size: 28px;
    font-weight: 700;
    color: $text-primary;
    margin: 0 0 8px 0;
    background: linear-gradient(135deg, $primary-color 0%, $primary-hover 100%);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  .register-subtitle {
    color: $text-secondary;
    font-size: 14px;
    margin: 0;
  }
}

.register-form {
  .el-form-item {
    margin-bottom: 20px;
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

  :deep(.el-checkbox) {
    .el-checkbox__label {
      color: $text-regular;
      font-size: 14px;
    }
  }
}

.password-strength {
  width: 100%;

  .strength-bar {
    height: 6px;
    background: $bg-gray;
    border-radius: 3px;
    overflow: hidden;
    margin-bottom: 8px;

    .strength-fill {
      height: 100%;
      border-radius: 3px;
      transition: all 0.3s ease;

      &.strength-weak {
        background: $danger-color;
      }

      &.strength-medium {
        background: $warning-color;
      }

      &.strength-strong {
        background: $success-color;
      }
    }
  }

  .strength-text {
    font-size: 12px;
    color: $text-secondary;
    text-align: center;
  }
}

.register-button {
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

.login-link {
  text-align: center;
  color: $text-secondary;
  font-size: 14px;

  .el-link {
    font-weight: 600;
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
  .register-container {
    padding: 20px;
  }

  .register-card {
    width: 100%;
    max-width: 480px;
    padding: 30px 24px;
  }

  .register-header {
    margin-bottom: 30px;

    .register-title {
      font-size: 24px;
    }
  }
}

@media (max-width: $breakpoint-xs) {
  .register-card {
    padding: 24px 20px;
  }

  :deep(.el-col) {
    margin-bottom: 0;
  }

  .register-form {
    .el-row {
      margin-bottom: -20px;
    }
  }
}
</style>
