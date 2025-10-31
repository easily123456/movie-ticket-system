<template>
  <div class="user-center-page">
    <div class="container">
      <div class="user-center-content">
        <!-- 侧边栏菜单 -->
        <aside class="sidebar">
          <div class="user-profile">
            <el-avatar :size="80" :src="userInfo.avatar" class="user-avatar">
              {{ userInfo.username?.charAt(0) }}
            </el-avatar>
            <div class="user-info">
              <h3 class="username">{{ userInfo.username }}</h3>
              <p class="user-email">{{ userInfo.email }}</p>
              <el-tag :type="userInfo.role === 'ADMIN' ? 'danger' : 'primary'">
                {{ userInfo.role === 'ADMIN' ? '管理员' : '普通用户' }}
              </el-tag>
            </div>
          </div>
          <el-menu
            :default-active="activeMenu"
            class="side-menu"
            @select="handleMenuSelect"
          >
            <el-menu-item index="profile">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </el-menu-item>
            <el-menu-item index="orders">
              <el-icon><Document /></el-icon>
              <span>我的订单</span>
            </el-menu-item>
            <el-menu-item index="favorites">
              <el-icon><Star /></el-icon>
              <span>我的收藏</span>
            </el-menu-item>
            <el-menu-item index="comments">
              <el-icon><ChatDotRound /></el-icon>
              <span>我的评论</span>
            </el-menu-item>
            <el-menu-item index="security">
              <el-icon><Lock /></el-icon>
              <span>安全设置</span>
            </el-menu-item>
          </el-menu>
        </aside>
        <!-- 主内容区 -->
        <main class="main-content">
          <!-- 个人信息 -->
          <section v-if="activeMenu === 'profile'" class="content-section">
            <h2 class="section-title">个人信息</h2>
            <div class="profile-form">
              <el-form
                :model="profileForm"
                :rules="profileRules"
                ref="profileFormRef"
                label-width="100px"
              >
                <el-form-item label="用户名" prop="username">
                  <el-input
                    v-model="profileForm.username"
                    placeholder="请输入用户名"
                  />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input
                    v-model="profileForm.email"
                    placeholder="请输入邮箱"
                    disabled
                  />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                  <el-input
                    v-model="profileForm.phone"
                    placeholder="请输入手机号"
                  />
                </el-form-item>
                <el-form-item label="头像">
                  <div class="avatar-upload">
                    <el-avatar :size="100" :src="profileForm.avatar" class="avatar-preview">
                      {{ profileForm.username?.charAt(0) }}
                    </el-avatar>
                    <el-upload
                      action="/api/upload/avatar"
                      :show-file-list="false"
                      :on-success="handleAvatarSuccess"
                      :before-upload="beforeAvatarUpload"
                    >
                      <el-button type="primary">更换头像</el-button>
                    </el-upload>
                  </div>
                </el-form-item>
                <el-form-item>
                  <el-button
                    type="primary"
                    @click="handleUpdateProfile"
                    :loading="updatingProfile"
                  >
                    保存修改
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </section>
          <!-- 我的订单 -->
          <section v-else-if="activeMenu === 'orders'" class="content-section">
            <h2 class="section-title">我的订单</h2>
            <div class="orders-content">
              <div class="orders-filter">
                <el-radio-group v-model="orderFilter.status" @change="loadOrders">
                  <el-radio-button label="">全部</el-radio-button>
                  <el-radio-button label="PENDING">待支付</el-radio-button>
                  <el-radio-button label="PAID">已支付</el-radio-button>
                  <el-radio-button label="CANCELLED">已取消</el-radio-button>
                  <el-radio-button label="REFUNDED">已退款</el-radio-button>
                </el-radio-group>
              </div>
              <div class="orders-list">
                <div v-if="ordersLoading" class="loading-container">
                  <el-skeleton :rows="5" animated />
                </div>
                <template v-else>
                  <div v-if="orders.length === 0" class="empty-state">
                    <el-empty description="暂无订单" />
                  </div>
                  <div v-else class="order-items">
                    <div
                      v-for="order in orders"
                      :key="order.id"
                      class="order-item"
                    >
                      <div class="order-header">
                        <div class="order-info">
                          <span class="order-no">订单号：{{ order.orderNo }}</span>
                          <span class="order-time">
                            {{ formatDate(order.createTime, 'YYYY-MM-DD HH:mm') }}
                          </span>
                        </div>
                        <div class="order-status">
                          <el-tag :type="getOrderStatusType(order.status)">
                            {{ getOrderStatusText(order.status) }}
                          </el-tag>
                        </div>
                      </div>
                      <div class="order-details">
                        <div class="movie-info">
                          <el-image
                            :src="order.moviePoster"
                            :alt="order.movieTitle"
                            class="movie-poster"
                            fit="cover"
                          />
                          <div class="movie-details">
                            <h4 class="movie-title">{{ order.movieTitle }}</h4>
                            <div class="session-info">
                              <span>{{ formatDate(order.sessionTime, 'MM月DD日 HH:mm') }}</span>
                              <span>{{ order.hallName }}</span>
                              <span>{{ order.seatCount }}张</span>
                            </div>
                            <div class="seats-info">
                              <span>座位：{{ order.seatNumbers.join(', ') }}</span>
                            </div>
                          </div>
                        </div>
                        <div class="order-price">
                          <span class="price-label">实付：</span>
                          <span class="price-value">{{ formatPrice(order.totalPrice) }}</span>
                        </div>
                      </div>
                      <div class="order-actions">
                        <el-button
                          v-if="order.status === 'PENDING'"
                          type="primary"
                          @click="handlePayOrder(order.id)"
                        >
                          立即支付
                        </el-button>
                        <el-button
                          v-if="order.status === 'PENDING'"
                          @click="handleCancelOrder(order.id)"
                        >
                          取消订单
                        </el-button>
                        <el-button
                          v-if="order.status === 'PAID'"
                          @click="handleViewTicket(order.id)"
                        >
                          查看票码
                        </el-button>
                        <el-button
                          v-if="order.status === 'PAID'"
                          type="danger"
                          @click="handleRefundOrder(order.id)"
                        >
                          申请退款
                        </el-button>
                      </div>
                    </div>
                  </div>
                  <Pagination
                    v-if="ordersPagination.total > 0"
                    :total="ordersPagination.total"
                    :page="ordersPagination.page"
                    :size="ordersPagination.size"
                    @change="handleOrdersPageChange"
                  />
                </template>
              </div>
            </div>
          </section>
          <!-- 我的收藏 -->
          <section v-else-if="activeMenu === 'favorites'" class="content-section">
            <h2 class="section-title">我的收藏</h2>
            <div class="favorites-content">
              <div v-if="favoritesLoading" class="loading-container">
                <el-skeleton :rows="6" animated />
              </div>
              <template v-else>
                <div v-if="favorites.length === 0" class="empty-state">
                  <el-empty description="暂无收藏" />
                </div>
                <div v-else class="favorites-grid">
                  <MovieCard
                    v-for="movie in favorites"
                    :key="movie.id"
                    :movie="movie"
                    show-action
                    @buy-ticket="handleBuyTicket"
                    @favorite="handleUnfavorite"
                  />
                </div>
                <Pagination
                  v-if="favoritesPagination.total > 0"
                  :total="favoritesPagination.total"
                  :page="favoritesPagination.page"
                  :size="favoritesPagination.size"
                  @change="handleFavoritesPageChange"
                />
              </template>
            </div>
          </section>
          <!-- 我的评论 -->
          <section v-else-if="activeMenu === 'comments'" class="content-section">
            <h2 class="section-title">我的评论</h2>
            <div class="comments-content">
              <div v-if="userCommentsLoading" class="loading-container">
                <el-skeleton :rows="5" animated />
              </div>
              <template v-else>
                <div v-if="userComments.length === 0" class="empty-state">
                  <el-empty description="暂无评论" />
                </div>
                <div v-else class="comment-items">
                  <div
                    v-for="comment in userComments"
                    :key="comment.id"
                    class="comment-item"
                  >
                    <div class="comment-movie">
                      <el-image
                        :src="comment.moviePoster"
                        :alt="comment.movieTitle"
                        class="movie-poster"
                        fit="cover"
                      />
                      <div class="movie-info">
                        <h4 class="movie-title">{{ comment.movieTitle }}</h4>
                        <el-rate
                          :model-value="comment.rating"
                          disabled
                          size="small"
                        />
                      </div>
                    </div>
                    <div class="comment-content">
                      <p class="comment-text">{{ comment.content }}</p>
                      <div class="comment-meta">
                        <span class="comment-time">
                          {{ formatDate(comment.createTime, 'YYYY-MM-DD HH:mm') }}
                        </span>
                        <span class="comment-likes">
                          <el-icon><Thumb /></el-icon>
                          {{ comment.likeCount }} 有用
                        </span>
                      </div>
                    </div>
                    <div class="comment-actions">
                      <el-button
                        type="primary"
                        link
                        @click="handleEditComment(comment)"
                      >
                        编辑
                      </el-button>
                      <el-button
                        type="danger"
                        link
                        @click="handleDeleteComment(comment.id)"
                      >
                        删除
                      </el-button>
                    </div>
                  </div>
                </div>
                <Pagination
                  v-if="userCommentsPagination.total > 0"
                  :total="userCommentsPagination.total"
                  :page="userCommentsPagination.page"
                  :size="userCommentsPagination.size"
                  @change="handleCommentsPageChange"
                />
              </template>
            </div>
          </section>
          <!-- 安全设置 -->
          <section v-else-if="activeMenu === 'security'" class="content-section">
            <h2 class="section-title">安全设置</h2>
            <div class="security-content">
              <el-form
                :model="securityForm"
                :rules="securityRules"
                ref="securityFormRef"
                label-width="120px"
              >
                <el-form-item label="当前密码" prop="currentPassword">
                  <el-input
                    v-model="securityForm.currentPassword"
                    type="password"
                    placeholder="请输入当前密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="securityForm.newPassword"
                    type="password"
                    placeholder="请输入新密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input
                    v-model="securityForm.confirmPassword"
                    type="password"
                    placeholder="请再次输入新密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item>
                  <el-button
                    type="primary"
                    @click="handleChangePassword"
                    :loading="changingPassword"
                  >
                    修改密码
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </section>
        </main>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
