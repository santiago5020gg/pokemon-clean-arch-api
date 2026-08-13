import useSWR from 'swr'
import { fetchPokemonList, listKey } from '../api/pokemon'
import type { PageResult, PokemonSummary } from '../api/types'

/**
 * Paginated list hook (US01). Keyed by (page,size) so identical requests are
 * served from the SWR cache instead of re-hitting the server.
 */
export function usePokemonList(page: number, size: number) {
  const { data, error, isLoading, mutate } = useSWR<PageResult<PokemonSummary>>(
    listKey(page, size),
    () => fetchPokemonList(page, size),
    { keepPreviousData: true },
  )

  return { page: data, error, isLoading, refresh: mutate }
}
