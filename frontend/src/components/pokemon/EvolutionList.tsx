import { Icon } from '../ui/Icon'
import { titleCase } from '../../lib/format'
import { cn } from '../../lib/cn'

interface EvolutionListProps {
  evolutions: string[]
  /** Highlights the current Pokémon in the chain. */
  current?: string
}

/** Evolutionary lineage as a horizontal chain (US02). */
export function EvolutionList({ evolutions, current }: EvolutionListProps) {
  if (evolutions.length === 0) {
    return <p className="text-sm text-slate-500">No known evolutions.</p>
  }

  return (
    <ol className="flex flex-wrap items-center gap-2">
      {evolutions.map((name, index) => {
        const isCurrent = current !== undefined && name.toLowerCase() === current.toLowerCase()
        return (
          <li key={name} className="flex items-center gap-2">
            <span
              className={cn(
                'rounded-full border px-3 py-1 text-sm',
                isCurrent
                  ? 'border-neon-violet/60 bg-neon-violet/15 text-neon-violet'
                  : 'border-white/10 bg-white/5 text-slate-300',
              )}
              aria-current={isCurrent ? 'true' : undefined}
            >
              {titleCase(name)}
            </span>
            {index < evolutions.length - 1 && (
              <span className="text-slate-500">
                <Icon name="chevron-right" size={16} />
              </span>
            )}
          </li>
        )
      })}
    </ol>
  )
}
