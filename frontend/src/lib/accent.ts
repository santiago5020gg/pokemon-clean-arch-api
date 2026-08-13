/**
 * The backend does not expose an elemental type, so cards get a deterministic
 * neon accent derived from the Pokémon id. Same id → same accent (stable across
 * renders and pages); different ids spread across the palette for visual variety.
 */
export interface Accent {
  /** Tailwind gradient classes for the card glow/aura. */
  gradient: string
  /** Solid ring/border tint. */
  ring: string
  /** Soft shadow glow. */
  glow: string
}

const ACCENTS: Accent[] = [
  { gradient: 'from-neon-violet/30 to-neon-indigo/10', ring: 'ring-neon-violet/40', glow: 'shadow-neon-violet' },
  { gradient: 'from-neon-cyan/30 to-neon-indigo/10', ring: 'ring-neon-cyan/40', glow: 'shadow-neon-cyan' },
  { gradient: 'from-neon-pink/30 to-neon-violet/10', ring: 'ring-neon-pink/40', glow: 'shadow-neon-violet' },
  { gradient: 'from-neon-lime/25 to-neon-cyan/10', ring: 'ring-neon-lime/40', glow: 'shadow-neon-cyan' },
]

export function accentForId(id: number): Accent {
  const index = Math.abs(Math.trunc(id)) % ACCENTS.length
  // Non-null: index is always within bounds by construction.
  return ACCENTS[index] as Accent
}
