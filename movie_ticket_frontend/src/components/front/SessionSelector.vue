<template>
  <div class="session-selector">
    <div class="date-selector">
      <h3 class="section-title">选择日期</h3>
      <div class="date-list">
        <div
          v-for="date in dateOptions"
          :key="date.value"
          :class="['date-item', { active: selectedDate === date.value }]"
          @click="selectDate(date.value)"
        >
          <div class="date-week">{{ date.week }}</div>
          <div class="date-day">{{ date.day }}</div>
          <div class="date-month">{{ date.month }}月</div>
        </div>
      </div>
    </div>
    <div class="session-list">
      <h3 class="section-title">选择场次</h3>
      <div v-if="sessions.length" class="sessions-container">
        <div
          v-for="session in sessions"
          :key="session.id"
          :class="['session-item', { active: selectedSession?.id === session.id }]"
          @click="selectSession(session)"
        >
          <div class="session-time">
            <div class="start-time">{{ formatTime(session.startTime) }}</div>
            <div class="end-time">{{ formatTime(session.endTime) }} 散场</div>
          </div>
          <div class="session-info">
            <div class="hall-name">{{ session.hallName }}</div>
            <div class="session-price">¥{{ session.price }}</div>
          </div>
          <div class="session-seats">
            <div class="available-seats">剩余 {{ session.availableSeats }} 座</div>
            <div class="language">{{ session.language || '国语2D' }}</div>
          </div>
        </div>
      </div>
      <div v-else class="empty-sessions">
        <el-empty description="暂无场次信息" />
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted, watch } from 'vue'
// import { useMovieStore } from '@/stores/movie'
// import { sessionApi } from '@/api'
import { ElMessage } from 'element-plus'

// const props = defineProps({
//   movieId: {
//     type: [String, Number],
//     required: true
//   }
// })

const emits = defineEmits(['session-select'])

// const movieStore = useMovieStore()
const sessions = ref([])
const selectedDate = ref('')
const selectedSession = ref(null)
const loading = ref(false)

// 生成未来7天的日期选项
const dateOptions = computed(() => {
  const options = []
  const today = new Date()

  for (let i = 0; i < 7; i++) {
    const date = new Date(today)
    date.setDate(today.getDate() + i)

    const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
    const month = date.getMonth() + 1
    const day = date.getDate()
    const week = weekDays[date.getDay()]

    // 格式化日期为 YYYY-MM-DD
    const value = date.toISOString().split('T')[0]

    let displayWeek = week
    if (i === 0) displayWeek = '今天'
    if (i === 1) displayWeek = '明天'
    if (i === 2) displayWeek = '后天'

    options.push({
      value,
      week: displayWeek,
      day: day.toString(),
      month: month.toString()
    })
  }

  return options
})

// 初始化选中今天
onMounted(() => {
  if (dateOptions.value.length > 0) {
    selectedDate.value = dateOptions.value[0].value
  }
})

// 监听日期变化，加载场次
watch(selectedDate, async (newDate) => {
  await loadSessions(newDate)
})

// 加载场次信息
const loadSessions = async (date) => {
  loading.value = true
  try {
    // 这里应该调用根据日期获取场次的API
    // 暂时使用模拟数据
    sessions.value = await getMockSessions(date)
  } catch (error) {
    ElMessage.error('加载场次信息失败')
    console.error('加载场次失败:', error)
  } finally {
    loading.value = false
  }
}

// 选择日期
const selectDate = (date) => {
  selectedDate.value = date
  selectedSession.value = null
  emits('session-select', null)
}

// 选择场次
const selectSession = (session) => {
  selectedSession.value = session
  emits('session-select', session)
}

// 格式化时间
const formatTime = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  return date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  })
}

// 模拟场次数据（实际开发中应该从API获取）
const getMockSessions = async (date) => {
  // 模拟API延迟
  await new Promise(resolve => setTimeout(resolve, 500))

  const halls = ['1号激光厅', '2号杜比厅', '3号IMAX厅', '4号巨幕厅']
  const times = ['10:00', '13:30', '16:00', '19:00', '21:30']

  return times.map((time, index) => {
    const startTime = `${date}T${time}:00`
    const endTime = `${date}T${addHours(time, 2)}:00`

    return {
      id: index + 1,
      startTime,
      endTime,
      hallName: halls[index % halls.length],
      price: 45 + (index % 3) * 5,
      availableSeats: 120 - (index * 15),
      language: index % 2 === 0 ? '国语2D' : '英语3D',
      status: true
    }
  })
}

// 辅助函数：添加小时
const addHours = (time, hours) => {
  const [hour, minute] = time.split(':').map(Number)
  let newHour = hour + hours
  if (newHour >= 24) newHour -= 24
  return newHour.toString().padStart(2, '0') + ':' + minute.toString().padStart(2, '0')
}
</script>
<style scoped lang="scss">
.session-selector {
  background: $bg-white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: $text-primary;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid $border-extra-light;
}

.date-selector {
  margin-bottom: 32px;
}

.date-list {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;

  &::-webkit-scrollbar {
    height: 4px;
  }

  &::-webkit-scrollbar-track {
    background: lighten($bg-gray, 2%);
    border-radius: 2px;
  }

  &::-webkit-scrollbar-thumb {
    background: darken($border-base, 15%);
    border-radius: 2px;
  }
}

.date-item {
  flex: 0 0 auto;
  width: 80px;
  padding: 12px 8px;
  border: 2px solid $border-lighter;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    border-color: $primary-color;
  }

  &.active {
    border-color: $primary-color;
    background: rgba($primary-color, 0.08);

    .date-week,
    .date-day {
      color: $primary-color;
      font-weight: 600;
    }
  }

  .date-week {
    font-size: 12px;
    color: $text-regular;
    margin-bottom: 4px;
  }

  .date-day {
    font-size: 20px;
    font-weight: 500;
    color: $text-primary;
    margin-bottom: 2px;
  }

  .date-month {
    font-size: 12px;
    color: $text-secondary;
  }
}

.session-list {
  .sessions-container {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
}

.session-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border: 2px solid $border-lighter;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    border-color: $primary-color;
    background: rgba($primary-color, 0.06);
  }

  &.active {
    border-color: $primary-color;
    background: rgba($primary-color, 0.08);
  }

  .session-time {
    flex: 0 0 100px;
    text-align: center;

    .start-time {
      font-size: 18px;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 4px;
    }

    .end-time {
      font-size: 12px;
      color: $text-secondary;
    }
  }

  .session-info {
    flex: 1;
    padding: 0 16px;

    .hall-name {
      font-size: 16px;
      font-weight: 500;
      color: $text-primary;
      margin-bottom: 4px;
    }

    .session-price {
      font-size: 18px;
      font-weight: 600;
      color: $danger-color;
    }
  }

  .session-seats {
    flex: 0 0 auto;
    text-align: right;

    .available-seats {
      font-size: 14px;
      color: $text-regular;
      margin-bottom: 4px;
    }

    .language {
      font-size: 12px;
      color: $text-secondary;
      padding: 2px 6px;
      background: $bg-gray;
      border-radius: 4px;
      display: inline-block;
    }
  }
}

.empty-sessions {
  padding: 40px 0;
}

@media (max-width: 768px) {
  .session-selector {
    padding: 16px;
  }

  .date-item {
    width: 70px;
    padding: 8px 4px;

    .date-day {
      font-size: 18px;
    }
  }

  .session-item {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;

    .session-time {
      flex: none;
      text-align: left;
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .session-info {
      padding: 0;
    }

    .session-seats {
      text-align: left;
      display: flex;
      justify-content: space-between;
    }
  }
}
</style>
