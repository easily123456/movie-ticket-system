<template>
  <div ref="chartRef" :style="{ width: '100%', height: height + 'px' }"></div>
</template>
<script setup>
import { ref, onMounted, watch, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  labels: {
    type: Array,
    default: () => []
  },
  color: {
    type: String,
    default: '#ff5f16'
  },
  height: {
    type: Number,
    default: 300
  },
  horizontal: {
    type: Boolean,
    default: false
  },
  showLabel: {
    type: Boolean,
    default: true
  }
})

const chartRef = ref()
let chartInstance = null

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return

  chartInstance = echarts.init(chartRef.value)
  updateChart()
}

// 更新图表
const updateChart = () => {
  if (!chartInstance) return

  const isHorizontal = props.horizontal
  const colors = Array.isArray(props.color) ? props.color : [props.color]

  const option = {
    grid: {
      top: 20,
      right: 20,
      bottom: 30,
      left: 40,
      containLabel: true
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: isHorizontal ? 'value' : 'category',
      data: isHorizontal ? null : props.labels,
      axisLine: {
        lineStyle: {
          color: '#e4e7ed'
        }
      },
      axisLabel: {
        color: '#606266'
      }
    },
    yAxis: {
      type: isHorizontal ? 'category' : 'value',
      data: isHorizontal ? props.labels : null,
      axisLine: {
        lineStyle: {
          color: '#e4e7ed'
        }
      },
      axisLabel: {
        color: '#606266'
      }
    },
    series: [
      {
        type: 'bar',
        data: props.data,
        itemStyle: {
          color: (params) => {
            if (colors.length > 1) {
              return colors[params.dataIndex % colors.length]
            }
            return new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: colors[0] + 'CC' },
              { offset: 1, color: colors[0] + '66' }
            ])
          },
          borderRadius: [2, 2, 0, 0]
        },
        label: props.showLabel ? {
          show: true,
          position: isHorizontal ? 'right' : 'top',
          color: '#303133'
        } : undefined,
        barWidth: '60%'
      }
    ]
  }

  chartInstance.setOption(option)
}

// 响应式更新
watch(() => props.data, updateChart, { deep: true })
watch(() => props.labels, updateChart, { deep: true })

// 处理窗口大小变化
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
  window.removeEventListener('resize', handleResize)
})

defineExpose({
  getInstance: () => chartInstance
})
</script>
