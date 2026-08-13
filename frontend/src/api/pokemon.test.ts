import { describe, it, expect, beforeEach, afterEach } from 'vitest'
import { setToken, clearToken } from '../lib/tokenStore'
import {
  listKey,
  detailKey,
  fetchPokemonList,
  fetchPokemonDetail,
  syncPokemon,
  updatePokemon,
  deletePokemon,
} from './pokemon'
import { ApiError } from './client'
import { listPage, bulbasaurDetail, syncResult } from '../test/fixtures'

describe('pokemon endpoints', () => {
  beforeEach(() => setToken('jwt-test-token'))

  it('builds stable SWR keys from params', () => {
    expect(listKey(2, 20)).toBe('/pokemon?page=2&size=20')
    expect(detailKey(1)).toBe('/pokemon/1')
  })

  it('fetches the paginated list', async () => {
    await expect(fetchPokemonList(0, 20)).resolves.toEqual(listPage)
  })

  it('fetches a detail', async () => {
    await expect(fetchPokemonDetail(1)).resolves.toEqual(bulbasaurDetail)
  })

  it('maps a 404 detail to an ApiError', async () => {
    const error = await fetchPokemonDetail(999).catch((e) => e)
    expect(error).toBeInstanceOf(ApiError)
    expect(error.status).toBe(404)
  })

  it('triggers a sync', async () => {
    await expect(syncPokemon({ limit: 2 })).resolves.toEqual(syncResult)
  })

  it('updates proprietary fields', async () => {
    const updated = await updatePokemon(1, {
      localizedName: 'Bulba',
      region: 'Johto',
      internalTags: ['legend'],
    })
    expect(updated.localizedName).toBe('Bulba')
    expect(updated.region).toBe('Johto')
  })

  it('surfaces a 400 with field errors on invalid update', async () => {
    const error = await updatePokemon(1, {
      localizedName: '',
      region: 'Kanto',
      internalTags: [],
    }).catch((e) => e)
    expect(error).toBeInstanceOf(ApiError)
    expect(error.status).toBe(400)
    expect(error.fieldErrors[0].field).toBe('localizedName')
  })

  it('deletes a pokemon', async () => {
    await expect(deletePokemon(1)).resolves.toBeUndefined()
  })

  afterEach(() => clearToken())
})
