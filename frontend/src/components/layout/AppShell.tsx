import type { ReactNode } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { useToast } from '../../context/UIContext'
import { Icon } from '../ui/Icon'
import { Button } from '../ui/Button'

/** App frame: sticky glass navbar + main content region. */
export function AppShell({ children }: { children: ReactNode }) {
  const { isAuthenticated, logout } = useAuth()
  const { notify } = useToast()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    notify('Signed out', 'info')
    navigate('/')
  }

  return (
    <div className="min-h-full">
      <header className="sticky top-0 z-40 border-b border-white/5 bg-void-900/70 backdrop-blur-xl">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-3.5 sm:px-6">
          <Link to="/" className="flex items-center gap-2.5" aria-label="Pokédex home">
            <span className="text-neon-violet">
              <Icon name="pokeball" size={26} />
            </span>
            <span className="font-display text-xl font-bold tracking-tight text-slate-50">
              Pokédex
            </span>
          </Link>

          {isAuthenticated ? (
            <Button variant="ghost" onClick={handleLogout}>
              <Icon name="logout" size={16} />
              Sign out
            </Button>
          ) : (
            <Button variant="ghost" onClick={() => navigate('/login')}>
              <Icon name="login" size={16} />
              Sign in
            </Button>
          )}
        </div>
      </header>

      <main className="mx-auto max-w-6xl px-4 py-8 sm:px-6">{children}</main>
    </div>
  )
}
