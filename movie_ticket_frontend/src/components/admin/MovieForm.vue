<template>
  <div class="movie-form">
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      label-position="left"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="电影标题" prop="title">
            <el-input
              v-model="form.title"
              placeholder="请输入电影标题"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="原始标题" prop="originalTitle">
            <el-input
              v-model="form.originalTitle"
              placeholder="请输入原始标题"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="电影类型" prop="genreId">
            <el-select
              v-model="form.genreId"
              placeholder="请选择电影类型"
              style="width: 100%"
            >
              <el-option
                v-for="genre in genres"
                :key="genre.id"
                :label="genre.name"
                :value="genre.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="时长" prop="duration">
            <el-input-number
              v-model="form.duration"
              :min="1"
              :max="500"
              controls-position="right"
              style="width: 100%"
              placeholder="请输入电影时长"
            >
              <template #append>分钟</template>
            </el-input-number>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="导演" prop="director">
        <el-input
          v-model="form.director"
          placeholder="请输入导演"
          maxlength="100"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="演员" prop="actors">
        <el-input
          v-model="form.actors"
          type="textarea"
          :rows="2"
          placeholder="请输入演员，多个演员用逗号分隔"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="上映日期" prop="releaseDate">
            <el-date-picker
              v-model="form.releaseDate"
              type="date"
              placeholder="选择上映日期"
              style="width: 100%"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="国家/地区" prop="country">
            <el-input
              v-model="form.country"
              placeholder="请输入国家/地区"
              maxlength="50"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="语言" prop="language">
            <el-input
              v-model="form.language"
              placeholder="请输入语言"
              maxlength="50"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="基础价格" prop="price">
            <el-input-number
              v-model="form.price"
              :min="0"
              :max="1000"
              :precision="2"
              controls-position="right"
              style="width: 100%"
              placeholder="请输入基础价格"
            >
              <template #prefix>￥</template>
            </el-input-number>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="电影描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="4"
          placeholder="请输入电影描述"
          maxlength="1000"
          show-word-limit
        />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="海报URL" prop="posterUrl">
            <el-input
              v-model="form.posterUrl"
              placeholder="请输入海报URL"
              maxlength="255"
            >
              <template #append>
                <el-button :icon="Picture" @click="handlePreviewPoster">
                  预览
                </el-button>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="预告片URL" prop="trailerUrl">
            <el-input
              v-model="form.trailerUrl"
              placeholder="请输入预告片URL"
              maxlength="255"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="状态">
            <el-switch
              v-model="form.status"
              active-text="上架"
              inactive-text="下架"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="热门">
            <el-switch
              v-model="form.isHot"
              active-text="是"
              inactive-text="否"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 海报预览 -->
      <el-form-item v-if="form.posterUrl" label="海报预览">
        <div class="poster-preview">
          <el-image
            :src="form.posterUrl"
            :alt="form.title"
            class="preview-image"
            fit="cover"
          >
            <template #error>
              <div class="preview-error">
                <el-icon><Picture /></el-icon>
                <span>图片加载失败</span>
              </div>
            </template>
          </el-image>
        </div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
        <el-button @click="handleCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script setup>
import { ref, reactive, watch } from 'vue'
// import { onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { useAdminStore } from '@/stores/admin'

const props = defineProps({
  movie: {
    type: Object,
    default: null
  },
  genres: {
    type: Array,
    default: () => []
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

const form = reactive({
  title: '',
  originalTitle: '',
  genreId: null,
  duration: null,
  director: '',
  actors: '',
  releaseDate: '',
  country: '',
  language: '',
  description: '',
  posterUrl: '',
  trailerUrl: '',
  price: 0,
  status: true,
  isHot: false
})

const rules = {
  title: [
    { required: true, message: '请输入电影标题', trigger: 'blur' },
    { min: 1, max: 200, message: '电影标题长度在 1 到 200 个字符', trigger: 'blur' }
  ],
  genreId: [
    { required: true, message: '请选择电影类型', trigger: 'change' }
  ],
  duration: [
    { required: true, message: '请输入电影时长', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入基础价格', trigger: 'blur' }
  ],
  releaseDate: [
    { required: true, message: '请选择上映日期', trigger: 'change' }
  ]
}

// 监听props.movie变化
watch(() => props.movie, (newMovie) => {
  if (newMovie) {
    Object.assign(form, {
      ...newMovie,
      genreId: newMovie.genreId || newMovie.genre?.id
    })
  } else {
    resetForm()
  }
}, { immediate: true })

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    title: '',
    originalTitle: '',
    genreId: null,
    duration: null,
    director: '',
    actors: '',
    releaseDate: '',
    country: '',
    language: '',
    description: '',
    posterUrl: '',
    trailerUrl: '',
    price: 0,
    status: true,
    isHot: false
  })

  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 预览海报
const handlePreviewPoster = () => {
  if (!form.posterUrl) {
    ElMessage.warning('请输入海报URL')
    return
  }

  // 在新窗口打开海报
  window.open(form.posterUrl, '_blank')
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  const valid = await formRef.value.validate()
  if (!valid) return

  submitting.value = true
  try {
    if (props.isEdit) {
      await adminStore.updateMovie(form.id, form)
      ElMessage.success('电影更新成功')
    } else {
      await adminStore.createMovie(form)
      ElMessage.success('电影创建成功')
    }

    emit('submit')
  } catch (error) {
    console.error(error)
    ElMessage.error(props.isEdit ? '更新电影失败' : '创建电影失败')
  } finally {
    submitting.value = false
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
.movie-form {
  .poster-preview {
    .preview-image {
      width: 200px;
      height: 280px;
      border-radius: $border-radius-base;
      border: 1px solid $border-light;
    }

    .preview-error {
      width: 200px;
      height: 280px;
      background: $bg-gray;
      border-radius: $border-radius-base;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: $text-disabled;

      .el-icon {
        font-size: 32px;
        margin-bottom: $spacing-sm;
      }
    }
  }
}

// 响应式设计
@media (max-width: $breakpoint-sm) {
  .movie-form {
    :deep(.el-form-item) {
      margin-bottom: $spacing-lg;
    }

    :deep(.el-col) {
      margin-bottom: 0;
    }
  }
}
</style>
