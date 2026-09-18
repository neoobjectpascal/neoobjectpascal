# Visual UI Builder (.xnpas)

The VS Code extension includes a **visual (WYSIWYG) editor** to build interfaces **without writing code** — for both the terminal (**TerminalInk**) and the web (**WebInk**). You assemble the screen by dragging components, tweak properties and events, and the editor automatically generates the matching `.npas` file.

## The `.xnpas` file

The editor works with **`.xnpas`** files — a JSON that holds the screen design. When you save `test.xnpas`, the editor **(re)generates** the sibling `test.npas`. Sync runs **both ways**: on save, the newer file updates the other. The generated `.npas` carries a header that marks it as a file of the visual editor.

## Creating a screen

1. Open the command palette (`Cmd/Ctrl+Shift+P`) and run **"New UI Builder File (.xnpas)"**.
2. Pick a template: *WebInk — blank*, *WebInk — Dashboard*, *TerminalInk — blank* or *TerminalInk — Form*.
3. The file opens directly in the visual editor.

## The editor layout

- **Palette** (left) — the components for the chosen target, grouped. Drag one onto the canvas.
- **Canvas** (center) — a faithful preview. Click to select a component; drag a placed component to **reorder** it (or move it into another container). Empty containers (a freshly added `Grid`, say) show a dashed area labelled *Drop a component here*, wide enough to take the drag.
- **Inspector** (right) — **Properties**, **State** and **Events** tabs.
- **Toolbar** — the **WebInk / TerminalInk** toggle, the screen/route picker, and **Run live** (generates and runs the `.npas`).

## State and events

- **State** — declare global variables (name, type and initial value) shared across screens and events.
- **Events** — for props like `onClick`, `onChange`, `onSubmit`, `onConfirm` and `onCancel`, use the **hybrid editor**: pick a **no-code action** (increment a variable, set a variable, navigate to a screen, use the typed value) **or** switch to **Code** and write NeoObjectPascal freely. The generated function is previewed right there.

## Targets: WebInk or TerminalInk

An `.xnpas` is entirely **WebInk** or **TerminalInk**. The toolbar toggle switches the target (resetting the tree, since the component sets differ).

- **WebInk** — `Page`, `Container`, `Section`, `Grid`, `Row`, `Col`, `Card`, `Navbar`, `Sidebar`, `Tabs`, `Heading`, `Text`, `Badge`, `StatCard`, `Link`, `Button`, `TextInput`, `TextArea`, `Select`, `Checkbox`, `Form`, `Table`, `List`, `Chart`, `Alert`, `ProgressBar`, `Spinner`.
- **TerminalInk** — `VBox`, `HBox`, `Box`, `Spacer`, `Text`, `Badge`, `TextInput`, `PasswordInput`, `EmailInput`, `ConfirmInput`, `Select`, `MultiSelect`, `Spinner`, `ProgressBar`, `StatusMessage`, `Alert`, `UnorderedList`, `OrderedList`.

## Sync between the `.xnpas` and the `.npas`

The two files stay in step both ways, and the modification time decides who wins: on save, the newer file updates the other.

- Saved `test.xnpas`? `test.npas` is regenerated from the design.
- Saved a generated `test.npas`? The changes are read back into `test.xnpas` and show up in the visual editor.
- Opened the visual editor? Before drawing the screen, it compares the timestamps and adopts the newer file.
- Does the visual editor hold unsaved changes? The import is held back: the editor warns you and waits for a save or an undo, rather than discarding work that never reached the disk.

On a tie the `.xnpas` wins, as the canonical source of the design.

### What the way back understands

Reading a `.npas` back into the design covers exactly what the editor emits: the `uses` clause, the state variables, the event functions, the screen functions and the `render` call. Hence the three rules below.

- Only a `.npas` carrying the generated-file header is read back. A hand-written file never overwrites the `.xnpas`.
- If the file was changed beyond that shape, the editor warns you, keeps the `.xnpas` untouched and offers to regenerate the `.npas`.
- Comments and code outside the generated shape do not survive the trip back.

::: tip
A `test.xnpas` generates a `test.npas`. Run the `.npas` normally (the *Run* button), or use **Run live** straight from the visual editor.
:::

### Going back to one-way

The setting **`neoobjectpascal.uiBuilder.sync`** takes `bidirectional` (the default) or `xnpasFirst`, which keeps the `.xnpas` as the only source and always overwrites the `.npas`.

## Visibility, dynamic data, and focus

Three features let screens react to state at runtime.

### The `visible` property

Every component has a **Visible** control in the Inspector: *Always* (default) or *Condition (fx)*. In *Condition* mode, enter a boolean expression — the component (and its subtree) only appears when it is true. For example, a `ConfirmInput` that shows only after the name is filled, with the condition `nome <> ""`.

### Data from variables (fx)

Data fields — `options` (Select/MultiSelect), `columns`/`rows` (Table), the chart (Chart), `value` (ProgressBar), `items` (List), and the value fields — carry an **fx** button. When enabled, the field stops taking a fixed value and instead binds to the name of a **variable or expression**. This lets you populate a component with data from an API, for instance, rather than hand-typed values.

### Focus in TerminalInk

TerminalInk input components (`TextInput`, `PasswordInput`, `EmailInput`, `ConfirmInput`, `Select`, `MultiSelect`) gain two fields:

- **Key** (`key`) — a stable identifier for the component.
- **Initial focus** (`autoFocus`) — puts focus on it on the very first frame.

In addition, the event editor offers a **Focus component** action that emits `focus("key")` — handy, for example, to return focus to a field when a confirmation is cancelled.

## Editor language

The visual editor is **multilingual**, in the same 5 languages as the documentation: **Português, English, Deutsch, Français, Italiano**. A language selector (globe icon) in the top bar switches the entire editor UI on the fly — palette, properties, events, hints, and messages.

The preference lives in the **`neoobjectpascal.uiBuilder.language`** setting (global, persistent). The default is **`auto`**, which follows the VS Code display language and falls back to Portuguese when it isn't one of the five. Picking a language in the selector writes this setting.
