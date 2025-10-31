<template>
  <div class="news-management-page">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <h1 class="page-title">资讯管理</h1>
      <div class="header-actions">
        <el-button type="primary" :icon="Plus" @click="handleCreate">
          发布资讯
        </el-button>
        <el-button :icon="Refresh" @click="refreshData">
          刷新
        </el-button>
      </div>
    </div>
    <!-- 搜索和筛选区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="资讯标题">
          <el-input
            v-model="searchForm.title"
            placeholder="请输入资讯标题"
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
        <el-form-item label="作者">
          <el-input
            v-model="searchForm.author"
            placeholder="请输入作者"
            clearable
            style="width: 150px"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
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
            <el-option label="已发布" :value="1" />
            <el-option label="草稿" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="置顶">
          <el-select
            v-model="searchForm.isTop"
            placeholder="全部"
            clearable
            style="width: 100px"
            @change="handleSearch"
          >
            <el-option label="全部" value="" />
            <el-option label="是" :value="1" />
            <el-option label="否" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布日期">
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
    <!-- 资讯列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="table-header">
          <div class="table-title">
            <span>资讯列表</span>
            <span class="total-text">共 {{ pagination.total }} 篇资讯</span>
          </div>
          <div class="table-actions">
            <el-button
              v-if="selectedNews.length > 0"
              type="danger"
              :icon="Delete"
              @click="handleBatchDelete"
              :disabled="batchDeleting"
            >
              批量删除 ({{ selectedNews.length }})
            </el-button>
            <el-button
              v-if="selectedNews.length > 0"
              type="warning"
              :icon="Download"
              @click="handleBatchPublish"
              :disabled="batchOperating"
            >
              批量发布
            </el-button>
            <el-button
              v-if="selectedNews.length > 0"
              type="success"
              :icon="Top"
              @click="handleBatchTop"
              :disabled="batchOperating"
            >
              批量置顶
            </el-button>
            <el-button
              v-if="selectedNews.length > 0"
              type="info"
              :icon="Remove"
              @click="handleBatchUnTop"
              :disabled="batchOperating"
            >
              批量取消置顶
            </el-button>
          </div>
        </div>
      </template>
      <div v-loading="loading">
        <el-table
          :data="newsList"
          style="width: 100%"
          @selection-change="handleSelectionChange"
          @sort-change="handleSortChange"
        >
          <el-table-column type="selection" width="55" />

          <el-table-column label="资讯信息" min-width="400">
            <template #default="{ row }">
              <div class="news-info">
                <el-image
                  :src="row.coverImage"
                  :alt="row.title"
                  class="news-cover"
                  fit="cover"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
                <div class="news-content">
                  <div class="news-title">
                    <span class="title-text">{{ row.title }}</span>
                    <el-tag v-if="row.isTop" type="danger" size="small">置顶</el-tag>
                    <el-tag v-if="!row.status" type="info" size="small">草稿</el-tag>
                  </div>
                  <div class="news-meta">
                    <span class="author">作者: {{ row.author || '系统' }}</span>
                    <span class="views">浏览: {{ row.viewCount }}</span>
                    <span class="publish-time" v-if="row.publishTime">
                      发布时间: {{ formatTime(row.publishTime) }}
                    </span>
                  </div>
                  <div class="news-excerpt">
                    {{ getExcerpt(row.content) }}
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status ? 'success' : 'info'" size="small">
                {{ row.status ? '已发布' : '草稿' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="置顶" width="60">
            <template #default="{ row }">
              <el-tag v-if="row.isTop" type="danger" size="small">是</el-tag>
              <span v-else class="no-data">-</span>
            </template>
          </el-table-column>
          <el-table-column label="浏览数" width="80" sortable="custom" prop="viewCount">
            <template #default="{ row }">
              <span class="view-count">{{ row.viewCount }}</span>
            </template>
          </el-table-column>
          <el-table-column label="发布时间" width="160" sortable="custom" prop="publishTime">
            <template #default="{ row }">
              <div v-if="row.publishTime" class="time-info">
                <div class="time">{{ formatTime(row.publishTime) }}</div>
                <div class="date">{{ formatDate(row.publishTime) }}</div>
              </div>
              <span v-else class="no-data">-</span>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="160" sortable="custom" prop="createTime">
            <template #default="{ row }">
              <div class="time-info">
                <div class="time">{{ formatTime(row.createTime) }}</div>
                <div class="date">{{ formatDate(row.createTime) }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="280" fixed="right">
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
                  v-if="!row.status"
                  link
                  type="success"
                  :icon="Check"
                  @click="handlePublish(row)"
                  :loading="row.publishLoading"
                >
                  发布
                </el-button>
                <el-button
                  v-if="row.status"
                  link
                  type="warning"
                  :icon="Close"
                  @click="handleUnpublish(row)"
                  :loading="row.unpublishLoading"
                >
                  取消发布
                </el-button>
                <el-button
                  v-if="!row.isTop"
                  link
                  type="warning"
                  :icon="Top"
                  @click="handleSetTop(row)"
                  :loading="row.topLoading"
                >
                  置顶
                </el-button>
                <el-button
                  v-if="row.isTop"
                  link
                  type="info"
                  :icon="Remove"
                  @click="handleCancelTop(row)"
                  :loading="row.cancelTopLoading"
                >
                  取消置顶
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
    <!-- 资讯详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="资讯详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <NewsDetail :news="selectedNewsItem" v-if="detailDialogVisible" />
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="formDialogVisible"
      :title="formTitle"
      width="900px"
      :close-on-click-modal="false"
      @closed="handleFormDialogClosed"
    >
      <NewsForm
        ref="newsFormRef"
        :news="formData"
        :is-edit="isEdit"
        @submit="handleFormSubmit"
        @cancel="formDialogVisible = false"
      />
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted} from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Refresh,
  Search,
  Edit,
  Delete,
  Close,
  Check,
  View,
  Picture,
  Top,
  Remove,
  Download
} from '@element-plus/icons-vue'
import { formatTime, formatDate  } from '@/utils'
import { useAdminStore } from '@/stores/admin'
import NewsDetail from '@/components/admin/NewsDetail.vue'
import NewsForm from '@/components/admin/NewsForm.vue'

const adminStore = useAdminStore()

// 响应式数据
const loading = ref(false)
const batchOperating = ref(false)
const batchDeleting = ref(false)
const detailDialogVisible = ref(false)
const formDialogVisible = ref(false)
const isEdit = ref(false)

const newsList = ref([])
const selectedNews = ref([])
const selectedNewsItem = ref(null)
const newsFormRef = ref()

const searchForm = reactive({
  title: '',
  author: '',
  status: '',
  isTop: '',
  dateRange: []
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
const formTitle = ref('发布资讯')

onMounted(async () => {
  await loadNews()
})

// 加载资讯列表
const loadNews = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
      sort: `${sort.field},${sort.order}`
    }

    // 添加搜索条件
    if (searchForm.title) {
      params.title = searchForm.title
    }
    if (searchForm.author) {
      params.author = searchForm.author
    }
    if (searchForm.status !== '') {
      params.status = searchForm.status
    }
    if (searchForm.isTop !== '') {
      params.isTop = searchForm.isTop
    }
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }

    const response = await adminStore.getNewsList(params)
    newsList.value = response.content || response
    pagination.total = response.totalElements || response.length || 0

    // 重置操作状态
    newsList.value.forEach(news => {
      news.publishLoading = false
      news.unpublishLoading = false
      news.topLoading = false
      news.cancelTopLoading = false
      news.deleteLoading = false
    })
  } catch (error) {
    ElMessage.error('加载资讯列表失败')
    console.error('加载资讯列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取内容摘要
const getExcerpt = (content, length = 100) => {
  if (!content) return ''
  // 去除HTML标签
  const text = content.replace(/<[^>]*>/g, '')
  return text.length > length ? text.substring(0, length) + '...' : text
}

// 搜索资讯
const handleSearch = () => {
  pagination.page = 1
  loadNews()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    title: '',
    author: '',
    status: '',
    isTop: '',
    dateRange: []
  })
  pagination.page = 1
  loadNews()
}

