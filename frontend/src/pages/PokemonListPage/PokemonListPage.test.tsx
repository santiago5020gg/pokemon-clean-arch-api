import { describe, it, expect, beforeEach } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { http, HttpResponse } from 'msw'
import { Route } from 'react-router-dom'
import { server } from '../../test/server'
import { syncResult } from '../../test/fixtures'
import { renderWithProviders } from '../../test/renderWithProviders'
import { clearToken, setToken } from '../../lib/tokenStore'
import { PokemonListPage } from './index'

/** Captures the JSON body of the next sync POST for assertions. */
function captureSyncBody() {
  const captured: { body?: unknown } = {}
  server.use(
    http.post('/api/pokemon/sync', async ({ request }) => {
      captured.body = await request.json()
      return HttpResponse.json(syncResult, { status: 201 })
    }),
  )
  return captured
}

describe('PokemonListPage', () => {
  beforeEach(() => clearToken())

  it('shows a loading spinner then the Pokémon cards', async () => {
    renderWithProviders(<PokemonListPage />)
    expect(screen.getByRole('status', { name: 'Loading Pokémon' })).toBeInTheDocument()
    expect(await screen.findByText('Bulbasaur')).toBeInTheDocument()
    expect(screen.getByText('Charmander')).toBeInTheDocument()
  })

  it('filters the loaded page by name', async () => {
    renderWithProviders(<PokemonListPage />)
    await screen.findByText('Bulbasaur')
    await userEvent.type(screen.getByLabelText('Filter this page'), 'char')
    expect(screen.queryByText('Bulbasaur')).not.toBeInTheDocument()
    expect(screen.getByText('Charmander')).toBeInTheDocument()
  })

  it('shows an empty state when there are no Pokémon', async () => {
    server.use(
      http.get('/api/pokemon', () =>
        HttpResponse.json({ content: [], page: 0, size: 20, totalElements: 0, totalPages: 0 }),
      ),
    )
    renderWithProviders(<PokemonListPage />)
    expect(await screen.findByText('No Pokémon yet')).toBeInTheDocument()
  })

  it('redirects an unauthenticated sync attempt to the login page', async () => {
    renderWithProviders(<PokemonListPage />, {
      extraRoutes: <Route path="/login" element={<div>Login screen</div>} />,
    })
    await screen.findByText('Bulbasaur')
    await userEvent.click(screen.getByRole('button', { name: /Replicate from PokeAPI/ }))
    expect(await screen.findByText('Login screen')).toBeInTheDocument()
  })

  it('replicates the default of 100 when authenticated', async () => {
    setToken('jwt-test-token')
    const captured = captureSyncBody()
    renderWithProviders(<PokemonListPage />)
    await screen.findByText('Bulbasaur')
    await userEvent.click(screen.getByRole('button', { name: /Replicate from PokeAPI/ }))
    await waitFor(() => expect(captured.body).toEqual({ limit: 100, offset: 0 }))
  })

  it('replicates the amount chosen in the count field', async () => {
    setToken('jwt-test-token')
    const captured = captureSyncBody()
    renderWithProviders(<PokemonListPage />)
    await screen.findByText('Bulbasaur')
    const count = screen.getByLabelText('How many to replicate')
    await userEvent.clear(count)
    await userEvent.type(count, '50')
    await userEvent.click(screen.getByRole('button', { name: /Replicate from PokeAPI/ }))
    await waitFor(() => expect(captured.body).toEqual({ limit: 50, offset: 0 }))
  })

  it('caps the replication count at 151', async () => {
    setToken('jwt-test-token')
    const captured = captureSyncBody()
    renderWithProviders(<PokemonListPage />)
    await screen.findByText('Bulbasaur')
    const count = screen.getByLabelText('How many to replicate')
    await userEvent.clear(count)
    await userEvent.type(count, '999')
    await userEvent.click(screen.getByRole('button', { name: /Replicate from PokeAPI/ }))
    await waitFor(() => expect(captured.body).toEqual({ limit: 151, offset: 0 }))
  })

  it('shows an error state when the list request fails', async () => {
    server.use(
      http.get('/api/pokemon', () =>
        HttpResponse.json({ status: 500, message: 'boom', fieldErrors: null }, { status: 500 }),
      ),
    )
    renderWithProviders(<PokemonListPage />)
    expect(await screen.findByRole('alert')).toBeInTheDocument()
  })
})
