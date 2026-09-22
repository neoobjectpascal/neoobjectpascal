# DesktopInk — introduzione

**DesktopInk** è il framework per applicazioni desktop native di NeoObjectPascal. Con `uses desktopink;` costruisci finestre native con componenti visivi dichiarativi, tema chiaro/scuro e lo stesso modello reattivo degli altri runtime.

A differenza di TerminalInk (terminale) e WebInk (server web), DesktopInk apre una **vera finestra Swing/Java2D** — senza dipendenze esterne. Ogni componente viene disegnato dal framework con angoli arrotondati, tipografia di sistema e un aspetto professionale ispirato a [shadcn/ui](https://ui.shadcn.com).

## Opzioni di rendering

Il secondo argomento di `render` accetta queste opzioni:

| Opzione    | Tipo    | Default  | Descrizione                            |
|------------|---------|----------|----------------------------------------|
| `title`    | String  | Nome app | Titolo della finestra                  |
| `width`    | Integer | 960      | Larghezza iniziale                     |
| `height`   | Integer | 720      | Altezza iniziale                       |
| `centered` | Boolean | `true`   | Centra la finestra                     |
| `maximized`| Boolean | `false`  | Apre la finestra massimizzata          |
| `theme`    | String  | `"auto"` | `"light"`, `"dark"` o `"auto"` (sistema) |

## Tema

DesktopInk ha due temi completi — chiaro e scuro — più la modalità `auto`, che segue le preferenze del sistema. Cambia tema con:

```npas
setTheme("dark");
setTheme("light");
setTheme("auto");
```

Vedi la [lista completa dei componenti](./components) in inglese per i dettagli.