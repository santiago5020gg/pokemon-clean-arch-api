import type { ButtonHTMLAttributes, ReactNode } from 'react'
import { cn } from '../../lib/cn'
import { Spinner } from './Spinner'

type Variant = 'primary' | 'ghost' | 'danger'

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant
  loading?: boolean
  children: ReactNode
}

const VARIANTS: Record<Variant, string> = {
  primary:
    'bg-gradient-to-r from-neon-violet to-neon-indigo text-white shadow-neon-violet hover:brightness-110',
  ghost: 'glass glass-hover text-slate-200',
  danger: 'border border-neon-pink/40 bg-neon-pink/10 text-neon-pink hover:bg-neon-pink/20',
}

/** Primary action control. When `loading`, shows a spinner and is disabled. */
export function Button({
  variant = 'primary',
  loading = false,
  disabled,
  children,
  className,
  ...rest
}: ButtonProps) {
  return (
    <button
      className={cn(
        'inline-flex items-center justify-center gap-2 rounded-xl px-5 py-2.5 text-sm font-semibold',
        'transition-all duration-200 active:scale-[0.98] disabled:cursor-not-allowed disabled:opacity-50',
        VARIANTS[variant],
        className,
      )}
      disabled={disabled || loading}
      aria-busy={loading}
      {...rest}
    >
      {loading && <Spinner size={16} className="text-current" />}
      {children}
    </button>
  )
}
