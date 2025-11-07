<template>
  <div class="comment-form">
    <div class="form-header">
      <h3 class="form-title">发表评论</h3>
      <div v-if="!isAuthenticated" class="login-prompt">
        <span>请先</span>
        <el-button type="primary" link @click="handleLogin">登录</el-button>
        <span>后发表评论</span>
      </div>
    </div>
    <div v-if="isAuthenticated" class="form-content">
      <!-- 评分 -->
      <div class="rating-section">
        <label class="rating-label">评分</label>
        <div class="rating-stars">
          <el-rate
            v-model="form.rating"
            :colors="['#99A9BF', '#F2C97D', '#E6A23C']"
            show-text
            text-color="#e6a23c"
            score-template="{value} 分"
          />
        </div>
      </div>
      <!-- 评论内容 -->
      <div class="content-section">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="4"
          maxlength="500"
          placeholder="分享你的观影感受...（最少5字，最多500字）"
          show-word-limit
          resize="none"
        />
      </div>
      <!-- 提交按钮 -->
      <div class="form-actions">
        <el-button
          type="primary"
          :loading="submitting"
          :disabled="!canSubmit"
          @click="handleSubmit"
        >
          发表评论
        </el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useCommentStore } from '@/stores/comments'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const props = defineProps({
  movieId: {
    type: [String, Number],
    required: true
  }
})

const emits = defineEmits(['success'])

const router = useRouter()
const commentStore = useCommentStore()
const authStore = useAuthStore()

const form = reactive({
  rating: 0,
  content: ''
})
const submitting = ref(false)

// 计算属性
const isAuthenticated = computed(() => authStore.isAuthenticated)
const canSubmit = computed(() => {
  return form.rating > 0 && form.content.trim().length >= 5
})

// 处理登录
const handleLogin = () => {
  router.push('/login')
}

// 处理提交
const handleSubmit = async () => {
  if (!canSubmit.value) {
    ElMessage.warning('请填写完整的评论内容（至少5个字）')
    return
  }

  submitting.value = true
  try {
    const commentData = {
      movieId: props.movieId,
      content: form.content.trim(),
      rating: form.rating
    }

    await commentStore.createComment(commentData)
    ElMessage.success('评论发表成功')
    handleReset()
    emits('success')
  } catch (error) {
    // 如果后端返回了友好的错误消息（比如：您已经评论过该电影），
    // 全局响应拦截器已经负责展示该消息，避免重复提示。
    const resp = error && error.response && error.response.data
    if (resp && resp.message) {
      // 已由请求拦截器显示，直接返回
      return
    }
    ElMessage.error(error.message || '评论发表失败')
  } finally {
    submitting.value = false
  }
}

// 处理重置
const handleReset = () => {
  form.rating = 0
  form.content = ''
}
</script>
<style scoped lang="scss">
.comment-form {
  background: $bg-white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-header {
  margin-bottom: 20px;

  .form-title {
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 12px 0;
  }

  .login-prompt {
    font-size: 14px;
    color: $text-regular;

    span {
      margin-right: 4px;
    }
  }
}

.form-content {
  .rating-section {
    display: flex;
    align-items: center;
    margin-bottom: 16px;

    .rating-label {
      width: 60px;
      font-size: 14px;
      color: $text-regular;
      font-weight: 500;
    }

    .rating-stars {
      flex: 1;
    }
  }

  .content-section {
    margin-bottom: 20px;
  }

  .form-actions {
    display: flex;
    gap: 12px;

    .el-button {
      min-width: 100px;
    }
  }
}

@media (max-width: 768px) {
  .comment-form {
    padding: 16px;
  }

  .rating-section {
    flex-direction: column;
    align-items: flex-start !important;
    gap: 8px;
  }
}
</style>
