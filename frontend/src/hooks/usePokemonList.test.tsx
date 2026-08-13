import { describe, it, expect } from 'vitest'
import { renderHook, waitFor } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { SWRConfig } from 'swr'
import type { ReactNode } from 'react'
import { server } from '../test/server'
import { usePokemonList } from './usePokemonList'
import { listPage } from '../test/fixtures'

// Fresh SWR cache per render so tests don't leak cached data into each other.
function wrapper({ children }: { children: ReactNode }) {
  return <SWRConfig value={{ provider: () => new Map(), dedupingInterval: 0 }}>{children}</SWRConfig>
}

describe('usePokemonList', () => {
  it('resolves the paginated page', async () => {
    const { result } = renderHook(() => usePokemonList('', 0, 20), { wrapper })
    expect(result.current.isLoading).toBe(true)
    await waitFor(() => expect(result.current.page).toEqual(listPage))
    expect(result.current.error).toBeUndefined()
  })

  it('exposes an error when the request fails', async () => {
    server.use(
      http.get('/api/pokemon', () =>
        HttpResponse.json({ status: 500, message: 'boom', fieldErrors: null }, { status: 500 }),
      ),
    )
    const { result } = renderHook(() => usePokemonList('', 0, 20), { wrapper })
    await waitFor(() => expect(result.current.error).toBeDefined())
    expect(result.current.error.status).toBe(500)
  })
})
