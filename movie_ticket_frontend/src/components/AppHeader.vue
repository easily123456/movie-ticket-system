<template>                          <!-- Vue组件的模板根标签，包含所有的HTML结构 -->
  <header class="app-header">       <!-- 语义化的头部标签，应用整个页面的顶部导航栏 -->
    <div class="container">         <!-- 容器div，用于限制内容的最大宽度并居中显示 -->
      <div class="header-content">  <!-- 头部内容区域，包含logo、导航、搜索框等主要元素 -->
        <!-- Logo -->
        <router-link to="/" class="logo">  <!-- 应用logo，点击可跳转到首页 -->
          <div class="logo-content"> <!-- 应用logo的容器 -->
            <span class="logo-icon">🎥</span>
            <span class="logo-text">星光影城</span> <!-- 应用名称 -->
          </div>
        </router-link>
        <!-- 主导航 -->
        <nav class="main-nav">
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            class="nav-item"
            :class="{ active: $route.path === item.path }"
          >
            {{ item.name }} <!-- 显示导航项的名称 ，{{}}意为数据会自动更新，并替换模板中的变量-->
          </router-link>
        </nav>
        <!-- nav 搜索框。router-link是 Vue Router 提供的一个组件标签，用于在 Vue 应用中实现页面导航跳转 -->
        <!-- v-for="item in navItems"  Vue指令，遍历navItems数组，为每个导航项创建一个router-link
            :key="item.path" Vue的key属性，用于标识每个列表项，优化渲染性能
            :to="item.path" router-link的属性，指定导航的目标路径
            class="nav-item" CSS类名，用于设置导航项的样式
            :class="{ active: $route.path === item.path }" 动态绑定CSS类，当当前路由路径匹配时添加active类，这样就可以通过 CSS 样式来高亮显示当前激活的导航项
            :为Vue的动态绑定语法，允许将JavaScript表达式绑定到HTML属性上，支持响应式更新
        -->

        <!-- 搜索框 -->
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索电影、演员、导演..."
            size="large"
            @keyup.enter="handleSearch"
            @focus="showSearchSuggestions = true"
            @blur="hideSuggestions"
          >
          <!-- el-input为Element Plus的输入框组件，用于创建一个具有输入功能的文本框
            v-model="searchKeyword" 双向数据绑定，将输入框内容与searchKeyword变量同步
            placeholder="搜索电影、演员、导演..." 输入框的占位符文本
            size="large"  设置输入框尺寸为大号
            @keyup.enter="handleSearch" 监听回车键按下事件，触发handleSearch方法
            @focus="showSearchSuggestions = true" 监听获得焦点事件，
                设置showSearchSuggestions为true，会触发搜索建议面板的显示，让用户可以看到相关的搜索建议
            @blur="hideSuggestions" 监听失去焦点事件，触发hideSuggestions方法
          -->
            <template #prefix> <!--template标签用于包装其他元素，不生成实际HTML元素，#prefix插槽用于在输入框前添加图标-->
              <el-icon><Search /></el-icon>
              <!--el-icon 是 Element Plus UI 库提供的图标容器组件，用于显示搜索图标，
                  <Search>是Element Plus UI库中的图标组件，用于显示搜索图标-->
            </template>
          </el-input>

          <!-- 搜索建议 -->
          <div
            v-if="showSearchSuggestions && searchKeyword"
            class="search-suggestions"
          >
          <!-- v-if="showSearchSuggestions && searchKeyword" Vue指令，条件渲染搜索建议面板，
              通常在输入框获得焦点时和输入关键字时显示该面板 -->
            <div class="suggestion-item" @mousedown="handleSuggestionClick(searchKeyword)">
            <!-- @mousedown="handleSuggestionClick(searchKeyword)" 监听鼠标点击事件，触发handleSuggestionClick方法 -->
              搜索 "{{ searchKeyword }}" <!--如果用户输入"阿凡达"，这里会显示："搜索 "阿凡达""-->
            </div>
          </div>
        </div>
        <!-- 用户操作 -->
        <div class="user-actions">
          <template v-if="authStore.isAuthenticated"> <!-- 如果用户已登录，显示用户操作项 -->
            <!-- 在用户登录部分添加 -->
            <el-dropdown @command="handleUserCommand">
              <span class="user-menu">
                <el-avatar :size="32" :src="authStore.user?.avatar" />
                <span class="username">{{ authStore.user?.username }}</span>
                <el-icon><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="orders">
                    <el-icon><Ticket /></el-icon>
                    我的订单
                  </el-dropdown-item>
                  <el-dropdown-item command="comments">
                    <el-icon><ChatDotRound /></el-icon>
                    我的评论
                  </el-dropdown-item>
                  <el-dropdown-item command="favorites">
                    <el-icon><Star /></el-icon>
                    我的收藏
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <div class="auth-buttons">
              <el-button link @click="$router.push('/login')">登录</el-button>
              <!-- link意为设置按钮外观为链接样式，@click="..." 监听按钮点击事件，触发$router.push方法跳转到指定路由 -->
              <el-button type="primary" @click="$router.push('/register')">
              <!-- type="primary" 设置按钮为主要操作按钮样式，primary由Element Plus提供的主题色 -->
                注册
              </el-button>
            </div>
          </template>
        </div>
      </div>
    </div>
  </header>
