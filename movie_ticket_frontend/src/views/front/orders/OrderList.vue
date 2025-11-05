<template>
  <div class="order-list-page">
    <div class="page-header">
      <h1 class="page-title">我的订单</h1>
      <p class="page-subtitle">查看和管理您的电影票订单</p>
    </div>
    <div class="page-content">
      <!-- 订单筛选 -->
      <div class="order-filter">
        <el-radio-group v-model="filterStatus" @change="handleFilterChange">
          <el-radio-button label="">全部订单</el-radio-button>
          <el-radio-button label="PENDING">待支付</el-radio-button>
          <el-radio-button label="PAID">已支付</el-radio-button>
          <el-radio-button label="CANCELLED">已取消</el-radio-button>
        </el-radio-group>
      </div>
      <!-- 订单列表 -->
      <div class="order-list">
        <div v-if="orderStore.orders.length" class="orders-container">
          <div
            v-for="order in orderStore.orders"
            :key="order.id"
            class="order-item"
          >
            <div class="order-header">
              <div class="order-info">
                <span class="order-no">订单号：{{ order.orderNo }}</span>
                <span class="order-time">{{ formatTime(order.createTime) }}</span>
              </div>
              <div class="order-status">
                <el-tag :type="getStatusType(order.status)">
                  {{ getStatusText(order.status) }}
                </el-tag>
              </div>
            </div>
            <div class="order-content">
              <div class="movie-info">
                <div class="movie-poster">
                  <img :src="order.moviePoster" :alt="order.movieTitle" />
                </div>
                <div class="movie-details">
                  <h3 class="movie-title">{{ order.movieTitle }}</h3>
                  <div class="session-info">
                    <span class="info-item">{{ formatSessionTime(order.sessionTime) }}</span>
                    <span class="info-item">{{ order.hallName }}</span>
                  </div>
                  <div class="seats-info">
                    <span
                      v-for="seat in order.seatNumbers"
                      :key="seat"
                      class="seat-tag"
                    >
                      {{ seat }}
                    </span>
                    <span class="ticket-count">{{ order.seatCount }}张</span>
                  </div>
                </div>
              </div>
              <div class="order-actions">
                <div class="order-price">
                  <span class="price">¥{{ order.totalPrice }}</span>
                </div>
                <div class="action-buttons">
                  <el-button
                    v-if="order.status === 'PENDING'"
                    type="primary"
                    @click="handlePayOrder(order.id)"
                  >
                    立即支付
                  </el-button>
                  <el-button
                    v-if="order.status === 'PENDING'"
                    @click="handleCancelOrder(order.id)"
                  >
                    取消订单
                  </el-button>
                  <el-button
                    @click="handleViewDetail(order.id)"
                  >
                    查看详情
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- 空状态 -->
        <div v-else-if="!orderStore.loading" class="empty-container">
          <el-empty description="暂无订单">
            <el-button type="primary" @click="$router.push('/movies')">
              去购票
            </el-button>
          </el-empty>
        </div>
        <!-- 加载状态 -->
        <div v-if="orderStore.loading" class="loading-container">
          <el-skeleton :rows="3" animated />
        </div>
        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            :background="true"
            layout="prev, pager, next, jumper, total"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const orderStore = useOrderStore()

const filterStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 初始化数据
onMounted(async () => {
  await loadOrders()
})

// 加载订单列表
const loadOrders = async () => {
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }

    if (filterStatus.value) {
      params.status = filterStatus.value
    }

    const response = await orderStore.getUserOrders(params)
    total.value = response.data.totalElements || orderStore.orders.length
  } catch (error) {
    console.error('加载订单列表失败:', error)
    ElMessage.error('加载订单列表失败')
  }
}

// 处理筛选变化
const handleFilterChange = () => {
  currentPage.value = 1
  loadOrders()
}

// 处理页码变化
const handlePageChange = () => {
  loadOrders()
}

// 处理支付订单
const handlePayOrder = async (orderId) => {
  try {
    await orderStore.payOrder(orderId)
    ElMessage.success('支付成功')
    await loadOrders()
  } catch (error) {
    ElMessage.error(error.message || '支付失败')
  }
}

// 处理取消订单
const handleCancelOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要取消订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '再想想',
      type: 'warning'
    })

    await orderStore.cancelOrder(orderId)
    ElMessage.success('订单取消成功')
    await loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '取消订单失败')
    }
  }
}

// 处理查看详情
const handleViewDetail = (orderId) => {
  router.push(`/order/${orderId}`)
}

// 获取状态标签类型
const getStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'PAID': 'success',
    'CANCELLED': 'info'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    'PENDING': '待支付',
    'PAID': '已支付',
    'CANCELLED': '已取消'
  }
  return texts[status] || '未知状态'
}

// 格式化时间
const formatTime = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  return date.toLocaleString('zh-CN')
}

// 格式化场次时间
const formatSessionTime = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>
<style scoped lang="scss">
.order-list-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;

  .page-title {
    font-size: 28px;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 8px 0;
  }

  .page-subtitle {
    font-size: 16px;
    color: $text-regular;
    margin: 0;
  }
}

.order-filter {
  margin-bottom: 24px;
  padding: 16px;
  background: $bg-white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.order-list {
  .orders-container {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
}

.order-item {
  background: $bg-white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 20px;
    background: $bg-gray;
    border-bottom: 1px solid $border-extra-light;

    .order-info {
      display: flex;
      flex-direction: column;
      gap: 4px;

      .order-no {
        font-size: 14px;
        font-weight: 500;
        color: $text-primary;
      }

      .order-time {
        font-size: 12px;
        color: $text-secondary;
      }
    }
  }

  .order-content {
    display: flex;
    padding: 20px;

    .movie-info {
      display: flex;
      flex: 1;
      gap: 16px;

      .movie-poster {
        flex: 0 0 80px;

        img {
          width: 80px;
          height: 110px;
          border-radius: 6px;
          object-fit: cover;
        }
      }

      .movie-details {
        flex: 1;

        .movie-title {
          font-size: 18px;
          font-weight: 600;
          color: $text-primary;
          margin: 0 0 8px 0;
        }

        .session-info {
          display: flex;
          flex-direction: column;
          gap: 4px;
          margin-bottom: 12px;

          .info-item {
            font-size: 14px;
            color: $text-regular;
          }
        }

        .seats-info {
          display: flex;
          align-items: center;
          gap: 8px;

          .seat-tag {
            padding: 2px 6px;
            background: rgba($primary-color, 0.08);
            color: $primary-color;
            border-radius: 4px;
            font-size: 12px;
            font-weight: 500;
          }

          .ticket-count {
            font-size: 12px;
            color: $text-secondary;
          }
        }
      }
    }

    .order-actions {
      flex: 0 0 200px;
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      justify-content: space-between;

      .order-price {
        .price {
          font-size: 20px;
          font-weight: 600;
          color: $danger-color;
        }
      }

      .action-buttons {
        display: flex;
        flex-direction: column;
        gap: 8px;

        .el-button {
          min-width: 100px;
        }
      }
    }
  }
}

.empty-container,
.loading-container {
  padding: 60px 0;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 24px 0;
}

@media (max-width: 768px) {
  .order-list-page {
    padding: 16px;
  }

  .order-content {
    flex-direction: column;
    gap: 16px;

    .order-actions {
      flex: none !important;
      flex-direction: row !important;
      align-items: center !important;
      justify-content: space-between !important;
      width: 100%;
    }
  }

  .action-buttons {
    flex-direction: row !important;
  }
}
</style>
