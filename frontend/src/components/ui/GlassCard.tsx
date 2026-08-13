import type { HTMLAttributes, ReactNode } from 'react'
import { cn } from '../../lib/cn'

interface GlassCardProps extends HTMLAttributes<HTMLDivElement> {
  children: ReactNode
  /** Adds hover elevation/interaction affordance. */
  interactive?: boolean
}

/** Frosted-glass surface — the base container of the design system. */
export function GlassCard({ children, interactive = false, className, ...rest }: GlassCardProps) {
  return (
    <div
      className={cn('rounded-2xl glass p-5', interactive && 'glass-hover', className)}
      {...rest}
    >
      {children}
    </div>
  )
}
