<template>
  <div class="session-management-page">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <h1 class="page-title">场次管理</h1>
      <div class="header-actions">
        <el-button type="primary" :icon="Plus" @click="handleCreate">
          新增场次
        </el-button>
        <el-button :icon="Refresh" @click="refreshData">
          刷新
        </el-button>
        <el-button :icon="Calendar" @click="handleToday">
          今天
        </el-button>
      </div>
    </div>
    <!-- 搜索和筛选区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="电影名称">
          <el-input
            v-model="searchForm.movieKeyword"
            placeholder="请输入电影名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="放映厅">
          <el-select
            v-model="searchForm.hallId"
            placeholder="全部放映厅"
            clearable
            style="width: 150px"
            @change="handleSearch"
          >
            <el-option label="全部" value="" />
            <el-option
              v-for="hall in halls"
              :key="hall.id"
              :label="hall.name"
              :value="hall.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
            @change="handleSearch"
          />
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
            <el-option label="正常" :value="true" />
            <el-option label="取消" :value="false" />
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
    <!-- 场次列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="table-header">
          <div class="table-title">
            <span>场次列表</span>
            <span class="total-text">共 {{ pagination.total }} 个场次</span>
          </div>
          <div class="table-actions">
            <el-button
              v-if="selectedSessions.length > 0"
              type="danger"
              :icon="Delete"
              @click="handleBatchDelete"
              :disabled="batchDeleting"
            >
              批量删除 ({{ selectedSessions.length }})
            </el-button>
            <el-button
              v-if="selectedSessions.length > 0"
              type="warning"
              :icon="Close"
              @click="handleBatchCancel"
              :disabled="batchOperating"
            >
              批量取消
            </el-button>
            <el-button
              v-if="selectedSessions.length > 0"
              type="success"
              :icon="Check"
              @click="handleBatchEnable"
              :disabled="batchOperating"
            >
              批量启用
            </el-button>
          </div>
        </div>
      </template>
      <div v-loading="loading">
        <el-table
          :data="sessions"
          style="width: 100%"
          @selection-change="handleSelectionChange"
          @sort-change="handleSortChange"
        >
          <el-table-column type="selection" width="55" />

          <el-table-column label="场次信息" min-width="300">
            <template #default="{ row }">
              <div class="session-info">
                <el-image
                  :src="row.moviePoster"
                  :alt="row.movieTitle"
                  class="movie-poster"
                  fit="cover"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
                <div class="session-details">
                  <div class="movie-title">{{ row.movieTitle }}</div>
                  <div class="session-time">
                    <span class="time">{{ formatDateTime(row.startTime) }}</span>
                    <span class="duration">~{{ formatDateTime(row.endTime) }}</span>
                  </div>
                  <div class="session-hall">
                    <el-tag size="small">{{ row.hallName }}</el-tag>
                    <span class="seats">座位: {{ row.availableSeats - row.bookedSeats }}/{{ row.availableSeats }}</span>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="放映厅" prop="hallName" width="120" />

          <el-table-column label="价格" width="100" sortable="custom" prop="price">
            <template #default="{ row }">
              <span class="price">{{ formatPrice(row.price) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="座位情况" width="120">
            <template #default="{ row }">
              <div class="seats-info">
                <div class="seats-progress">
                  <el-progress
                    :percentage="getSeatPercentage(row)"
                    :color="getSeatColor(row)"
                    :show-text="false"
                  />
                </div>
                <div class="seats-text">
                  {{ row.bookedSeats }}/{{ row.availableSeats }}
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status ? 'success' : 'danger'" size="small">
                {{ row.status ? '正常' : '取消' }}
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
                  type="info"
                  :icon="Position"
                  @click="handleViewSeats(row)"
                >
                  座位
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
                  :icon="Close"
                  @click="handleCancel(row)"
                  :loading="row.cancelLoading"
                >
                  取消
                </el-button>

                <el-button
                  v-else
                  link
                  type="success"
                  :icon="Check"
                  @click="handleEnable(row)"
                  :loading="row.enableLoading"
                >
                  启用
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
    <!-- 场次详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="场次详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <SessionDetail :session="selectedSession" v-if="detailDialogVisible" />
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
      <SessionForm
        ref="sessionFormRef"
        :session="formData"
        :movies="movies"
        :halls="halls"
        :is-edit="isEdit"
        @submit="handleFormSubmit"
        @cancel="formDialogVisible = false"
      />
    </el-dialog>
    <!-- 座位管理对话框 -->
    <el-dialog
      v-model="seatsDialogVisible"
      title="座位管理"
      width="900px"
      :close-on-click-modal="false"
    >
      <SessionSeats
        :session="selectedSession"
        v-if="seatsDialogVisible"
        @close="seatsDialogVisible = false"
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
  Calendar,
  Search,
  Edit,
  Delete,
  Close,
  Check,
  View,
  Position,
  Picture
} from '@element-plus/icons-vue'
import { formatTime, formatDateTime, formatPrice } from '@/utils'
import { useAdminStore } from '@/stores/admin'
import SessionDetail from '@/components/admin/SessionDetail.vue'
import SessionForm from '@/components/admin/SessionForm.vue'
import SessionSeats from '@/components/admin/SessionSeats.vue'
import { colors } from '@/utils/theme'

const adminStore = useAdminStore()

// 响应式数据
const loading = ref(false)
const batchOperating = ref(false)
const batchDeleting = ref(false)
const detailDialogVisible = ref(false)
const formDialogVisible = ref(false)
const seatsDialogVisible = ref(false)
const isEdit = ref(false)

const sessions = ref([])
const movies = ref([])
const halls = ref([])
const selectedSessions = ref([])
const selectedSession = ref(null)
const sessionFormRef = ref()

const searchForm = reactive({
  movieKeyword: '',
  hallId: '',
  dateRange: [],
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const sort = reactive({
  field: 'startTime',
  order: 'asc'
})

const formData = ref(null)
const formTitle = ref('新增场次')

onMounted(async () => {
  await loadInitialData()
  await loadSessions()
})

// 加载初始数据
const loadInitialData = async () => {
  try {
    // 并行加载电影和放映厅数据
    const [moviesData, hallsData] = await Promise.all([
      adminStore.getMovies({ page: 0, size: 1000 }),
      adminStore.getHalls()
    ])

    movies.value = moviesData.content || moviesData
    halls.value = hallsData
  } catch (error) {
    console.error('加载初始数据失败:', error)
  }
}

// 加载场次列表
const loadSessions = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
      sort: `${sort.field},${sort.order}`
    }

    // 添加搜索条件
    if (searchForm.movieKeyword) {
      params.movieKeyword = searchForm.movieKeyword
    }
    if (searchForm.hallId) {
      params.hallId = searchForm.hallId
    }
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }
    if (searchForm.status !== '') {
      params.status = searchForm.status
    }

    const response = await adminStore.getSessions(params)
    sessions.value = response.content || response
    pagination.total = response.totalElements || response.length || 0

    // 重置操作状态
    sessions.value.forEach(session => {
      session.cancelLoading = false
      session.enableLoading = false
      session.deleteLoading = false
    })
  } catch (error) {
    ElMessage.error('加载场次列表失败')
    console.error('加载场次列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 计算座位百分比
const getSeatPercentage = (session) => {
  if (!session.availableSeats) return 0
  return Math.round((session.bookedSeats / session.availableSeats) * 100)
}

// 获取座位颜色
const getSeatColor = (session) => {
  const percentage = getSeatPercentage(session)
  if (percentage >= 80) return colors.danger
  if (percentage >= 50) return colors.warning
  return colors.success
}

// 搜索场次
const handleSearch = () => {
  pagination.page = 1
  loadSessions()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    movieKeyword: '',
    hallId: '',
    dateRange: [],
    status: ''
  })
  pagination.page = 1
  loadSessions()
}

