<template>
  <div class="movie-detail-page">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/movies' }">电影</el-breadcrumb-item>
        <el-breadcrumb-item>{{ movie.title }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <!-- 电影基本信息 -->
    <div class="movie-hero">
      <div class="movie-poster">
        <img :src="movie.posterUrl || '/images/default-movie-poster.jpg'" :alt="movie.title" />
        <div class="movie-badges">
          <span v-if="movie.isHot" class="badge hot">热门</span>
          <span v-if="movie.isNew && !movie.isHot" class="badge new">新上映</span>
        </div>
      </div>

      <div class="movie-info">
        <h1 class="movie-title">{{ movie.title }}</h1>
        <p class="movie-original-title" v-if="movie.originalTitle && movie.originalTitle !== movie.title">
          {{ movie.originalTitle }}
        </p>

        <div class="movie-rating">
          <div class="rating-score">
            <span class="score">{{ movie.rating || 0 }}</span>
            <div class="score-detail">
              <!-- 显示来自评论的平均分（基于评论统计），与 movie.rating（来自 movie 表）区分 -->
              <span class="comment-average">观众均分：{{ formatCommentAverage(movie.commentAverageRating) }}</span>
              <span class="vote-count">{{ movie.voteCount || 0 }} 人评价</span>
            </div>
          </div>
        </div>
        <div class="movie-meta">
          <div class="meta-item">
            <span class="label">类型：</span>
            <span class="value">{{ movie.genreName }}</span>
          </div>
          <div class="meta-item">
            <span class="label">时长：</span>
            <span class="value">{{ movie.formattedDuration || '未知' }}</span>
          </div>
          <div class="meta-item">
            <span class="label">上映日期：</span>
            <span class="value">{{ formatDate(movie.releaseDate) }}</span>
          </div>
          <div class="meta-item">
            <span class="label">导演：</span>
            <span class="value">{{ movie.director || '未知' }}</span>
          </div>
          <div class="meta-item">
            <span class="label">主演：</span>
            <span class="value">{{ movie.actors || '未知' }}</span>
          </div>
          <div class="meta-item">
            <span class="label">国家/地区：</span>
            <span class="value">{{ movie.country || '未知' }}</span>
          </div>
          <div class="meta-item">
            <span class="label">语言：</span>
            <span class="value">{{ movie.language || '未知' }}</span>
          </div>
        </div>
        <div class="movie-actions">
          <el-button type="primary" size="large" @click="goToSessions">
            <el-icon><VideoPlay /></el-icon>
            立即购票
          </el-button>
          <FavoriteButton v-if="movieId" :movie-id="movieId" :show-count="true" size="large" />
          <el-button :type="isShared ? 'primary' : 'default'" size="large" @click="handleShare">
            <el-icon><Share /></el-icon>
            分享
          </el-button>
        </div>
      </div>
    </div>
    <!-- 电影内容区域 -->
    <div class="movie-content">
      <!-- 电影简介 -->
      <div class="content-section">
        <h2 class="section-title">电影简介</h2>
        <div class="section-content">
          <p class="movie-description">{{ movie.description || '暂无简介' }}</p>
        </div>
      </div>
      <!-- 预告片 -->
      <div v-if="movie.trailerUrl" class="content-section">
        <h2 class="section-title">预告片</h2>
        <div class="section-content">
          <div class="trailer-container">
            <video
              :src="movie.trailerUrl"
              controls
              class="trailer-video"

            >
            <!-- poster="/images/video-poster.jpg" -->
              您的浏览器不支持视频播放
            </video>
          </div>
        </div>
      </div>
      <!-- 评论区域 -->
      <div class="content-section">
        <h2 class="section-title">观众评论</h2>
        <div class="section-content">
          <CommentForm v-if="movieId" :movie-id="movieId" @success="handleCommentSuccess" />
          <CommentList
            v-if="movieId"
            :movie-id="movieId"
            :show-stats="true"
            :show-sort="true"
            :show-rating-distribution="true"
            ref="commentListRef"
          />
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMovieStore } from '@/stores/movie'
import { VideoPlay, Share } from '@element-plus/icons-vue'
import FavoriteButton from '@/components/front/FavoriteButton.vue'
// import StarRating from '@/components/common/StarRating.vue'
import CommentForm from '@/components/front/CommentForm.vue'
import CommentList from '@/components/front/CommentList.vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const movieStore = useMovieStore()

const movieId = route.params.id
const movie = ref({})
const commentListRef = ref()
const isShared = ref(false)

// 初始化数据
onMounted(async () => {
  try {
    await loadMovieDetail()
  } catch (error) {
    console.error('加载电影详情失败:', error)
    ElMessage.error('加载电影详情失败')
  }
})

