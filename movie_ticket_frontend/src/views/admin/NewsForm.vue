<template>
  <div class="news-form">
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      label-position="top"
    >
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="资讯标题" prop="title">
            <el-input
              v-model="form.title"
              placeholder="请输入资讯标题"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="作者" prop="author">
            <el-input
              v-model="form.author"
              placeholder="请输入作者名称"
              maxlength="50"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="封面图片">
            <el-upload
              class="cover-upload"
              action="/api/admin/upload"
              :show-file-list="false"
              :on-success="handleCoverSuccess"
              :before-upload="beforeCoverUpload"
            >
              <el-image
                v-if="form.coverImage"
                :src="form.coverImage"
                class="cover-image"
                fit="cover"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div v-else class="upload-placeholder">
                <el-icon><Plus /></el-icon>
                <div class="upload-text">上传封面</div>
              </div>
            </el-upload>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="资讯内容" prop="content">
            <div class="editor-container">
              <textarea
                v-if="!richEditor"
                v-model="form.content"
                class="simple-editor"
                placeholder="请输入资讯内容..."
                rows="12"
              />
              <div v-else class="editor-wrapper">
                <!-- 这里可以集成富文本编辑器，如 WangEditor 或 TinyMCE -->
                <div class="editor-toolbar">
                  <el-button-group>
                    <el-button
                      :type="editorMode === 'simple' ? 'primary' : ''"
                      @click="editorMode = 'simple'"
                    >
                      简易模式
                    </el-button>
                    <el-button
                      :type="editorMode === 'rich' ? 'primary' : ''"
                      @click="editorMode = 'rich'"
                    >
                      富文本模式
                    </el-button>
                  </el-button-group>
                </div>
                <textarea
                  v-if="editorMode === 'simple'"
                  v-model="form.content"
                  class="simple-editor"
                  placeholder="请输入资讯内容..."
                  rows="12"
                />
                <div v-else class="rich-editor-placeholder">
                  <el-alert
                    title="富文本编辑器"
                    description="在实际项目中，这里可以集成 WangEditor、TinyMCE 等富文本编辑器"
                    type="info"
                    :closable="false"
                  />
                  <textarea
                    v-model="form.content"
                    class="simple-editor"
                    placeholder="请输入资讯内容..."
                    rows="12"
                  />
                </div>
              </div>
            </div>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="发布状态">
            <el-switch
              v-model="form.status"
              active-text="发布"
              inactive-text="草稿"
              :active-value="1"
              :inactive-value="0"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="置顶设置">
            <el-switch
              v-model="form.isTop"
              active-text="置顶"
              inactive-text="普通"
              :active-value="1"
              :inactive-value="0"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="发布时间" v-if="form.status">
            <el-date-picker
              v-model="form.publishTime"
              type="datetime"
              placeholder="选择发布时间"
              style="width: 100%"
              :disabled-date="disabledDate"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 内容预览 -->
      <el-form-item label="内容预览" v-if="form.content">
        <div class="content-preview">
          <div class="preview-header">
            <h3 class="preview-title">{{ form.title || '无标题' }}</h3>
            <div class="preview-meta">
              <span class="author">作者: {{ form.author || '未知' }}</span>
              <span class="time">发布时间: {{ formatDateTime(form.publishTime || new Date()) }}</span>
            </div>
          </div>
          <div class="preview-content">
            {{ getPreviewContent(form.content) }}
          </div>
          <div class="preview-footer">
            字数: {{ form.content.length }} 字符
          </div>
        </div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEdit ? '更新' : '发布' }}
        </el-button>
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="info" @click="handleSaveDraft" :loading="savingDraft" v-if="!isEdit">
          保存草稿
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Picture } from '@element-plus/icons-vue'
import { formatDateTime } from '@/utils'
import { useAdminStore } from '@/stores/admin'

const props = defineProps({
  news: {
    type: Object,
    default: null
  },
  isEdit: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['submit', 'cancel'])

const adminStore = useAdminStore()
const formRef = ref()
const submitting = ref(false)
const savingDraft = ref(false)

// 编辑器配置
const richEditor = ref(false) // 是否启用富文本编辑器
const editorMode = ref('simple') // 编辑器模式

const form = reactive({
  title: '',
  author: '',
  coverImage: '',
  content: '',
  status: 1, // 1: 发布, 0: 草稿
  isTop: 0,  // 1: 置顶, 0: 普通
  publishTime: null
})

const rules = {
  title: [
    { required: true, message: '请输入资讯标题', trigger: 'blur' },
    { min: 5, max: 200, message: '标题长度在 5 到 200 个字符', trigger: 'blur' }
  ],
  author: [
    { max: 50, message: '作者名称不能超过 50 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入资讯内容', trigger: 'blur' },
    { min: 10, message: '内容长度至少 10 个字符', trigger: 'blur' }
  ]
}

// 监听props.news变化
watch(() => props.news, (newNews) => {
  if (newNews) {
    Object.assign(form, {
      ...newNews,
      publishTime: newNews.publishTime ? new Date(newNews.publishTime) : null
    })
  } else {
    resetForm()
  }
}, { immediate: true })

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    title: '',
    author: '',
    coverImage: '',
    content: '',
    status: 1,
    isTop: 0,
    publishTime: new Date()
  })

  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 获取预览内容
