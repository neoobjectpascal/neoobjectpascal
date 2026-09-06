# NeoObjectPascal Docs

Official multilingual documentation for the **NeoObjectPascal** language, built with [VitePress](https://vitepress.dev).

Languages: **Português** (canonical, `/`), **English** (`/en`), **Deutsch** (`/de`), **Français** (`/fr`), **Italiano** (`/it`).

## Development

```bash
npm install
npm run docs:dev      # local dev server (hot reload)
npm run docs:build    # static build → .vitepress/dist
npm run docs:preview  # preview the production build
npm run test:e2e      # Playwright smoke tests (requires a built/preview server)
```

## Structure

```
.vitepress/
├─ config.ts              # root config, assembles the 5 locales
├─ config/
│  ├─ shared.ts           # theme, brand, .npas Shiki grammar, search
│  ├─ sidebar.ts          # DRY sidebar/nav builder (shared slugs)
│  └─ pt|en|de|fr|it.ts   # per-locale labels + meta
├─ theme/                 # brand CSS + <Output> component
└─ shiki/                 # NeoObjectPascal TextMate grammar (from the VS Code extension)

<lang>/                    # content per locale (getting-started, language, oop, features, testing, reference)
```

## Writing content

- Directory **slugs are shared** across locales (English); only labels and page content are translated.
- NeoObjectPascal code fences use the ` ```npas ` language for real syntax highlighting.
- Use the `<Output>` component to show a program's expected output:

  ````md
  ```npas
  begin WriteLn("Olá!"); end.
  ```

  <Output>
  Olá!
  </Output>
  ````

## Syntax highlighting source

The `.npas` grammar is copied from `../VS-Code-Extension/syntaxes/neoobjectpascal.tmLanguage.json`.
If the language grammar changes there, re-copy it into `.vitepress/shiki/`.
