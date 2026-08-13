import { describe, it, expect, beforeEach } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { http, HttpResponse } from 'msw'
import { server } from '../../test/server'
import { renderWithProviders } from '../../test/renderWithProviders'
import { setToken, clearToken } from '../../lib/tokenStore'
import { PokemonDetailPage } from './index'

function renderDetail(id = 1) {
  return renderWithProviders(<PokemonDetailPage />, {
    route: `/pokemon/${id}`,
    path: '/pokemon/:id',
  })
}

describe('PokemonDetailPage', () => {
  beforeEach(() => clearToken())

  it('renders the description, stats and evolutions', async () => {
    renderDetail()
    expect(await screen.findByText(/A strange seed was planted/)).toBeInTheDocument()
    expect(screen.getByText('Base stats')).toBeInTheDocument()
    expect(screen.getByText('Venusaur')).toBeInTheDocument()
  })

  it('shows a not-found state for a missing Pokémon', async () => {
    renderDetail(999)
    expect(await screen.findByText('Pokémon not found')).toBeInTheDocument()
  })

  it('hides owner actions when signed out', async () => {
    renderDetail()
    await screen.findByText('Regional data')
    expect(screen.queryByRole('button', { name: /Edit/ })).not.toBeInTheDocument()
    expect(screen.getByText(/Sign in/)).toBeInTheDocument()
  })

  it('lets an authenticated user edit the proprietary fields', async () => {
    setToken('jwt-test-token')
    let putBody: unknown = null
    server.use(
      http.put('/api/pokemon/1', async ({ request }) => {
        putBody = await request.json()
        return HttpResponse.json({ ...(putBody as object) })
      }),
      http.get('/api/pokemon/1', () =>
        HttpResponse.json({
          id: 1,
          name: 'bulbasaur',
          imageUrl: 'x',
          stats: { hp: 45, attack: 49, defense: 49, specialAttack: 65, specialDefense: 65, speed: 45 },
          description: 'desc',
          evolutions: ['bulbasaur'],
          localizedName: 'Bulbasaur',
          region: 'Kanto',
          internalTags: ['starter'],
        }),
      ),
    )
    renderDetail()
    await userEvent.click(await screen.findByRole('button', { name: /Edit/ }))
    const region = screen.getByLabelText('Region')
    await userEvent.clear(region)
    await userEvent.type(region, 'Johto')
    await userEvent.click(screen.getByRole('button', { name: 'Save changes' }))
    await waitFor(() => expect(putBody).toMatchObject({ region: 'Johto' }))
  })
})
