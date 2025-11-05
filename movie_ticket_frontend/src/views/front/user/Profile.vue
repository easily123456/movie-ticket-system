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
            <el-form-item label="邮箱">
              <el-input v-model="profileForm.email" disabled />
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
                  :show-file-list="false"
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { userApi } from '@/api'

const authStore = useAuthStore()

// 个人信息
const saving = ref(false)
const profileFormRef = ref()
const profileForm = reactive({
  username: '',
  email: '',
  phone: '',
  avatar: ''
})

const profileRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
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
    profileForm.avatar = data.avatar || authStore.user?.avatar || ''
  } catch (e) {
    // 回退：若接口异常则从本地缓存填充
    profileForm.username = authStore.user?.username || ''
    profileForm.email = authStore.user?.email || ''
    profileForm.phone = authStore.user?.phone || ''
    profileForm.avatar = authStore.user?.avatar || ''
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
        phone: profileForm.phone,
        avatar: profileForm.avatar
      })
      // 同步到本地认证存储
      const updated = {
        ...(authStore.user || {}),
        username: profileForm.username,
        phone: profileForm.phone,
        avatar: profileForm.avatar
      }
      localStorage.setItem('user', JSON.stringify(updated))
      // 如果 store 暴露 user 可直接赋值
      if (authStore.user !== undefined) {
        authStore.user = updated
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

const handleAvatarSuccess = (response) => {
  // 后端返回 { url: '...' }
  profileForm.avatar = response.url || response.data?.url || ''
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
      await userApi.changePassword({
        currentPassword: passwordForm.currentPassword,
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


