<template>
  <div class="session-detail" v-if="session">
    <!-- 场次基本信息 -->
    <div class="session-basic-info">
      <el-image
        :src="session.moviePoster"
        :alt="session.movieTitle"
        class="movie-poster"
        fit="cover"
      >
        <template #error>
          <div class="image-error">
            <el-icon><Picture /></el-icon>
          </div>
        </template>
      </el-image>
      <div class="session-main-info">
        <h3 class="movie-title">{{ session.movieTitle }}</h3>
        <div class="session-meta">
          <el-tag :type="session.status ? 'success' : 'danger'" size="large">
            {{ session.status ? '正常' : '取消' }}
          </el-tag>
          <el-tag type="primary" size="large">{{ session.hallName }}</el-tag>
        </div>
        <div class="session-time">
          <div class="time-range">
            {{ formatDateTime(session.startTime) }} ~ {{ formatDateTime(session.endTime) }}
          </div>
        </div>
      </div>
    </div>
    <el-divider />

    <!-- 详细信息 -->
    <el-descriptions :column="2" border>
      <el-descriptions-item label="场次ID">
        {{ session.id }}
      </el-descriptions-item>
      <el-descriptions-item label="电影名称">
        {{ session.movieTitle }}
      </el-descriptions-item>
      <el-descriptions-item label="放映厅">
        {{ session.hallName }}
      </el-descriptions-item>
      <el-descriptions-item label="价格">
        {{ formatPrice(session.price) }}
      </el-descriptions-item>
      <el-descriptions-item label="总座位数">
        {{ session.availableSeats }}
      </el-descriptions-item>
      <el-descriptions-item label="已预订">
        {{ session.bookedSeats }}
      </el-descriptions-item>
      <el-descriptions-item label="剩余座位">
        {{ session.availableSeats - session.bookedSeats }}
      </el-descriptions-item>
      <el-descriptions-item label="上座率">
        <el-progress
          :percentage="getSeatPercentage(session)"
          :color="getSeatColor(session)"
          :show-text="true"
        />
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ formatTime(session.createTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="更新时间">
        {{ formatTime(session.updateTime) }}
      </el-descriptions-item>
    </el-descriptions>
    <!-- 订单信息 -->
    <div class="orders-section" v-if="orders.length > 0">
      <h4 class="section-title">相关订单</h4>
      <el-table :data="orders" style="width: 100%">
        <el-table-column label="订单号" width="180">
          <template #default="{ row }">
            <span class="order-no">{{ row.orderNo }}</span>
          </template>
        </el-table-column>
        <el-table-column label="用户" prop="username" width="100" />
        <el-table-column label="座位" width="120">
          <template #default="{ row }">
            <span>{{ row.seatNumbers.join(', ') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="金额" width="100">
          <template #default="{ row }">
            <span class="price">{{ formatPrice(row.totalPrice) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusType(row.status)" size="small">
              {{ getOrderStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="140">
          <template #default="{ row }">
            <span class="time">{{ formatTime(row.createTime) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 无订单提示 -->
    <div v-else class="no-orders">
      <el-empty description="暂无订单" :image-size="80" />
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import { formatTime, formatDateTime, formatPrice } from '@/utils'
import { colors } from '@/utils/theme'

// const props = defineProps({
//   session: {
//     type: Object,
//     required: true
//   }
// })

const orders = ref([])

// 计算座位百分比
const getSeatPercentage = (session) => {
  if (!session.availableSeats) return 0
  return Math.round((session.bookedSeats / session.availableSeats) * 100)
}

// 获取座位颜色
const getSeatColor = (session) => {
  const percentage = getSeatPercentage(session)
  if (percentage >= 80) return colors.danger
  if (percentage >= 50) return colors.warning
  return colors.success
}

// 订单状态相关方法
const getOrderStatusType = (status) => {
  const typeMap = {
    PENDING: 'warning',
    PAID: 'success',
    CANCELLED: 'info',
    REFUNDED: 'danger'
  }
  return typeMap[status] || 'info'
}

const getOrderStatusText = (status) => {
  const textMap = {
    PENDING: '待支付',
    PAID: '已支付',
    CANCELLED: '已取消',
    REFUNDED: '已退款'
  }
  return textMap[status] || status
}

onMounted(() => {
  loadSessionOrders()
})

const loadSessionOrders = async () => {
  try {
    // 调用API获取场次订单
    // orders.value = await adminApi.getSessionOrders(props.session.id)

    // 临时模拟数据
    orders.value = [
      {
        id: 1,
        orderNo: 'ORD202401150001',
        username: 'user1',
        seatNumbers: ['A1', 'A2'],
        totalPrice: 120.00,
        status: 'PAID',
        createTime: new Date()
      },
      {
        id: 2,
        orderNo: 'ORD202401150002',
        username: 'user2',
        seatNumbers: ['B5'],
        totalPrice: 60.00,
        status: 'PENDING',
        createTime: new Date(Date.now() - 30 * 60 * 1000)
      }
    ]
  } catch (error) {
    console.error('加载场次订单失败:', error)
  }
}
</script>
<style scoped lang="scss">
.session-detail {
  .session-basic-info {
    display: flex;
    align-items: flex-start;
    gap: $spacing-lg;
    margin-bottom: $spacing-lg;

    .movie-poster {
      width: 100px;
      height: 140px;
      border-radius: $border-radius-base;
      flex-shrink: 0;
    }

    .image-error {
      width: 100px;
      height: 140px;
      background: $bg-gray;
      border-radius: $border-radius-base;
      display: flex;
      align-items: center;
      justify-content: center;
      color: $text-disabled;

      .el-icon {
        font-size: 32px;
      }
    }

    .session-main-info {
      flex: 1;

      .movie-title {
        font-size: 20px;
        font-weight: 700;
        color: $text-primary;
        margin: 0 0 $spacing-sm 0;
      }

      .session-meta {
        display: flex;
        gap: $spacing-md;
        margin-bottom: $spacing-md;
      }

      .session-time {
        .time-range {
          font-size: 16px;
          color: $text-regular;
          font-weight: 500;
        }
      }
    }
  }
}

.orders-section {
  margin: $spacing-xl 0;

  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-lg;
  }
}

.no-orders {
  padding: $spacing-xl 0;
}

.order-no {
  font-family: 'Monaco', 'Consolas', monospace;
  color: $text-regular;
}

.price {
  font-weight: 600;
  color: $primary-color;
}

.time {
  color: $text-secondary;
  font-size: $font-size-small;
}

// 响应式设计
@media (max-width: $breakpoint-sm) {
  .session-basic-info {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .session-meta {
    justify-content: center;
  }
}
</style>
