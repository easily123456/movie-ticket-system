<template>
  <div class="news-detail" v-if="news">
    <!-- 资讯基本信息 -->
    <el-descriptions title="资讯基本信息" :column="2" border>
      <el-descriptions-item label="资讯标题">
        <span class="news-title">{{ news.title }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="资讯状态">
        <el-tag :type="news.status ? 'success' : 'info'" size="large">
          {{ news.status ? '已发布' : '草稿' }}
        </el-tag>
        <el-tag v-if="news.isTop" type="danger" size="large" style="margin-left: 8px;">
          置顶
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="作者">
        {{ news.author || '系统' }}
      </el-descriptions-item>
      <el-descriptions-item label="浏览数">
        {{ news.viewCount }}
      </el-descriptions-item>
      <el-descriptions-item label="发布时间" v-if="news.publishTime">
        {{ formatDateTime(news.publishTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ formatDateTime(news.createTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="更新时间">
        {{ formatDateTime(news.updateTime) }}
      </el-descriptions-item>
    </el-descriptions>
    <!-- 封面图片 -->
    <el-divider />
    <div class="cover-section" v-if="news.coverImage">
      <h3 class="section-title">封面图片</h3>
      <el-image
        :src="news.coverImage"
        :alt="news.title"
        class="cover-image"
        fit="cover"
        :preview-src-list="[news.coverImage]"
      >
        <template #error>
          <div class="image-error">
            <el-icon><Picture /></el-icon>
            <div>图片加载失败</div>
          </div>
        </template>
      </el-image>
    </div>
    <!-- 资讯内容 -->
    <el-divider />
    <div class="content-section">
      <h3 class="section-title">资讯内容</h3>
      <div class="content-container">
        <div class="content-text" v-html="formatContent(news.content)"></div>
      </div>
      <div class="content-stats">
        <span class="word-count">字数: {{ getWordCount(news.content) }}</span>
        <span class="char-count">字符数: {{ news.content?.length || 0 }}</span>
      </div>
    </div>
    <!-- 操作记录 -->
    <el-divider />
    <div class="operation-records">
      <h3 class="section-title">操作记录</h3>
      <el-timeline>
        <el-timeline-item
          v-for="record in operationRecords"
          :key="record.id"
          :timestamp="formatDateTime(record.time)"
          :type="record.type"
        >
          {{ record.action }}
          <div class="record-operator" v-if="record.operator">
            操作人: {{ record.operator }}
          </div>
        </el-timeline-item>
      </el-timeline>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import { formatDateTime } from '@/utils'

const props = defineProps({
  news: {
    type: Object,
    required: true
  }
})

const operationRecords = ref([])

// 格式化内容（处理换行等）
const formatContent = (content) => {
  if (!content) return ''
  // 将换行符转换为HTML换行
  return content.replace(/\n/g, '<br>')
}

// 获取字数统计
const getWordCount = (content) => {
  if (!content) return 0
  // 简单的中文字数统计（实际项目可能需要更复杂的逻辑）
  const chineseChars = content.match(/[\u4e00-\u9fa5]/g) || []
  const otherChars = content.replace(/[\u4e00-\u9fa5]/g, '').replace(/\s/g, '')
  return chineseChars.length + Math.ceil(otherChars.length / 3)
}

onMounted(() => {
  loadOperationRecords()
})

// 加载操作记录
const loadOperationRecords = async () => {
  try {
    // 模拟操作记录数据
    operationRecords.value = [
      {
        id: 1,
        time: props.news.createTime,
        action: '资讯创建',
        type: 'primary',
        operator: props.news.author || '系统'
      }
    ]

    if (props.news.publishTime && props.news.status) {
      operationRecords.value.push({
        id: 2,
        time: props.news.publishTime,
        action: '资讯发布',
        type: 'success',
        operator: props.news.author || '系统'
      })
    }

    if (props.news.updateTime && props.news.updateTime !== props.news.createTime) {
      operationRecords.value.push({
        id: 3,
        time: props.news.updateTime,
        action: '资讯更新',
        type: 'info',
        operator: props.news.author || '系统'
      })
    }

    // 按时间排序
    operationRecords.value.sort((a, b) => new Date(a.time) - new Date(b.time))
  } catch (error) {
    console.error('加载操作记录失败:', error)
  }
}
</script>
<style scoped lang="scss">
.news-detail {
  .news-title {
    font-weight: 600;
    color: $text-primary;
    font-size: 16px;
  }
}

.cover-section {
  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-lg;
  }

  .cover-image {
    width: 100%;
    max-width: 400px;
    height: 200px;
    border-radius: $border-radius-base;
  }

  .image-error {
    width: 100%;
    height: 200px;
    background: $bg-gray;
    border-radius: $border-radius-base;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: $text-disabled;

    .el-icon {
      font-size: 32px;
      margin-bottom: $spacing-sm;
    }
  }
}

.content-section {
  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-lg;
  }

  .content-container {
    border: 1px solid $border-light;
    border-radius: $border-radius-base;
    padding: $spacing-lg;
    background: $bg-white;
    margin-bottom: $spacing-md;

    .content-text {
      line-height: 1.8;
      color: $text-regular;
      white-space: pre-wrap;
      word-wrap: break-word;
    }
  }

  .content-stats {
    display: flex;
    gap: $spacing-lg;
    color: $text-secondary;
    font-size: $font-size-small;

    span {
      padding: 4px 8px;
      background: $bg-gray;
      border-radius: $border-radius-small;
    }
  }
}

.operation-records {
  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-lg;
  }

  .record-operator {
    color: $text-secondary;
    font-size: $font-size-small;
    margin-top: 2px;
  }
}

:deep(.el-descriptions__title) {
  font-size: 16px;
  font-weight: 600;
  color: $text-primary;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
  color: $text-regular;
}

:deep(.el-timeline) {
  padding-left: $spacing-md;
}

// 响应式设计
@media (max-width: $breakpoint-sm) {
  .content-stats {
    flex-direction: column;
    gap: $spacing-sm !important;
  }

  .cover-image {
    max-width: 100% !important;
  }
}
</style>
