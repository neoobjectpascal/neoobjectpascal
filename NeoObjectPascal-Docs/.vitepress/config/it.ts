import type { DefaultTheme, LocaleSpecificConfig } from 'vitepress'
import { buildSidebar, buildNav } from './sidebar'

const base = '/it'

export const itConfig: LocaleSpecificConfig<DefaultTheme.Config> = {
  label: 'Italiano',
  lang: 'it-IT',
  title: 'NeoObjectPascal',
  description: 'Il linguaggio Pascal moderno con orientamento agli oggetti, test nativi e integrazione Java.',
  themeConfig: {
    nav: buildNav(base, {
      guide: 'Guida',
      reference: 'Riferimento',
      examples: 'Esempi'
    }),
    sidebar: buildSidebar(base, {
      gettingStarted: 'Per iniziare',
      language: 'Linguaggio',
      oop: 'Orientamento agli oggetti',
      features: 'Funzionalità',
      terminalink: 'TerminalInk',
      webink: 'WebInk',
      testing: 'Test e strumenti',
      reference: 'Riferimento',
      introduction: 'Introduzione',
      aboutAuthor: 'L’autore',
      installation: 'Installazione e primo programma',
      programStructure: 'Struttura di un programma',
      variablesAndTypes: 'Variabili e tipi',
      operators: 'Operatori',
      controlFlow: 'Strutture di controllo',
      functions: 'Funzioni e procedure',
      arrays: 'Array',
      errorHandling: 'Gestione degli errori',
      classes: 'Classi e oggetti',
      inheritancePolymorphism: 'Ereditarietà e polimorfismo',
      interfaces: 'Interfacce',
      functional: 'Programmazione funzionale',
      dataParsing: 'Elaborazione dati (JSON/CSV)',
      http: 'Richieste HTTP',
      io: 'Input e output',
      javaIntegration: 'Integrazione Java',
      modules: 'Moduli e uses',
      internalLibraries: 'Librerie interne',
      datesAndCurrency: 'Date, ore e valuta',
      uiBuilder: 'Editor visivo (.xnpas)',
      terminalinkIntro: 'Introduzione',
      terminalinkComponents: 'Componenti',
      terminalinkTheming: 'Temi',
      webinkIntro: 'Introduzione',
      webinkComponents: 'Componenti',
      unitTesting: 'Test unitari e mocking',
      debuggingTools: 'Debugger, VS Code e cloud',
      buildingExecutables: 'Creare eseguibili nativi',
      languageReference: 'Riferimento del linguaggio',
      examples: 'Galleria di esempi'
    }),
    docFooter: { prev: 'Precedente', next: 'Successivo' },
    outline: { label: 'In questa pagina' }
  }
}
