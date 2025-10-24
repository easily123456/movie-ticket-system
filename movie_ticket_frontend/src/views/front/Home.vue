<template>
  <div class="home-page">
    <!-- 轮播图区域 -->
    <section class="banner-section">
      <div class="container">
        <el-carousel
          height="400px"
          indicator-position="outside"
          :interval="5000"
          arrow="always"
        >
          <el-carousel-item v-for="item in banners" :key="item.id">
            <div class="banner-item">
              <img
                :src="item.image"
                :alt="item.title"
                class="banner-image"
                @error="handleBannerError"
              />
              <div class="banner-content">
                <h2 class="banner-title">{{ item.title }}</h2>
                <p class="banner-desc">{{ item.description }}</p>
                <el-button type="primary" size="large" @click="handleBannerClick(item)">
                  立即购票
                </el-button>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
    </section>
    <!-- 正在热映 -->
    <section class="movie-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">
            <span class="title-icon">🔥</span>
            正在热映
          </h2>
          <router-link to="/movies?type=hot" class="more-link">
            查看更多
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>

        <div class="movie-grid">
          <div
            v-for="movie in hotMovies"
            :key="movie.id"
            class="movie-card"
            @click="goToMovieDetail(movie.id)"
          >
            <div class="movie-poster">
              <img
                :src="movie.poster"
                :alt="movie.title"
                class="poster-image"
              />
              <div class="movie-overlay">
                <div class="movie-actions">
                  <el-button type="primary" size="small" @click.stop="handleBuyTicket(movie)">
                    <el-icon><Ticket /></el-icon>
                    购票
                  </el-button>
                  <el-button size="small" @click.stop="handleAddFavorite(movie)">
                    <el-icon><Star /></el-icon>
                    收藏
                  </el-button>
                </div>
              </div>
              <div v-if="movie.isHot" class="hot-badge">热映</div>
            </div>
            <div class="movie-info">
              <h3 class="movie-title text-ellipsis">{{ movie.title }}</h3>
              <p class="movie-rating">
                <el-rate
                  v-model="movie.rating"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value}"
                  size="small"
                />
              </p>
              <p class="movie-genre">{{ movie.genre }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>
    <!-- 即将上映 -->
    <section class="movie-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">
            <span class="title-icon">🎭</span>
            即将上映
          </h2>
          <router-link to="/movies?type=upcoming" class="more-link">
            查看更多
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>

        <div class="movie-grid">
          <div
            v-for="movie in upcomingMovies"
            :key="movie.id"
            class="movie-card coming-soon"
          >
            <div class="movie-poster">
              <img
                :src="movie.poster"
                :alt="movie.title"
                class="poster-image"
              />
              <div class="release-date">
                {{ movie.releaseDate }} 上映
              </div>
              <div class="movie-overlay">
                <el-button type="primary" size="small" @click.stop="handleRemind(movie)">
                  <el-icon><Bell /></el-icon>
                  提醒我
                </el-button>
              </div>
            </div>
            <div class="movie-info">
              <h3 class="movie-title text-ellipsis">{{ movie.title }}</h3>
              <p class="movie-genre">{{ movie.genre }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowRight,
  Ticket,
  Star,
  Bell
} from '@element-plus/icons-vue'

defineOptions({ name: 'HomePage' })

const router = useRouter()

// 模拟数据 - 实际开发中从API获取
const banners = ref([
  {
    id: 1,
    image: '/images/banner-1.jpg',
    title: '年度科幻巨制',
    description: '震撼视效，颠覆想象',
    link: '/movie/1'
  },
  {
    id: 2,
    image: '/images/banner-2.jpg',
    title: '浪漫爱情故事',
    description: '温暖治愈，触动心弦',
    link: '/movie/2'
  },
  {
    id: 3,
    image: '/images/banner-3.jpg',
    title: '动作冒险大片',
    description: '惊险刺激，全程高能',
    link: '/movie/3'
  }
])

const hotMovies = ref([
  {
    id: 1,
    title: '星际穿越：时空之谜',
    poster: '/images/poster-1.jpg',
    rating: 4.8,
    genre: '科幻/冒险',
    isHot: true
  },
  {
    id: 2,
    title: '城市之光',
    poster: '/images/poster-2.jpg',
    rating: 4.6,
    genre: '剧情/爱情',
    isHot: true
  },
  {
    id: 3,
    title: '极限追捕',
    poster: '/images/poster-3.jpg',
    rating: 4.5,
    genre: '动作/犯罪',
    isHot: true
  },
  {
    id: 4,
    title: '奇幻森林',
    poster: '/images/poster-4.jpg',
    rating: 4.7,
    genre: '动画/冒险',
    isHot: true
  }
])

const upcomingMovies = ref([
  {
    id: 5,
    title: '未来战争',
    poster: '/images/poster-5.jpg',
    genre: '科幻/动作',
    releaseDate: '12月25日'
  },
  {
    id: 6,
    title: '时光旅人',
    poster: '/images/poster-6.jpg',
    genre: '爱情/奇幻',
    releaseDate: '1月15日'
  },
  {
    id: 7,
    title: '深海探险',
    poster: '/images/poster-7.jpg',
    genre: '冒险/科幻',
    releaseDate: '2月10日'
  },
  {
    id: 8,
    title: '喜剧之王2',
    poster: '/images/poster-8.jpg',
    genre: '喜剧/剧情',
    releaseDate: '1月30日'
  }
])

onMounted(() => {
  // 页面加载时获取数据
  loadHomeData()
})

const loadHomeData = async () => {
  // 实际开发中调用API
  // try {
  //   const [bannersRes, hotMoviesRes, upcomingMoviesRes] = await Promise.all([
  //     movieApi.getBanners(),
  //     movieApi.getHotMovies(),
  //     movieApi.getUpcomingMovies()
  //   ])
  //   banners.value = bannersRes
  //   hotMovies.value = hotMoviesRes
  //   upcomingMovies.value = upcomingMoviesRes
  // } catch (error) {
  //   ElMessage.error('数据加载失败')
  // }
}

const handleBannerError = (event) => {
  event.target.src = '/images/default-banner.jpg'
}

const handleBannerClick = (banner) => {
  router.push(banner.link)
}

const goToMovieDetail = (movieId) => {
  router.push(`/movie/${movieId}`)
}

const handleBuyTicket = (movie) => {
  ElMessage.success(`即将跳转到 ${movie.title} 的购票页面`)
  // 实际开发中跳转到选座购票页面
}

const handleAddFavorite = (movie) => {
  ElMessage.success(`已收藏 ${movie.title}`)
}

const handleRemind = (movie) => {
  ElMessage.info(`已设置 ${movie.title} 的上映提醒`)
}
</script>
<style scoped lang="scss">
.home-page {
  padding-bottom: $spacing-xxl;
}

.banner-section {
  margin-bottom: $spacing-xxl;
}

.banner-item {
  position: relative;
  height: 400px;
  border-radius: $border-radius-base;
  overflow: hidden;

  .banner-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .banner-content {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: $spacing-xl;
    background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
    color: $bg-white;

    .banner-title {
      font-size: 32px;
      font-weight: 700;
      margin-bottom: $spacing-sm;
    }

    .banner-desc {
      font-size: $font-size-large;
      margin-bottom: $spacing-lg;
      opacity: 0.9;
    }
  }
}

.movie-section {
  margin-bottom: $spacing-xxl;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $spacing-lg;
}

.section-title {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  font-size: 24px;
  font-weight: 700;
  color: $text-primary;

  .title-icon {
    font-size: 28px;
  }
}

.more-link {
  display: flex;
  align-items: center;
  gap: $spacing-xs;
  color: $text-secondary;
  font-weight: 500;
  transition: $transition-base;

  &:hover {
    color: $primary-color;
  }
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: $spacing-lg;

  @media (max-width: $breakpoint-sm) {
    grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
    gap: $spacing-md;
  }
}

.movie-card {
  background: $bg-white;
  border-radius: $border-radius-base;
  overflow: hidden;
  box-shadow: $shadow-base;
  transition: $transition-base;
  cursor: pointer;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-light;

    .movie-overlay {
      opacity: 1;
    }

    .poster-image {
      transform: scale(1.05);
    }
  }

  &.coming-soon {
    .movie-poster::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.3);
      z-index: 1;
    }
  }
}

