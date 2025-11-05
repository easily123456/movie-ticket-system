<template>
  <div class="select-seats-page">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/movies' }">电影</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: `/movie/${movie.id}` }">
          {{ movie.title }}
        </el-breadcrumb-item>
        <el-breadcrumb-item>选择座位</el-breadcrumb-item>
      </el-breadcrumb>

      <h1 class="page-title">选择座位</h1>
      <p class="page-subtitle">{{ movie.title }} - {{ formatSessionTime(session.startTime) }}</p>
    </div>
    <div class="page-content">
      <div class="seats-container">
        <!-- 座位选择组件 -->
        <SeatSelection
          ref="seatSelectionRef"
          :session="session"
          :booked-seats="bookedSeats"
          :locked-seats="lockedSeats"
          @seats-change="handleSeatsChange"
        />
      </div>
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="selection-summary">
          <div class="summary-item">
            <span class="label">已选座位：</span>
            <span class="value">
              <span
                v-for="seat in selectedSeats"
                :key="seat.id"
                class="seat-tag"
              >
                {{ seat.row }}{{ seat.number }}
              </span>
              <span v-if="!selectedSeats.length" class="no-seats">暂未选择</span>
            </span>
          </div>
          <div class="summary-item">
            <span class="label">总计：</span>
            <span class="value total-price">¥{{ totalPrice }}</span>
          </div>
        </div>
        <div class="action-buttons">
          <el-button @click="handleBack">返回上一步</el-button>
          <el-button
            type="primary"
            :disabled="!selectedSeats.length"
            @click="goToOrderConfirm"
          >
            确认选座 ({{ selectedSeats.length }})
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMovieStore } from '@/stores/movie'
import { sessionApi } from '@/api'
import SeatSelection from '@/components/front/SeatSelection.vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const movieStore = useMovieStore()

const sessionId = route.params.sessionId
const movie = ref({})
const session = ref({})
const bookedSeats = ref([])
const lockedSeats = ref([])
const selectedSeats = ref([])
const seatSelectionRef = ref()

// 计算总价
const totalPrice = computed(() => {
  return selectedSeats.value.reduce((sum, seat) => sum + seat.price, 0).toFixed(2)
})

// 初始化数据
onMounted(async () => {
  try {
    await loadSessionDetail()
    await loadMovieDetail()
  } catch (error) {
    console.error('加载场次信息失败:', error)
    ElMessage.error('加载场次信息失败')
  }
})

// 加载场次详情
const loadSessionDetail = async () => {
  try {
    const response = await sessionApi.getSessionDetail(sessionId)
    session.value = response.data
    bookedSeats.value = response.data.bookedSeatNumbers || []
    lockedSeats.value = response.data.lockedSeatNumbers || []
  } catch (error) {
    console.error('加载场次详情失败:', error)
    throw error
  }
}

// 加载电影详情
const loadMovieDetail = async () => {
  try {
    if (session.value.movieId) {
      await movieStore.getMovieDetail(session.value.movieId)
      movie.value = movieStore.currentMovie || {}
    }
  } catch (error) {
    console.error('加载电影详情失败:', error)
    throw error
  }
}

// 格式化场次时间
const formatSessionTime = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    weekday: 'short'
  })
}

// 处理座位变化
const handleSeatsChange = (seats) => {
  selectedSeats.value = seats
}

// 返回上一步
const handleBack = () => {
  router.back()
}

// 跳转到订单确认
const goToOrderConfirm = () => {
  if (!selectedSeats.value.length) {
    ElMessage.warning('请选择座位')
    return
  }

  router.push({
    name: 'CreateOrder',
    query: {
      sessionId: sessionId,
      seatNumbers: selectedSeats.value.map(seat => seat.id).join(',')
    }
  })
}
</script>
<style scoped lang="scss">
.select-seats-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;

  .page-title {
    font-size: 28px;
    font-weight: 600;
    color: $text-primary;
    margin: 16px 0 8px 0;
  }

  .page-subtitle {
    font-size: 16px;
    color: $text-regular;
    margin: 0;
  }
}

.page-content {
  background: $bg-white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.seats-container {
  padding: 24px;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: $bg-gray;
  border-top: 1px solid $border-extra-light;
}

.selection-summary {
  .summary-item {
    margin-bottom: 8px;

    &:last-child {
      margin-bottom: 0;
    }

    .label {
      font-size: 14px;
      color: $text-regular;
    }

    .value {
      font-size: 14px;
      color: $text-primary;
      font-weight: 500;

      &.total-price {
        color: $danger-color;
        font-size: 18px;
        font-weight: 600;
      }
    }

    .seat-tag {
      display: inline-block;
      padding: 4px 8px;
      background: rgba($primary-color, 0.08);
      color: $primary-color;
      border-radius: 4px;
      margin-right: 8px;
      font-size: 12px;
      font-weight: 500;
    }

    .no-seats {
      color: $text-secondary;
    }
  }
}

.action-buttons {
  display: flex;
  gap: 12px;

  .el-button {
    min-width: 120px;
  }
}

@media (max-width: 768px) {
  .select-seats-page {
    padding: 16px;
  }

  .seats-container {
    padding: 16px;
  }

  .action-bar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .action-buttons {
    .el-button {
      flex: 1;
    }
  }
}
</style>
