import { getToken } from '../lib/tokenStore'

/** Shape of the backend's ErrorResponse (GlobalExceptionHandler). */
export interface ApiErrorBody {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
  fieldErrors: Array<{ field: string; message: string }> | null
}

/**
 * Error thrown for any non-2xx response. Carries the HTTP status and, when the
 * backend returns validation failures, the per-field messages (US04 → 400).
 */
export class ApiError extends Error {
  readonly status: number
  readonly fieldErrors: Array<{ field: string; message: string }>

  constructor(status: number, message: string, fieldErrors: Array<{ field: string; message: string }> = []) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.fieldErrors = fieldErrors
  }
}

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? '/api'

/** Join the configured base with a path, tolerating a leading slash on either side. */
function url(path: string): string {
  const base = API_BASE.replace(/\/$/, '')
  const suffix = path.startsWith('/') ? path : `/${path}`
  return `${base}${suffix}`
}

/**
 * Central fetch wrapper: attaches the Bearer token when present, serializes JSON
 * bodies, and normalizes errors into {@link ApiError}. Returns `undefined` for
 * 204 No Content (e.g. DELETE).
 */
export async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers)
  const token = getToken()
  if (token) headers.set('Authorization', `Bearer ${token}`)
  if (init.body !== undefined && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }
  headers.set('Accept', 'application/json')

  const res = await fetch(url(path), { ...init, headers })

  if (!res.ok) {
    throw await toApiError(res)
  }

  if (res.status === 204) {
    return undefined as T
  }
  return (await res.json()) as T
}

async function toApiError(res: Response): Promise<ApiError> {
  try {
    const body = (await res.json()) as Partial<ApiErrorBody>
    return new ApiError(
      res.status,
      body.message ?? res.statusText,
      body.fieldErrors ?? [],
    )
  } catch {
    return new ApiError(res.status, res.statusText || 'Request failed')
  }
}
