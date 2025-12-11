<template>
  <div class="seat-selection">
    <div class="page-header">
      <el-page-header @back="handleBack">
        <template #content>
          <span class="header-title">选座购票</span>
        </template>
      </el-page-header>
    </div>

    <div class="content-container">
      <!-- 电影信息 -->
      <div class="movie-info-card">
        <div class="movie-poster">
          <img
            v-if="movie.posterUrl || movie.poster"
            :src="movie.posterUrl || movie.poster"
            :alt="movie.title"
            class="poster-image"
          />
          <div v-else class="poster-placeholder">
            <el-icon><Picture /></el-icon>
          </div>
        </div>
        <div class="movie-details">
          <h2 class="movie-title">{{ movie.title }}</h2>
          <div class="session-info">
            <div class="info-item">
              <el-icon><Calendar /></el-icon>
              <span>{{ formatDateTime(session.startTime) }}</span>
            </div>
            <div class="info-item">
              <el-icon><Location /></el-icon>
              <span>{{ session.hallName }}</span>
            </div>
            <div class="info-item">
              <el-icon><Film /></el-icon>
              <span>{{ formatDuration(movie.duration) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 座位选择区域 -->
      <div class="seat-selection-area">
        <div class="screen-indicator">银幕</div>
        <div class="seats-grid">
          <div
            v-for="(row, rowIndex) in seatLayout.rows"
            :key="rowIndex"
            class="seat-row"
          >
          <!-- 行标签和座位 -->
            <div class="row-label">{{ getRowLabel(rowIndex + 1) }}</div>
            <div class="seats-in-row">
              <div
                v-for="(col, colIndex) in seatLayout.cols"
                :key="colIndex"
                class="seat-wrapper"
              >
                <div
                  class="seat"
                  :class="getSeatClass(rowIndex + 1, colIndex + 1)"
                  @click="handleSeatClick(rowIndex + 1, colIndex + 1)"
                >
                  {{ colIndex + 1 }}
                </div>
                <!-- 根据座位状态显示不同的样式 -->
              </div>
            </div>
            <div class="row-label">{{ getRowLabel(rowIndex + 1) }}</div>
          </div>
        </div>

        <!-- 座位图例 -->
        <div class="seat-legend">
          <div class="legend-item">
            <div class="legend-sample available"></div>
            <span>可选座位</span>
          </div>
          <div class="legend-item">
            <div class="legend-sample selected"></div>
            <span>已选座位</span>
          </div>
          <!-- 已售座位图例已移除，保持界面简洁（座位仍按已售样式显示） -->
          <div class="legend-item">
            <div class="legend-sample unavailable"></div>
            <span>不可选座位</span>
          </div>
        </div>
      </div>

      <!-- 选座信息和操作 -->
      <div class="selection-summary">
        <div class="selected-seats-info">
          <h3 class="summary-title">已选座位</h3>
          <div v-if="selectedSeats.length === 0" class="no-seats-selected">
            请先选择座位
          </div>
          <div v-else class="selected-seats">
            <div
              v-for="seat in selectedSeats"
              :key="seat.id"
              class="selected-seat-item"
            >
              <span class="seat-position">{{ seat.row }}{{ seat.col }}</span>
              <span class="seat-price">{{ formatPrice(session.price) }}</span>
              <el-button
                link
                type="danger"
                :icon="Close"
                @click="removeSeat(seat)"
              />
            </div>
          </div>
          <div class="summary-total">
            <span class="total-label">总计：</span>
            <span class="total-price">{{ formatPrice(totalPrice) }}</span>
          </div>
        </div>
        <el-button
          type="primary"
          size="large"
          :disabled="selectedSeats.length === 0"
          @click="handleCreateOrder"
          :loading="creatingOrder"
        >
          确认选座
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Close, Calendar, Location, Film, Picture } from '@element-plus/icons-vue'
import { formatPrice, formatDuration, formatDateTime } from '@/utils'
import { useSessionStore } from '@/stores/session'
import { useOrderStore } from '@/stores/order'
import { useMovieStore } from '@/stores/movie'

const route = useRoute()
const router = useRouter()
const sessionStore = useSessionStore()
const orderStore = useOrderStore()
const movieStore = useMovieStore()

// const currentStep = ref(1)
const creatingOrder = ref(false)

const session = ref({})
const movie = ref({})
const seatLayout = reactive({
  rows: 8,
  cols: 10
})

// 座位状态：0-不可用，1-可选，2-已售，3-已选
const seats = ref({})
const selectedSeats = ref([])

// 计算总价
const totalPrice = computed(() => {
  return selectedSeats.value.length * (session.value.price || 0)
})

onMounted(async () => {
  await loadSessionDetail()
  await loadSeatMap()
})

const loadSeatMap = async () => {
  try {
    // 优先使用后端返回的座位布局和已占用/锁定座位
    const detail = session.value || {}
    const layoutFromServer = detail.seatLayout || {}

    const rows = layoutFromServer.rows || seatLayout.rows
    const cols = layoutFromServer.cols || seatLayout.cols

    // 可能后端会返回不可用座位列表，字段名可能为 disabledSeats/unavailableSeats
    const disabledSeats = layoutFromServer.disabledSeats || layoutFromServer.unavailableSeats || []

    const booked = detail.bookedSeatNumbers || []
    const locked = detail.lockedSeatNumbers || []

    const newSeats = {}
    for (let row = 1; row <= rows; row++) {
      for (let col = 1; col <= cols; col++) {
        const seatKey = `${getRowLabel(row)}${col}` // e.g. A1

        if (disabledSeats.includes(seatKey)) {
          newSeats[seatKey] = 0 // 不可用
        } else if (booked.includes(seatKey)) {
          newSeats[seatKey] = 2 // 已售
        } else if (locked.includes(seatKey)) {
          // 锁定（待支付）视为暂时不可选
          newSeats[seatKey] = 0 // 不可用
        } else {
          newSeats[seatKey] = 1 // 可选
        }
      }
    }

    seats.value = newSeats
    // 如果后端提供了具体的行/列信息，更新本地布局（用于渲染）
    seatLayout.rows = rows
    seatLayout.cols = cols
  } catch (error) {
    console.error('加载座位图失败:', error)
  }
}

const loadSessionDetail = async () => {
  const sessionId = route.params.sessionId
  try {
    const sessionDetail = await sessionStore.getSessionDetail(sessionId)
    session.value = sessionDetail

    // 获取电影信息（movie store 暴露 getMovieDetail）
    const response = await movieStore.getMovieDetail(sessionDetail.movieId)
    // movieStore.getMovieDetail 会把当前电影保存到 currentMovie
    movie.value = movieStore.currentMovie || (response && response.data ? response.data : response)
  } catch (error) {
    ElMessage.error('加载场次信息失败')
    console.error('加载场次信息失败:', error)
  }
}



// 获取座位类名
const getSeatClass = (row, col) => {
  const seatKey = `${getRowLabel(row)}${col}` // e.g. A1
  const status = seats.value[seatKey]

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

// 获取行标签
const getRowLabel = (rowIndex) => {
  return String.fromCharCode(64 + rowIndex) // 1->A, 2->B, ...
}

// 座位点击处理
const handleSeatClick = (row, col) => {
  const seatKey = `${getRowLabel(row)}${col}`
  const status = seats.value[seatKey]

  // 只能选择可选或已选的座位
  if (status !== 1 && status !== 3) return

  if (status === 1) {
    // 选择座位
    seats.value[seatKey] = 3
    selectedSeats.value.push({
      id: seatKey,
      row: getRowLabel(row),
      col: col
    })
  } else {
    // 取消选择
    seats.value[seatKey] = 1
    selectedSeats.value = selectedSeats.value.filter(seat => seat.id !== seatKey)
  }
}

// 移除座位
const removeSeat = (seat) => {
  const seatId = seat.id
  // 有可能 seatId 是 A1 形式
  seats.value[seatId] = 1
  selectedSeats.value = selectedSeats.value.filter(s => s.id !== seatId)
}

// 创建订单
const handleCreateOrder = async () => {
  creatingOrder.value = true
  try {
    const seatNumbers = selectedSeats.value.map(seat => `${seat.row}${seat.col}`)
    const orderData = {
      sessionId: session.value.id,
      seatNumbers: seatNumbers
    }

  const created = await orderStore.createOrder(orderData)
  ElMessage.success('订单创建成功')

  // 跳转到订单确认页面（orderStore.createOrder 返回已创建订单对象）
  router.push(`/order/${created.id}`)
  } catch (error) {
    console.error('创建订单失败:', error)
    ElMessage.error('创建订单失败')
  } finally {
    creatingOrder.value = false
  }
}

// 返回上一页
const handleBack = () => {
  router.back()
}
</script>

<style scoped lang="scss">
@use "sass:color";
@use '@/assets/styles/variables.scss';

.seat-selection {
  min-height: calc(100vh - 160px);
  background: variables.$bg-color;
  padding: variables.$spacing-lg;

  .page-header {
    margin-bottom: variables.$spacing-lg;
    background: variables.$bg-white;
    padding: variables.$spacing-md variables.$spacing-lg;
    border-radius: variables.$border-radius-base;
    box-shadow: variables.$shadow-base;

    .header-title {
      font-size: 18px;
      font-weight: 600;
    }
  }

  .content-container {
    display: grid;
    grid-template-columns: 1fr 350px;
    gap: variables.$spacing-lg;

    .movie-info-card {
      background: variables.$bg-white;
      border-radius: variables.$border-radius-base;
      padding: variables.$spacing-lg;
      display: flex;
      gap: variables.$spacing-lg;
      box-shadow: variables.$shadow-base;

      .movie-poster {
        flex-shrink: 0;

        .poster-image {
          width: 120px;
          height: 160px;
          border-radius: variables.$border-radius-small;
          object-fit: cover;
        }

        .poster-placeholder {
          width: 120px;
          height: 160px;
          border-radius: variables.$border-radius-small;
          background: variables.$bg-gray;
          display: flex;
          align-items: center;
          justify-content: center;
          color: variables.$text-placeholder;

          .el-icon {
            font-size: 32px;
          }
        }
      }

      .movie-details {
        flex: 1;

        .movie-title {
          font-size: 20px;
          font-weight: 700;
          color: variables.$text-primary;
          margin: 0 0 variables.$spacing-md 0;
        }

        .session-info {
          display: flex;
          flex-direction: column;
          gap: variables.$spacing-sm;

          .info-item {
            display: flex;
            align-items: center;
            gap: variables.$spacing-sm;
            color: variables.$text-secondary;

            .el-icon {
              font-size: 16px;
            }
          }
        }
      }
    }

    .seat-selection-area {
      grid-column: 1;

      .screen-indicator {
        text-align: center;
        margin-bottom: variables.$spacing-xl;
        font-weight: 600;
        color: variables.$text-secondary;
      }

      .seats-grid {
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

      .seat-legend {
        display: flex;
        justify-content: center;
        gap: variables.$spacing-lg;
        margin-top: variables.$spacing-xl;
        flex-wrap: wrap;

        .legend-item {
          display: flex;
          align-items: center;
          gap: variables.$spacing-xs;

          .legend-sample {
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
    }

    .selection-summary {
      grid-column: 2;
      background: variables.$bg-white;
      border-radius: variables.$border-radius-base;
      padding: variables.$spacing-lg;
      display: flex;
      flex-direction: column;
      box-shadow: variables.$shadow-base;

      .selected-seats-info {
        flex: 1;
        margin-bottom: variables.$spacing-lg;

        .summary-title {
          font-size: 18px;
          font-weight: 600;
          color: variables.$text-primary;
          margin: 0 0 variables.$spacing-md 0;
        }

        .no-seats-selected {
          text-align: center;
          color: variables.$text-secondary;
          padding: variables.$spacing-xl 0;
        }

        .selected-seats {
          max-height: 300px;
          overflow-y: auto;
          margin-bottom: variables.$spacing-lg;

          .selected-seat-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: variables.$spacing-sm variables.$spacing-md;
            background: variables.$bg-gray;
            border-radius: variables.$border-radius-small;
            margin-bottom: variables.$spacing-sm;

            &:last-child {
              margin-bottom: 0;
            }

            .seat-position {
              font-weight: 600;
              color: variables.$text-primary;
            }

            .seat-price {
              color: variables.$primary-color;
              font-weight: 600;
            }
          }
        }

        .summary-total {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding-top: variables.$spacing-md;
          border-top: 1px solid variables.$border-light;

          .total-label {
            font-size: 16px;
            font-weight: 600;
            color: variables.$text-primary;
          }

          .total-price {
            font-size: 20px;
            font-weight: 700;
            color: variables.$primary-color;
          }
        }
      }

      .el-button {
        width: 100%;
      }
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

// 响应式设计
@media (max-width: variables.$breakpoint-md) {
  .seat-selection {
    padding: variables.$spacing-md;

    .content-container {
      grid-template-columns: 1fr;
      gap: variables.$spacing-md;

      .movie-info-card {
        flex-direction: column;

        .movie-poster {
          .poster-image,
          .poster-placeholder {
            width: 100%;
            height: 200px;
          }
        }
      }

      .seat-selection-area,
      .selection-summary {
        grid-column: 1;
      }
    }
  }
}

@media (max-width: variables.$breakpoint-sm) {
  .seat-selection {
    padding: variables.$spacing-sm;

    .content-container {
      .seat-selection-area {
        .seats-grid {
          .seat-row {
            .seats-in-row {
              gap: 2px;
            }
          }
        }
      }

      .selection-summary {
        padding: variables.$spacing-md;
      }
    }
  }

  .seat {
    width: 25px;
    height: 25px;
    font-size: 8px;
  }
}
</style>
