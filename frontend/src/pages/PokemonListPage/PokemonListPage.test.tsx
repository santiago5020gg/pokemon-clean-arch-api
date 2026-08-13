import { describe, it, expect, beforeEach } from 'vitest'
import { screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { http, HttpResponse } from 'msw'
import { Route } from 'react-router-dom'
import { server } from '../../test/server'
import { renderWithProviders } from '../../test/renderWithProviders'
import { clearToken } from '../../lib/tokenStore'
import { PokemonListPage } from './index'

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
