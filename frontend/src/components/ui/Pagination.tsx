import { Icon } from './Icon'
import { Button } from './Button'

interface PaginationProps {
  /** Zero-based current page. */
  page: number
  totalPages: number
  onChange: (page: number) => void
}

/** Previous/next pager with a 1-based label. Disabled at the bounds. */
export function Pagination({ page, totalPages, onChange }: PaginationProps) {
  const canPrev = page > 0
  const canNext = page < totalPages - 1

  return (
    <nav className="flex items-center justify-center gap-4" aria-label="Pagination">
      <Button
        variant="ghost"
        onClick={() => onChange(page - 1)}
        disabled={!canPrev}
        aria-label="Previous page"
      >
        <Icon name="chevron-left" size={16} />
        Prev
      </Button>
      <span className="font-mono text-sm text-slate-400" aria-live="polite">
        {Math.min(page + 1, totalPages)} / {totalPages}
      </span>
      <Button
        variant="ghost"
        onClick={() => onChange(page + 1)}
        disabled={!canNext}
        aria-label="Next page"
      >
        Next
        <Icon name="chevron-right" size={16} />
      </Button>
    </nav>
  )
}
