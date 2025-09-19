import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { fileURLToPath } from 'node:url'
import { dirname, resolve } from 'node:path'

const __filename = fileURLToPath(import.meta.url)
const __dirname = dirname(__filename)

export default defineConfig({
    plugins: [react()],
    build: {
        outDir: resolve(__dirname, '../src/main/resources/static/app'),
        emptyOutDir: true,
        assetsDir: 'assets'
    },
    server: {
        port: 5173,
        proxy: {
            '/api':   { target: 'http://localhost:8080', changeOrigin: true },
            '/oauth2':{ target: 'http://localhost:8080', changeOrigin: true }
        }
    }
})
