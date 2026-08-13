import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { SWRConfig } from 'swr'
import './index.css'
import { App } from './App'
import { AuthProvider } from './context/AuthContext'
import { UIProvider } from './context/UIContext'
import { ToastViewport } from './components/ui/ToastViewport'

const root = document.getElementById('root')
if (!root) throw new Error('Root element #root not found')

createRoot(root).render(
  <StrictMode>
    {/* SWR: dedupe identical GETs and serve from cache; revalidate on focus off. */}
    <SWRConfig value={{ revalidateOnFocus: false, dedupingInterval: 5000 }}>
      <BrowserRouter>
        <AuthProvider>
          <UIProvider>
            <App />
            <ToastViewport />
          </UIProvider>
        </AuthProvider>
      </BrowserRouter>
    </SWRConfig>
  </StrictMode>,
)