// 刷新数据
const refreshData = () => {
  loadSessions()
  ElMessage.success('数据已刷新')
}

// 跳转到今天
const handleToday = () => {
  const today = new Date()
  const tomorrow = new Date(today)
  tomorrow.setDate(tomorrow.getDate() + 1)

  searchForm.dateRange = [today, tomorrow]
  handleSearch()
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedSessions.value = selection
}

// 排序变化
const handleSortChange = ({ prop, order }) => {
  if (prop) {
    sort.field = prop
    sort.order = order === 'ascending' ? 'asc' : 'desc'
    loadSessions()
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadSessions()
}

// 当前页变化
const handleCurrentChange = (page) => {
  pagination.page = page
  loadSessions()
}

// 查看场次详情
const handleViewDetail = (session) => {
  selectedSession.value = session
  detailDialogVisible.value = true
}

// 查看座位
const handleViewSeats = (session) => {
  selectedSession.value = session
  seatsDialogVisible.value = true
}

// 新增场次
const handleCreate = () => {
  isEdit.value = false
  formTitle.value = '新增场次'
  formData.value = null
  formDialogVisible.value = true
}

// 编辑场次
const handleEdit = (session) => {
  isEdit.value = true
  formTitle.value = '编辑场次'
  formData.value = { ...session }
  formDialogVisible.value = true
}

// 取消场次
const handleCancel = async (session) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消场次 "${session.movieTitle}" 吗？取消后用户将无法购买该场次。`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    session.cancelLoading = true
    await adminStore.cancelSession(session.id)
    ElMessage.success('场次已取消')
    loadSessions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消场次失败')
    }
  } finally {
    session.cancelLoading = false
  }
}

// 启用场次
const handleEnable = async (session) => {
  try {
    await ElMessageBox.confirm(
      `确定要启开场次 "${session.movieTitle}" 吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    session.enableLoading = true
    await adminStore.enableSession(session.id)
    ElMessage.success('场次已启用')
    loadSessions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('启用场次失败')
    }
  } finally {
    session.enableLoading = false
  }
}

