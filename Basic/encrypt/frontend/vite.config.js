import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/session': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/keys': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/query': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        bypass: (req, res, options) => {
          // 对于/query路径的GET请求，不代理到后端，让前端路由处理
          if (req.method === 'GET') {
            return req.url;
          }
        }
      },
      '/audit-logs': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})