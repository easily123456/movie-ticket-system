<template>
  <div class="home-page">
    <!-- 轮播图区域 -->
    <section class="banner-section">
      <div class="container">
        <!--container 类通常由 CSS 框架（如 Bootstrap、Element UI 等）提供的一个类，用于创建响应式固定宽度容器，自动居中并带有左右内边距 -->
        <el-carousel
          height="400px"
          indicator-position="outside"
          :interval="5000"
          arrow="always"
        >
        <!-- el-carousel 是 Element Plus 的轮播图容器组件 -->
        <!-- indicator-position 设置指示器位置为外部，指示器会显示在轮播图内容区域的下方外部 -->
        <!-- :interval 设置轮播间隔时间为 5000 毫秒，若为0则手动触发轮播 -->
        <!-- arrow 设置箭头始终显示 -->

          <el-carousel-item v-for="item in banners" :key="item.id">
          <!-- el-carousel-item 是 Element Plus 的轮播图子组件，v-for="item in banners" 遍历 banners 数组，key 为当前组件的索引 -->
          <!--banners数组在 <script setup> 部分通过 ref 进行了定义：-->
            <div class="banner-item">
              <img
                :src="item.image"
                :alt="item.title"
                class="banner-image"
                @error="handleBannerError"
              />
              <!--@error 监听的是HTML <img> 元素的原生 error 事件,当图片加载失败（例如图片路径错误、网络问题等）时，该事件会被触发-->
              <div class="banner-content">
                <h2 class="banner-title">{{ item.title }}</h2><!--{{  }}为动态内容绑定-->
                <p class="banner-desc">{{ item.description }}</p>
                <el-button type="primary" size="large" @click="handleBannerClick(item)">
                  <!--handleBannerClick(item)为router.push(banner.link)-->
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
            <el-icon><ArrowRight /></el-icon><!-- 右箭头图标 -->
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
import { useRouter } from 'vue-router' // 导入 useRouter 用于路由跳转
import { ElMessage } from 'element-plus'
import MovieCard from '@/components/front/MovieCard.vue'
import { movieApi } from '@/api'
import { ArrowRight } from '@element-plus/icons-vue'

defineOptions({ name: 'HomePage' })
//明确指定组件的名称为 'HomePage'，组件会以 'HomePage' 的名称显示，而不是默认的文件名或匿名组件

const router = useRouter() // 获取路由实例

