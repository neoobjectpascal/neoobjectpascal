# Change Log

All notable changes to the "NeoObjectPascal" extension will be documented in this file.

## [2.16.0] - 2026-07-26

### Changed
- **All UI Builder dropdowns are now themed.** Native `<select>` controls opened the OS popup (a light macOS menu that clashed with the dark editor). Every select — language, route/screen, enum properties, state type, chart type, event actions — is now rendered as a custom themed dropdown that matches the editor (dark surface, hover/selected states, check mark, chevron). The native `<select>` is kept hidden underneath as the value/change source, so all behavior is unchanged; the themed popup is appended to `<body>` and fixed-positioned so no panel overflow clips it.

## [2.15.0] - 2026-07-26

### Added
- **Multilingual UI Builder editor.** The `.xnpas` visual editor now speaks the same 5 languages as the documentation — **Português, English, Deutsch, Français, Italiano**. A language selector (globe icon) in the toolbar switches the whole editor UI live: palette, properties, events, hints, and the host-side prompts/warnings (template picker, create/run messages). A new `media/i18n.js` translation layer keys every string by its Portuguese source with graceful fallback. The preference is the global setting **`neoobjectpascal.uiBuilder.language`** (`auto | pt | en | de | fr | it`); `auto` follows the VS Code display language and falls back to Portuguese.

## [2.14.0] - 2026-07-26

### Added
- **`visible` property on every component (WebInk and TerminalInk).** The Inspector now has a **Visible** control on every component: *Always* (default) or *Condition (fx)*, where you write a boolean expression. When false, the component and its subtree are dropped from the render. The interpreter honors `visible` in `HtmlRenderer` (WebInk) and in child coercion (TerminalInk).
- **Dynamic data binding (fx) on data fields.** `options` (Select/MultiSelect), `columns`/`rows` (Table), the chart (Chart), `value` (ProgressBar), `items` (List) and value fields now have an **fx** toggle that binds the field to a variable or expression instead of a fixed value — so a component can be populated from an API response. Previews render bound fields as a `fx …` chip instead of crashing on non-array data.
- **Focus control for TerminalInk.** Input components (`TextInput`, `PasswordInput`, `EmailInput`, `ConfirmInput`, `Select`, `MultiSelect`) gain **Key** (`key`) and **Initial focus** (`autoFocus`) fields, and the event editor offers a **Focus component** action that emits `focus("key")`. The interpreter adds a `focus(key)` native plus one-shot `autoFocus` handling in the render loop.

## [2.13.1] - 2026-07-26

### Fixed
- **UI Builder: the event editor's "Sem código / Código" toggle now works both ways.** A handler with a trivial body (`return true;`) was classified as free-code, so it opened stuck in Code mode and "Sem código" had no effect. Such handlers now default to the no-code action list, and the toggle is honored explicitly in both directions.

## [2.13.0] - 2026-07-26

### Added
- **UI Builder: drag-to-reorder.** Drag a component already on the canvas to reorder it, or move it into another container. The insertion point follows the cursor.
- **UI Builder: starter templates.** "New UI Builder File (.xnpas)" now offers templates — WebInk (blank / Dashboard) and TerminalInk (blank / Form).
- **UI Builder: generated-file guard.** Opening a generated `.npas` shows a warning (it is overwritten on `.xnpas` save) with a one-click "Open visual editor".
- **Automated tests** (`npm test`): golden codegen tests for both targets plus an optional JAR smoke test (`RUN_JAR=1`).
- **Docs:** a "Visual UI Builder (.xnpas)" page in all five languages.

### Fixed
- **UI Builder: event handlers bound only via `onConfirm`/`onCancel` are no longer dropped.** The unused-handler cleanup ignored those props, so a TerminalInk `ConfirmInput` handler could be pruned, leaving a dangling reference.

## [2.12.0] - 2026-07-26

