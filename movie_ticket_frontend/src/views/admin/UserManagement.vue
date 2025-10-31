<template>
  <div class="user-management-page">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <h1 class="page-title">用户管理</h1>
      <div class="header-actions">
        <el-button type="primary" :icon="Refresh" @click="refreshData">
          刷新
        </el-button>
        <el-button :icon="Download" @click="exportUsers">
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
            placeholder="请输入用户名、邮箱或手机号"
            clearable
            style="width: 240px"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select
            v-model="searchForm.role"
            placeholder="全部角色"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="全部" value="" />
            <el-option label="管理员" value="ADMIN" />
            <el-option label="普通用户" value="USER" />
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
            <el-option label="正常" :value="true" />
            <el-option label="禁用" :value="false" />
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
    <!-- 用户列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="table-header">
          <div class="table-title">
            <span>用户列表</span>
            <span class="total-text">共 {{ pagination.total }} 个用户</span>
          </div>
          <div class="table-actions">
            <el-button
              v-if="selectedUsers.length > 0"
              type="danger"
              :icon="Delete"
              @click="handleBatchDelete"
              :disabled="batchDeleting"
            >
              批量删除 ({{ selectedUsers.length }})
            </el-button>
            <el-button
              v-if="selectedUsers.length > 0"
              type="warning"
              :icon="Close"
              @click="handleBatchDisable"
              :disabled="batchOperating"
            >
              批量禁用
            </el-button>
            <el-button
              v-if="selectedUsers.length > 0"
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
          :data="users"
          style="width: 100%"
          @selection-change="handleSelectionChange"
          @sort-change="handleSortChange"
        >
          <el-table-column type="selection" width="55" />

          <el-table-column label="用户信息" min-width="200">
            <template #default="{ row }">
              <div class="user-info">
                <el-avatar :size="40" :src="row.avatar" class="user-avatar">
                  {{ row.username?.charAt(0) }}
                </el-avatar>
                <div class="user-details">
                  <div class="username">{{ row.username }}</div>
                  <div class="user-email">{{ row.email }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="手机号" prop="phone" width="140">
            <template #default="{ row }">
              <span>{{ row.phone || '未设置' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="角色" width="100">
            <template #default="{ row }">
              <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'" size="small">
                {{ row.role === 'ADMIN' ? '管理员' : '普通用户' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status ? 'success' : 'danger'" size="small">
                {{ row.status ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="最后登录" width="160" sortable="custom" prop="lastLoginTime">
            <template #default="{ row }">
              <span v-if="row.lastLoginTime" class="time-text">
                {{ formatTime(row.lastLoginTime) }}
              </span>
              <span v-else class="empty-text">从未登录</span>
            </template>
          </el-table-column>
          <el-table-column label="注册时间" width="160" sortable="custom" prop="createTime">
            <template #default="{ row }">
              <span class="time-text">{{ formatTime(row.createTime) }}</span>
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
                  v-if="row.status"
                  link
                  type="warning"
                  :icon="Close"
                  @click="handleDisableUser(row)"
                  :loading="row.disableLoading"
                >
                  禁用
                </el-button>

                <el-button
                  v-else
                  link
                  type="success"
                  :icon="Check"
                  @click="handleEnableUser(row)"
                  :loading="row.enableLoading"
                >
                  启用
                </el-button>
                <el-button
                  link
                  type="danger"
                  :icon="Delete"
                  @click="handleDeleteUser(row)"
                  :loading="row.deleteLoading"
                  :disabled="row.role === 'ADMIN'"
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
    <!-- 用户详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="用户详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <UserDetail :user="selectedUser" v-if="detailDialogVisible" />
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Refresh,
  Download,
  Search,
  Delete,
  Close,
  Check,
  View
} from '@element-plus/icons-vue'
import { formatTime } from '@/utils'
import { useAdminStore } from '@/stores/admin'
import UserDetail from '@/components/admin/UserDetail.vue'

const adminStore = useAdminStore()

// 响应式数据
const loading = ref(false)
const batchOperating = ref(false)
const batchDeleting = ref(false)
const detailDialogVisible = ref(false)

const users = ref([])
const selectedUsers = ref([])
const selectedUser = ref(null)

const searchForm = reactive({
  keyword: '',
  role: '',
  status: ''
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

onMounted(() => {
  loadUsers()
})

// 加载用户列表
const loadUsers = async () => {
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
    if (searchForm.role) {
      params.role = searchForm.role
    }
    if (searchForm.status !== '') {
      params.status = searchForm.status
    }

    const response = await adminStore.getUsers(params)
    users.value = response.content || response
    pagination.total = response.totalElements || response.length || 0

    // 重置操作状态
    users.value.forEach(user => {
      user.disableLoading = false
      user.enableLoading = false
      user.deleteLoading = false
    })
  } catch (error) {
    ElMessage.error('加载用户列表失败')
    console.error('加载用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索用户
const handleSearch = () => {
  pagination.page = 1
  loadUsers()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    role: '',
    status: ''
  })
  pagination.page = 1
  loadUsers()
}

// 刷新数据
const refreshData = () => {
  loadUsers()
  ElMessage.success('数据已刷新')
}

// 导出用户
const exportUsers = async () => {
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
  selectedUsers.value = selection
}

// 排序变化
const handleSortChange = ({ prop, order }) => {
  if (prop) {
    sort.field = prop
    sort.order = order === 'ascending' ? 'asc' : 'desc'
    loadUsers()
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadUsers()
}

// 当前页变化
const handleCurrentChange = (page) => {
  pagination.page = page
  loadUsers()
}

// 查看用户详情
const handleViewDetail = (user) => {
  selectedUser.value = user
  detailDialogVisible.value = true
}

// 禁用用户
const handleDisableUser = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要禁用用户 "${user.username}" 吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    user.disableLoading = true
    await adminStore.disableUser(user.id)
    ElMessage.success('用户已禁用')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('禁用用户失败')
    }
  } finally {
    user.disableLoading = false
  }
}

// 启用用户
const handleEnableUser = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要启用用户 "${user.username}" 吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    user.enableLoading = true
    await adminStore.enableUser(user.id)
    ElMessage.success('用户已启用')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('启用用户失败')
    }
  } finally {
    user.enableLoading = false
  }
}

// 删除用户
const handleDeleteUser = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${user.username}" 吗？此操作不可恢复！`,
      '警告',
      {
        type: 'error',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        confirmButtonClass: 'el-button--danger'
      }
    )

    user.deleteLoading = true
    await adminStore.deleteUser(user.id)
    ElMessage.success('用户已删除')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除用户失败')
    }
  } finally {
    user.deleteLoading = false
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedUsers.value.length} 个用户吗？此操作不可恢复！`,
      '警告',
      {
        type: 'error',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        confirmButtonClass: 'el-button--danger'
      }
    )

    batchDeleting.value = true
    const userIds = selectedUsers.value.map(user => user.id)
    await adminStore.batchDeleteUsers(userIds)
    ElMessage.success(`已删除 ${userIds.length} 个用户`)
    selectedUsers.value = []
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  } finally {
    batchDeleting.value = false
  }
}

// 批量禁用
const handleBatchDisable = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要禁用选中的 ${selectedUsers.value.length} 个用户吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    batchOperating.value = true
    const userIds = selectedUsers.value.map(user => user.id)
    await adminStore.batchDisableUsers(userIds)
    ElMessage.success(`已禁用 ${userIds.length} 个用户`)
    selectedUsers.value = []
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量禁用失败')
    }
  } finally {
    batchOperating.value = false
  }
}

// 批量启用
const handleBatchEnable = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要启用选中的 ${selectedUsers.value.length} 个用户吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    batchOperating.value = true
    const userIds = selectedUsers.value.map(user => user.id)
    await adminStore.batchEnableUsers(userIds)
    ElMessage.success(`已启用 ${userIds.length} 个用户`)
    selectedUsers.value = []
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量启用失败')
    }
  } finally {
    batchOperating.value = false
  }
}
</script>
<style scoped lang="scss">
.user-management-page {
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

.user-info {
  display: flex;
  align-items: center;
  gap: $spacing-md;

  .user-avatar {
    flex-shrink: 0;
  }

  .user-details {
    flex: 1;
    min-width: 0;

    .username {
      font-weight: 600;
      color: $text-primary;
      margin-bottom: $spacing-xs;
      @include text-ellipsis;
    }

    .user-email {
      color: $text-secondary;
      font-size: $font-size-small;
      @include text-ellipsis;
    }
  }
}

.time-text {
  color: $text-regular;
  font-size: $font-size-small;
}

.empty-text {
  color: $text-disabled;
  font-size: $font-size-small;
  font-style: italic;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: $spacing-xs;

  .el-button {
    padding: 4px 8px;
    height: auto;
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
@media (max-width: $breakpoint-md) {
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
    }
  }

  .action-buttons {
    flex-direction: column;
    align-items: flex-start;
    gap: $spacing-xs;
  }
}

@media (max-width: $breakpoint-sm) {
  .user-info {
    flex-direction: column;
    align-items: flex-start;
    gap: $spacing-sm;
  }
}
</style>
