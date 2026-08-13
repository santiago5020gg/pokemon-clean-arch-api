import { setupServer } from 'msw/node'
import { handlers } from './handlers'

/** Shared MSW server used across the test suite (started in setup.ts). */
export const server = setupServer(...handlers)