// import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User,
  Document,
  Star,
  ChatDotRound,
  Lock,
  Thumb
} from '@element-plus/icons-vue'
import { formatDate, formatPrice } from '@/utils'
import { useAuthStore } from '@/stores/auth'
import { useOrderStore } from '@/stores/order'
import { useFavoriteStore } from '@/stores/favorite'
import { useCommentStore } from '@/stores/comment'
import MovieCard from '@/components/front/MovieCard.vue'
import Pagination from '@/components/common/Pagination.vue'

const router = useRouter()
const authStore = useAuthStore()
const orderStore = useOrderStore()
const favoriteStore = useFavoriteStore()
const commentStore = useCommentStore()

const activeMenu = ref('profile')

// 用户信息
const userInfo = ref({})
const profileForm = reactive({
  username: '',
  email: '',
  phone: '',
  avatar: ''
})
const profileFormRef = ref()
const updatingProfile = ref(false)

const profileRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 订单相关
const orders = ref([])
const ordersLoading = ref(false)
const orderFilter = reactive({
  status: ''
})
const ordersPagination = ref({
  page: 1,
  size: 10,
  total: 0
})

// 收藏相关
const favorites = ref([])
const favoritesLoading = ref(false)
const favoritesPagination = ref({
  page: 1,
  size: 12,
  total: 0
})

