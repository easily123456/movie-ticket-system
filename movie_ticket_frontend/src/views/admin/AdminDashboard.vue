<template>
  <div class="admin-dashboard">
    <div class="dashboard-header">
      <h1 class="page-title">数据概览</h1>
      <p class="page-subtitle">实时监控系统运行状态</p>
    </div>
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <StatCard
        title="总用户数"
        :value="dashboardStats.totalUsers"
        icon="User"
        type="primary"
        :trend="12.5"
        :show-chart="true"
        :chart-data="userGrowthData"
      />
      <StatCard
        title="总电影数"
        :value="dashboardStats.totalMovies"
        icon="VideoCamera"
        type="success"
        :trend="8.3"
        :show-chart="true"
        :chart-data="movieGrowthData"
      />
      <StatCard
        title="总订单数"
        :value="dashboardStats.totalOrders"
        icon="Ticket"
        type="warning"
        :trend="15.2"
        :show-chart="true"
        :chart-data="orderGrowthData"
      />
      <StatCard
        title="今日营收"
        :value="dashboardStats.todayRevenue"
        icon="Money"
        type="danger"
        :trend="22.1"
        :show-chart="true"
        :chart-data="revenueGrowthData"
      />
    </div>
    <!-- 图表区域 -->
    <div class="charts-grid">
      <!-- 营收趋势 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3 class="chart-title">营收趋势</h3>
          <div class="chart-actions">
            <el-radio-group v-model="revenuePeriod" size="small">
              <el-radio-button label="week">本周</el-radio-button>
              <el-radio-button label="month">本月</el-radio-button>
              <el-radio-button label="year">本年</el-radio-button>
            </el-radio-group>
          </div>
        </div>
        <LineChart
          :data="revenueChartData"
          :height="300"
          color="#ff5f16"
        />
      </div>
      <!-- 订单状态分布 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3 class="chart-title">订单状态分布</h3>
        </div>
        <PieChart
          :data="orderStatusData"
          :height="300"
          :show-legend="true"
        />
      </div>
      <!-- 电影类型分布 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3 class="chart-title">电影类型分布</h3>
        </div>
        <BarChart
          :data="genreDistributionData"
          :labels="genreLabels"
          :height="300"
          :horizontal="true"
          color="['#ff5f16', '#67c23a', '#e6a23c', '#f56c6c', '#909399']"
        />
      </div>
      <!-- 用户活跃度 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3 class="chart-title">用户活跃度</h3>
          <div class="chart-actions">
            <el-radio-group v-model="activityPeriod" size="small">
              <el-radio-button label="day">今日</el-radio-button>
              <el-radio-button label="week">本周</el-radio-button>
            </el-radio-group>
          </div>
        </div>
        <BarChart
          :data="userActivityData"
          :labels="activityLabels"
          :height="300"
          color="#67c23a"
        />
      </div>
    </div>
    <!-- 最近活动 -->
    <div class="recent-activities">
      <div class="section-header">
        <h3 class="section-title">最近活动</h3>
        <el-button type="primary" link>查看全部</el-button>
      </div>
      <div class="activities-list">
        <el-timeline>
          <el-timeline-item
            v-for="activity in recentActivities"
            :key="activity.id"
            :timestamp="activity.time"
            :type="activity.type"
            :size="activity.size"
          >
            <div class="activity-item">
              <div class="activity-icon" :class="activity.type">
                <el-icon>
                  <component :is="activity.icon" />
                </el-icon>
              </div>
              <div class="activity-content">
                <p class="activity-text">{{ activity.text }}</p>
                <span class="activity-user">{{ activity.user }}</span>
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted, computed } from 'vue'
// import { useAdminStore } from '@/stores/admin'
import StatCard from '@/components/admin/StatCard.vue'
import LineChart from '@/components/admin/charts/LineChart.vue'
import BarChart from '@/components/admin/charts/BarChart.vue'
import PieChart from '@/components/admin/charts/PieChart.vue'
import { ElMessage } from 'element-plus'

// const adminStore = useAdminStore()

// 响应式数据
const revenuePeriod = ref('month')
const activityPeriod = ref('week')

// 模拟数据
const dashboardStats = ref({
  totalUsers: 12345,
  totalMovies: 567,
  totalOrders: 8901,
  todayRevenue: 12345.67
})

