import { request } from './client'
import type {
  PageResult,
  PokemonDetail,
  PokemonSummary,
  PokemonUpdateRequest,
  SyncRequest,
  SyncResult,
} from './types'

/** SWR key builder for the paginated list — identical (q,page,size) → cache hit. */
export function listKey(q: string, page: number, size: number): string {
  const base = `/pokemon?page=${page}&size=${size}`
  return q ? `${base}&q=${encodeURIComponent(q)}` : base
}

/** SWR key builder for a single Pokémon detail. */
export function detailKey(id: number): string {
  return `/pokemon/${id}`
}

export function fetchPokemonList(
  q: string,
  page: number,
  size: number,
): Promise<PageResult<PokemonSummary>> {
  return request<PageResult<PokemonSummary>>(listKey(q, page, size))
}

export function fetchPokemonDetail(id: number): Promise<PokemonDetail> {
  return request<PokemonDetail>(detailKey(id))
}

/** US03 — replicate PokeAPI data into the local store (protected). */
export function syncPokemon(body: SyncRequest = {}): Promise<SyncResult> {
  return request<SyncResult>('/pokemon/sync', {
    method: 'POST',
    body: JSON.stringify(body),
  })
}

/** US04 — edit proprietary fields (protected); 404 unknown id, 400 invalid. */
export function updatePokemon(id: number, body: PokemonUpdateRequest): Promise<PokemonDetail> {
  return request<PokemonDetail>(detailKey(id), {
    method: 'PUT',
    body: JSON.stringify(body),
  })
}

/** Delete a replicated Pokémon (protected). */
export function deletePokemon(id: number): Promise<void> {
  return request<void>(detailKey(id), { method: 'DELETE' })
}
