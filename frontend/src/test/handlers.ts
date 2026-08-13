import { http, HttpResponse } from 'msw'
import {
  authResponse,
  adminUser,
  bulbasaurDetail,
  listPage,
  syncResult,
} from './fixtures'

/**
 * Default happy-path handlers for the backend API. Individual tests override
 * these with `server.use(...)` to exercise error and edge cases.
 */
export const handlers = [
  http.get('/api/pokemon', () => HttpResponse.json(listPage)),

  http.get('/api/pokemon/:id', ({ params }) => {
    if (params.id === '999') {
      return HttpResponse.json(
        { status: 404, error: 'Not Found', message: 'Pokemon 999 not found', path: '/api/pokemon/999', fieldErrors: null },
        { status: 404 },
      )
    }
    return HttpResponse.json(bulbasaurDetail)
  }),

  http.post('/api/pokemon/sync', () => HttpResponse.json(syncResult, { status: 201 })),

  http.put('/api/pokemon/:id', async ({ request }) => {
    const body = (await request.json()) as { localizedName?: string }
    if (!body.localizedName) {
      return HttpResponse.json(
        {
          status: 400,
          error: 'Bad Request',
          message: 'Validation failed',
          path: '/api/pokemon/1',
          fieldErrors: [{ field: 'localizedName', message: 'must not be blank' }],
        },
        { status: 400 },
      )
    }
    return HttpResponse.json({ ...bulbasaurDetail, ...body })
  }),

  http.delete('/api/pokemon/:id', () => new HttpResponse(null, { status: 204 })),

  http.post('/api/auth/register', () => HttpResponse.json(adminUser, { status: 201 })),

  http.post('/api/auth/login', () => HttpResponse.json(authResponse)),
]
