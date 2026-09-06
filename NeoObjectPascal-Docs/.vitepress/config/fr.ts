import type { DefaultTheme, LocaleSpecificConfig } from 'vitepress'
import { buildSidebar, buildNav } from './sidebar'

const base = '/fr'

export const frConfig: LocaleSpecificConfig<DefaultTheme.Config> = {
  label: 'Français',
  lang: 'fr-FR',
  title: 'NeoObjectPascal',
  description: 'Le langage Pascal moderne avec orientation objet, tests natifs et intégration Java.',
  themeConfig: {
    nav: buildNav(base, {
      guide: 'Guide',
      reference: 'Référence',
      examples: 'Exemples'
    }),
    sidebar: buildSidebar(base, {
      gettingStarted: 'Démarrage',
      language: 'Langage',
      oop: 'Orientation objet',
      features: 'Fonctionnalités',
      terminalink: 'TerminalInk',
      webink: 'WebInk',
      testing: 'Tests et outils',
      reference: 'Référence',
      introduction: 'Introduction',
      installation: 'Installation et premier programme',
      programStructure: 'Structure d’un programme',
      variablesAndTypes: 'Variables et types',
      operators: 'Opérateurs',
      controlFlow: 'Structures de contrôle',
      functions: 'Fonctions et procédures',
      arrays: 'Tableaux',
      errorHandling: 'Gestion des erreurs',
      classes: 'Classes et objets',
      inheritancePolymorphism: 'Héritage et polymorphisme',
      interfaces: 'Interfaces',
      functional: 'Programmation fonctionnelle',
      dataParsing: 'Traitement des données (JSON/CSV)',
      http: 'Requêtes HTTP',
      io: 'Entrée et sortie',
      javaIntegration: 'Intégration Java',
      modules: 'Modules et uses',
      internalLibraries: 'Bibliothèques internes',
      datesAndCurrency: 'Dates, heures et monnaie',
      uiBuilder: 'Éditeur visuel (.xnpas)',
      terminalinkIntro: 'Introduction',
      terminalinkComponents: 'Composants',
      terminalinkTheming: 'Thèmes',
      webinkIntro: 'Introduction',
      webinkComponents: 'Composants',
      unitTesting: 'Tests unitaires et mocking',
      debuggingTools: 'Débogueur, VS Code et cloud',
      buildingExecutables: 'Créer des exécutables natifs',
      languageReference: 'Référence du langage',
      examples: 'Galerie d’exemples'
    }),
    docFooter: { prev: 'Précédent', next: 'Suivant' },
    outline: { label: 'Sur cette page' }
  }
}
