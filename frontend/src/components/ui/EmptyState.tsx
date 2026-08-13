import type { ReactNode } from 'react'
import { Icon, type IconName } from './Icon'

interface EmptyStateProps {
  icon?: IconName
  title: string
  message: string
  action?: ReactNode
}

/** Neutral empty state with an icon, explanation and optional action. */
export function EmptyState({ icon = 'inbox', title, message, action }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
      <span className="rounded-2xl glass p-4 text-neon-cyan">
        <Icon name={icon} size={32} />
      </span>
      <h2 className="text-lg font-semibold text-slate-100">{title}</h2>
      <p className="max-w-sm text-sm text-slate-400">{message}</p>
      {action && <div className="mt-2">{action}</div>}
    </div>
  )
}
