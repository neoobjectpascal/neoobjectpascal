# Componentes do TerminalInk

O TerminalInk oferece **17 componentes**, organizados em quatro grupos: layout e texto, entradas interativas, feedback e exibição, e listas. Todos recebem props como um [literal de registro](../language/variables-and-types) `#{ ... }`.

Antes de começar, uma nota sobre **cores**. Onde uma prop aceita cor, os valores possíveis (strings) são:

`red`, `green`, `yellow`, `blue`, `magenta`, `cyan`, `white`, `black`, `gray`, `default`.

## Layout e texto

### Text

Exibe uma string com estilos opcionais.

| Prop              | Tipo    | Descrição                          |
| ----------------- | ------- | ---------------------------------- |
| `color`           | String  | Cor do texto                       |
| `backgroundColor` | String  | Cor de fundo                       |
| `bold`            | Boolean | Negrito                            |
| `dim`             | Boolean | Esmaecido                          |
| `inverse`         | Boolean | Inverte frente e fundo             |

```npas
Text(#{ bold: true, color: "cyan" }, "Título")
```

### Box, VBox e HBox

Contêineres de layout no estilo *flexbox*. `Box` é genérico; `VBox` é uma coluna (`flexDirection: "column"`) e `HBox` é uma linha (`flexDirection: "row"`). O segundo argumento é a **lista de filhos**.

| Prop             | Tipo         | Descrição                                                        |
| ---------------- | ------------ | ---------------------------------------------------------------- |
| `flexDirection`  | String       | `"row"` ou `"column"`                                            |
| `gap`            | Integer      | Espaço entre os filhos                                           |
| `padding`        | Integer      | Preenchimento interno                                            |
| `border`         | String/Bool  | `"round"`, `"single"` ou `true`                                  |
| `borderColor`    | String       | Cor da borda                                                     |
| `flexGrow`       | Integer      | Fator de crescimento (ocupa o espaço livre)                     |
| `width`          | Integer      | Largura fixa                                                     |
| `height`         | Integer      | Altura fixa                                                      |
| `alignItems`     | String       | `"start"`, `"center"` ou `"end"` (eixo transversal)             |
| `justifyContent` | String       | `"start"`, `"center"`, `"end"` ou `"space-between"` (eixo principal) |

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

Um preenchedor flexível: empurra o que vier depois para a extremidade oposta. Útil dentro de um `HBox` ou `VBox` para alinhar elementos.

```npas
HBox(#{}, [
    Text(#{}, "Esquerda"),
    Spacer(),
    Text(#{}, "Direita")
])
```

## Entradas interativas

### TextInput

Campo de texto de uma linha.

| Prop          | Tipo     | Descrição                                   |
| ------------- | -------- | ------------------------------------------- |
| `placeholder` | String   | Texto exibido quando vazio                  |
| `onChange`    | Callback | Chamado a cada tecla, recebe o texto atual  |
| `onSubmit`    | Callback | Chamado ao pressionar Enter                 |

```npas
TextInput(#{ placeholder: "Seu nome...", onChange: onNome, onSubmit: aoEnviar })
```

### EmailInput

Como o `TextInput`, mas **autocompleta o domínio** do e-mail depois do `@`.

| Prop          | Tipo     | Descrição                                        |
| ------------- | -------- | ------------------------------------------------ |
| `placeholder` | String   | Texto exibido quando vazio                       |
| `domains`     | Array    | Lista de domínios sugeridos (ex.: `["gmail.com"]`) |
| `onChange`    | Callback | Chamado a cada tecla                             |
| `onSubmit`    | Callback | Chamado ao pressionar Enter                      |

```npas
EmailInput(#{ placeholder: "email...", domains: ["gmail.com", "outlook.com"], onChange: onEmail })
```

### PasswordInput

Campo de senha que **mascara** os caracteres digitados com `*`.

| Prop          | Tipo     | Descrição                    |
| ------------- | -------- | ---------------------------- |
| `placeholder` | String   | Texto exibido quando vazio   |
| `onChange`    | Callback | Chamado a cada tecla         |
| `onSubmit`    | Callback | Chamado ao pressionar Enter  |

```npas
PasswordInput(#{ placeholder: "Senha...", onChange: onSenha })
```

