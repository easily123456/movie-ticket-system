<template>
  <div class="session-form">
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      label-position="left"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="电影" prop="movieId">
            <el-select
              v-model="form.movieId"
              placeholder="请选择电影"
              style="width: 100%"
              filterable
              @change="handleMovieChange"
            >
              <el-option
                v-for="movie in movies"
                :key="movie.id"
                :label="movie.title"
                :value="movie.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="放映厅" prop="hallId">
            <el-select
              v-model="form.hallId"
              placeholder="请选择放映厅"
              style="width: 100%"
              @change="handleHallChange"
            >
              <el-option
                v-for="hall in halls"
                :key="hall.id"
                :label="hall.name"
                :value="hall.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="开始时间" prop="startTime">
            <el-date-picker
              v-model="form.startTime"
              type="datetime"
              placeholder="选择开始时间"
              style="width: 100%"
              :disabled-date="disabledDate"
              :disabled-hours="disabledHours"
              :disabled-minutes="disabledMinutes"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="结束时间" prop="endTime">
            <el-date-picker
              v-model="form.endTime"
              type="datetime"
              placeholder="选择结束时间"
              style="width: 100%"
              :disabled="!form.startTime"
              :disabled-date="disabledDate"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="价格" prop="price">
            <el-input-number
              v-model="form.price"
              :min="0"
              :max="1000"
              :precision="2"
              controls-position="right"
              style="width: 100%"
              placeholder="请输入价格"
            >
              <template #prefix>￥</template>
            </el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态">
            <el-switch
              v-model="form.status"
              active-text="正常"
              inactive-text="取消"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 电影信息预览 -->
      <el-form-item v-if="selectedMovie" label="电影信息">
        <div class="movie-preview">
          <el-image
            :src="selectedMovie.posterUrl"
            :alt="selectedMovie.title"
            class="preview-poster"
            fit="cover"
          >
            <template #error>
              <div class="preview-error">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
          <div class="movie-info">
            <div class="movie-title">{{ selectedMovie.title }}</div>
            <div class="movie-meta">
              <span>{{ selectedMovie.genreName }}</span>
              <span>{{ formatDuration(selectedMovie.duration) }}</span>
              <span>{{ selectedMovie.director }}</span>
            </div>
            <div class="movie-price">
              基础价格: {{ formatPrice(selectedMovie.price) }}
            </div>
          </div>
        </div>
      </el-form-item>
      <!-- 放映厅信息预览 -->
      <el-form-item v-if="selectedHall" label="放映厅信息">
        <div class="hall-preview">
          <div class="hall-info">
            <div class="hall-name">{{ selectedHall.name }}</div>
            <div class="hall-capacity">容量: {{ selectedHall.capacity }} 座</div>
          </div>
        </div>
      </el-form-item>
      <!-- 时间冲突检查 -->
      <el-form-item v-if="timeConflict" label="时间检查">
        <el-alert
          title="时间冲突"
          type="warning"
          :description="timeConflictMessage"
          show-icon
          :closable="false"
        />
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
import { ref, reactive, watch, computed} from 'vue'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { formatPrice, formatDuration } from '@/utils'
import { useAdminStore } from '@/stores/admin'

const props = defineProps({
  session: {
    type: Object,
    default: null
  },
  movies: {
    type: Array,
    default: () => []
  },
  halls: {
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
  movieId: null,
  hallId: null,
  startTime: null,
  endTime: null,
  price: 0,
  status: true
})

const rules = {
  movieId: [
    { required: true, message: '请选择电影', trigger: 'change' }
  ],
  hallId: [
    { required: true, message: '请选择放映厅', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ]
}

// 计算属性
const selectedMovie = computed(() => {
  return props.movies.find(movie => movie.id === form.movieId) || null
})

const selectedHall = computed(() => {
  return props.halls.find(hall => hall.id === form.hallId) || null
})

const timeConflict = computed(() => {
  // 检查时间冲突的逻辑
  if (!form.startTime || !form.endTime || !form.hallId) {
    return false
  }

  // 这里应该调用API检查时间冲突
  // 暂时返回false
  return false
})

const timeConflictMessage = computed(() => {
  return '该放映厅在选定时间已有其他场次安排，请选择其他时间'
})

// 监听props.session变化
watch(() => props.session, (newSession) => {
  if (newSession) {
    Object.assign(form, {
      ...newSession,
      movieId: newSession.movieId || newSession.movie?.id,
      hallId: newSession.hallId || newSession.hall?.id,
      startTime: newSession.startTime ? new Date(newSession.startTime) : null,
      endTime: newSession.endTime ? new Date(newSession.endTime) : null
    })
  } else {
    resetForm()
  }
}, { immediate: true })

// 监听开始时间变化，自动计算结束时间
watch(() => form.startTime, (newStartTime) => {
  if (newStartTime && selectedMovie.value?.duration) {
    const endTime = new Date(newStartTime)
    endTime.setMinutes(endTime.getMinutes() + selectedMovie.value.duration + 30) // 电影时长 + 清理时间
    form.endTime = endTime
  }
})

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    movieId: null,
    hallId: null,
    startTime: null,
    endTime: null,
    price: 0,
    status: true
  })

  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 电影选择变化
