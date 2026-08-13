import { cn } from '../../lib/cn'

interface SpinnerProps {
  /** Diameter in pixels. */
  size?: number
  className?: string
  /** Accessible label announced to screen readers. */
  label?: string
}

/**
 * Neon ring spinner used for every loading state. Drawn as an SVG arc (not an
 * emoji/glyph) so stroke and weight stay consistent with the icon system.
 */
export function Spinner({ size = 24, className, label = 'Loading' }: SpinnerProps) {
  return (
    <span
      role="status"
      aria-live="polite"
      aria-label={label}
      className={cn('inline-flex', className)}
    >
      <svg
        width={size}
        height={size}
        viewBox="0 0 24 24"
        fill="none"
        className="animate-spin"
        aria-hidden="true"
      >
        <circle cx="12" cy="12" r="9" stroke="currentColor" strokeOpacity="0.15" strokeWidth="3" />
        <path
          d="M21 12a9 9 0 0 0-9-9"
          stroke="currentColor"
          strokeWidth="3"
          strokeLinecap="round"
          className="text-neon-violet"
        />
      </svg>
      <span className="sr-only">{label}</span>
    </span>
  )
}
