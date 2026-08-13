import { useState, type FormEvent } from 'react'
import type { RegisterRequest } from '../../api/types'
import { Input } from '../ui/Input'
import { Button } from '../ui/Button'

interface RegisterFormProps {
  onSubmit: (data: RegisterRequest) => void
  submitting?: boolean
  error?: string
}

type Errors = Partial<Record<'username' | 'email' | 'password', string>>

// Mirrors the backend RegisterRequest Bean Validation.
function validate(username: string, email: string, password: string): Errors {
  const errors: Errors = {}
  if (username.trim().length < 3) errors.username = 'At least 3 characters'
  else if (username.length > 50) errors.username = 'Max 50 characters'
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) errors.email = 'Enter a valid email'
  if (password.length < 6) errors.password = 'At least 6 characters'
  else if (password.length > 100) errors.password = 'Max 100 characters'
  return errors
}

/** Presentational registration form (US auth). */
export function RegisterForm({ onSubmit, submitting = false, error }: RegisterFormProps) {
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [errors, setErrors] = useState<Errors>({})

  function handleSubmit(event: FormEvent) {
    event.preventDefault()
    const found = validate(username, email, password)
    setErrors(found)
    if (Object.keys(found).length > 0) return
    onSubmit({ username: username.trim(), email: email.trim(), password })
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
        error={errors.username}
      />
      <Input
        label="Email"
        type="email"
        autoComplete="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        error={errors.email}
      />
      <Input
        label="Password"
        type="password"
        autoComplete="new-password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        error={errors.password}
      />
      <Button type="submit" loading={submitting} className="mt-1 w-full">
        Create account
      </Button>
    </form>
  )
}
