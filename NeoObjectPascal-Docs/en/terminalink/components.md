# TerminalInk Components

TerminalInk provides **17 components**, organized into four groups: layout and text, interactive inputs, feedback and display, and lists. All of them receive props as a [record literal](../language/variables-and-types) `#{ ... }`.

Before we start, a note on **colors**. Wherever a prop accepts a color, the possible values (strings) are:

`red`, `green`, `yellow`, `blue`, `magenta`, `cyan`, `white`, `black`, `gray`, `default`.

## Layout and text

### Text

Displays a string with optional styles.

| Prop              | Type    | Description                        |
| ----------------- | ------- | ---------------------------------- |
| `color`           | String  | Text color                         |
| `backgroundColor` | String  | Background color                   |
| `bold`            | Boolean | Bold                               |
| `dim`             | Boolean | Dimmed                             |
| `inverse`         | Boolean | Swaps foreground and background    |

```npas
Text(#{ bold: true, color: "cyan" }, "Title")
```

### Box, VBox and HBox

*Flexbox*-style layout containers. `Box` is generic; `VBox` is a column (`flexDirection: "column"`) and `HBox` is a row (`flexDirection: "row"`). The second argument is the **list of children**.

| Prop             | Type         | Description                                                       |
| ---------------- | ------------ | ----------------------------------------------------------------- |
| `flexDirection`  | String       | `"row"` or `"column"`                                             |
| `gap`            | Integer      | Space between children                                            |
| `padding`        | Integer      | Inner padding                                                     |
| `border`         | String/Bool  | `"round"`, `"single"` or `true`                                   |
| `borderColor`    | String       | Border color                                                      |
| `flexGrow`       | Integer      | Growth factor (takes up the free space)                          |
| `width`          | Integer      | Fixed width                                                       |
| `height`         | Integer      | Fixed height                                                      |
| `alignItems`     | String       | `"start"`, `"center"` or `"end"` (cross axis)                    |
| `justifyContent` | String       | `"start"`, `"center"`, `"end"` or `"space-between"` (main axis)  |

```npas
VBox(#{ gap: 1, padding: 1, border: "round", borderColor: "blue" }, [
    Text(#{ bold: true }, "Panel"),
    HBox(#{ gap: 2 }, [
        Text(#{ color: "green" }, "OK"),
        Text(#{ color: "red" }, "Error")
    ])
])
```

### Spacer

A flexible filler: it pushes whatever comes after it toward the opposite edge. Useful inside an `HBox` or `VBox` to align elements.

```npas
HBox(#{}, [
    Text(#{}, "Left"),
    Spacer(),
    Text(#{}, "Right")
])
```

## Interactive inputs

### TextInput

Single-line text field.

| Prop          | Type     | Description                                 |
| ------------- | -------- | ------------------------------------------- |
| `placeholder` | String   | Text shown when empty                       |
| `onChange`    | Callback | Called on each keystroke, receives the text |
| `onSubmit`    | Callback | Called when Enter is pressed                |

```npas
TextInput(#{ placeholder: "Your name...", onChange: onNome, onSubmit: aoEnviar })
```

### EmailInput

Like `TextInput`, but it **autocompletes the email domain** after the `@`.

| Prop          | Type     | Description                                        |
| ------------- | -------- | -------------------------------------------------- |
| `placeholder` | String   | Text shown when empty                              |
| `domains`     | Array    | List of suggested domains (e.g. `["gmail.com"]`)   |
| `onChange`    | Callback | Called on each keystroke                           |
| `onSubmit`    | Callback | Called when Enter is pressed                       |

```npas
EmailInput(#{ placeholder: "email...", domains: ["gmail.com", "outlook.com"], onChange: onEmail })
```

### PasswordInput

Password field that **masks** the typed characters with `*`.

| Prop          | Type     | Description                  |
| ------------- | -------- | ---------------------------- |
| `placeholder` | String   | Text shown when empty        |
| `onChange`    | Callback | Called on each keystroke     |
| `onSubmit`    | Callback | Called when Enter is pressed |

