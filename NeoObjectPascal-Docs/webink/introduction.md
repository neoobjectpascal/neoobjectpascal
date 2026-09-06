# WebInk — introdução

**WebInk** é o framework de frontend web do NeoObjectPascal — o análogo web do [TerminalInk](../terminalink/introduction). Com `uses webink;` você constrói uma interface web **profissional** em código NeoObjectPascal, estilizada com **Tailwind CSS** e com gráficos via **Chart.js**, e navega entre telas com URLs reais.

Diferente de um gerador estático, o WebInk é um **app vivo**: `render(...)` sobe um **servidor local** e abre o navegador. O estado vive nas variáveis; a cada interação (clique, digitação, envio) um callback roda no servidor, muda o estado e a tela é redesenhada — uma UI **dirigida pelo servidor**, exatamente como o loop reativo do TerminalInk, mas na web.

::: warning TerminalInk e WebInk não se misturam
Um programa é **totalmente TerminalInk ou totalmente WebInk**. Usar `uses terminalink` e `uses webink` no mesmo programa gera um erro claro.
:::

## Primeiro app

```npas
uses webink;

var cliques: Integer;

function registrar(): Boolean
begin
    cliques := cliques + 1;
    return true;
end;

function home(): Object
begin
    return Page(#{}, [
        Navbar(#{}, [
            Heading(#{ level: 3 }, "Acme Inc."),
            Link(#{ href: "/relatorio" }, "Relatório")
        ]),
        Container(#{ className: "py-8 space-y-6" }, [
            Heading(#{ level: 1 }, "Painel"),
            Grid(#{ cols: 3 }, [
                StatCard(#{ label: "Receita", value: "R$ 128k", delta: "+12%" }),
                StatCard(#{ label: "Usuários", value: "3.420" }),
                StatCard(#{ label: "Cliques", value: cliques })
            ]),
            Card(#{}, [
                Heading(#{ level: 4 }, "Vendas por mês"),
                Chart(#{ type: "line", data: #{
                    labels: ["Jan", "Fev", "Mar", "Abr"],
                    datasets: [ #{ label: "R$ mil", data: [30, 45, 38, 52] } ]
                } })
            ]),
            Button(#{ onClick: registrar }, "Registrar clique")
        ])
    ]);
end;

begin
    cliques := 0;
    render(#{ "/": home });
end.
```

Rode e o navegador abre com o painel; clicar no botão incrementa o contador ao vivo.

## Como funciona

1. `render(rotas)` inicia um servidor HTTP local **single-thread** e abre o navegador.
2. O navegador carrega um shell com Tailwind + Chart.js (embutidos, offline) e um runtime JS pequeno.
3. O runtime pede ao servidor a tela da rota atual; o servidor chama a função de build, gera o HTML e devolve.
4. Numa interação, o runtime envia o evento ao servidor, que chama o **callback NeoObjectPascal**, atualiza o estado, redesenha a tela e devolve o novo HTML.

O estado vive em variáveis globais, como no TerminalInk. Os callbacks mudam o estado; a função da tela atual relê e redesenha.

## Estado e eventos

Widgets interativos recebem callbacks por props: `onClick`, `onChange`, `onSubmit`. O callback é uma função que muda o estado e retorna:

```npas
var nome: String;

function onNome(v): Boolean
begin
    nome := v;
    return true;
end;

// ...
TextInput(#{ placeholder: "Seu nome", value: nome, onChange: onNome })
```

::: tip Props e palavras reservadas
`class` e `to` são palavras reservadas — por isso os widgets usam **`className`** (estilo React) e **`href`** (no `Link`). Qualquer outra chave "reservada" pode ser passada como string: `#{ "class": "..." }`.
:::

## Rodar, buildar e depurar

- **Rodar:** `java -jar neoobjectpascal.jar app.npas` sobe o servidor e abre o navegador; encerra com **Ctrl+C**. No VS Code, use **Run**.
- **Buildar nativo:** `--build` empacota o app (com os assets embutidos) — o executável autocontido sobe o servidor e abre o navegador, **offline**. Veja [Gerar executáveis nativos](../testing/building-executables).
- **Depurar:** WebInk tem a **melhor experiência de debug**. O servidor é single-thread e o navegador é apenas um cliente — sem a janela Swing do terminal. Ponha um breakpoint numa função de tela ou num callback: ele dispara quando você interage no navegador, a pausa é determinística e o **Step Into (F7)** entra nas suas funções normalmente. Veja [Debugger, VS Code e nuvem](../testing/debugging-tools).

## Próximos passos

Conheça os [componentes do WebInk](./components) — layout, tipografia, formulários, tabelas, gráficos Chart.js e navegação com rotas.
