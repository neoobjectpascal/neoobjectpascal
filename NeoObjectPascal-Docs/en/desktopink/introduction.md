# DesktopInk — introduction

**DesktopInk** is NeoObjectPascal's native desktop application framework. With `uses desktopink;` you build native windows using declarative visual components, light/dark theme, and the same reactive model as the other runtimes.

Unlike TerminalInk (terminal) and WebInk (web server), DesktopInk opens a **real Swing/Java2D window** — no external dependencies. Every component is drawn by the framework with rounded corners, system typography, and a professional look inspired by [shadcn/ui](https://ui.shadcn.com).

```npas
uses desktopink;

function main(): Object
begin
    return Window(#{ padding: 24 }, [
        Heading(#{ level: 1, text: "Hello, DesktopInk!" }),
        Text(#{}, "This is a native window.")
    ]);
end;

begin
    render(#{ main: main }, #{
        title: "My App",
        width: 800,
        height: 600,
        centered: true,
        theme: "auto"
    });
end.
```

::: tip TerminalInk, WebInk and DesktopInk don't mix
A program is **fully TerminalInk, fully WebInk, or fully DesktopInk**. Using two different UI `uses` in the same program produces a clear error.
:::

## Render options

The second argument of `render` accepts these options:

| Option      | Type    | Default  | Description                           |
|-------------|---------|----------|---------------------------------------|
| `title`     | String  | App name | Window title                          |
| `width`     | Integer | 960      | Initial width                         |
| `height`    | Integer | 720      | Initial height                        |
| `centered`  | Boolean | `true`   | Center the window on screen           |
| `maximized` | Boolean | `false`  | Open window maximized                 |
| `theme`     | String  | `"auto"` | `"light"`, `"dark"` or `"auto"` (system) |

```npas
render(#{ main: main }, #{
    title: "Dashboard",
    width: 1100,
    height: 720,
    centered: true,
    maximized: false,
    theme: "dark"
});
```

## Theme

DesktopInk has two complete themes — light and dark — plus `auto` mode, which follows the system preference.

Visual tokens are shared with WebInk, ensuring both runtimes look like the same product. Switch themes with:

```npas
setTheme("dark");
setTheme("light");
setTheme("auto");
```

## Components

DesktopInk offers over 25 components, from layout to forms, tables, charts, and modals. See the [full component list](./components).

![DesktopInk editable DataGrid](/screenshots/desktop-ink.jpg)
*Editable DataGrid with modified cell highlighting in dark theme.*

## How it works

1. `render(#{ screens }, opts)` opens a Swing window and builds the component tree.
2. State lives in NeoObjectPascal variables.
3. Events (click, typing, Select change) call NeoObjectPascal callbacks.
4. After each callback, the runtime rebuilds the UI with the new state.
5. Windows run on Swing's EDT (Event Dispatch Thread); rebuild is thread-safe.

Layout uses `BoxLayout` and `GridLayout` with rounded corners painted in Java2D. Text fields use Swing's native `InputHost`, styled to match the theme.

::: tip Native executable
Use `--build` to generate a native executable via `jpackage`. The `java.desktop` module enters the runtime image, resulting in a self-contained 15–30 MB binary.
:::

Next, check out all [DesktopInk Components](./components).