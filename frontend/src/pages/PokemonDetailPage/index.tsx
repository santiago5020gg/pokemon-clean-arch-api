import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { usePokemonDetail } from '../../hooks/usePokemonDetail'
import { useAuth } from '../../context/AuthContext'
import { useToast } from '../../context/UIContext'
import { updatePokemon, deletePokemon } from '../../api/pokemon'
import { ApiError } from '../../api/client'
import type { PokemonUpdateRequest } from '../../api/types'
import { StatsPanel } from '../../components/pokemon/StatsPanel'
import { EvolutionList } from '../../components/pokemon/EvolutionList'
import { EditForm } from '../../components/pokemon/EditForm'
import { GlassCard } from '../../components/ui/GlassCard'
import { Spinner } from '../../components/ui/Spinner'
import { ErrorState } from '../../components/ui/ErrorState'
import { Button } from '../../components/ui/Button'
import { Icon } from '../../components/ui/Icon'
import { formatDexId, titleCase } from '../../lib/format'

/** US02 detail container + US04 edit + delete. */
export function PokemonDetailPage() {
  const params = useParams()
  const id = Number(params.id)
  const { pokemon, error, isLoading, refresh } = usePokemonDetail(Number.isNaN(id) ? null : id)
  const navigate = useNavigate()
  const { isAuthenticated } = useAuth()
  const { notify } = useToast()

  const [editing, setEditing] = useState(false)
  const [saving, setSaving] = useState(false)
  const [fieldErrors, setFieldErrors] = useState<Array<{ field: string; message: string }>>([])
  const [confirmingDelete, setConfirmingDelete] = useState(false)
  const [deleting, setDeleting] = useState(false)

  async function handleUpdate(values: PokemonUpdateRequest) {
    setSaving(true)
    setFieldErrors([])
    try {
      await updatePokemon(id, values)
      await refresh()
      setEditing(false)
      notify('Pokémon updated', 'success')
    } catch (err) {
      if (err instanceof ApiError && err.fieldErrors.length > 0) {
        setFieldErrors(err.fieldErrors)
      } else {
        notify(err instanceof ApiError ? err.message : 'Update failed', 'error')
      }
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete() {
    setDeleting(true)
    try {
      await deletePokemon(id)
      notify('Pokémon deleted', 'success')
      navigate('/')
    } catch (err) {
      notify(err instanceof ApiError ? err.message : 'Delete failed', 'error')
      setDeleting(false)
    }
  }

  if (isLoading) {
    return (
      <div className="flex justify-center py-24 text-neon-violet">
        <Spinner size={40} label="Loading Pokémon" />
      </div>
    )
  }

  if (error) {
    const notFound = error instanceof ApiError && error.status === 404
    return (
      <ErrorState
        title={notFound ? 'Pokémon not found' : 'Something went wrong'}
        message={
          notFound
            ? 'This Pokémon is not in your local Pokédex. Try replicating it first.'
            : "We couldn't load this Pokémon."
        }
        onRetry={notFound ? undefined : () => void refresh()}
      />
    )
  }

  if (!pokemon) return null

  return (
    <article className="flex flex-col gap-8">
      <Link
        to="/"
        className="inline-flex w-fit items-center gap-2 text-sm text-slate-400 transition-colors hover:text-slate-100"
      >
        <Icon name="arrow-left" size={16} />
        Back to Pokédex
      </Link>

      <div className="grid gap-6 lg:grid-cols-[minmax(0,1fr)_1.2fr]">
        {/* Portrait */}
        <GlassCard className="relative flex flex-col items-center justify-center overflow-hidden py-10">
          <div
            aria-hidden="true"
            className="absolute -top-10 h-48 w-48 rounded-full bg-neon-violet/20 blur-3xl"
          />
          <span className="relative z-10 font-mono text-sm text-slate-400">{formatDexId(pokemon.id)}</span>
          <img
            src={pokemon.imageUrl}
            alt={titleCase(pokemon.name)}
            className="relative z-10 h-56 w-56 animate-float object-contain drop-shadow-[0_16px_40px_rgba(124,92,255,0.4)]"
          />
          <h1 className="relative z-10 mt-2 font-display text-3xl font-bold text-slate-50">
            {titleCase(pokemon.localizedName || pokemon.name)}
          </h1>
          {pokemon.region && (
            <p className="relative z-10 text-sm text-neon-cyan">{pokemon.region}</p>
          )}
        </GlassCard>

        {/* Facts */}
        <div className="flex flex-col gap-6">
          <GlassCard>
            <h2 className="mb-3 font-display text-lg font-semibold text-slate-100">Description</h2>
            <p className="text-sm leading-relaxed text-slate-300">{pokemon.description}</p>
          </GlassCard>

          <GlassCard>
            <h2 className="mb-4 font-display text-lg font-semibold text-slate-100">Base stats</h2>
            <StatsPanel stats={pokemon.stats} />
          </GlassCard>

          <GlassCard>
            <h2 className="mb-4 font-display text-lg font-semibold text-slate-100">Evolutions</h2>
            <EvolutionList evolutions={pokemon.evolutions} current={pokemon.name} />
          </GlassCard>
        </div>
      </div>

      {/* Proprietary data + owner actions */}
      <GlassCard>
        <div className="mb-4 flex items-center justify-between">
          <h2 className="font-display text-lg font-semibold text-slate-100">Regional data</h2>
          {isAuthenticated && !editing && (
            <div className="flex gap-2">
              <Button variant="ghost" onClick={() => setEditing(true)}>
                <Icon name="edit" size={16} />
                Edit
              </Button>
              {!confirmingDelete ? (
                <Button variant="danger" onClick={() => setConfirmingDelete(true)}>
                  <Icon name="trash" size={16} />
                  Delete
                </Button>
              ) : (
                <>
                  <Button variant="ghost" onClick={() => setConfirmingDelete(false)}>
                    Cancel
                  </Button>
                  <Button variant="danger" onClick={handleDelete} loading={deleting}>
                    Confirm delete
                  </Button>
                </>
              )}
            </div>
          )}
        </div>

        {editing ? (
          <EditForm
            initial={{
              localizedName: pokemon.localizedName,
              region: pokemon.region,
              internalTags: pokemon.internalTags,
            }}
            onSubmit={handleUpdate}
            onCancel={() => {
              setEditing(false)
              setFieldErrors([])
            }}
            submitting={saving}
            serverErrors={fieldErrors}
          />
        ) : (
          <dl className="grid gap-4 sm:grid-cols-2">
            <div>
              <dt className="text-xs uppercase tracking-wide text-slate-500">Localized name</dt>
              <dd className="mt-1 text-slate-100">{pokemon.localizedName || '—'}</dd>
            </div>
            <div>
              <dt className="text-xs uppercase tracking-wide text-slate-500">Region</dt>
              <dd className="mt-1 text-slate-100">{pokemon.region || '—'}</dd>
            </div>
            <div className="sm:col-span-2">
              <dt className="text-xs uppercase tracking-wide text-slate-500">Internal tags</dt>
              <dd className="mt-2 flex flex-wrap gap-2">
                {pokemon.internalTags.length > 0 ? (
                  pokemon.internalTags.map((tag) => (
                    <span
                      key={tag}
                      className="rounded-full border border-neon-violet/30 bg-neon-violet/10 px-3 py-1 text-xs text-neon-violet"
                    >
                      {tag}
                    </span>
                  ))
                ) : (
                  <span className="text-slate-500">No tags</span>
                )}
              </dd>
            </div>
          </dl>
        )}

        {!isAuthenticated && (
          <p className="mt-4 text-sm text-slate-500">
            <Link to="/login" className="text-neon-cyan hover:underline">
              Sign in
            </Link>{' '}
            to edit or delete this Pokémon.
          </p>
        )}
      </GlassCard>
    </article>
  )
}
