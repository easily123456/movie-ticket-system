<template>
  <div class="pagination-container">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="pageSizes"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
    <!--
    使用 Element Plus 的分页组件，并绑定当前页和每页大小
    : 是 v-model 指令的参数语法，current-page指定了要绑定的具体属性名 currentPage
    :page-sizes="pageSizes"这里前面只有个：是(v-bind的简写)，表示将pageSizes属性绑定到组件的page-sizes属性上，单向数据绑定
    layout 属性定义了分页组件的布局，包括显示总数、每页大小选择器、上一页按钮、页码、下一页按钮和跳转输入框
    当选择不同的每页显示条数（如从10条改为20条）时，触发@size-change="handleSizeChange" ，调用 handleSizeChange 方法
    当切换页码时，触发@current-change="handleCurrentChange"，调用 handleCurrentChange 方法
     -->
  </div>
</template>
<script setup>
import { ref, watch } from 'vue'
//watch用于监听父组件的属性的变化，同步到子组件的响应式变量
//当子组件的响应式变量变化时，通过emit触发事件，通知父组件更新对应的属性值

defineOptions({
  name: 'MoviePagination'
})

const props = defineProps({
  total: {           // 定义 total 属性，用于接收数据总条数
    type: Number,    // 指定属性类型为 Number
    default: 0       // 设置默认值为 0
  },
  page: {            // 定义 page 属性，用于接收当前页码
    type: Number,    // 指定属性类型为 Number
    default: 1       // 设置默认值为 1（第一页）
  },
  size: {            // 定义 size 属性，用于接收每页显示的数据条数
    type: Number,    // 指定属性类型为 Number
    default: 10      // 设置默认值为 10（每页显示10条数据）
  },
  pageSizes: {       // 定义 pageSizes 属性，用于接收可选的每页条数数组
    type: Array,     // 指定属性类型为 Array
    default: () => [10, 20, 50, 100]  // 设置默认值为包含常见分页大小的数组
  }
})

const emit = defineEmits(['update:page', 'update:size', 'change'])
// 定义事件，用于向父组件传递数据，子组件为Pagination.vue，父组件为使用该分页组件的组件

const currentPage = ref(props.page) // 创建一个响应式变量 currentPage，初始值为父组件传入的 page 属性值，用于存储当前页码
const pageSize = ref(props.size)

watch(() => props.page, (newVal) => {
  currentPage.value = newVal
})
//()=>props.page：监听源
//(newVal) => { currentPage.value = newVal }：监听回调，当props.page变化时，更新currentPage的值
// props.page 是父组件传递给子组件的数据属性
// currentPage 是子组件内部的响应式变量

watch(() => props.size, (newVal) => {
  pageSize.value = newVal
})

const handleSizeChange = (size) => {           // 定义处理每页大小改变的函数，接收新的每页大小size参数
  pageSize.value = size                        // 更新pageSize响应式变量的值为新的每页大小
  emit('update:size', size)                    // 触发update:size事件，向父组件传递新的每页大小值
  emit('change', { page: currentPage.value, size })  // 触发change事件，向父组件传递当前页码和新的每页大小对象
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  emit('update:page', page)
  emit('change', { page, size: pageSize.value })
}
</script>
<style scoped lang="scss">
.pagination-container {
  display: flex; /* 使用弹性布局使分页组件居中显示 */
  justify-content: center; /* 水平居中对齐分页组件 */
  padding: $spacing-lg 0; /* 设置上下内边距，$spacing-lg是预定义的间距变量，用于创建垂直间距 */

  :deep(.el-pagination) { /* 深度选择器，用于穿透scoped样式限制，修改Element Plus分页组件内部样式 */
    .el-pagination__total {
      margin-right: $spacing-md; /* 为总数信息添加右边距，$spacing-md是中等间距变量 */
    }

    .el-pagination__sizes {
      margin: 0 $spacing-md; /* 为每页大小选择器设置左右边距，保持与其他元素的间距 */
    }

    .btn-prev,
    .btn-next {
      padding: 0 $spacing-sm; /* 为上一页和下一页按钮设置水平内边距，$spacing-sm是小间距变量 */
    }

    .el-pager {
      li {
        min-width: 32px; /* 设置页码数字的最小宽度，确保数字显示一致性 */
        height: 32px; /* 设置页码数字的高度 */
        line-height: 32px; /* 设置行高与高度一致，使文字垂直居中 */
        margin: 0 2px; /* 为每个页码数字设置左右外边距，创建页码间的间隔 */

        &.active {
          color: $primary-color; /* 为当前激活的页码设置主题色，$primary-color是预定义的主题色变量 */
        }
      }
    }

    .el-pagination__jump {
      margin-left: $spacing-md; /* 为跳转到指定页的输入框添加左边距，与其他元素保持间距 */
    }
  }
}

@media (max-width: $breakpoint-sm) {  /* 媒体查询，当屏幕宽度小于等于$breakpoint-sm变量值时生效 */
  .pagination-container {             /* 选择pagination-container类 */
    :deep(.el-pagination) {           /* 深度选择器，穿透scoped限制选择el-pagination组件 */
      .el-pagination__sizes,          /* 选择el-pagination下的每页大小选择器 */
      .el-pagination__jump {          /* 选择el-pagination下的跳转输入框 */
        display: none;                /* 隐藏这两个元素，不在小屏幕上显示 */
      }
    }
  }
}
//在小屏幕设备上隐藏分页组件的每页大小选择器和跳转输入框，自动触发
</style>
