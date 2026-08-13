import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { Pagination } from './Pagination'

describe('Pagination', () => {
  it('shows a 1-based position label', () => {
    render(<Pagination page={0} totalPages={5} onChange={() => {}} />)
    expect(screen.getByText('1 / 5')).toBeInTheDocument()
  })

  it('disables Prev on the first page', () => {
    render(<Pagination page={0} totalPages={5} onChange={() => {}} />)
    expect(screen.getByRole('button', { name: 'Previous page' })).toBeDisabled()
  })

  it('disables Next on the last page', () => {
    render(<Pagination page={4} totalPages={5} onChange={() => {}} />)
    expect(screen.getByRole('button', { name: 'Next page' })).toBeDisabled()
  })

  it('requests the next page on click', async () => {
    const onChange = vi.fn()
    render(<Pagination page={1} totalPages={5} onChange={onChange} />)
    await userEvent.click(screen.getByRole('button', { name: 'Next page' }))
    expect(onChange).toHaveBeenCalledWith(2)
  })
})
