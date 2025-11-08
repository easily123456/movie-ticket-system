<template>
  <div class="order-management-page">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <h1 class="page-title">订单管理</h1>
      <div class="header-actions">
        <el-button :icon="Refresh" @click="refreshData">
          刷新
        </el-button>
        <el-button :icon="Download" @click="handleExport">
          导出
        </el-button>
      </div>
    </div>
    <!-- 搜索和筛选区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="订单号">
          <el-input
            v-model="searchForm.orderNo"
            placeholder="请输入订单号"
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
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            style="width: 150px"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="电影名称">
          <el-input
            v-model="searchForm.movieTitle"
            placeholder="请输入电影名称"
            clearable
            style="width: 180px"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select
            v-model="searchForm.status"
            placeholder="全部状态"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="全部" value="" />
            <el-option label="待支付" value="PENDING" />
            <el-option label="已支付" value="PAID" />
            <el-option label="已取消" value="CANCELLED" />
            <el-option label="已退款" value="REFUNDED" />
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
    <!-- 订单列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="table-header">
          <div class="table-title">
            <span>订单列表</span>
            <span class="total-text">共 {{ pagination.total }} 个订单</span>
            <span class="revenue-text">总收入: {{ formatPrice(totalRevenue) }}</span>
          </div>
          <div class="table-actions">
            <el-button
              v-if="selectedOrders.length > 0"
              type="danger"
              :icon="Delete"
              @click="handleBatchDelete"
              :disabled="batchDeleting"
            >
              批量删除 ({{ selectedOrders.length }})
            </el-button>
            <el-button
              v-if="selectedOrders.length > 0"
              type="warning"
              :icon="Close"
              @click="handleBatchCancel"
              :disabled="batchOperating"
            >
              批量取消
            </el-button>
            <el-button
              v-if="selectedOrders.length > 0"
              type="success"
              :icon="Check"
              @click="handleBatchComplete"
              :disabled="batchOperating"
            >
              批量完成
            </el-button>
          </div>
        </div>
      </template>
      <div v-loading="loading">
        <el-table
          :data="orders"
          style="width: 100%"
          @selection-change="handleSelectionChange"
          @sort-change="handleSortChange"
        >
          <el-table-column type="selection" width="55" />

          <el-table-column label="订单信息" min-width="350">
            <template #default="{ row }">
              <div class="order-info">
                <div class="order-main">
                  <div class="order-no">
                    <span class="label">订单号:</span>
                    <span class="value">{{ row.orderNo }}</span>
                  </div>
                  <div class="order-user">
                    <span class="label">用户:</span>
                    <span class="value">{{ row.username }}</span>
                  </div>
                </div>
                <div class="order-movie">
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
                  <div class="movie-info">
                    <div class="movie-title">{{ row.movieTitle }}</div>
                    <div class="session-info">
                      <span class="time">{{ formatDateTime(row.sessionTime) }}</span>
                      <span class="hall">{{ row.hallName }}</span>
                    </div>
                    <div class="seats-info">
                      <span class="seats">{{ row.seatNumbers.join(', ') }}</span>
                      <span class="seat-count">({{ row.seatCount }}张)</span>
                    </div>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="金额" width="120" sortable="custom" prop="totalPrice">
            <template #default="{ row }">
              <div class="price-info">
                <div class="price">{{ formatPrice(row.totalPrice) }}</div>
                <div class="price-detail">单价: {{ formatPrice(row.totalPrice / row.seatCount) }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
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
          <el-table-column label="支付时间" width="160" v-if="showPaymentTime">
            <template #default="{ row }">
              <div v-if="row.payTime" class="time-info">
                <div class="time">{{ formatTime(row.payTime) }}</div>
                <div class="date">{{ formatDate(row.payTime) }}</div>
              </div>
              <span v-else class="no-data">-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
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
                  v-if="row.status === 'PENDING'"
                  link
                  type="success"
                  :icon="Check"
                  @click="handleComplete(row)"
                  :loading="row.completeLoading"
                >
                  完成
                </el-button>
                <el-button
                  v-if="row.status === 'PENDING'"
                  link
                  type="warning"
                  :icon="Close"
                  @click="handleCancel(row)"
                  :loading="row.cancelLoading"
                >
                  取消
                </el-button>
                <el-button
                  v-if="row.status === 'PAID'"
                  link
                  type="danger"
                  :icon="RefreshLeft"
                  @click="handleRefund(row)"
                  :loading="row.refundLoading"
                >
                  退款
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
    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="700px"
      :close-on-click-modal="false"
    >
      <OrderDetail :order="selectedOrder" v-if="detailDialogVisible" />
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Refresh,
  Search,
  Delete,
  Close,
  Check,
  View,
  Picture,
  Download,
  RefreshLeft
} from '@element-plus/icons-vue'
import { formatTime, formatDate, formatDateTime, formatPrice } from '@/utils'
import { useAdminStore } from '@/stores/admin'
import OrderDetail from '@/components/admin/OrderDetail.vue'

