import type { ReactElement, SVGProps } from 'react'

export type IconName =
  | 'search'
  | 'chevron-left'
  | 'chevron-right'
  | 'arrow-left'
  | 'close'
  | 'sparkles'
  | 'edit'
  | 'trash'
  | 'logout'
  | 'login'
  | 'alert'
  | 'inbox'
  | 'pokeball'
  | 'check'

const PATHS: Record<IconName, ReactElement> = {
  search: (
    <>
      <circle cx="11" cy="11" r="7" />
      <path d="m20 20-3.5-3.5" />
    </>
  ),
  'chevron-left': <path d="m15 6-6 6 6 6" />,
  'chevron-right': <path d="m9 6 6 6-6 6" />,
  'arrow-left': <path d="M19 12H5m0 0 6-6m-6 6 6 6" />,
  close: <path d="M6 6l12 12M18 6L6 18" />,
  sparkles: (
    <>
      <path d="M12 3v6m0 6v6m-9-9h6m6 0h6" />
      <path d="m6.5 6.5 2 2m7 7 2 2m0-11-2 2m-7 7-2 2" />
    </>
  ),
  edit: (
    <>
      <path d="M4 20h4L18.5 9.5a2.1 2.1 0 0 0-3-3L5 17v3Z" />
      <path d="M13.5 6.5l3 3" />
    </>
  ),
  trash: (
    <>
      <path d="M4 7h16" />
      <path d="M9 7V5a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2" />
      <path d="M6 7l1 12a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1l1-12" />
    </>
  ),
  logout: (
    <>
      <path d="M15 4h3a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1h-3" />
      <path d="M10 12H3m0 0 3-3m-3 3 3 3" />
    </>
  ),
  login: (
    <>
      <path d="M9 4H6a1 1 0 0 0-1 1v14a1 1 0 0 0 1 1h3" />
      <path d="M14 12h7m0 0-3-3m3 3-3 3" />
    </>
  ),
  alert: (
    <>
      <path d="M12 3 2 20h20L12 3Z" />
      <path d="M12 10v4m0 3h.01" />
    </>
  ),
  inbox: (
    <>
      <path d="M4 13v6a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1v-6l-3-8H7l-3 8Z" />
      <path d="M4 13h5l1 2h4l1-2h5" />
    </>
  ),
  pokeball: (
    <>
      <circle cx="12" cy="12" r="9" />
      <path d="M3 12h6m6 0h6" />
      <circle cx="12" cy="12" r="2.5" />
    </>
  ),
  check: <path d="m5 12 5 5L20 7" />,
}

interface IconProps extends SVGProps<SVGSVGElement> {
  name: IconName
  size?: number
}

/** Single-stroke icon system. All icons share stroke width and line caps. */
export function Icon({ name, size = 20, ...rest }: IconProps) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth={1.8}
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
      {...rest}
    >
      {PATHS[name]}
    </svg>
  )
}