// 加载电影详情
const loadMovieDetail = async () => {
  try {
    await movieStore.getMovieDetail(movieId)
    movie.value = movieStore.currentMovie || {}
  } catch (error) {
    console.error('加载电影详情失败:', error)
    throw error
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未知'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 格式化来自评论的平均分（可能为 null）
const formatCommentAverage = (v) => {
  if (v === null || v === undefined) return '暂无'
  const n = Number(v)
  if (Number.isNaN(n)) return '暂无'
  return n.toFixed(1)
}

// 跳转到场次页面
const goToSessions = () => {
  router.push(`/movie/${movieId}/sessions`)
}

// 处理分享
const handleShare = () => {
  // 立即标记为已点击分享（用户要求：点击即变色）
  isShared.value = true
  // 这里可以实现分享功能，例如复制链接到剪贴板
  const url = window.location.href
  if (navigator.clipboard) {
    navigator.clipboard.writeText(url).then(() => {
      ElMessage.success('链接已复制到剪贴板')
      // 标记为已点击分享，切换样式
    })
  } else {
    // 兼容性处理
    const textArea = document.createElement('textarea')
    textArea.value = url
    document.body.appendChild(textArea)
    textArea.select()
    document.execCommand('copy')
    document.body.removeChild(textArea)
    ElMessage.success('链接已复制到剪贴板')
  }
}

// 处理评论成功
const handleCommentSuccess = () => {
  // 刷新评论列表
  commentListRef.value.refresh()
  // 重新加载电影详情以更新评分
  loadMovieDetail()
}
</script>
<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.movie-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: $spacing-lg;
  font-family: $font-family-movie;
}

.page-header {
  margin-bottom: $spacing-md;

  :deep(.el-breadcrumb) {
    font-size: $font-size-large;
  }
}

.movie-hero {
  display: flex;
  gap: $spacing-xl;
  margin-bottom: $spacing-xl;
  background: $bg-white;
  border-radius: $border-radius-base;
  padding: $spacing-lg;
  box-shadow: $shadow-base;

  .movie-poster {
    flex: 0 0 300px;
    position: relative;

    img {
      width: 300px;
      height: 400px;
      border-radius: $border-radius-base;
      object-fit: cover;
      box-shadow: $shadow-dark;
    }

    .movie-badges {
      position: absolute;
      top: $spacing-sm;
      left: $spacing-sm;
      display: flex;
      flex-direction: column;
      gap: $spacing-xs;

      .badge {
        padding: $spacing-xs $spacing-sm;
        border-radius: $border-radius-small;
        font-size: $font-size-small;
        font-weight: 600;
        color: $bg-white;
        font-family: $font-family-movie;

        &.hot {
          background: $danger-color;
        }

        &.new {
          background: $success-color;
        }
      }
    }
  }

  .movie-info {
    flex: 1;

    .movie-title {
      font-size: 32px;
      font-weight: 700;
      color: $text-primary;
      margin: 0 0 $spacing-xs 0;
      line-height: 1.2;
      font-family: $font-family-movie;
    }

    .movie-original-title {
      font-size: $font-size-large;
      color: $text-regular;
      margin: 0 0 $spacing-lg 0;
      font-style: italic;
      font-family: $font-family-movie;
    }
  }
}

.movie-rating {
  margin-bottom: $spacing-lg;

  .rating-score {
    display: flex;
    align-items: center;
    gap: $spacing-lg;

    .score {
      font-size: 48px;
      font-weight: 700;
      color: $warning-color;
      line-height: 1;
      font-family: $font-family-movie;
    }

    .score-detail {
      display: flex;
      flex-direction: column;
      gap: $spacing-xs;

      .vote-count {
        font-size: $font-size-base;
        color: $text-secondary;
        font-family: $font-family-movie;
      }
    }
  }
}

.movie-meta {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: $spacing-sm;
  margin-bottom: $spacing-xl;

  .meta-item {
    display: flex;

    .label {
      width: 100px;
      font-size: $font-size-base;
      color: $text-regular;
      font-weight: 500;
      font-family: $font-family-movie;
    }

    .value {
      flex: 1;
      font-size: $font-size-base;
      color: $text-primary;
      font-family: $font-family-movie;
    }
  }
}

.movie-actions {
  display: flex;
  gap: $spacing-xxl; //按钮之间的间距

  .el-button {
    min-width: 120px;
    display: flex;
    align-items: center;
    gap: $spacing-xs;

    :deep(span) {
      font-family: $font-family-movie;
    }
  }
}

.movie-content {
  .content-section {
    background: $bg-white;
    border-radius: $border-radius-base;
    padding: $spacing-lg;
    margin-bottom: $spacing-lg;
    box-shadow: $shadow-base;

    .section-title {
      font-size: $font-size-big;
      font-weight: 600;
      color: $text-primary;
      margin: 0 0 $spacing-lg 0;
      padding-bottom: $spacing-sm;
      border-bottom: 2px solid $border-extra-light;
      position: relative;
      padding-left: $spacing-md;
      font-family: $font-family-movie;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        bottom: $spacing-sm;
        width: 7px;
        background-color: #f56c6c;
        border-radius: 2px;
      }
    }
  }
}

.movie-description {
  font-size: $font-size-base;
  line-height: 1.8;
  color: $text-primary;
  margin: 0;
  white-space: pre-line;
  font-family: $font-family-movie;
  text-indent: 2em; /* 首行缩进2字符 */
}

.trailer-container {
  display: flex;
  justify-content: center;

  .trailer-video {
    width: 100%;
    max-width: 800px;
    border-radius: $border-radius-small;
    background: #000;
  }
}

@media (max-width: $breakpoint-md) {
  .movie-detail-page {
    padding: $spacing-md;
  }

  .movie-hero {
    flex-direction: column;
    gap: $spacing-lg;
    padding: $spacing-md;

    .movie-poster {
      align-self: center;

      img {
        width: 250px;
        height: 350px;
      }
    }
  }

  .movie-rating {
    .rating-score {
      flex-direction: column;
      align-items: flex-start;
      gap: $spacing-sm;
    }
  }

  .movie-meta {
    grid-template-columns: 1fr;
  }

  .movie-actions {
    flex-direction: column;

    .el-button {
      width: 100%;
    }
  }

  .movie-content {
    .content-section {
      padding: $spacing-md;
    }
  }
}

@media (max-width: $breakpoint-sm) {
  .movie-hero {
    .movie-poster {
      img {
        width: 100%;
        height: auto;
        max-height: 500px;
      }
    }
  }
}
</style>
