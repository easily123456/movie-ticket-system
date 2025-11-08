<template>
  <div class="session-selector">
    <div class="selector-header">
      <h3 class="selector-title">场次选择</h3>
      <div class="date-tabs">
        <el-tabs v-model="activeDate" @tab-change="handleDateChange">
          <el-tab-pane
            v-for="date in availableDates"
            :key="date.value"
            :label="date.label"
            :name="date.value"
          />
        </el-tabs>
      </div>
    </div>

    <div class="sessions-container">
      <div
        v-for="session in filteredSessions"
        :key="session.id"
        class="session-item"
        @click="selectSession(session)"
      >
        <div class="session-time">
          <div class="start-time">{{ formatTime(session.startTime) }}</div>
          <div class="end-time">{{ formatTime(session.endTime) }}</div>
        </div>
        <div class="session-info">
          <div class="hall-name">{{ session.hallName }}</div>
          <div class="language-type">{{ session.language || '国语' }}{{ session.screenType || '2D' }}</div>
        </div>
        <div class="session-price">¥{{ session.price }}</div>
        <div class="session-seats">
          <div class="available-seats">剩余 {{ session.availableSeats }} 座</div>
          <div class="language">{{ session.language || '国语2D' }}</div>
        </div>
      </div>
      <div v-if="filteredSessions.length === 0" class="empty-sessions">
        <el-empty description="暂无场次信息" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
// import { ElMessage } from 'element-plus'
import { formatTime, formatDate } from '@/utils'

const props = defineProps({
  movieId: {
    type: [String, Number],
    required: true
  },
  sessions: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['select'])

const activeDate = ref('')
const sessionData = ref([])

// 计算可用日期
const availableDates = computed(() => {
  const dates = []
  // 使用固定基准日期：2025年11月10日，后续天数按 +i 递增
  const base = new Date(2025, 10, 10) // 月份从0开始，10表示11月

  for (let i = 0; i < 7; i++) {
    const date = new Date(base)
    date.setDate(base.getDate() + i)
    const dateStr = formatDate(date)
    const label = `${date.getMonth() + 1}月${date.getDate()}日`

    dates.push({
      value: dateStr,
      label: label
    })
  }

  return dates
})

// 过滤场次
const filteredSessions = computed(() => {
  if (!activeDate.value || sessionData.value.length === 0) {
    return []
  }

  return sessionData.value.filter(session => {
    const sessionDate = new Date(session.startTime)
    const sessionDateStr = formatDate(sessionDate)
    return sessionDateStr === activeDate.value
  })
})

// 监听电影ID和场次数据变化
watch(() => [props.movieId, props.sessions], ([newMovieId, newSessions]) => {
  if (newMovieId && newSessions.length > 0) {
    sessionData.value = newSessions
    // 默认选中第一个日期
    if (availableDates.value.length > 0 && !activeDate.value) {
      activeDate.value = availableDates.value[0].value
    }
  }
}, { immediate: true })

// 处理日期变化
const handleDateChange = (date) => {
  activeDate.value = date
}

// 选择场次
const selectSession = (session) => {
  emit('select', session)
}

// 格式化时间
// const formatTime = (timeString) => {
//   if (!timeString) return ''
//   const date = new Date(timeString)
//   return date.toLocaleTimeString('zh-CN', {
//     hour: '2-digit',
//     minute: '2-digit'
//   })
// }
</script>

<style scoped lang="scss">
@use 'sass:color';
.session-selector {
  background: $bg-white;
  border-radius: $border-radius-base;
  box-shadow: $shadow-light;

  .selector-header {
    padding: $spacing-lg;

    .selector-title {
      font-size: $font-size-large;
      font-weight: 600;
      color: $text-primary;
      margin: 0 0 $spacing-md 0;
    }

    .date-tabs {
      :deep(.el-tabs__header) {
        margin-bottom: 0;
      }

      :deep(.el-tabs__nav-wrap)::after {
        display: none;
      }

      :deep(.el-tabs__item) {
        font-size: $font-size-small;
        padding: 0 $spacing-md;
        height: 32px;
        line-height: 32px;
      }
    }
  }

  .sessions-container {
    border-top: 1px solid $border-light;

    .session-item {
      display: flex;
      align-items: center;
      padding: $spacing-lg;
      border-bottom: 1px solid $border-light;
      cursor: pointer;
      transition: $transition-base;

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        background: color.adjust($bg-gray, $lightness: 2%);
      }

      .session-time {
        width: 80px;
        text-align: center;

        .start-time {
          font-size: 18px;
          font-weight: 600;
          color: $text-primary;
        }

        .end-time {
          font-size: $font-size-small;
          color: $text-secondary;
        }
      }

      .session-info {
        flex: 1;
        margin-left: $spacing-lg;

        .hall-name {
          font-weight: 500;
          color: $text-primary;
          margin-bottom: $spacing-xs;
        }

        .language-type {
          font-size: $font-size-small;
          color: $text-secondary;
        }
      }

      .session-price {
        font-size: 18px;
        font-weight: 600;
        color: $primary-color;
        margin-right: $spacing-lg;
      }

      .session-seats {
        text-align: right;

        .available-seats {
          font-weight: 500;
          color: $success-color;
          margin-bottom: $spacing-xs;
        }

        .language {
          font-size: $font-size-small;
          color: $text-secondary;
        }
      }
    }

    .empty-sessions {
      padding: $spacing-xl;
      text-align: center;
    }
  }
}
</style>
