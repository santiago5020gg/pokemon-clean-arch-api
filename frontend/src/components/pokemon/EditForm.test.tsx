import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { EditForm } from './EditForm'

const initial = { localizedName: 'Bulbasaur', region: 'Kanto', internalTags: ['starter'] }

describe('EditForm', () => {
  it('blocks submit and shows an error when localized name is cleared', async () => {
    const onSubmit = vi.fn()
    render(<EditForm initial={initial} onSubmit={onSubmit} onCancel={() => {}} />)
    await userEvent.clear(screen.getByLabelText('Localized name'))
    await userEvent.click(screen.getByRole('button', { name: 'Save changes' }))
    expect(screen.getByText('Localized name is required')).toBeInTheDocument()
    expect(onSubmit).not.toHaveBeenCalled()
  })

  it('submits parsed tags when valid', async () => {
    const onSubmit = vi.fn()
    render(<EditForm initial={initial} onSubmit={onSubmit} onCancel={() => {}} />)
    const tags = screen.getByLabelText('Internal tags')
    await userEvent.clear(tags)
    await userEvent.type(tags, 'grass, kanto ,  seed')
    await userEvent.click(screen.getByRole('button', { name: 'Save changes' }))
    expect(onSubmit).toHaveBeenCalledWith({
      localizedName: 'Bulbasaur',
      region: 'Kanto',
      internalTags: ['grass', 'kanto', 'seed'],
    })
  })

  it('renders a server-side field error', () => {
    render(
      <EditForm
        initial={initial}
        onSubmit={() => {}}
        onCancel={() => {}}
        serverErrors={[{ field: 'region', message: 'Unknown region' }]}
      />,
    )
    expect(screen.getByText('Unknown region')).toBeInTheDocument()
  })

  it('cancels via the cancel button', async () => {
    const onCancel = vi.fn()
    render(<EditForm initial={initial} onSubmit={() => {}} onCancel={onCancel} />)
    await userEvent.click(screen.getByRole('button', { name: 'Cancel' }))
    expect(onCancel).toHaveBeenCalledOnce()
  })
})
