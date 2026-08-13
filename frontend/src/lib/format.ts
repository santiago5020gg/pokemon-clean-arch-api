/** Domain formatting helpers. Pure functions — unit-tested by input/output. */

/** PokeAPI weight is in hectograms; the UI shows kilograms. */
export function hectogramsToKg(hectograms: number): number {
  return Math.round((hectograms / 10) * 10) / 10
}

/** Human-readable mass, e.g. 69 → "6.9 kg". */
export function formatWeight(hectograms: number): string {
  return `${hectogramsToKg(hectograms)} kg`
}

/** Capitalize an API name/slug: "bulbasaur" → "Bulbasaur", "mr-mime" → "Mr Mime". */
export function titleCase(value: string): string {
  return value
    .split(/[\s-]+/)
    .filter(Boolean)
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ')
}

/** Zero-padded Pokédex id, e.g. 1 → "#001". */
export function formatDexId(id: number): string {
  return `#${String(id).padStart(3, '0')}`
}