</template>
<script setup>
// setup 无需使用 export default 导出组件选项，自动将顶层绑定暴露给模板，无需显式 return
import { ref, computed } from 'vue'//computed用于创建计算属性，ref用于创建响应式变量
import { useRouter } from 'vue-router'
import {
  Search,
  User,
  // Document,
  Star,
  // Monitor,
  SwitchButton,
  ArrowDown,
  Ticket,
  ChatDotRound
} from '@element-plus/icons-vue' // 引入Element Plus图标组件
import { useAuthStore } from '@/stores/auth'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter() // 获取路由实例，用于编程式导航
const authStore = useAuthStore() // 获取认证状态的store实例

const searchKeyword = ref('') // 定义响应式变量searchKeyword，用于存储搜索关键字
const showSearchSuggestions = ref(false) // 定义响应式变量showSearchSuggestions，控制搜索建议面板的显示与隐藏

const navItems = computed(() => [ //不接受对象参数，返回一个数组，[]用于构建数组
  { name: '首页', path: '/' },
  { name: '电影', path: '/movies' },
  //{ name: '影院', path: '/cinemas' },
  //{ name: '榜单', path: '/rankings' },
  { name: '资讯', path: '/news' }
])

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      name: 'MovieSearch',
      query: { q: searchKeyword.value.trim() }//query接收一个键值对，q表示查询参数的键名，searchKeyword.value.trim()表示查询参数的值
    })
    searchKeyword.value = ''// 清空搜索框
    showSearchSuggestions.value = false// 隐藏搜索建议面板
  }
}

const handleSuggestionClick = (keyword) => {
  searchKeyword.value = keyword // 设置搜索关键字
  handleSearch() // 执行搜索
}

const hideSuggestions = () => { // 延迟200ms后隐藏搜索建议，防止点击建议时面板消失
  setTimeout(() => {
    showSearchSuggestions.value = false
  }, 200)
}

const handleUserCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/user')
      break
    case 'orders':
      router.push('/user/orders')
      break
    case 'comments':
      router.push('/user/comments')
      break
    case 'favorites':
      router.push('/user/favorites')
      break
    case 'admin':
      window.open('/admin', '_blank')// window.open是浏览器的内置方法，用于在新标签页打开管理后台
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await authStore.logout()
        router.push('/')
        ElMessage.success('退出成功')
      } catch (error) {
        // 用户取消操作或出现其他错误
        if (error !== 'cancel') {
          ElMessage.error('退出登录失败')
        }
      }
      break
  }
}
</script>
<style scoped lang="scss">//scoped表示当前样式只对当前组件生效，不与全局样式冲突，lang="scss"表示使用SCSS预处理器编写样式
.user-menu {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.2s ease;

  &:hover {
    background: $bg-color;
  }

  .username {
    font-size: 14px;
    color: $text-primary;
  }
}

.app-header {
  background: $bg-white; /* 应用白色背景色，确保头部与页面背景一致 */
  box-shadow: $shadow-base; /* 添加基础阴影效果，使头部在页面滚动时有立体感 */
  position: sticky; /* 使用粘性定位，使头部在页面滚动时固定在顶部 */
  top: 0; /* 配合sticky定位，指定距离顶部为0，实现吸顶效果 */
  z-index: $z-index-sticky; /* 设置较高的层级，确保头部始终在其他内容之上 */
  border-bottom: 1px solid $border-light; /* 添加底部边框线，与页面内容分隔 */
}

.header-content {
  display: flex; /* 使用flex布局，使子元素水平排列并便于对齐 */
  align-items: center; /* 垂直居中对齐所有子元素 */
  justify-content: space-between; /* 在主轴上均匀分布子元素，左右两端对齐 */
  height: 64px; /* 设置头部内容区域固定高度，确保视觉一致性 */
  gap: $spacing-lg; /* 设置子元素之间的间距，避免元素过于紧凑 */
}

.logo {
  flex-shrink: 0;/* 防止logo在flex容器中被压缩，确保logo始终完整显示 */

  .logo-content {
    display: flex; /* 使用flex布局，使图标和文字水平排列 */
    align-items: center; /* 垂直居中对齐图标和文字 */
    gap: $spacing-sm; /* 设置图标和文字之间的间距 */
  }

  .logo-icon {
    //font-size: 28px; /* 设置图标大小 */
    font-size: 24px;
  }

  .logo-text {
    //font-size: 28px; /* 设置文字大小 */
    font-size: 24px;
    font-weight: 700; /* 加粗 */
    color: $primary-color; /* 主题色 */
    letter-spacing: 1px; /* 设置字母间距，使文字更具辨识度 */
  }
}

