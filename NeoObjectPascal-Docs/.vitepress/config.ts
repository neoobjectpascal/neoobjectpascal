import { defineConfig } from 'vitepress'
import { sharedConfig } from './config/shared'
import { ptConfig } from './config/pt'
import { enConfig } from './config/en'
import { deConfig } from './config/de'
import { frConfig } from './config/fr'
import { itConfig } from './config/it'

// Portuguese is the canonical/root locale (served at `/`); the others live under
// `/en`, `/de`, `/fr`, `/it`. The language dropdown is generated automatically.
export default defineConfig({
  ...sharedConfig,
  locales: {
    root: ptConfig,
    en: enConfig,
    de: deConfig,
    fr: frConfig,
    it: itConfig
  }
})
