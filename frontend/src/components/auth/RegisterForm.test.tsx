import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { RegisterForm } from './RegisterForm'

describe('RegisterForm', () => {
  it('validates username length, email format and password length', async () => {
    const onSubmit = vi.fn()
    render(<RegisterForm onSubmit={onSubmit} />)
    await userEvent.type(screen.getByLabelText('Username'), 'ab')
    await userEvent.type(screen.getByLabelText('Email'), 'not-an-email')
    await userEvent.type(screen.getByLabelText('Password'), '123')
    await userEvent.click(screen.getByRole('button', { name: 'Create account' }))
    expect(screen.getByText('At least 3 characters')).toBeInTheDocument()
    expect(screen.getByText('Enter a valid email')).toBeInTheDocument()
    expect(screen.getByText('At least 6 characters')).toBeInTheDocument()
    expect(onSubmit).not.toHaveBeenCalled()
  })

  it('submits a valid registration', async () => {
    const onSubmit = vi.fn()
    render(<RegisterForm onSubmit={onSubmit} />)
    await userEvent.type(screen.getByLabelText('Username'), 'ash')
    await userEvent.type(screen.getByLabelText('Email'), 'ash@pallet.town')
    await userEvent.type(screen.getByLabelText('Password'), 'pikapika')
    await userEvent.click(screen.getByRole('button', { name: 'Create account' }))
    expect(onSubmit).toHaveBeenCalledWith({
      username: 'ash',
      email: 'ash@pallet.town',
      password: 'pikapika',
    })
  })
})
