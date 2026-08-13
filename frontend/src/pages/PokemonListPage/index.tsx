import { useMemo, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { usePokemonList } from '../../hooks/usePokemonList'
import { useAuth } from '../../context/AuthContext'
import { useToast } from '../../context/UIContext'
import { syncPokemon } from '../../api/pokemon'
import { ApiError } from '../../api/client'
import { PokemonGrid } from '../../components/pokemon/PokemonGrid'
import { Pagination } from '../../components/ui/Pagination'
import { Spinner } from '../../components/ui/Spinner'
import { EmptyState } from '../../components/ui/EmptyState'
import { ErrorState } from '../../components/ui/ErrorState'
import { Button } from '../../components/ui/Button'
import { Input } from '../../components/ui/Input'
import { Icon } from '../../components/ui/Icon'

const PAGE_SIZE = 20

/** Replication count bounds (mirrors the backend cap of Gen 1 = 151). */
const DEFAULT_COUNT = 100
const MIN_COUNT = 1
const MAX_COUNT = 151

function clampCount(raw: string): number {
  const parsed = Number.parseInt(raw, 10)
  if (Number.isNaN(parsed)) return DEFAULT_COUNT
  return Math.min(Math.max(parsed, MIN_COUNT), MAX_COUNT)
}

/** US01 list container + US03 replication trigger. */
export function PokemonListPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const page = Math.max(0, Number(searchParams.get('page') ?? '0'))
  const { page: data, error, isLoading, refresh } = usePokemonList(page, PAGE_SIZE)
  const navigate = useNavigate()
  const { isAuthenticated } = useAuth()
  const { notify } = useToast()

  const [filter, setFilter] = useState('')
  const [syncing, setSyncing] = useState(false)
  const [count, setCount] = useState(String(DEFAULT_COUNT))

  const visible = useMemo(() => {
    const all = data?.content ?? []
    const q = filter.trim().toLowerCase()
    return q ? all.filter((p) => p.name.toLowerCase().includes(q)) : all
  }, [data, filter])

  function goToPage(next: number) {
    setSearchParams({ page: String(next) })
  }

  async function handleSync() {
    if (!isAuthenticated) {
      notify('Sign in to replicate Pokémon', 'info')
      navigate('/login', { state: { from: '/' } })
      return
    }
    setSyncing(true)
    try {
      const result = await syncPokemon({ limit: clampCount(count), offset: 0 })
      notify(`Replicated ${result.synced} Pokémon (${result.created} new)`, 'success')
      if (page !== 0) setSearchParams({ page: '0' })
      await refresh()
    } catch (err) {
      const message = err instanceof ApiError ? err.message : 'Sync failed'
      notify(message, 'error')
    } finally {
      setSyncing(false)
    }
  }

  return (
    <div className="flex flex-col gap-8">
      <section className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h1 className="font-display text-4xl font-bold tracking-tight text-slate-50 sm:text-5xl">
            Explore the Pokédex
          </h1>
          <p className="mt-2 max-w-lg text-slate-400">
            Browse replicated Pokémon, inspect their stats and lineage, and tailor your own regional
            data.
          </p>
        </div>
        <div className="flex flex-wrap items-end gap-3">
          {isAuthenticated && (
            <div className="w-32">
              <Input
                label="How many to replicate"
                type="number"
                min={MIN_COUNT}
                max={MAX_COUNT}
                value={count}
                onChange={(e) => setCount(e.target.value)}
              />
            </div>
          )}
          <Button onClick={handleSync} loading={syncing}>
            <Icon name="sparkles" size={16} />
            Replicate from PokeAPI
          </Button>
        </div>
      </section>

      {data && data.content.length > 0 && (
        <div className="max-w-xs">
          <Input
            label="Filter this page"
            placeholder="Search by name…"
            value={filter}
            onChange={(e) => setFilter(e.target.value)}
          />
        </div>
      )}

      {isLoading && (
        <div className="flex justify-center py-24 text-neon-violet">
          <Spinner size={40} label="Loading Pokémon" />
        </div>
      )}

      {error && !isLoading && (
        <ErrorState
          message="We couldn't reach the Pokédex service. Check that the backend is running."
          onRetry={() => void refresh()}
        />
      )}

      {data && data.content.length === 0 && !isLoading && (
        <EmptyState
          icon="pokeball"
          title="No Pokémon yet"
          message="Replicate data from the PokeAPI to populate your local Pokédex."
          action={
            <Button onClick={handleSync} loading={syncing}>
              <Icon name="sparkles" size={16} />
              Replicate now
            </Button>
          }
        />
      )}

      {data && data.content.length > 0 && visible.length === 0 && (
        <EmptyState
          icon="search"
          title="No matches on this page"
          message={`Nothing named "${filter}" here. Try another page or clear the filter.`}
        />
      )}

      {visible.length > 0 && (
        <>
          <PokemonGrid pokemon={visible} onSelect={(id) => navigate(`/pokemon/${id}`)} />
          {data && data.totalPages > 1 && (
            <Pagination page={page} totalPages={data.totalPages} onChange={goToPage} />
          )}
        </>
      )}
    </div>
  )
}
