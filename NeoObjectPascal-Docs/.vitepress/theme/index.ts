import DefaultTheme from 'vitepress/theme'
import type { Theme } from 'vitepress'
import Output from './components/Output.vue'
import './custom.css'

// Extends the default VitePress theme with the NeoObjectPascal brand styling
// and a global <Output> component for showing expected program output.
export default {
  extends: DefaultTheme,
  enhanceApp({ app }) {
    app.component('Output', Output)
  }
} satisfies Theme
