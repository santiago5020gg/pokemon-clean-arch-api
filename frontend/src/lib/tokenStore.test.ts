import { describe, it, expect, beforeEach } from 'vitest'
import { getToken, setToken, clearToken } from './tokenStore'

describe('tokenStore', () => {
  beforeEach(() => localStorage.clear())

  it('returns null when no token is stored', () => {
    expect(getToken()).toBeNull()
  })

  it('persists and reads back a token', () => {
    setToken('jwt-abc')
    expect(getToken()).toBe('jwt-abc')
  })

  it('clears a stored token', () => {
    setToken('jwt-abc')
    clearToken()
    expect(getToken()).toBeNull()
  })
})