### Added
- **UI Builder: TerminalInk target (Phase 3).** The visual builder now designs terminal UIs too. A **WebInk / TerminalInk** toggle switches the target; the canvas becomes a monospace terminal preview and the palette shows all TerminalInk components: `VBox`/`HBox`/`Box`/`Spacer`, `Text`/`Badge`, `TextInput`/`PasswordInput`/`EmailInput`/`ConfirmInput`/`Select`/`MultiSelect`, `Spinner`/`ProgressBar`/`StatusMessage`/`Alert`, and `UnorderedList`/`OrderedList`. Inspector fields include a border picker, terminal color enums, an options editor (`Select`/`MultiSelect`, "label = value" per line), and list-item editors. Saving generates a valid `uses terminalink;` program (text as positional args, lists expanded into `Item`s, `render(ui)` or named screens with `navigate`). Event editor supports `onChange`/`onSubmit`/`onConfirm`/`onCancel` with screen-name navigation.

## [2.11.4] - 2026-07-26

### Fixed
- **UI Builder: the selection tag (component type + delete button) now shows for every component.** It was appended inside the selected widget and clipped by widgets that clip their overflow (e.g. `ProgressBar`, `Table`), so it disappeared. The tag is now a canvas-level overlay positioned over the selection, so it is never clipped — consistent across all components.

## [2.11.3] - 2026-07-26

### Added
- **UI Builder: the full WebInk component palette.** All WebInk widgets are now available, added in a new **Dados** group and completing the others: `Col`, `Sidebar`, `Tabs`, `TextInput`, `TextArea`, `Select`, `Checkbox`, `Form`, `Table`, `List`, `Chart`, `Alert`, `ProgressBar`, `Spinner` (palette went from 15 to 28 components). Each renders a faithful canvas preview and has inspector fields — including comma-separated editors for `Select`/`List` options and `Table` columns, a row editor for `Table`, and a data editor for `Chart` (type, labels, series, values).

## [2.11.2] - 2026-07-26

### Fixed
- **UI Builder: the palette "search" box no longer loses focus while typing** — filtering now updates only the component list, not the search field.
- **UI Builder: the event editor's "Código" (free-code) mode now works** — the chosen mode is remembered per component/event instead of being inferred from the handler body, so you can always switch to writing NeoObjectPascal by hand (and typing there keeps focus too).

## [2.11.1] - 2026-07-26

### Fixed
- **UI Builder: property inputs no longer lose focus while typing.** Each edit was echoed back by the editor's save-sync round-trip, forcing a full re-render on every keystroke. The webview now ignores echoes of its own edits, so typing in the inspector (and the event code editor and state fields) is smooth.

## [2.11.0] - 2026-07-26

### Added
- **Visual UI Builder (`.xnpas`) — Fase 1 (WebInk).** A drag-and-drop WYSIWYG editor for building interfaces without writing code. Open or create a `.xnpas` file and design visually: a component palette, a faithful live canvas preview (real WebInk look), and a property inspector. Includes global **state** variables and a **hybrid event editor** (no-code common actions like "increment a variable" / "navigate", plus a free NeoObjectPascal code escape hatch). One-way sync: saving `teste.xnpas` (re)generates the sibling `teste.npas`. New command **"New UI Builder File (.xnpas)"**. The editor UI uses professional SVG icons throughout. TerminalInk target and more widgets arrive in later phases.

## [2.10.2] - 2026-07-18

### Fixed
- **Adding/removing a breakpoint while debugging now takes effect immediately.** Breakpoints were only applied to the running program at launch, so toggling one mid-session (common with a long-lived WebInk server) was ignored — a removed breakpoint kept stopping. `setBreakpoints` now updates the live debugger too. Bundled interpreter JAR refreshed.

## [2.10.1] - 2026-07-18

### Fixed
- **WebInk: reactive text inputs keep focus.** A `TextInput` with `onChange` (recalculating on every keystroke) now preserves focus and caret position across the server re-render, so typing is smooth. Bundled interpreter JAR refreshed.

## [2.10.0] - 2026-07-18

