import type { DefaultTheme, LocaleSpecificConfig } from 'vitepress'
import { buildSidebar, buildNav } from './sidebar'

const base = '/de'

export const deConfig: LocaleSpecificConfig<DefaultTheme.Config> = {
  label: 'Deutsch',
  lang: 'de-DE',
  title: 'NeoObjectPascal',
  description: 'Die moderne Pascal-Sprache mit Objektorientierung, nativem Testing und Java-Integration.',
  themeConfig: {
    nav: buildNav(base, {
      guide: 'Handbuch',
      reference: 'Referenz',
      examples: 'Beispiele'
    }),
    sidebar: buildSidebar(base, {
      gettingStarted: 'Erste Schritte',
      language: 'Sprache',
      oop: 'Objektorientierung',
      features: 'Funktionen',
      terminalink: 'TerminalInk',
      webink: 'WebInk',
      testing: 'Testen & Werkzeuge',
      reference: 'Referenz',
      introduction: 'Einführung',
      installation: 'Installation & erstes Programm',
      programStructure: 'Programmstruktur',
      variablesAndTypes: 'Variablen und Typen',
      operators: 'Operatoren',
      controlFlow: 'Kontrollstrukturen',
      functions: 'Funktionen und Prozeduren',
      arrays: 'Arrays',
      errorHandling: 'Fehlerbehandlung',
      classes: 'Klassen und Objekte',
      inheritancePolymorphism: 'Vererbung und Polymorphie',
      interfaces: 'Schnittstellen',
      functional: 'Funktionale Programmierung',
      dataParsing: 'Datenverarbeitung (JSON/CSV)',
      http: 'HTTP-Anfragen',
      io: 'Ein- und Ausgabe',
      javaIntegration: 'Java-Integration',
      modules: 'Module und uses',
      internalLibraries: 'Interne Bibliotheken',
      datesAndCurrency: 'Datum, Zeit & Währung',
      uiBuilder: 'Visueller UI-Builder (.xnpas)',
      terminalinkIntro: 'Einführung',
      terminalinkComponents: 'Komponenten',
      terminalinkTheming: 'Themes',
      webinkIntro: 'Einführung',
      webinkComponents: 'Komponenten',
      unitTesting: 'Unit-Tests und Mocking',
      debuggingTools: 'Debugger, VS Code & Cloud',
      buildingExecutables: 'Native ausführbare Dateien erstellen',
      languageReference: 'Sprachreferenz',
      examples: 'Beispielgalerie'
    }),
    docFooter: { prev: 'Zurück', next: 'Weiter' },
    outline: { label: 'Auf dieser Seite' }
  }
}
