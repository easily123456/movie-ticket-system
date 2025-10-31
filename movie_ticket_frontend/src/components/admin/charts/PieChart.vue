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
  height: {
    type: Number,
    default: 300
  },
  colors: {
    type: Array,
    default: () => ['#ff5f16', '#67c23a', '#e6a23c', '#f56c6c', '#909399']
  },
  showLegend: {
    type: Boolean,
    default: true
  },
  radius: {
    type: Array,
    default: () => ['50%', '70%']
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

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: props.showLegend ? {
      orient: 'horizontal',
      bottom: 0,
      data: props.data.map(item => item.name),
      textStyle: {
        color: '#606266'
      }
    } : undefined,
    series: [
      {
        name: '数据分布',
        type: 'pie',
        radius: props.radius,
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#ffffff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: props.data.map((item, index) => ({
          ...item,
          itemStyle: {
            color: props.colors[index % props.colors.length]
          }
        }))
      }
    ]
  }

  chartInstance.setOption(option)
}

// 响应式更新
watch(() => props.data, updateChart, { deep: true })

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