const getPreviewContent = (content) => {
  if (!content) return ''
  // 去除HTML标签（如果有的话）
  const text = content.replace(/<[^>]*>/g, '')
  return text.length > 300 ? text.substring(0, 300) + '...' : text
}

// 封面图片上传前验证
const beforeCoverUpload = (file) => {
  const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPGOrPNG) {
    ElMessage.error('封面图片只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('封面图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 封面图片上传成功
const handleCoverSuccess = (response) => {
  if (response && response.data) {
    form.coverImage = response.data.url
    ElMessage.success('封面图片上传成功')
  }
}

// 禁用日期（不能选择过去的日期）
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  const valid = await formRef.value.validate()
  if (!valid) return

  submitting.value = true
  try {
    if (props.isEdit) {
      await adminStore.updateNews(form.id, form)
      ElMessage.success('资讯更新成功')
    } else {
      await adminStore.createNews(form)
      ElMessage.success('资讯发布成功')
    }

    emit('submit')
  } catch (error) {
    console.error(error)
    ElMessage.error(props.isEdit ? '更新资讯失败' : '发布资讯失败')
  } finally {
    submitting.value = false
  }
}

// 保存草稿
const handleSaveDraft = async () => {
  if (!formRef.value) return

  const valid = await formRef.value.validate()
  if (!valid) return

  savingDraft.value = true
  try {
    const draftData = { ...form, status: 0 }
    await adminStore.createNews(draftData)
    ElMessage.success('草稿保存成功')
    emit('submit')
  } catch (error) {
    console.error(error)
    ElMessage.error('保存草稿失败')
  } finally {
    savingDraft.value = false
  }
}

// 取消
const handleCancel = () => {
  emit('cancel')
}

defineExpose({
  resetForm
})
</script>
<style scoped lang="scss">
.news-form {
  .cover-upload {
    width: 100%;

    .cover-image {
      width: 100%;
      height: 120px;
      border-radius: $border-radius-small;
    }

    .image-error {
      width: 100%;
      height: 120px;
      background: $bg-gray;
      border-radius: $border-radius-small;
      display: flex;
      align-items: center;
      justify-content: center;
      color: $text-disabled;

      .el-icon {
        font-size: 32px;
      }
    }

    .upload-placeholder {
      width: 100%;
      height: 120px;
      border: 1px dashed $border-color;
      border-radius: $border-radius-small;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: $text-secondary;
      cursor: pointer;
      transition: border-color 0.3s;

      &:hover {
        border-color: $primary-color;
      }

      .el-icon {
        font-size: 24px;
        margin-bottom: $spacing-xs;
      }

      .upload-text {
        font-size: $font-size-small;
      }
    }
  }
}

.editor-container {
  .simple-editor {
    width: 100%;
    padding: $spacing-md;
    border: 1px solid $border-color;
    border-radius: $border-radius-small;
    font-family: inherit;
    font-size: $font-size-base;
    line-height: 1.6;
    resize: vertical;
    transition: border-color 0.3s;

    &:focus {
      outline: none;
      border-color: $primary-color;
    }

    &::placeholder {
      color: $text-placeholder;
    }
  }

  .editor-wrapper {
    .editor-toolbar {
      margin-bottom: $spacing-md;
      padding: $spacing-md;
      background: $bg-gray;
      border-radius: $border-radius-small;
    }

    .rich-editor-placeholder {
      .el-alert {
        margin-bottom: $spacing-md;
      }
    }
  }
}

.content-preview {
  border: 1px solid $border-light;
  border-radius: $border-radius-base;
  padding: $spacing-lg;
  background: $bg-white;

  .preview-header {
    border-bottom: 1px solid $border-light;
    padding-bottom: $spacing-md;
    margin-bottom: $spacing-md;

    .preview-title {
      font-size: 18px;
      font-weight: 700;
      color: $text-primary;
      margin: 0 0 $spacing-sm 0;
    }

    .preview-meta {
      display: flex;
      gap: $spacing-lg;
      color: $text-secondary;
      font-size: $font-size-small;

      span {
        padding: 2px 6px;
        background: $bg-gray;
        border-radius: $border-radius-small;
      }
    }
  }

  .preview-content {
    line-height: 1.8;
    color: $text-regular;
    margin-bottom: $spacing-md;
    white-space: pre-wrap;
    word-wrap: break-word;
  }

  .preview-footer {
    border-top: 1px solid $border-light;
    padding-top: $spacing-md;
    color: $text-secondary;
    font-size: $font-size-small;
    text-align: right;
  }
}

// 响应式设计
@media (max-width: $breakpoint-md) {
  .news-form {
    :deep(.el-form-item) {
      margin-bottom: $spacing-lg;
    }

    :deep(.el-col) {
      margin-bottom: 0;
    }
  }

  .preview-meta {
    flex-direction: column;
    gap: $spacing-sm !important;
  }
}
</style>
