import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  define: {
    'process.env.VITE_BACKEND_URL': JSON.stringify(process.env.BACKEND_URL || 'http://localhost:8080')
  }
})