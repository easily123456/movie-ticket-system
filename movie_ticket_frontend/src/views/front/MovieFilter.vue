<template>
  <div class="movie-filter">
    <div class="filter-section">
      <h3 class="filter-title">筛选条件</h3>

      <!-- 关键词搜索 -->
      <div class="filter-group">
        <label class="filter-label">关键词搜索</label>
        <el-input
          v-model="localFilters.keyword"
          placeholder="搜索电影名称、导演、演员..."
          clearable
          @clear="handleFilterChange"
          @keyup.enter="handleFilterChange"
        >
          <template #append>
            <el-button @click="handleFilterChange">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
      <!-- 电影类型 -->
      <div class="filter-group">
        <label class="filter-label">电影类型</label>
        <el-select
          v-model="localFilters.genreId"
          placeholder="全部类型"
          clearable
          @change="handleFilterChange"
        >
          <el-option
            v-for="genre in genres"
            :key="genre.id"
            :label="genre.name"
            :value="genre.id"
          />
        </el-select>
      </div>
      <!-- 特殊筛选 -->
      <div class="filter-group">
        <label class="filter-label">特殊筛选</label>
        <div class="filter-options">
          <el-checkbox
            v-model="localFilters.isHot"
            :true-label="true"
            :false-label="null"
            @change="handleFilterChange"
          >
            只看热门
          </el-checkbox>
        </div>
      </div>
      <!-- 评分范围 -->
      <div class="filter-group">
        <label class="filter-label">评分范围</label>
        <div class="rating-range">
          <el-input-number
            v-model="localFilters.minRating"
            :min="0"
            :max="10"
            :step="0.1"
            placeholder="最低分"
            controls-position="right"
            @change="handleFilterChange"
          />
          <span class="range-separator">-</span>
          <el-input-number
            v-model="localFilters.maxRating"
            :min="0"
            :max="10"
            :step="0.1"
            placeholder="最高分"
            controls-position="right"
            @change="handleFilterChange"
          />
        </div>
      </div>
      <!-- 上映时间 -->
      <div class="filter-group">
        <label class="filter-label">上映时间</label>
        <div class="date-range">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateRangeChange"
          />
        </div>
      </div>
      <!-- 排序方式 -->
      <div class="filter-group">
        <label class="filter-label">排序方式</label>
        <div class="sort-options">
          <el-radio-group v-model="localFilters.sortBy" @change="handleFilterChange">
            <el-radio label="createTime">最新上映</el-radio>
            <el-radio label="rating">评分最高</el-radio>
            <el-radio label="releaseDate">上映时间</el-radio>
          </el-radio-group>
          <el-radio-group v-model="localFilters.sortOrder" @change="handleFilterChange">
            <el-radio label="desc">降序</el-radio>
            <el-radio label="asc">升序</el-radio>
          </el-radio-group>
        </div>
      </div>
      <!-- 操作按钮 -->
      <div class="filter-actions">
        <el-button type="primary" @click="handleFilterChange">
          应用筛选
        </el-button>
        <el-button @click="handleReset">
          重置筛选
        </el-button>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, watch, computed } from 'vue'
import { Search } from '@element-plus/icons-vue'

const props = defineProps({
  filters: {
    type: Object,
    required: true
  },
  genres: {
    type: Array,
    default: () => []
  }
})

const emits = defineEmits(['filter-change'])

// 本地筛选条件
const localFilters = ref({ ...props.filters })

// 日期范围（用于日期选择器）
const dateRange = ref([])

// 计算属性：将日期范围转换为开始和结束日期
const computedDateRange = computed(() => {
  if (dateRange.value && dateRange.value.length === 2) {
    return {
      releaseDateStart: dateRange.value[0],
      releaseDateEnd: dateRange.value[1]
    }
  }
  return {
    releaseDateStart: null,
    releaseDateEnd: null
  }
})

// 监听props变化
watch(() => props.filters, (newFilters) => {
  localFilters.value = { ...newFilters }

  // 更新日期范围
  if (newFilters.releaseDateStart || newFilters.releaseDateEnd) {
    dateRange.value = [newFilters.releaseDateStart, newFilters.releaseDateEnd]
  } else {
    dateRange.value = []
  }
}, { deep: true, immediate: true })

// 处理筛选条件变化
const handleFilterChange = () => {
  const filters = {
    ...localFilters.value,
    ...computedDateRange.value
  }
  emits('filter-change', filters)
}

// 处理日期范围变化
const handleDateRangeChange = () => {
  handleFilterChange()
}

// 重置筛选条件
const handleReset = () => {
  localFilters.value = {
    keyword: '',
    genreId: null,
    isHot: null,
    releaseDateStart: null,
    releaseDateEnd: null,
    minRating: null,
    maxRating: null,
    sortBy: 'createTime',
    sortOrder: 'desc'
  }
  dateRange.value = []
  handleFilterChange()
}
</script>
<style scoped lang="scss">
.movie-filter {
  background: $bg-white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-section {
  .filter-title {
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 20px 0;
    padding-bottom: 12px;
    border-bottom: 2px solid $border-extra-light;
  }
}

.filter-group {
  margin-bottom: 20px;

  .filter-label {
    display: block;
    font-size: 14px;
    font-weight: 500;
    color: $text-regular;
    margin-bottom: 8px;
  }
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.rating-range {
  display: flex;
  align-items: center;
  gap: 8px;

  .range-separator {
    color: $text-secondary;
    font-weight: 500;
  }

  .el-input-number {
    flex: 1;
  }
}

.date-range {
  .el-date-editor {
    width: 100%;
  }
}

.sort-options {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .el-radio-group {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
  }
}

.filter-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;

  .el-button {
    flex: 1;
  }
}

@media (max-width: 768px) {
  .movie-filter {
    padding: 16px;
  }

  .rating-range {
    flex-direction: column;

    .range-separator {
      display: none;
    }
  }

  .sort-options .el-radio-group {
    flex-direction: column;
    gap: 8px;
  }

  .filter-actions {
    flex-direction: column;
  }
}
</style>
