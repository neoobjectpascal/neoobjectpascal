# Building native executables

NeoObjectPascal packages a `.npas` program into a **self-contained native executable** — without requiring the end user to have Java installed. A single command produces:

- **macOS** → a `.app` bundle
- **Windows** → a folder with an `.exe`
- **Linux** → a folder with a binary in `bin/`

Under the hood, the build uses **`jpackage`** (part of JDK 14+), which packages the interpreter, your project, and a trimmed Java runtime (JRE) inside the app.

## Prerequisites

- A **JDK 14 or later** on the PATH (`jpackage` ships with the JDK).
- Run from the interpreter's **packaged JAR** (`neoobjectpascal.jar`).

## Usage

```bash
java -jar neoobjectpascal.jar --build programa.npas [opções]
```

| Option | Description |
|-------|-----------|
| `--icon <png>` | app icon, from a **PNG** (converted per platform) |
| `--name <Nome>` | app/executable name (default: program name) |
| `--output <dir>` | output directory (default: `./dist`) |
| `--target <mac\|windows\|linux>` | target platform (default: the current OS) |

### Example

```bash
java -jar neoobjectpascal.jar --build main.npas --icon icon.png --name CalculoITBI --output dist
```

<Output>
📦 Empacotando 'main.npas' → macOS (.app) (CalculoITBI)
   • 5 arquivo(s) do projeto incluídos (a partir de .../NeoObjectPascal-Examples)
   • ícone: app-icon.icns
   • rodando jpackage...
✅ Build concluído: dist/CalculoITBI.app
   Executar: open "dist/CalculoITBI.app"
</Output>

## The entire project is packaged

The whole program directory is included in the app — **files and subfolders**, preserving the structure. That means:

- Modules imported with `uses pasta.modulo` (for example `helpers/calculoitbi.npas`) come along.
- Data files and assets the program reads at run time are included too.

This way, the packaged app runs exactly as the program ran from source. "Junk" directories (`.git`, `node_modules`, the output directory itself, ...) are ignored.

## Icon from a PNG

Pass a single **PNG** with `--icon`; the build converts it to the right format for each platform:

| Platform | Icon format | Conversion |
|-----------|------------------|-----------|
| macOS | `.icns` | via `iconset` + `iconutil` (native to macOS) |
| Windows | `.ico` | built into Java (no external tools) |
| Linux | `.png` | used directly |

Use a square PNG (for example 512×512 or 1024×1024) for the best result.

## One platform at a time

`jpackage` **does not cross-compile**: each artifact is built on its own operating system. On macOS you build the `.app`; the `.exe` is built on Windows and the `bin` on Linux. To produce all three, run the build on each OS (or use a CI matrix). If you pass a `--target` different from the current OS, the command warns you with a clear error.

## From VS Code

With the extension installed, right-click a `.npas` file (or a project folder) and choose **Build Native Executable**. The extension asks for the app name and, optionally, a PNG icon, and produces the result in `dist/`.

## Running the result

- **macOS:** `open dist/CalculoITBI.app` (or double-click it).
- **Windows:** `dist\CalculoITBI\CalculoITBI.exe`.
- **Linux:** `dist/CalculoITBI/bin/CalculoITBI`.

::: info App size
Each executable is self-contained and includes a JRE, so it takes up a few dozen MB — that is the nature of a native Java app, in exchange for not requiring Java on the user's machine.
:::

::: tip Interactive programs (TerminalInk)
Terminal apps built with [TerminalInk](../terminalink/introduction) work when packaged: when opened by double-click (without a terminal), TerminalInk opens its own window. Pure console programs (`WriteLn`) are best run from a terminal, so you can see the output.
:::

## Next steps

With native builds you distribute your programs as real apps. To review the complete language syntax, see the [Language reference](../reference/language-reference).
