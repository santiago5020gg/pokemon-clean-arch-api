import { describe, it, expect } from 'vitest'
import { hectogramsToKg, formatWeight, titleCase, formatDexId } from './format'

describe('format helpers', () => {
  it('converts hectograms to kilograms', () => {
    expect(hectogramsToKg(69)).toBe(6.9)
    expect(hectogramsToKg(85)).toBe(8.5)
    expect(hectogramsToKg(0)).toBe(0)
  })

  it('formats weight with a kg suffix', () => {
    expect(formatWeight(69)).toBe('6.9 kg')
  })

  it('title-cases names and slugs', () => {
    expect(titleCase('bulbasaur')).toBe('Bulbasaur')
    expect(titleCase('mr-mime')).toBe('Mr Mime')
  })

  it('zero-pads the dex id', () => {
    expect(formatDexId(1)).toBe('#001')
    expect(formatDexId(151)).toBe('#151')
  })
})
