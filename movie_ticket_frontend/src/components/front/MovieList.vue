<template>
  <div class="movie-list-page">
    <div class="page-header">
      <h1 class="page-title">电影列表</h1>
      <p class="page-subtitle">发现精彩电影，享受视听盛宴</p>
    </div>
    <div class="page-content">
      <!-- 筛选区域 -->
      <div class="filter-container">
        <MovieFilter
          :filters="movieStore.filters"
          :genres="genres"
          @filter-change="handleFilterChange"
        />
      </div>
      <!-- 主要内容区域 -->
      <div class="main-content">
        <!-- 排序和显示选项 -->
        <div class="content-header">
          <div class="results-info">
            <span class="total-count">共找到 {{ movieStore.pagination.total }} 部电影</span>
            <span v-if="movieStore.filters.keyword" class="search-keyword">
              搜索关键词: "{{ movieStore.filters.keyword }}"
            </span>
          </div>

          <div class="view-options">
            <el-select v-model="pageSize" @change="handlePageSizeChange" style="width: 120px">
              <el-option label="12条/页" :value="12" />
              <el-option label="24条/页" :value="24" />
              <el-option label="48条/页" :value="48" />
            </el-select>
          </div>
        </div>
        <!-- 电影网格 -->
        <div class="movies-grid">
          <div
            v-for="movie in movieStore.movies"
            :key="movie.id"
            class="movie-item"
          >
            <MovieCard
              :movie="movie"
              @buy-ticket="handleBookMovie"
              @favorite="handleViewDetail"
            />
          </div>
        </div>
        <!-- 加载状态 -->
        <div v-if="movieStore.loading" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>
        <!-- 空状态 -->
        <div v-else-if="movieStore.isEmpty" class="empty-container">
          <el-empty description="暂无电影数据">
            <el-button type="primary" @click="handleResetFilters">重置筛选条件</el-button>
          </el-empty>
        </div>
        <!-- 分页和加载更多 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="movieStore.pagination.page"
            :page-size="movieStore.pagination.size"
            :total="movieStore.pagination.total"
            :background="true"
            layout="prev, pager, next, jumper, total"
            @current-change="handlePageChange"
          />

          <div v-if="movieStore.hasMore" class="load-more">
            <el-button
              :loading="movieStore.loading"
              @click="handleLoadMore"
              type="primary"
              link
            >
              加载更多
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useMovieStore } from '@/stores/movie'
import { useGenreStore } from '@/stores/genre'
import MovieFilter from '@/views/front/MovieFilter.vue'
import MovieCard from '@/components/front/MovieCard.vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const movieStore = useMovieStore()
const genreStore = useGenreStore()

const genres = ref([])
const pageSize = ref(12)

// 初始化数据
onMounted(async () => {
  try {
    // 获取电影类型
    await loadGenres()
    // 获取电影列表
    await movieStore.getMovies()
  } catch (error) {
    console.error('初始化数据失败:', error)
    ElMessage.error('初始化数据失败')
  }
})

// 加载电影类型
const loadGenres = async () => {
  try {
    genres.value = await genreStore.getGenres()
  } catch (error) {
    console.error('加载电影类型失败:', error)
  }
}

// 处理筛选条件变化
const handleFilterChange = async (filters) => {
  movieStore.updateFilters(filters)
  await movieStore.getMovies()
}

// 处理页码变化
const handlePageChange = async (page) => {
  movieStore.pagination.page = page
  await movieStore.getMovies()
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 处理每页大小变化
const handlePageSizeChange = async (size) => {
  movieStore.pagination.size = size
  movieStore.pagination.page = 1
  await movieStore.getMovies()
}

// 处理加载更多
const handleLoadMore = async () => {
  try {
    await movieStore.loadMore()
  } catch (error) {
    console.error('加载更多失败:', error)
    ElMessage.error('加载更多失败')
  }
}

// 重置筛选条件
const handleResetFilters = async () => {
  movieStore.resetFilters()
  await movieStore.getMovies()
}

// 处理购票
const handleBookMovie = (movie) => {
  router.push(`/movie/${movie.id}/sessions`)
}

// 处理查看详情
const handleViewDetail = (movie) => {
  router.push(`/movie/${movie.id}`)
}

// 监听筛选条件变化
watch(
  () => movieStore.filters,
  async () => {
    await movieStore.getMovies()
  },
  { deep: true }
)
</script>
<style scoped lang="scss">
.movie-list-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: $spacing-lg;
}

.page-header {
  text-align: center;
  margin-bottom: $spacing-xl;

  .page-title {
    font-size: 32px;
    font-weight: 700;
    color: $text-primary;
    margin: 0 0 $spacing-xs 0;
  }

  .page-subtitle {
    font-size: $font-size-base;
    color: $text-regular;
    margin: 0;
  }
}

.page-content {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: $spacing-lg;
}

.filter-container {
  position: sticky;
  top: $spacing-lg;
  height: fit-content;
}

.main-content {
  min-height: 600px;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-lg;
  padding: $spacing-md;
  background: $bg-white;
  border-radius: $border-radius-base;
  box-shadow: $shadow-base;

  .results-info {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;

    .total-count {
      font-size: $font-size-base;
      color: $text-primary;
      font-weight: 500;
    }

    .search-keyword {
      font-size: $font-size-small;
      color: $text-regular;
    }
  }
}

.movies-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: $spacing-lg;
  margin-bottom: $spacing-xl;
}

.movie-item {
  transition: $transition-base;

  &:hover {
    transform: translateY(-2px);
  }
}

.loading-container {
  padding: $spacing-xl 0;
}

.empty-container {
  padding: $spacing-xxl 0;
}

.pagination-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-md;
  padding: $spacing-lg 0;

  .load-more {
    text-align: center;
  }
}

@media (max-width: $breakpoint-lg) {
  .page-content {
    grid-template-columns: 1fr;
    gap: $spacing-md;
  }

  .filter-container {
    position: static;
  }
}

@media (max-width: $breakpoint-sm) {
  .movie-list-page {
    padding: $spacing-sm;
  }

  .page-header {
    margin-bottom: $spacing-lg;

    .page-title {
      font-size: 24px;
    }
  }

  .movies-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: $spacing-md;
  }

  .content-header {
    flex-direction: column;
    gap: $spacing-sm;
    align-items: stretch;
    padding: $spacing-sm;
  }
}

@media (max-width: $breakpoint-xs) {
  .movies-grid {
    grid-template-columns: 1fr;
  }
}
</style>