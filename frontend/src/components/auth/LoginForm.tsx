import { useState, type FormEvent } from 'react'
import type { LoginRequest } from '../../api/types'
import { Input } from '../ui/Input'
import { Button } from '../ui/Button'

interface LoginFormProps {
  onSubmit: (credentials: LoginRequest) => void
  submitting?: boolean
  /** Top-level error message (e.g. invalid credentials). */
  error?: string
}

/** Presentational login form (US auth). */
export function LoginForm({ onSubmit, submitting = false, error }: LoginFormProps) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [touched, setTouched] = useState(false)

  const missing = !username.trim() || !password

  function handleSubmit(event: FormEvent) {
    event.preventDefault()
    setTouched(true)
    if (missing) return
    onSubmit({ username: username.trim(), password })
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-4" noValidate>
      {error && (
        <p role="alert" className="rounded-xl border border-neon-pink/30 bg-neon-pink/10 px-4 py-2.5 text-sm text-neon-pink">
          {error}
        </p>
      )}
      <Input
        label="Username"
        autoComplete="username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        error={touched && !username.trim() ? 'Username is required' : undefined}
      />
      <Input
        label="Password"
        type="password"
        autoComplete="current-password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        error={touched && !password ? 'Password is required' : undefined}
      />
      <Button type="submit" loading={submitting} className="mt-1 w-full">
        Sign in
      </Button>
    </form>
  )
}
