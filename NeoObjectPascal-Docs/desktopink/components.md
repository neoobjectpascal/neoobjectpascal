# Componentes do DesktopInk

DesktopInk oferece **26 componentes**, organizados por categoria. Todos recebem props como um [literal de registro](../language/variables-and-types) `#{ ... }`.

## Layout

| Componente   | Propriedades                              | Descrição                           |
|--------------|-------------------------------------------|-------------------------------------|
| `Window`     | `padding`, `gap`, `title`                 | Raiz da tela (apenas uma)          |
| `Container`  | `padding`, `gap`, `width`                 | Centraliza e limita a largura      |
| `Section`    | `padding`, `gap`, `title`                 | Agrupamento com título opcional    |
| `Grid`       | `cols`, `gap`                             | Grade de colunas iguais            |
| `Row`        | `gap`, `align`                            | Eixo principal horizontal          |
| `Col`        | `gap`, `flex`                             | Eixo principal vertical            |
| `Card`       | `padding`, `gap`, `width`, `height`       | Superfície elevada com borda       |
| `Divider`    | —                                         | Linha separadora                   |
| `Spacer`     | `width`, `height`                         | Espaço elástico                    |
| `Sidebar`    | `width`, `gap`                            | Barra lateral fixa à esquerda      |

```npas
Row(#{ gap: 24 }, [
    Sidebar(#{ width: 220 }, [
        Heading(#{ level: 3 }, "Menu"),
        Button(#{ text: "Início", variant: "primary" }),
        Button(#{ text: "Config", variant: "secondary" })
    ]),
    Container(#{ width: 700 }, [
        Grid(#{ cols: 2, gap: 16 }, [
            Card(#{}, [ StatCard(#{ label: "Vendas", value: "R$ 50k" }) ]),
            Card(#{}, [ StatCard(#{ label: "Meta", value: "78%" }) ])
        ])
    ])
])
```

## Tipografia

| Componente | Propriedades                                           |
|------------|--------------------------------------------------------|
| `Heading`  | `level` de 1 a 3, `text`                               |
| `Text`     | `text`, `color`                                        |
| `Badge`    | `text`, `color`                                        |
| `StatCard` | `label`, `value`, `delta`                              |

```npas
Heading(#{ level: 1 }, "Dashboard")
Text(#{}, "Resumo do período.")
StatCard(#{ label: "Receita", value: "R$ 128k", delta: "+12%" })
```

## Formulários

| Componente      | Propriedades                                                       |
|-----------------|--------------------------------------------------------------------|
| `Button`        | `text`, `variant` (`primary`, `secondary`), `disabled`, `onClick` |
| `TextInput`     | `placeholder`, `value`, `onChange`, `autoFocus`, `width`           |
| `PasswordInput` | `placeholder`, `value`, `onChange`, `width`                        |
| `TextArea`      | `placeholder`, `value`, `onChange`, `rows`, `width`                |
| `Select`        | `options`, `value`, `onChange`, `width`                            |
| `Checkbox`      | `checked`, `onChange`, texto posicional                            |
| `Form`          | `gap`                                                              |

```npas
Text(#{}, "Nome completo"),
TextInput(#{ value: nome, width: 360, onChange: atualizarNome }),
Select(#{
    options: ["Admin", "Editor", "Leitor"],
    value: perfil,
    width: 360,
    onChange: atualizarPerfil
})
```

## Dados

| Componente   | Propriedades                                                           |
|--------------|-----------------------------------------------------------------------|
| `Table`      | `columns`, `rows`, `editable`, `editors`, `onChange`, `onSave`, `selectedRow`, `onRowClick` |
| `List`       | `items`                                                               |
| `Chart`      | `type`, `data`                                                        |
| `ProgressBar`| `value`, `showValue`                                                  |
| `Spinner`    | `size`                                                                |

```npas
Table(#{
    columns: ["Nome", "Papel", "Status"],
    rows: [["Ana", "Admin", "Ativo"], ["Bruno", "Editor", "Pendente"]],
    editable: true,
    editors: #{
        Papel: #{ options: ["Admin", "Editor", "Leitor"] },
        Status: #{ options: ["Ativo", "Pendente", "Inativo"] }
    },
    onChange: onCellChange,
    onSave: salvar
})
```

A tabela editável exibe células alteradas com fundo azul claro. O botão **Salvar alterações** coleta os valores e dispara o callback `onSave`.

## Feedback

| Componente    | Propriedades                              |
|---------------|-------------------------------------------|
| `Alert`       | `variant` (`info`, `success`, `warning`, `danger`), texto posicional |
| `ProgressBar` | `value`, `showValue`                      |
| `Spinner`     | texto posicional                          |

## Modal

| Propriedade       | Tipo    | Padrão   | Descrição                           |
|-------------------|---------|----------|-------------------------------------|
| `open`            | Boolean | `false`  | Controla a abertura                 |
| `title`           | String  | —        | Título do modal                     |
| `width`           | Integer | Tamanho intrínseco | Largura do painel          |
| `height`          | Integer | Tamanho intrínseco | Altura do painel           |
| `onClose`         | Callback| —        | Disparado ao fechar (Esc/botão)    |
| `closeOnEscape`   | Boolean | `true`   | Fecha com Esc                       |
| `closeOnBackdrop` | Boolean | `true`   | Fecha ao clicar fora                |

O modal abre como um overlay centralizado com fundo escuro translúcido, bloqueando interação com o conteúdo atrás.

```npas
Modal(#{ open: aberto, title: "Novo projeto", width: 560, height: 520 }, [
    Text(#{}, "Preencha os dados para criar o projeto."),
    TextInput(#{ placeholder: "Nome do projeto", width: 480 }),
    Text(#{}, "Responsável"),
    Select(#{ options: ["Ana", "Bruno", "Carla"], width: 480 })
])
```

## Integração com o editor visual

O editor `.xnpas` do VS Code suporta DesktopInk como terceiro alvo. Você pode alternar entre WebInk, TerminalInk e DesktopInk na barra de ferramentas, e o editor gera o código `.npas` correspondente. Modais e opções de janela como `centered` e `maximized` são preservados na sincronização bidirecional.

Veja o guia do [Editor visual (.xnpas)](../features/ui-builder).