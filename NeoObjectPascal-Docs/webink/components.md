# WebInk — componentes

Todo widget aceita um record de props `#{}` como primeiro argumento e, depois, filhos (outros nós) e/ou texto. Todos aceitam `className` para adicionar classes Tailwind.

## Layout

| Widget | Descrição |
|--------|-----------|
| `Page` | casca da página (fundo, cor de texto) |
| `Section` | seção com espaçamento vertical |
| `Container` | largura máxima centralizada com padding |
| `Grid(#{ cols })` | grade responsiva de `cols` colunas |
| `Row` / `Col` | linha flex e coluna flexível |
| `Card` | cartão com borda, sombra e padding |
| `Divider` · `Spacer` | separador · espaço vertical |

```npas
Grid(#{ cols: 3 }, [
    Card(#{}, [ Heading(#{ level: 4 }, "A"), Text(#{}, "conteúdo") ]),
    Card(#{}, [ Heading(#{ level: 4 }, "B") ]),
    Card(#{}, [ Heading(#{ level: 4 }, "C") ])
])
```

## Tipografia

| Widget | Descrição |
|--------|-----------|
| `Heading(#{ level: 1..6 })` | título (h1–h6) |
| `Text` | parágrafo |
| `Badge(#{ color })` | selo colorido |
| `StatCard(#{ label, value, delta })` | cartão de KPI (rótulo, valor, variação) |

```npas
StatCard(#{ label: "Receita", value: "R$ 128k", delta: "+12%" })
```

## Navegação

| Widget | Descrição |
|--------|-----------|
| `Navbar` | barra de navegação superior |
| `Sidebar` | barra lateral |
| `Link(#{ href })` | link de navegação (rota) |
| `Tabs` | faixa de abas |

## Formulários

| Widget | Props principais |
|--------|------------------|
| `Button(#{ variant, onClick })` | `variant`: `primary` \| `secondary` \| `danger` |
| `TextInput(#{ placeholder, value, onChange, onSubmit })` | `onSubmit` dispara no Enter |
| `TextArea(#{ rows, value, onChange })` | área de texto |
| `Select(#{ options, onChange })` | `options`: array de valores |
| `Checkbox(#{ label, checked, onChange })` | caixa de seleção |
| `Form(#{ onSubmit })` | agrupa campos; envia no submit |

```npas
Form(#{ onSubmit: enviar }, [
    TextInput(#{ placeholder: "E-mail", onChange: onEmail }),
    Button(#{ onClick: enviar }, "Entrar")
])
```

## Dados e feedback

| Widget | Descrição |
|--------|-----------|
| `Table(#{ columns, rows })` | tabela; `columns` e `rows` são arrays |
| `List(#{ items, ordered })` | lista com marcadores ou numerada |
| `Chart(#{ type, data, options })` | gráfico Chart.js |
| `Alert(#{ variant })` | aviso (`info`/`success`/`warning`/`error`) |
| `ProgressBar(#{ value })` | barra de progresso (0–100) |
| `Spinner` | indicador de carregamento |

```npas
Table(#{
    columns: ["Produto", "Qtd", "Total"],
    rows: [ ["Café", "120", "R$ 600"], ["Chá", "80", "R$ 320"] ]
})
```

## Gráficos (Chart.js)

O widget `Chart` mapeia diretamente para a configuração do [Chart.js](https://www.chartjs.org): `type`, `data` e `options` são records `#{}` que viram o JSON do gráfico.

```npas
Chart(#{
    type: "bar",
    data: #{
        labels: ["Jan", "Fev", "Mar"],
        datasets: [
            #{ label: "Vendas", data: [30, 45, 38] },
            #{ label: "Meta",   data: [40, 40, 40] }
        ]
    },
    options: #{ plugins: #{ legend: #{ position: "bottom" } } }
})
```

Tipos suportados: `bar`, `line`, `pie`, `doughnut`, `radar`, e os demais do Chart.js. Os gráficos são reinstanciados a cada redesenho.

## Navegação com rotas

`render` recebe um mapa de rotas (URL → função de tela). A URL do navegador reflete a tela atual, e o botão **Voltar** e os **deep links** funcionam.

```npas
function home(): Object      begin return Page(#{}, [ /* ... */ ]); end;
function dashboard(): Object begin return Page(#{}, [ /* ... */ ]); end;

begin
    render(#{
        "/":          home,
        "/dashboard": dashboard
    });
end.
```

Duas formas de navegar:

- **`Link(#{ href: "/dashboard" }, "Ir")`** — link declarativo (`<a>`), interceptado no cliente com `pushState`.
- **`navigate("/dashboard")`** — dentro de um callback, muda a rota programaticamente (o servidor avisa o navegador).

```npas
function irParaPainel(): Boolean
begin
    navigate("/dashboard");
    return true;
end;
```

::: tip Chaves de rota são strings
As chaves do mapa de rotas são strings (`"/"`, `"/dashboard"`) — os record literals aceitam chaves string além de identificadores.
:::

## Próximos passos

Para distribuir seu app como um executável nativo, veja [Gerar executáveis nativos](../testing/building-executables). Para a sintaxe completa da linguagem, veja a [Referência da linguagem](../reference/language-reference).
