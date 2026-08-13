import { createContext, useCallback, useContext, useMemo, useState, type ReactNode } from 'react'
import * as authApi from '../api/auth'
import { getToken, setToken, clearToken } from '../lib/tokenStore'
import type { LoginRequest, RegisterRequest } from '../api/types'

interface AuthContextValue {
  isAuthenticated: boolean
  login: (credentials: LoginRequest) => Promise<void>
  /** Registers, then logs in with the same credentials (auto-login UX). */
  register: (data: RegisterRequest) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setTokenState] = useState<string | null>(() => getToken())

  const login = useCallback(async (credentials: LoginRequest) => {
    const res = await authApi.login(credentials)
    setToken(res.token)
    setTokenState(res.token)
  }, [])

  const register = useCallback(
    async (data: RegisterRequest) => {
      await authApi.register(data)
      await login({ username: data.username, password: data.password })
    },
    [login],
  )

  const logout = useCallback(() => {
    clearToken()
    setTokenState(null)
  }, [])

  const value = useMemo<AuthContextValue>(
    () => ({ isAuthenticated: token !== null, login, register, logout }),
    [token, login, register, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

/** Typed accessor — throws if used outside the provider (no untyped defaults). */
export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within an AuthProvider')
  return ctx
}
