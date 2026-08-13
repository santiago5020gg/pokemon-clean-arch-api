import { describe, it, expect, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { AuthProvider, useAuth } from './AuthContext'
import { clearToken, getToken } from '../lib/tokenStore'

function Probe() {
  const { isAuthenticated, login, register, logout } = useAuth()
  return (
    <div>
      <span>{isAuthenticated ? 'in' : 'out'}</span>
      <button onClick={() => void login({ username: 'ash', password: 'pikapika' })}>login</button>
      <button
        onClick={() => void register({ username: 'ash', email: 'a@b.co', password: 'pikapika' })}
      >
        register
      </button>
      <button onClick={logout}>logout</button>
    </div>
  )
}

function renderProbe() {
  return render(
    <AuthProvider>
      <Probe />
    </AuthProvider>,
  )
}

describe('AuthContext', () => {
  beforeEach(() => clearToken())

  it('starts unauthenticated with no token', () => {
    renderProbe()
    expect(screen.getByText('out')).toBeInTheDocument()
  })

  it('authenticates and persists the token on login', async () => {
    renderProbe()
    await userEvent.click(screen.getByRole('button', { name: 'login' }))
    await waitFor(() => expect(screen.getByText('in')).toBeInTheDocument())
    expect(getToken()).toBe('jwt-test-token')
  })

  it('auto-logs-in after registration', async () => {
    renderProbe()
    await userEvent.click(screen.getByRole('button', { name: 'register' }))
    await waitFor(() => expect(screen.getByText('in')).toBeInTheDocument())
    expect(getToken()).toBe('jwt-test-token')
  })

  it('clears the token on logout', async () => {
    renderProbe()
    await userEvent.click(screen.getByRole('button', { name: 'login' }))
    await waitFor(() => expect(screen.getByText('in')).toBeInTheDocument())
    await userEvent.click(screen.getByRole('button', { name: 'logout' }))
    expect(screen.getByText('out')).toBeInTheDocument()
    expect(getToken()).toBeNull()
  })

  it('throws when useAuth is used outside the provider', () => {
    expect(() => render(<Probe />)).toThrow(/AuthProvider/)
  })
})
