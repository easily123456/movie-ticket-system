<template>
  <div class="user-center-page">
    <!-- 用户信息 -->
    <div class="user-profile">
      <div class="profile-header">
        <div class="avatar-section">
          <img
            :src="user.avatar || '/images/default-avatar.png'"
            :alt="user.username"
            class="user-avatar"
          />
          <div class="avatar-actions">
            <el-button type="primary" link @click="handleEditAvatar">
              更换头像
            </el-button>
          </div>
        </div>
        <div class="user-info">
          <h1 class="username">{{ user.username }}</h1>
          <p class="user-email">{{ user.email }}</p>
          <p class="user-phone" v-if="user.phone">
            <el-icon><Phone /></el-icon>
            {{ user.phone }}
          </p>
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-value">{{ orderCount }}</div>
              <div class="stat-label">购票数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ commentCount }}</div>
              <div class="stat-label">评论数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ favoriteCount }}</div>
              <div class="stat-label">收藏数</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 功能导航 -->
    <div class="user-actions">
      <el-row :gutter="20">
        <el-col :xs="12" :sm="8" :md="6">
          <div class="action-card" @click="$router.push('/user/orders')">
            <div class="action-icon">
              <el-icon><Ticket /></el-icon>
            </div>
            <div class="action-info">
              <div class="action-title">我的订单</div>
              <div class="action-desc">查看购票记录</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="8" :md="6">
          <div class="action-card" @click="$router.push('/user/comments')">
            <div class="action-icon">
              <el-icon><ChatDotRound /></el-icon>
            </div>
            <div class="action-info">
              <div class="action-title">我的评论</div>
              <div class="action-desc">管理我的评论</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="8" :md="6">
          <div class="action-card" @click="$router.push('/user/favorites')">
            <div class="action-icon">
              <el-icon><Star /></el-icon>
            </div>
            <div class="action-info">
              <div class="action-title">我的收藏</div>
              <div class="action-desc">查看收藏电影</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="8" :md="6">
          <div class="action-card" @click="$router.push('/user/profile')">
            <div class="action-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="action-info">
              <div class="action-title">个人信息</div>
              <div class="action-desc">修改个人资料</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    <!-- 最近活动 -->
    <div class="recent-activities">
      <h2 class="section-title">最近活动</h2>
      <div class="activities-list">
        <div v-if="recentActivities.length" class="activities-container">
          <div
            v-for="activity in recentActivities"
            :key="activity.id"
            class="activity-item"
          >
            <div class="activity-icon">
              <el-icon>
                <component :is="activity.icon" />
              </el-icon>
            </div>
            <div class="activity-content">
              <p class="activity-text">{{ activity.text }}</p>
              <span class="activity-time">{{ activity.time }}</span>
            </div>
            <div class="activity-action">
              <el-button
                v-if="activity.action"
                type="primary"
                link
                @click="handleActivityAction(activity)"
              >
                {{ activity.action }}
              </el-button>
            </div>
          </div>
        </div>
        <div v-else class="empty-activities">
          <el-empty description="暂无活动记录" />
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
// import { useOrderStore } from '@/stores/order'
import { useCommentStore } from '@/stores/comments'
import { useFavoriteStore } from '@/stores/favorite'
import { Ticket, ChatDotRound, Star, User, Phone } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
// const orderStore = useOrderStore()
const commentStore = useCommentStore()
const favoriteStore = useFavoriteStore()

const user = ref({})
const orderCount = ref(0)
const commentCount = ref(0)
const favoriteCount = ref(0)
const recentActivities = ref([])

// 初始化数据
onMounted(async () => {
  await loadUserData()
  await loadRecentActivities()
})

// 加载用户数据
const loadUserData = async () => {
  try {
    // 加载用户信息
    user.value = authStore.user || {}

    // 加载订单数量（这里需要订单store提供相关方法）
    // orderCount.value = await orderStore.getUserOrderCount()

    // 加载评论数量
    const commentsResponse = await commentStore.getUserComments({ page: 0, size: 1 })
    commentCount.value = commentsResponse.data?.totalElements || 0

    // 加载收藏数量
    favoriteCount.value = await favoriteStore.getUserFavoriteCount()
  } catch (error) {
    console.error('加载用户数据失败:', error)
  }
}

