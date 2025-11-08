<template>
  <div class="movie-list-page">
    <div class="container">
      <!-- 页面标题和搜索 -->
      <div class="page-header">
        <h1 class="page-title">电影列表</h1>
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
// import { Search } from '@element-plus/icons-vue'
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

// 监听路由参数变化（将在文件下方，确保 loadMovies 已声明）

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
  loading.value = true //表示开始加载电影数据
  try {
    const params = {
      page: pagination.value.page - 1,// 后端页面从0开始
      size: pagination.value.size, // 后端大小
      status: true // 默认只显示已上架电影
    }

    // 处理筛选条件
    if (filters.genreId) {
      params.genreId = filters.genreId // 电影类型过滤
    }
    if (filters.status === 'hot') {
      // 仅设置 isHot（Boolean），不要设置 status=hot（会导致后端 Boolean 绑定失败）
      params.isHot = true
    } else if (filters.status === 'upcoming') {
      // 使用后端提供的 /api/movies/new 接口获取“即将/最新”电影列表
      // 这里直接调用 store 的 getNewMovies 方法，避免重写过滤逻辑。
      // 注意：后端返回的是 List 而非分页数据，所以我们将结果映射为组件需要的 movies 和分页信息。
      try {
        const limit = pagination.value.size || 12
        await movieStore.getNewMovies(limit)
        movies.value = movieStore.newMovies || []
        // 设置分页信息（newMovies 返回非分页列表）
        pagination.value = {
          page: 1,
          size: limit,
          total: movies.value.length,
          pages: 1
        }
        // 早退出，避免后面再次调用 fetch
        loading.value = false
        return
      } catch (err) {
        console.error('获取最新/即将上映电影失败:', err)
        // 继续走普通的 params 路径作为回退
      }
    }

    // 处理排序（使用 sortBy/sortOrder 与 store 统一，避免发送重复字段导致后端验证失败）
    if (filters.sort && params.sortBy === undefined) {
      const [sortField, sortDirection] = filters.sort.split(',')
      params.sortBy = sortField
      params.sortOrder = sortDirection
    }

    // 处理搜索：searchMovies 接口签名为 (keyword, page, size)
    if (searchKeyword.value) {
      const pageIndex = pagination.value.page - 1
      await movieStore.searchMovies(searchKeyword.value, pageIndex, pagination.value.size)
      movies.value = movieStore.searchResults
      // 更新分页信息（search 结果可能不同）
      pagination.value = { ...movieStore.pagination }
    } else {
      // 使用 fetchMovies 兼容旧调用，内部会调用 getMovies
      await movieStore.fetchMovies(params)
      // movieList 在 store 中已映射到 movies
      movies.value = movieStore.movieList
      // 若 store 返回了分页信息则合并
      pagination.value = { ...movieStore.pagination }
    }
  } catch (error) {
    ElMessage.error('加载电影列表失败')
    console.error('加载电影列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 监听路由参数变化（支持 ?search=, ?type=hot|upcoming, ?genreId= 等）
watch(() => route.query, (newQuery) => {
  if (newQuery.search) {
    searchKeyword.value = newQuery.search
    pagination.value.page = Number(newQuery.page) || 1
    handleSearch()
    return
  }

  // 支持从 Home 或其他页面传 type=hot 或 type=upcoming 的跳转
  if (newQuery.type) {
    if (newQuery.type === 'hot') {
      filters.status = 'hot'
    } else if (newQuery.type === 'upcoming') {
      filters.status = 'upcoming'
    } else {
      filters.status = null
    }
  }

  // 支持通过路由设置类型过滤（genreId）
  if (newQuery.genreId) {
    // 路由参数为字符串，转换为数字或保留字符串ID
    const gid = isNaN(Number(newQuery.genreId)) ? newQuery.genreId : Number(newQuery.genreId)
    filters.genreId = gid
  }

  // 触发加载
  loadMovies()
}, { immediate: false })

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
