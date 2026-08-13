import { describe, it, expect } from 'vitest'
import { renderHook, waitFor } from '@testing-library/react'
import { SWRConfig } from 'swr'
import type { ReactNode } from 'react'
import { usePokemonDetail } from './usePokemonDetail'
import { bulbasaurDetail } from '../test/fixtures'

function wrapper({ children }: { children: ReactNode }) {
  return <SWRConfig value={{ provider: () => new Map(), dedupingInterval: 0 }}>{children}</SWRConfig>
}

describe('usePokemonDetail', () => {
  it('resolves a detail by id', async () => {
    const { result } = renderHook(() => usePokemonDetail(1), { wrapper })
    await waitFor(() => expect(result.current.pokemon).toEqual(bulbasaurDetail))
  })

  it('does not fetch when id is null', () => {
    const { result } = renderHook(() => usePokemonDetail(null), { wrapper })
    expect(result.current.isLoading).toBe(false)
    expect(result.current.pokemon).toBeUndefined()
  })

  it('surfaces a 404 as an error', async () => {
    const { result } = renderHook(() => usePokemonDetail(999), { wrapper })
    await waitFor(() => expect(result.current.error).toBeDefined())
    expect(result.current.error.status).toBe(404)
  })
})
