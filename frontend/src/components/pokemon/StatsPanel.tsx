import type { Stats } from '../../api/types'
import { cn } from '../../lib/cn'

interface StatsPanelProps {
  stats: Stats
}

const STAT_ROWS: Array<{ key: keyof Stats; label: string }> = [
  { key: 'hp', label: 'HP' },
  { key: 'attack', label: 'Attack' },
  { key: 'defense', label: 'Defense' },
  { key: 'specialAttack', label: 'Sp. Atk' },
  { key: 'specialDefense', label: 'Sp. Def' },
  { key: 'speed', label: 'Speed' },
]

// Highest base stat in the games; caps the bar scale.
const MAX_STAT = 255

/** Base-stats bar chart (US02). */
export function StatsPanel({ stats }: StatsPanelProps) {
  return (
    <dl className="flex flex-col gap-3">
      {STAT_ROWS.map(({ key, label }) => {
        const value = stats[key]
        const pct = Math.max(4, Math.round((value / MAX_STAT) * 100))
        return (
          <div key={key} className="grid grid-cols-[5rem_1fr_2.5rem] items-center gap-3">
            <dt className="text-sm text-slate-400">{label}</dt>
            <div className="h-2 overflow-hidden rounded-full bg-white/5">
              <div
                className={cn(
                  'h-full rounded-full bg-gradient-to-r from-neon-violet to-neon-cyan transition-all duration-500',
                )}
                style={{ width: `${pct}%` }}
                role="meter"
                aria-valuenow={value}
                aria-valuemin={0}
                aria-valuemax={MAX_STAT}
                aria-label={label}
              />
            </div>
            <dd className="text-right font-mono text-sm text-slate-200">{value}</dd>
          </div>
        )
      })}
    </dl>
  )
}
