import { Icon } from './Icon'
import { Button } from './Button'

interface ErrorStateProps {
  title?: string
  message: string
  onRetry?: () => void
}

/** Error panel that names the problem and offers recovery. */
export function ErrorState({ title = 'Something went wrong', message, onRetry }: ErrorStateProps) {
  return (
    <div
      role="alert"
      className="flex flex-col items-center justify-center gap-3 py-16 text-center"
    >
      <span className="rounded-2xl border border-neon-pink/30 bg-neon-pink/10 p-4 text-neon-pink">
        <Icon name="alert" size={32} />
      </span>
      <h2 className="text-lg font-semibold text-slate-100">{title}</h2>
      <p className="max-w-sm text-sm text-slate-400">{message}</p>
      {onRetry && (
        <Button variant="ghost" onClick={onRetry} className="mt-2">
          Try again
        </Button>
      )}
    </div>
  )
}
