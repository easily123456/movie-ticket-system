<template>
  <el-dialog
    v-model="visible"
    title="导出数据"
    width="500px"
    :before-close="handleClose"
  >
    <div class="export-dialog">
      <!-- 导出格式选择 -->
      <div class="export-section">
        <h4 class="section-title">导出格式</h4>
        <el-radio-group v-model="exportFormat">
          <el-radio label="excel">Excel (.xlsx)</el-radio>
          <el-radio label="csv">CSV (.csv)</el-radio>
          <el-radio label="pdf">PDF (.pdf)</el-radio>
        </el-radio-group>
      </div>
      <!-- 导出范围 -->
      <div class="export-section">
        <h4 class="section-title">导出范围</h4>
        <el-radio-group v-model="exportRange">
          <el-radio label="all">全部数据</el-radio>
          <el-radio label="selected">选中数据 ({{ selectedCount }} 项)</el-radio>
          <el-radio label="current">当前页数据</el-radio>
        </el-radio-group>
      </div>
      <!-- 字段选择 -->
      <div class="export-section">
        <h4 class="section-title">导出字段</h4>
        <div class="fields-selection">
          <div class="fields-actions">
            <el-button type="primary" link @click="selectAllFields">
              全选
            </el-button>
            <el-button type="primary" link @click="clearAllFields">
              清空
            </el-button>
          </div>
          <el-checkbox-group v-model="selectedFields">
            <div class="fields-grid">
              <el-checkbox
                v-for="field in availableFields"
                :key="field.key"
                :label="field.key"
                :disabled="field.required"
              >
                {{ field.label }}
              </el-checkbox>
            </div>
          </el-checkbox-group>
        </div>
      </div>
      <!-- 导出选项 -->
      <div class="export-section">
        <h4 class="section-title">导出选项</h4>
        <el-checkbox v-model="includeHeader">包含表头</el-checkbox>
        <el-checkbox v-model="includeTimestamp">包含时间戳</el-checkbox>
        <el-checkbox v-model="compressFile">压缩文件</el-checkbox>
      </div>
    </div>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button
        type="primary"
        :loading="exporting"
        @click="handleExport"
      >
        {{ exporting ? '导出中...' : '开始导出' }}
      </el-button>
    </template>
  </el-dialog>
</template>
<script setup>
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  selectedCount: {
    type: Number,
    default: 0
  },
  availableFields: {
    type: Array,
    default: () => []
  }
})

const emits = defineEmits(['update:modelValue', 'export'])

const visible = ref(false)
const exporting = ref(false)

// 导出配置
const exportFormat = ref('excel')
const exportRange = ref('all')
const selectedFields = ref([])
const includeHeader = ref(true)
const includeTimestamp = ref(true)
const compressFile = ref(false)

// 计算选中的字段
const computedSelectedFields = computed(() => {
  if (selectedFields.value.length === 0) {
    return props.availableFields.filter(field => field.required).map(field => field.key)
  }
  return selectedFields.value
})

// 监听显示状态
watch(() => props.modelValue, (newVal) => {
  visible.value = newVal
  if (newVal) {
    initializeFields()
  }
})

watch(visible, (newVal) => {
  emits('update:modelValue', newVal)
})

// 初始化字段选择
const initializeFields = () => {
  selectedFields.value = props.availableFields
    .filter(field => field.defaultSelected !== false)
    .map(field => field.key)
}

// 全选字段
const selectAllFields = () => {
  selectedFields.value = props.availableFields.map(field => field.key)
}

// 清空字段选择
const clearAllFields = () => {
  selectedFields.value = props.availableFields
    .filter(field => field.required)
    .map(field => field.key)
}

// 处理关闭
const handleClose = () => {
  visible.value = false
  exporting.value = false
}

// 处理导出
const handleExport = async () => {
  if (computedSelectedFields.value.length === 0) {
    ElMessage.warning('请选择至少一个导出字段')
    return
  }

  exporting.value = true

  try {
    const exportConfig = {
      format: exportFormat.value,
      range: exportRange.value,
      fields: computedSelectedFields.value,
      options: {
        includeHeader: includeHeader.value,
        includeTimestamp: includeTimestamp.value,
        compressFile: compressFile.value
      }
    }

    emits('export', exportConfig)

    // 模拟导出过程
    setTimeout(() => {
      exporting.value = false
      visible.value = false
      ElMessage.success('导出任务已开始，文件将自动下载')
    }, 1500)
  } catch (error) {
    exporting.value = false
    ElMessage.error('导出失败：' + error.message)
  }
}
</script>
<style scoped lang="scss">
.export-dialog {
  .export-section {
    margin-bottom: 24px;

    .section-title {
      font-size: 14px;
      font-weight: 600;
      color: $text-primary;
      margin: 0 0 12px 0;
    }
  }
}

.fields-selection {
  .fields-actions {
    margin-bottom: 12px;

    .el-button {
      padding: 0;
      margin-right: 16px;
    }
  }

  .fields-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    gap: 12px;
    max-height: 200px;
    overflow-y: auto;
    padding: 12px;
    border: 1px solid $border-light;
    border-radius: 4px;

    .el-checkbox {
      margin-right: 0;
    }
  }
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-radio-group) {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

:deep(.el-checkbox-group) {
  width: 100%;
}
</style>