const adminStore = useAdminStore()

// 响应式数据
const loading = ref(false)
const batchOperating = ref(false)
const batchDeleting = ref(false)
const detailDialogVisible = ref(false)

const orders = ref([])
const selectedOrders = ref([])
const selectedOrder = ref(null)

const searchForm = reactive({
  orderNo: '',
  username: '',
  movieTitle: '',
  status: '',
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

// 计算属性
const totalRevenue = computed(() => {
  return orders.value
    .filter(order => order.status === 'PAID')
    .reduce((sum, order) => sum + parseFloat(order.totalPrice || 0), 0)
})

const showPaymentTime = computed(() => {
  return orders.value.some(order => order.payTime)
})

onMounted(async () => {
  await loadOrders()
})

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
      sort: `${sort.field},${sort.order}`
    }

    // 添加搜索条件
    if (searchForm.orderNo) {
      params.orderNo = searchForm.orderNo
    }
    if (searchForm.username) {
      params.username = searchForm.username
    }
    if (searchForm.movieTitle) {
      params.movieTitle = searchForm.movieTitle
    }
    if (searchForm.status) {
      params.status = searchForm.status
    }
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }

    const response = await adminStore.getOrders(params)
    orders.value = response.content || response
    pagination.total = response.totalElements || response.length || 0

    // 重置操作状态
    orders.value.forEach(order => {
      order.completeLoading = false
      order.cancelLoading = false
      order.refundLoading = false
      order.deleteLoading = false
    })
  } catch (error) {
    ElMessage.error('加载订单列表失败')
    console.error('加载订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    PENDING: 'warning',
    PAID: 'success',
    CANCELLED: 'info',
    REFUNDED: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    PENDING: '待支付',
    PAID: '已支付',
    CANCELLED: '已取消',
    REFUNDED: '已退款'
  }
  return textMap[status] || status
}

// 搜索订单
const handleSearch = () => {
  pagination.page = 1
  loadOrders()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    orderNo: '',
    username: '',
    movieTitle: '',
    status: '',
    dateRange: []
  })
  pagination.page = 1
  loadOrders()
}

// 刷新数据
const refreshData = () => {
  loadOrders()
  ElMessage.success('数据已刷新')
}

// 导出数据
const handleExport = async () => {
  try {
    await adminStore.exportOrders(searchForm)
    ElMessage.success('导出任务已开始，请稍后查看下载文件')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedOrders.value = selection
}

// 排序变化
const handleSortChange = ({ prop, order }) => {
  if (prop) {
    sort.field = prop
    sort.order = order === 'ascending' ? 'asc' : 'desc'
    loadOrders()
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadOrders()
}

// 当前页变化
const handleCurrentChange = (page) => {
  pagination.page = page
  loadOrders()
}

// 查看订单详情
const handleViewDetail = (order) => {
  selectedOrder.value = order
  detailDialogVisible.value = true
}

// 完成订单（支付）
const handleComplete = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确定要将订单 "${order.orderNo}" 标记为已支付吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    order.completeLoading = true
    await adminStore.updateOrderStatus(order.id, 'PAID')
    ElMessage.success('订单已标记为已支付')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  } finally {
    order.completeLoading = false
  }
}

// 取消订单
const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消订单 "${order.orderNo}" 吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    order.cancelLoading = true
    await adminStore.updateOrderStatus(order.id, 'CANCELLED')
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  } finally {
    order.cancelLoading = false
  }
}

