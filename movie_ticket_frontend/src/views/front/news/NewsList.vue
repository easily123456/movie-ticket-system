<template>
  <div class="news-list-page">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>资讯中心</el-breadcrumb-item>
      </el-breadcrumb>
      <h1 class="page-title">资讯中心</h1>
    </div>

    <div class="content-container">
      <!-- 置顶资讯 -->
      <div v-if="topNews.length > 0" class="top-news-section">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon><Bell /></el-icon>
            置顶资讯
          </h2>
        </div>
        <div class="top-news-list">
          <div
            v-for="news in topNews"
            :key="news.id"
            class="top-news-item"
            @click="goToNewsDetail(news.id)"
          >
            <div class="news-content">
              <h3 class="news-title">{{ news.title }}</h3>
              <div class="news-meta">
                <span class="publish-time">{{ formatDateTime(news.publishTime) }}</span>
                <span class="view-count">浏览: {{ news.viewCount }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 资讯列表 -->
      <div class="news-list-section">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon><Document /></el-icon>
            最新资讯
          </h2>
        </div>

        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="5" animated />
        </div>

        <div v-else-if="newsList.length === 0" class="empty-container">
          <el-empty description="暂无资讯">
            <el-button type="primary" @click="loadNews">刷新</el-button>
          </el-empty>
        </div>

        <div v-else class="news-list">
          <div
            v-for="news in newsList"
            :key="news.id"
            class="news-item"
            @click="goToNewsDetail(news.id)"
          >
            <div class="news-cover" v-if="news.coverImage">
              <el-image
                :src="news.coverImage"
                :alt="news.title"
                class="cover-image"
                fit="cover"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>
            <div class="news-info">
              <h3 class="news-title">{{ news.title }}</h3>
              <div class="news-excerpt" v-if="news.content">
                {{ getExcerpt(news.content) }}
              </div>
              <div class="news-meta">
                <span class="author">作者: {{ news.author || '系统' }}</span>
                <span class="publish-time">{{ formatDateTime(news.publishTime) }}</span>
                <span class="view-count">浏览: {{ news.viewCount }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-container" v-if="pagination.total > 0">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 30, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Bell, Document, Picture } from '@element-plus/icons-vue'
import { newsApi } from '@/api'
import { formatDateTime } from '@/utils'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref(false)
const topNews = ref([])
const newsList = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 初始化数据
onMounted(async () => {
  // 如果路由上有 page 查询参数，使用它作为初始页码
  const pageFromQuery = Number(route.query.page) || 1
  pagination.page = pageFromQuery

  await loadTopNews()
  await loadNews()
  // 尝试恢复之前保存的滚动位置（若页码匹配）
  restoreScroll()
})

// 当用户通过浏览器后退/前进返回到带 ?page= 的新闻列表时，保持组件页码与路由一致
watch(() => route.query.page, async (newPage) => {
  const pageNum = Number(newPage) || 1
  if (pageNum !== pagination.page) {
    pagination.page = pageNum
    await loadNews()
    restoreScroll()
  }
})

// 加载置顶资讯
const loadTopNews = async () => {
  try {
    const response = await newsApi.getTopNews()
    topNews.value = response.data || []
  } catch (error) {
    console.error('加载置顶资讯失败:', error)
    ElMessage.error('加载置顶资讯失败')
  }
}

// 加载资讯列表
const loadNews = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page - 1,
      size: pagination.size
    }

    const response = await newsApi.getNewsList(params)
    const data = response.data
    newsList.value = data.content || data || []
    pagination.total = data.totalElements || data.length || 0
  } catch (error) {
    console.error('加载资讯列表失败:', error)
    ElMessage.error('加载资讯列表失败')
  } finally {
    loading.value = false
  }
}

// 获取内容摘要
const getExcerpt = (content, length = 100) => {
  if (!content) return ''
  // 去除HTML标签
  const text = content.replace(/<[^>]*>/g, '')
  return text.length > length ? text.substring(0, length) + '...' : text
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  // 同步到路由，但不新增历史记录（replace），以便浏览器返回行为更可预测
  router.replace({ path: '/news', query: { page: pagination.page } })
  loadNews()
}

// 当前页变化
const handleCurrentChange = (page) => {
  pagination.page = page
  // 更新 URL 查询参数以保存当前页状态
  router.replace({ path: '/news', query: { page: pagination.page } })
  loadNews()
}

// 跳转到资讯详情
const goToNewsDetail = (newsId) => {
  // 保存当前分页和滚动位置，以便返回时恢复
  try {
    const state = {
      page: pagination.page,
      scroll: window.scrollY || window.pageYOffset || 0
    }
    sessionStorage.setItem('news:list:scroll', JSON.stringify(state))
  } catch {
    // ignore
  }

  // 在跳转到详情页时把当前页写入 query，这样回退或从详情返回时能恢复到该页
  router.push({ path: `/news/${newsId}`, query: { page: pagination.page } })
}

