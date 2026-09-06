# Temas no TerminalInk

O TerminalInk tem um **sistema de temas** que permite definir estilos padrão para os componentes num único lugar, em vez de repetir props de cor em cada chamada. Um tema é um **registro** que mapeia nomes de componentes para conjuntos de estilos.

## Cores disponíveis

As cores usadas em props e temas são strings:

`red`, `green`, `yellow`, `blue`, `magenta`, `cyan`, `white`, `black`, `gray`, `default`.

## As funções de tema

| Função                       | Papel                                                                 |
| ---------------------------- | --------------------------------------------------------------------- |
| `defaultTheme()`             | Retorna o registro do tema padrão.                                    |
| `extendTheme(base, override)`| Faz uma **mesclagem profunda**: `override` vence sobre `base`.        |
| `setTheme(theme)`            | Define o tema global ativo. Chame **antes** de `render`.              |
| `ThemeProvider(#{ theme }, [filhos])` | Define o tema ativo e envolve os filhos.                     |

### defaultTheme e extendTheme

Comece a partir do tema padrão e sobrescreva apenas o que quiser. Um registro de tema mapeia nomes de componentes para estilos:

```npas
uses terminalink;

var t: Object;

function ui(): Object
begin
    return VBox(#{ gap: 1, padding: 1 }, [
        Spinner(#{ label: "Processando..." }),
        Badge(#{}, "PRO")
    ]);
end;

begin
    t := extendTheme(defaultTheme(), #{
        Spinner: #{ color: "magenta" },
        Badge: #{ color: "cyan" }
    });
    setTheme(t);
    render(ui);
end.
```

Aqui o `Spinner` fica magenta e o `Badge` fica ciano **sem** que essas cores apareçam nas chamadas dos componentes — elas vêm do tema.

### setTheme vs. ThemeProvider

- `setTheme(t)` define o tema global. É a forma mais direta: chame uma vez, antes de `render`.
- `ThemeProvider(#{ theme: t }, [ ... ])` define o tema ativo e envolve uma subárvore. Também é útil quando você prefere expressar o tema dentro da própria árvore de UI:

```npas
function ui(): Object
begin
    return ThemeProvider(#{ theme: t }, [
        VBox(#{ padding: 1 }, [ Badge(#{}, "PRO") ])
    ]);
end;
```

::: warning O tema é global no modo imediato
No modo imediato, o tema ativo é **global** — ele não é isolado por subárvore. Mesmo dentro de um `ThemeProvider`, o tema definido vale para toda a renderização daquele quadro. Trate o tema como uma configuração de aplicação, não como um estilo local.
:::

## Exemplo completo: um formulário

Este programa monta um pequeno formulário de cadastro com validação visual, combinando entradas e feedback.

```npas
uses terminalink;

var nome: String;
var email: String;
var enviado: Boolean;

function onNome(v): Boolean begin nome := v; return true; end;
function onEmail(v): Boolean begin email := v; return true; end;
function aoEnviar(v): Boolean begin enviado := true; return true; end;

function statusEmail(): Object
begin
    if enviado then
        return StatusMessage(#{ variant: "success" }, "Cadastro enviado!");
    return StatusMessage(#{ variant: "info" }, "Preencha e pressione Enter no e-mail.");
end;

function ui(): Object
begin
    return VBox(#{ gap: 1, padding: 1, border: "round", borderColor: "cyan" }, [
        Text(#{ bold: true, color: "cyan" }, "Cadastro"),
        HBox(#{ gap: 1 }, [ Text(#{ dim: true }, "Nome:"), TextInput(#{ placeholder: "Nome...", onChange: onNome }) ]),
        HBox(#{ gap: 1 }, [ Text(#{ dim: true }, "E-mail:"), EmailInput(#{ placeholder: "email...", domains: ["gmail.com"], onChange: onEmail, onSubmit: aoEnviar }) ]),
        Text(#{}, "Olá, " + nome),
        statusEmail()
    ]);
end;

begin
    enviado := false;
    setTheme(extendTheme(defaultTheme(), #{ StatusMessage: #{ } }));
    render(ui);
end.
```

Sair com **Esc** (há campos de entrada em foco, então evite `q`).

## Exemplo completo: um dashboard

Um painel de acompanhamento que reúne `Spinner`, `ProgressBar` e `Select`, com um tema aplicado.

```npas
uses terminalink;

var progresso: Integer;
var ambiente: String;

function onAmbiente(v): Boolean begin ambiente := v; return true; end;

function ui(): Object
begin
    return VBox(#{ gap: 1, padding: 1, border: "round", borderColor: "blue" }, [
        HBox(#{ gap: 2 }, [
            Text(#{ bold: true }, "Deploy"),
            Spacer(),
            Badge(#{ color: "green" }, ambiente)
        ]),
        Spinner(#{ type: "dots", label: "Publicando artefatos..." }),
        ProgressBar(#{ value: progresso }),
        Text(#{ dim: true }, "Ambiente de destino:"),
        Select(#{ options: [
            #{ label: "Produção", value: "prod" },
            #{ label: "Homologação", value: "staging" },
            #{ label: "Desenvolvimento", value: "dev" }
        ], onChange: onAmbiente, visibleCount: 3 })
    ]);
end;

begin
    progresso := 65;
    ambiente := "prod";
    setTheme(extendTheme(defaultTheme(), #{
        Spinner: #{ color: "cyan" },
        ProgressBar: #{ color: "green" },
        Badge: #{ color: "green" }
    }));
    render(ui);
end.
```

::: tip Combine com o resto da linguagem
As funções de construção da UI são funções normais do NeoObjectPascal. Você pode compor a árvore com [funções](../language/functions), reaproveitar lógica e até chamar [blocos Java](../features/java-integration) para obter dados. O TerminalInk é só mais uma biblioteca da linguagem.
:::

---

Volte ao início em [Introdução ao TerminalInk](./introduction) ou reveja todos os [Componentes](./components).
