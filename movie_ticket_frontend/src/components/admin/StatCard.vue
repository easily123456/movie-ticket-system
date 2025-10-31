<template>
  <div class="stat-card" :class="[type, { 'with-chart': showChart }]">
    <div class="stat-content">
      <div class="stat-icon">
        <el-icon :size="24">
          <component :is="icon" />
        </el-icon>
      </div>
      <div class="stat-info">
        <div class="stat-value">{{ formatValue(value) }}</div>
        <div class="stat-title">{{ title }}</div>
        <div v-if="trend !== null" class="stat-trend" :class="trend > 0 ? 'up' : 'down'">
          <el-icon :size="12">
            <CaretTop v-if="trend > 0" />
            <CaretBottom v-else />
          </el-icon>
          <span>{{ Math.abs(trend) }}%</span>
        </div>
      </div>
    </div>
    <div v-if="showChart && chartData" class="stat-chart">
      <LineChart
        :data="chartData"
        :color="chartColor"
        :height="40"
      />
    </div>
  </div>
</template>
<script setup>
import { computed } from 'vue'
import { CaretTop, CaretBottom } from '@element-plus/icons-vue'
import LineChart from './charts/LineChart.vue'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  value: {
    type: [Number, String],
    required: true
  },
  icon: {
    type: [String, Object],
    required: true
  },
  type: {
    type: String,
    default: 'default', // default, primary, success, warning, danger
    validator: (value) => ['default', 'primary', 'success', 'warning', 'danger'].includes(value)
  },
  trend: {
    type: Number,
    default: null
  },
  showChart: {
    type: Boolean,
    default: false
  },
  chartData: {
    type: Array,
    default: null
  }
})

// 计算图表颜色
const chartColor = computed(() => {
  const colors = {
    default: '#ff5f16',
    primary: '#ff5f16',
    success: '#67c23a',
    warning: '#e6a23c',
    danger: '#f56c6c'
  }
  return colors[props.type]
})

// 格式化数值
const formatValue = (value) => {
  if (typeof value === 'number') {
    if (value >= 1000000) {
      return (value / 1000000).toFixed(1) + 'M'
    } else if (value >= 1000) {
      return (value / 1000).toFixed(1) + 'K'
    }
    return value.toString()
  }
  return value
}
</script>
<style scoped lang="scss">
.stat-card {
  background: $bg-white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-left: 4px solid;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  }
  
  // 类型样式
  &.default {
    border-color: $primary-color;
    .stat-icon {
      background: rgba($primary-color, 0.08);
      color: $primary-color;
    }
  }
  
  &.primary {
    border-color: $primary-color;
    .stat-icon {
      background: rgba($primary-color, 0.08);
      color: $primary-color;
    }
  }
  
  &.success {
    border-color: $success-color;
    .stat-icon {
      background: rgba($success-color, 0.1);
      color: $success-color;
    }
  }
  
  &.warning {
    border-color: $warning-color;
    .stat-icon {
      background: rgba($warning-color, 0.1);
      color: $warning-color;
    }
  }
  
  &.danger {
    border-color: $danger-color;
    .stat-icon {
      background: rgba($danger-color, 0.1);
      color: $danger-color;
    }
  }
  
  // 带图表的样式
  &.with-chart {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    height: 140px;
  }
}

.stat-content {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
  
  .stat-value {
    font-size: 28px;
    font-weight: 700;
    color: $text-primary;
    line-height: 1;
    margin-bottom: 4px;
  }
  
  .stat-title {
    font-size: 14px;
    color: $text-regular;
    margin-bottom: 8px;
  }
  
  .stat-trend {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    font-weight: 500;
    
    &.up {
      color: $success-color;
    }
    
    &.down {
      color: $danger-color;
    }
  }
}

.stat-chart {
  margin-top: 12px;
  height: 40px;
}
</style>
