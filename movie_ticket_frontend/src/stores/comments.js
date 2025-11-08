import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { commentApi } from '@/api/index'

export const useCommentStore = defineStore('comment', () => {
  const comments = ref([])
  const hotComments = ref([])
  const latestComments = ref([])
  const userComments = ref([])
  const commentStats = ref(null)
  const loading = ref(false)
  const submitting = ref(false)

  // 分页信息
  const pagination = ref({
    page: 1,
    size: 10,
    total: 0,
    totalPages: 0
  })

  // 筛选条件
  const filters = ref({
    movieId: null,
    sortBy: 'createTime',
    sortOrder: 'desc'
  })

  // 计算属性
  const hasMore = computed(() => {
    return pagination.value.page < pagination.value.totalPages
  })

  const isEmpty = computed(() => {
    return comments.value.length === 0 && !loading.value
  })

  // 获取评论列表
  const getComments = async (params = {}) => {
    loading.value = true
    try {
      const queryParams = {
        ...filters.value,
        ...params,
        page: pagination.value.page - 1,
        size: pagination.value.size
      }

      const response = await commentApi.getComments(queryParams)
      comments.value = response.data.content || response.data
      pagination.value = {
        page: response.data.number + 1 || 1,
        size: response.data.size || pagination.value.size,
        total: response.data.totalElements || response.data.length || 0,
        totalPages: response.data.totalPages || 1
      }

      return response
    } catch (error) {
      console.error('获取评论列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取电影评论
  const getMovieComments = async (movieId, params = {}) => {
    loading.value = true
    try {
      const response = await commentApi.getMovieComments(movieId, {
        ...params,
        page: pagination.value.page - 1,
        size: pagination.value.size
      })
      comments.value = response.data.content || response.data
      pagination.value = {
        page: response.data.number + 1 || 1,
        size: response.data.size || pagination.value.size,
        total: response.data.totalElements || response.data.length || 0,
        totalPages: response.data.totalPages || 1
      }
      return response
    } catch (error) {
      console.error('获取电影评论失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取用户评论
  const getUserComments = async (params = {}) => {
    loading.value = true
    try {
      const response = await commentApi.getUserComments({
        ...params,
        page: params.page !== undefined ? params.page : pagination.value.page - 1,
        size: params.size !== undefined ? params.size : pagination.value.size
      })
      userComments.value = response.data.content || response.data
      // 更新分页信息
      if (response.data.totalElements !== undefined) {
        pagination.value = {
          page: response.data.number + 1 || 1,
          size: response.data.size || pagination.value.size,
          total: response.data.totalElements || 0,
          totalPages: response.data.totalPages || 1
        }
      }
      return response
    } catch (error) {
      console.error('获取用户评论失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 创建评论
  const createComment = async (commentData) => {
    submitting.value = true
    try {
      const response = await commentApi.createComment(commentData)
      // 添加到评论列表
      comments.value.unshift(response.data)
      // 更新统计信息
      if (commentData.movieId) {
        await getCommentStats(commentData.movieId)
      }
      return response
    } catch (error) {
      console.error('创建评论失败:', error)
      throw error
    } finally {
      submitting.value = false
    }
  }

  // 获取评论统计
  const getCommentStats = async (movieId) => {
    try {
      const response = await commentApi.getCommentStats(movieId)
      commentStats.value = response.data
      return response
    } catch (error) {
      console.error('获取评论统计失败:', error)
      throw error
    }
  }

  // 获取热门评论
  const getHotComments = async (movieId, limit = 5) => {
    try {
      const response = await commentApi.getHotComments(movieId, limit)
      hotComments.value = response.data
      return response
    } catch (error) {
      console.error('获取热门评论失败:', error)
      throw error
    }
  }

  // 获取最新评论
  const getLatestComments = async (movieId, limit = 5) => {
    try {
      const response = await commentApi.getLatestComments(movieId, limit)
      latestComments.value = response.data
      return response
    } catch (error) {
      console.error('获取最新评论失败:', error)
      throw error
    }
  }

  // 点赞评论
  const likeComment = async (commentId) => {
    try {
      const response = await commentApi.likeComment(commentId)
      // 更新本地评论数据
      updateCommentInList(response.data)
      return response
    } catch (error) {
      console.error('点赞评论失败:', error)
      throw error
    }
  }

  // 取消点赞评论
  const unlikeComment = async (commentId) => {
    try {
      const response = await commentApi.unlikeComment(commentId)
      // 更新本地评论数据
      updateCommentInList(response.data)
      return response
    } catch (error) {
      console.error('取消点赞评论失败:', error)
      throw error
    }
  }

  // 删除评论
  const deleteComment = async (commentId) => {
    try {
      await commentApi.deleteComment(commentId)
      // 从评论列表中移除
      comments.value = comments.value.filter(comment => comment.id !== commentId)
      userComments.value = userComments.value.filter(comment => comment.id !== commentId)
    } catch (error) {
      console.error('删除评论失败:', error)
      throw error
    }
  }

  // 更新评论列表中的评论
  const updateCommentInList = (updatedComment) => {
    const index = comments.value.findIndex(comment => comment.id === updatedComment.id)
    if (index !== -1) {
      comments.value[index] = updatedComment
    }

    const userIndex = userComments.value.findIndex(comment => comment.id === updatedComment.id)
    if (userIndex !== -1) {
      userComments.value[userIndex] = updatedComment
    }
  }

  // 重置筛选条件
  const resetFilters = () => {
    filters.value = {
      movieId: null,
      sortBy: 'createTime',
      sortOrder: 'desc'
    }
    pagination.value.page = 1
  }

  // 更新筛选条件
  const updateFilters = (newFilters) => {
    filters.value = { ...filters.value, ...newFilters }
    pagination.value.page = 1
  }

  // 加载更多
  const loadMore = async () => {
    if (!hasMore.value || loading.value) return

    pagination.value.page += 1
    try {
      const queryParams = {
        ...filters.value,
        page: pagination.value.page - 1,
        size: pagination.value.size
      }

      const response = await commentApi.getComments(queryParams)
      const newComments = response.data.content || response.data
      comments.value = [...comments.value, ...newComments]

      pagination.value.totalPages = response.data.totalPages || 1
    } catch (error) {
      console.error('加载更多评论失败:', error)
      pagination.value.page -= 1 // 回滚页码
      throw error
    }
  }

  return {
    // 状态
    comments,
    hotComments,
    latestComments,
    userComments,
    commentStats,
    loading,
    submitting,
    pagination,
    filters,

    // 计算属性
    hasMore,
    isEmpty,

    // 方法
    getComments,
    getMovieComments,
    getUserComments,
    createComment,
    getCommentStats,
    getHotComments,
    getLatestComments,
    likeComment,
    unlikeComment,
    deleteComment,
    resetFilters,
    updateFilters,
    loadMore
  }
})
