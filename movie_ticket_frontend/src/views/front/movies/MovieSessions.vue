<template>
  <div class="movie-sessions-page">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/movies' }">电影</el-breadcrumb-item>
        <el-breadcrumb-item>{{ movie.title }}</el-breadcrumb-item>
        <el-breadcrumb-item>场次选择</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="content-container">
      <!-- 电影信息卡片 -->
      <div class="movie-info-card">
        <div class="movie-poster">
          <img
            v-if="movie.posterUrl"
            :src="movie.posterUrl"
            :alt="movie.title"
            class="poster-image"
          />
          <div v-else class="poster-placeholder">
            <el-icon><Picture /></el-icon>
          </div>
        </div>
        <div class="movie-details">
          <h2 class="movie-title">{{ movie.title }}</h2>
          <div class="movie-meta">
            <div class="meta-item">
              <span class="label">类型：</span>
              <span class="value">{{ movie.genreName }}</span>
            </div>
            <div class="meta-item">
              <span class="label">时长：</span>
              <span class="value">{{ formatDuration(movie.duration) }}</span>
            </div>
            <div class="meta-item">
              <span class="label">上映日期：</span>
              <span class="value">{{ formatDate(movie.releaseDate) }}</span>
            </div>
            <div class="meta-item">
              <span class="label">导演：</span>
              <span class="value">{{ movie.director }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 场次选择区域 -->
      <div class="sessions-section">
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="5" animated />
        </div>
        <div v-else-if="sessions.length === 0" class="empty-container">
          <el-empty description="暂无场次信息">
            <el-button type="primary" @click="loadSessions">刷新</el-button>
          </el-empty>
        </div>
        <SessionSelector
          v-else
          :movie-id="movieId"
          :sessions="sessions"
          @select="handleSessionSelect"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { useMovieStore } from '@/stores/movie'
import { useSessionStore } from '@/stores/session'
import SessionSelector from '@/components/front/SessionSelector.vue'
import { formatDuration, formatDate } from '@/utils'

const route = useRoute()
const router = useRouter()
const movieStore = useMovieStore()
const sessionStore = useSessionStore()

const movieId = route.params.id
const movie = ref({})
const sessions = ref([])
const loading = ref(false)

// 初始化数据
onMounted(async () => {
  try {
    await loadMovieDetail()
    await loadSessions()
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
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

// 加载场次信息
const loadSessions = async () => {
  loading.value = true
  try {
    const sessionList = await sessionStore.getSessionsByMovie(movieId)
    sessions.value = sessionList || []
  } catch (error) {
    console.error('加载场次信息失败:', error)
    ElMessage.error('加载场次信息失败')
  } finally {
    loading.value = false
  }
}

// 处理场次选择
const handleSessionSelect = (session) => {
  router.push(`/booking/${session.id}`)
}
</script>

<style scoped lang="scss">
.movie-sessions-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: $spacing-lg;
}

.page-header {
  margin-bottom: $spacing-lg;
}

.content-container {
  display: grid;
  grid-template-columns: 1fr;
  gap: $spacing-lg;
}

.movie-info-card {
  background: $bg-white;
  border-radius: $border-radius-base;
  padding: $spacing-lg;
  display: flex;
  gap: $spacing-lg;
  box-shadow: $shadow-base;

  .movie-poster {
    flex-shrink: 0;

    .poster-image {
      width: 150px;
      height: 200px;
      border-radius: $border-radius-small;
      object-fit: cover;
    }

    .poster-placeholder {
      width: 150px;
      height: 200px;
      border-radius: $border-radius-small;
      background: $bg-gray;
      display: flex;
      align-items: center;
      justify-content: center;
      color: $text-placeholder;

      .el-icon {
        font-size: 32px;
      }
    }
  }

  .movie-details {
    flex: 1;

    .movie-title {
      font-size: 24px;
      font-weight: 700;
      color: $text-primary;
      margin: 0 0 $spacing-md 0;
    }

    .movie-meta {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: $spacing-sm;

      .meta-item {
        display: flex;

        .label {
          width: 80px;
          font-size: $font-size-base;
          color: $text-regular;
          font-weight: 500;
        }

        .value {
          flex: 1;
          font-size: $font-size-base;
          color: $text-primary;
        }
      }
    }
  }
}

.sessions-section {
  background: $bg-white;
  border-radius: $border-radius-base;
  box-shadow: $shadow-base;
  overflow: hidden;
}

.loading-container,
.empty-container {
  padding: $spacing-xl;
}

@media (max-width: $breakpoint-sm) {
  .movie-sessions-page {
    padding: $spacing-md;
  }

  .movie-info-card {
    flex-direction: column;
    align-items: center;
    text-align: center;

    .movie-poster {
      .poster-image,
      .poster-placeholder {
        width: 120px;
        height: 160px;
      }
    }

    .movie-details {
      .movie-meta {
        grid-template-columns: 1fr;
      }
    }
  }
}
</style>