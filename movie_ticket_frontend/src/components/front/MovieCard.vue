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
          {{ movie.rating || '暂无评分' }}
        </div>
        <div class="movie-tags">
          <span v-if="movie.isHot" class="tag hot">热门</span>
          <span v-if="movie.isNew && !movie.isHot" class="tag new">新上映</span>
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
          {{ formattedReleaseDate }}
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
import { computed } from 'vue'

const props = defineProps({
  movie: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const emits = defineEmits(['buy-ticket', 'favorite'])

const handleImageError = (event) => {
  event.target.src = '/images/default-movie-poster.jpg'
}

const formattedReleaseDate = computed(() => {
  if (!props.movie.releaseDate) return '未知'
  const date = new Date(props.movie.releaseDate)
  return date.toLocaleDateString('zh-CN')
})

const handleClick = () => {
  router.push({ name: 'movie-detail', params: { id: props.movie.id } })
}

const handleBookClick = (event) => {
  event.stopPropagation()
  emits('buy-ticket', props.movie)
}

const handleDetailClick = (event) => {
  event.stopPropagation()
  router.push({ name: 'movie-detail', params: { id: props.movie.id } })
}
</script>
<style scoped lang="scss">
.movie-card {
  background: $bg-white;
  border-radius: $border-radius-base;
  box-shadow: $shadow-base;
  overflow: hidden;
  transition: $transition-base;
  cursor: pointer;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-light;

    .movie-poster img {
      transform: scale(1.05);
    }
  }
}

.movie-poster {
  position: relative;
  width: 100%;
  aspect-ratio: 27 / 40;
  overflow: hidden;
  background: #000; // 为使用 contain 时提供信噪更好的底色

  img {
    width: 100%;
    height: 100%;
    object-fit: contain;
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
  padding: $spacing-sm;
}

.movie-rating {
  background: rgba(255, 193, 7, 0.9);
  color: $bg-white;
  padding: $spacing-xs $spacing-sm;
  border-radius: $border-radius-small;
  font-size: $font-size-small;
  font-weight: bold;
  align-self: flex-start;

  .rating-star {
    color: $bg-white;
    margin-right: 2px;
  }
}

.movie-tags {
  display: flex;
  gap: $spacing-xs;

  .tag {
    padding: 2px 6px;
    border-radius: $border-radius-small;
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
  padding: $spacing-md;
}

.movie-title {
  font-size: $font-size-base;
  font-weight: 600;
  color: $text-primary;
  margin: 0 0 $spacing-xs 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.movie-original-title {
  font-size: $font-size-small;
  color: $text-regular;
  margin: 0 0 $spacing-sm 0;
  font-style: italic;
}

.movie-meta {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-xs;
  margin-bottom: $spacing-sm;

  .meta-item {
    font-size: $font-size-small;
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
  font-size: $font-size-small;
  color: $text-regular;
  margin-bottom: $spacing-md;
}

.movie-actions {
  display: flex;
  gap: $spacing-sm;

  .el-button {
    flex: 1;
    font-size: $font-size-small;
  }
}

@media (max-width: $breakpoint-sm) {
  .movie-poster {
    height: auto; // 维持 27/40 比例自适应高度
  }

  .movie-info {
    padding: $spacing-sm;
  }

  .movie-actions {
    flex-direction: column;
  }
}
</style>
