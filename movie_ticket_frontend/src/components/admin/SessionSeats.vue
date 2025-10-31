<template>
  <div class="session-seats" v-if="session">
    <!-- 场次信息 -->
    <div class="session-header">
      <h3 class="session-title">{{ session.movieTitle }}</h3>
      <div class="session-info">
        <span class="hall">{{ session.hallName }}</span>
        <span class="time">{{ formatDateTime(session.startTime) }}</span>
        <span class="seats-stats">
          已选: {{ selectedSeats.length }} / 剩余: {{ availableSeats }} / 总数: {{ totalSeats }}
        </span>
      </div>
    </div>
    <!-- 座位图 -->
    <div class="seats-container">
      <!-- 屏幕 -->
      <div class="screen">银幕</div>
      <!-- 座位布局 -->
      <div class="seats-layout">
        <div
          v-for="row in seatLayout.rows"
          :key="row"
          class="seat-row"
        >
          <span class="row-label">{{ getRowLabel(row) }}</span>
          <div class="seats-in-row">
            <div
              v-for="col in seatLayout.cols"
              :key="col"
              class="seat-wrapper"
            >
              <div
                class="seat"
                :class="getSeatClass(row, col)"
                @click="handleSeatClick(row, col)"
              >
                {{ col }}
              </div>
            </div>
          </div>
          <span class="row-label">{{ getRowLabel(row) }}</span>
        </div>
      </div>
      <!-- 座位图例 -->
      <div class="seat-legend">
        <div class="legend-item">
          <div class="seat-sample available"></div>
          <span>可选</span>
        </div>
        <div class="legend-item">
          <div class="seat-sample selected"></div>
          <span>已选</span>
        </div>
        <div class="legend-item">
          <div class="seat-sample occupied"></div>
          <span>已售</span>
        </div>
        <div class="legend-item">
          <div class="seat-sample unavailable"></div>
          <span>不可用</span>
        </div>
      </div>
    </div>
    <!-- 操作按钮 -->
    <div class="seats-actions">
      <el-button @click="handleClose">关闭</el-button>
      <el-button
        type="primary"
        :disabled="selectedSeats.length === 0"
        @click="handleSaveSeats"
      >
        保存座位状态
      </el-button>
    </div>
  </div>
</template>
<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { formatDateTime } from '@/utils'

