<template>
  <div v-if="selectedCount > 0" class="batch-actions">
    <div class="batch-info">
      <span class="selected-count">已选择 {{ selectedCount }} 项</span>
      <el-button type="primary" link @click="handleClearSelection">
        清空选择
      </el-button>
    </div>

    <div class="action-buttons">
      <el-dropdown v-if="actions.length > 0" @command="handleAction">
        <el-button type="primary">
          批量操作
          <el-icon class="el-icon--right">
            <arrow-down />
          </el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item
              v-for="action in actions"
              :key="action.command"
              :command="action.command"
              :disabled="action.disabled"
            >
              <el-icon v-if="action.icon">
                <component :is="action.icon" />
              </el-icon>
              {{ action.label }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>

      <el-button
        v-if="exportable"
        type="success"
        @click="handleExport"
      >
        <el-icon><Download /></el-icon>
        导出选中
      </el-button>

      <el-button
        v-if="deletable"
        type="danger"
        @click="handleBatchDelete"
      >
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
    </div>
  </div>
</template>
<script setup>
import { ArrowDown, Download, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps({
  selectedCount: {
    type: Number,
    required: true
  },
  actions: {
    type: Array,
    default: () => []
  },
  exportable: {
    type: Boolean,
    default: true
  },
  deletable: {
    type: Boolean,
    default: true
  }
})

const emits = defineEmits([
  'clear-selection',
  'action',
  'export',
  'delete'
])

// 处理清空选择
const handleClearSelection = () => {
  emits('clear-selection')
}

// 处理批量操作
const handleAction = (command) => {
  emits('action', command)
}

// 处理导出
const handleExport = () => {
  emits('export')
}

// 处理批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${props.selectedCount} 项吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    emits('delete')
  } catch (error) {
    // 用户取消操作
    if (error === 'cancel' || error === undefined) {
      ElMessage.info('已取消删除')
    } else {
      ElMessage.error('删除失败: ' + error.message || '未知错误')
    }
  }
}
</script>
<style scoped lang="scss">
.batch-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba($primary-color, 0.06);
  border: 1px solid rgba($primary-color, 0.3);
  border-radius: 8px;
  padding: 12px 16px;
  margin-bottom: 16px;

  .batch-info {
    display: flex;
    align-items: center;
    gap: 12px;

    .selected-count {
      font-size: 14px;
      font-weight: 500;
      color: $primary-color;
    }
  }

  .action-buttons {
    display: flex;
    gap: 8px;

    .el-button {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }
}

@media (max-width: 768px) {
  .batch-actions {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;

    .batch-info {
      justify-content: space-between;
    }

    .action-buttons {
      justify-content: flex-end;
    }
  }
}
</style>
