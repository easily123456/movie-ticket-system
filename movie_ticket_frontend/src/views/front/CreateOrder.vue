<template>
  <div class="create-order-page">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/movies' }">电影</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: `/movie/${movie.id}` }">
          {{ movie.title }}
        </el-breadcrumb-item>
        <el-breadcrumb-item>确认订单</el-breadcrumb-item>
      </el-breadcrumb>

      <h1 class="page-title">确认订单</h1>
    </div>
    <div class="page-content">
      <OrderConfirm
        v-if="session.id"
        :movie="movie"
        :session="session"
        :selected-seats="selectedSeats"
        @cancel="handleCancel"
        @success="handleOrderSuccess"
      />
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMovieStore } from '@/stores/movie'
import { sessionApi } from '@/api'
import OrderConfirm from '@/components/front/OrderConfirm.vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const movieStore = useMovieStore()

const sessionId = route.query.sessionId
const seatNumbers = route.query.seatNumbers?.split(',') || []
const movie = ref({})
const session = ref({})
const selectedSeats = ref([])

// 初始化数据
onMounted(async () => {
  try {
    await loadSessionDetail()
    await loadMovieDetail()
    formatSelectedSeats()
  } catch (error) {
    console.error('加载订单信息失败:', error)
    ElMessage.error('加载订单信息失败')
  }
})

// 加载场次详情
const loadSessionDetail = async () => {
  try {
    const response = await sessionApi.getSessionDetail(sessionId)
    session.value = response.data
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

// 格式化选中的座位信息
const formatSelectedSeats = () => {
  selectedSeats.value = seatNumbers.map(seatNumber => {
    // 解析座位号，例如 "A1" -> 行: "A", 号: 1
    const row = seatNumber.charAt(0)
    const number = parseInt(seatNumber.substring(1))

    // 判断是否为VIP座位（前两排）
    const isVip = row === 'A' || row === 'B'
    const price = isVip ? session.value.price * 1.5 : session.value.price

    return {
      id: seatNumber,
      row: row,
      number: number,
      price: price
    }
  })
}

// 处理取消
const handleCancel = () => {
  router.back()
}

// 处理订单成功
const handleOrderSuccess = (order) => {
  ElMessage.success('订单创建成功！')
  router.push({
    name: 'OrderDetail',
    params: { orderId: order.id }
  })
}
</script>
<style scoped lang="scss">
.create-order-page {
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
  background: $bg-white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

@media (max-width: 768px) {
  .create-order-page {
    padding: 16px;
  }
}
</style>
