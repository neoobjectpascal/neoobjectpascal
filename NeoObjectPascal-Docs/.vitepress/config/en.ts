import type { DefaultTheme, LocaleSpecificConfig } from 'vitepress'
import { buildSidebar, buildNav } from './sidebar'

const base = '/en'

export const enConfig: LocaleSpecificConfig<DefaultTheme.Config> = {
  label: 'English',
  lang: 'en-US',
  title: 'NeoObjectPascal',
  description: 'The modern Pascal language with object orientation, native testing and Java integration.',
  themeConfig: {
    nav: buildNav(base, {
      guide: 'Guide',
      reference: 'Reference',
      examples: 'Examples'
    }),
    sidebar: buildSidebar(base, {
      gettingStarted: 'Getting Started',
      language: 'Language',
      oop: 'Object Orientation',
      features: 'Features',
      terminalink: 'TerminalInk',
      webink: 'WebInk',
      testing: 'Testing & Tools',
      reference: 'Reference',
      introduction: 'Introduction',
      aboutAuthor: 'About the author',
      installation: 'Installation & first program',
      programStructure: 'Program structure',
      variablesAndTypes: 'Variables and types',
      operators: 'Operators',
      controlFlow: 'Control flow',
      functions: 'Functions and procedures',
      arrays: 'Arrays',
      errorHandling: 'Error handling',
      classes: 'Classes and objects',
      inheritancePolymorphism: 'Inheritance and polymorphism',
      interfaces: 'Interfaces',
      functional: 'Functional programming',
      dataParsing: 'Data parsing (JSON/CSV)',
      http: 'HTTP requests',
      io: 'Input and output',
      javaIntegration: 'Java integration',
      modules: 'Modules and uses',
      internalLibraries: 'Internal libraries',
      datesAndCurrency: 'Dates, times & currency',
      uiBuilder: 'Visual UI Builder (.xnpas)',
      terminalinkIntro: 'Introduction',
      terminalinkComponents: 'Components',
      terminalinkTheming: 'Theming',
      webinkIntro: 'Introduction',
      webinkComponents: 'Components',
      unitTesting: 'Unit testing and mocking',
      debuggingTools: 'Debugger, VS Code & cloud',
      buildingExecutables: 'Building native executables',
      languageReference: 'Language reference',
      examples: 'Examples gallery'
    }),
    docFooter: { prev: 'Previous', next: 'Next' },
    outline: { label: 'On this page' }
  }
}
