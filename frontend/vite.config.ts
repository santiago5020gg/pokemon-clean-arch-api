import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Dev proxy: the frontend talks to same-origin `/api`, which Vite forwards to the
// Spring Boot backend. This sidesteps CORS in development (the backend does not
// enable it) and keeps the API base URL configurable for Docker via env.
const API_TARGET = process.env.VITE_API_PROXY_TARGET ?? 'http://localhost:8080'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: API_TARGET,
        changeOrigin: true,
      },
    },
  },
})
