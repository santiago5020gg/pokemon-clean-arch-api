import { describe, it, expect } from 'vitest'
import { accentForId } from './accent'

describe('accentForId', () => {
  it('is deterministic for the same id', () => {
    expect(accentForId(1)).toEqual(accentForId(1))
  })

  it('spreads different ids across the palette', () => {
    const gradients = new Set([1, 2, 3, 4].map((id) => accentForId(id).gradient))
    expect(gradients.size).toBe(4)
  })

  it('always returns a defined accent, even for large ids', () => {
    expect(accentForId(9999).gradient).toBeTruthy()
  })
})
