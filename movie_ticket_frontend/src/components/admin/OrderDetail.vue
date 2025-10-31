<template>
  <div class="order-detail" v-if="order">
    <!-- 订单基本信息 -->
    <el-descriptions title="订单基本信息" :column="2" border>
      <el-descriptions-item label="订单号">
        <span class="order-no">{{ order.orderNo }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="订单状态">
        <el-tag :type="getStatusType(order.status)" size="large">
          {{ getStatusText(order.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="用户名">
        {{ order.username }}
      </el-descriptions-item>
      <el-descriptions-item label="手机号">
        {{ order.phone || '未填写' }}
      </el-descriptions-item>
      <el-descriptions-item label="订单金额">
        <span class="price">{{ formatPrice(order.totalPrice) }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="座位数量">
        {{ order.seatCount }} 张
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ formatDateTime(order.createTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="支付时间" v-if="order.payTime">
        {{ formatDateTime(order.payTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="取消时间" v-if="order.cancelTime">
        {{ formatDateTime(order.cancelTime) }}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider />

    <!-- 电影场次信息 -->
    <el-descriptions title="电影场次信息" :column="1" border>
      <el-descriptions-item label="电影名称">
        <div class="movie-info">
          <el-image
            :src="order.moviePoster"
            :alt="order.movieTitle"
            class="movie-poster"
            fit="cover"
          >
            <template #error>
              <div class="image-error">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
          <div class="movie-details">
            <div class="movie-title">{{ order.movieTitle }}</div>
            <div class="movie-meta">
              <span class="duration" v-if="order.duration">{{ order.duration }}分钟</span>
              <span class="genre" v-if="order.genreName">{{ order.genreName }}</span>
            </div>
          </div>
        </div>
      </el-descriptions-item>
      <el-descriptions-item label="放映厅">
        {{ order.hallName }}
      </el-descriptions-item>
      <el-descriptions-item label="场次时间">
        {{ formatDateTime(order.sessionTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="座位信息">
        <div class="seats-display">
          <el-tag
            v-for="(seat, index) in order.seatNumbers"
            :key="index"
            type="primary"
            class="seat-tag"
          >
            {{ seat }}
          </el-tag>
        </div>
      </el-descriptions-item>
    </el-descriptions>
    <!-- 价格明细 -->
    <el-divider />
    <el-descriptions title="价格明细" :column="1" border>
      <el-descriptions-item label="单价">
        {{ formatPrice(order.totalPrice / order.seatCount) }}
      </el-descriptions-item>
      <el-descriptions-item label="数量">
        {{ order.seatCount }} 张
      </el-descriptions-item>
      <el-descriptions-item label="总金额">
        <span class="total-price">{{ formatPrice(order.totalPrice) }}</span>
      </el-descriptions-item>
    </el-descriptions>
    <!-- 操作记录 -->
    <el-divider />
    <div class="operation-records">
      <h3 class="section-title">操作记录</h3>
      <el-timeline>
        <el-timeline-item
          v-for="record in operationRecords"
          :key="record.id"
          :timestamp="formatDateTime(record.time)"
          :type="record.type"
        >
          {{ record.action }}
          <div class="record-operator" v-if="record.operator">
            操作人: {{ record.operator }}
          </div>
        </el-timeline-item>
      </el-timeline>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import { formatDateTime, formatPrice } from '@/utils'

const props = defineProps({
  order: {
    type: Object,
    required: true
  }
})

const operationRecords = ref([])

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    PENDING: 'warning',
    PAID: 'success',
    CANCELLED: 'info',
    REFUNDED: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    PENDING: '待支付',
    PAID: '已支付',
    CANCELLED: '已取消',
    REFUNDED: '已退款'
  }
  return textMap[status] || status
}

onMounted(() => {
  loadOperationRecords()
})

// 加载操作记录
const loadOperationRecords = async () => {
  try {
    // 模拟操作记录数据
    operationRecords.value = [
      {
        id: 1,
        time: props.order.createTime,
        action: '订单创建',
        type: 'primary',
        operator: props.order.username
      }
    ]

    if (props.order.payTime) {
      operationRecords.value.push({
        id: 2,
        time: props.order.payTime,
        action: '支付成功',
        type: 'success',
        operator: props.order.username
      })
    }

    if (props.order.cancelTime) {
      operationRecords.value.push({
        id: 3,
        time: props.order.cancelTime,
        action: '订单取消',
        type: 'warning',
        operator: props.order.username
      })
    }

    // 按时间排序
    operationRecords.value.sort((a, b) => new Date(a.time) - new Date(b.time))
  } catch (error) {
    console.error('加载操作记录失败:', error)
  }
}
</script>
<style scoped lang="scss">
.order-detail {
  .order-no {
    font-family: 'Monaco', 'Consolas', monospace;
    font-weight: 600;
    color: $text-primary;
  }

  .price {
    font-weight: 600;
    color: $primary-color;
  }

  .total-price {
    font-size: 18px;
    font-weight: 700;
    color: $success-color;
  }
}

.movie-info {
  display: flex;
  align-items: flex-start;
  gap: $spacing-md;

  .movie-poster {
    width: 60px;
    height: 80px;
    border-radius: $border-radius-small;
    flex-shrink: 0;
  }

  .image-error {
    width: 60px;
    height: 80px;
    background: $bg-gray;
    border-radius: $border-radius-small;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $text-disabled;

    .el-icon {
      font-size: 24px;
    }
  }

  .movie-details {
    flex: 1;

    .movie-title {
      font-weight: 600;
      color: $text-primary;
      margin-bottom: $spacing-xs;
    }

    .movie-meta {
      display: flex;
      gap: $spacing-md;

      span {
        padding: 2px 6px;
        background: $bg-gray;
        border-radius: $border-radius-small;
        color: $text-secondary;
        font-size: $font-size-small;
      }
    }
  }
}

.seats-display {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-sm;

  .seat-tag {
    min-width: 50px;
    text-align: center;
  }
}

.operation-records {
  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-lg;
  }

  .record-operator {
    color: $text-secondary;
    font-size: $font-size-small;
    margin-top: 2px;
  }
}

:deep(.el-descriptions__title) {
  font-size: 16px;
  font-weight: 600;
  color: $text-primary;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
  color: $text-regular;
}

:deep(.el-timeline) {
  padding-left: $spacing-md;
}

// 响应式设计
@media (max-width: $breakpoint-sm) {
  .movie-info {
    flex-direction: column;
    align-items: flex-start;
    gap: $spacing-sm;
  }

  .seats-display {
    gap: $spacing-xs;

    .seat-tag {
      min-width: 40px;
      font-size: $font-size-small;
    }
  }
}
</style>
