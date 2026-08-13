import type {
  AuthResponse,
  PageResult,
  PokemonDetail,
  PokemonSummary,
  SyncResult,
  User,
} from '../api/types'

export const bulbasaurSummary: PokemonSummary = {
  id: 1,
  name: 'bulbasaur',
  spriteUrl: 'https://img.example/1.png',
  category: 'Seed Pokémon',
  weight: 69,
  abilities: ['overgrow', 'chlorophyll'],
}

export const charmanderSummary: PokemonSummary = {
  id: 4,
  name: 'charmander',
  spriteUrl: 'https://img.example/4.png',
  category: 'Lizard Pokémon',
  weight: 85,
  abilities: ['blaze', 'solar-power'],
}

export const listPage: PageResult<PokemonSummary> = {
  content: [bulbasaurSummary, charmanderSummary],
  page: 0,
  size: 20,
  totalElements: 2,
  totalPages: 1,
}

export const bulbasaurDetail: PokemonDetail = {
  id: 1,
  name: 'bulbasaur',
  imageUrl: 'https://img.example/1.png',
  stats: { hp: 45, attack: 49, defense: 49, specialAttack: 65, specialDefense: 65, speed: 45 },
  description: 'A strange seed was planted on its back at birth.',
  evolutions: ['bulbasaur', 'ivysaur', 'venusaur'],
  localizedName: 'Bulbasaur',
  region: 'Kanto',
  internalTags: ['starter', 'grass'],
}

export const syncResult: SyncResult = {
  synced: 2,
  created: 2,
  updated: 0,
  items: [
    { id: 1, name: 'bulbasaur', category: 'Seed Pokémon' },
    { id: 4, name: 'charmander', category: 'Lizard Pokémon' },
  ],
}

export const adminUser: User = {
  id: 1,
  username: 'ash',
  email: 'ash@pallet.town',
  role: 'USER',
}

export const authResponse: AuthResponse = {
  token: 'jwt-test-token',
  type: 'Bearer',
  expiresIn: 3600,
}
