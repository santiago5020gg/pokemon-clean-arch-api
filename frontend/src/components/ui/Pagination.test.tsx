import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { Pagination } from './Pagination'

describe('Pagination', () => {
  it('renders a numbered button for each page when they fit', () => {
    render(<Pagination page={0} totalPages={5} onChange={() => {}} />)
    for (const n of ['1', '2', '3', '4', '5']) {
      expect(screen.getByRole('button', { name: `Page ${n}` })).toBeInTheDocument()
    }
  })

  it('marks the current page with aria-current', () => {
    render(<Pagination page={2} totalPages={5} onChange={() => {}} />)
    expect(screen.getByRole('button', { name: 'Page 3' })).toHaveAttribute('aria-current', 'page')
    expect(screen.getByRole('button', { name: 'Page 1' })).not.toHaveAttribute('aria-current')
  })

  it('calls onChange with the 0-based index when a number is clicked', async () => {
    const onChange = vi.fn()
    render(<Pagination page={0} totalPages={5} onChange={onChange} />)
    await userEvent.click(screen.getByRole('button', { name: 'Page 4' }))
    expect(onChange).toHaveBeenCalledWith(3)
  })

  it('windows to at most five numbers around the current page', () => {
    render(<Pagination page={10} totalPages={20} onChange={() => {}} />)
    // current is 1-based 11; window of 5 → 9,10,11,12,13
    for (const n of ['9', '10', '11', '12', '13']) {
      expect(screen.getByRole('button', { name: `Page ${n}` })).toBeInTheDocument()
    }
    expect(screen.queryByRole('button', { name: 'Page 1' })).not.toBeInTheDocument()
    expect(screen.queryByRole('button', { name: 'Page 20' })).not.toBeInTheDocument()
  })

  it('disables Prev on the first page and Next on the last', () => {
    const { rerender } = render(<Pagination page={0} totalPages={5} onChange={() => {}} />)
    expect(screen.getByRole('button', { name: 'Previous page' })).toBeDisabled()
    rerender(<Pagination page={4} totalPages={5} onChange={() => {}} />)
    expect(screen.getByRole('button', { name: 'Next page' })).toBeDisabled()
  })

  it('requests the next page on Next click', async () => {
    const onChange = vi.fn()
    render(<Pagination page={1} totalPages={5} onChange={onChange} />)
    await userEvent.click(screen.getByRole('button', { name: 'Next page' }))
    expect(onChange).toHaveBeenCalledWith(2)
  })
})
