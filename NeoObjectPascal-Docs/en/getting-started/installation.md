# Installation and your first program

The NeoObjectPascal interpreter is distributed as an **executable JAR file**, so all you need is **Java 11 or later** installed.

## Prerequisites

- **Java 11+** (JRE or JDK). Check with:

```bash
java -version
```

- (Optional) **Maven**, if you want to build the interpreter from source.

## Getting the interpreter

You can use the pre-packaged JAR (for example, the one that ships with the VS Code extension, in `VS-Code-Extension/bin/`) or build it from the repository:

```bash
cd NeoObjectPascal
mvn package -DskipTests
# The final JAR is in target/neoobjectpascal-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Hello, world

Create a file named `ola.npas`:

```npas
begin
    WriteLn("Olá, NeoObjectPascal!");
end.
```

And run it:

```bash
java -jar neoobjectpascal.jar ola.npas
```

<Output>
Olá, NeoObjectPascal!
</Output>

::: tip File extension
Programs use the `.npas` extension. Test files use `.test.npas`.
:::

## Command-line options

The interpreter accepts several flags:

| Flag | Description |
|------|-----------|
| _(none)_ | Runs the `.npas` file |
| `-q`, `--no-warnings` | Suppresses analysis warnings |
| `-t`, `--test` | Test mode (runs a `.test.npas`) |
| `-ta`, `--test-all <dir>` | Runs all tests recursively, with coverage |
| `-d`, `--debug` | Interactive debugger mode |
| `--dap` | DAP mode (VS Code integration) |
| `--build <file> [--icon png] [--name] [--output] [--target]` | Builds a native executable (exe/app/bin) |
| `--execute-on-cloud <url> <proj> <user> <pass>` | Runs on NeoObjectPascalCloud |
| `-h`, `--help` | Shows help |

Examples:

```bash
java -jar neoobjectpascal.jar --no-warnings ola.npas
java -jar neoobjectpascal.jar -t calculadora.test.npas
java -jar neoobjectpascal.jar --test-all ./examples
```

::: tip Recommended editor
Install the **VS Code extension** for NeoObjectPascal and get syntax highlighting, execution, testing, and debugging all integrated. See [Debugger, VS Code, and cloud](../testing/debugging-tools).
:::

Now that you can run code, let's understand the [structure of a program](./program-structure).
