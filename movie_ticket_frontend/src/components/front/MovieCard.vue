<template>
  <div class="movie-card" @click="handleClick">
    <div class="movie-poster">
      <img
        :src="movie.posterUrl || '/images/default-movie-poster.jpg'"
        :alt="movie.title"
        @error="handleImageError"
      />
      <div class="movie-overlay">
        <div class="movie-rating">
          <span class="rating-star">★</span>
          {{ movie.formattedRating || '暂无评分' }}
        </div>
        <div class="movie-tags">
          <span v-if="movie.isHot" class="tag hot">热门</span>
          <span v-if="movie.isNew" class="tag new">新上映</span>
        </div>
      </div>
    </div>

    <div class="movie-info">
      <h3 class="movie-title">{{ movie.title }}</h3>
      <p class="movie-original-title" v-if="movie.originalTitle && movie.originalTitle !== movie.title">
        {{ movie.originalTitle }}
      </p>

      <div class="movie-meta">
        <span class="meta-item">
          <i class="icon">🎬</i>
          {{ movie.genreName }}
        </span>
        <span class="meta-item">
          <i class="icon">⏱️</i>
          {{ movie.formattedDuration || '未知' }}
        </span>
        <span class="meta-item">
          <i class="icon">📅</i>
          {{ formatDate(movie.releaseDate) }}
        </span>
      </div>

      <div class="movie-director">
        <span>导演：{{ movie.director || '未知' }}</span>
      </div>

      <div class="movie-actions">
        <el-button type="primary" size="small" @click.stop="handleBookClick">
          立即购票
        </el-button>
        <el-button size="small" @click.stop="handleDetailClick">
          查看详情
        </el-button>
      </div>
    </div>
  </div>
</template>
<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  movie: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const emits = defineEmits(['book', 'detail'])

const handleImageError = (event) => {
  event.target.src = '/images/default-movie-poster.jpg'
}

const formatDate = (dateString) => {
  if (!dateString) return '未知'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

const handleClick = () => {
  emits('detail', props.movie)
}

const handleBookClick = () => {
  emits('book', props.movie)
}

const handleDetailClick = () => {
  router.push(`/movie/${props.movie.id}`)
}
</script>
<style scoped lang="scss">
.movie-card {
  background: $bg-white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);

    .movie-poster img {
      transform: scale(1.05);
    }
  }
}

.movie-poster {
  position: relative;
  width: 100%;
  height: 300px;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
  }
}

.movie-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    to bottom,
    transparent 60%,
    rgba(0, 0, 0, 0.7) 100%
  );
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 12px;
}

.movie-rating {
  background: rgba(255, 193, 7, 0.9);
  color: $bg-white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  align-self: flex-start;

  .rating-star {
    color: $bg-white;
    margin-right: 2px;
  }
}

.movie-tags {
  display: flex;
  gap: 4px;

  .tag {
    padding: 2px 6px;
    border-radius: 4px;
    font-size: 10px;
    font-weight: bold;
    color: $bg-white;

    &.hot {
      background: $danger-color;
    }

    &.new {
      background: $success-color;
    }
  }
}

.movie-info {
  padding: 16px;
}

.movie-title {
  font-size: 16px;
  font-weight: 600;
  color: $text-primary;
  margin: 0 0 4px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.movie-original-title {
  font-size: 12px;
  color: $text-regular;
  margin: 0 0 12px 0;
  font-style: italic;
}

.movie-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;

  .meta-item {
    font-size: 12px;
    color: $text-secondary;
    display: flex;
    align-items: center;
    gap: 2px;

    .icon {
      font-size: 10px;
    }
  }
}

.movie-director {
  font-size: 12px;
  color: $text-regular;
  margin-bottom: 16px;
}

.movie-actions {
  display: flex;
  gap: 8px;

  .el-button {
    flex: 1;
  }
}

@media (max-width: 768px) {
  .movie-poster {
    height: 250px;
  }

  .movie-info {
    padding: 12px;
  }

  .movie-actions {
    flex-direction: column;
  }
}
</style>
    z-index: 2; /* 设置层级，确保标签在海报上方显示 */