// 刷新数据
const refreshData = () => {
  loadNews()
  ElMessage.success('数据已刷新')
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedNews.value = selection
}

// 排序变化
const handleSortChange = ({ prop, order }) => {
  if (prop) {
    sort.field = prop
    sort.order = order === 'ascending' ? 'asc' : 'desc'
    loadNews()
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadNews()
}

// 当前页变化
const handleCurrentChange = (page) => {
  pagination.page = page
  loadNews()
}

// 查看资讯详情
const handleViewDetail = (news) => {
  selectedNewsItem.value = news
  detailDialogVisible.value = true
}

// 新增资讯
const handleCreate = () => {
  isEdit.value = false
  formTitle.value = '发布资讯'
  formData.value = null
  formDialogVisible.value = true
}

// 编辑资讯
const handleEdit = (news) => {
  isEdit.value = true
  formTitle.value = '编辑资讯'
  formData.value = { ...news }
  formDialogVisible.value = true
}

// 发布资讯
const handlePublish = async (news) => {
  try {
    await ElMessageBox.confirm(
      `确定要发布资讯 "${news.title}" 吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定发布',
        cancelButtonText: '取消'
      }
    )

    news.publishLoading = true
    await adminStore.publishNews(news.id)
    ElMessage.success('资讯发布成功')
    loadNews()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发布失败')
    }
  } finally {
    news.publishLoading = false
  }
}

// 取消发布
const handleUnpublish = async (news) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消发布资讯 "${news.title}" 吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    news.unpublishLoading = true
    await adminStore.unpublishNews(news.id)
    ElMessage.success('已取消发布')
    loadNews()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消发布失败')
    }
  } finally {
    news.unpublishLoading = false
  }
}

// 置顶资讯
const handleSetTop = async (news) => {
  try {
    await ElMessageBox.confirm(
      `确定要将资讯 "${news.title}" 置顶吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定置顶',
        cancelButtonText: '取消'
      }
    )

    news.topLoading = true
    await adminStore.setNewsTop(news.id, true)
    ElMessage.success('置顶成功')
    loadNews()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('置顶失败')
    }
  } finally {
    news.topLoading = false
  }
}

