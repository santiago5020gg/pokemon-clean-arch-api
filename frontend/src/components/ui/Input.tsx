import { useId, type InputHTMLAttributes } from 'react'
import { cn } from '../../lib/cn'

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string
  /** Field-level error message; also sets aria-invalid. */
  error?: string
}

/** Labeled text field with an accessible error message. */
export function Input({ label, error, id, className, ...rest }: InputProps) {
  const generatedId = useId()
  const inputId = id ?? generatedId
  const errorId = `${inputId}-error`

  return (
    <div className="flex flex-col gap-1.5">
      <label htmlFor={inputId} className="text-sm font-medium text-slate-300">
        {label}
      </label>
      <input
        id={inputId}
        aria-invalid={error ? true : undefined}
        aria-describedby={error ? errorId : undefined}
        className={cn(
          'w-full rounded-xl border bg-void-800/60 px-4 py-2.5 text-slate-100 placeholder:text-slate-500',
          'backdrop-blur transition-colors focus:bg-void-800/80',
          error ? 'border-neon-pink/60' : 'border-white/10 focus:border-neon-violet/60',
          className,
        )}
        {...rest}
      />
      {error && (
        <p id={errorId} role="alert" className="text-sm text-neon-pink">
          {error}
        </p>
      )}
    </div>
  )
}
