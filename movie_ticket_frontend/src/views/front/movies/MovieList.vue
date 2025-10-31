<template>
  <div class="movie-list-page">
    <div class="container">
      <!-- 页面标题和搜索 -->
      <div class="page-header">
        <h1 class="page-title">电影列表</h1>
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索电影、演员、导演..."
            size="large"
            @keyup.enter="handleSearch"
            clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button :icon="Search" @click="handleSearch" />
            </template>
          </el-input>
        </div>
      </div>
      <!-- 筛选条件 -->
      <div class="filter-section">
        <div class="filter-group">
          <span class="filter-label">类型：</span>
          <el-radio-group v-model="filters.genreId" @change="handleFilterChange">
            <el-radio-button :label="null">全部</el-radio-button>
            <el-radio-button
              v-for="genre in genres"
              :key="genre.id"
              :label="genre.id"
            >
              {{ genre.name }}
            </el-radio-button>
          </el-radio-group>
        </div>
        <div class="filter-group">
          <span class="filter-label">状态：</span>
          <el-radio-group v-model="filters.status" @change="handleFilterChange">
            <el-radio-button :label="null">全部</el-radio-button>
            <el-radio-button label="hot">热映</el-radio-button>
            <el-radio-button label="upcoming">即将上映</el-radio-button>
          </el-radio-group>
        </div>
        <div class="filter-group">
          <span class="filter-label">排序：</span>
          <el-radio-group v-model="filters.sort" @change="handleFilterChange">
            <el-radio-button label="createTime,desc">最新</el-radio-button>
            <el-radio-button label="rating,desc">评分最高</el-radio-button>
            <el-radio-button label="releaseDate,desc">上映时间</el-radio-button>
          </el-radio-group>
        </div>
      </div>
      <!-- 电影列表 -->
      <div class="movie-list-section">
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>
        <template v-else>
          <div v-if="movies.length === 0" class="empty-state">
            <el-empty description="暂无电影数据" />
          </div>
          <div v-else class="movie-grid">
            <MovieCard
              v-for="movie in movies"
              :key="movie.id"
              :movie="movie"
              @buy-ticket="handleBuyTicket"
              @favorite="handleFavorite"
            />
          </div>
          <!-- 分页 -->
          <Pagination
            v-if="pagination.total > 0"
            :total="pagination.total"
            :page="pagination.page"
            :size="pagination.size"
            @change="handlePageChange"
          />
        </template>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { useMovieStore } from '@/stores/movie'
import { useGenreStore } from '@/stores/genre'
import MovieCard from '@/components/front/MovieCard.vue'
import Pagination from '@/components/common/Pagination.vue'

const route = useRoute()
const router = useRouter()
const movieStore = useMovieStore()
const genreStore = useGenreStore()

const loading = ref(false)
const searchKeyword = ref('')

const filters = reactive({
  genreId: null,
  status: null,
  sort: 'createTime,desc'
})

const movies = ref([])
const pagination = ref({
  page: 1,
  size: 12,
  total: 0,
  pages: 0
})

// 计算属性
const genres = ref([])

// 监听路由参数变化
watch(() => route.query, (newQuery) => {
  if (newQuery.search) {
    searchKeyword.value = newQuery.search
    handleSearch()
  }
}, { immediate: true })

onMounted(async () => {
  await loadInitialData()
  await loadMovies()
})

const loadInitialData = async () => {
  try {
    await genreStore.fetchGenres()
    genres.value = genreStore.genres
  } catch (error) {
    console.error('加载电影类型失败:', error)
  }
}

const loadMovies = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.value.page - 1,
      size: pagination.value.size
    }

    // 处理筛选条件
    if (filters.genreId) {
      params.genreId = filters.genreId
    }
    if (filters.status === 'hot') {
      params.isHot = true
    } else if (filters.status === 'upcoming') {
      params.upcoming = true
    }

    // 处理排序
    if (filters.sort) {
      const [sortField, sortDirection] = filters.sort.split(',')
      params.sort = sortField
      params.direction = sortDirection
    }

    // 处理搜索
    if (searchKeyword.value) {
      await movieStore.searchMovies(searchKeyword.value, params)
      movies.value = movieStore.searchResults
    } else {
      await movieStore.fetchMovies(params)
      movies.value = movieStore.movieList
    }

    pagination.value = { ...movieStore.pagination }
  } catch (error) {
    ElMessage.error('加载电影列表失败')
    console.error('加载电影列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.page = 1
  loadMovies()
}

const handleFilterChange = () => {
  pagination.value.page = 1
  loadMovies()
}

const handlePageChange = ({ page, size }) => {
  pagination.value.page = page
  pagination.value.size = size
  loadMovies()
}

const handleBuyTicket = (movie) => {
  router.push(`/movie/${movie.id}/sessions`)
}

const handleFavorite = (movie, isFavorited) => {
  console.log(`${isFavorited ? '取消收藏' : '收藏'}电影:`, movie.title)
}
</script>
<style scoped lang="scss">
.movie-list-page {
  padding: $spacing-lg 0 $spacing-xxl;
  min-height: 80vh;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $spacing-xl;

  @media (max-width: $breakpoint-sm) {
    flex-direction: column;
    align-items: stretch;
    gap: $spacing-md;
  }
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: $text-primary;
  margin: 0;
}

.search-box {
  width: 400px;

  @media (max-width: $breakpoint-sm) {
    width: 100%;
  }
}

.filter-section {
  background: $bg-white;
  padding: $spacing-lg;
  border-radius: $border-radius-base;
  box-shadow: $shadow-base;
  margin-bottom: $spacing-xl;
}

.filter-group {
  display: flex;
  align-items: center;
  margin-bottom: $spacing-md;

  &:last-child {
    margin-bottom: 0;
  }

  .filter-label {
    font-weight: 600;
    color: $text-primary;
    min-width: 60px;
    margin-right: $spacing-md;
  }

  :deep(.el-radio-group) {
    flex: 1;
  }

  @media (max-width: $breakpoint-sm) {
    flex-direction: column;
    align-items: stretch;

    .filter-label {
      margin-bottom: $spacing-sm;
      margin-right: 0;
    }
  }
}

.movie-list-section {
  min-height: 400px;
}

.loading-container {
  background: $bg-white;
  padding: $spacing-xl;
  border-radius: $border-radius-base;
  box-shadow: $shadow-base;
}

.empty-state {
  background: $bg-white;
  padding: $spacing-xxl;
  border-radius: $border-radius-base;
  box-shadow: $shadow-base;
  text-align: center;
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: $spacing-lg;
  margin-bottom: $spacing-xl;

  @media (max-width: $breakpoint-sm) {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: $spacing-md;
  }
}
</style>
