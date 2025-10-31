<template>
  <div class="user-comments-page">
    <div class="page-header">
      <h1 class="page-title">我的评论</h1>
      <p class="page-subtitle">管理您发表的所有评论</p>
    </div>
    <div class="page-content">
      <!-- 评论统计 -->
      <div class="comments-stats">
        <div class="stat-card">
          <div class="stat-value">{{ totalComments }}</div>
          <div class="stat-label">总评论数</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ avgRating }}</div>
          <div class="stat-label">平均评分</div>
        </div>
      </div>
      <!-- 评论列表 -->
      <div class="comments-list">
        <div v-if="userComments.length" class="comments-container">
          <div
            v-for="comment in userComments"
            :key="comment.id"
            class="comment-item"
          >
            <div class="comment-movie">
              <img
                :src="getMoviePoster(comment.movieId)"
                :alt="comment.movieTitle"
                class="movie-poster"
                @click="goToMovieDetail(comment.movieId)"
              />
              <div class="movie-info">
                <h3
                  class="movie-title"
                  @click="goToMovieDetail(comment.movieId)"
                >
                  {{ getMovieTitle(comment.movieId) }}
                </h3>
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
            <div class="comment-content">
              <p class="content-text">{{ comment.content }}</p>
              <div class="comment-meta">
                <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                <span class="like-count">
                  <el-icon><Star /></el-icon>
                  {{ comment.likeCount }}
                </span>
              </div>
            </div>
            <div class="comment-actions">
              <el-button
                type="primary"
                link
                @click="goToMovieDetail(comment.movieId)"
              >
                查看电影
              </el-button>
              <el-button
                type="danger"
                link
                @click="handleDeleteComment(comment.id)"
              >
                删除
              </el-button>
            </div>
          </div>
        </div>
        <!-- 空状态 -->
        <div v-else-if="!loading" class="empty-container">
          <el-empty description="暂无评论">
            <el-button type="primary" @click="$router.push('/movies')">
              去发现电影
            </el-button>
          </el-empty>
        </div>
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="3" animated />
        </div>
        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="totalComments"
            :background="true"
            layout="prev, pager, next, jumper, total"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCommentStore } from '@/stores/comments'
import { useMovieStore } from '@/stores/movie'
import { Star } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'


defineOptions({
  name: 'UserCommentsPage'
})

const router = useRouter()
const commentStore = useCommentStore()
const movieStore = useMovieStore()

const currentPage = ref(1)
const pageSize = ref(10)

// 计算属性
const userComments = computed(() => commentStore.userComments)
const loading = computed(() => commentStore.loading)
const totalComments = computed(() => commentStore.userComments.length)

const avgRating = computed(() => {
  if (userComments.value.length === 0) return 0
  const total = userComments.value.reduce((sum, comment) => sum + comment.rating, 0)
  return (total / userComments.value.length).toFixed(1)
})

// 初始化数据
onMounted(async () => {
  await loadUserComments()
})

// 加载用户评论
const loadUserComments = async () => {
  try {
    await commentStore.getUserComments({
      page: currentPage.value - 1,
      size: pageSize.value
    })
  } catch (error) {
    console.error('加载评论失败:', error)
    ElMessage.error('加载评论失败')
  }
}

// 获取电影标题
const getMovieTitle = (movieId) => {
  // 从电影store中查找对应的电影信息
  const movie = movieStore.movies.find(m => m.id === movieId)
  return movie ? movie.title : '未知电影'
}

// 获取电影海报
const getMoviePoster = (movieId) => {
  // 从电影store中查找对应的电影信息
  const movie = movieStore.movies.find(m => m.id === movieId)
  return movie ? movie.poster : '/images/default-movie-poster.jpg'
}

// 跳转到电影详情
const goToMovieDetail = (movieId) => {
  router.push(`/movie/${movieId}`)
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
    await loadUserComments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 处理页码变化
const handlePageChange = () => {
  loadUserComments()
}

// 格式化时间
const formatTime = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>
<style scoped lang="scss">
.user-comments-page {
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
    margin: 0 0 8px 0;
  }

  .page-subtitle {
    font-size: 16px;
    color: $text-regular;
    margin: 0;
  }
}

.comments-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;

  .stat-card {
    flex: 1;
    background: $bg-white;
    border-radius: 12px;
    padding: 24px;
    text-align: center;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .stat-value {
      font-size: 32px;
      font-weight: 700;
      color: $primary-color;
      margin-bottom: 8px;
    }

    .stat-label {
      font-size: 14px;
      color: $text-regular;
    }
  }
}

.comments-list {
  .comments-container {
    display: flex;
    flex-direction: column;
    gap: 16px;
    margin-bottom: 24px;
  }
}

.comment-item {
  background: $bg-white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 16px;

  .comment-movie {
    flex: 0 0 120px;
    display: flex;
    flex-direction: column;
    gap: 12px;

    .movie-poster {
      width: 100%;
      height: 160px;
      border-radius: 8px;
      object-fit: cover;
      cursor: pointer;
      transition: transform 0.2s ease;

      &:hover {
        transform: scale(1.05);
      }
    }

    .movie-info {
      .movie-title {
        font-size: 14px;
        font-weight: 600;
        color: $text-primary;
        margin: 0;
        cursor: pointer;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;

        &:hover {
          color: $primary-color;
        }
      }
    }
  }

  .comment-content {
    flex: 1;

    .content-text {
      font-size: 14px;
      line-height: 1.6;
      color: $text-primary;
      margin: 0 0 12px 0;
    }

    .comment-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .comment-time {
        font-size: 12px;
        color: $text-secondary;
      }

      .like-count {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        color: $text-regular;
      }
    }
  }

  .comment-actions {
    flex: 0 0 auto;
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
}

.empty-container,
.loading-container {
  padding: 60px 0;
}

.pagination-container {
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .user-comments-page {
    padding: 16px;
  }

  .comments-stats {
    flex-direction: column;
  }

  .comment-item {
    flex-direction: column;

    .comment-movie {
      flex-direction: row;
      align-items: center;

      .movie-poster {
        width: 80px;
        height: 120px;
      }
    }

    .comment-actions {
      flex-direction: row;
      justify-content: flex-end;
    }
  }
}
</style>
