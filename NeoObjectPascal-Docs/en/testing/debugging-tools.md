# Debugger, VS Code, and cloud execution

Beyond the testing framework, NeoObjectPascal offers tools to investigate, edit, and run code at scale: an **interactive debugger** (with a DAP mode for editors), a **VS Code extension**, and **remote cloud execution** via NeoObjectPascalCloud. This guide introduces all three.

## Interactive debugger

To debug a program step by step, run it with the `-d` flag (or `--debug`):

```bash
java -jar neoobjectpascal.jar -d programa.npas
```

The interpreter opens an interactive REPL where you control execution. The main commands:

| Command | Action |
|---------|------|
| `b <line>` | adds a breakpoint on the line |
| `d <line>` | removes the breakpoint from the line |
| `list` | lists the active breakpoints |
| `c`, `continue` | continues to the next breakpoint |
| `s`, `step` | runs the next line (step over) |
| `i`, `into` | enters the called function (step into) |
| `o`, `out` | step out of the current function |
| `p <var>` | prints the value of a variable |
| `vars` | lists all variables in the current scope |
| `w <var>` | watches changes to a variable |
| `set <var> <value>` | changes the value of a variable at run time |
| `stack` | shows the call stack |
| `q`, `quit` | exits the debugger and the program |

### An example session

Consider the program below:

```npas
var x: Integer;
var y: Integer;
var resultado: Integer;

begin
    x := 10;
    y := 20;
    resultado := x + y;
    WriteLn("resultado = ", resultado);
end.
```

We place a breakpoint on the summation line, watch `x`, inspect values, and even modify a variable before continuing:

<Output>
debug> b 8
✓ Breakpoint adicionado na linha 8

debug> w x
✓ Watching variável: x

debug> c

⏸ PAUSADO na linha 8
  resultado := x + y;

debug> p x
x = 10 (tipo: INTEGER)

debug> vars
Variáveis locais:
  x = 10 (tipo: INTEGER)
  y = 20 (tipo: INTEGER)
  resultado = null (tipo: INTEGER)

debug> set x 50
✓ x = 50 (anterior: 10)

debug> c
resultado = 70
</Output>

::: tip Breakpoint strategy
Place breakpoints at the start of loops, important `if` conditions, function calls, and return points. Combine `w` (watch) with `s` (step) to follow exactly when and where a variable changes.
:::

### DAP mode for editors

For graphical debugging inside an editor, the interpreter exposes a **DAP** (Debug Adapter Protocol) server — the same protocol that VS Code and other editors use for visual breakpoints, variable inspection, and step-by-step control:

```bash
java -jar neoobjectpascal.jar --dap
```

In this mode the interpreter does not run a text REPL: it waits for a DAP client (such as VS Code) to connect. You set breakpoints by clicking in the editor's gutter, and use the continue/step/inspect buttons of the interface itself. You normally do not start DAP mode by hand — the VS Code extension does it for you.

**Step Into (F7)** enters the body of class methods and functions in your project — including those defined in another file imported via `uses` — opening the correct file and line. The **Call Stack** shows each stack frame (`Class.method`, functions, and `main`) with its file and line; the **Variables** tab shows the local variables of the selected frame, including `self` inside a method. Use **Step Over** to execute a call without entering it, and **Step Out** to return to the caller.

::: tip Debugging in VS Code
When debugging from VS Code, program output (`WriteLn`) appears in the **Debug Console** while the program runs. Module resolution is rooted at the program's directory, so debugging a program that uses `uses folder.module` works as expected. However, **interactive input (`ReadLn`) is NOT available while debugging** — it returns a default value. For interactive programs, use **Run** instead of the debugger.
:::

## VS Code extension

The official **VS Code** extension turns the editor into a complete environment for NeoObjectPascal. It includes:

- **Syntax highlighting** for `.npas` and `.test.npas` files.
- **Embedded interpreter** — the extension ships the packaged JAR (in `VS-Code-Extension/bin/`), so you do not need to configure the interpreter path manually.
- **Commands** to run, test, and debug without leaving the editor.

The commands are available through the command palette (`Cmd/Ctrl+Shift+P`):

| Command | Action |
|---------|------|
| `neoobjectpascal.run` | runs the current `.npas` file |
| `neoobjectpascal.debug` | starts the debugger (via DAP) on the current file |
| `neoobjectpascal.runTest` | runs the current `.test.npas` file |
| `neoobjectpascal.runAllTests` | runs all the project's tests recursively |
| `neoobjectpascal.build` | builds a native executable (exe/app/bin) of the project — see [Building native executables](./building-executables) |

::: info Cloud execution
Cloud execution is done from the command line via the interpreter's `--execute-on-cloud` flag (see the section below).
:::

::: tip Recommended workflow
Write the code with highlighting and autocomplete, run `neoobjectpascal.runTest` to validate the open file, and use `neoobjectpascal.debug` to follow execution with visual breakpoints. All using the JAR that already comes with the extension.
:::

## Cloud execution

NeoObjectPascal integrates natively with **NeoObjectPascalCloud**, letting you run a project on a remote server with a single command. This is useful for sharing executions via URL, keeping a log history, and running without depending on the local environment.

### Syntax

```bash
java -jar neoobjectpascal.jar --execute-on-cloud <url> <projeto> <usuario> <senha> <arquivo.npas>
```

| Parameter | Description |
|-----------|-----------|
| `<url>` | base URL of the cloud API (e.g., `http://localhost:8000`) |
| `<projeto>` | project name (base folder) |
| `<usuario>` | cloud user |
| `<senha>` | cloud password |
| `<arquivo.npas>` | main file to run |

### Example

Given the program `hello.npas`:

```npas
var mensagem: String;
var numero: Integer;

begin
    mensagem := "Olá do NeoObjectPascal Cloud!";
    numero := 42;

    WriteLn("=================================");
    WriteLn(mensagem);
    WriteLn("Número mágico: ", numero);
    WriteLn("=================================");
end.
```

We run it in the cloud:

```bash
java -jar neoobjectpascal.jar \
  --execute-on-cloud \
  http://localhost:8000 \
  hello_project \
  usuario@email.com \
  senha123 \
  hello.npas
```

The interpreter authenticates, collects **all the `.npas` files** in the project directory (preserving the folder structure), uploads them, triggers the remote execution, and returns a link to follow the logs:

<Output>
🔐 Autenticando no cloud...
✓ Autenticado com sucesso!

📁 Coletando arquivos do projeto...
✓ Encontrados 1 arquivo(s)
  - hello.npas

⬆️  Fazendo upload dos arquivos...
  [1/1] hello.npas
✓ Upload concluído!

🚀 Executando projeto no cloud...
✅ Projeto executado com sucesso!

🔗 Link da execução:
   http://localhost:8000/executions/123
</Output>

::: warning Do not expose passwords
Avoid passing the password directly on the command line, since it becomes visible in the shell history and the process list. Prefer environment variables:

```bash
export CLOUD_PASSWORD="senha123"
java -jar neoobjectpascal.jar --execute-on-cloud \
  http://localhost:8000 meu_projeto usuario@email.com "$CLOUD_PASSWORD" main.npas
```
:::

::: info Multi-file projects
Collection is recursive: if the project has subfolders (`helpers/`, `utils/`), all `.npas` files are detected and uploaded automatically, preserving the relative paths. Just point to the main file in the command.
:::

## Next steps

You now know the three investigation and execution tools of NeoObjectPascal. To consult the complete language syntax in one place, continue to the [Language reference](../reference/language-reference).
