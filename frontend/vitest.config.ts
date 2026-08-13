import { mergeConfig, defineConfig } from 'vitest/config'
import viteConfig from './vite.config.ts'

// Test runner config, kept separate from the app config per the project layout.
// Extends vite.config.ts so path/plugin behavior matches the real build.
export default mergeConfig(
  viteConfig,
  defineConfig({
    test: {
      environment: 'jsdom',
      globals: true,
      setupFiles: ['./src/test/setup.ts'],
      css: false,
      include: ['src/**/*.{test,spec}.{ts,tsx}'],
    },
  }),
)