```npas
PasswordInput(#{ placeholder: "Password...", onChange: onSenha })
```

### ConfirmInput

Yes/no confirmation (`y`/`n`).

| Prop            | Type     | Description                                   |
| --------------- | -------- | --------------------------------------------- |
| `defaultChoice` | String   | `"confirm"` or `"cancel"` (default option)    |
| `onConfirm`     | Callback | Called on confirm (`y`)                       |
| `onCancel`      | Callback | Called on cancel (`n`)                        |

```npas
ConfirmInput(#{ defaultChoice: "confirm", onConfirm: aoConfirmar, onCancel: aoCancelar })
```

### Select

Single-option selection from a list, navigating with **↑↓** and confirming with **Enter**.

| Prop           | Type     | Description                                        |
| -------------- | -------- | -------------------------------------------------- |
| `options`      | Array    | List of `#{ label, value }`                         |
| `onChange`     | Callback | Called when the selected option changes            |
| `visibleCount` | Integer  | How many options to show at once (scroll window)   |

```npas
Select(#{ options: [
    #{ label: "Small", value: "S" },
    #{ label: "Medium", value: "M" },
    #{ label: "Large", value: "L" }
], onChange: onTamanho, visibleCount: 5 })
```

### MultiSelect

Selection of **multiple** options. Navigate with **↑↓**, toggle the selection with **Space**, and submit the list with **Enter**.

| Prop       | Type     | Description                                    |
| ---------- | -------- | ---------------------------------------------- |
| `options`  | Array    | List of `#{ label, value }`                     |
| `onChange` | Callback | Called when the marking changes                |
| `onSubmit` | Callback | Called when Enter is pressed, receives the list |

```npas
MultiSelect(#{ options: [
    #{ label: "Bash", value: "bash" },
    #{ label: "Java", value: "java" },
    #{ label: "Pascal", value: "pascal" }
], onSubmit: onSelecionados })
```

## Feedback and display

### Spinner

**Animated** loading indicator.

| Prop    | Type   | Description                        |
| ------- | ------ | ---------------------------------- |
| `type`  | String | Animation style                    |
| `label` | String | Text shown next to the spinner     |

```npas
Spinner(#{ type: "dots", label: "Loading..." })
```

### ProgressBar

Progress bar that fills its own width according to the value (from 0 to 100).

| Prop    | Type    | Description               |
| ------- | ------- | ------------------------- |
| `value` | Integer | Progress from `0` to `100` |

```npas
ProgressBar(#{ value: 65 })
```

### Badge

Compact, colored label.

| Prop    | Type   | Description     |
| ------- | ------ | --------------- |
| `color` | String | Label color     |

```npas
Badge(#{ color: "green" }, "ACTIVE")
```

### StatusMessage

Status message with an **icon and color** based on the variant.

| Prop      | Type   | Description                                         |
| --------- | ------ | --------------------------------------------------- |
| `variant` | String | `"info"`, `"success"`, `"error"` or `"warning"`     |

```npas
StatusMessage(#{ variant: "success" }, "Saved successfully")
```

### Alert

**Bordered** box with a colored variant and an optional title. The second argument is the list of children (the body).

| Prop      | Type   | Description                                         |
| --------- | ------ | --------------------------------------------------- |
| `variant` | String | `"info"`, `"success"`, `"error"` or `"warning"`     |
| `title`   | String | Title shown at the top                              |

```npas
Alert(#{ variant: "warning", title: "Attention" }, [
    Text(#{}, "Check the data before continuing.")
])
```

## Lists

### UnorderedList and OrderedList

Lists of items. `UnorderedList` uses bullets; `OrderedList` numbers the items. Both take a list of `Item` as children.

### Item

A list item. The second argument is the content (text or children).

```npas
UnorderedList(#{}, [
    Item(#{}, "First"),
    Item(#{}, "Second")
])

OrderedList(#{}, [
    Item(#{}, "Step one"),
    Item(#{}, "Step two")
])
```

<Output>
• First
• Second

1. Step one
2. Step two
</Output>

---

With the components in hand, customize their appearance in [Theming](./theming).
