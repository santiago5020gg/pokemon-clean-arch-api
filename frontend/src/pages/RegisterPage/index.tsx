import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { useToast } from '../../context/UIContext'
import { ApiError } from '../../api/client'
import type { RegisterRequest } from '../../api/types'
import { RegisterForm } from '../../components/auth/RegisterForm'
import { GlassCard } from '../../components/ui/GlassCard'

/** Registration container. Auto-logs-in on success (see AuthContext.register). */
export function RegisterPage() {
  const { register } = useAuth()
  const { notify } = useToast()
  const navigate = useNavigate()

  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string>()

  async function handleSubmit(data: RegisterRequest) {
    setSubmitting(true)
    setError(undefined)
    try {
      await register(data)
      notify('Account created — you are signed in', 'success')
      navigate('/', { replace: true })
    } catch (err) {
      setError(
        err instanceof ApiError && err.status === 409
          ? 'That username or email is already taken'
          : 'Could not create the account. Please try again.',
      )
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="mx-auto flex max-w-md flex-col gap-6 py-8">
      <div className="text-center">
        <h1 className="font-display text-3xl font-bold text-slate-50">Create your account</h1>
        <p className="mt-1 text-slate-400">Join to build your own regional Pokédex.</p>
      </div>
      <GlassCard className="p-7">
        <RegisterForm onSubmit={handleSubmit} submitting={submitting} error={error} />
      </GlassCard>
      <p className="text-center text-sm text-slate-400">
        Already have an account?{' '}
        <Link to="/login" className="text-neon-cyan hover:underline">
          Sign in
        </Link>
      </p>
    </div>
  )
}
