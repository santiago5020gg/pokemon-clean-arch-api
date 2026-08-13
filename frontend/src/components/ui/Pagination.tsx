import { Icon } from './Icon'
import { Button } from './Button'

interface PaginationProps {
  /** Zero-based current page. */
  page: number
  totalPages: number
  onChange: (page: number) => void
}

/** How many numbered buttons to show at once. */
const WINDOW = 5

/** Zero-based page indices to render, windowed around the current page. */
function pageWindow(page: number, totalPages: number): number[] {
  const end = Math.min(totalPages, Math.max(page - Math.floor(WINDOW / 2), 0) + WINDOW)
  const start = Math.max(0, end - WINDOW)
  return Array.from({ length: end - start }, (_, i) => start + i)
}

/** Numbered pager (1,2,3…) with Prev/Next, windowed and disabled at the bounds. */
export function Pagination({ page, totalPages, onChange }: PaginationProps) {
  const canPrev = page > 0
  const canNext = page < totalPages - 1

  return (
    <nav className="flex flex-wrap items-center justify-center gap-2" aria-label="Pagination">
      <Button
        variant="ghost"
        onClick={() => onChange(page - 1)}
        disabled={!canPrev}
        aria-label="Previous page"
      >
        <Icon name="chevron-left" size={16} />
        Prev
      </Button>

      {pageWindow(page, totalPages).map((p) => {
        const isCurrent = p === page
        return (
          <Button
            key={p}
            variant={isCurrent ? 'primary' : 'ghost'}
            className="min-w-[2.75rem] px-3"
            onClick={() => onChange(p)}
            aria-label={`Page ${p + 1}`}
            aria-current={isCurrent ? 'page' : undefined}
          >
            {p + 1}
          </Button>
        )
      })}

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