### ConfirmInput

Confirmação **sim/não** (`y`/`n`).

| Prop            | Tipo     | Descrição                                     |
| --------------- | -------- | --------------------------------------------- |
| `defaultChoice` | String   | `"confirm"` ou `"cancel"` (opção padrão)      |
| `onConfirm`     | Callback | Chamado ao confirmar (`y`)                    |
| `onCancel`      | Callback | Chamado ao cancelar (`n`)                     |

```npas
ConfirmInput(#{ defaultChoice: "confirm", onConfirm: aoConfirmar, onCancel: aoCancelar })
```

### Select

Seleção de uma opção em uma lista, navegando com **↑↓** e confirmando com **Enter**.

| Prop           | Tipo     | Descrição                                          |
| -------------- | -------- | -------------------------------------------------- |
| `options`      | Array    | Lista de `#{ label, value }`                        |
| `onChange`     | Callback | Chamado quando a opção selecionada muda            |
| `visibleCount` | Integer  | Quantas opções mostrar por vez (janela de rolagem) |

```npas
Select(#{ options: [
    #{ label: "Pequeno", value: "P" },
    #{ label: "Médio", value: "M" },
    #{ label: "Grande", value: "G" }
], onChange: onTamanho, visibleCount: 5 })
```

### MultiSelect

Seleção de **várias** opções. Navegue com **↑↓**, alterne a seleção com **Espaço** e envie a lista com **Enter**.

| Prop       | Tipo     | Descrição                                       |
| ---------- | -------- | ----------------------------------------------- |
| `options`  | Array    | Lista de `#{ label, value }`                     |
| `onChange` | Callback | Chamado quando a marcação muda                  |
| `onSubmit` | Callback | Chamado ao pressionar Enter, recebe a lista     |

```npas
MultiSelect(#{ options: [
    #{ label: "Bash", value: "bash" },
    #{ label: "Java", value: "java" },
    #{ label: "Pascal", value: "pascal" }
], onSubmit: onSelecionados })
```

## Feedback e exibição

### Spinner

Indicador de carregamento **animado**.

| Prop    | Tipo   | Descrição                          |
| ------- | ------ | ---------------------------------- |
| `type`  | String | Estilo da animação                 |
| `label` | String | Texto exibido ao lado do spinner   |

```npas
Spinner(#{ type: "dots", label: "Carregando..." })
```

### ProgressBar

Barra de progresso que preenche a própria largura conforme o valor (de 0 a 100).

| Prop    | Tipo    | Descrição                 |
| ------- | ------- | ------------------------- |
| `value` | Integer | Progresso de `0` a `100`  |

```npas
ProgressBar(#{ value: 65 })
```

### Badge

Rótulo colorido e compacto.

| Prop    | Tipo   | Descrição       |
| ------- | ------ | --------------- |
| `color` | String | Cor do rótulo   |

```npas
Badge(#{ color: "green" }, "ATIVO")
```

### StatusMessage

Mensagem de status com **ícone e cor** conforme a variante.

| Prop      | Tipo   | Descrição                                           |
| --------- | ------ | --------------------------------------------------- |
| `variant` | String | `"info"`, `"success"`, `"error"` ou `"warning"`     |

```npas
StatusMessage(#{ variant: "success" }, "Salvo com sucesso")
```

### Alert

Caixa com **borda**, variante colorida e título opcional. O segundo argumento é a lista de filhos (o corpo).

| Prop      | Tipo   | Descrição                                           |
| --------- | ------ | --------------------------------------------------- |
| `variant` | String | `"info"`, `"success"`, `"error"` ou `"warning"`     |
| `title`   | String | Título exibido no topo                              |

```npas
Alert(#{ variant: "warning", title: "Atenção" }, [
    Text(#{}, "Verifique os dados antes de continuar.")
])
```

## Listas

### UnorderedList e OrderedList

Listas de itens. `UnorderedList` usa marcadores; `OrderedList` numera os itens. Ambas recebem uma lista de `Item` como filhos.

### Item

Um item de lista. O segundo argumento é o conteúdo (texto ou filhos).

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

Com os componentes na mão, personalize a aparência em [Temas](./theming).