.movie-poster {
  position: relative;
  aspect-ratio: 2/3;
  overflow: hidden;

  .poster-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: $transition-slow;
  }

  .movie-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.7);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: $transition-base;
    z-index: 2;
  }

  .movie-actions {
    display: flex;
    gap: $spacing-sm;
    flex-direction: column;
  }

  .hot-badge {
    position: absolute;
    top: $spacing-sm;
    left: $spacing-sm;
    background: $danger-color;
    color: $bg-white;
    padding: $spacing-xs $spacing-sm;
    border-radius: $border-radius-small;
    font-size: $font-size-small;
    font-weight: 600;
  }

  .release-date {
    position: absolute;
    bottom: $spacing-sm;
    left: 0;
    right: 0;
    text-align: center;
    color: $bg-white;
    font-weight: 600;
    z-index: 2;
  }
}

.movie-info {
  padding: $spacing-md;

  .movie-title {
    font-size: $font-size-large;
    font-weight: 600;
    margin-bottom: $spacing-xs;
    color: $text-primary;
  }

  .movie-rating {
    margin-bottom: $spacing-xs;
  }

  .movie-genre {
    color: $text-secondary;
    font-size: $font-size-small;
    margin: 0;
  }
}

// 响应式设计
@media (max-width: $breakpoint-md) {
  .banner-item .banner-content {
    padding: $spacing-lg;

    .banner-title {
      font-size: 24px;
    }

    .banner-desc {
      font-size: $font-size-base;
    }
  }

  .section-title {
    font-size: 20px;
  }
}

@media (max-width: $breakpoint-sm) {
  .banner-section {
    margin-bottom: $spacing-xl;
  }

  .banner-item {
    height: 300px;

    .banner-content {
      padding: $spacing-md;

      .banner-title {
        font-size: 20px;
      }

      .banner-desc {
        font-size: $font-size-small;
        margin-bottom: $spacing-md;
      }
    }
  }

  .movie-section {
    margin-bottom: $spacing-xl;
  }
}
</style>