const handleMovieChange = (movieId) => {
  const movie = props.movies.find(m => m.id === movieId)
  if (movie && !form.price) {
    // 如果没有设置价格，使用电影的基础价格
    form.price = movie.price
  }
}

// 放映厅选择变化
const handleHallChange = (hallId) => {
  form.hallId = hallId
  // 可以在这里添加放映厅相关的逻辑
}

// 禁用日期（只能选择今天及之后的日期）
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 禁用小时（只允许在营业时间内）
const disabledHours = () => {
  return [0, 1, 2, 3, 4, 5, 6, 7, 22, 23] // 只允许8:00-21:00
}

// 禁用分钟（只允许整点或30分钟）
const disabledMinutes = (hour) => {
  if ([8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21].includes(hour)) {
    return [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,51,52,53,54,55,56,57,58,59]
  }
  return []
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  const valid = await formRef.value.validate()
  if (!valid) return

  if (timeConflict.value) {
    ElMessage.warning('请解决时间冲突后再提交')
    return
  }

  submitting.value = true
  try {
    if (props.isEdit) {
      await adminStore.updateSession(form.id, form)
      ElMessage.success('场次更新成功')
    } else {
      await adminStore.createSession(form)
      ElMessage.success('场次创建成功')
    }

    emit('submit')
  } catch (error) {
    console.error(error)
    ElMessage.error(props.isEdit ? '更新场次失败' : '创建场次失败')
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
.session-form {
  .movie-preview {
    display: flex;
    align-items: flex-start;
    gap: $spacing-md;
    padding: $spacing-md;
    background: $bg-gray;
    border-radius: $border-radius-base;

    .preview-poster {
      width: 80px;
      height: 110px;
      border-radius: $border-radius-small;
      flex-shrink: 0;
    }

    .preview-error {
      width: 80px;
      height: 110px;
      background: $bg-white;
      border-radius: $border-radius-small;
      display: flex;
      align-items: center;
      justify-content: center;
      color: $text-disabled;

      .el-icon {
        font-size: 24px;
      }
    }

    .movie-info {
      flex: 1;

      .movie-title {
        font-weight: 600;
        color: $text-primary;
        margin-bottom: $spacing-xs;
      }

      .movie-meta {
        display: flex;
        gap: $spacing-md;
        margin-bottom: $spacing-xs;

        span {
          padding: 2px 6px;
          background: $bg-white;
          border-radius: $border-radius-small;
          color: $text-secondary;
          font-size: $font-size-small;
        }
      }

      .movie-price {
        color: $primary-color;
        font-weight: 600;
      }
    }
  }

  .hall-preview {
    .hall-info {
      padding: $spacing-md;
      background: $bg-gray;
      border-radius: $border-radius-base;

      .hall-name {
        font-weight: 600;
        color: $text-primary;
        margin-bottom: $spacing-xs;
      }

      .hall-capacity {
        color: $text-secondary;
      }
    }
  }
}

// 响应式设计
@media (max-width: $breakpoint-sm) {
  .session-form {
    :deep(.el-form-item) {
      margin-bottom: $spacing-lg;
    }

    :deep(.el-col) {
      margin-bottom: 0;
    }

    .movie-preview {
      flex-direction: column;
      align-items: center;
      text-align: center;
    }
  }
}
</style>
