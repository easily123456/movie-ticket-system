<template>
  <div class="home-page">
    
    <section class="banner-section">
      <div class="container">
        
        <el-carousel
          height="500px"
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
                  
                  查看详情
                </el-button>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
    </section>
    
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
          <MovieCard
            v-for="movie in hotMovies"
            :key="movie.id"
            :movie="movie"
            @buy-ticket="handleBuyTicket"
          />
        </div>
      </div>
    </section>
    
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
          <MovieCard
            v-for="movie in upcomingMovies"
            :key="movie.id"
            :movie="movie"
            @buy-ticket="handleBuyTicket"
          />
        </div>
      </div>
    </section>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import MovieCard from '@/components/front/MovieCard.vue'
import { movieApi } from '@/api'
import { ArrowRight } from '@element-plus/icons-vue'

defineOptions({ name: 'HomePage' })

const router = useRouter()

const banners = ref([
  {
    id: 1,
    image: '/images/banner-1.jpg',
    title: '《流浪地球3》概念海报曝光，吴京刘德华再续前缘',
    description: '',
    link: '/news/15?page=1'
  },
  {
    id: 2,
    image: '/images/banner-2.jpg',
    title: 'IMAX中国与万达电影达成战略合作，未来将有更多IMAX影院落地',
    description: '',
    link: '/news/18?page=1'
  },
  {
    id: 3,
    image: '/images/banner-3.jpg',
    title: '电影《满江红》延长上映至3月，票房有望再创新高',
    description: '',
    link: '/news/17?page=1'
  }
])

const hotMovies = ref([])

const upcomingMovies = ref([])

onMounted(() => {
  
  loadHomeData()
})

const loadHomeData = async () => {
  try {
    const [hotRes, upcomingRes] = await Promise.all([
      movieApi.getHotMovies(4),
      movieApi.getNewMovies(4)
    ])
  hotMovies.value = hotRes.data || []
  upcomingMovies.value = (upcomingRes.data && (upcomingRes.data.content || upcomingRes.data)) || []

  
  } catch (error) {
    console.error('加载首页数据失败:', error)
    ElMessage.error('数据加载失败')
  }
}

const handleBannerError = (event) => {
  event.target.src = '/images/default-banner.jpg'
}

const handleBannerClick = (banner) => {
  router.push(banner.link)
}

const handleBuyTicket = (movie) => {
  ElMessage.success(`即将跳转到 ${movie.title} 的购票页面`)
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
  height: 500px;
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
  grid-template-columns: repeat(auto-fit, minmax(270px, 1fr));
  gap: $spacing-lg;

  @media (max-width: $breakpoint-sm) {
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
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
    height: 360px;

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