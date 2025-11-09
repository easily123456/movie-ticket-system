// 格式化日期（简洁的本地化实现）
export const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 格式化时间
export const formatTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 格式化日期时间
export const formatDateTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 生成订单号
export const generateOrderNo = () => {
  const timestamp = new Date().getTime()
  const random = Math.floor(Math.random() * 1000)
  return `ORD${timestamp}${random.toString().padStart(3, '0')}`
}

export const isToday = (date) => {
  const today = new Date()//默认构造函数会创建一个表示当前日期和时间的Date对象
  const target = new Date(date)
  return today.toDateString() === target.toDateString()
}

export const isAfterToday = (date) => { //判断传入的日期是否在今天及之后
  const today = new Date()
  today.setHours(0, 0, 0, 0)//时分秒 毫秒
  const target = new Date(date)
  return target > today
}

// 价格格式化
export const formatPrice = (price) => {
  if (!price && price !== 0) return '¥0.00'
  return `¥${parseFloat(price).toFixed(2)}`
}

export const calculateTotalPrice = (price, count) => {
  return formatPrice(price * count)
}

// 字符串处理
export const truncateText = (text, maxLength = 50) => { //当文本长度超过指定最大长度时，返回截断后的文本并添加省略号
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

export const capitalizeFirst = (str) => {  //将字符串首字母大写并返回
  if (!str) return ''
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase()//slice(1) 截取字符串的第二个字符开始到末尾的部分
}

// 验证工具
export const isValidEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  // /^ - 开始锚点，表示字符串开头
  // [^\s@]+ - 匹配一个或多个非空白字符且非@符号的字符（邮箱用户名部分），[^...]表示取反字符集，\s表示空白字符，+表示匹配前面的元素一次或多次
  // @ - 匹配@符号（邮箱必需的分隔符）
  // [^\s@]+ - 再次匹配一个或多个非空白字符且非@符号的字符（域名部分）
  // . - 匹配点号.（转义后的点号）
  // [^\s@]+ - 最后匹配一个或多个非空白字符且非@符号的字符（顶级域名部分）
  // $/ - 结束锚点，表示字符串结尾
  return emailRegex.test(email) //test() 方法用于检测一个字符串是否匹配某个模式，返回布尔值
}

export const isValidPhone = (phone) => {
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(phone)
}

export const isValidPassword = (password) => {
  return password && password.length >= 6
}

// 本地存储工具
export const storage = {
  get(key) {
    try {
      const item = localStorage.getItem(key)//从本地存储中获取指定key的值
      return item ? JSON.parse(item) : null //如果item存在，则将其解析为JavaScript对象并返回；否则返回null
    } catch { //主要捕获JSON.parse可能抛出的异常和localStorage访问异常
      return localStorage.getItem(key)
    }
  },

  set(key, value) {
    try {
      const serialized = typeof value === 'string' ? value : JSON.stringify(value)//JSON.stringify(value)会将JavaScript对象转换为JSON字符串
      localStorage.setItem(key, serialized)
    } catch (error) {//捕获localStorage访问异常或JSON.stringify可能抛出的异常，这里的声明捕获对象，因为需要输出它
      console.error('存储数据失败:', error)
    }
  },

  remove(key) {
    localStorage.removeItem(key)
  },

  clear() {
    localStorage.clear()
  }
}

// 防抖函数是一种限制函数执行频率的技术，它确保函数在一段时间内只执行一次。
// 当函数被频繁调用时，防抖会取消之前的调用，只执行最后一次调用
export const debounce = (func, wait) => {  // 导出防抖函数，接收要防抖的函数和等待时间
  let timeout  // 声明一个变量用于存储定时器ID
  return function executedFunction(...args) {  // 返回一个包装函数，使用剩余参数收集所有传入参数
    const later = () => {  // 定义later函数用于执行原函数
      clearTimeout(timeout)  // 清除之前的定时器
      func(...args)  // 执行原始函数并传入所有参数
    }
    clearTimeout(timeout)  // 在设置新定时器前先清除已存在的定时器
    timeout = setTimeout(later, wait)  // 设置新的定时器，在wait毫秒后执行later函数
  }
}
// 工作流程：
// 当函数被调用时，设置一个定时器
// 如果在定时器到期前再次调用函数，会清除之前的定时器并重新设置
// 只有当定时器完整地等待到指定时间后，函数才会真正执行

// 节流函数确保函数在指定的时间间隔内最多只执行一次
export const throttle = (func, limit) => {
  let inThrottle
  return function(...args) {
    if (!inThrottle) {
      func.apply(this, args)//this 指向的是节流包装函数执行时的上下文，apply调用函数，同时设置函数内部的 this 值和参数
      inThrottle = true
      setTimeout(() => inThrottle = false, limit)
    }
  }
}
// 工作流程：
// 当函数被调用时，检查是否处于节流状态
// 如果不在节流状态，立即执行函数并设置节流状态
// 在指定的时间限制内，后续的调用都会被忽略
// 时间限制过后，解除节流状态，允许下一次执行