### Added
- **WebInk — build professional web frontends in NeoObjectPascal.** New `uses webink;` framework (the web analog of TerminalInk): `render(#{ "/": home, "/dashboard": dashboard })` starts a local server and opens the browser. State lives in variables; browser events (onClick/onChange/onSubmit) call server-side callbacks that mutate state and re-render — a live, server-driven UI. Styled with **Tailwind CSS** and charts via **Chart.js**, both bundled (offline). ~25 widgets: Page, Section, Container, Grid, Card, Heading, Text, Badge, StatCard, Navbar, Sidebar, Link, Tabs, Button, TextInput, Select, Checkbox, TextArea, Form, Table, List, Chart, Alert, ProgressBar, Spinner.
- **URL routing.** Real routes with the browser History API — back/forward and deep links work. `Link(#{ href: "/x" }, ...)` and `navigate("/x")` inside a callback.
- **TerminalInk screen navigation.** `render(#{ "home": home, "about": about })` + `navigate("about")`; the classic single-screen `render(ui)` still works.
- **Record literals accept string keys** — `#{ "/": home }` (URL routes, JSON-style field names), in addition to identifier keys.
- **~13 new snippets** for WebInk (`wink-app`, `wink-page`, `wink-card`, `wink-chart`, `wink-navbar`, `wink-button`, …) and TerminalInk navigation (`tink-nav`).
- Example `56-WebInkDashboard.npas`.

### Notes
- A program is **entirely TerminalInk or entirely WebInk** — using both `uses terminalink` and `uses webink` raises a clear error.
- WebInk uses the **system browser** (no extra dependency); the interpreter jar stays ~9 MB and cross-platform. Debugging WebInk is clean — the server is single-threaded and the browser is only a client, so breakpoints in screen/callback functions fire on interaction and Step Into works via DAP.

## [2.9.1] - 2026-07-18

### Fixed
- **macOS icon conversion.** `--build --icon logo.png` no longer fails with `sips ... Error 13` on RGB/large PNGs. The icon is now built via an `.iconset` + `iconutil` (the canonical macOS route) instead of `sips -s format icns` directly.

### Removed
- Removed the "Execute Project On Cloud (Coming Soon)" entry from the context menu and command palette (the feature is not available yet).

## [2.9.0] - 2026-07-18

