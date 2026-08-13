/**
 * TypeScript mirrors of the backend DTOs (Spring Boot records).
 * Source of truth: backend/src/main/java/com/pokedex/core/dto/*.
 * Keep field names identical to the JSON the API returns.
 */

/** Generic paginated envelope: PageResult<T>. */
export interface PageResult<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

/** List projection (US01): sprite, category, mass (hectograms), abilities. */
export interface PokemonSummary {
  id: number
  name: string
  spriteUrl: string
  category: string
  /** Weight in hectograms; divide by 10 for kg. */
  weight: number
  abilities: string[]
}

/** Core statistics (US02). */
export interface Stats {
  hp: number
  attack: number
  defense: number
  specialAttack: number
  specialDefense: number
  speed: number
}

/** Detail projection (US02) including proprietary fields. */
export interface PokemonDetail {
  id: number
  name: string
  imageUrl: string
  stats: Stats
  description: string
  evolutions: string[]
  localizedName: string
  region: string
  internalTags: string[]
}

/** Body of PUT /api/pokemon/{id} (US04): only proprietary fields are editable. */
export interface PokemonUpdateRequest {
  localizedName: string
  region: string
  internalTags: string[]
}

/** Body of POST /api/pokemon/sync (US03). */
export interface SyncRequest {
  limit?: number
  offset?: number
}

/** One replicated item within a sync run. */
export interface SyncItem {
  id: number
  name: string
  category: string
}

/** Outcome of POST /api/pokemon/sync (US03). */
export interface SyncResult {
  synced: number
  created: number
  updated: number
  items: SyncItem[]
}

/** Body of POST /api/auth/register. */
export interface RegisterRequest {
  username: string
  email: string
  password: string
}

/** Body of POST /api/auth/login. */
export interface LoginRequest {
  username: string
  password: string
}

/** Public user projection returned by register (password hash never exposed). */
export interface User {
  id: number
  username: string
  email: string
  role: string
}

/** Successful login response: JWT + type + lifetime (seconds). */
export interface AuthResponse {
  token: string
  type: string
  expiresIn: number
}
