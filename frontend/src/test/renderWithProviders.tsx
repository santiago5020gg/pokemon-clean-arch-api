import type { ReactElement, ReactNode } from 'react'
import { render } from '@testing-library/react'
import { MemoryRouter, Routes, Route } from 'react-router-dom'
import { SWRConfig } from 'swr'
import { AuthProvider } from '../context/AuthContext'
import { UIProvider } from '../context/UIContext'
import { ToastViewport } from '../components/ui/ToastViewport'

interface Options {
  /** Initial URL. */
  route?: string
  /** Route pattern for the element under test (e.g. "/pokemon/:id"). */
  path?: string
  /** Optional extra routes to enable navigation assertions. */
  extraRoutes?: ReactNode
}

/**
 * Render a page with the full provider stack and a real (in-memory) router, so
 * tests exercise behavior the way a user experiences it.
 */
export function renderWithProviders(ui: ReactElement, options: Options = {}) {
  const { route = '/', path = '/', extraRoutes } = options
  return render(
    <SWRConfig value={{ provider: () => new Map(), dedupingInterval: 0 }}>
      <MemoryRouter initialEntries={[route]}>
        <AuthProvider>
          <UIProvider>
            <Routes>
              <Route path={path} element={ui} />
              {extraRoutes}
            </Routes>
            <ToastViewport />
          </UIProvider>
        </AuthProvider>
      </MemoryRouter>
    </SWRConfig>,
  )
}
