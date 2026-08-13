import type { PokemonSummary } from '../../api/types'
import { accentForId } from '../../lib/accent'
import { formatDexId, formatWeight, titleCase } from '../../lib/format'
import { cn } from '../../lib/cn'

interface PokemonCardProps {
  pokemon: PokemonSummary
  onSelect: (id: number) => void
}

/** Presentational Pokémon tile (US01). Whole card is one keyboard-focusable button. */
export function PokemonCard({ pokemon, onSelect }: PokemonCardProps) {
  const accent = accentForId(pokemon.id)

  return (
    <button
      type="button"
      onClick={() => onSelect(pokemon.id)}
      aria-label={`View ${titleCase(pokemon.name)}`}
      className={cn(
        'group relative flex flex-col items-center gap-3 overflow-hidden rounded-2xl glass glass-hover p-5 text-center',
        'ring-1 ring-inset ring-white/5 animate-fade-up',
      )}
    >
      {/* Neon aura, derived from id (the API exposes no elemental type). */}
      <div
        aria-hidden="true"
        className={cn(
          'absolute inset-x-0 -top-16 h-32 bg-gradient-to-b blur-2xl transition-opacity duration-300 opacity-60 group-hover:opacity-100',
          accent.gradient,
        )}
      />
      <span className="relative z-10 self-end font-mono text-xs text-slate-400">
        {formatDexId(pokemon.id)}
      </span>
      <img
        src={pokemon.spriteUrl}
        alt={titleCase(pokemon.name)}
        loading="lazy"
        className="relative z-10 h-24 w-24 object-contain drop-shadow-[0_8px_20px_rgba(124,92,255,0.35)] transition-transform duration-300 group-hover:-translate-y-1 group-hover:scale-105"
      />
      <div className="relative z-10">
        <h3 className="font-display text-lg font-semibold text-slate-50">
          {titleCase(pokemon.name)}
        </h3>
        <p className="text-xs text-slate-400">{pokemon.category}</p>
      </div>
      <div className="relative z-10 flex flex-wrap justify-center gap-1.5">
        {pokemon.abilities.map((ability) => (
          <span
            key={ability}
            className="rounded-full border border-white/10 bg-white/5 px-2.5 py-0.5 text-[11px] text-slate-300"
          >
            {titleCase(ability)}
          </span>
        ))}
      </div>
      <span className="relative z-10 font-mono text-xs text-neon-cyan">
        {formatWeight(pokemon.weight)}
      </span>
    </button>
  )
}
