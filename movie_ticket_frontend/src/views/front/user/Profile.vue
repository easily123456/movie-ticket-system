<template>
  <div class="profile-page">
    <div class="container">
      <div class="profile-content">
        <section class="content-section">
          <h2 class="section-title">个人信息</h2>
          <el-form
            :model="profileForm"
            :rules="profileRules"
            ref="profileFormRef"
            label-width="100px"
          >
            <el-form-item label="用户名" prop="username">
              <el-input v-model="profileForm.username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="头像">
              <div class="avatar-upload">
                <el-avatar :size="100" :src="profileForm.avatar" class="avatar-preview">
                  {{ profileForm.username?.charAt(0) }}
                </el-avatar>
                <el-upload
                  action="/api/upload/avatar"
                  name="file"
                  accept="image/*"
                  :show-file-list="false"
                  :headers="uploadHeaders"
                  :on-success="handleAvatarSuccess"
                  :before-upload="beforeAvatarUpload"
                >
                  <el-button type="primary">更换头像</el-button>
                </el-upload>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSaveProfile">
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </section>

        <section class="content-section">
          <h2 class="section-title">安全设置</h2>
          <el-form
            :model="passwordForm"
            :rules="passwordRules"
            ref="passwordFormRef"
            label-width="120px"
          >
            <el-form-item label="当前密码" prop="currentPassword">
              <el-input v-model="passwordForm.currentPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="changing" @click="handleChangePassword">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { userApi } from '@/api'

const authStore = useAuthStore()

defineOptions({
  name: 'UserProfile',
})

// 个人信息
const saving = ref(false)
const profileFormRef = ref()
const profileForm = reactive({
  username: '',
  email: '',
  phone: '',
  avatar: '',
  avatarRaw: ''
})

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const normalizeAvatarUrl = (url) => {
  if (!url) return ''
  // 如果后端返回相对路径（以 /uploads/ 开头），则加上后端地址
  if (url.startsWith('/')) return `${API_BASE}${url}`
  return url
}

// 上传组件需要单独添加 Authorization header（el-upload 不会使用 axios 实例的拦截器）
const uploadHeaders = computed(() => {
  return authStore.token ? { Authorization: `Bearer ${authStore.token}` } : {}
})

const profileRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 密码
const changing = ref(false)
const passwordFormRef = ref()
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  currentPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else callback()
      },
      trigger: 'blur'
    }
  ]
}

onMounted(async () => {
  await loadProfile()
})

const loadProfile = async () => {
  try {
    // 优先从后端获取，兼容已登录本地缓存
    const resp = await userApi.getProfile()
    const data = resp.data || resp
  profileForm.username = data.username || authStore.user?.username || ''
  profileForm.email = data.email || authStore.user?.email || ''
  profileForm.phone = data.phone || authStore.user?.phone || ''
  const raw = data.avatar || authStore.user?.avatar || ''
  profileForm.avatarRaw = raw
  profileForm.avatar = normalizeAvatarUrl(raw)
  } catch (e) {
    // 回退：若接口异常则从本地缓存填充
    console.error(e)
  profileForm.username = authStore.user?.username || ''
  profileForm.email = authStore.user?.email || ''
  profileForm.phone = authStore.user?.phone || ''
  const raw2 = authStore.user?.avatar || ''
  profileForm.avatarRaw = raw2
  profileForm.avatar = normalizeAvatarUrl(raw2)
  }
}

const handleSaveProfile = async () => {
  if (!profileFormRef.value) return
  await profileFormRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      await userApi.updateProfile({
        username: profileForm.username,
        email: profileForm.email,
        phone: profileForm.phone,
        avatar: profileForm.avatarRaw || profileForm.avatar
      })
      // 同步到本地认证存储
      const rawAvatar = profileForm.avatarRaw || profileForm.avatar
      const updated = {
        ...(authStore.user || {}),
        username: profileForm.username,
        phone: profileForm.phone,
        avatar: rawAvatar
      }
      // 保存原始值到 localStorage（后端/初始化时会统一处理）
      localStorage.setItem('user', JSON.stringify(updated))
      // 将 normalized avatar 赋给 store，确保应用中显示即时更新
      if (authStore.user !== undefined) {
        authStore.user = {
          ...updated,
          avatar: normalizeAvatarUrl(rawAvatar)
        }
      }
      ElMessage.success('个人信息更新成功')
    } catch (e) {
      console.error('更新个人信息失败:', e)
      ElMessage.error('更新失败，请稍后重试')
    } finally {
      saving.value = false
    }
  })
}

const handleAvatarSuccess = async (response) => {
  // 后端返回 { url: '...' }
  const raw = response?.url || response?.data?.url || ''
  if (!raw) {
    ElMessage.error('无法解析上传结果')
    return
  }

  profileForm.avatarRaw = raw
  profileForm.avatar = normalizeAvatarUrl(raw)
  ElMessage.success('头像上传成功')
}

const beforeAvatarUpload = (file) => {
  const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isJPGOrPNG) {
    ElMessage.error('头像必须是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return
    changing.value = true
    try {
      // 后端期望的字段名为 oldPassword 和 newPassword（与 DTO PasswordChangeRequest 对应）
      await userApi.changePassword({
        oldPassword: passwordForm.currentPassword,
        newPassword: passwordForm.newPassword
      })
      ElMessage.success('密码修改成功')
      passwordFormRef.value.resetFields()
    } catch (e) {
      console.error('修改密码失败:', e)
      ElMessage.error('修改密码失败')
    } finally {
      changing.value = false
    }
  })
}
</script>

<style scoped lang="scss">
.profile-page {
  padding: $spacing-lg 0 $spacing-xxl;
  min-height: 80vh;
}

.profile-content {
  background: $bg-white;
  border-radius: $border-radius-base;
  box-shadow: $shadow-base;
  overflow: hidden;
}

.content-section {
  padding: $spacing-xl;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: $spacing-xl;
  color: $text-primary;
  border-bottom: 1px solid $border-light;
  padding-bottom: $spacing-md;
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: $spacing-lg;

  .avatar-preview {
    flex-shrink: 0;
  }
}
</style>


