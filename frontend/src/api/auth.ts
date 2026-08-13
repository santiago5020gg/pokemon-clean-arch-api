import { request } from './client'
import type { AuthResponse, LoginRequest, RegisterRequest, User } from './types'

export function register(body: RegisterRequest): Promise<User> {
  return request<User>('/auth/register', {
    method: 'POST',
    body: JSON.stringify(body),
  })
}

export function login(body: LoginRequest): Promise<AuthResponse> {
  return request<AuthResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(body),
  })
}
