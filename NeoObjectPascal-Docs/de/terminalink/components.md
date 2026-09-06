# TerminalInk-Komponenten

TerminalInk bietet **17 Komponenten**, gegliedert in vier Gruppen: Layout und Text, interaktive Eingaben, Feedback und Anzeige sowie Listen. Alle erhalten Props als [Record-Literal](../language/variables-and-types) `#{ ... }`.

Bevor es losgeht, ein Hinweis zu **Farben**. Wo eine Prop eine Farbe akzeptiert, sind die möglichen Werte (Strings):

`red`, `green`, `yellow`, `blue`, `magenta`, `cyan`, `white`, `black`, `gray`, `default`.

## Layout und Text

### Text

Zeigt einen String mit optionalen Stilen an.

| Prop              | Typ     | Beschreibung                       |
| ----------------- | ------- | ---------------------------------- |
| `color`           | String  | Textfarbe                          |
| `backgroundColor` | String  | Hintergrundfarbe                   |
| `bold`            | Boolean | Fett                               |
| `dim`             | Boolean | Gedämpft                           |
| `inverse`         | Boolean | Kehrt Vorder- und Hintergrund um   |

```npas
Text(#{ bold: true, color: "cyan" }, "Título")
```

### Box, VBox und HBox

Layout-Container im *Flexbox*-Stil. `Box` ist generisch; `VBox` ist eine Spalte (`flexDirection: "column"`) und `HBox` eine Zeile (`flexDirection: "row"`). Das zweite Argument ist die **Liste der Kinder**.

| Prop             | Typ          | Beschreibung                                                     |
| ---------------- | ------------ | ---------------------------------------------------------------- |
| `flexDirection`  | String       | `"row"` oder `"column"`                                          |
| `gap`            | Integer      | Abstand zwischen den Kindern                                     |
| `padding`        | Integer      | Innenabstand                                                     |
| `border`         | String/Bool  | `"round"`, `"single"` oder `true`                               |
| `borderColor`    | String       | Rahmenfarbe                                                      |
| `flexGrow`       | Integer      | Wachstumsfaktor (füllt den freien Platz)                        |
| `width`          | Integer      | Feste Breite                                                     |
| `height`         | Integer      | Feste Höhe                                                       |
| `alignItems`     | String       | `"start"`, `"center"` oder `"end"` (Querachse)                  |
| `justifyContent` | String       | `"start"`, `"center"`, `"end"` oder `"space-between"` (Hauptachse) |

```npas
VBox(#{ gap: 1, padding: 1, border: "round", borderColor: "blue" }, [
    Text(#{ bold: true }, "Painel"),
    HBox(#{ gap: 2 }, [
        Text(#{ color: "green" }, "OK"),
        Text(#{ color: "red" }, "Erro")
    ])
])
```

### Spacer

Ein flexibler Füller: Er schiebt das Folgende an das gegenüberliegende Ende. Nützlich innerhalb eines `HBox` oder `VBox`, um Elemente auszurichten.

```npas
HBox(#{}, [
    Text(#{}, "Esquerda"),
    Spacer(),
    Text(#{}, "Direita")
])
```

## Interaktive Eingaben

### TextInput

Einzeiliges Textfeld.

| Prop          | Typ      | Beschreibung                                  |
| ------------- | -------- | --------------------------------------------- |
| `placeholder` | String   | Angezeigter Text, wenn leer                   |
| `onChange`    | Callback | Bei jeder Taste aufgerufen, erhält den aktuellen Text |
| `onSubmit`    | Callback | Beim Drücken von Enter aufgerufen             |

```npas
TextInput(#{ placeholder: "Seu nome...", onChange: onNome, onSubmit: aoEnviar })
```

### EmailInput

Wie `TextInput`, aber **vervollständigt die Domain** der E-Mail nach dem `@` automatisch.

| Prop          | Typ      | Beschreibung                                       |
| ------------- | -------- | -------------------------------------------------- |
| `placeholder` | String   | Angezeigter Text, wenn leer                        |
| `domains`     | Array    | Liste vorgeschlagener Domains (z. B. `["gmail.com"]`) |
| `onChange`    | Callback | Bei jeder Taste aufgerufen                         |
| `onSubmit`    | Callback | Beim Drücken von Enter aufgerufen                  |

```npas
EmailInput(#{ placeholder: "email...", domains: ["gmail.com", "outlook.com"], onChange: onEmail })
```

### PasswordInput

Passwortfeld, das die eingegebenen Zeichen mit `*` **maskiert**.

| Prop          | Typ      | Beschreibung                    |
| ------------- | -------- | ------------------------------- |
| `placeholder` | String   | Angezeigter Text, wenn leer     |
| `onChange`    | Callback | Bei jeder Taste aufgerufen      |
| `onSubmit`    | Callback | Beim Drücken von Enter aufgerufen |

```npas
PasswordInput(#{ placeholder: "Senha...", onChange: onSenha })
```

