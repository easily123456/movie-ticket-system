<template>
  <div class="session-seats" v-if="session">
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
    <div class="seats-container">
      <div class="screen">银幕</div>
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

const seats = ref({})
const selectedSeats = ref([])

const seatLayout = reactive({
  rows: 8,
  cols: 10
})

const totalSeats = computed(() => {
  return props.session.availableSeats || 0
})

const availableSeats = computed(() => {
  return totalSeats.value - props.session.bookedSeats
})

onMounted(() => {
  loadSeatMap()
})

const loadSeatMap = async () => {
  try {
    const mockSeats = {}
    for (let row = 1; row <= seatLayout.rows; row++) {
      for (let col = 1; col <= seatLayout.cols; col++) {
        const seatId = `${row}-${col}`
        const random = Math.random()
        if (random < 0.1) {
          mockSeats[seatId] = 0
        } else if (random < 0.3) {
          mockSeats[seatId] = 2
        } else {
          mockSeats[seatId] = 1
        }
      }
    }
    seats.value = mockSeats
  } catch (error) {
    console.error('加载座位图失败:', error)
  }
}

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

const handleSeatClick = (row, col) => {
  const seatId = `${row}-${col}`
  const status = seats.value[seatId]

  if (status !== 1 && status !== 3) return

  if (status === 1) {
    seats.value[seatId] = 3
    selectedSeats.value.push({
      id: seatId,
      row: getRowLabel(row),
      col: col,
      rowIndex: row,
      colIndex: col
    })
  } else {
    seats.value[seatId] = 1
    selectedSeats.value = selectedSeats.value.filter(seat => seat.id !== seatId)
  }
}

const getRowLabel = (row) => {
  return String.fromCharCode(64 + row)
}

const handleClose = () => {
  emit('close')
}

const handleSaveSeats = async () => {
  try {
    ElMessage.success('座位状态已更新')
    emit('close')
  } catch (error) {
    console.error('保存座位状态失败:', error)
    ElMessage.error('保存座位状态失败')
  }
}
</script>
<style scoped lang="scss">
@use "sass:color";
@use '@/assets/styles/variables.scss';

.session-seats {
  .session-header {
    text-align: center;
    margin-bottom: variables.$spacing-lg;

    .session-title {
      font-size: 20px;
      font-weight: 700;
      color: variables.$text-primary;
      margin: 0 0 variables.$spacing-sm 0;
    }

    .session-info {
      display: flex;
      justify-content: center;
      gap: variables.$spacing-lg;
      color: variables.$text-secondary;

      .seats-stats {
        font-weight: 600;
        color: variables.$primary-color;
      }
    }
  }
}

.seats-container {
  text-align: center;
  margin-bottom: variables.$spacing-xl;
}

.screen {
  width: 80%;
  height: 40px;
  background: linear-gradient(180deg, color.adjust(variables.$border-base, $lightness: 20%) 0%, variables.$border-base 100%);
  margin: 0 auto variables.$spacing-xl;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: variables.$text-secondary;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.seats-layout {
  margin-bottom: variables.$spacing-xl;

  .seat-row {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: variables.$spacing-md;

    .row-label {
      width: 30px;
      text-align: center;
      font-weight: 600;
      color: variables.$text-primary;
      font-size: variables.$font-size-small;
    }

    .seats-in-row {
      display: flex;
      gap: 4px;
      margin: 0 variables.$spacing-sm;
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
    background: variables.$success-color;
    color: white;
    border-color: variables.$success-color;

    &:hover {
      background: color.adjust(variables.$success-color, $lightness: -10%);
    }
  }

  &.selected {
    background: variables.$primary-color;
    color: white;
    border-color: variables.$primary-color;
  }

  &.occupied {
    background: variables.$danger-color;
    color: white;
    border-color: variables.$danger-color;
    cursor: not-allowed;
  }

  &.unavailable {
    background: variables.$bg-gray;
    color: variables.$text-disabled;
    border-color: variables.$border-light;
    cursor: not-allowed;
  }
}

.seat-legend {
  display: flex;
  justify-content: center;
  gap: variables.$spacing-lg;
  flex-wrap: wrap;

  .legend-item {
    display: flex;
    align-items: center;
    gap: variables.$spacing-xs;

    .seat-sample {
      width: 20px;
      height: 20px;
      border-radius: 3px;
      border: 1px solid transparent;

      &.available {
        background: variables.$success-color;
        border-color: variables.$success-color;
      }

      &.selected {
        background: variables.$primary-color;
        border-color: variables.$primary-color;
      }

      &.occupied {
        background: variables.$danger-color;
        border-color: variables.$danger-color;
      }

      &.unavailable {
        background: variables.$bg-gray;
        border-color: variables.$border-light;
      }
    }

    span {
      font-size: variables.$font-size-small;
      color: variables.$text-secondary;
    }
  }
}

.seats-actions {
  display: flex;
  justify-content: center;
  gap: variables.$spacing-md;
  padding-top: variables.$spacing-lg;
  border-top: 1px solid variables.$border-light;
}

@media (max-width: variables.$breakpoint-sm) {
  .session-info {
    flex-direction: column;
    gap: variables.$spacing-sm !important;
  }

  .seat {
    width: 25px;
    height: 25px;
    font-size: 8px;
  }

  .seat-legend {
    gap: variables.$spacing-md;
  }
}
</style>
