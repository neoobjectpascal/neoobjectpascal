# Componenti di TerminalInk

TerminalInk offre **17 componenti**, organizzati in quattro gruppi: layout e testo, input interattivi, feedback e visualizzazione, ed elenchi. Tutti ricevono le props come un [literal di record](../language/variables-and-types) `#{ ... }`.

Prima di iniziare, una nota sui **colori**. Dove una prop accetta un colore, i valori possibili (stringhe) sono:

`red`, `green`, `yellow`, `blue`, `magenta`, `cyan`, `white`, `black`, `gray`, `default`.

## Layout e testo

### Text

Mostra una stringa con stili opzionali.

| Prop              | Tipo    | Descrizione                        |
| ----------------- | ------- | ---------------------------------- |
| `color`           | String  | Colore del testo                   |
| `backgroundColor` | String  | Colore di sfondo                   |
| `bold`            | Boolean | Grassetto                          |
| `dim`             | Boolean | Attenuato                          |
| `inverse`         | Boolean | Inverte primo piano e sfondo       |

```npas
Text(#{ bold: true, color: "cyan" }, "Título")
```

### Box, VBox e HBox

Contenitori di layout in stile *flexbox*. `Box` è generico; `VBox` è una colonna (`flexDirection: "column"`) e `HBox` è una riga (`flexDirection: "row"`). Il secondo argomento è la **lista dei figli**.

| Prop             | Tipo         | Descrizione                                                      |
| ---------------- | ------------ | ---------------------------------------------------------------- |
| `flexDirection`  | String       | `"row"` o `"column"`                                             |
| `gap`            | Integer      | Spazio tra i figli                                              |
| `padding`        | Integer      | Riempimento interno                                             |
| `border`         | String/Bool  | `"round"`, `"single"` o `true`                                  |
| `borderColor`    | String       | Colore del bordo                                               |
| `flexGrow`       | Integer      | Fattore di crescita (occupa lo spazio libero)                  |
| `width`          | Integer      | Larghezza fissa                                                |
| `height`         | Integer      | Altezza fissa                                                  |
| `alignItems`     | String       | `"start"`, `"center"` o `"end"` (asse trasversale)             |
| `justifyContent` | String       | `"start"`, `"center"`, `"end"` o `"space-between"` (asse principale) |

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

Un riempitivo flessibile: spinge ciò che viene dopo verso l'estremità opposta. Utile all'interno di un `HBox` o `VBox` per allineare gli elementi.

```npas
HBox(#{}, [
    Text(#{}, "Esquerda"),
    Spacer(),
    Text(#{}, "Direita")
])
```

## Input interattivi

### TextInput

Campo di testo su una sola riga.

| Prop          | Tipo     | Descrizione                                 |
| ------------- | -------- | ------------------------------------------- |
| `placeholder` | String   | Testo mostrato quando è vuoto               |
| `onChange`    | Callback | Chiamato a ogni tasto, riceve il testo attuale |
| `onSubmit`    | Callback | Chiamato alla pressione di Invio            |

```npas
TextInput(#{ placeholder: "Seu nome...", onChange: onNome, onSubmit: aoEnviar })
```

### EmailInput

Come il `TextInput`, ma **completa automaticamente il dominio** dell'e-mail dopo la `@`.

| Prop          | Tipo     | Descrizione                                      |
| ------------- | -------- | ------------------------------------------------ |
| `placeholder` | String   | Testo mostrato quando è vuoto                    |
| `domains`     | Array    | Elenco di domini suggeriti (es.: `["gmail.com"]`) |
| `onChange`    | Callback | Chiamato a ogni tasto                            |
| `onSubmit`    | Callback | Chiamato alla pressione di Invio                 |

```npas
EmailInput(#{ placeholder: "email...", domains: ["gmail.com", "outlook.com"], onChange: onEmail })
```

### PasswordInput

Campo password che **maschera** i caratteri digitati con `*`.

| Prop          | Tipo     | Descrizione                  |
| ------------- | -------- | ---------------------------- |
| `placeholder` | String   | Testo mostrato quando è vuoto |
| `onChange`    | Callback | Chiamato a ogni tasto        |
| `onSubmit`    | Callback | Chiamato alla pressione di Invio |

```npas
PasswordInput(#{ placeholder: "Senha...", onChange: onSenha })
```