// 退款
const handleRefund = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确定要为订单 "${order.orderNo}" 办理退款吗？`,
      '退款确认',
      {
        type: 'warning',
        confirmButtonText: '确定退款',
        cancelButtonText: '取消'
      }
    )

    order.refundLoading = true
    await adminStore.updateOrderStatus(order.id, 'REFUNDED')
    ElMessage.success('退款操作成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('退款失败')
    }
  } finally {
    order.refundLoading = false
  }
}

// 删除订单
const handleDelete = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除订单 "${order.orderNo}" 吗？此操作不可恢复！`,
      '警告',
      {
        type: 'error',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        confirmButtonClass: 'el-button--danger'
      }
    )

    order.deleteLoading = true
    await adminStore.deleteOrder(order.id)
    ElMessage.success('订单已删除')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  } finally {
    order.deleteLoading = false
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedOrders.value.length} 个订单吗？此操作不可恢复！`,
      '警告',
      {
        type: 'error',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        confirmButtonClass: 'el-button--danger'
      }
    )

    batchDeleting.value = true
    const orderIds = selectedOrders.value.map(order => order.id)
    await adminStore.batchDeleteOrders(orderIds)
    ElMessage.success(`已删除 ${orderIds.length} 个订单`)
    selectedOrders.value = []
    loadOrders()
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
      `确定要取消选中的 ${selectedOrders.value.length} 个订单吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    batchOperating.value = true
    const orderIds = selectedOrders.value.map(order => order.id)
    await adminStore.batchUpdateOrderStatus(orderIds, 'CANCELLED')
    ElMessage.success(`已取消 ${orderIds.length} 个订单`)
    selectedOrders.value = []
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量取消失败')
    }
  } finally {
    batchOperating.value = false
  }
}

// 批量完成
const handleBatchComplete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要将选中的 ${selectedOrders.value.length} 个订单标记为已支付吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    batchOperating.value = true
    const orderIds = selectedOrders.value.map(order => order.id)
    await adminStore.batchUpdateOrderStatus(orderIds, 'PAID')
    ElMessage.success(`已完成 ${orderIds.length} 个订单`)
    selectedOrders.value = []
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量操作失败')
    }
  } finally {
    batchOperating.value = false
  }
}
</script>
<style scoped lang="scss">
.order-management-page {
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

      .revenue-text {
        color: $success-color;
        font-weight: 600;
        font-size: $font-size-small;
      }
    }

    .table-actions {
      display: flex;
      gap: $spacing-sm;
    }
  }
}

.order-info {
  .order-main {
    margin-bottom: $spacing-sm;

    .order-no {
      font-weight: 600;
      color: $text-primary;
      margin-bottom: $spacing-xs;

      .label {
        color: $text-secondary;
        margin-right: $spacing-xs;
      }
    }

    .order-user {
      color: $text-secondary;
      font-size: $font-size-small;

      .label {
        margin-right: $spacing-xs;
      }
    }
  }

  .order-movie {
    display: flex;
    align-items: flex-start;
    gap: $spacing-md;

    .movie-poster {
      width: 50px;
      height: 70px;
      border-radius: $border-radius-small;
      flex-shrink: 0;
    }

    .image-error {
      width: 50px;
      height: 70px;
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

    .movie-info {
      flex: 1;
      min-width: 0;

      .movie-title {
        font-weight: 600;
        color: $text-primary;
        margin-bottom: $spacing-xs;
  /* single-line ellipsis fallback */
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
      }

      .session-info {
        display: flex;
        align-items: center;
        gap: $spacing-md;
        margin-bottom: $spacing-xs;

        .time {
          color: $text-regular;
          font-size: $font-size-small;
        }

        .hall {
          color: $text-secondary;
          font-size: $font-size-small;
        }
      }

      .seats-info {
        .seats {
          color: $primary-color;
          font-weight: 500;
        }

        .seat-count {
          color: $text-secondary;
          font-size: $font-size-small;
          margin-left: $spacing-xs;
        }
      }
    }
  }
}

.price-info {
  .price {
    font-weight: 600;
    color: $primary-color;
    margin-bottom: 2px;
  }

  .price-detail {
    color: $text-secondary;
    font-size: $font-size-small;
  }
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

  .order-movie {
    flex-direction: column;
    align-items: flex-start;
    gap: $spacing-sm !important;

    .movie-poster {
      width: 40px !important;
      height: 60px !important;
    }
  }
}
</style>
