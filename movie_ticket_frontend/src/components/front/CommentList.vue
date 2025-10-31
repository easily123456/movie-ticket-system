<template>
  <div class="comment-list">
    <!-- 评论统计 -->
    <div v-if="showStats && commentStats" class="comment-stats">
      <div class="stats-overview">
        <div class="stat-item">
          <div class="stat-value">{{ commentStats.totalComments }}</div>
          <div class="stat-label">总评论</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ commentStats.averageRating || 0 }}</div>
          <div class="stat-label">平均分</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ commentStats.todayComments }}</div>
          <div class="stat-label">今日评论</div>
        </div>
      </div>
      <!-- 评分分布 -->
      <div v-if="showRatingDistribution" class="rating-distribution">
        <div
          v-for="rating in 5"
          :key="rating"
          class="rating-bar"
        >
          <span class="rating-label">{{ rating }}星</span>
          <div class="bar-container">
            <div
              class="bar-fill"
              :style="{ width: getRatingPercentage(rating) + '%' }"
            ></div>
          </div>
          <span class="rating-count">
            {{ commentStats.ratingDistribution[rating] || 0 }}
          </span>
        </div>
      </div>
    </div>
    <!-- 评论排序 -->
    <div v-if="showSort" class="comment-sort">
      <el-radio-group v-model="sortBy" @change="handleSortChange">
        <el-radio-button label="createTime">最新</el-radio-button>
        <el-radio-button label="likeCount">最热</el-radio-button>
      </el-radio-group>
    </div>
    <!-- 评论列表 -->
    <div class="comments-container">
      <div
        v-for="comment in comments"
        :key="comment.id"
        class="comment-item"
      >
        <div class="comment-header">
          <div class="user-info">
            <img
              :src="comment.avatar || '/images/default-avatar.png'"
              :alt="comment.username"
              class="user-avatar"
            />
            <div class="user-details">
              <span class="username">{{ comment.username }}</span>
              <div class="comment-rating">
                <el-rate
                  v-model="comment.rating"
                  disabled
                  show-score
                  text-color="#e6a23c"
                  score-template="{value}"
                  size="small"
                />
              </div>
            </div>
          </div>
          <div class="comment-time">
            {{ formatTime(comment.createTime) }}
          </div>
        </div>
        <div class="comment-content">
          <p class="content-text">{{ comment.content }}</p>
        </div>
        <div class="comment-actions">
          <div class="action-item" @click="handleLikeComment(comment)">
            <el-icon><Star /></el-icon>
            <span class="action-count">{{ comment.likeCount }}</span>
          </div>
          <div
            v-if="showDelete && comment.userId === currentUserId"
            class="action-item delete"
            @click="handleDeleteComment(comment.id)"
          >
            <el-icon><Delete /></el-icon>
            <span>删除</span>
          </div>
        </div>
      </div>
    </div>
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>
    <!-- 空状态 -->
    <div v-else-if="isEmpty" class="empty-container">
      <el-empty description="暂无评论">
        <span>快来发表第一条评论吧！</span>
      </el-empty>
    </div>
    <!-- 加载更多 -->
    <div v-if="hasMore && !loading" class="load-more">
      <el-button
        type="primary"
        link
        @click="handleLoadMore"
      >
        加载更多评论
      </el-button>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useCommentStore } from '@/stores/comments'
