import { createRequire } from 'node:module'
import type { DefaultTheme, UserConfig } from 'vitepress'

const require = createRequire(import.meta.url)

// Reuse the TextMate grammar shipped with the VS Code extension so `.npas`
// code fences get real NeoObjectPascal highlighting (scope: source.neoobjectpascal).
const neoGrammar = require('../shiki/neoobjectpascal.tmLanguage.json')

const neoLang = {
  ...neoGrammar,
  name: 'npas',
  aliases: ['neopascal', 'neoobjectpascal', 'neo-object-pascal']
}

// GitHub URL of the monorepo — adjust if the canonical remote changes.
export const REPO_URL = 'https://github.com/alvaro-brito/NeoObjectPascal-Monorepo'

export const sharedConfig: UserConfig<DefaultTheme.Config> = {
  // The `.npas` slug is what authors type after ``` in fences.
  markdown: {
    languages: [neoLang as any],
    theme: {
      light: 'github-light',
      dark: 'github-dark'
    },
    lineNumbers: false
  },

  cleanUrls: true,
  metaChunk: true,
  lastUpdated: true,
  // Strict dead-link checking, EXCEPT illustrative localhost URLs that appear in
  // sample terminal output (e.g. cloud execution links). Everything else must resolve.
  ignoreDeadLinks: [/^https?:\/\/localhost/],

  head: [
    ['link', { rel: 'icon', href: '/favicon.svg', type: 'image/svg+xml' }],
    ['meta', { name: 'theme-color', content: '#e6a817' }],
    ['meta', { property: 'og:type', content: 'website' }],
    ['meta', { property: 'og:image', content: '/og-image.svg' }]
  ],

  themeConfig: {
    logo: '/logo.svg',
    socialLinks: [{ icon: 'github', link: REPO_URL }],
    search: {
      provider: 'local'
    }
  }
}
