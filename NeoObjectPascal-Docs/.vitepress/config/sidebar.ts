import type { DefaultTheme } from 'vitepress'

// Sidebar section + page labels for a single locale.
// Directory slugs are shared across all locales (English); only labels/content are translated.
export interface SidebarLabels {
  // sections
  gettingStarted: string
  language: string
  oop: string
  features: string
  terminalink: string
  webink: string
  testing: string
  reference: string
  // getting-started
  introduction: string
  installation: string
  programStructure: string
  // language
  variablesAndTypes: string
  operators: string
  controlFlow: string
  functions: string
  arrays: string
  errorHandling: string
  // oop
  classes: string
  inheritancePolymorphism: string
  interfaces: string
  // features
  functional: string
  dataParsing: string
  http: string
  io: string
  javaIntegration: string
  modules: string
  internalLibraries: string
  datesAndCurrency: string
  uiBuilder: string
  // terminalink
  terminalinkIntro: string
  terminalinkComponents: string
  terminalinkTheming: string
  // webink
  webinkIntro: string
  webinkComponents: string
  // testing & tools
  unitTesting: string
  debuggingTools: string
  buildingExecutables: string
  // reference
  languageReference: string
  examples: string
}

// `base` is '' for the root locale (pt) or '/en', '/de', '/fr', '/it'.
export function buildSidebar(base: string, l: SidebarLabels): DefaultTheme.SidebarItem[] {
  const p = (slug: string) => `${base}/${slug}`
  return [
    {
      text: l.gettingStarted,
      collapsed: false,
      items: [
        { text: l.introduction, link: p('getting-started/introduction') },
        { text: l.installation, link: p('getting-started/installation') },
        { text: l.programStructure, link: p('getting-started/program-structure') }
      ]
    },
    {
      text: l.language,
      collapsed: false,
      items: [
        { text: l.variablesAndTypes, link: p('language/variables-and-types') },
        { text: l.operators, link: p('language/operators') },
        { text: l.controlFlow, link: p('language/control-flow') },
        { text: l.functions, link: p('language/functions') },
        { text: l.arrays, link: p('language/arrays') },
        { text: l.errorHandling, link: p('language/error-handling') }
      ]
    },
    {
      text: l.oop,
      collapsed: false,
      items: [
        { text: l.classes, link: p('oop/classes') },
        { text: l.inheritancePolymorphism, link: p('oop/inheritance-polymorphism') },
        { text: l.interfaces, link: p('oop/interfaces') }
      ]
    },
    {
      text: l.features,
      collapsed: false,
      items: [
        { text: l.functional, link: p('features/functional') },
        { text: l.dataParsing, link: p('features/data-parsing') },
        { text: l.http, link: p('features/http') },
        { text: l.io, link: p('features/io') },
        { text: l.javaIntegration, link: p('features/java-integration') },
        { text: l.modules, link: p('features/modules') },
        { text: l.internalLibraries, link: p('features/internal-libraries') },
        { text: l.datesAndCurrency, link: p('features/dates-and-currency') },
        { text: l.uiBuilder, link: p('features/ui-builder') }
      ]
    },
    {
      text: l.terminalink,
      collapsed: false,
      items: [
        { text: l.terminalinkIntro, link: p('terminalink/introduction') },
        { text: l.terminalinkComponents, link: p('terminalink/components') },
        { text: l.terminalinkTheming, link: p('terminalink/theming') }
      ]
    },
    {
      text: l.webink,
      collapsed: false,
      items: [
        { text: l.webinkIntro, link: p('webink/introduction') },
        { text: l.webinkComponents, link: p('webink/components') }
      ]
    },
    {
      text: l.testing,
      collapsed: false,
      items: [
        { text: l.unitTesting, link: p('testing/unit-testing') },
        { text: l.debuggingTools, link: p('testing/debugging-tools') },
        { text: l.buildingExecutables, link: p('testing/building-executables') }
      ]
    },
    {
      text: l.reference,
      collapsed: false,
      items: [
        { text: l.languageReference, link: p('reference/language-reference') },
        { text: l.examples, link: p('reference/examples') }
      ]
    }
  ]
}

export interface NavLabels {
  guide: string
  reference: string
  examples: string
}

export function buildNav(base: string, n: NavLabels): DefaultTheme.NavItem[] {
  return [
    { text: n.guide, link: `${base}/getting-started/introduction` },
    { text: n.reference, link: `${base}/reference/language-reference` },
    { text: n.examples, link: `${base}/reference/examples` }
  ]
}
