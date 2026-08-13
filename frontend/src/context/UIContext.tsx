import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useRef,
  useState,
  type ReactNode,
} from 'react'

export type ToastVariant = 'success' | 'error' | 'info'

export interface Toast {
  id: number
  message: string
  variant: ToastVariant
}

interface UIContextValue {
  toasts: Toast[]
  notify: (message: string, variant?: ToastVariant) => void
  dismiss: (id: number) => void
}

const UIContext = createContext<UIContextValue | null>(null)

const AUTO_DISMISS_MS = 4000

export function UIProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<Toast[]>([])
  const nextId = useRef(1)

  const dismiss = useCallback((id: number) => {
    setToasts((current) => current.filter((t) => t.id !== id))
  }, [])

  const notify = useCallback(
    (message: string, variant: ToastVariant = 'info') => {
      const id = nextId.current++
      setToasts((current) => [...current, { id, message, variant }])
      setTimeout(() => dismiss(id), AUTO_DISMISS_MS)
    },
    [dismiss],
  )

  const value = useMemo<UIContextValue>(() => ({ toasts, notify, dismiss }), [toasts, notify, dismiss])

  return <UIContext.Provider value={value}>{children}</UIContext.Provider>
}

/** Typed accessor — throws outside the provider. */
export function useToast(): UIContextValue {
  const ctx = useContext(UIContext)
  if (!ctx) throw new Error('useToast must be used within a UIProvider')
  return ctx
}