import { useAuthStore } from '@/stores/auth'
import { Star, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps({
  movieId: {
    type: [String, Number],
    default: null
  },
  showStats: {
    type: Boolean,
    default: true
  },
  showSort: {
    type: Boolean,
    default: true
  },
  showRatingDistribution: {
    type: Boolean,
    default: true
  },
  showDelete: {
    type: Boolean,
    default: false
  },
  autoLoad: {
    type: Boolean,
    default: true
  }
})

const emits = defineEmits(['comment-deleted'])

const commentStore = useCommentStore()
const authStore = useAuthStore()

const sortBy = ref('createTime')
const currentUserId = computed(() => authStore.user?.id)

// 计算属性
const comments = computed(() => commentStore.comments)
const commentStats = computed(() => commentStore.commentStats)
const loading = computed(() => commentStore.loading)
const isEmpty = computed(() => commentStore.isEmpty)
const hasMore = computed(() => commentStore.hasMore)

// 初始化数据
onMounted(() => {
  if (props.autoLoad) {
    loadComments()
  }
})

// 监听电影ID变化
watch(() => props.movieId, (newMovieId) => {
  if (newMovieId) {
    commentStore.updateFilters({ movieId: newMovieId })
    loadComments()
  }
})

// 加载评论
const loadComments = async () => {
  try {
    if (props.movieId) {
      await commentStore.getMovieComments(props.movieId)
      await commentStore.getCommentStats(props.movieId)
    } else {
      await commentStore.getComments()
    }
  } catch (error) {
    console.error('加载评论失败:', error)
    ElMessage.error('加载评论失败')
  }
}

// 处理排序变化
const handleSortChange = () => {
  commentStore.updateFilters({
    sortBy: sortBy.value,
    sortOrder: sortBy.value === 'likeCount' ? 'desc' : 'desc'
  })
  loadComments()
}

// 处理点赞评论
const handleLikeComment = async (comment) => {
  if (!authStore.isAuthenticated) {
    ElMessage.warning('请先登录')
    return
  }

  try {
    await commentStore.likeComment(comment.id)
    ElMessage.success('点赞成功')
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('点赞失败')
  }
}

// 处理删除评论
const handleDeleteComment = async (commentId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await commentStore.deleteComment(commentId)
    ElMessage.success('删除成功')
    emits('comment-deleted')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 处理加载更多
const handleLoadMore = async () => {
  try {
    await commentStore.loadMore()
  } catch (error) {
    console.error('加载更多失败:', error)
    ElMessage.error('加载更多失败')
  }
}

// 获取评分百分比
const getRatingPercentage = (rating) => {
  if (!commentStats.value || !commentStats.value.ratingDistribution) {
    return 0
  }

  const total = commentStats.value.totalComments
  const count = commentStats.value.ratingDistribution[rating] || 0
  return total > 0 ? (count / total) * 100 : 0
}

// 格式化时间
const formatTime = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  // 小于1分钟
  if (diff < 60000) {
    return '刚刚'
  }

  // 小于1小时
  if (diff < 3600000) {
    return Math.floor(diff / 60000) + '分钟前'
  }

  // 小于1天
  if (diff < 86400000) {
    return Math.floor(diff / 3600000) + '小时前'
  }

  // 小于7天
  if (diff < 604800000) {
    return Math.floor(diff / 86400000) + '天前'
  }

  // 显示具体日期
  return date.toLocaleDateString('zh-CN')
}

// 暴露方法给父组件
defineExpose({
  loadComments,
  refresh: loadComments
})
</script>
<style scoped lang="scss">
.comment-list {
  .comment-stats {
    background: $bg-gray;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 20px;
  }

  .stats-overview {
    display: flex;
    justify-content: space-around;
    margin-bottom: 20px;

    .stat-item {
      text-align: center;

      .stat-value {
        font-size: 24px;
        font-weight: 600;
        color: $text-primary;
        margin-bottom: 4px;
      }

      .stat-label {
        font-size: 14px;
        color: $text-regular;
      }
    }
  }

  .rating-distribution {
    .rating-bar {
      display: flex;
      align-items: center;
      margin-bottom: 8px;

      .rating-label {
        width: 40px;
        font-size: 12px;
        color: $text-regular;
      }

      .bar-container {
        flex: 1;
        height: 8px;
        background: $bg-gray;
        border-radius: 4px;
        margin: 0 12px;
        overflow: hidden;
      }

      .bar-fill {
        height: 100%;
        background: linear-gradient(90deg, lighten($warning-color, 25%), $warning-color);
        border-radius: 4px;
        transition: width 0.3s ease;
      }

      .rating-count {
        width: 30px;
        font-size: 12px;
        color: $text-secondary;
        text-align: right;
      }
    }
  }

  .comment-sort {
    margin-bottom: 20px;
    padding-bottom: 16px;
    border-bottom: 1px solid $border-extra-light;
  }

  .comments-container {
    .comment-item {
      padding: 16px 0;
      border-bottom: 1px solid $border-extra-light;

      &:last-child {
        border-bottom: none;
      }
    }

    .comment-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 12px;
    }

    .user-info {
      display: flex;
      align-items: flex-start;
      gap: 12px;

      .user-avatar {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        object-fit: cover;
      }

      .user-details {
        .username {
          font-size: 14px;
          font-weight: 500;
          color: $text-primary;
          display: block;
          margin-bottom: 4px;
        }
      }
    }

    .comment-time {
      font-size: 12px;
      color: $text-secondary;
    }

    .comment-content {
      margin-bottom: 12px;

      .content-text {
        margin: 0;
        font-size: 14px;
        line-height: 1.6;
        color: $text-primary;
      }
    }

    .comment-actions {
      display: flex;
      gap: 16px;

      .action-item {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        color: $text-regular;
        cursor: pointer;
        transition: color 0.2s ease;

        &:hover {
          color: $primary-color;
        }

        &.delete:hover {
          color: $danger-color;
        }

        .action-count {
          font-size: 12px;
        }
      }
    }
  }

  .loading-container {
    padding: 40px 0;
  }

  .empty-container {
    padding: 60px 0;
    text-align: center;

    span {
      color: $text-secondary;
      font-size: 14px;
    }
  }

  .load-more {
    text-align: center;
    padding: 20px 0;
  }
}

@media (max-width: 768px) {
  .comment-list {
    .stats-overview {
      .stat-item {
        .stat-value {
          font-size: 20px;
        }
      }
    }

    .comment-header {
      flex-direction: column;
      gap: 8px;
    }

    .comment-time {
      align-self: flex-end;
    }
  }
}
</style>
