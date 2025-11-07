<template>
  <div class="comment-list">
    <!-- 评论统计 -->
    <div class="comment-stats" v-if="showStats && stats">
      <div class="stats-header">
        <h3 class="stats-title">用户评价</h3>
        <div class="stats-summary">
          <div class="average-rating">
            <span class="rating-score">{{ stats.averageRating?.toFixed(1) || '0.0' }}</span>
            <StarRating :value="stats.averageRating" :max="5" size="18" />
            <span class="rating-count">{{ stats.totalComments }}条评价</span>
          </div>
        </div>
      </div>

      <!-- 评分分布 -->
      <div class="rating-distribution" v-if="showRatingDistribution">
        <div
          v-for="item in ratingDistribution"
          :key="item.rating"
          class="distribution-item"
        >
          <span class="rating-label">{{ item.rating }}星</span>
          <el-progress
            :percentage="item.percentage"
            :stroke-width="8"
            :show-text="false"
            class="rating-progress"
          />
          <span class="rating-count">{{ item.count }}</span>
        </div>
      </div>
    </div>

    <!-- 评论排序 -->
    <div class="comment-sort" v-if="showSort">
      <el-select
        v-model="sortOption"
        placeholder="排序方式"
        size="small"
        @change="handleSortChange"
      >
        <el-option
          v-for="option in sortOptions"
          :key="option.value"
          :label="option.label"
          :value="option.value"
        />
      </el-select>
    </div>

    <!-- 评论列表 -->
    <div class="comments-container">
      <div
        v-for="comment in comments"
        :key="comment.id"
        class="comment-item"
      >
        <div class="comment-avatar">
          <el-avatar :size="40" :src="comment.userAvatar">
            {{ comment.username?.charAt(0) }}
          </el-avatar>
        </div>
        <div class="comment-content">
          <div class="comment-header">
            <div class="user-info">
              <span class="username">{{ comment.username }}</span>
              <StarRating :value="comment.rating" :max="5" size="14" />
            </div>
            <div class="comment-time">
              {{ formatDate(comment.createTime) }}
            </div>
          </div>
          <div class="comment-text">
            {{ comment.content }}
          </div>
          <div class="comment-actions">
          <el-button
              link
              :type="comment.liked ? 'primary' : ''"
              @click="handleLike(comment)"
            >
            <el-icon><StarFilled /></el-icon>
              {{ comment.likeCount }}
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="comments.length === 0 && !loading" class="empty-comments">
        <el-empty description="暂无评论" />
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-comments">
        <el-skeleton :rows="3" animated />
      </div>

      <!-- 分页 -->
      <div v-if="showPagination && pagination.total > 0" class="comment-pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          :page-size="pagination.size"
          :total="pagination.total"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive,  watch } from 'vue'
import { ElMessage } from 'element-plus'
import { StarFilled } from '@element-plus/icons-vue'
import StarRating from '@/components/common/StarRating.vue'
import { formatDateTime } from '@/utils'
import { commentApi } from '@/api'

const props = defineProps({
  movieId: {
    type: [String, Number],
    required: true
  },
  showStats: {
    type: Boolean,
    default: false
  },
  showSort: {
    type: Boolean,
    default: false
  },
  showRatingDistribution: {
    type: Boolean,
    default: false
  },
  showPagination: {
    type: Boolean,
    default: true
  }
})

// const emit = defineEmits(['success'])

const comments = ref([])
const stats = ref(null)
const loading = ref(false)

const sortOption = ref('createTime,desc')
const sortOptions = [
  { label: '最新评论', value: 'createTime,desc' },
  { label: '最早评论', value: 'createTime,asc' },
  { label: '最高评分', value: 'rating,desc' },
  { label: '最低评分', value: 'rating,asc' }
]

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const ratingDistribution = ref([])

// 监听电影ID变化
watch(() => props.movieId, (newId) => {
  if (newId) {
    loadComments()
    if (props.showStats) {
      loadStats()
    }
  }
}, { immediate: true })

// 加载评论列表（函数声明可提升，避免 watcher immediate 前未初始化）
async function loadComments() {
  loading.value = true
  try {
    // 调用后端接口获取电影评论，兼容常见分页返回结构
    const params = {
      page: pagination.page - 1, // 后端可能从0开始
      size: pagination.size,
      sort: sortOption.value
    }
    const res = await commentApi.getMovieComments(props.movieId, params)
    // 支持多种后端返回结构：{ data: { content: [], totalElements } } 或 { data: [], total }
    const data = res.data || res
    if (Array.isArray(data)) {
      comments.value = data
      pagination.total = data.length
    } else if (data.content) {
      comments.value = data.content
      pagination.total = data.totalElements || data.total || 0
    } else if (data.data && Array.isArray(data.data)) {
      comments.value = data.data
      pagination.total = data.total || 0
    } else {
      // 兜底：尝试使用 data.items
      comments.value = data.items || []
      pagination.total = data.total || comments.value.length
    }
  } catch (error) {
    console.error('加载评论失败:', error)
    ElMessage.error('加载评论失败')
  } finally {
    loading.value = false
  }
}