// 加载最近活动
const loadRecentActivities = async () => {
  // 模拟数据
  recentActivities.value = [
    {
      id: 1,
      icon: 'Ticket',
      text: '您购买了《流浪地球2》的电影票',
      time: '2小时前',
      action: '查看订单'
    },
    {
      id: 2,
      icon: 'ChatDotRound',
      text: '您评论了《封神第一部》',
      time: '1天前',
      action: '查看评论'
    },
    {
      id: 3,
      icon: 'Star',
      text: '您收藏了《消失的她》',
      time: '2天前',
      action: '查看收藏'
    }
  ]
}

// 处理编辑头像
const handleEditAvatar = () => {
  // 这里可以实现头像编辑功能
  ElMessage.info('头像编辑功能开发中')
}

// 处理活动动作
const handleActivityAction = (activity) => {
  if (activity.action === '查看订单') {
    router.push('/user/orders')
  } else if (activity.action === '查看评论') {
    router.push('/user/comments')
  } else if (activity.action === '查看收藏') {
    router.push('/user/favorites')
  }
}
</script>
<style scoped lang="scss">
.user-center-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.user-profile {
  background: $bg-white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.profile-header {
  display: flex;
  gap: 30px;
  align-items: flex-start;

  .avatar-section {
    text-align: center;

    .user-avatar {
      width: 100px;
      height: 100px;
      border-radius: 50%;
      object-fit: cover;
      border: 4px solid $border-extra-light;
      margin-bottom: 12px;
    }

    .avatar-actions {
      .el-button {
        font-size: 12px;
      }
    }
  }

  .user-info {
    flex: 1;

    .username {
      font-size: 28px;
      font-weight: 600;
      color: $text-primary;
      margin: 0 0 8px 0;
    }

    .user-email {
      font-size: 16px;
      color: $text-regular;
      margin: 0 0 8px 0;
    }

    .user-phone {
      font-size: 14px;
      color: $text-regular;
      margin: 0 0 20px 0;
      display: flex;
      align-items: center;
      gap: 6px;
    }
  }
}

.user-stats {
  display: flex;
  gap: 30px;

  .stat-item {
    text-align: center;

    .stat-value {
      font-size: 24px;
      font-weight: 600;
      color: $primary-color;
      margin-bottom: 4px;
    }

    .stat-label {
      font-size: 14px;
      color: $text-regular;
    }
  }
}

.user-actions {
  margin-bottom: 30px;

  .action-card {
    background: $bg-white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    transition: all 0.3s ease;
    margin-bottom: 20px;
    height: 120px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
    }

    .action-icon {
      width: 50px;
      height: 50px;
      background: rgba($primary-color, 0.08);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 12px;

      .el-icon {
        font-size: 24px;
        color: $primary-color;
      }
    }

    .action-title {
      font-size: 16px;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 4px;
    }

    .action-desc {
      font-size: 12px;
      color: $text-secondary;
    }
  }
}

.recent-activities {
  background: $bg-white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .section-title {
    font-size: 20px;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 20px 0;
    padding-bottom: 12px;
    border-bottom: 2px solid $border-extra-light;
  }
}

.activities-list {
  .activities-container {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 8px;
  transition: background-color 0.2s ease;

  &:hover {
    background: $bg-gray;
  }

  .activity-icon {
    width: 40px;
    height: 40px;
    background: $bg-gray;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;

    .el-icon {
      font-size: 18px;
      color: $text-regular;
    }
  }

  .activity-content {
    flex: 1;

    .activity-text {
      font-size: 14px;
      color: $text-primary;
      margin: 0 0 4px 0;
    }

    .activity-time {
      font-size: 12px;
      color: $text-secondary;
    }
  }
}

.empty-activities {
  padding: 40px 0;
}

@media (max-width: 768px) {
  .user-center-page {
    padding: 16px;
  }

  .profile-header {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }

  .user-stats {
    justify-content: center;
  }

  .user-actions {
    .el-col {
      margin-bottom: 16px;
    }
  }

  .activity-item {
    flex-direction: column;
    text-align: center;
    gap: 12px;

    .activity-content {
      text-align: center;
    }
  }
}
</style>
