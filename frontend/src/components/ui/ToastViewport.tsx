import { useToast, type ToastVariant } from '../../context/UIContext'
import { Icon, type IconName } from './Icon'
import { cn } from '../../lib/cn'

const VARIANT_STYLES: Record<ToastVariant, { ring: string; icon: IconName; tint: string }> = {
  success: { ring: 'border-neon-lime/40', icon: 'check', tint: 'text-neon-lime' },
  error: { ring: 'border-neon-pink/40', icon: 'alert', tint: 'text-neon-pink' },
  info: { ring: 'border-neon-cyan/40', icon: 'sparkles', tint: 'text-neon-cyan' },
}

/** Fixed-position stack of toasts, driven by UIContext. */
export function ToastViewport() {
  const { toasts, dismiss } = useToast()

  return (
    <div
      className="pointer-events-none fixed inset-x-0 bottom-0 z-50 flex flex-col items-center gap-2 p-4 sm:items-end"
      aria-live="polite"
    >
      {toasts.map((toast) => {
        const style = VARIANT_STYLES[toast.variant]
        return (
          <div
            key={toast.id}
            role="status"
            className={cn(
              'pointer-events-auto flex w-full max-w-sm animate-fade-up items-center gap-3 rounded-xl border glass px-4 py-3',
              style.ring,
            )}
          >
            <span className={style.tint}>
              <Icon name={style.icon} size={18} />
            </span>
            <p className="flex-1 text-sm text-slate-100">{toast.message}</p>
            <button
              onClick={() => dismiss(toast.id)}
              aria-label="Dismiss notification"
              className="text-slate-400 transition-colors hover:text-slate-100"
            >
              <Icon name="close" size={16} />
            </button>
          </div>
        )
      })}
    </div>
  )
}
