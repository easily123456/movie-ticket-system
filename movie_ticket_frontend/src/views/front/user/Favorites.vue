<template>
  <div class="user-favorites-page">
    <div class="page-header">
      <h1 class="page-title">我的收藏</h1>
      <p class="page-subtitle">查看您收藏的所有电影</p>
    </div>
    <div class="page-content">
      <!-- 收藏统计 -->
      <div class="favorites-stats">
        <div class="stat-card">
          <div class="stat-value">{{ totalCount }}</div>
          <div class="stat-label">收藏总数</div>
        </div>
      </div>
      <!-- 收藏列表 -->
      <div class="favorites-list">
        <div v-if="favorites.length" class="movies-grid">
          <div
            v-for="movie in favorites"
            :key="movie.id"
            class="movie-item"
          >
            <MovieCard
              :movie="movie"
              @book="handleBookMovie"
              @detail="handleViewDetail"
            />
            <div class="movie-actions">
              <el-button
                type="danger"
                size="small"
                @click="handleRemoveFavorite(movie.id)"
              >
                取消收藏
              </el-button>
            </div>
          </div>
        </div>
        <!-- 空状态 -->
        <div v-else-if="!loading" class="empty-container">
          <el-empty description="暂无收藏">
            <el-button type="primary" @click="$router.push('/movies')">
              去发现电影
            </el-button>
          </el-empty>
        </div>
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>
        <!-- 加载更多 -->
        <div v-if="hasMore && !loading" class="load-more">
          <el-button
            type="primary"
            link
            @click="handleLoadMore"
          >
            加载更多
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useFavoriteStore } from '@/stores/favorite'
import MovieCard from '@/components/front/MovieCard.vue'
import { ElMessage, ElMessageBox } from 'element-plus'


defineOptions({
  name: 'UserFavoritesPage'
})

const router = useRouter()
const favoriteStore = useFavoriteStore()

const loading = ref(false)

// 计算属性
const favorites = computed(() => favoriteStore.favorites)
const totalCount = computed(() => favoriteStore.pagination.total)
const hasMore = computed(() => favoriteStore.pagination.page < favoriteStore.pagination.totalPages)

// 初始化数据
onMounted(async () => {
  await loadFavorites()
})

// 加载收藏列表
const loadFavorites = async () => {
  loading.value = true
  try {
    await favoriteStore.getFavorites()
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    ElMessage.error('加载收藏列表失败')
  } finally {
    loading.value = false
  }
}

// 处理取消收藏
const handleRemoveFavorite = async (movieId) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await favoriteStore.removeFavorite(movieId)
    ElMessage.success('取消收藏成功')
    // 重新加载列表
    await loadFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消收藏失败')
    }
  }
}

// 处理购票
const handleBookMovie = (movie) => {
  router.push(`/movie/${movie.id}/sessions`)
}

// 处理查看详情
const handleViewDetail = (movie) => {
  router.push(`/movie/${movie.id}`)
}

// 处理加载更多
const handleLoadMore = async () => {
  try {
    await favoriteStore.loadMore()
  } catch (error) {
    console.error('加载更多失败:', error)
    ElMessage.error('加载更多失败')
  }
}
</script>
<style scoped lang="scss">
.user-favorites-page {
  max-width: 1200px;
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

.favorites-stats {
  margin-bottom: 30px;

  .stat-card {
    display: inline-block;
    padding: 20px 30px;
    background: $bg-white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    text-align: center;

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

.favorites-list {
  .movies-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 24px;
    margin-bottom: 32px;
  }

  .movie-item {
    position: relative;

    .movie-actions {
      position: absolute;
      top: 10px;
      right: 10px;
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    &:hover .movie-actions {
      opacity: 1;
    }
  }
}

.empty-container,
.loading-container {
  padding: 60px 0;
}

.load-more {
  text-align: center;
  padding: 20px 0;
}

@media (max-width: 768px) {
  .user-favorites-page {
    padding: 16px;
  }

  .movies-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)) !important;
    gap: 16px !important;
  }

  .movie-actions {
    opacity: 1 !important;
    position: static !important;
    margin-top: 12px;
    text-align: center;
  }
}
</style>
