import { AppShell } from './components/layout/AppShell'
import { AppRoutes } from './router/routes'

export function App() {
  return (
    <AppShell>
      <AppRoutes />
    </AppShell>
  )
}