// 删除场次
const handleDelete = async (session) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除场次 "${session.movieTitle}" 吗？此操作不可恢复！`,
      '警告',
      {
        type: 'error',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        confirmButtonClass: 'el-button--danger'
      }
    )

    session.deleteLoading = true
    await adminStore.deleteSession(session.id)
    ElMessage.success('场次已删除')
    loadSessions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除场次失败')
    }
  } finally {
    session.deleteLoading = false
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedSessions.value.length} 个场次吗？此操作不可恢复！`,
      '警告',
      {
        type: 'error',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        confirmButtonClass: 'el-button--danger'
      }
    )

    batchDeleting.value = true
    const sessionIds = selectedSessions.value.map(session => session.id)
    await adminStore.batchDeleteSessions(sessionIds)
    ElMessage.success(`已删除 ${sessionIds.length} 个场次`)
    selectedSessions.value = []
    loadSessions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  } finally {
    batchDeleting.value = false
  }
}

// 批量取消
const handleBatchCancel = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要取消选中的 ${selectedSessions.value.length} 个场次吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    batchOperating.value = true
    const sessionIds = selectedSessions.value.map(session => session.id)
    await adminStore.batchCancelSessions(sessionIds)
    ElMessage.success(`已取消 ${sessionIds.length} 个场次`)
    selectedSessions.value = []
    loadSessions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量取消失败')
    }
  } finally {
    batchOperating.value = false
  }
}

// 批量启用
const handleBatchEnable = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要启用选中的 ${selectedSessions.value.length} 个场次吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    batchOperating.value = true
    const sessionIds = selectedSessions.value.map(session => session.id)
    await adminStore.batchEnableSessions(sessionIds)
    ElMessage.success(`已启用 ${sessionIds.length} 个场次`)
    selectedSessions.value = []
    loadSessions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量启用失败')
    }
  } finally {
    batchOperating.value = false
  }
}

// 表单对话框关闭
const handleFormDialogClosed = () => {
  formData.value = null
  if (sessionFormRef.value) {
    sessionFormRef.value.resetForm()
  }
}

// 表单提交
const handleFormSubmit = async () => {
  formDialogVisible.value = false
  await loadSessions()
}
</script>
<style scoped lang="scss">
.session-management-page {
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

.session-info {
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

  .session-details {
    flex: 1;
    min-width: 0;

    .movie-title {
      font-weight: 600;
      color: $text-primary;
      margin-bottom: $spacing-xs;
      @include text-ellipsis;
    }

    .session-time {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      margin-bottom: $spacing-xs;

      .time {
        font-weight: 500;
        color: $text-regular;
      }

      .duration {
        color: $text-secondary;
        font-size: $font-size-small;
      }
    }

    .session-hall {
      display: flex;
      align-items: center;
      gap: $spacing-md;

      .seats {
        color: $text-secondary;
        font-size: $font-size-small;
      }
    }
  }
}

.price {
  font-weight: 600;
  color: $primary-color;
}

.seats-info {
  .seats-progress {
    margin-bottom: $spacing-xs;
  }

  .seats-text {
    text-align: center;
    color: $text-secondary;
    font-size: $font-size-small;
  }
}

.time-text {
  color: $text-regular;
  font-size: $font-size-small;
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

  .session-info {
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