// 评论相关
const userComments = ref([])
const userCommentsLoading = ref(false)
const userCommentsPagination = ref({
  page: 1,
  size: 10,
  total: 0
})

// 安全设置
const securityForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const securityFormRef = ref()
const changingPassword = ref(false)

const securityRules = {
  currentPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== securityForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

onMounted(() => {
  loadUserInfo()
  loadInitialData()
})

const loadUserInfo = () => {
  userInfo.value = authStore.userInfo || {}
  Object.assign(profileForm, userInfo.value)
}

const loadInitialData = () => {
  // 根据当前菜单加载数据
  switch (activeMenu.value) {
    case 'orders':
      loadOrders()
      break
    case 'favorites':
      loadFavorites()
      break
    case 'comments':
      loadUserComments()
      break
  }
}

const handleMenuSelect = (index) => {
  activeMenu.value = index
  loadInitialData()
}

// 个人信息相关方法
const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return

  await profileFormRef.value.validate(async (valid) => {
    if (!valid) return

    updatingProfile.value = true
    try {
      await authStore.updateProfile(profileForm)
      ElMessage.success('个人信息更新成功')
      loadUserInfo()
    } catch (error) {
      console.error('更新个人信息失败:', error)
      ElMessage.error('更新失败')
    } finally {
      updatingProfile.value = false
    }
  })
}

const handleAvatarSuccess = (response) => {
  profileForm.avatar = response.url
  ElMessage.success('头像上传成功')
}