.main-nav {
  display: flex; /* 使用flex布局，使导航项水平排列并便于对齐 */
  gap: $spacing-xs; /* 设置导航项之间的间距 */
  flex: 1; /* 使导航栏占据剩余空间 */
  justify-content: center; /* 居中对齐导航项 */

  .nav-item {
    padding: $spacing-sm $spacing-md; /* 设置内边距 */
    color: $text-regular; /* 设置文字颜色 */
    font-weight: 600; /* 设置字体粗细 */
    //font-size: 22px; /* 增大字体大小 */
    font-size: 18px;
    border-radius: $border-radius-base; /* 设置边框圆角 */
    transition: $transition-base; /* 设置过渡效果 */
    white-space: nowrap; /* 防止文本换行 */
    margin: 0 $spacing-xxl; /* 增加导航项之间的间距 */
    //margin: 0 $spacing-lg;

    &:hover {
      color: $primary-color; /* 设置鼠标悬停时的颜色 */
      background: rgba($primary-color, 0.1); /* 设置鼠标悬停时的背景色 */
    }

    &.active {
      color: $primary-color; /* 设置激活时的颜色 */
      background: rgba($primary-color, 0.1); /* 设置激活时的背景色 */
    }
  }
}

.search-box {
  position: relative; /* relative是相对定位，用于定位元素，使搜索建议面板相对于搜索框进行定位 */
  width: 280px; /* 设置搜索框的固定宽度 */
  flex-shrink: 0; /* 防止搜索框在flex容器中被压缩，确保搜索框始终完整显示 */

  :deep(.el-input__wrapper) { /* 使用deep选择器，用于选择搜索框的子元素，并添加样式 */
    border-radius: $border-radius-round; /* 设置搜索框的边框圆角 */
  }
}

.search-suggestions { /* 搜索建议面板 */
  position: absolute; /* absolute是绝对定位，用于定位元素，使搜索建议面板相对于页面进行定位 */
  top: 100%; /* 将搜索建议面板放置在搜索框的下方 */
  left: 0; /* 将搜索建议面板放置在搜索框的左侧 */
  right: 0; /* 将搜索建议面板放置在搜索框的右侧 */
  background: $bg-white; /* 设置搜索建议面板的背景色 */
  border: 1px solid $border-light; /* 设置搜索建议面板的边框样式 */
  border-radius: $border-radius-base; /* 设置搜索建议面板的边框圆角 */
  box-shadow: $shadow-light; /* 设置搜索建议面板的阴影效果 */
  margin-top: $spacing-xs; /* 设置搜索建议面板与搜索框之间的间距 */
  z-index: $z-index-dropdown; /* 设置搜索建议面板的层级，确保搜索建议始终在其他内容之上 */

  .suggestion-item {
    padding: $spacing-sm $spacing-md; /* 设置搜索建议项的内边距 */
    cursor: pointer; /* 设置鼠标悬停时的光标样式为指针，表示该项可点击 */
    transition: $transition-base; /* 设置过渡效果 */

    &:hover {
      background: $bg-gray; /* 设置鼠标悬停时的背景色 */
    }
  }
}

.user-actions {
  flex-shrink: 0;  /* 防止用户信息在flex容器中被压缩，确保用户信息始终完整显示 */
}

.auth-buttons {
  display: flex; /* 使用flex布局，使按钮水平排列并便于对齐 */
  align-items: center; /* 垂直居中对齐按钮 */
  gap: $spacing-sm; /* 设置按钮之间的间距 */
}

// 响应式设计
@media (max-width: $breakpoint-md) { /* 当屏幕宽度小于等于中等断点时应用以下样式 */
  .header-content { /* 修改头部内容区域样式 */
    gap: $spacing-md; /* 减小子元素之间的间距 */
  }

  .main-nav {
    gap: 0; /* 移除导航项之间的间距 */

    .nav-item {
      padding: $spacing-sm; /* 减小导航项的内边距 */
      font-size: $font-size-small; /* 减小导航项的字体大小 */
    }
  }

  .search-box {
    width: 200px; /* 减小搜索框的宽度 */
  }
}

@media (max-width: $breakpoint-sm) { /* 当屏幕宽度小于等于小型断点时应用以下样式 */
  .header-content { /* 修改头部内容区域样式 */
    gap: $spacing-sm; /* 进一步减小子元素之间的间距 */
  }

  .logo .logo-text {
    display: none; /* 隐藏logo文字 */
  }

  .main-nav {
    display: none; /* 隐藏主导航 */
  }

  .search-box {
    width: 160px;   /* 减小搜索框的宽度 */
  }
}
</style>