const props = defineProps({
  session: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close'])

// 座位状态：0-不可用，1-可选，2-已售，3-已选（管理员选择）
const seats = ref({})
const selectedSeats = ref([])

// 座位布局配置
const seatLayout = reactive({
  rows: 8,
  cols: 10
})

// 计算属性
const totalSeats = computed(() => {
  return props.session.availableSeats || 0
})

const availableSeats = computed(() => {
  return totalSeats.value - props.session.bookedSeats
})

onMounted(() => {
  loadSeatMap()
})

// 加载座位图
const loadSeatMap = async () => {
  try {
    // 调用API获取座位图
    // const seatMap = await adminApi.getSessionSeats(props.session.id)

    // 临时模拟数据
    const mockSeats = {}
    for (let row = 1; row <= seatLayout.rows; row++) {
      for (let col = 1; col <= seatLayout.cols; col++) {
        const seatId = `${row}-${col}`
        // 随机设置一些已售和不可用座位
        const random = Math.random()
        if (random < 0.1) {
          mockSeats[seatId] = 0 // 不可用
        } else if (random < 0.3) {
          mockSeats[seatId] = 2 // 已售
        } else {
          mockSeats[seatId] = 1 // 可选
        }
      }
    }
    seats.value = mockSeats
  } catch (error) {
    console.error('加载座位图失败:', error)
  }
}

// 获取座位类名
const getSeatClass = (row, col) => {
  const seatId = `${row}-${col}`
  const status = seats.value[seatId]

  switch (status) {
    case 0:
      return 'unavailable'
    case 1:
      return 'available'
    case 2:
      return 'occupied'
    case 3:
      return 'selected'
    default:
      return 'unavailable'
  }
}

// 座位点击处理
const handleSeatClick = (row, col) => {
  const seatId = `${row}-${col}`
  const status = seats.value[seatId]

  // 只能选择可选或已选的座位
  if (status !== 1 && status !== 3) return

  if (status === 1) {
    // 选择座位
    seats.value[seatId] = 3
    selectedSeats.value.push({
      id: seatId,
      row: getRowLabel(row),
      col: col,
      rowIndex: row,
      colIndex: col
    })
  } else {
    // 取消选择
    seats.value[seatId] = 1
    selectedSeats.value = selectedSeats.value.filter(seat => seat.id !== seatId)
  }
}

// 获取行标签
const getRowLabel = (row) => {
  return String.fromCharCode(64 + row) // 1->A, 2->B, ...
}

// 关闭
const handleClose = () => {
  emit('close')
}

// 保存座位状态
const handleSaveSeats = async () => {
  try {
    // 调用API保存座位状态
    // await adminApi.updateSessionSeats(props.session.id, {
    //   unavailableSeats: selectedSeats.value.map(seat => seat.id)
    // })

    ElMessage.success('座位状态已更新')
    emit('close')
  } catch (error) {
    console.error('保存座位状态失败:', error)
    ElMessage.error('保存座位状态失败')
  }
}
</script>
<style scoped lang="scss">
.session-seats {
  .session-header {
    text-align: center;
    margin-bottom: $spacing-lg;

    .session-title {
      font-size: 20px;
      font-weight: 700;
      color: $text-primary;
      margin: 0 0 $spacing-sm 0;
    }

    .session-info {
      display: flex;
      justify-content: center;
      gap: $spacing-lg;
      color: $text-secondary;

      .seats-stats {
        font-weight: 600;
        color: $primary-color;
      }
    }
  }
}

.seats-container {
  text-align: center;
  margin-bottom: $spacing-xl;
}

.screen {
  width: 80%;
  height: 40px;
  background: linear-gradient(180deg, lighten($border-base, 20%) 0%, $border-base 100%);
  margin: 0 auto $spacing-xl;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: $text-secondary;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.seats-layout {
  margin-bottom: $spacing-xl;

  .seat-row {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: $spacing-md;

    .row-label {
      width: 30px;
      text-align: center;
      font-weight: 600;
      color: $text-primary;
      font-size: $font-size-small;
    }

    .seats-in-row {
      display: flex;
      gap: 4px;
      margin: 0 $spacing-sm;
    }
  }
}

.seat-wrapper {
  padding: 2px;
}

.seat {
  width: 30px;
  height: 30px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid transparent;

  &.available {
    background: $success-color;
    color: white;
    border-color: $success-color;

    &:hover {
      background: darken($success-color, 10%);
    }
  }

  &.selected {
    background: $primary-color;
    color: white;
    border-color: $primary-color;
  }

  &.occupied {
    background: $danger-color;
    color: white;
    border-color: $danger-color;
    cursor: not-allowed;
  }

  &.unavailable {
    background: $bg-gray;
    color: $text-disabled;
    border-color: $border-light;
    cursor: not-allowed;
  }
}

.seat-legend {
  display: flex;
  justify-content: center;
  gap: $spacing-lg;
  flex-wrap: wrap;

  .legend-item {
    display: flex;
    align-items: center;
    gap: $spacing-xs;

    .seat-sample {
      width: 20px;
      height: 20px;
      border-radius: 3px;
      border: 1px solid transparent;

      &.available {
        background: $success-color;
        border-color: $success-color;
      }

      &.selected {
        background: $primary-color;
        border-color: $primary-color;
      }

      &.occupied {
        background: $danger-color;
        border-color: $danger-color;
      }

      &.unavailable {
        background: $bg-gray;
        border-color: $border-light;
      }
    }

    span {
      font-size: $font-size-small;
      color: $text-secondary;
    }
  }
}

.seats-actions {
  display: flex;
  justify-content: center;
  gap: $spacing-md;
  padding-top: $spacing-lg;
  border-top: 1px solid $border-light;
}

// 响应式设计
@media (max-width: $breakpoint-sm) {
  .session-info {
    flex-direction: column;
    gap: $spacing-sm !important;
  }

  .seat {
    width: 25px;
    height: 25px;
    font-size: 8px;
  }

  .seat-legend {
    gap: $spacing-md;
  }
}
</style>