// 数组工具是一个数组分块工具，主要用途是将一个大数组分割成多个固定大小的小数组，将输入的 array 按照指定的 size 大小进行分割
export const chunkArray = (array, size) => {
  const chunks = []
  for (let i = 0; i < array.length; i += size) {
    chunks.push(array.slice(i, i + size))
  }
  return chunks
}

// 数组去重工具，接收一个数组和一个可选的键名参数，如果提供了键名key，则根据该键名对对象数组进行去重；否则对基本类型数组进行去重
export const uniqueArray = (array, key = null) => {
  if (key) {
    const seen = new Set() // 创建一个空的Set用于存储已见过的键值
    return array.filter(item => { //返回一个新数组，包含通过过滤条件的元素
      const value = item[key]
      if (seen.has(value)) {
        return false
      }
      seen.add(value)
      return true
    })
  }
  return [...new Set(array)]
}

// 对象工具深度克隆对象，创建一个完全独立的副本
export const deepClone = (obj) => {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj.getTime()) //instanceof 用于检测 obj 是否是 Date 的实例，如果是则创建一个新的 Date 对象并返回
  if (obj instanceof Array) return obj.map(item => deepClone(item))
  //obj.map() 用于对数组中的每个元素执行指定的操作并返回一个新数组，这里对每个元素递归调用 deepClone 实现深度克隆

  const cloned = {}
  for (const key in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, key)) {//Object.prototype.hasOwnProperty.call()检查对象是否具有指定自有属性
      cloned[key] = deepClone(obj[key])
    }
  }
  return cloned
}

// 从对象中排除指定键名的属性，返回一个新对象
export const omit = (obj, keys) => {
  const result = { ...obj } //这里使用了对象展开运算符（...）来创建 obj 的浅拷贝，确保原始对象不被修改
  keys.forEach(key => delete result[key])//遍历 keys 数组，使用 delete 运算符从 result 对象中删除对应的属性
  return result
}

// 从对象中提取指定键名的属性，返回一个新对象
export const pick = (obj, keys) => {
  const result = {}
  keys.forEach(key => {
    if (key in obj) {
      result[key] = obj[key]
    }
  })
  return result
}

// 座位相关工具
export const generateSeatLayout = (rows, cols, disabledSeats = []) => { //disabledSeats参数是一个包含不可用座位编号的数组，默认值为空数组
  const layout = []
  const rowLabels = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('')//split('')将字符串拆分为单个字符的数组

  for (let i = 0; i < rows; i++) {
    const row = []
    for (let j = 0; j < cols; j++) {
      const seatNumber = `${rowLabels[i]}${j + 1}`//当i=0, j=0时，生成座位号"A1"
      const isDisabled = disabledSeats.includes(seatNumber)//includes()方法用于检查数组中是否包含指定的元素，返回布尔值
      row.push({
        number: seatNumber,
        row: rowLabels[i],
        col: j + 1,
        available: !isDisabled,
        selected: false
      })
      //每个座位对象包含座位编号、行号、列号、是否可用和是否被选中等属性
    }
    layout.push(row)
  }

  return layout
}

export const parseSeatNumbers = (seatNumbersStr) => {
  try {
    if (typeof seatNumbersStr === 'string') {
      return JSON.parse(seatNumbersStr)
    }
    return seatNumbersStr || []
  } catch {
    return []
  }
}

import { colors } from './theme'

// 电影评分颜色工具，根据评分值返回对应的颜色代码
export const getRatingColor = (rating) => {
  if (rating >= 8) return colors.success // 优秀 - 绿色
  if (rating >= 6) return colors.warning // 良好 - 黄色
  if (rating >= 4) return colors.danger // 一般 - 红色
  return colors.info // 较差 - 灰色
}

// 时间工具，分钟时长转换成时分的形式或分钟的形式
export const formatDuration = (minutes) => {
  if (!minutes) return '' // 如果没有分钟数，则返回空字符串
  const hours = Math.floor(minutes / 60) // 计算小时数，Math.floor用于向下取整
  const mins = minutes % 60 // 计算剩余分钟数
  return hours > 0 ? `${hours}小时${mins}分钟` : `${mins}分钟` // 根据小时数是否大于0，返回不同格式的字符串
}

export default {
  formatDate,
  formatTime,
  formatDateTime,
  isToday,
  isAfterToday,
  formatPrice,
  calculateTotalPrice,
  truncateText,
  capitalizeFirst,
  isValidEmail,
  isValidPhone,
  isValidPassword,
  storage,
  debounce,
  throttle,
  chunkArray,
  uniqueArray,
  deepClone,
  omit,
  pick,
  generateSeatLayout,
  parseSeatNumbers,
  getRatingColor,
  formatDuration
  ,generateOrderNo
}
