import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { orderApi } from '@/api'

export const useOrderStore = defineStore('order', () => {
  // State
  const orders = ref([])
  const currentOrder = ref(null)
  const pagination = ref({
    page: 1,
    size: 10,
    total: 0,
    pages: 0
  })
  const loading = ref(false)

  // Getters
  const orderList = computed(() => orders.value)
  const orderDetail = computed(() => currentOrder.value)
  const isLoading = computed(() => loading.value)

  // Actions
  const fetchUserOrders = async (params = {}) => {
    loading.value = true
    try {
      const response = await orderApi.getUserOrders({
        page: pagination.value.page,
        size: pagination.value.size,
        ...params
      })

      if (response.content) {
        orders.value = response.content
        pagination.value = {
          page: response.number + 1,
          size: response.size,
          total: response.totalElements,
          pages: response.totalPages
        }
      } else {
        orders.value = response
      }
      return orders.value
    } catch (error) {
      console.error('获取用户订单失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchOrderDetail = async (id) => {
    loading.value = true
    try {
      const order = await orderApi.getOrderDetail(id)
      currentOrder.value = order
      return order
    } catch (error) {
      console.error('获取订单详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // const createOrder = async (orderData) => {
  //   loading.value = true
  //   try {
  //     const order = await orderApi.createOrder(orderData)
  //     return order
  //   } catch (error) {
  //     console.error('创建订单失败:', error)
  //     throw error
  //   } finally {
  //     loading.value = false
  //   }
  // }

  // const payOrder = async (id) => {
  //   try {
  //     await orderApi.payOrder(id)
  //     // 更新本地订单状态
  //     const order = orders.value.find(o => o.id === id)
  //     if (order) {
  //       order.status = 'PAID'
  //       order.payTime = new Date().toISOString()
  //     }
  //     if (currentOrder.value && currentOrder.value.id === id) {
  //       currentOrder.value.status = 'PAID'
  //       currentOrder.value.payTime = new Date().toISOString()
  //     }
  //   } catch (error) {
  //     console.error('支付订单失败:', error)
  //     throw error
  //   }
  // }

  // const cancelOrder = async (id) => {
  //   try {
  //     await orderApi.cancelOrder(id)
  //     // 更新本地订单状态
  //     const order = orders.value.find(o => o.id === id)
  //     if (order) {
  //       order.status = 'CANCELLED'
  //       order.cancelTime = new Date().toISOString()
  //     }
  //     if (currentOrder.value && currentOrder.value.id === id) {
  //       currentOrder.value.status = 'CANCELLED'
  //       currentOrder.value.cancelTime = new Date().toISOString()
  //     }
  //   } catch (error) {
  //     console.error('取消订单失败:', error)
  //     throw error
  //   }
  // }

  const refundOrder = async (id) => {
    try {
      await orderApi.refundOrder(id)
      // 更新本地状态
      const order = orders.value.find(o => o.id === id)
      if (order) {
        order.status = 'REFUNDED'
      }
      if (currentOrder.value?.id === id) {
        currentOrder.value.status = 'REFUNDED'
      }
    } catch (error) {
      console.error('申请退款失败:', error)
      throw error
    }
  }







  // 创建选座订单
  const createOrder = async (orderData) => {
    try {
      const response = await orderApi.createSeatOrder(orderData)
      currentOrder.value = response.data
      return response.data
    } catch (error) {
      const errorMessage = error.response?.data?.message || '创建订单失败'
      throw new Error(errorMessage)
    }
  }

  // 支付订单
  const payOrder = async (orderId) => {
    try {
      const response = await orderApi.payOrder(orderId)
      // 更新本地订单状态
      if (currentOrder.value && currentOrder.value.id === orderId) {
        currentOrder.value = response.data
      }
      return response.data
    } catch (error) {
      const errorMessage = error.response?.data?.message || '支付失败'
      throw new Error(errorMessage)
    }
  }

  // 取消订单
  const cancelOrder = async (orderId) => {
    try {
      const response = await orderApi.cancelOrder(orderId)
      // 更新本地订单状态
      if (currentOrder.value && currentOrder.value.id === orderId) {
        currentOrder.value = response.data
      }
      return response.data
    } catch (error) {
      const errorMessage = error.response?.data?.message || '取消订单失败'
      throw new Error(errorMessage)
    }
  }

  // 获取订单详情
  const getOrderDetail = async (orderId) => {
    try {
      const response = await orderApi.getOrderDetail(orderId)
      currentOrder.value = response.data
      return response
    } catch (error) {
      const errorMessage = error.response?.data?.message || '获取订单详情失败'
      throw new Error(errorMessage)
    }
  }

  // 获取用户订单列表
  const getUserOrders = async (params = {}) => {
    loading.value = true
    try {
      const response = await orderApi.getUserOrders(params)
      orders.value = response.data.content || response.data
      return response
    } catch (error) {
      const errorMessage = error.response?.data?.message || '获取订单列表失败'
      throw new Error(errorMessage)
    } finally {
      loading.value = false
    }
  }




  const clearCurrentOrder = () => {
    currentOrder.value = null
  }

  const updatePagination = (newPagination) => {
    pagination.value = { ...pagination.value, ...newPagination }
  }

  return {
    // State
    orders,
    currentOrder,
    pagination,
    loading,

    // Getters
    orderList,
    orderDetail,
    isLoading,

    // Actions
    fetchUserOrders,
    fetchOrderDetail,
    createOrder,
    payOrder,
    cancelOrder,
    refundOrder,
    clearCurrentOrder,
    updatePagination,
    getOrderDetail,
    getUserOrders,
    
  }
})
