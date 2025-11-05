import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import locale from 'element-plus/es/locale/lang/zh-cn'

import App from './App.vue'
import router from './router'
import { permissionDirective, roleDirective, authDirective } from './directives/permission'
import '@/assets/styles/index.scss'

const app = createApp(App)
const pinia = createPinia()

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 注册全局指令
app.directive('permission', permissionDirective)
app.directive('role', roleDirective)
app.directive('auth', authDirective)

app.use(pinia)
app.use(router)
app.use(ElementPlus, { locale })

app.mount('#app')