### Added
- **Build Native Executable.** New command (right-click a `.npas` file/folder → **Build Native Executable**, or the interpreter's `--build`) packages the program into a self-contained native app via `jpackage`: **`.app`** on macOS, a folder with **`.exe`** on Windows, **`bin/`** on Linux. Prompts for the app name and an optional **PNG icon** (converted per platform: `.icns` on macOS, `.ico` on Windows, PNG on Linux). Output goes to `dist/`.
- **The whole project is bundled.** Every file and subfolder next to the program (`uses` modules like `helpers/`, plus any data files/assets) is included, preserving structure — the packaged app runs exactly like the source. Junk (`.git`, `node_modules`, the output dir, …) is skipped.
- CLI: `java -jar neoobjectpascal.jar --build <program.npas> [--icon logo.png] [--name Name] [--output dir] [--target mac|windows|linux]`. `jpackage` can't cross-compile, so each target is built on its own OS (a clear error explains this on mismatch).

## [2.8.0] - 2026-07-18

### Added
- **Automatic main detection.** Run and Debug no longer require selecting the program file. They scan the project and find the main automatically — a `.npas` file that terminates with `end.` (units, classes and helpers end with `end;`). Right-click any `.npas` file **or a folder** and Run/Debug just works.
- **Pick-the-program prompt.** When a project has more than one program (several files ending with `end.`), Run/Debug show a quick picker so you choose which one to execute.
- **Context-aware "Run All Unit Tests".** The "Run All Unit Tests" entry now appears whenever the project contains any `*.test.npas` file — from any `.npas` file or folder — not only when a test file is selected. Backed by a file watcher, so it updates as tests are added/removed.

### Fixed
- **Step Into now enters project methods.** Pressing **Step Into (F7)** on a class-method call (e.g. `calculoItbi.calcular()`) now descends into the method body and opens the **correct source file** — even when the class lives in another module (`uses helpers.calculoitbi`). Previously it stayed on the same line or appeared to skip to the end because every frame was reported against the main file.
- **Real, multi-file call stack.** The Call Stack view now shows each active frame (`ClassName.method`, functions, `main`) with its own file and line. The Variables view shows that frame's locals — including `self` while stepping through a method.
- **Step Out (⇧F11) is supported.** It returns to the caller instead of erroring.
- **Correct Step Over vs Step Into.** Step Over no longer descends into constructors/methods; Step Into does. Depth-aware stepping across files.
- **No more double-stop.** The first Step Into on a `WriteLn(...)` line used to stop twice on the same line; it now advances in a single press.
- **File-aware breakpoints.** A breakpoint on line N in one file no longer falsely triggers on line N of a different file.
- Bundled interpreter JAR refreshed with these debugger fixes.

## [2.7.1] - 2026-07-18

### Fixed
- Run / Run Test / Run All Tests no longer dump the raw shell one-liner into the terminal. They now run inside a pseudoterminal the extension fully controls — clean framed output with **no** echoed command line, identical on **Windows, Linux and macOS** (no shell, no `printf`/ANSI portability issues).
- Interactive `ReadLn` input now works in the framed Run terminal (keystrokes, backspace and Ctrl+C are handled and piped to the program).

## [2.7.0] - 2026-07-18

### Changed
- Run/Test show a clean framed output (header · file · separator · program output · ✓/✗ status) instead of the raw `java -jar` command.

### Fixed
- Debug: fixed "Class not found" when a program uses `uses folder.module` — the debugger now roots module resolution at the program directory (same as Run).
- Debug: program output (WriteLn) now appears in the Debug Console (previously collided with the DAP protocol on stdout).
- Debug: the Debug Console no longer floods with internal `[DAP]` protocol traces (set `NEOPASCAL_DAP_VERBOSE=true` to re-enable).
- `--test-all` coverage now measures PUBLIC CLASS METHODS exercised by tests (with a per-method report), not a file-count ratio.
- Bundled interpreter JAR refreshed with these fixes.

## [2.6.0] - 2026-07-18

### Added
- Highlighting + snippets for the new Date/Time/DateTime/Currency types and Double/Float aliases, and their built-in functions (now/today/date/format/currency/formatCurrency/â¦).

## [2.5.0] - 2026-07-18

### Changed
- Rewrote ALL code snippets to match the real NeoObjectPascal grammar (the old ones used traditional Object Pascal). Correct `class`/`interface` with inline bodies, `try/catch/finally` with `begin/end`, `test "..." begin ... end;`, accurate TerminalInk (`VBox(props, [children])`, function callbacks, `render(ui)`) and HTTP snippets, `java` blocks with named arguments, and new snippets (arrays, pipe, mock/verify, new, method/procedure members, choice pickers). ~52 snippets.
- Cleaned leftover traditional Object Pascal keywords from the syntax grammar (`type`, `unit`, `program`, `implementation`, `property`, `nil`, `downto`, `repeat`, `until`, `with`, `goto`, `published`, `overload`, ...) so highlighting reflects the actual language.

## [2.4.2] - 2026-07-18

### Changed
- "Execute Project On Cloud" is now labeled "(Coming Soon)" and temporarily disabled: invoking it shows a "coming soon" notice instead of running. The implementation is preserved for a future release.

## [2.4.1] - 2026-07-18

### Fixed
- Robust JAR resolution: the bundled interpreter now uses a stable filename (`bin/neoobjectpascal.jar`) and the extension finds any `neoobjectpascal*.jar` in the target folder, so Run/Debug/Test no longer fail with "JAR not found" after a version change or with a custom `jarPath`.

## [2.4.0] - 2026-07-18

### Changed
- New extension icon: an amber-on-dark `>_` terminal-prompt mark, matching the NeoObjectPascal brand (`images/icon.png`).
- Bundled interpreter JAR updated to the current build (`bin/neoobjectpascal-v2.4-cloud.jar`) â now includes record literals `#{}`, TerminalInk, the HTTP module, and named java-block arguments. Run/Debug/Test use it by default.

## [2.3.0] - 2026-07-17

### Added
- HTTP/API function highlighting (httpGet/httpPost/.../httpForm/httpBearer/httpBasic) and snippets (http-get/post/request/auth/form).

## [2.2.0] - 2026-07-17

### Added
- **Record-literal highlighting**: `#{ key: value, ... }` literals now highlight `#{`/`}` as punctuation and each key before its `:` as a property. Nested records are supported and the rule does not swallow embedded `java:(...) { ... }` blocks.
- **New keyword highlighting**: boolean operators (`and`, `or`, `not`), error handling (`try`, `catch`, `finally`, `raise`), the `array` keyword, value keywords (`true`, `false`, `self`, `new`), OO modifiers (`extends`, `implements`, `public`, `private`, `protected`, `virtual`, `override`), and testing keywords (`test`, `describe`, `it`, `expect`, `toBe`, `toEqual`, `toBeTrue`, `toBeFalse`, `toBeNull`, `mock`, `whenCall`, `thenReturn`, `verify`, `assert`).
- **TerminalInk component highlighting**: builtin components/functions (`Text`, `Box`, `VBox`, `HBox`, `Spacer`, `TextInput`, `EmailInput`, `PasswordInput`, `ConfirmInput`, `Select`, `MultiSelect`, `Spinner`, `ProgressBar`, `Badge`, `StatusMessage`, `Alert`, `Item`, `UnorderedList`, `OrderedList`, `render`, `defaultTheme`, `extendTheme`, `setTheme`, `ThemeProvider`) plus the `terminalink` module name after `uses`.
- **Code snippets**: new snippet library covering core language (program, var, function, procedure, class, interface, if/if-else, for, while, for..in, try/catch, try/catch/finally, raise, test, expect, uses, record literal, java block, WriteLn) and TerminalInk (app/render skeleton, TextInput, Select, Spinner, ProgressBar, Alert, StatusMessage, full form, theme setup).

### Changed
- Comments are now `//` line comments only; the obsolete `{ }` / `(* *)` block-comment grammar rules were removed so `{ }` works correctly as the embedded Java-code delimiter and `#{` as a record literal.
- Added `#{`â`}` auto-closing pair to the language configuration.

## [2.1.0] - 2025-10-05

### Added
- **Interactive Debugging**: Full debug support with Debug Adapter Protocol
  - Visual breakpoints (click on line numbers)
  - Step over, step into, step out
  - Continue execution
  - Variable inspection in Variables view
  - Watch expressions
  - Call stack view
  - Debug console with REPL
  - Set variable values during debugging
  
- **Unit Testing Support**:
  - Execute single `.test.npas` files
  - Execute all unit tests in workspace
  - Separate context menu for test files
  - Dedicated test terminal
  
- **Cloud Execution**:
  - Execute projects on NeoObjectPascal Cloud
  - Configurable cloud URL
  - Username/password authentication
  - Automatic project upload
  - Execution URL returned
  
- **Debug Console Commands**:
  - `b <line>` - Add breakpoint
  - `d <line>` - Remove breakpoint
  - `list` - List breakpoints
  - `c, continue` - Continue execution
  - `s, step` - Step over
  - `i, into` - Step into
  - `w <var>` - Watch variable
  - `p <var>` - Print variable
  - `set <var> <value>` - Modify variable
  - `vars` - List all variables
  - `stack` - Show call stack
  - `q, quit` - Quit debugger
  
- **Configuration Options**:
  - `neoobjectpascal.jarPath` - Custom JAR path
  - `neoobjectpascal.cloudUrl` - Cloud base URL
  - `neoobjectpascal.cloudUsername` - Cloud username
  - `neoobjectpascal.cloudPassword` - Cloud password
  
- **Context Menus**:
  - Different menus for `.npas` and `.test.npas` files
  - Run, Debug, Execute on Cloud for regular files
  - Execute Unit Test, Execute All Tests for test files
  
- **Bundled JAR**: Includes neoobjectpascal-v2.1-cloud.jar

### Changed
- Updated to NeoObjectPascal v2.1
- Enhanced syntax highlighting for OO features (classes, interfaces, inheritance)
- Improved error messages
- Better terminal integration

### Fixed
- JAR path resolution
- File extension detection
- Terminal reuse logic

## [1.0.0] - 2025-10-04

### Added
- Initial release
- Syntax highlighting for `.npas` files
- Run NeoObjectPascal File command
- Context menu integration
- JAR path configuration
- Terminal integration
- Support for keywords, operators, strings, comments
- Java embedded blocks highlighting

### Based On
- Fork of [object-pascal-syntax-highlighting](https://github.com/thevikke/object-pascal-syntax-highlighting)
- NeoObjectPascal ANTLR4 Grammar

---

## Version History

- **2.1.0**: Full IDE support with debugging, testing, and cloud execution
- **1.0.0**: Initial release with syntax highlighting and run support

---

**Format**: Based on [Keep a Changelog](https://keepachangelog.com/)  
**Versioning**: [Semantic Versioning](https://semver.org/)
