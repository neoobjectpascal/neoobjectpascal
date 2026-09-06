# Introduction to TerminalInk

**TerminalInk** is the terminal-UI framework for NeoObjectPascal. Inspired by [React Ink](https://github.com/vadimdemedes/ink), it lets you build terminal applications (TUIs) **declaratively** — you describe what the screen should look like and the framework takes care of drawing it, redrawing it, and decoding the keyboard.

Under the hood, TerminalInk is backed by the Java [Lanterna](https://github.com/mabe02/lanterna) library, which provides a cross-platform terminal, a buffered screen, and key decoding. You never touch that directly: you just write components.

## Enabling the module

To use TerminalInk, declare the module in the `uses` clause:

```npas
uses terminalink;
```

This makes every component (`Text`, `VBox`, `TextInput`, `Spinner`, ...) and the render/theme functions available.

## The render model (immediate mode)

TerminalInk works in **immediate mode**. Instead of building the component tree once, you write a **build function** that **returns** the UI tree. The `render` function takes that function and calls it repeatedly — every frame (~60 ms) — to redraw the screen.

Application state lives in **ordinary NeoObjectPascal variables**. Event callbacks (passed by function name) mutate those variables, and on the next frame the UI is rebuilt already reflecting the new state.

```npas
uses terminalink;

var nome: String;

function onNome(v): Boolean
begin
    nome := v;
    return true;
end;

function ui(): Object
begin
    return VBox(#{ gap: 1, padding: 1, border: "round" }, [
        Text(#{ bold: true, color: "cyan" }, "Cadastro"),
        TextInput(#{ placeholder: "Nome...", onChange: onNome }),
        Text(#{}, "Olá, " + nome)
    ]);
end;

begin
    render(ui);
end.
```

This program draws a box with a title, a text field, and a greeting. Each keystroke triggers `onNome`, which updates the `nome` variable; on the next frame, the "Olá, ..." line appears updated.

::: tip The cycle in one sentence
`state` (variables) → `ui()` returns the tree → `render` draws → the user interacts → the callback mutates the `state` → repeat.
:::

## Props and record literals

Every component receives its **props** as a **record literal** — the `#{ key: value, ... }` syntax:

```npas
Text(#{ bold: true, color: "yellow" }, "Warning")
```

Records are a language feature in their own right. You read a field with `record.field`, which also applies to the values delivered to callbacks. See [Variables and types](../language/variables-and-types) for more on records.

## Callbacks: functions passed by name

Event handlers (`onChange`, `onSubmit`, `onConfirm`, ...) receive the **name of a function**. The framework calls that function when the event happens:

```npas
function aoEnviar(v): Boolean
begin
    WriteLn("Enviado: " + v);
    return true;
end;

// ... inside ui():
TextInput(#{ placeholder: "Type and Enter", onSubmit: aoEnviar })
```

By convention, callbacks return `Boolean` (usually `true`). What matters is the side effect: mutating the state variables.

## Exit keys

To quit a TerminalInk application:

- **Esc** or **Ctrl+C** always exit.
- The **`q`** key also exits — **but only** when no `TextInput` (or input field) is focused. If a text field is focused, `q` is typed normally into the field.

::: warning Focus and the `q` key
If your UI has input fields, prefer telling the user to quit with **Esc**. The `q` key is an exit shortcut only when focus is not on an editable field.
:::

## How it works under the hood

- The foundation is **Lanterna**, which provides a cross-platform terminal with a **buffered screen** and key decoding.
- Each high-level component (`VBox`, `Badge`, `Spinner`, ...) **expands** into a tree of boxes and text (a *flexbox*-style layout).
- A small **layout engine** computes positions and sizes; then a **renderer** draws the tree to the buffered screen, frame by frame.

You do not need to understand these details to write applications — but they explain why the model is immediate mode and why layout props like `flexDirection`, `gap`, and `flexGrow` behave like *flexbox*.

---

Next, get to know all the available [Components](./components).
