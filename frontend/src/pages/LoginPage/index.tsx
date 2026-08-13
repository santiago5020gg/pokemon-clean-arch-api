import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { useToast } from '../../context/UIContext'
import { ApiError } from '../../api/client'
import type { LoginRequest } from '../../api/types'
import { LoginForm } from '../../components/auth/LoginForm'
import { GlassCard } from '../../components/ui/GlassCard'

interface FromState {
  from?: string
}

/** Login container. Redirects to the intended route (or home) on success. */
export function LoginPage() {
  const { login } = useAuth()
  const { notify } = useToast()
  const navigate = useNavigate()
  const location = useLocation()
  const from = (location.state as FromState | null)?.from ?? '/'

  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string>()

  async function handleSubmit(credentials: LoginRequest) {
    setSubmitting(true)
    setError(undefined)
    try {
      await login(credentials)
      notify('Welcome back!', 'success')
      navigate(from, { replace: true })
    } catch (err) {
      setError(
        err instanceof ApiError && err.status === 401
          ? 'Invalid username or password'
          : 'Could not sign in. Please try again.',
      )
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="mx-auto flex max-w-md flex-col gap-6 py-8">
      <div className="text-center">
        <h1 className="font-display text-3xl font-bold text-slate-50">Welcome back</h1>
        <p className="mt-1 text-slate-400">Sign in to replicate and customize Pokémon.</p>
      </div>
      <GlassCard className="p-7">
        <LoginForm onSubmit={handleSubmit} submitting={submitting} error={error} />
      </GlassCard>
      <p className="text-center text-sm text-slate-400">
        No account?{' '}
        <Link to="/register" className="text-neon-cyan hover:underline">
          Create one
        </Link>
      </p>
    </div>
  )
}
