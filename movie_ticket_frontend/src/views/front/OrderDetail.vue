<template>
  <div class="order-detail-page">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/user/orders' }">我的订单</el-breadcrumb-item>
        <el-breadcrumb-item>订单详情</el-breadcrumb-item>
      </el-breadcrumb>

      <h1 class="page-title">订单详情</h1>
    </div>
    <div class="page-content">
      <!-- 订单状态 -->
      <div class="order-status-card">
        <div class="status-header">
          <div class="status-info">
            <h2 class="order-no">订单号：{{ order.orderNo }}</h2>
            <p class="order-time">下单时间：{{ formatTime(order.createTime) }}</p>
          </div>
          <div class="status-badge">
            <el-tag :type="getStatusType(order.status)" size="large">
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>
        </div>

        <div class="status-steps">
          <el-steps :active="getStepActive(order.status)" align-center>
            <el-step title="待支付" :description="formatTime(order.createTime)" />
            <el-step title="已支付" :description="formatTime(order.payTime)" />
            <el-step title="已完成" />
          </el-steps>
        </div>
      </div>
      <!-- 订单信息 -->
      <div class="order-info-card">
        <h3 class="card-title">订单信息</h3>

        <div class="info-section">
          <div class="info-item">
            <span class="label">电影名称</span>
            <span class="value">{{ order.movieTitle }}</span>
          </div>
          <div class="info-item">
            <span class="label">影院信息</span>
            <span class="value">XX影城（{{ order.hallName }}）</span>
          </div>
          <div class="info-item">
            <span class="label">观影时间</span>
            <span class="value">{{ formatSessionTime(order.sessionTime) }}</span>
          </div>
          <div class="info-item">
            <span class="label">座位信息</span>
            <span class="value">
              <span
                v-for="seat in order.seatNumbers"
                :key="seat"
                class="seat-tag"
              >
                {{ seat }}
              </span>
            </span>
          </div>
          <div class="info-item">
            <span class="label">票数</span>
            <span class="value">{{ order.seatCount }}张</span>
          </div>
        </div>
      </div>
      <!-- 价格信息 -->
      <div class="price-info-card">
        <h3 class="card-title">价格信息</h3>

        <div class="price-list">
          <div class="price-item">
            <span class="label">票价</span>
            <span class="value">¥{{ calculateTicketPrice() }} × {{ order.seatCount }}</span>
          </div>
          <div class="price-item">
            <span class="label">服务费</span>
            <span class="value">¥{{ serviceFee }}</span>
          </div>
          <div class="price-total">
            <span class="label">实付金额</span>
            <span class="value">¥{{ order.totalPrice }}</span>
          </div>
        </div>
      </div>
      <!-- 操作按钮 -->
      <div class="action-card" v-if="order.status === 'PENDING'">
        <div class="action-tips">
          <el-alert
            title="请在15分钟内完成支付，超时订单将自动取消"
            type="warning"
            :closable="false"
            show-icon
          />
        </div>
        <div class="action-buttons">
          <el-button @click="handleCancelOrder">取消订单</el-button>
          <el-button type="primary" @click="handlePayOrder">立即支付</el-button>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useOrderStore } from '@/stores/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
// const router = useRouter()
const orderStore = useOrderStore()

const orderId = route.params.orderId
const order = ref({
  orderNo: '',
  status: '',
  createTime: '',
  payTime: '',
  movieTitle: '',
  hallName: '',
  sessionTime: '',
  seatNumbers: [],
  seatCount: 0,
  totalPrice: 0
})

// 服务费
const serviceFee = (order.value.seatCount * 3).toFixed(2)

// 初始化数据
onMounted(async () => {
  try {
    await loadOrderDetail()
  } catch (error) {
    console.error('加载订单详情失败:', error)
    ElMessage.error('加载订单详情失败')
  }
})

// 加载订单详情
const loadOrderDetail = async () => {
  try {
    const response = await orderStore.getOrderDetail(orderId)
    order.value = response.data
  } catch (error) {
    console.error('加载订单详情失败:', error)
    throw error
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'PAID': 'success',
    'CANCELLED': 'info',
    'REFUNDED': 'info'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    'PENDING': '待支付',
    'PAID': '已支付',
    'CANCELLED': '已取消',
    'REFUNDED': '已退款'
  }
  return texts[status] || '未知状态'
}

// 获取步骤激活状态
const getStepActive = (status) => {
  const steps = {
    'PENDING': 0,
    'PAID': 1,
    'CANCELLED': 0,
    'REFUNDED': 0
  }
  return steps[status] || 0
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
    minute: '2-digit',
    weekday: 'short'
  })
}

// 计算票价
const calculateTicketPrice = () => {
  if (!order.value.totalPrice || !order.value.seatCount) return 0
  const ticketPrice = (order.value.totalPrice - order.value.seatCount * 3) / order.value.seatCount
  return ticketPrice.toFixed(2)
}

// 处理取消订单
const handleCancelOrder = async () => {
  try {
    await ElMessageBox.confirm('确定要取消订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '再想想',
      type: 'warning'
    })

    await orderStore.cancelOrder(orderId)
    ElMessage.success('订单取消成功')
    await loadOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '取消订单失败')
    }
  }
}

// 处理支付订单
const handlePayOrder = async () => {
  try {
    await orderStore.payOrder(orderId)
    ElMessage.success('支付成功')
    await loadOrderDetail()
  } catch (error) {
    ElMessage.error(error.message || '支付失败')
  }
}
</script>
<style scoped lang="scss">
.order-detail-page {
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
    margin: 16px 0 8px 0;
  }
}

.page-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-status-card,
.order-info-card,
.price-info-card,
.action-card {
  background: $bg-white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.order-status-card {
  .status-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 24px;

    .order-no {
      font-size: 18px;
      font-weight: 600;
      color: $text-primary;
      margin: 0 0 8px 0;
    }

    .order-time {
      font-size: 14px;
      color: $text-regular;
      margin: 0;
    }
  }
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: $text-primary;
  margin: 0 0 20px 0;
  padding-bottom: 12px;
  border-bottom: 2px solid $border-extra-light;
}

.info-section {
  .info-item {
    display: flex;
    margin-bottom: 16px;

    &:last-child {
      margin-bottom: 0;
    }

    .label {
      width: 100px;
      font-size: 14px;
      color: $text-regular;
    }

    .value {
      flex: 1;
      font-size: 14px;
      color: $text-primary;
      font-weight: 500;
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
  }
}

.price-list {
  .price-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 12px;
    font-size: 14px;
    color: $text-regular;
  }

  .price-total {
    display: flex;
    justify-content: space-between;
    margin-top: 12px;
    padding-top: 12px;
    border-top: 1px solid $border-extra-light;
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
  }
}

.action-card {
  .action-tips {
    margin-bottom: 20px;
  }

  .action-buttons {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}

@media (max-width: 768px) {
  .order-detail-page {
    padding: 16px;
  }

  .order-status-card,
  .order-info-card,
  .price-info-card,
  .action-card {
    padding: 16px;
  }

  .status-header {
    flex-direction: column;
    gap: 12px;
  }

  .info-item {
    flex-direction: column;
    gap: 4px;
  }
}
</style>