const userGrowthData = ref([30, 40, 35, 50, 49, 60, 70, 91, 125, 150, 180, 200])
const movieGrowthData = ref([10, 15, 12, 18, 25, 30, 35, 40, 45, 50, 55, 60])
const orderGrowthData = ref([100, 120, 110, 130, 125, 140, 150, 160, 170, 180, 190, 200])
const revenueGrowthData = ref([5000, 7000, 6500, 8000, 7500, 9000, 8500, 10000, 11000, 12000, 11500, 13000])

const revenueChartData = computed(() => {
  const data = {
    week: [1200, 1800, 1500, 2000, 1700, 2200, 2500],
    month: [12000, 15000, 13000, 16000, 14000, 17000, 18000, 20000, 19000, 22000, 21000, 23000],
    year: [120000, 150000, 130000, 160000, 140000, 170000, 180000, 200000, 190000, 220000, 210000, 230000]
  }
  return data[revenuePeriod.value] || data.month
})

const orderStatusData = ref([
  { value: 335, name: '已完成' },
  { value: 310, name: '待支付' },
  { value: 234, name: '已取消' },
  { value: 135, name: '退款中' }
])

const genreDistributionData = ref([120, 200, 150, 80, 70, 110, 130])
const genreLabels = ref(['动作', '喜剧', '爱情', '科幻', '恐怖', '动画', '剧情'])

const userActivityData = computed(() => {
  const data = {
    day: [65, 59, 80, 81, 56, 55, 40, 78, 90, 120, 145, 180],
    week: [300, 450, 600, 550, 700, 650, 800]
  }
  return data[activityPeriod.value] || data.week
})

const activityLabels = computed(() => {
  const labels = {
    day: ['0:00', '2:00', '4:00', '6:00', '8:00', '10:00', '12:00', '14:00', '16:00', '18:00', '20:00', '22:00'],
    week: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  }
  return labels[activityPeriod.value] || labels.week
})

const recentActivities = ref([
  {
    id: 1,
    time: '2024-01-15 14:30',
    type: 'primary',
    icon: 'User',
    text: '新用户注册',
    user: '张三'
  },
  {
    id: 2,
    time: '2024-01-15 13:15',
    type: 'success',
    icon: 'VideoCamera',
    text: '新电影上架',
    user: '李四'
  },
  {
    id: 3,
    time: '2024-01-15 12:00',
    type: 'warning',
    icon: 'Ticket',
    text: '订单支付成功',
    user: '王五'
  },
  {
    id: 4,
    time: '2024-01-15 11:45',
    type: 'danger',
    icon: 'ChatDotRound',
    text: '用户发表评论',
    user: '赵六'
  }
])

// 初始化数据
onMounted(async () => {
  try {
    await loadDashboardData()
  } catch (error) {
    console.error('初始化数据失败:', error)
    ElMessage.error('加载仪表盘数据失败')
  }
})

// 加载仪表盘数据
const loadDashboardData = async () => {
  try {
    // 这里应该调用API获取真实数据
    // const stats = await adminStore.getDashboardStats()
    // dashboardStats.value = stats
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
    throw error
  }
}
</script>
<style scoped lang="scss">
.admin-dashboard {
  padding: 20px;
}

.dashboard-header {
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

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.chart-card {
  background: $bg-white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .chart-title {
      font-size: 16px;
      font-weight: 600;
      color: $text-primary;
      margin: 0;
    }
  }
}

.recent-activities {
  background: $bg-white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: $text-primary;
      margin: 0;
    }
  }
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;

  .activity-icon {
    width: 32px;
    height: 32px;
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    &.primary {
      background: rgba($primary-color, 0.08);
      color: $primary-color;
    }

    &.success {
      background: rgba($success-color, 0.1);
      color: $success-color;
    }

    &.warning {
      background: rgba($warning-color, 0.1);
      color: $warning-color;
    }

    &.danger {
      background: rgba($danger-color, 0.1);
      color: $danger-color;
    }
  }

  .activity-content {
    flex: 1;

    .activity-text {
      font-size: 14px;
      color: $text-primary;
      margin: 0 0 4px 0;
    }

    .activity-user {
      font-size: 12px;
      color: $text-secondary;
    }
  }
}

:deep(.el-timeline) {
  padding-left: 0;
}

:deep(.el-timeline-item) {
  padding-bottom: 20px;
}

@media (max-width: 768px) {
  .admin-dashboard {
    padding: 16px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }

  .chart-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>
