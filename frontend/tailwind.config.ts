import type { Config } from 'tailwindcss'

/**
 * Glassmorphism-neon design system. Dark-first: deep space background, frosted
 * glass surfaces, neon accents. Type-based gradients live in `lib/pokemonTypes`.
 */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        // Space background scale (near-black indigo)
        void: {
          900: '#050614',
          800: '#0a0b1e',
          700: '#11132e',
          600: '#1a1c3d',
        },
        // Neon accents
        neon: {
          violet: '#7c5cff',
          indigo: '#5b6bff',
          cyan: '#22d3ee',
          pink: '#f472b6',
          lime: '#a3e635',
        },
      },
      fontFamily: {
        display: ['"Space Grotesk"', 'system-ui', 'sans-serif'],
        sans: ['Inter', 'system-ui', 'sans-serif'],
        mono: ['"JetBrains Mono"', 'ui-monospace', 'monospace'],
      },
      backgroundImage: {
        'radial-glow':
          'radial-gradient(circle at 20% 0%, rgba(124,92,255,0.18), transparent 45%), radial-gradient(circle at 80% 20%, rgba(34,211,238,0.14), transparent 40%)',
      },
      boxShadow: {
        glass: '0 8px 32px -8px rgba(0,0,0,0.6), inset 0 1px 0 0 rgba(255,255,255,0.06)',
        'neon-violet': '0 0 24px -4px rgba(124,92,255,0.6)',
        'neon-cyan': '0 0 24px -4px rgba(34,211,238,0.55)',
      },
      backdropBlur: {
        xs: '2px',
      },
      keyframes: {
        float: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-8px)' },
        },
        'glow-pulse': {
          '0%, 100%': { opacity: '0.6' },
          '50%': { opacity: '1' },
        },
        shimmer: {
          '100%': { transform: 'translateX(100%)' },
        },
        'fade-up': {
          '0%': { opacity: '0', transform: 'translateY(12px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        spin: {
          to: { transform: 'rotate(360deg)' },
        },
      },
      animation: {
        float: 'float 6s ease-in-out infinite',
        'glow-pulse': 'glow-pulse 2.4s ease-in-out infinite',
        shimmer: 'shimmer 1.6s infinite',
        'fade-up': 'fade-up 0.5s ease-out both',
        spin: 'spin 0.7s linear infinite',
      },
    },
  },
  plugins: [],
} satisfies Config
