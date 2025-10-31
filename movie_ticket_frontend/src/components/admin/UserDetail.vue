<template>
  <div class="user-detail" v-if="user">
    <!-- 用户基本信息 -->
    <div class="user-basic-info">
      <el-avatar :size="80" :src="user.avatar" class="user-avatar">
        {{ user.username?.charAt(0) }}
      </el-avatar>
      <div class="user-main-info">
        <h3 class="username">{{ user.username }}</h3>
        <div class="user-meta">
          <el-tag :type="user.role === 'ADMIN' ? 'danger' : 'primary'" size="large">
            {{ user.role === 'ADMIN' ? '管理员' : '普通用户' }}
          </el-tag>
          <el-tag :type="user.status ? 'success' : 'danger'" size="large">
            {{ user.status ? '正常' : '禁用' }}
          </el-tag>
        </div>
      </div>
    </div>
    <el-divider />

    <!-- 详细信息 -->
    <el-descriptions :column="2" border>
      <el-descriptions-item label="用户ID">
        {{ user.id }}
      </el-descriptions-item>
      <el-descriptions-item label="邮箱">
        {{ user.email }}
      </el-descriptions-item>
      <el-descriptions-item label="手机号">
        {{ user.phone || '未设置' }}
      </el-descriptions-item>
      <el-descriptions-item label="注册时间">
        {{ formatTime(user.createTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="最后登录">
        {{ user.lastLoginTime ? formatTime(user.lastLoginTime) : '从未登录' }}
      </el-descriptions-item>
      <el-descriptions-item label="更新时间">
        {{ formatTime(user.updateTime) }}
      </el-descriptions-item>
    </el-descriptions>
    <!-- 用户统计信息 -->
    <div class="user-stats">
      <h4 class="stats-title">用户统计</h4>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ userStats.orderCount || 0 }}</div>
            <div class="stat-label">订单数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ userStats.commentCount || 0 }}</div>
            <div class="stat-label">评论数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ userStats.favoriteCount || 0 }}</div>
            <div class="stat-label">收藏数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ formatPrice(userStats.totalSpent || 0) }}</div>
            <div class="stat-label">总消费</div>
          </div>
        </el-col>
      </el-row>
    </div>
    <!-- 最近活动 -->
    <div class="recent-activities">
      <h4 class="activities-title">最近活动</h4>
      <div v-if="recentActivities.length === 0" class="empty-activities">
        <el-empty description="暂无活动记录" :image-size="80" />
      </div>
      <el-timeline v-else>
        <el-timeline-item
          v-for="activity in recentActivities"
          :key="activity.id"
          :timestamp="formatTime(activity.time)"
          :type="activity.type"
        >
          {{ activity.content }}
        </el-timeline-item>
      </el-timeline>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { formatTime, formatPrice } from '@/utils'

// const props = defineProps({
//   user: {
//     type: Object,
//     required: true
//   }
// })

const userStats = ref({})
const recentActivities = ref([])

onMounted(() => {
  loadUserStats()
  loadRecentActivities()
})

const loadUserStats = async () => {
  try {
    // 调用API获取用户统计信息
    // userStats.value = await adminApi.getUserStats(props.user.id)

    // 临时模拟数据
    userStats.value = {
      orderCount: 15,
      commentCount: 8,
      favoriteCount: 12,
      totalSpent: 1560.00
    }
  } catch (error) {
    console.error('加载用户统计失败:', error)
  }
}

const loadRecentActivities = async () => {
  try {
    // 调用API获取用户最近活动
    // recentActivities.value = await adminApi.getUserActivities(props.user.id)

    // 临时模拟数据
    recentActivities.value = [
      {
        id: 1,
        type: 'primary',
        content: '购买了《流浪地球3》电影票',
        time: new Date(Date.now() - 2 * 60 * 60 * 1000)
      },
      {
        id: 2,
        type: 'success',
        content: '发表了电影评论',
        time: new Date(Date.now() - 5 * 60 * 60 * 1000)
      },
      {
        id: 3,
        type: 'warning',
        content: '收藏了《热辣滚烫》',
        time: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000)
      }
    ]
  } catch (error) {
    console.error('加载用户活动失败:', error)
  }
}
</script>
<style scoped lang="scss">
.user-detail {
  .user-basic-info {
    display: flex;
    align-items: center;
    gap: $spacing-lg;
    margin-bottom: $spacing-lg;

    .user-avatar {
      flex-shrink: 0;
    }

    .user-main-info {
      .username {
        font-size: 24px;
        font-weight: 700;
        color: $text-primary;
        margin: 0 0 $spacing-md 0;
      }

      .user-meta {
        display: flex;
        gap: $spacing-md;
      }
    }
  }
}

.user-stats {
  margin: $spacing-xl 0;

  .stats-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-lg;
  }

  .stat-item {
    text-align: center;
    padding: $spacing-lg;
    background: $bg-gray;
    border-radius: $border-radius-base;

    .stat-value {
      font-size: 24px;
      font-weight: 700;
      color: $primary-color;
      margin-bottom: $spacing-xs;
    }

    .stat-label {
      color: $text-secondary;
      font-size: $font-size-small;
    }
  }
}

.recent-activities {
  .activities-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-lg;
  }

  .empty-activities {
    padding: $spacing-xl 0;
  }
}

// 响应式设计
@media (max-width: $breakpoint-sm) {
  .user-basic-info {
    flex-direction: column;
    text-align: center;
  }

  .user-meta {
    justify-content: center;
  }

  .user-stats .stat-item {
    padding: $spacing-md;

    .stat-value {
      font-size: 20px;
    }
  }
}
</style>
