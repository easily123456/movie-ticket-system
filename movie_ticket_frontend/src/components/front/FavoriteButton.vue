<template>
  <el-button
    :class="['favorite-button', { active: isFavorited }]"
    :type="isFavorited ? 'primary' : 'default'"
    :loading="loading"
    :disabled="!isAuthenticated"
    @click="handleFavorite"
  >
    <el-icon>
      <Star />
    </el-icon>
    <span class="button-text">
      {{ isFavorited ? '已收藏' : '收藏' }}
    </span>
    <span v-if="showCount" class="favorite-count">
      {{ favoriteCount }}
    </span>
  </el-button>
</template>
<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useFavoriteStore } from '@/stores/favorite'
import { useAuthStore } from '@/stores/auth'
import { Star } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  movieId: {
    type: [String, Number],
    required: true
  },
  showCount: {
    type: Boolean,
    default: true
  },
  size: {
    type: String,
    default: 'default'
  }
})

const router = useRouter()
const favoriteStore = useFavoriteStore()
const authStore = useAuthStore()

const isFavorited = ref(false)
const favoriteCount = ref(0)
const loading = ref(false)

// 计算属性
const isAuthenticated = computed(() => authStore.isAuthenticated)

// 初始化数据
onMounted(() => {
  loadFavoriteStatus()
  loadFavoriteCount()
})

// 监听电影ID变化
watch(() => props.movieId, () => {
  loadFavoriteStatus()
  loadFavoriteCount()
})

// 加载收藏状态
const loadFavoriteStatus = async () => {
  if (!isAuthenticated.value) return

  try {
    isFavorited.value = await favoriteStore.checkFavorite(props.movieId)
  } catch (error) {
    console.error('加载收藏状态失败:', error)
  }
}

// 加载收藏数量
const loadFavoriteCount = async () => {
  try {
    favoriteCount.value = await favoriteStore.getFavoriteCount(props.movieId)
  } catch (error) {
    console.error('加载收藏数量失败:', error)
  }
}

// 处理收藏操作
const handleFavorite = async () => {
  if (!isAuthenticated.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  loading.value = true
  try {
    if (isFavorited.value) {
      await favoriteStore.removeFavorite(props.movieId)
      isFavorited.value = false
      favoriteCount.value = Math.max(0, favoriteCount.value - 1)
      ElMessage.success('取消收藏成功')
    } else {
      await favoriteStore.addFavorite(props.movieId)
      isFavorited.value = true
      favoriteCount.value += 1
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    loading.value = false
  }
}
</script>
<style scoped lang="scss">
.favorite-button {
  transition: all 0.3s ease;

  &.active {
    .el-icon {
      color: $warning-color;
    }
  }

  .button-text {
    margin-left: 4px;
  }

  .favorite-count {
    margin-left: 4px;
    font-size: 12px;
    opacity: 0.8;
  }
}

:deep(.el-button) {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
</style>
