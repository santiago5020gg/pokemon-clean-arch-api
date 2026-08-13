import useSWR from 'swr'
import { fetchPokemonList, listKey } from '../api/pokemon'
import type { PageResult, PokemonSummary } from '../api/types'

/**
 * Paginated list hook (US01). Keyed by (q,page,size) so identical requests are
 * served from the SWR cache instead of re-hitting the server. An optional name
 * query drives server-side search so it composes with pagination.
 */
export function usePokemonList(q: string, page: number, size: number) {
  const { data, error, isLoading, mutate } = useSWR<PageResult<PokemonSummary>>(
    listKey(q, page, size),
    () => fetchPokemonList(q, page, size),
    { keepPreviousData: true },
  )

  return { page: data, error, isLoading, refresh: mutate }
}