### ConfirmInput

Conferma **sì/no** (`y`/`n`).

| Prop            | Tipo     | Descrizione                                   |
| --------------- | -------- | --------------------------------------------- |
| `defaultChoice` | String   | `"confirm"` o `"cancel"` (opzione predefinita) |
| `onConfirm`     | Callback | Chiamato alla conferma (`y`)                  |
| `onCancel`      | Callback | Chiamato all'annullamento (`n`)               |

```npas
ConfirmInput(#{ defaultChoice: "confirm", onConfirm: aoConfirmar, onCancel: aoCancelar })
```

### Select

Selezione di un'opzione in un elenco, navigando con **↑↓** e confermando con **Invio**.

| Prop           | Tipo     | Descrizione                                        |
| -------------- | -------- | -------------------------------------------------- |
| `options`      | Array    | Elenco di `#{ label, value }`                       |
| `onChange`     | Callback | Chiamato quando l'opzione selezionata cambia       |
| `visibleCount` | Integer  | Quante opzioni mostrare per volta (finestra di scorrimento) |

```npas
Select(#{ options: [
    #{ label: "Pequeno", value: "P" },
    #{ label: "Médio", value: "M" },
    #{ label: "Grande", value: "G" }
], onChange: onTamanho, visibleCount: 5 })
```

### MultiSelect

Selezione di **più** opzioni. Naviga con **↑↓**, alterna la selezione con **Spazio** e invia l'elenco con **Invio**.

| Prop       | Tipo     | Descrizione                                     |
| ---------- | -------- | ----------------------------------------------- |
| `options`  | Array    | Elenco di `#{ label, value }`                    |
| `onChange` | Callback | Chiamato quando la selezione cambia             |
| `onSubmit` | Callback | Chiamato alla pressione di Invio, riceve l'elenco |

```npas
MultiSelect(#{ options: [
    #{ label: "Bash", value: "bash" },
    #{ label: "Java", value: "java" },
    #{ label: "Pascal", value: "pascal" }
], onSubmit: onSelecionados })
```

## Feedback e visualizzazione

### Spinner

Indicatore di caricamento **animato**.

| Prop    | Tipo   | Descrizione                        |
| ------- | ------ | ---------------------------------- |
| `type`  | String | Stile dell'animazione              |
| `label` | String | Testo mostrato accanto allo spinner |

```npas
Spinner(#{ type: "dots", label: "Carregando..." })
```

### ProgressBar

Barra di avanzamento che riempie la propria larghezza in base al valore (da 0 a 100).

| Prop    | Tipo    | Descrizione               |
| ------- | ------- | ------------------------- |
| `value` | Integer | Avanzamento da `0` a `100` |

```npas
ProgressBar(#{ value: 65 })
```

### Badge

Etichetta colorata e compatta.

| Prop    | Tipo   | Descrizione        |
| ------- | ------ | ------------------ |
| `color` | String | Colore dell'etichetta |

```npas
Badge(#{ color: "green" }, "ATIVO")
```

### StatusMessage

Messaggio di stato con **icona e colore** in base alla variante.

| Prop      | Tipo   | Descrizione                                         |
| --------- | ------ | --------------------------------------------------- |
| `variant` | String | `"info"`, `"success"`, `"error"` o `"warning"`      |

```npas
StatusMessage(#{ variant: "success" }, "Salvo com sucesso")
```

### Alert

Riquadro con **bordo**, variante colorata e titolo opzionale. Il secondo argomento è la lista dei figli (il corpo).

| Prop      | Tipo   | Descrizione                                         |
| --------- | ------ | --------------------------------------------------- |
| `variant` | String | `"info"`, `"success"`, `"error"` o `"warning"`      |
| `title`   | String | Titolo mostrato in alto                             |

```npas
Alert(#{ variant: "warning", title: "Atenção" }, [
    Text(#{}, "Verifique os dados antes de continuar.")
])
```

## Elenchi

### UnorderedList e OrderedList

Elenchi di elementi. `UnorderedList` usa marcatori; `OrderedList` numera gli elementi. Entrambi ricevono una lista di `Item` come figli.

### Item

Un elemento di elenco. Il secondo argomento è il contenuto (testo o figli).

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

Con i componenti a disposizione, personalizza l'aspetto in [Temi](./theming).
