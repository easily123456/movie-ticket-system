<template>
  <div class="movie-detail" v-if="movie">
    <!-- 电影基本信息 -->
    <div class="movie-basic-info">
      <el-image
        :src="movie.posterUrl"
        :alt="movie.title"
        class="movie-poster"
        fit="cover"
      >
        <template #error>
          <div class="image-error">
            <el-icon><Picture /></el-icon>
          </div>
        </template>
      </el-image>
      <div class="movie-main-info">
        <h3 class="movie-title">{{ movie.title }}</h3>
        <p v-if="movie.originalTitle" class="movie-original-title">
          {{ movie.originalTitle }}
        </p>
        <div class="movie-meta">
          <el-tag :type="movie.status ? 'success' : 'danger'" size="large">
            {{ movie.status ? '上架' : '下架' }}
          </el-tag>
          <el-tag v-if="movie.isHot" type="danger" size="large">热门</el-tag>
          <el-tag type="primary" size="large">{{ movie.genreName }}</el-tag>
        </div>
        <div class="movie-rating">
          <StarRating :value="movie.rating" :max="5" :size="20" />
          <span class="vote-count">({{ movie.voteCount }}人评价)</span>
        </div>
      </div>
    </div>
    <el-divider />

    <!-- 详细信息 -->
    <el-descriptions :column="2" border>
      <el-descriptions-item label="电影ID">
        {{ movie.id }}
      </el-descriptions-item>
      <el-descriptions-item label="电影类型">
        {{ movie.genreName }}
      </el-descriptions-item>
      <el-descriptions-item label="时长">
        {{ formatDuration(movie.duration) }}
      </el-descriptions-item>
      <el-descriptions-item label="导演">
        {{ movie.director || '未知' }}
      </el-descriptions-item>
      <el-descriptions-item label="演员">
        {{ movie.actors || '未知' }}
      </el-descriptions-item>
      <el-descriptions-item label="上映日期">
        {{ formatDate(movie.releaseDate) }}
      </el-descriptions-item>
      <el-descriptions-item label="国家/地区">
        {{ movie.country || '未知' }}
      </el-descriptions-item>
      <el-descriptions-item label="语言">
        {{ movie.language || '未知' }}
      </el-descriptions-item>
      <el-descriptions-item label="基础价格">
        {{ formatPrice(movie.price) }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ formatTime(movie.createTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="更新时间">
        {{ formatTime(movie.updateTime) }}
      </el-descriptions-item>
    </el-descriptions>
    <!-- 电影描述 -->
    <div class="movie-description-section">
      <h4 class="section-title">电影描述</h4>
      <div class="description-content">
        {{ movie.description || '暂无描述' }}
      </div>
    </div>
    <!-- 预告片 -->
    <div v-if="movie.trailerUrl" class="trailer-section">
      <h4 class="section-title">预告片</h4>
      <div class="trailer-content">
        <video
          :src="movie.trailerUrl"
          controls
          preload="metadata"
          class="trailer-player"
        >
          您的浏览器不支持视频播放
        </video>
        <div class="trailer-link">
          <el-link :href="movie.trailerUrl" target="_blank" type="primary">
            在新窗口打开预告片
          </el-link>
        </div>
      </div>
    </div>
    <!-- 电影统计 -->
    <div class="movie-stats">
      <h4 class="section-title">电影统计</h4>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ movieStats.sessionCount || 0 }}</div>
            <div class="stat-label">场次数量</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ movieStats.orderCount || 0 }}</div>
            <div class="stat-label">订单数量</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ movieStats.commentCount || 0 }}</div>
            <div class="stat-label">评论数量</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ movieStats.favoriteCount || 0 }}</div>
            <div class="stat-label">收藏数量</div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import StarRating from '@/components/common/StarRating.vue'
import { formatTime, formatDate, formatPrice, formatDuration } from '@/utils'

  // const props = defineProps({
  //   movie: {
  //     type: Object,
  //     required: true
  //   }
// })()

const movieStats = ref({})

onMounted(() => {
  loadMovieStats()
})

const loadMovieStats = async () => {
  try {
    // 调用API获取电影统计信息
    // movieStats.value = await adminApi.getMovieStats(props.movie.id)

    // 临时模拟数据
    movieStats.value = {
      sessionCount: 15,
      orderCount: 120,
      commentCount: 45,
      favoriteCount: 89
    }
  } catch (error) {
    console.error('加载电影统计失败:', error)
  }
}
</script>
<style scoped lang="scss">
.movie-detail {
  .movie-basic-info {
    display: flex;
    align-items: flex-start;
    gap: $spacing-lg;
    margin-bottom: $spacing-lg;

    .movie-poster {
      width: 120px;
      height: 160px;
      border-radius: $border-radius-base;
      flex-shrink: 0;
    }

    .image-error {
      width: 120px;
      height: 160px;
      background: $bg-gray;
      border-radius: $border-radius-base;
      display: flex;
      align-items: center;
      justify-content: center;
      color: $text-disabled;

      .el-icon {
        font-size: 32px;
      }
    }

    .movie-main-info {
      flex: 1;

      .movie-title {
        font-size: 24px;
        font-weight: 700;
        color: $text-primary;
        margin: 0 0 $spacing-sm 0;
      }

      .movie-original-title {
        color: $text-secondary;
        margin-bottom: $spacing-md;
      }

      .movie-meta {
        display: flex;
        gap: $spacing-md;
        margin-bottom: $spacing-md;
      }

      .movie-rating {
        display: flex;
        align-items: center;
        gap: $spacing-md;

        .vote-count {
          color: $text-secondary;
        }
      }
    }
  }
}

.movie-description-section {
  margin: $spacing-xl 0;

  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-md;
  }

  .description-content {
    line-height: 1.6;
    color: $text-regular;
    white-space: pre-line;
  }
}

.trailer-section {
  margin: $spacing-xl 0;

  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-md;
  }

  .trailer-content {
    .trailer-player {
      width: 100%;
      max-width: 600px;
      border-radius: $border-radius-base;
      margin-bottom: $spacing-md;
    }
  }
}

.movie-stats {
  margin: $spacing-xl 0;

  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-lg;
  }

  .stat-item {
    text-align: center;
    padding: $spacing-lg;
    background: $bg-gray;
    border-radius: $border-radius-base;

    .stat-value {
      font-size: 24px;
      font-weight: 700;
      color: $primary-color;
      margin-bottom: $spacing-xs;
    }

    .stat-label {
      color: $text-secondary;
      font-size: $font-size-small;
    }
  }
}

// 响应式设计
@media (max-width: $breakpoint-sm) {
  .movie-basic-info {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .movie-meta {
    justify-content: center;
  }

  .movie-stats .stat-item {
    padding: $spacing-md;

    .stat-value {
      font-size: 20px;
    }
  }
}
</style>