// 加载统计信息
async function loadStats() {
  try {
    const res = await commentApi.getCommentStats(props.movieId)
    const d = res.data || res
    // 兼容不同结构
    stats.value = d
    const dist = d.ratingDistribution || d.distribution || []
    const total = d.totalCount || d.total || (dist.reduce((s, it) => s + (it.count || 0), 0))
    if (dist && dist.length) {
      ratingDistribution.value = dist.map(item => ({
        ...item,
        percentage: total > 0 ? Math.round(((item.count || 0) / total) * 100) : 0
      }))
    } else {
      ratingDistribution.value = []
    }
  } catch (error) {
    console.error('加载评论统计失败:', error)
  }
}

// 处理排序变化
const handleSortChange = (value) => {
  sortOption.value = value
  pagination.page = 1
  loadComments()
}

// 处理分页变化
const handlePageChange = (page) => {
  pagination.page = page
  loadComments()
}

// 处理点赞
const handleLike = async (comment) => {
  try {
    // 调用后端点赞/取消点赞接口
    if (comment.liked) {
      await commentApi.unlikeComment(comment.id)
      comment.likeCount = Math.max(0, (comment.likeCount || 1) - 1)
      comment.liked = false
      ElMessage.success('取消点赞')
    } else {
      await commentApi.likeComment(comment.id)
      comment.likeCount = (comment.likeCount || 0) + 1
      comment.liked = true
      ElMessage.success('点赞成功')
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('操作失败')
  }
}

// 格式化时间
const formatDate = (dateString) => {
  return formatDateTime(dateString)
}

// 刷新评论列表
const refresh = () => {
  loadComments()
  if (props.showStats) {
    loadStats()
  }
}

defineExpose({
  refresh
})
</script>

<style scoped lang="scss">
@use 'sass:color';

.comment-list {
  .comment-stats {
    background: $bg-white;
    border-radius: $border-radius-base;
    padding: $spacing-lg;
    margin-bottom: $spacing-lg;
    box-shadow: $shadow-light;

    .stats-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: $spacing-lg;

      .stats-title {
        font-size: $font-size-large;
        font-weight: 600;
        color: $text-primary;
        margin: 0;
      }

      .stats-summary {
        .average-rating {
          display: flex;
          align-items: center;
          gap: $spacing-sm;

          .rating-score {
            font-size: 24px;
            font-weight: 700;
            color: $warning-color;
          }

          .rating-stars {
            :deep(.el-rate__icon) {
              font-size: 16px;
            }
          }

          .rating-count {
            color: $text-secondary;
            font-size: $font-size-small;
            margin-left: $spacing-md;
          }
        }
      }
    }

    .rating-distribution {
      .distribution-item {
        display: flex;
        align-items: center;
        gap: $spacing-md;
        margin-bottom: $spacing-sm;

        &:last-child {
          margin-bottom: 0;
        }

        .rating-label {
          width: 30px;
          font-size: $font-size-small;
          color: $text-secondary;
        }

        .rating-progress {
          flex: 1;

          :deep(.el-progress-bar__outer) {
            background: color.adjust($warning-color, $lightness: 25%);
          }

          :deep(.el-progress-bar__inner) {
            background: $warning-color;
          }
        }

        .rating-count {
          width: 30px;
          text-align: right;
          font-size: $font-size-small;
          color: $text-secondary;
        }
      }
    }
  }

  .comment-sort {
    margin-bottom: $spacing-lg;
    text-align: right;

    :deep(.el-select) {
      width: 120px;
    }
  }

  .comments-container {
    .comment-item {
      display: flex;
      gap: $spacing-md;
      padding: $spacing-lg;
      border-bottom: 1px solid $border-light;

      &:last-child {
        border-bottom: none;
      }

      .comment-avatar {
        flex-shrink: 0;
      }

      .comment-content {
        flex: 1;
        min-width: 0;

        .comment-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: $spacing-sm;

          .user-info {
            display: flex;
            align-items: center;
            gap: $spacing-sm;

            .username {
              font-weight: 500;
              color: $text-primary;
            }

            .comment-rating {
              :deep(.el-rate__icon) {
                font-size: 14px;
              }
            }
          }

          .comment-time {
            color: $text-secondary;
            font-size: $font-size-small;
          }
        }

        .comment-text {
          color: $text-regular;
          line-height: 1.6;
          margin-bottom: $spacing-sm;
          white-space: pre-wrap;
        }

        .comment-actions {
          :deep(.el-button) {
            color: $text-secondary;
            font-size: $font-size-small;

            &:hover {
              color: $primary-color;
            }
          }
        }
      }
    }

    .empty-comments,
    .loading-comments {
      padding: $spacing-xl;
      text-align: center;
    }

    .comment-pagination {
      margin-top: $spacing-lg;
      text-align: center;
    }
  }
}
</style>
