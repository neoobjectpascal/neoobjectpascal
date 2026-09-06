import type { DefaultTheme, LocaleSpecificConfig } from 'vitepress'
import { buildSidebar, buildNav } from './sidebar'

const base = ''

export const ptConfig: LocaleSpecificConfig<DefaultTheme.Config> = {
  label: 'Português',
  lang: 'pt-BR',
  title: 'NeoObjectPascal',
  description: 'A linguagem Pascal moderna com orientação a objetos, testes nativos e integração Java.',
  themeConfig: {
    nav: buildNav(base, {
      guide: 'Guia',
      reference: 'Referência',
      examples: 'Exemplos'
    }),
    sidebar: buildSidebar(base, {
      gettingStarted: 'Começando',
      language: 'Linguagem',
      oop: 'Orientação a Objetos',
      features: 'Recursos',
      terminalink: 'TerminalInk',
      webink: 'WebInk',
      testing: 'Testes e Ferramentas',
      reference: 'Referência',
      introduction: 'Introdução',
      aboutAuthor: 'Sobre o autor',
      installation: 'Instalação e primeiro programa',
      programStructure: 'Estrutura de um programa',
      variablesAndTypes: 'Variáveis e tipos',
      operators: 'Operadores',
      controlFlow: 'Estruturas de controle',
      functions: 'Funções e procedimentos',
      arrays: 'Arrays',
      errorHandling: 'Tratamento de erros',
      classes: 'Classes e objetos',
      inheritancePolymorphism: 'Herança e polimorfismo',
      interfaces: 'Interfaces',
      functional: 'Programação funcional',
      dataParsing: 'Manipulação de dados (JSON/CSV)',
      http: 'Chamadas de API (HTTP)',
      io: 'Entrada e saída',
      javaIntegration: 'Integração com Java',
      modules: 'Módulos e uses',
      internalLibraries: 'Bibliotecas internas',
      datesAndCurrency: 'Datas, horas e moeda',
      uiBuilder: 'Editor visual (.xnpas)',
      terminalinkIntro: 'Introdução',
      terminalinkComponents: 'Componentes',
      terminalinkTheming: 'Temas',
      webinkIntro: 'Introdução',
      webinkComponents: 'Componentes',
      unitTesting: 'Testes unitários e mocking',
      debuggingTools: 'Debugger, VS Code e nuvem',
      buildingExecutables: 'Gerar executáveis nativos',
      languageReference: 'Referência da linguagem',
      examples: 'Galeria de exemplos'
    }),
    docFooter: { prev: 'Anterior', next: 'Próximo' },
    outline: { label: 'Nesta página' },
    lastUpdatedText: 'Atualizado em',
    darkModeSwitchLabel: 'Aparência',
    returnToTopLabel: 'Voltar ao topo',
    langMenuLabel: 'Mudar idioma'
  }
}
