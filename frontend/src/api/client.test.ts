import { describe, it, expect, beforeEach } from 'vitest'
import { http, HttpResponse } from 'msw'
import { server } from '../test/server'
import { request, ApiError } from './client'
import { setToken, clearToken } from '../lib/tokenStore'

describe('api client', () => {
  beforeEach(() => clearToken())

  it('parses a JSON response body', async () => {
    server.use(http.get('/api/ping', () => HttpResponse.json({ ok: true })))
    const data = await request<{ ok: boolean }>('/ping')
    expect(data).toEqual({ ok: true })
  })

  it('attaches the Bearer token when one is stored', async () => {
    setToken('secret-jwt')
    let seen: string | null = null
    server.use(
      http.get('/api/whoami', ({ request: req }) => {
        seen = req.headers.get('Authorization')
        return HttpResponse.json({})
      }),
    )
    await request('/whoami')
    expect(seen).toBe('Bearer secret-jwt')
  })

  it('omits the Authorization header when no token is stored', async () => {
    let seen: string | null = 'unset'
    server.use(
      http.get('/api/whoami', ({ request: req }) => {
        seen = req.headers.get('Authorization')
        return HttpResponse.json({})
      }),
    )
    await request('/whoami')
    expect(seen).toBeNull()
  })

  it('returns undefined for 204 No Content', async () => {
    server.use(http.delete('/api/thing/1', () => new HttpResponse(null, { status: 204 })))
    const result = await request<void>('/thing/1', { method: 'DELETE' })
    expect(result).toBeUndefined()
  })

  it('throws ApiError with status and field errors on 400', async () => {
    server.use(
      http.post('/api/thing', () =>
        HttpResponse.json(
          {
            status: 400,
            message: 'Validation failed',
            fieldErrors: [{ field: 'name', message: 'must not be blank' }],
          },
          { status: 400 },
        ),
      ),
    )
    const error = (await request('/thing', { method: 'POST', body: '{}' }).catch(
      (e) => e,
    )) as ApiError
    expect(error).toBeInstanceOf(ApiError)
    expect(error.status).toBe(400)
    expect(error.fieldErrors).toEqual([{ field: 'name', message: 'must not be blank' }])
  })
})
