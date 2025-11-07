<template>
  <div class="news-detail-page">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/news' }">资讯中心</el-breadcrumb-item>
        <el-breadcrumb-item>资讯详情</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="content-container">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>
      
      <div v-else-if="news" class="news-content">
        <!-- 资讯标题区域 -->
        <div class="news-header">
          <h1 class="news-title">{{ news.title }}</h1>
          <div class="news-meta">
            <span class="author">作者: {{ news.author || '系统' }}</span>
            <span class="publish-time">发布时间: {{ formatDateTime(news.publishTime) }}</span>
            <span class="view-count">浏览: {{ news.viewCount }}</span>
          </div>
        </div>

        <!-- 封面图片 -->
        <div v-if="news.coverImage" class="cover-section">
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
        <div class="content-section">
          <div class="content-text" v-html="news.content"></div>
        </div>

        <!-- 相关操作 -->
        <div class="news-actions">
          <el-button @click="goBack">返回资讯列表</el-button>
        </div>
      </div>

      <div v-else class="empty-container">
        <el-empty description="资讯不存在或已被删除">
          <el-button type="primary" @click="goBack">返回资讯列表</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { newsApi } from '@/api'
import { formatDateTime } from '@/utils'

const route = useRoute()
const router = useRouter()

const newsId = route.params.id
const news = ref(null)
const loading = ref(false)

// 初始化数据
onMounted(async () => {
  await loadNewsDetail()
})

// 加载资讯详情
const loadNewsDetail = async () => {
  if (!newsId) {
    ElMessage.error('参数错误')
    router.push('/news')
    return
  }

  loading.value = true
  try {
    const response = await newsApi.getNewsDetail(newsId)
    news.value = response.data
  } catch (error) {
    console.error('加载资讯详情失败:', error)
    ElMessage.error('加载资讯详情失败')
  } finally {
    loading.value = false
  }
}

// 返回资讯列表
const goBack = () => {
  router.push('/news')
}
</script>

<style scoped lang="scss">
.news-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: $spacing-lg;
}

.page-header {
  margin-bottom: $spacing-lg;
}

.content-container {
  background: $bg-white;
  border-radius: $border-radius-base;
  box-shadow: $shadow-base;
  overflow: hidden;
}

.loading-container,
.empty-container {
  padding: $spacing-xl;
}

.news-content {
  padding: $spacing-lg;
}

.news-header {
  margin-bottom: $spacing-xl;
  padding-bottom: $spacing-lg;
  border-bottom: 1px solid $border-light;

  .news-title {
    font-size: 28px;
    font-weight: 700;
    color: $text-primary;
    margin: 0 0 $spacing-md 0;
    line-height: 1.4;
  }

  .news-meta {
    display: flex;
    gap: $spacing-lg;
    color: $text-secondary;
    font-size: $font-size-small;

    @media (max-width: $breakpoint-sm) {
      flex-direction: column;
      gap: $spacing-sm;
    }
  }
}

.cover-section {
  margin-bottom: $spacing-xl;
  text-align: center;

  .cover-image {
    max-width: 100%;
    max-height: 400px;
    border-radius: $border-radius-base;
  }

  .image-error {
    width: 100%;
    height: 300px;
    background: $bg-gray;
    border-radius: $border-radius-base;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: $text-disabled;

    .el-icon {
      font-size: 48px;
      margin-bottom: $spacing-sm;
    }
  }
}

.content-section {
  margin-bottom: $spacing-xl;

  .content-text {
    line-height: 1.8;
    color: $text-regular;
    font-size: 16px;
    
    // 处理富文本内容样式
    :deep(img) {
      max-width: 100%;
      height: auto;
      border-radius: $border-radius-small;
    }

    :deep(p) {
      margin: 0 0 $spacing-md 0;
    }

    :deep(h1),
    :deep(h2),
    :deep(h3),
    :deep(h4),
    :deep(h5),
    :deep(h6) {
      margin: $spacing-lg 0 $spacing-md 0;
      color: $text-primary;
    }

    :deep(ul),
    :deep(ol) {
      padding-left: $spacing-lg;
      margin: 0 0 $spacing-md 0;
    }

    :deep(li) {
      margin-bottom: $spacing-xs;
    }
  }
}

.news-actions {
  padding-top: $spacing-lg;
  border-top: 1px solid $border-light;
  text-align: center;
}

@media (max-width: $breakpoint-sm) {
  .news-detail-page {
    padding: $spacing-md;
  }

  .news-header {
    .news-title {
      font-size: 24px;
    }
  }

  .news-content {
    padding: $spacing-md;
  }
}
</style>