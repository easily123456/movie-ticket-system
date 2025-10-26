import { createApp } from 'vue' // 从 vue 模块中导入 createApp 函数，用于创建 Vue 应用实例
import App from './App.vue' // 导入根组件 App.vue
import router from './router' // 导入路由配置
import { createPinia } from 'pinia' // 从 pinia 模块中导入 createPinia 函数，用于创建状态管理实例

// Element Plus - 引入 Element Plus UI 组件库的相关模块
import ElementPlus from 'element-plus' // 导入 Element Plus 组件库
import 'element-plus/dist/index.css' // 导入 Element Plus 的样式文件
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
// 将 @element-plus/icons-vue 包中导出的所有内容作为一个命名空间对象导入到 ElementPlusIconsVue 变量中
import zhCn from 'element-plus/es/locale/lang/zh-cn' // 导入 Element Plus 中文语言包

// 全局样式 - 导入全局样式文件
import '@/assets/styles/index.scss' // 导入自定义的全局样式

const app = createApp(App) // 创建 Vue 应用实例，以 App.vue 为根组件
const pinia = createPinia() // 创建 Pinia 状态管理实例

// 注册所有Element Plus图标 - 遍历并注册所有 Element Plus 图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component) // 将每个图标组件注册到应用中
}

app.use(pinia) // 在应用中使用 Pinia 状态管理
app.use(router) // 在应用中使用路由
app.use(ElementPlus, { // 在应用中使用 Element Plus 组件库
  locale: zhCn, // 设置语言为中文
  size: 'default' // 设置组件默认尺寸
})

app.mount('#app') // 将 Vue 应用挂载到 id 为 'app' 的 DOM 元素上
