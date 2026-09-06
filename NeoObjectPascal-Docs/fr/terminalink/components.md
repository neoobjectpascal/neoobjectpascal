# Composants TerminalInk

TerminalInk propose **17 composants**, organisés en quatre groupes : disposition et texte, saisies interactives, retour d'information et affichage, et listes. Tous reçoivent leurs props sous forme d'un [littéral d'enregistrement](../language/variables-and-types) `#{ ... }`.

Avant de commencer, une note sur les **couleurs**. Là où une prop accepte une couleur, les valeurs possibles (chaînes) sont :

`red`, `green`, `yellow`, `blue`, `magenta`, `cyan`, `white`, `black`, `gray`, `default`.

## Disposition et texte

### Text

Affiche une chaîne avec des styles optionnels.

| Prop              | Type    | Description                        |
| ----------------- | ------- | ---------------------------------- |
| `color`           | String  | Couleur du texte                   |
| `backgroundColor` | String  | Couleur de fond                    |
| `bold`            | Boolean | Gras                               |
| `dim`             | Boolean | Atténué                            |
| `inverse`         | Boolean | Inverse le premier plan et le fond |

```npas
Text(#{ bold: true, color: "cyan" }, "Título")
```

### Box, VBox et HBox

Conteneurs de disposition de style *flexbox*. `Box` est générique ; `VBox` est une colonne (`flexDirection: "column"`) et `HBox` est une ligne (`flexDirection: "row"`). Le second argument est la **liste des enfants**.

| Prop             | Type         | Description                                                        |
| ---------------- | ------------ | ---------------------------------------------------------------- |
| `flexDirection`  | String       | `"row"` ou `"column"`                                            |
| `gap`            | Integer      | Espace entre les enfants                                          |
| `padding`        | Integer      | Marge intérieure                                                 |
| `border`         | String/Bool  | `"round"`, `"single"` ou `true`                                  |
| `borderColor`    | String       | Couleur de la bordure                                            |
| `flexGrow`       | Integer      | Facteur de croissance (occupe l'espace libre)                    |
| `width`          | Integer      | Largeur fixe                                                     |
| `height`         | Integer      | Hauteur fixe                                                     |
| `alignItems`     | String       | `"start"`, `"center"` ou `"end"` (axe transversal)             |
| `justifyContent` | String       | `"start"`, `"center"`, `"end"` ou `"space-between"` (axe principal) |

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

Un remplisseur flexible : il pousse ce qui suit vers l'extrémité opposée. Utile à l'intérieur d'un `HBox` ou d'un `VBox` pour aligner des éléments.

```npas
HBox(#{}, [
    Text(#{}, "Esquerda"),
    Spacer(),
    Text(#{}, "Direita")
])
```

## Saisies interactives

### TextInput

Champ de texte sur une ligne.

| Prop          | Type     | Description                                    |
| ------------- | -------- | ---------------------------------------------- |
| `placeholder` | String   | Texte affiché lorsque le champ est vide        |
| `onChange`    | Callback | Appelé à chaque touche, reçoit le texte actuel |
| `onSubmit`    | Callback | Appelé lors de l'appui sur Entrée              |

```npas
TextInput(#{ placeholder: "Seu nome...", onChange: onNome, onSubmit: aoEnviar })
```

### EmailInput

Comme `TextInput`, mais **complète automatiquement le domaine** de l'e-mail après le `@`.

| Prop          | Type     | Description                                          |
| ------------- | -------- | --------------------------------------------------- |
| `placeholder` | String   | Texte affiché lorsque le champ est vide             |
| `domains`     | Array    | Liste de domaines suggérés (ex. : `["gmail.com"]`)  |
| `onChange`    | Callback | Appelé à chaque touche                              |
| `onSubmit`    | Callback | Appelé lors de l'appui sur Entrée                   |

```npas
EmailInput(#{ placeholder: "email...", domains: ["gmail.com", "outlook.com"], onChange: onEmail })
```

### PasswordInput

Champ de mot de passe qui **masque** les caractères saisis avec `*`.

| Prop          | Type     | Description                              |
| ------------- | -------- | ---------------------------------------- |
| `placeholder` | String   | Texte affiché lorsque le champ est vide  |
| `onChange`    | Callback | Appelé à chaque touche                   |
| `onSubmit`    | Callback | Appelé lors de l'appui sur Entrée        |

```npas
PasswordInput(#{ placeholder: "Senha...", onChange: onSenha })
```

### ConfirmInput

Confirmation **oui/non** (`y`/`n`).

| Prop            | Type     | Description                                     |
| --------------- | -------- | ---------------------------------------------- |
| `defaultChoice` | String   | `"confirm"` ou `"cancel"` (option par défaut)  |
| `onConfirm`     | Callback | Appelé lors de la confirmation (`y`)           |
| `onCancel`      | Callback | Appelé lors de l'annulation (`n`)              |

```npas
ConfirmInput(#{ defaultChoice: "confirm", onConfirm: aoConfirmar, onCancel: aoCancelar })
```

### Select

Sélection d'une option dans une liste, avec navigation par **↑↓** et confirmation par **Entrée**.

| Prop           | Type     | Description                                            |
| -------------- | -------- | ----------------------------------------------------- |
| `options`      | Array    | Liste de `#{ label, value }`                           |
| `onChange`     | Callback | Appelé lorsque l'option sélectionnée change           |
| `visibleCount` | Integer  | Nombre d'options affichées à la fois (fenêtre de défilement) |

```npas
Select(#{ options: [
    #{ label: "Pequeno", value: "P" },
    #{ label: "Médio", value: "M" },
    #{ label: "Grande", value: "G" }
], onChange: onTamanho, visibleCount: 5 })
```

### MultiSelect

Sélection de **plusieurs** options. Naviguez avec **↑↓**, basculez la sélection avec **Espace** et envoyez la liste avec **Entrée**.

| Prop       | Type     | Description                                    |
| ---------- | -------- | ---------------------------------------------- |
| `options`  | Array    | Liste de `#{ label, value }`                    |
| `onChange` | Callback | Appelé lorsque le marquage change              |
| `onSubmit` | Callback | Appelé lors de l'appui sur Entrée, reçoit la liste |

```npas
MultiSelect(#{ options: [
    #{ label: "Bash", value: "bash" },
    #{ label: "Java", value: "java" },
    #{ label: "Pascal", value: "pascal" }
], onSubmit: onSelecionados })
```

## Retour d'information et affichage

### Spinner

Indicateur de chargement **animé**.

| Prop    | Type   | Description                          |
| ------- | ------ | ------------------------------------ |
| `type`  | String | Style de l'animation                 |
| `label` | String | Texte affiché à côté du spinner      |

```npas
Spinner(#{ type: "dots", label: "Carregando..." })
```

### ProgressBar

Barre de progression qui remplit sa propre largeur en fonction de la valeur (de 0 à 100).

| Prop    | Type    | Description                  |
| ------- | ------- | ---------------------------- |
| `value` | Integer | Progression de `0` à `100`   |

```npas
ProgressBar(#{ value: 65 })
```

### Badge

Étiquette colorée et compacte.

| Prop    | Type   | Description         |
| ------- | ------ | ------------------- |
| `color` | String | Couleur de l'étiquette |

```npas
Badge(#{ color: "green" }, "ATIVO")
```

### StatusMessage

Message de statut avec **icône et couleur** selon la variante.

| Prop      | Type   | Description                                         |
| --------- | ------ | --------------------------------------------------- |
| `variant` | String | `"info"`, `"success"`, `"error"` ou `"warning"`     |

```npas
StatusMessage(#{ variant: "success" }, "Salvo com sucesso")
```

### Alert

Boîte avec **bordure**, variante colorée et titre optionnel. Le second argument est la liste des enfants (le corps).

| Prop      | Type   | Description                                         |
| --------- | ------ | --------------------------------------------------- |
| `variant` | String | `"info"`, `"success"`, `"error"` ou `"warning"`     |
| `title`   | String | Titre affiché en haut                               |

```npas
Alert(#{ variant: "warning", title: "Atenção" }, [
    Text(#{}, "Verifique os dados antes de continuar.")
])
```

## Listes

### UnorderedList et OrderedList

Listes d'éléments. `UnorderedList` utilise des puces ; `OrderedList` numérote les éléments. Toutes deux reçoivent une liste d'`Item` comme enfants.

### Item

Un élément de liste. Le second argument est le contenu (texte ou enfants).

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

Avec les composants en main, personnalisez l'apparence dans [Thèmes](./theming).