const beforeAvatarUpload = (file) => {
  const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPGOrPNG) {
    ElMessage.error('头像必须是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

// 订单相关方法
const loadOrders = async () => {
  ordersLoading.value = true
  try {
    const params = {
      page: ordersPagination.value.page - 1,
      size: ordersPagination.value.size
    }
    if (orderFilter.status) {
      params.status = orderFilter.status
    }

    const response = await orderStore.getUserOrders(params)
    orders.value = response.content || response
    ordersPagination.value.total = response.totalElements || response.length || 0
  } catch (error) {
    ElMessage.error('加载订单失败')
    console.error('加载订单失败:', error)
  } finally {
    ordersLoading.value = false
  }
}

const getOrderStatusType = (status) => {
  const typeMap = {
    PENDING: 'warning',
    PAID: 'success',
    CANCELLED: 'info',
    REFUNDED: 'danger'
  }
  return typeMap[status] || 'info'
}

const getOrderStatusText = (status) => {
  const textMap = {
    PENDING: '待支付',
    PAID: '已支付',
    CANCELLED: '已取消',
    REFUNDED: '已退款'
  }
  return textMap[status] || status
}

const handlePayOrder = (orderId) => {
  router.push(`/order/${orderId}/payment`)
}

const handleCancelOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗？', '提示', {
      type: 'warning'
    })

    await orderStore.cancelOrder(orderId)
    ElMessage.success('订单取消成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败')
    }
  }
}

const handleViewTicket = (orderId) => {
  router.push(`/order/${orderId}/ticket`)
}

const handleRefundOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要申请退款吗？', '提示', {
      type: 'warning'
    })

    await orderStore.refundOrder(orderId)
    ElMessage.success('退款申请已提交')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('申请退款失败')
    }
  }
}

const handleOrdersPageChange = ({ page, size }) => {
  ordersPagination.value.page = page
  ordersPagination.value.size = size
  loadOrders()
}

// 收藏相关方法
const loadFavorites = async () => {
  favoritesLoading.value = true
  try {
    const params = {
      page: favoritesPagination.value.page - 1,
      size: favoritesPagination.value.size
    }

    const response = await favoriteStore.getFavorites(params)
    favorites.value = response.content || response
    favoritesPagination.value.total = response.totalElements || response.length || 0
  } catch (error) {
    ElMessage.error('加载收藏失败')
    console.error('加载收藏失败:', error)
  } finally {
    favoritesLoading.value = false
  }
}

const handleBuyTicket = (movie) => {
  router.push(`/movie/${movie.id}/sessions`)
}

const handleUnfavorite = async (movie) => {
  try {
    await favoriteStore.removeFavorite(movie.id)
    ElMessage.success('已取消收藏')
    loadFavorites()
  } catch (error) {
    console.error('取消收藏失败:', error)
    ElMessage.error('取消收藏失败')
  }
}

const handleFavoritesPageChange = ({ page, size }) => {
  favoritesPagination.value.page = page
  favoritesPagination.value.size = size
  loadFavorites()
}

// 评论相关方法
const loadUserComments = async () => {
  userCommentsLoading.value = true
  try {
    const params = {
      page: userCommentsPagination.value.page - 1,
      size: userCommentsPagination.value.size
    }

    const response = await commentStore.getUserComments(params)
    userComments.value = response.content || response
    userCommentsPagination.value.total = response.totalElements || response.length || 0
  } catch (error) {
    ElMessage.error('加载评论失败')
    console.error('加载评论失败:', error)
  } finally {
    userCommentsLoading.value = false
  }
}

// const handleEditComment = (comment) => {

//   // 编辑评论逻辑
//   ElMessage.info('编辑评论功能开发中...')
// }

const handleDeleteComment = async (commentId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      type: 'warning'
    })

    await commentStore.deleteComment(commentId)
    ElMessage.success('评论删除成功')
    loadUserComments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除评论失败')
    }
  }
}

const handleCommentsPageChange = ({ page, size }) => {
  userCommentsPagination.value.page = page
  userCommentsPagination.value.size = size
  loadUserComments()
}

// 安全设置相关方法
const handleChangePassword = async () => {
  if (!securityFormRef.value) return

  await securityFormRef.value.validate(async (valid) => {
    if (!valid) return

    changingPassword.value = true
    try {
      await authStore.changePassword({
        currentPassword: securityForm.currentPassword,
        newPassword: securityForm.newPassword
      })
      ElMessage.success('密码修改成功')
      securityFormRef.value.resetFields()
    } catch (error) {
      console.error('密码修改失败:', error)
      ElMessage.error('密码修改失败')
    } finally {
      changingPassword.value = false
    }
  })
}
</script>
<style scoped lang="scss">
.user-center-page {
  padding: $spacing-lg 0 $spacing-xxl;
  min-height: 100vh;
  background: $bg-gray;
}