### ConfirmInput

Ja/Nein-Bestätigung (`y`/`n`).

| Prop            | Typ      | Beschreibung                                  |
| --------------- | -------- | --------------------------------------------- |
| `defaultChoice` | String   | `"confirm"` oder `"cancel"` (Standardoption)  |
| `onConfirm`     | Callback | Beim Bestätigen aufgerufen (`y`)              |
| `onCancel`      | Callback | Beim Abbrechen aufgerufen (`n`)               |

```npas
ConfirmInput(#{ defaultChoice: "confirm", onConfirm: aoConfirmar, onCancel: aoCancelar })
```

### Select

Auswahl einer Option aus einer Liste, Navigation mit **↑↓** und Bestätigung mit **Enter**.

| Prop           | Typ      | Beschreibung                                       |
| -------------- | -------- | -------------------------------------------------- |
| `options`      | Array    | Liste von `#{ label, value }`                       |
| `onChange`     | Callback | Aufgerufen, wenn sich die ausgewählte Option ändert |
| `visibleCount` | Integer  | Wie viele Optionen gleichzeitig anzeigen (Scrollfenster) |

```npas
Select(#{ options: [
    #{ label: "Pequeno", value: "P" },
    #{ label: "Médio", value: "M" },
    #{ label: "Grande", value: "G" }
], onChange: onTamanho, visibleCount: 5 })
```

### MultiSelect

Auswahl **mehrerer** Optionen. Navigieren Sie mit **↑↓**, schalten Sie die Auswahl mit **Leertaste** um und senden Sie die Liste mit **Enter**.

| Prop       | Typ      | Beschreibung                                    |
| ---------- | -------- | ----------------------------------------------- |
| `options`  | Array    | Liste von `#{ label, value }`                    |
| `onChange` | Callback | Aufgerufen, wenn sich die Markierung ändert     |
| `onSubmit` | Callback | Beim Drücken von Enter aufgerufen, erhält die Liste |

```npas
MultiSelect(#{ options: [
    #{ label: "Bash", value: "bash" },
    #{ label: "Java", value: "java" },
    #{ label: "Pascal", value: "pascal" }
], onSubmit: onSelecionados })
```

## Feedback und Anzeige

### Spinner

**Animierter** Ladeindikator.

| Prop    | Typ    | Beschreibung                       |
| ------- | ------ | ---------------------------------- |
| `type`  | String | Stil der Animation                 |
| `label` | String | Neben dem Spinner angezeigter Text |

```npas
Spinner(#{ type: "dots", label: "Carregando..." })
```

### ProgressBar

Fortschrittsbalken, der seine eigene Breite je nach Wert füllt (von 0 bis 100).

| Prop    | Typ     | Beschreibung              |
| ------- | ------- | ------------------------- |
| `value` | Integer | Fortschritt von `0` bis `100` |

```npas
ProgressBar(#{ value: 65 })
```

### Badge

Farbiges und kompaktes Label.

| Prop    | Typ    | Beschreibung    |
| ------- | ------ | --------------- |
| `color` | String | Farbe des Labels |

```npas
Badge(#{ color: "green" }, "ATIVO")
```

### StatusMessage

Statusmeldung mit **Symbol und Farbe** je nach Variante.

| Prop      | Typ    | Beschreibung                                        |
| --------- | ------ | --------------------------------------------------- |
| `variant` | String | `"info"`, `"success"`, `"error"` oder `"warning"`   |

```npas
StatusMessage(#{ variant: "success" }, "Salvo com sucesso")
```

### Alert

Kasten mit **Rahmen**, farbiger Variante und optionalem Titel. Das zweite Argument ist die Liste der Kinder (der Inhalt).

| Prop      | Typ    | Beschreibung                                        |
| --------- | ------ | --------------------------------------------------- |
| `variant` | String | `"info"`, `"success"`, `"error"` oder `"warning"`   |
| `title`   | String | Oben angezeigter Titel                              |

```npas
Alert(#{ variant: "warning", title: "Atenção" }, [
    Text(#{}, "Verifique os dados antes de continuar.")
])
```

## Listen

### UnorderedList und OrderedList

Listen von Einträgen. `UnorderedList` verwendet Aufzählungszeichen; `OrderedList` nummeriert die Einträge. Beide erhalten eine Liste von `Item` als Kinder.

### Item

Ein Listeneintrag. Das zweite Argument ist der Inhalt (Text oder Kinder).

```npas
UnorderedList(#{}, [
    Item(#{}, "Primeiro"),
    Item(#{}, "Segundo")
])

OrderedList(#{}, [
    Item(#{}, "Passo um"),
    Item(#{}, "Passo dois")
])
```

<Output>
• Primeiro
• Segundo

1. Passo um
2. Passo dois
</Output>

---

Mit den Komponenten in der Hand passen Sie das Erscheinungsbild unter [Themes](./theming) an.
