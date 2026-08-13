/**
 * Tiny persistence wrapper for the JWT. Kept separate from the client and the
 * AuthContext so both can share one source of truth without a circular import.
 */
const STORAGE_KEY = 'pokedex.jwt'

export function getToken(): string | null {
  try {
    return localStorage.getItem(STORAGE_KEY)
  } catch {
    return null
  }
}

export function setToken(token: string): void {
  try {
    localStorage.setItem(STORAGE_KEY, token)
  } catch {
    /* storage unavailable (private mode) — auth simply won't persist */
  }
}

export function clearToken(): void {
  try {
    localStorage.removeItem(STORAGE_KEY)
  } catch {
    /* ignore */
  }
}
