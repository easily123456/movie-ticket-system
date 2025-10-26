import { createPinia } from 'pinia'  // 从pinia库中导入createPinia函数，用于创建Pinia状态管理实例

const pinia = createPinia()          // 调用createPinia函数创建一个Pinia实例

export default pinia                 // 将创建的pinia实例作为默认导出，供其他文件使用

export * from './auth'              // 导出auth.js文件中的所有内容（通常是认证相关的store）
export * from './app'               // 导出app.js文件中的所有内容（通常是应用级别的store）
