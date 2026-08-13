import { useState, type FormEvent } from 'react'
import type { PokemonUpdateRequest } from '../../api/types'
import { Input } from '../ui/Input'
import { Button } from '../ui/Button'

interface EditFormProps {
  /**
   * Current proprietary values. A freshly replicated Pokémon (US03) has null
   * localizedName/region until it is edited (US04), so both may be null here.
   */
  initial: { localizedName: string | null; region: string | null; internalTags: string[] }
  onSubmit: (values: PokemonUpdateRequest) => void
  onCancel: () => void
  submitting?: boolean
  /** Field errors returned by the backend (400). */
  serverErrors?: Array<{ field: string; message: string }>
}

type Errors = Partial<Record<'localizedName' | 'region' | 'internalTags', string>>

// Mirrors the backend Bean Validation on PokemonUpdateRequest.
function validate(localizedName: string, region: string, tags: string[]): Errors {
  const errors: Errors = {}
  if (!localizedName.trim()) errors.localizedName = 'Localized name is required'
  else if (localizedName.length > 120) errors.localizedName = 'Max 120 characters'
  if (!region.trim()) errors.region = 'Region is required'
  else if (region.length > 120) errors.region = 'Max 120 characters'
  if (tags.some((t) => t.length > 50)) errors.internalTags = 'Each tag must be 50 characters or fewer'
  return errors
}

function parseTags(raw: string): string[] {
  return raw
    .split(',')
    .map((t) => t.trim())
    .filter(Boolean)
}

/** Edit the proprietary fields of a Pokémon (US04). */
export function EditForm({ initial, onSubmit, onCancel, submitting = false, serverErrors }: EditFormProps) {
  const [localizedName, setLocalizedName] = useState(initial.localizedName ?? '')
  const [region, setRegion] = useState(initial.region ?? '')
  const [tagsRaw, setTagsRaw] = useState(initial.internalTags.join(', '))
  const [errors, setErrors] = useState<Errors>({})

  const serverFor = (field: string) => serverErrors?.find((e) => e.field === field)?.message

  function handleSubmit(event: FormEvent) {
    event.preventDefault()
    const tags = parseTags(tagsRaw)
    const found = validate(localizedName, region, tags)
    setErrors(found)
    if (Object.keys(found).length > 0) return
    onSubmit({ localizedName: localizedName.trim(), region: region.trim(), internalTags: tags })
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-4" noValidate>
      <Input
        label="Localized name"
        value={localizedName}
        onChange={(e) => setLocalizedName(e.target.value)}
        error={errors.localizedName ?? serverFor('localizedName')}
      />
      <Input
        label="Region"
        value={region}
        onChange={(e) => setRegion(e.target.value)}
        error={errors.region ?? serverFor('region')}
      />
      <Input
        label="Internal tags"
        value={tagsRaw}
        placeholder="starter, grass, kanto"
        onChange={(e) => setTagsRaw(e.target.value)}
        error={errors.internalTags ?? serverFor('internalTags')}
      />
      <p className="-mt-2 text-xs text-slate-500">Separate tags with commas.</p>
      <div className="flex justify-end gap-3">
        <Button type="button" variant="ghost" onClick={onCancel}>
          Cancel
        </Button>
        <Button type="submit" loading={submitting}>
          Save changes
        </Button>
      </div>
    </form>
  )
}
