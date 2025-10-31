<template>
  <div class="seat-selection-page">
    <div class="container">
      <!-- 页面头部 -->
      <div class="page-header">
        <h1 class="page-title">选座购票</h1>
        <el-steps :active="currentStep" align-center>
          <el-step title="选择场次" />
          <el-step title="选择座位" />
          <el-step title="确认订单" />
          <el-step title="支付" />
        </el-steps>
      </div>
      <div class="selection-content">
        <!-- 场次信息 -->
        <section class="session-info-section">
          <div class="session-info-card">
            <el-image
              :src="movie.posterUrl"
              :alt="movie.title"
              class="movie-poster"
              fit="cover"
            />
            <div class="movie-details">
              <h2 class="movie-title">{{ movie.title }}</h2>
              <div class="movie-meta">
                <span>{{ movie.genreName }}</span>
                <span>{{ formatDuration(movie.duration) }}</span>
                <span>{{ movie.country }}</span>
              </div>
              <div class="session-details">
                <div class="detail-item">
                  <span class="label">场次时间：</span>
                  <span class="value">{{ formatSessionTime(session.startTime) }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">放映厅：</span>
                  <span class="value">{{ session.hallName }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">票价：</span>
                  <span class="value price">{{ formatPrice(session.price) }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>
        <!-- 座位选择 -->
        <section class="seat-selection-section">
          <div class="seat-selection-container">
            <!-- 屏幕 -->
            <div class="screen">银幕</div>
            <!-- 座位图 -->
            <div class="seat-map">
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
                <span>不可选</span>
              </div>
            </div>
          </div>
          <!-- 选座信息 -->
          <div class="selection-summary">
            <h3 class="summary-title">已选座位</h3>
            <div v-if="selectedSeats.length === 0" class="no-seats">
              尚未选择座位
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
        </section>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Close } from '@element-plus/icons-vue'
import { formatPrice, formatDuration, formatDateTime } from '@/utils'
import { useSessionStore } from '@/stores/session'
import { useOrderStore } from '@/stores/order'
import { useMovieStore } from '@/stores/movie'

const route = useRoute()
const router = useRouter()
const sessionStore = useSessionStore()
const orderStore = useOrderStore()
const movieStore = useMovieStore()

const currentStep = ref(1)
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

const loadSessionDetail = async () => {
  const sessionId = route.params.sessionId
  try {
    const sessionDetail = await sessionStore.getSessionDetail(sessionId)
    session.value = sessionDetail

    // 获取电影信息
    const movieDetail = await movieStore.fetchMovieDetail(sessionDetail.movieId)
    movie.value = movieDetail
  } catch (error) {
    ElMessage.error('加载场次信息失败')
    console.error('加载场次信息失败:', error)
  }
}

const loadSeatMap = async () => {
  // 模拟座位数据，实际应该从后端获取
  //const hallId = session.value.hallId
  try {
    // 这里应该调用API获取座位图
    // const seatMap = await sessionApi.getSeatMap(hallId)
    // seats.value = seatMap

    // 临时模拟数据
    const mockSeats = {}
    for (let row = 1; row <= seatLayout.rows; row++) {
      for (let col = 1; col <= seatLayout.cols; col++) {
        const seatId = `${row}-${col}`
        // 随机设置一些已售座位
        mockSeats[seatId] = Math.random() > 0.8 ? 2 : 1
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

  // 只能选择可选座位
  if (status !== 1 && status !== 3) return

  if (status === 1) {
    // 选择座位
    if (selectedSeats.value.length >= 6) {
      ElMessage.warning('最多只能选择6个座位')
      return
    }
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

const removeSeat = (seat) => {
  seats.value[seat.id] = 1
  selectedSeats.value = selectedSeats.value.filter(s => s.id !== seat.id)
}

const getRowLabel = (row) => {
  return String.fromCharCode(64 + row) // 1->A, 2->B, ...
}

const formatSessionTime = (time) => {
  return formatDateTime(time, 'MM月DD日 HH:mm')
}

const handleCreateOrder = async () => {
  if (!selectedSeats.value.length) return

  creatingOrder.value = true
  try {
    const orderData = {
      sessionId: session.value.id,
      seatNumbers: selectedSeats.value.map(seat => `${seat.row}${seat.col}`),
      seatCount: selectedSeats.value.length,
      totalPrice: totalPrice.value
    }

    const order = await orderStore.createOrder(orderData)
    ElMessage.success('订单创建成功')
    router.push(`/order/${order.id}/confirm`)
  } catch (error) {
    ElMessage.error('创建订单失败')
    console.error('创建订单失败:', error)
  } finally {
    creatingOrder.value = false
  }
}
</script>
<style scoped lang="scss">
.seat-selection-page {
  padding: $spacing-lg 0 $spacing-xxl;
  min-height: 100vh;
  background: $bg-gray;
}

.page-header {
  background: $bg-white;
  padding: $spacing-xl;
  border-radius: $border-radius-base;
  box-shadow: $shadow-base;
  margin-bottom: $spacing-xl;

  .page-title {
    font-size: 28px;
    font-weight: 700;
    text-align: center;
    margin-bottom: $spacing-lg;
    color: $text-primary;
  }
}

.selection-content {
  display: grid;
  gap: $spacing-xl;
}

.session-info-section {
  .session-info-card {
    display: flex;
    gap: $spacing-lg;
    background: $bg-white;
    padding: $spacing-lg;
    border-radius: $border-radius-base;
    box-shadow: $shadow-base;

    .movie-poster {
      width: 120px;
      height: 160px;
      border-radius: $border-radius-base;
      flex-shrink: 0;
    }

    .movie-details {
      flex: 1;

      .movie-title {
        font-size: 24px;
        font-weight: 700;
        margin-bottom: $spacing-sm;
        color: $text-primary;
      }

      .movie-meta {
        display: flex;
        gap: $spacing-md;
        margin-bottom: $spacing-lg;
        color: $text-secondary;

        span {
          padding: $spacing-xs $spacing-sm;
          background: $bg-gray;
          border-radius: $border-radius-small;
          font-size: $font-size-small;
        }
      }

      .session-details {
        .detail-item {
          margin-bottom: $spacing-sm;

          .label {
            font-weight: 600;
            color: $text-primary;
            margin-right: $spacing-sm;
          }

          .value {
            color: $text-regular;

            &.price {
              color: $primary-color;
              font-weight: 700;
              font-size: $font-size-large;
            }
          }
        }
      }
    }
  }
}

.seat-selection-section {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: $spacing-xl;

  @media (max-width: $breakpoint-lg) {
    grid-template-columns: 1fr;
  }
}

.seat-selection-container {
  background: $bg-white;
  padding: $spacing-xl;
  border-radius: $border-radius-base;
  box-shadow: $shadow-base;
  text-align: center;
}

.screen {
  width: 80%;
  height: 40px;
  background: linear-gradient(180deg, lighten($border-base, 20%) 0%, $border-base 100%);
  margin: 0 auto 16px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: $text-secondary;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.seat-map {
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

.selection-summary {
  background: $bg-white;
  padding: $spacing-xl;
  border-radius: $border-radius-base;
  box-shadow: $shadow-base;
  height: fit-content;
  position: sticky;
  top: $spacing-xl;

  .summary-title {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: $spacing-lg;
    color: $text-primary;
    text-align: center;
  }

  .no-seats {
    text-align: center;
    color: $text-secondary;
    padding: $spacing-xl 0;
  }

  .selected-seats {
    margin-bottom: $spacing-lg;

    .selected-seat-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: $spacing-sm $spacing-md;
      background: $bg-gray;
      border-radius: $border-radius-base;
      margin-bottom: $spacing-sm;

      .seat-position {
        font-weight: 600;
        color: $text-primary;
      }

      .seat-price {
        color: $primary-color;
        font-weight: 600;
      }
    }
  }

  .summary-total {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: $spacing-lg 0;
    border-top: 1px solid $border-light;
    border-bottom: 1px solid $border-light;
    margin-bottom: $spacing-lg;

    .total-label {
      font-size: 16px;
      font-weight: 600;
      color: $text-primary;
    }

    .total-price {
      font-size: 24px;
      font-weight: 700;
      color: $primary-color;
    }
  }

  .el-button {
    width: 100%;
    height: 50px;
    font-size: 16px;
    font-weight: 600;
  }
}
</style>
