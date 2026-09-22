# DesktopInk Components

DesktopInk offers **26 components**, organized by category. All receive props as a [record literal](../language/variables-and-types) `#{ ... }`.

## Layout

| Component   | Properties                              | Description                        |
|-------------|----------------------------------------|------------------------------------|
| `Window`    | `padding`, `gap`, `title`              | Screen root (one per screen)       |
| `Container` | `padding`, `gap`, `width`              | Center and constrain width         |
| `Section`   | `padding`, `gap`, `title`              | Grouping with optional title       |
| `Grid`      | `cols`, `gap`                          | Equal-column grid                  |
| `Row`       | `gap`, `align`                         | Horizontal main axis               |
| `Col`       | `gap`, `flex`                          | Vertical main axis                 |
| `Card`      | `padding`, `gap`, `width`, `height`    | Elevated surface with border       |
| `Divider`   | —                                      | Separator line                     |
| `Spacer`    | `width`, `height`                      | Elastic space                      |
| `Sidebar`   | `width`, `gap`                         | Fixed-width sidebar on the left    |

```npas
Row(#{ gap: 24 }, [
    Sidebar(#{ width: 220 }, [
        Heading(#{ level: 3 }, "Menu"),
        Button(#{ text: "Home", variant: "primary" }),
        Button(#{ text: "Settings", variant: "secondary" })
    ]),
    Container(#{ width: 700 }, [
        Grid(#{ cols: 2, gap: 16 }, [
            Card(#{}, [ StatCard(#{ label: "Revenue", value: "$50k" }) ]),
            Card(#{}, [ StatCard(#{ label: "Goal", value: "78%" }) ])
        ])
    ])
])
```

## Typography

| Component  | Properties                                           |
|------------|------------------------------------------------------|
| `Heading`  | `level` 1 to 3, `text`                               |
| `Text`     | `text`, `color`                                      |
| `Badge`    | `text`, `color`                                      |
| `StatCard` | `label`, `value`, `delta`                            |

```npas
Heading(#{ level: 1 }, "Dashboard")
Text(#{}, "Period summary.")
StatCard(#{ label: "Revenue", value: "$128k", delta: "+12%" })
```

## Forms

| Component        | Properties                                                         |
|------------------|--------------------------------------------------------------------|
| `Button`         | `text`, `variant` (`primary`, `secondary`), `disabled`, `onClick` |
| `TextInput`      | `placeholder`, `value`, `onChange`, `autoFocus`, `width`           |
| `PasswordInput`  | `placeholder`, `value`, `onChange`, `width`                        |
| `TextArea`       | `placeholder`, `value`, `onChange`, `rows`, `width`                |
| `Select`         | `options`, `value`, `onChange`, `width`                            |
| `Checkbox`       | `checked`, `onChange`, positional text                             |
| `Form`           | `gap`                                                              |

```npas
Text(#{}, "Full name"),
TextInput(#{ value: name, width: 360, onChange: updateName }),
Select(#{
    options: ["Admin", "Editor", "Reader"],
    value: profile,
    width: 360,
    onChange: updateProfile
})
```

## Data

| Component    | Properties                                                                         |
|--------------|------------------------------------------------------------------------------------|
| `Table`      | `columns`, `rows`, `editable`, `editors`, `onChange`, `onSave`, `selectedRow`, `onRowClick` |
| `List`       | `items`                                                                            |
| `Chart`      | `type`, `data`                                                                     |
| `ProgressBar`| `value`, `showValue`                                                               |
| `Spinner`    | `size`, positional text                                                            |

```npas
Table(#{
    columns: ["Name", "Role", "Status"],
    rows: [["Ana", "Admin", "Active"], ["Bruno", "Editor", "Pending"]],
    editable: true,
    editors: #{
        Role: #{ options: ["Admin", "Editor", "Reader"] },
        Status: #{ options: ["Active", "Pending", "Inactive"] }
    },
    onChange: onCellChange,
    onSave: onSave
})
```

The editable table displays modified cells with a light blue background. The **Save changes** button collects values and fires the `onSave` callback.

## Feedback

| Component     | Properties                                       |
|---------------|--------------------------------------------------|
| `Alert`       | `variant` (`info`, `success`, `warning`, `danger`), positional text |
| `ProgressBar` | `value`, `showValue`                              |
| `Spinner`     | positional text                                   |

## Modal

| Property          | Type    | Default  | Description                       |
|-------------------|---------|----------|----------------------------------|
| `open`            | Boolean | `false`  | Controls visibility               |
| `title`           | String  | —        | Modal title                       |
| `width`           | Integer | Intrinsic | Panel width                      |
| `height`          | Integer | Intrinsic | Panel height                     |
| `onClose`         | Callback| —        | Fired on close (Esc / button)    |
| `closeOnEscape`   | Boolean | `true`   | Close with Esc                    |
| `closeOnBackdrop` | Boolean | `true`   | Close by clicking outside         |

The modal opens as a centered overlay with a translucent dark backdrop, blocking interaction with content behind it.

```npas
Modal(#{ open: open, title: "New project", width: 560, height: 520 }, [
    Text(#{}, "Fill in the data to create the project."),
    TextInput(#{ placeholder: "Project name", width: 480 }),
    Text(#{}, "Responsible"),
    Select(#{ options: ["Ana", "Bruno", "Carla"], width: 480 })
])
```

## Visual editor integration

The `.xnpas` editor in VS Code supports DesktopInk as a third target. You can switch between WebInk, TerminalInk and DesktopInk in the toolbar, and the editor generates the corresponding `.npas` code. Modals and window options like `centered` and `maximized` are preserved in bidirectional sync.

See the [Visual UI Builder (.xnpas)](../features/ui-builder) guide.