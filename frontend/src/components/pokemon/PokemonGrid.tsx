import type { PokemonSummary } from '../../api/types'
import { PokemonCard } from './PokemonCard'

interface PokemonGridProps {
  pokemon: PokemonSummary[]
  onSelect: (id: number) => void
}

/** Responsive grid of Pokémon cards. */
export function PokemonGrid({ pokemon, onSelect }: PokemonGridProps) {
  return (
    <ul className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
      {pokemon.map((p) => (
        <li key={p.id}>
          <PokemonCard pokemon={p} onSelect={onSelect} />
        </li>
      ))}
    </ul>
  )
}
