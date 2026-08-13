import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { PokemonCard } from './PokemonCard'
import { bulbasaurSummary } from '../../test/fixtures'

describe('PokemonCard', () => {
  it('shows the name, dex id, category and mass in kg', () => {
    render(<PokemonCard pokemon={bulbasaurSummary} onSelect={() => {}} />)
    expect(screen.getByText('Bulbasaur')).toBeInTheDocument()
    expect(screen.getByText('#001')).toBeInTheDocument()
    expect(screen.getByText('Seed Pokémon')).toBeInTheDocument()
    expect(screen.getByText('6.9 kg')).toBeInTheDocument()
  })

  it('lists the abilities', () => {
    render(<PokemonCard pokemon={bulbasaurSummary} onSelect={() => {}} />)
    expect(screen.getByText('Overgrow')).toBeInTheDocument()
    expect(screen.getByText('Chlorophyll')).toBeInTheDocument()
  })

  it('calls onSelect with the id when clicked', async () => {
    const onSelect = vi.fn()
    render(<PokemonCard pokemon={bulbasaurSummary} onSelect={onSelect} />)
    await userEvent.click(screen.getByRole('button', { name: 'View Bulbasaur' }))
    expect(onSelect).toHaveBeenCalledWith(1)
  })
})
