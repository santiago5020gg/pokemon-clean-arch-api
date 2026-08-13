import useSWR from 'swr'
import { fetchPokemonDetail, detailKey } from '../api/pokemon'
import type { PokemonDetail } from '../api/types'

/**
 * Single-Pokémon detail hook (US02). Keyed by id; a null id disables fetching.
 */
export function usePokemonDetail(id: number | null) {
  const { data, error, isLoading, mutate } = useSWR<PokemonDetail>(
    id === null ? null : detailKey(id),
    () => fetchPokemonDetail(id as number),
  )

  return { pokemon: data, error, isLoading, refresh: mutate }
}