// 取消置顶
const handleCancelTop = async (news) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消资讯 "${news.title}" 的置顶吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    news.cancelTopLoading = true
    await adminStore.setNewsTop(news.id, false)
    ElMessage.success('取消置顶成功')
    loadNews()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消置顶失败')
    }
  } finally {
    news.cancelTopLoading = false
  }
}

// 删除资讯
const handleDelete = async (news) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除资讯 "${news.title}" 吗？此操作不可恢复！`,
      '警告',
      {
        type: 'error',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        confirmButtonClass: 'el-button--danger'
      }
    )

    news.deleteLoading = true
    await adminStore.deleteNews(news.id)
    ElMessage.success('资讯已删除')
    loadNews()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  } finally {
    news.deleteLoading = false
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedNews.value.length} 篇资讯吗？此操作不可恢复！`,
      '警告',
      {
        type: 'error',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        confirmButtonClass: 'el-button--danger'
      }
    )

    batchDeleting.value = true
    const newsIds = selectedNews.value.map(news => news.id)
    await adminStore.batchDeleteNews(newsIds)
    ElMessage.success(`已删除 ${newsIds.length} 篇资讯`)
    selectedNews.value = []
    loadNews()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  } finally {
    batchDeleting.value = false
  }
}

// 批量发布
const handleBatchPublish = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要发布选中的 ${selectedNews.value.length} 篇资讯吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定发布',
        cancelButtonText: '取消'
      }
    )

    batchOperating.value = true
    const newsIds = selectedNews.value.map(news => news.id)
    await adminStore.batchPublishNews(newsIds)
    ElMessage.success(`已发布 ${newsIds.length} 篇资讯`)
    selectedNews.value = []
    loadNews()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量发布失败')
    }
  } finally {
    batchOperating.value = false
  }
}

// 批量置顶
const handleBatchTop = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要将选中的 ${selectedNews.value.length} 篇资讯置顶吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定置顶',
        cancelButtonText: '取消'
      }
    )

    batchOperating.value = true
    const newsIds = selectedNews.value.map(news => news.id)
    await adminStore.batchSetNewsTop(newsIds, true)
    ElMessage.success(`已置顶 ${newsIds.length} 篇资讯`)
    selectedNews.value = []
    loadNews()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量置顶失败')
    }
  } finally {
    batchOperating.value = false
  }
}

// 批量取消置顶
const handleBatchUnTop = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要取消选中的 ${selectedNews.value.length} 篇资讯的置顶吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    batchOperating.value = true
    const newsIds = selectedNews.value.map(news => news.id)
    await adminStore.batchSetNewsTop(newsIds, false)
    ElMessage.success(`已取消 ${newsIds.length} 篇资讯的置顶`)
    selectedNews.value = []
    loadNews()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量取消置顶失败')
    }
  } finally {
    batchOperating.value = false
  }
}

// 表单对话框关闭
const handleFormDialogClosed = () => {
  formData.value = null
  if (newsFormRef.value) {
    newsFormRef.value.resetForm()
  }
}

// 表单提交
const handleFormSubmit = async () => {
  formDialogVisible.value = false
  await loadNews()
}
</script>
<style scoped lang="scss">
.news-management-page {
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

.news-info {
  display: flex;
  align-items: flex-start;
  gap: $spacing-md;

  .news-cover {
    width: 80px;
    height: 60px;
    border-radius: $border-radius-small;
    flex-shrink: 0;
  }

  .image-error {
    width: 80px;
    height: 60px;
    background: $bg-gray;
    border-radius: $border-radius-small;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $text-disabled;

    .el-icon {
      font-size: 20px;
    }
  }

  .news-content {
    flex: 1;
    min-width: 0;

    .news-title {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      margin-bottom: $spacing-xs;

      .title-text {
        font-weight: 600;
        color: $text-primary;
        @include text-ellipsis;
      }

      .el-tag {
        flex-shrink: 0;
      }
    }

    .news-meta {
      display: flex;
      align-items: center;
      gap: $spacing-md;
      margin-bottom: $spacing-xs;

      span {
        color: $text-secondary;
        font-size: $font-size-small;
      }
    }

    .news-excerpt {
      color: $text-regular;
      font-size: $font-size-small;
      line-height: 1.4;
      @include text-ellipsis;
    }
  }
}

.view-count {
  font-weight: 600;
  color: $primary-color;
}

.time-info {
  .time {
    color: $text-regular;
    margin-bottom: 2px;
  }

  .date {
    color: $text-secondary;
    font-size: $font-size-small;
  }
}

.no-data {
  color: $text-disabled;
  font-style: italic;
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
}

@media (max-width: $breakpoint-md) {
  .action-buttons {
    flex-direction: column;
    align-items: flex-start;
  }

  .news-info {
    flex-direction: column;
    align-items: flex-start;
    gap: $spacing-sm;

    .news-cover {
      width: 100%;
      height: 120px;
    }
  }

  .news-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: $spacing-xs !important;
  }
}
</style>
