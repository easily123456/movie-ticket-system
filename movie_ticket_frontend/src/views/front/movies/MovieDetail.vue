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
          <span v-if="movie.isNew" class="badge new">新上映</span>
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
              <el-rate
                v-model="movie.rating"
                disabled
                show-score
                text-color="#e6a23c"
                score-template="{value}"
              />
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
          <FavoriteButton :movie-id="movie.id" :show-count="true" size="large" />
          <el-button size="large" @click="handleShare">
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
              poster="/images/video-poster.jpg"
            >
              您的浏览器不支持视频播放
            </video>
          </div>
        </div>
      </div>
      <!-- 评论区域 -->
      <div class="content-section">
        <h2 class="section-title">观众评论</h2>
        <div class="section-content">
          <CommentForm :movie-id="movie.id" @success="handleCommentSuccess" />
          <CommentList
            :movie-id="movie.id"
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
import CommentForm from '@/components/front/CommentForm.vue'
import CommentList from '@/components/front/CommentList.vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const movieStore = useMovieStore()

const movieId = route.params.id
const movie = ref({})
const commentListRef = ref()

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

// 跳转到场次页面
const goToSessions = () => {
  router.push(`/movie/${movieId}/sessions`)
}

// 处理分享
const handleShare = () => {
  // 这里可以实现分享功能，例如复制链接到剪贴板
  const url = window.location.href
  if (navigator.clipboard) {
    navigator.clipboard.writeText(url).then(() => {
      ElMessage.success('链接已复制到剪贴板')
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
.movie-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.movie-hero {
  display: flex;
  gap: 40px;
  margin-bottom: 40px;
  background: $bg-white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

  .movie-poster {
    flex: 0 0 300px;
    position: relative;

    img {
      width: 300px;
      height: 400px;
      border-radius: 12px;
      object-fit: cover;
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    }

    .movie-badges {
      position: absolute;
      top: 12px;
      left: 12px;
      display: flex;
      flex-direction: column;
      gap: 8px;

      .badge {
        padding: 4px 8px;
        border-radius: 4px;
        font-size: 12px;
        font-weight: 600;
        color: $bg-white;

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
      margin: 0 0 8px 0;
      line-height: 1.2;
    }

    .movie-original-title {
      font-size: 18px;
      color: $text-regular;
      margin: 0 0 24px 0;
      font-style: italic;
    }
  }
}

.movie-rating {
  margin-bottom: 24px;

  .rating-score {
    display: flex;
    align-items: center;
    gap: 16px;

    .score {
      font-size: 48px;
      font-weight: 700;
      color: #e6a23c;
      line-height: 1;
    }

    .score-detail {
      display: flex;
      flex-direction: column;
      gap: 4px;

      .vote-count {
        font-size: 14px;
        color: $text-secondary;
      }
    }
  }
}

.movie-meta {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 30px;

  .meta-item {
    display: flex;

    .label {
      width: 100px;
      font-size: 14px;
      color: $text-regular;
      font-weight: 500;
    }

    .value {
      flex: 1;
      font-size: 14px;
      color: $text-primary;
    }
  }
}

.movie-actions {
  display: flex;
  gap: 12px;

  .el-button {
    min-width: 120px;
    display: flex;
    align-items: center;
    gap: 6px;
  }
}

.movie-content {
  .content-section {
    background: $bg-white;
    border-radius: 12px;
    padding: 24px;
    margin-bottom: 24px;
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
}

.movie-description {
  font-size: 16px;
  line-height: 1.8;
  color: $text-primary;
  margin: 0;
  white-space: pre-line;
}

.trailer-container {
  display: flex;
  justify-content: center;

  .trailer-video {
    width: 100%;
    max-width: 800px;
    border-radius: 8px;
    background: #000;
  }
}

@media (max-width: 768px) {
  .movie-detail-page {
    padding: 16px;
  }

  .movie-hero {
    flex-direction: column;
    gap: 20px;
    padding: 20px;

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
      gap: 8px;
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
      padding: 16px;
    }
  }
}

@media (max-width: 480px) {
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
