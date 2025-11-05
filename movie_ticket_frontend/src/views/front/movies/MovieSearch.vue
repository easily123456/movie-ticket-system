<template>
  <div class="movie-search-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">搜索电影</h1>
        <div class="search-box">
          <el-input
            v-model="keyword"
            placeholder="输入片名、演员、导演... 按回车搜索"
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

      <div class="result-section">
        <div v-if="searchLoading" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>
        <template v-else>
          <div v-if="results.length === 0" class="empty-state">
            <el-empty description="没有找到相关电影" />
          </div>
          <div v-else class="movie-grid">
            <MovieCard
              v-for="movie in results"
              :key="movie.id"
              :movie="movie"
              @buy-ticket="handleBuyTicket"
            />
          </div>
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
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { useMovieStore } from '@/stores/movie'
import MovieCard from '@/components/front/MovieCard.vue'
import Pagination from '@/components/common/Pagination.vue'

const route = useRoute()
const router = useRouter()
const movieStore = useMovieStore()

const keyword = ref('')
const results = ref([])
const searchLoading = movieStore.searchLoading
const pagination = ref({
  page: 1,
  size: 12,
  total: 0
})

// 从路由查询参数还原搜索
onMounted(() => {
  const q = route.query.keyword || route.query.q || ''
  const page = Number(route.query.page || 1)
  const size = Number(route.query.size || 12)
  keyword.value = String(q)
  pagination.value.page = page
  pagination.value.size = size
  if (keyword.value) {
    doSearch()
  }
})

// 监听地址栏参数变化（例如浏览器前进/后退）
watch(
  () => route.query,
  (q) => {
    const newKeyword = q.keyword || q.q || ''
    const newPage = Number(q.page || 1)
    const newSize = Number(q.size || 12)
    if (
      newKeyword !== keyword.value ||
      newPage !== pagination.value.page ||
      newSize !== pagination.value.size
    ) {
      keyword.value = String(newKeyword)
      pagination.value.page = newPage
      pagination.value.size = newSize
      if (keyword.value) {
        doSearch()
      } else {
        results.value = []
        pagination.value.total = 0
      }
    }
  }
)

const syncQuery = () => {
  router.replace({
    path: '/search',
    query: {
      keyword: keyword.value || undefined,
      page: pagination.value.page,
      size: pagination.value.size
    }
  })
}

const doSearch = async () => {
  if (!keyword.value) {
    results.value = []
    pagination.value.total = 0
    return
  }
  try {
    const resp = await movieStore.searchMovies(
      keyword.value,
      pagination.value.page - 1,
      pagination.value.size
    )
    const data = resp.data
    // 兼容分页与非分页返回
    results.value = data.content || data || []
    pagination.value.total = data.totalElements || data.length || 0
  } catch (e) {
    console.error('搜索失败:', e)
    ElMessage.error('搜索失败，请稍后重试')
  }
}

const handleSearch = () => {
  pagination.value.page = 1
  syncQuery()
  doSearch()
}

const handlePageChange = ({ page, size }) => {
  pagination.value.page = page
  pagination.value.size = size
  syncQuery()
  doSearch()
}

const handleBuyTicket = (movie) => {
  router.push(`/movie/${movie.id}/sessions`)
}
</script>

<style scoped lang="scss">
.movie-search-page {
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
  width: 420px;
  @media (max-width: $breakpoint-sm) {
    width: 100%;
  }
}

.result-section {
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