// 轮播图（仍使用静态演示图）
const banners = ref([ //ref创建响应式数据
  {
    id: 1, // 轮播图项的唯一标识符
    image: '/images/banner-1.jpg', // 轮播图图片的路径，即 public/images/banner-1.jpg
    title: '年度科幻巨制', // 轮播图的标题
    description: '震撼视效，颠覆想象', // 轮播图的描述信息
    link: '/movie/1' // 点击轮播图后跳转的链接地址
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

const hotMovies = ref([])

const upcomingMovies = ref([])

onMounted(() => {
  // 页面加载时获取数据
  loadHomeData()
})

const loadHomeData = async () => {
  try {
    const [hotRes, upcomingRes] = await Promise.all([
      movieApi.getHotMovies(8),
      movieApi.getMovies({ upcoming: true, page: 0, size: 8, sort: 'releaseDate', direction: 'asc' })
    ])
    hotMovies.value = hotRes.data || []
    // 支持分页或非分页两种返回结构
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
  padding-bottom: $spacing-xxl; // 底部留白
}

.banner-section {
  margin-bottom: $spacing-xxl; // 底部留白
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
  margin-bottom: $spacing-xxl; /* 在每个电影板块底部添加超大号间距，确保板块之间有足够的垂直间隔，提升页面的层次感和可读性 */
}

.section-header {
  display: flex; /* 使用flex布局使标题和"查看更多"链接水平排列 */
  align-items: center; /* 垂直居中对齐标题和链接，确保两者在垂直方向上居中 */
  justify-content: space-between; /* 在主轴上两端对齐，标题在左侧，"查看更多"链接在右侧 */
  margin-bottom: $spacing-lg; /* 在底部添加大号间距，与下方的电影网格保持适当距离 */
}

.section-title {
  display: flex; /* 使用flex布局使标题文字和图标水平排列 */
  align-items: center; /* 垂直居中对齐标题文字和图标 */
  gap: $spacing-sm; /* 设置标题文字和图标之间的间距为小号间距 */
  font-size: 24px; /* 设置标题字体大小为24px，使其醒目易读 */
  font-weight: 700; /* 设置标题字体粗细为700，增强视觉重要性 */
  color: $text-primary; /* 使用主要文本颜色，确保标题具有良好的可读性 */

  .title-icon {
    font-size: 28px; /* 设置图标字体大小为28px，使其与标题图标保持一致 */
  }
}

.more-link {
  display: flex; // 使用flex布局使链接内的图标和文字水平排列
  align-items: center; // 垂直居中对齐文字和图标
  gap: $spacing-xs; // 设置文字和图标之间的间距为小号间距
  color: $text-secondary; // 设置链接文字颜色为次要文本颜色
  font-weight: 500; // 设置链接文字为中等字体粗细
  transition: $transition-base; // 添加基础过渡动画效果，使颜色变化更平滑

  &:hover {
    color: $primary-color; // 鼠标悬停时将链接文字颜色更改为品牌主色
  }
}

.movie-grid {
  display: grid; // 使用CSS Grid布局来排列电影卡片
  grid-template-columns: repeat(auto-fit, minmax(270px, 1fr)); // 调整为与海报宽度匹配的最小列宽270px
  gap: $spacing-lg; // 设置网格项之间的间距为大号间距

  @media (max-width: $breakpoint-sm) { // 当屏幕宽度小于或等于小屏幕断点时应用以下样式
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); // 小屏上稍作提高以保持比例显示
    gap: $spacing-md; // 在小屏幕上使用中等间距
  }
}

.movie-card {
  background: $bg-white; // 设置电影卡片背景为白色，提供清晰的内容展示区域
  border-radius: $border-radius-base; // 添加基础圆角，使卡片边缘更柔和美观
  overflow: hidden; // 隐藏超出卡片边界的内容，确保圆角效果正常显示
  box-shadow: $shadow-base; // 添加基础阴影效果，使卡片具有立体感和层次感
  transition: $transition-base; // 添加基础过渡动画，使hover效果更平滑
  cursor: pointer; // 设置鼠标悬停时显示手型光标，提示用户该区域可点击

  &:hover {
    transform: translateY(-4px); // 鼠标悬停时向上轻微移动4px，产生悬浮效果
    box-shadow: $shadow-light; // 悬停时使用更浅的阴影，增强悬浮感

    .movie-overlay {
      opacity: 1; // 显示电影海报上的操作按钮覆盖层
    }

    .poster-image {
      transform: scale(1.05); // 海报图片轻微放大1.05倍，产生聚焦效果
    }
  }

  &.coming-soon {
    .movie-poster::before {
      content: ''; // 创建伪元素内容为空
      position: absolute; // 设置绝对定位覆盖整个海报区域
      top: 0; // 顶部对齐
      left: 0; // 左侧对齐
      right: 0; // 右侧对齐
      bottom: 0; // 底部对齐
      background: rgba(0, 0, 0, 0.3); // 添加半透明黑色遮罩，使"即将上映"信息更突出
      z-index: 1; // 设置层级在海报图片之上
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
    display: flex; /* 使用flex布局来排列按钮，使购票和收藏按钮垂直排列 */
    gap: $spacing-sm; /* 设置按钮之间的间距为小号间距，提供合适的视觉间隔 */
    flex-direction: column; /* 设置主轴方向为垂直方向，使按钮从上到下排列 */
  }

  .hot-badge {
    position: absolute; /* 使用绝对定位，使热映标签相对于其最近的定位祖先元素(movie-poster)进行定位 */
    top: $spacing-sm; /* 距离顶部间距为小号间距，确保标签不会紧贴边缘 */
    left: $spacing-sm; /* 距离左侧间距为小号间距，确保标签不会紧贴边缘 */
    background: $danger-color; /* 使用危险色(通常是红色)作为背景色，突出显示热门标识 */
    color: $bg-white; /* 文字颜色设为白色，与红色背景形成对比，提高可读性 */
    padding: $spacing-xs $spacing-sm; /* 内边距设置为水平小号间距、垂直超小间距，给文字提供合适的空间 */
    border-radius: $border-radius-small; /* 添加小圆角，使标签边缘更加圆润美观 */
    font-size: $font-size-small; /* 使用小号字体大小，避免标签过大影响海报展示 */
    font-weight: 600; /* 字体粗细设为600，使文字更加醒目易读 */
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
  padding: $spacing-md; /* 为电影信息区域添加中等间距的内边距，使内容与卡片边缘保持适当距离 */

  .movie-title {
    font-size: $font-size-large; /* 使用大号字体显示电影标题，使其更加醒目易于识别 */
    font-weight: 600; /* 设置字体粗细为600，增强标题的视觉重要性 */
    margin-bottom: $spacing-xs; /* 在标题下方添加小号间距，与评分信息保持适当距离 */
    color: $text-primary; /* 使用主要文本颜色，确保标题具有良好的可读性 */
  }

  .movie-rating {
    margin-bottom: $spacing-xs; /* 在评分组件下方添加小号间距，与类型信息保持适当距离 */
  }

  .movie-genre {
    color: $text-secondary; /* 使用次要文本颜色显示电影类型，使其不那么突出但仍然清晰可见 */
    font-size: $font-size-small; /* 使用小号字体显示电影类型，避免与标题竞争视觉注意力 */
    margin: 0; /* 移除默认外边距，确保类型信息紧贴在评分下方 */
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