// 从 sessionStorage 恢复列表的滚动位置（如果之前跳转到详情页时保存了）
const restoreScroll = () => {
  try {
    const raw = sessionStorage.getItem('news:list:scroll')
    if (!raw) return
    const obj = JSON.parse(raw)
    if (obj && Number(obj.page) === Number(pagination.page)) {
      nextTick(() => {
        const top = Number(obj.scroll) || 0
        window.scrollTo({ top, left: 0 })
        sessionStorage.removeItem('news:list:scroll')
      })
    }
  } catch {
    // ignore parse error
  }
}
</script>

<style scoped lang="scss">
.news-list-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: $spacing-lg;
}

.page-header {
  margin-bottom: $spacing-xl;

  .page-title {
    font-size: 28px;
    font-weight: 700;
    color: $text-primary;
    margin: $spacing-md 0 0 0;
  }
}

.content-container {
  .section-header {
    margin-bottom: $spacing-lg;

    .section-title {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      font-size: 20px;
      font-weight: 600;
      color: $text-primary;
      margin: 0;
    }
  }
}

.top-news-section {
  margin-bottom: $spacing-xl;
  background: $bg-white;
  border-radius: $border-radius-base;
  box-shadow: $shadow-base;
  padding: $spacing-lg;
}

.top-news-list {
  .top-news-item {
    padding: $spacing-md 0;
    border-bottom: 1px solid $border-light;
    cursor: pointer;
    transition: background-color 0.3s;

    &:last-child {
      border-bottom: none;
    }

    &:hover {
      background-color: $bg-gray;
      border-radius: $border-radius-small;
    }

        .news-content {
      .news-title {
        font-size: 16px;
        font-weight: 600;
        color: $text-primary;
        margin: 0 0 $spacing-xs 0;
        /* single-line ellipsis fallback (replaced missing mixin) */
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .news-meta {
        display: flex;
        gap: $spacing-md;
        color: $text-secondary;
        font-size: $font-size-small;

        .publish-time,
        .view-count {
          display: flex;
          align-items: center;
          gap: $spacing-xs;
        }
      }
    }
  }
}

.news-list-section {
  background: $bg-white;
  border-radius: $border-radius-base;
  box-shadow: $shadow-base;
  padding: $spacing-lg;
  overflow: hidden;
}

.loading-container,
.empty-container {
  padding: $spacing-xl 0;
}

.news-list {
  .news-item {
    display: flex;
    gap: $spacing-lg;
    padding: $spacing-lg 0;
    border-bottom: 1px solid $border-light;
    cursor: pointer;
    transition: background-color 0.3s;

    &:last-child {
      border-bottom: none;
    }

    &:hover {
      background-color: $bg-gray;
      border-radius: $border-radius-small;
      padding-left: $spacing-md;
      padding-right: $spacing-md;
    }

    .news-cover {
      flex-shrink: 0;

      .cover-image {
        width: 150px;
        height: 100px;
        border-radius: $border-radius-small;
      }

      .image-error {
        width: 150px;
        height: 100px;
        background: $bg-gray;
        border-radius: $border-radius-small;
        display: flex;
        align-items: center;
        justify-content: center;
        color: $text-placeholder;

        .el-icon {
          font-size: 24px;
        }
      }
    }

    .news-info {
      flex: 1;
      min-width: 0;

      .news-title {
        font-size: 18px;
        font-weight: 600;
        color: $text-primary;
        margin: 0 0 $spacing-sm 0;
        /* multi-line ellipsis (2 lines) fallback */
        display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .news-excerpt {
        color: $text-regular;
        font-size: $font-size-base;
        line-height: 1.5;
        margin-bottom: $spacing-sm;
        /* multi-line ellipsis (3 lines) fallback */
        display: -webkit-box;
  line-clamp: 3;
  -webkit-line-clamp: 3;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .news-meta {
        display: flex;
        gap: $spacing-lg;
        color: $text-secondary;
        font-size: $font-size-small;

        .author,
        .publish-time,
        .view-count {
          display: flex;
          align-items: center;
          gap: $spacing-xs;
        }
      }
    }
  }
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: $spacing-lg;
  padding-top: $spacing-lg;
  border-top: 1px solid $border-light;
}

@media (max-width: $breakpoint-sm) {
  .news-list-page {
    padding: $spacing-md;
  }

  .news-list {
    .news-item {
      flex-direction: column;

      .news-cover {
        .cover-image,
        .image-error {
          width: 100%;
          height: 150px;
        }
      }
    }
  }

  .top-news-list {
    .top-news-item {
      .news-content {
        .news-meta {
          flex-direction: column;
          gap: $spacing-xs;
        }
      }
    }
  }

  .news-list-section,
  .top-news-section {
    padding: $spacing-md;
  }

  .news-info {
    .news-meta {
      flex-direction: column;
      gap: $spacing-xs !important;
    }
  }
}
</style>
