import { defineConfig } from 'vite' // 从 vite 包中导入 defineConfig 函数，用于定义 Vite 配置
import vue from '@vitejs/plugin-vue' // 导入 Vue 插件，用于支持 Vue 单文件组件
import { resolve, dirname } from 'path' // 从 path 模块导入 resolve 和 dirname 函数，用于路径处理
import { fileURLToPath } from 'url' // 从 url 模块导入 fileURLToPath 函数，用于将 URL 路径转换为文件路径
import vueDevTools from 'vite-plugin-vue-devtools' // 导入 Vue DevTools 插件，用于开发调试

const __filename = fileURLToPath(import.meta.url) // 获取当前模块的文件路径
const __dirname = dirname(__filename) // 获取当前模块所在的目录路径

export default defineConfig({ // 导出默认配置对象
  plugins: [ // 配置插件数组
    vue(), // 启用 Vue 插件
    vueDevTools() // 启用 Vue DevTools 插件
  ],
  resolve: { // 配置模块解析选项
    alias: { // 设置路径别名
      '@': resolve(__dirname, 'src') // 将 @ 符号映射到 src 目录的绝对路径
    }
  },
  server: { // 开发服务器配置
    port: 3000, // 设置开发服务器端口为 3000
    proxy: { // 配置代理
      '/api': { // 当请求以 /api 开头时启用代理
        target: 'http://localhost:8080', // 代理目标地址
        changeOrigin: true, // 改变请求头中的 origin 字段
        rewrite: (path) => path.replace(/^\/api/, '/api') // 重写请求路径
      }
    }
  },
  css: { // CSS 相关配置
    preprocessorOptions: { // 预处理器选项
      scss: { // SCSS 预处理器配置
        additionalData: `@use "@/assets/styles/variables.scss" as *;` // 为每个 SCSS 文件注入变量文件
      }
    }
  },
  build: { // 生产环境打包设置
    outDir: 'dist', // 指定打包后文件的输出目录，运行构建命令后所有文件会放在 dist 文件夹中
    assetsDir: 'assets', // 指定静态资源（图片、CSS、JS等）在输出目录中的子目录名称
    sourcemap: false, // 生产环境通常关闭源码映射以减小文件体积并防止源码泄露
    rollupOptions: { // 自定义底层 Rollup 打包工具的配置选项
      output: { // 配置打包后输出文件的具体设置
        chunkFileNames: 'js/[name]-[hash].js', // 动态导入的 chunk 文件命名规则，用于浏览器缓存优化
        entryFileNames: 'js/[name]-[hash].js', // 入口文件命名规则，便于识别和缓存管理
        assetFileNames: '[ext]/[name]-[hash].[ext]' // 其他静态资源文件命名规则，按文件类型分类存储
      }
    }
  }
})
