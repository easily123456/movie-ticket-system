<template>
  <div class="genre-management-page">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <h1 class="page-title">电影类型管理</h1>
      <div class="header-actions">
        <el-button type="primary" :icon="Plus" @click="handleCreate">
          新增类型
        </el-button>
        <el-button :icon="Refresh" @click="refreshData">
          刷新
        </el-button>
      </div>
    </div>
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="类型名称">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入类型名称"
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
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="全部状态"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="全部" value="" />
            <el-option label="启用" :value="true" />
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
    <!-- 类型列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="table-header">
          <div class="table-title">
            <span>类型列表</span>
            <span class="total-text">共 {{ pagination.total }} 个类型</span>
          </div>
          <div class="table-actions">
            <el-button
              v-if="selectedGenres.length > 0"
              type="danger"
              :icon="Delete"
              @click="handleBatchDelete"
              :disabled="batchDeleting"
            >
              批量删除 ({{ selectedGenres.length }})
            </el-button>
            <el-button
              v-if="selectedGenres.length > 0"
              type="warning"
              :icon="Close"
              @click="handleBatchDisable"
              :disabled="batchOperating"
            >
              批量禁用
            </el-button>
            <el-button
              v-if="selectedGenres.length > 0"
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
          :data="genres"
          style="width: 100%"
          @selection-change="handleSelectionChange"
          @sort-change="handleSortChange"
        >
          <el-table-column type="selection" width="55" />

          <el-table-column label="类型名称" prop="name" min-width="120" sortable="custom">
            <template #default="{ row }">
              <div class="genre-name">
                <span class="name">{{ row.name }}</span>
                <el-tag v-if="row.isDefault" type="info" size="small">默认</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="类型描述" prop="description" min-width="200">
            <template #default="{ row }">
              <span class="description">{{ row.description || '暂无描述' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="排序" prop="sortOrder" width="80" sortable="custom">
            <template #default="{ row }">
              <el-tag size="small">{{ row.sortOrder }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status ? 'success' : 'danger'" size="small">
                {{ row.status ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="电影数量" width="100" sortable="custom" prop="movieCount">
            <template #default="{ row }">
              <span class="count">{{ row.movieCount || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="160" sortable="custom" prop="createTime">
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
                  @click="handleDisable(row)"
                  :loading="row.disableLoading"
                >
                  禁用
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
                  :disabled="row.isDefault"
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
    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :close-on-click-modal="false"
      @closed="handleDialogClosed"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="类型名称" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入类型名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="类型描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入类型描述"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number
            v-model="form.sortOrder"
            :min="0"
            :max="999"
            controls-position="right"
          />
          <div class="form-tip">数字越小排序越前</div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="form.status"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="handleSubmit"
          :loading="submitting"
        >
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
// import { nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Refresh,
  Search,
  Edit,
  Delete,
  Close,
  Check
} from '@element-plus/icons-vue'
import { formatTime } from '@/utils'
import { useAdminStore } from '@/stores/admin'

const adminStore = useAdminStore()

// 响应式数据
const loading = ref(false)
const batchOperating = ref(false)
const batchDeleting = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const isEdit = ref(false)

const formRef = ref()
const genres = ref([])
const selectedGenres = ref([])

const searchForm = reactive({
  keyword: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const sort = reactive({
  field: 'sortOrder',
  order: 'asc'
})

const form = reactive({
  id: null,
  name: '',
  description: '',
  sortOrder: 0,
  status: true
})

const rules = {
  name: [
    { required: true, message: '请输入类型名称', trigger: 'blur' },
    { min: 1, max: 50, message: '类型名称长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '类型描述不能超过 200 个字符', trigger: 'blur' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序值', trigger: 'blur' }
  ]
}

const dialogTitle = ref('新增类型')

onMounted(() => {
  loadGenres()
})

// 加载类型列表
const loadGenres = async () => {
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
    if (searchForm.status !== '') {
      params.status = searchForm.status
    }

    const response = await adminStore.getGenres(params)
    genres.value = response.content || response
    pagination.total = response.totalElements || response.length || 0

    // 重置操作状态
    genres.value.forEach(genre => {
      genre.disableLoading = false
      genre.enableLoading = false
      genre.deleteLoading = false
    })
  } catch (error) {
    ElMessage.error('加载类型列表失败')
    console.error('加载类型列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索类型
const handleSearch = () => {
  pagination.page = 1
  loadGenres()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    status: ''
  })
  pagination.page = 1
  loadGenres()
}

// 刷新数据
const refreshData = () => {
  loadGenres()
  ElMessage.success('数据已刷新')
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedGenres.value = selection
}

// 排序变化
const handleSortChange = ({ prop, order }) => {
  if (prop) {
    sort.field = prop
    sort.order = order === 'ascending' ? 'asc' : 'desc'
    loadGenres()
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadGenres()
}

// 当前页变化
const handleCurrentChange = (page) => {
  pagination.page = page
  loadGenres()
}

// 新增类型
const handleCreate = () => {
  isEdit.value = false
  dialogTitle.value = '新增类型'
  resetForm()
  dialogVisible.value = true
}

// 编辑类型
const handleEdit = (genre) => {
  isEdit.value = true
  dialogTitle.value = '编辑类型'
  Object.assign(form, {
    id: genre.id,
    name: genre.name,
    description: genre.description,
    sortOrder: genre.sortOrder,
    status: genre.status
  })
  dialogVisible.value = true
}

// 禁用类型
const handleDisable = async (genre) => {
  try {
    await ElMessageBox.confirm(
      `确定要禁用类型 "${genre.name}" 吗？禁用后该类型将不可用。`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    genre.disableLoading = true
    await adminStore.disableGenre(genre.id)
    ElMessage.success('类型已禁用')
    loadGenres()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('禁用类型失败')
    }
  } finally {
    genre.disableLoading = false
  }
}

// 启用类型
const handleEnable = async (genre) => {
  try {
    await ElMessageBox.confirm(
      `确定要启用类型 "${genre.name}" 吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    genre.enableLoading = true
    await adminStore.enableGenre(genre.id)
    ElMessage.success('类型已启用')
    loadGenres()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('启用类型失败')
    }
  } finally {
    genre.enableLoading = false
  }
}

// 删除类型
const handleDelete = async (genre) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除类型 "${genre.name}" 吗？此操作不可恢复！`,
      '警告',
      {
        type: 'error',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        confirmButtonClass: 'el-button--danger'
      }
    )

    genre.deleteLoading = true
    await adminStore.deleteGenre(genre.id)
    ElMessage.success('类型已删除')
    loadGenres()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除类型失败')
    }
  } finally {
    genre.deleteLoading = false
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedGenres.value.length} 个类型吗？此操作不可恢复！`,
      '警告',
      {
        type: 'error',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        confirmButtonClass: 'el-button--danger'
      }
    )

    batchDeleting.value = true
    const genreIds = selectedGenres.value.map(genre => genre.id)
    await adminStore.batchDeleteGenres(genreIds)
    ElMessage.success(`已删除 ${genreIds.length} 个类型`)
    selectedGenres.value = []
    loadGenres()
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
      `确定要禁用选中的 ${selectedGenres.value.length} 个类型吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    batchOperating.value = true
    const genreIds = selectedGenres.value.map(genre => genre.id)
    await adminStore.batchDisableGenres(genreIds)
    ElMessage.success(`已禁用 ${genreIds.length} 个类型`)
    selectedGenres.value = []
    loadGenres()
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
      `确定要启用选中的 ${selectedGenres.value.length} 个类型吗？`,
      '提示',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    batchOperating.value = true
    const genreIds = selectedGenres.value.map(genre => genre.id)
    await adminStore.batchEnableGenres(genreIds)
    ElMessage.success(`已启用 ${genreIds.length} 个类型`)
    selectedGenres.value = []
    loadGenres()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量启用失败')
    }
  } finally {
    batchOperating.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: null,
    name: '',
    description: '',
    sortOrder: 0,
    status: true
  })

  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 对话框关闭
const handleDialogClosed = () => {
  resetForm()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  const valid = await formRef.value.validate()
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      await adminStore.updateGenre(form.id, form)
      ElMessage.success('类型更新成功')
    } else {
      await adminStore.createGenre(form)
      ElMessage.success('类型创建成功')
    }

    dialogVisible.value = false
    loadGenres()
  } catch (error) {
    console.error(isEdit.value ? '更新类型失败' : '创建类型失败:', error)
    ElMessage.error(isEdit.value ? '更新类型失败' : '创建类型失败')
  } finally {
    submitting.value = false
  }
}
</script>
<style scoped lang="scss">
.genre-management-page {
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

.genre-name {
  display: flex;
  align-items: center;
  gap: $spacing-sm;

  .name {
    font-weight: 600;
    color: $text-primary;
  }
}

.description {
  color: $text-regular;
  line-height: 1.4;

  @include text-ellipsis-multi(2);
}

.count {
  font-weight: 600;
  color: $primary-color;
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

.form-tip {
  color: $text-secondary;
  font-size: $font-size-small;
  margin-top: $spacing-xs;
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
</style>
