<template>
  <div class="comment-list">
    <!-- 评论统计 -->
    <div class="comment-stats" v-if="showStats && stats">
      <div class="stats-header">
        <h3 class="stats-title">用户评价</h3>
        <div class="stats-summary">
          <div class="average-rating">
            <span class="rating-score">{{ stats.averageRating?.toFixed(1) || '0.0' }}</span>
            <el-rate
              v-model="stats.averageRating"
              disabled
              :colors="['#909399', '#e6a23c', '#f56c6c']"
              :max="10"
              class="rating-stars"
            />
            <span class="rating-count">{{ stats.totalCount }}条评价</span>
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
              <el-rate
                v-model="comment.rating"
                disabled
                :colors="['#909399', '#e6a23c', '#f56c6c']"
                class="comment-rating"
              />
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
              <el-icon><Like /></el-icon>
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
import { Like } from '@element-plus/icons-vue'
import { formatDateTime } from '@/utils'

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

// 加载评论列表
const loadComments = async () => {
  loading.value = true
  try {
    // 这里应该调用API获取评论数据
    // const response = await commentApi.getCommentsByMovie(props.movieId, {
    //   page: pagination.page - 1,
    //   size: pagination.size,
    //   sort: sortOption.value
    // })
    // comments.value = response.data.content
    // pagination.total = response.data.totalElements

    // 模拟数据
    comments.value = [
      {
        id: 1,
        username: '张三',
        userAvatar: '',
        rating: 8,
        content: '这部电影真的很棒，剧情紧凑，演员演技在线，特效也很震撼！',
        likeCount: 25,
        liked: false,
        createTime: '2024-01-15T10:30:00'
      },
      {
        id: 2,
        username: '李四',
        userAvatar: '',
        rating: 6,
        content: '整体还行，但有些地方感觉节奏有点慢。',
        likeCount: 8,
        liked: true,
        createTime: '2024-01-14T15:45:00'
      }
    ]
    pagination.total = 2
  } catch (error) {
    console.error('加载评论失败:', error)
    ElMessage.error('加载评论失败')
  } finally {
    loading.value = false
  }
}

// 加载统计信息
const loadStats = async () => {
  try {
    // 这里应该调用API获取统计信息
    // const response = await commentApi.getCommentStats(props.movieId)
    // stats.value = response.data

    // 模拟数据
    stats.value = {
      averageRating: 7.5,
      totalCount: 128,
      ratingDistribution: [
        { rating: 10, count: 25 },
        { rating: 9, count: 30 },
        { rating: 8, count: 20 },
        { rating: 7, count: 15 },
        { rating: 6, count: 10 },
        { rating: 5, count: 8 },
        { rating: 4, count: 5 },
        { rating: 3, count: 3 },
        { rating: 2, count: 2 },
        { rating: 1, count: 1 }
      ]
    }

    // 计算评分分布百分比
    if (stats.value.ratingDistribution) {
      const total = stats.value.totalCount
      ratingDistribution.value = stats.value.ratingDistribution.map(item => ({
        ...item,
        percentage: total > 0 ? Math.round((item.count / total) * 100) : 0
      }))
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
    // 这里应该调用API处理点赞
    // await commentApi.likeComment(comment.id)

    if (comment.liked) {
      comment.likeCount--
    } else {
      comment.likeCount++
    }
    comment.liked = !comment.liked

    ElMessage.success(comment.liked ? '点赞成功' : '取消点赞')
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
@use "sass:color";
@use '@/assets/styles/variables.scss';

.comment-list {
  .comment-stats {
    background: variables.$bg-white;
    border-radius: variables.$border-radius-base;
    padding: variables.$spacing-lg;
    margin-bottom: variables.$spacing-lg;
    box-shadow: variables.$shadow-light;

    .stats-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: variables.$spacing-lg;

      .stats-title {
        font-size: 18px;
        font-weight: 600;
        color: variables.$text-primary;
        margin: 0;
      }

      .stats-summary {
        .average-rating {
          display: flex;
          align-items: center;
          gap: variables.$spacing-sm;

          .rating-score {
            font-size: 24px;
            font-weight: 700;
            color: variables.$warning-color;
          }

          .rating-stars {
            :deep(.el-rate__icon) {
              font-size: 16px;
            }
          }

          .rating-count {
            color: variables.$text-secondary;
            font-size: variables.$font-size-small;
          }
        }
      }
    }

    .rating-distribution {
      .distribution-item {
        display: flex;
        align-items: center;
        gap: variables.$spacing-md;
        margin-bottom: variables.$spacing-sm;

        &:last-child {
          margin-bottom: 0;
        }

        .rating-label {
          width: 30px;
          font-size: variables.$font-size-small;
          color: variables.$text-secondary;
        }

        .rating-progress {
          flex: 1;

          :deep(.el-progress-bar__outer) {
            background: color.adjust(variables.$warning-color, $lightness: 25%);
          }

          :deep(.el-progress-bar__inner) {
            background: variables.$warning-color;
          }
        }

        .rating-count {
          width: 30px;
          text-align: right;
          font-size: variables.$font-size-small;
          color: variables.$text-secondary;
        }
      }
    }
  }

  .comment-sort {
    margin-bottom: variables.$spacing-lg;
    text-align: right;

    :deep(.el-select) {
      width: 120px;
    }
  }

  .comments-container {
    .comment-item {
      display: flex;
      gap: variables.$spacing-md;
      padding: variables.$spacing-lg;
      border-bottom: 1px solid variables.$border-light;

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
          margin-bottom: variables.$spacing-sm;

          .user-info {
            display: flex;
            align-items: center;
            gap: variables.$spacing-sm;

            .username {
              font-weight: 500;
              color: variables.$text-primary;
            }

            .comment-rating {
              :deep(.el-rate__icon) {
                font-size: 14px;
              }
            }
          }

          .comment-time {
            color: variables.$text-secondary;
            font-size: variables.$font-size-small;
          }
        }

        .comment-text {
          color: variables.$text-regular;
          line-height: 1.6;
          margin-bottom: variables.$spacing-sm;
          white-space: pre-wrap;
        }

        .comment-actions {
          :deep(.el-button) {
            color: variables.$text-secondary;
            font-size: variables.$font-size-small;

            &:hover {
              color: variables.$primary-color;
            }
          }
        }
      }
    }

    .empty-comments,
    .loading-comments {
      padding: variables.$spacing-xl;
      text-align: center;
    }

    .comment-pagination {
      margin-top: variables.$spacing-lg;
      text-align: center;
    }
  }
}
</style>