.user-center-content {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: $spacing-xl;

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.sidebar {
  background: $bg-white;
  border-radius: $border-radius-base;
  box-shadow: $shadow-base;
  overflow: hidden;
  height: fit-content;
  position: sticky;
  top: $spacing-xl;
}

.user-profile {
  padding: $spacing-xl;
  text-align: center;
  border-bottom: 1px solid $border-light;

  .user-avatar {
    margin-bottom: $spacing-md;
  }

  .user-info {
    .username {
      font-size: 18px;
      font-weight: 700;
      margin-bottom: $spacing-xs;
      color: $text-primary;
    }

    .user-email {
      color: $text-secondary;
      margin-bottom: $spacing-sm;
    }
  }
}

.side-menu {
  border: none;

  :deep(.el-menu-item) {
    height: 60px;
    line-height: 60px;

    &.is-active {
      background: $primary-color;
      color: white;

      .el-icon {
        color: white;
      }
    }
  }
}

.main-content {
  background: $bg-white;
  border-radius: $border-radius-base;
  box-shadow: $shadow-base;
  overflow: hidden;
}

.content-section {
  padding: $spacing-xl;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: $spacing-xl;
  color: $text-primary;
  border-bottom: 1px solid $border-light;
  padding-bottom: $spacing-md;
}

.profile-form {
  max-width: 600px;

  .avatar-upload {
    display: flex;
    align-items: center;
    gap: $spacing-lg;

    .avatar-preview {
      flex-shrink: 0;
    }
  }
}

.orders-content {
  .orders-filter {
    margin-bottom: $spacing-lg;
    padding-bottom: $spacing-lg;
    border-bottom: 1px solid $border-light;
  }
}

.order-items {
  .order-item {
    padding: $spacing-lg;
    border: 1px solid $border-light;
    border-radius: $border-radius-base;
    margin-bottom: $spacing-lg;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .order-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: $spacing-md;

    .order-info {
      .order-no {
        font-weight: 600;
        color: $text-primary;
        margin-right: $spacing-md;
      }

      .order-time {
        color: $text-secondary;
        font-size: $font-size-small;
      }
    }
  }

  .order-details {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: $spacing-md;

    @media (max-width: $breakpoint-sm) {
      flex-direction: column;
      align-items: stretch;
      gap: $spacing-md;
    }
  }

  .movie-info {
    display: flex;
    align-items: center;
    gap: $spacing-md;
    flex: 1;

    .movie-poster {
      width: 80px;
      height: 100px;
      border-radius: $border-radius-base;
      flex-shrink: 0;
    }

    .movie-details {
      .movie-title {
        font-size: 16px;
        font-weight: 600;
        margin-bottom: $spacing-sm;
        color: $text-primary;
      }

      .session-info {
        display: flex;
        gap: $spacing-md;
        margin-bottom: $spacing-xs;
        color: $text-secondary;
        font-size: $font-size-small;
      }

      .seats-info {
        color: $text-regular;
        font-size: $font-size-small;
      }
    }
  }

  .order-price {
    .price-label {
      color: $text-secondary;
      margin-right: $spacing-xs;
    }

    .price-value {
      font-size: 18px;
      font-weight: 700;
      color: $primary-color;
    }
  }

  .order-actions {
    display: flex;
    gap: $spacing-sm;
    justify-content: flex-end;
  }
}

.favorites-content {
  .favorites-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: $spacing-lg;
    margin-bottom: $spacing-xl;
  }
}

.comments-content {
  .comment-items {
    .comment-item {
      display: flex;
      gap: $spacing-lg;
      padding: $spacing-lg;
      border-bottom: 1px solid $border-light;

      &:last-child {
        border-bottom: none;
      }

      @media (max-width: $breakpoint-sm) {
        flex-direction: column;
        gap: $spacing-md;
      }
    }

    .comment-movie {
      display: flex;
      gap: $spacing-md;
      min-width: 200px;

      .movie-poster {
        width: 60px;
        height: 80px;
        border-radius: $border-radius-base;
        flex-shrink: 0;
      }

      .movie-info {
        .movie-title {
          font-size: 14px;
          font-weight: 600;
          margin-bottom: $spacing-xs;
          color: $text-primary;
        }
      }
    }

    .comment-content {
      flex: 1;

      .comment-text {
        line-height: 1.6;
        color: $text-regular;
        margin-bottom: $spacing-sm;
      }

      .comment-meta {
        display: flex;
        gap: $spacing-lg;
        color: $text-secondary;
        font-size: $font-size-small;

        .comment-likes {
          display: flex;
          align-items: center;
          gap: $spacing-xs;
        }
      }
    }

    .comment-actions {
      display: flex;
      gap: $spacing-sm;

      @media (max-width: $breakpoint-sm) {
        justify-content: flex-end;
      }
    }
  }
}

.security-content {
  max-width: 500px;
}

.loading-container {
  padding: $spacing-xl 0;
}

.empty-state {
  padding: $spacing-xxl 0;
  text-align: center;
}
</style>
