<template>
  <div class="movie-management-page">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <h1 class="page-title">电影管理</h1>
      <div class="header-actions">
        <el-button type="primary" :icon="Plus" @click="handleCreate">
          新增电影
        </el-button>
        <el-button :icon="Refresh" @click="refreshData">
          刷新
        </el-button>
        <el-button :icon="Download" @click="exportMovies">
          导出
        </el-button>
      </div>
    </div>
    <!-- 搜索和筛选区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入电影名称、导演或演员"
            clearable
            style="width: 280px"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="类型">
          <el-select
            v-model="searchForm.genreId"
            placeholder="全部类型"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="全部" value="" />
            <el-option
              v-for="genre in genres"
              :key="genre.id"
              :label="genre.name"
              :value="genre.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="全部状态"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="全部" value="" />
            <el-option label="上架" :value="true" />
            <el-option label="下架" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item label="热门">
          <el-select
            v-model="searchForm.isHot"
            placeholder="热门状态"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="全部" value="" />
            <el-option label="热门" :value="true" />
            <el-option label="普通" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <!-- 电影列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="table-header">
          <div class="table-title">
            <span>电影列表</span>
            <span class="total-text">共 {{ pagination.total }} 部电影</span>
          </div>
          <div class="table-actions">
            <el-button
              v-if="selectedMovies.length > 0"
              type="danger"
              :icon="Delete"
              @click="handleBatchDelete"
              :disabled="batchDeleting"
            >
              批量删除 ({{ selectedMovies.length }})
            </el-button>
            <el-button
              v-if="selectedMovies.length > 0"
              type="warning"
              :icon="ArrowDown"
              @click="handleBatchDisable"
              :disabled="batchOperating"
            >
              批量下架
            </el-button>
            <el-button
              v-if="selectedMovies.length > 0"
              type="success"
              :icon="ArrowUp"
              @click="handleBatchEnable"
              :disabled="batchOperating"
            >
              批量上架
            </el-button>
            <el-button
              v-if="selectedMovies.length > 0"
              type="primary"
              :icon="Star"
              @click="handleBatchSetHot"
              :disabled="batchOperating"
            >
              批量设热门
            </el-button>
          </div>
        </div>
      </template>
      <div v-loading="loading">
        <el-table
          :data="movies"
          style="width: 100%"
          @selection-change="handleSelectionChange"
          @sort-change="handleSortChange"
        >
          <el-table-column type="selection" width="55" />

          <el-table-column label="电影信息" min-width="300">
            <template #default="{ row }">
              <div class="movie-info">
                <el-image
                  :src="row.posterUrl"
                  :alt="row.title"
                  class="movie-poster"
                  fit="cover"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
                <div class="movie-details">
                  <div class="movie-title">
                    <span>{{ row.title }}</span>
                    <el-tag v-if="row.isHot" type="danger" size="small">热门</el-tag>
                  </div>
                  <div class="movie-original-title">{{ row.originalTitle }}</div>
                  <div class="movie-meta">
                    <span>{{ row.genreName }}</span>
                    <span>{{ formatDuration(row.duration) }}</span>
                    <span>{{ row.country }}</span>
                  </div>
                  <div class="movie-rating">
                    <el-rate
                      v-model="row.rating"
                      disabled
                      show-score
                      text-color="#e6a23c"
                      score-template="{value}"
                      size="small"
                    />
                    <span class="vote-count">({{ row.voteCount }})</span>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="导演" prop="director" width="120">
            <template #default="{ row }">
              <span class="text">{{ row.director || '未知' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="演员" min-width="150">
            <template #default="{ row }">
              <el-tooltip
                v-if="row.actors && row.actors.length > 20"
                :content="row.actors"
                placement="top"
              >
                <span class="text">{{ row.actors?.substring(0, 20) }}...</span>
              </el-tooltip>
              <span v-else class="text">{{ row.actors || '未知' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="上映时间" width="120" sortable="custom" prop="releaseDate">
            <template #default="{ row }">
              <span class="time-text">{{ formatDate(row.releaseDate) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="价格" width="100" sortable="custom" prop="price">
            <template #default="{ row }">
              <span class="price">{{ formatPrice(row.price) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status ? 'success' : 'danger'" size="small">
                {{ row.status ? '上架' : '下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="160" sortable="custom" prop="createTime">
            <template #default="{ row }">
              <span class="time-text">{{ formatTime(row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button
                  link
                  type="primary"
                  :icon="View"
                  @click="handleViewDetail(row)"
                >
                  详情
                </el-button>

                <el-button
                  link
                  type="primary"
                  :icon="Edit"
                  @click="handleEdit(row)"
                >
                  编辑
                </el-button>
                <el-button
                  v-if="row.status"
                  link
                  type="warning"
                  :icon="ArrowDown"
                  @click="handleDisable(row)"
                  :loading="row.disableLoading"
                >
                  下架
                </el-button>

                <el-button
                  v-else
                  link
                  type="success"
                  :icon="ArrowUp"
                  @click="handleEnable(row)"
                  :loading="row.enableLoading"
                >
                  上架
                </el-button>
                <el-button
                  v-if="!row.isHot"
                  link
                  type="info"
                  :icon="Star"
                  @click="handleSetHot(row, true)"
                  :loading="row.setHotLoading"
                >
                  设热门
                </el-button>
                <el-button
                  v-else
                  link
                  type="info"
                  :icon="StarFilled"
                  @click="handleSetHot(row, false)"
                  :loading="row.setHotLoading"
                >
                  取消热门
                </el-button>
                <el-button
                  link
                  type="danger"
                  :icon="Delete"
                  @click="handleDelete(row)"
                  :loading="row.deleteLoading"
                >
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>
    <!-- 电影详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="电影详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <MovieDetail :movie="selectedMovie" v-if="detailDialogVisible" />
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="formDialogVisible"
      :title="formTitle"
      width="800px"
      :close-on-click-modal="false"
      @closed="handleFormDialogClosed"
    >
      <MovieForm
        ref="movieFormRef"
        :movie="formData"
        :genres="genres"
        :is-edit="isEdit"
        @submit="handleFormSubmit"
        @cancel="formDialogVisible = false"
      />
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Refresh,
  Download,
  Search,
  Edit,
  Delete,
  ArrowDown,
  ArrowUp,
  Star,
  StarFilled,
  View,
  Picture
} from '@element-plus/icons-vue'
import { formatTime, formatDate, formatPrice, formatDuration } from '@/utils'
import { useAdminStore } from '@/stores/admin'
import { useGenreStore } from '@/stores/genre'
import MovieDetail from '@/components/admin/MovieDetail.vue'
import MovieForm from '@/components/admin/MovieForm.vue'

const adminStore = useAdminStore()
const genreStore = useGenreStore()

// 响应式数据
const loading = ref(false)
const batchOperating = ref(false)
const batchDeleting = ref(false)
const detailDialogVisible = ref(false)
const formDialogVisible = ref(false)
const isEdit = ref(false)

const movies = ref([])
const genres = ref([])
const selectedMovies = ref([])
const selectedMovie = ref(null)
const movieFormRef = ref()

const searchForm = reactive({
  keyword: '',
  genreId: '',
  status: '',
  isHot: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const sort = reactive({
  field: 'createTime',
  order: 'desc'
})

const formData = ref(null)
const formTitle = ref('新增电影')

onMounted(async () => {
  await loadGenres()
  await loadMovies()
})

// 加载电影类型
const loadGenres = async () => {
  try {
    await genreStore.fetchGenres()
    genres.value = genreStore.genres
  } catch (error) {
    console.error('加载电影类型失败:', error)
  }
}

// 加载电影列表
const loadMovies = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
      sort: `${sort.field},${sort.order}`
    }

    // 添加搜索条件
    if (searchForm.keyword) {
      params.keyword = searchForm.keyword
    }
    if (searchForm.genreId) {
      params.genreId = searchForm.genreId
    }
    if (searchForm.status !== '') {
      params.status = searchForm.status
    }
    if (searchForm.isHot !== '') {
      params.isHot = searchForm.isHot
    }

    const response = await adminStore.getMovies(params)
    movies.value = response.content || response
    pagination.total = response.totalElements || response.length || 0

    // 重置操作状态
    movies.value.forEach(movie => {
      movie.disableLoading = false
      movie.enableLoading = false
      movie.setHotLoading = false
      movie.deleteLoading = false
    })
  } catch (error) {
    ElMessage.error('加载电影列表失败')
    console.error('加载电影列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索电影
const handleSearch = () => {
  pagination.page = 1
  loadMovies()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    genreId: '',
    status: '',
    isHot: ''
  })
  pagination.page = 1
  loadMovies()
}

// 刷新数据
const refreshData = () => {
  loadMovies()
  ElMessage.success('数据已刷新')
}

// 导出电影
const exportMovies = async () => {
  try {
    // 这里实现导出逻辑
    ElMessage.info('导出功能开发中...')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedMovies.value = selection
}

// 排序变化
const handleSortChange = ({ prop, order }) => {
  if (prop) {
    sort.field = prop
    sort.order = order === 'ascending' ? 'asc' : 'desc'
    loadMovies()
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadMovies()
}

// 当前页变化
const handleCurrentChange = (page) => {
  pagination.page = page
  loadMovies()
}

// 查看电影详情
const handleViewDetail = (movie) => {
  selectedMovie.value = movie
  detailDialogVisible.value = true
}

// 新增电影
const handleCreate = () => {
  isEdit.value = false
  formTitle.value = '新增电影'
  formData.value = null
  formDialogVisible.value = true
}

// 编辑电影
const handleEdit = (movie) => {
  isEdit.value = true
  formTitle.value = '编辑电影'
  formData.value = { ...movie }
  formDialogVisible.value = true
}

// 下架电影
const handleDisable = async (movie) => {
  try {
    await ElMessageBox.confirm(
      `确定要下架电影 "${movie.title}" 吗？下架后用户将无法查看和购买。`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    movie.disableLoading = true
    await adminStore.disableMovie(movie.id)
    ElMessage.success('电影已下架')
    loadMovies()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('下架电影失败')
    }
  } finally {
    movie.disableLoading = false
  }
}

// 上架电影
const handleEnable = async (movie) => {
  try {
    await ElMessageBox.confirm(
      `确定要上架电影 "${movie.title}" 吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    movie.enableLoading = true
    await adminStore.enableMovie(movie.id)
    ElMessage.success('电影已上架')
    loadMovies()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('上架电影失败')
    }
  } finally {
    movie.enableLoading = false
  }
}

// 设置热门
const handleSetHot = async (movie, isHot) => {
  const action = isHot ? '设为热门' : '取消热门'
  try {
    await ElMessageBox.confirm(
      `确定要${action}电影 "${movie.title}" 吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    movie.setHotLoading = true
    await adminStore.setMovieHot(movie.id, isHot)
    ElMessage.success(`电影已${action}`)
    loadMovies()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}电影失败`)
    }
  } finally {
    movie.setHotLoading = false
  }
}

// 删除电影
const handleDelete = async (movie) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除电影 "${movie.title}" 吗？此操作不可恢复！`,
      '警告',
      {
        type: 'error',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        confirmButtonClass: 'el-button--danger'
      }
    )

    movie.deleteLoading = true
    await adminStore.deleteMovie(movie.id)
    ElMessage.success('电影已删除')
    loadMovies()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除电影失败')
    }
  } finally {
    movie.deleteLoading = false
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedMovies.value.length} 部电影吗？此操作不可恢复！`,
      '警告',
      {
        type: 'error',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        confirmButtonClass: 'el-button--danger'
      }
    )

    batchDeleting.value = true
    const movieIds = selectedMovies.value.map(movie => movie.id)
    await adminStore.batchDeleteMovies(movieIds)
    ElMessage.success(`已删除 ${movieIds.length} 部电影`)
    selectedMovies.value = []
    loadMovies()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  } finally {
    batchDeleting.value = false
  }
}

// 批量下架
const handleBatchDisable = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要下架选中的 ${selectedMovies.value.length} 部电影吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    batchOperating.value = true
    const movieIds = selectedMovies.value.map(movie => movie.id)
    await adminStore.batchDisableMovies(movieIds)
    ElMessage.success(`已下架 ${movieIds.length} 部电影`)
    selectedMovies.value = []
    loadMovies()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量下架失败')
    }
  } finally {
    batchOperating.value = false
  }
}

// 批量上架
const handleBatchEnable = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要上架选中的 ${selectedMovies.value.length} 部电影吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    batchOperating.value = true
    const movieIds = selectedMovies.value.map(movie => movie.id)
    await adminStore.batchEnableMovies(movieIds)
    ElMessage.success(`已上架 ${movieIds.length} 部电影`)
    selectedMovies.value = []
    loadMovies()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量上架失败')
    }
  } finally {
    batchOperating.value = false
  }
}

// 批量设置热门
const handleBatchSetHot = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要将选中的 ${selectedMovies.value.length} 部电影设为热门吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    batchOperating.value = true
    const movieIds = selectedMovies.value.map(movie => movie.id)
    await adminStore.batchSetMoviesHot(movieIds, true)
    ElMessage.success(`已设置 ${movieIds.length} 部电影为热门`)
    selectedMovies.value = []
    loadMovies()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量设置热门失败')
    }
  } finally {
    batchOperating.value = false
  }
}

// 表单对话框关闭
const handleFormDialogClosed = () => {
  formData.value = null
  if (movieFormRef.value) {
    movieFormRef.value.resetForm()
  }
}

// 表单提交
const handleFormSubmit = async () => {
  formDialogVisible.value = false
  await loadMovies()
}
</script>
<style scoped lang="scss">
.movie-management-page {
  .page-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: $spacing-xl;

    .page-title {
      font-size: 24px;
      font-weight: 700;
      color: $text-primary;
      margin: 0;
    }
  }
}

.search-card {
  margin-bottom: $spacing-lg;

  .search-form {
    margin-bottom: 0;
  }
}

.table-card {
  .table-header {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .table-title {
      display: flex;
      align-items: center;
      gap: $spacing-md;

      .total-text {
        color: $text-secondary;
        font-size: $font-size-small;
      }
    }

    .table-actions {
      display: flex;
      gap: $spacing-sm;
    }
  }
}

.movie-info {
  display: flex;
  align-items: flex-start;
  gap: $spacing-md;

  .movie-poster {
    width: 60px;
    height: 80px;
    border-radius: $border-radius-small;
    flex-shrink: 0;
  }

  .image-error {
    width: 60px;
    height: 80px;
    background: $bg-gray;
    border-radius: $border-radius-small;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $text-disabled;

    .el-icon {
      font-size: 24px;
    }
  }

  .movie-details {
    flex: 1;
    min-width: 0;

    .movie-title {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      margin-bottom: $spacing-xs;

      span {
        font-weight: 600;
        color: $text-primary;
        @include text-ellipsis;
      }
    }

    .movie-original-title {
      color: $text-secondary;
      font-size: $font-size-small;
      margin-bottom: $spacing-xs;
      @include text-ellipsis;
    }

    .movie-meta {
      display: flex;
      gap: $spacing-md;
      margin-bottom: $spacing-xs;

      span {
        padding: 2px 6px;
        background: $bg-gray;
        border-radius: $border-radius-small;
        color: $text-secondary;
        font-size: $font-size-small;
      }
    }

    .movie-rating {
      display: flex;
      align-items: center;
      gap: $spacing-xs;

      .vote-count {
        color: $text-secondary;
        font-size: $font-size-small;
      }
    }
  }
}

.text {
  color: $text-regular;
}

.time-text {
  color: $text-regular;
  font-size: $font-size-small;
}

.price {
  font-weight: 600;
  color: $primary-color;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: $spacing-xs;

  .el-button {
    padding: 4px 6px;
    height: auto;
    font-size: $font-size-small;
  }
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: $spacing-lg;
  padding-top: $spacing-lg;
  border-top: 1px solid $border-light;
}

// 响应式设计
@media (max-width: $breakpoint-lg) {
  .search-form {
    .el-form-item {
      margin-bottom: $spacing-md;
    }
  }

  .table-header {
    flex-direction: column;
    align-items: stretch;
    gap: $spacing-md;

    .table-actions {
      justify-content: flex-start;
      flex-wrap: wrap;
    }
  }

  .action-buttons {
    flex-wrap: wrap;
    gap: $spacing-xs;
  }

  .movie-info {
    flex-direction: column;
    align-items: flex-start;
    gap: $spacing-sm;

    .movie-poster {
      width: 50px;
      height: 70px;
    }
  }
}

@media (max-width: $breakpoint-md) {
  .action-buttons {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
