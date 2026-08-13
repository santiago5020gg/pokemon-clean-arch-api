import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { UIProvider, useToast } from './UIContext'

function Probe() {
  const { toasts, notify, dismiss } = useToast()
  return (
    <div>
      <button onClick={() => notify('Saved!', 'success')}>notify</button>
      <ul>
        {toasts.map((t) => (
          <li key={t.id}>
            {t.variant}:{t.message}
            <button onClick={() => dismiss(t.id)}>x{t.id}</button>
          </li>
        ))}
      </ul>
    </div>
  )
}

function renderProbe() {
  return render(
    <UIProvider>
      <Probe />
    </UIProvider>,
  )
}

describe('UIContext toasts', () => {
  it('pushes a toast with its variant on notify', async () => {
    renderProbe()
    await userEvent.click(screen.getByRole('button', { name: 'notify' }))
    expect(screen.getByText('success:Saved!')).toBeInTheDocument()
  })

  it('removes a toast on dismiss', async () => {
    renderProbe()
    await userEvent.click(screen.getByRole('button', { name: 'notify' }))
    await userEvent.click(screen.getByRole('button', { name: /^x/ }))
    expect(screen.queryByText('success:Saved!')).not.toBeInTheDocument()
  })

  it('throws when used outside the provider', () => {
    expect(() => render(<Probe />)).toThrow(/UIProvider/)
  })
})
