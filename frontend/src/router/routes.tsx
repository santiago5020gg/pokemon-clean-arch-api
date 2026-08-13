import { Routes, Route } from 'react-router-dom'
import { PokemonListPage } from '../pages/PokemonListPage'
import { PokemonDetailPage } from '../pages/PokemonDetailPage'
import { LoginPage } from '../pages/LoginPage'
import { RegisterPage } from '../pages/RegisterPage'
import { EmptyState } from '../components/ui/EmptyState'

/**
 * Route table. All read routes are public; write actions (sync/edit/delete) are
 * gated inside their pages by auth state, matching the backend's security model.
 */
export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<PokemonListPage />} />
      <Route path="/pokemon/:id" element={<PokemonDetailPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route
        path="*"
        element={
          <EmptyState
            icon="search"
            title="Page not found"
            message="The page you were looking for doesn't exist."
          />
        }
      />
    </Routes>
  )
}
