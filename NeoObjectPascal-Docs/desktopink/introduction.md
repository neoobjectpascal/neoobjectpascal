# DesktopInk — introdução

**DesktopInk** é o framework de aplicações desktop nativas do NeoObjectPascal. Com `uses desktopink;` você constrói janelas nativas com componentes visuais declarativos, tema claro/escuro, e o mesmo modelo reativo dos outros runtimes.

Diferente do TerminalInk (terminal) e do WebInk (servidor web), o DesktopInk abre uma **janela Swing/Java2D real** — sem dependências externas. Cada componente é desenhado pelo framework com cantos arredondados, tipografia do sistema e aparência profissional inspirada em [shadcn/ui](https://ui.shadcn.com).

```npas
uses desktopink;

function principal(): Object
begin
    return Window(#{ padding: 24 }, [
        Heading(#{ level: 1, text: "Olá, DesktopInk!" }),
        Text(#{}, "Esta é uma janela nativa.")
    ]);
end;

begin
    render(#{ principal: principal }, #{
        title: "Meu App",
        width: 800,
        height: 600,
        centered: true,
        theme: "auto"
    });
end.
```

::: tip TerminalInk, WebInk e DesktopInk não se misturam
Um programa é **totalmente TerminalInk, totalmente WebInk ou totalmente DesktopInk**. Usar dois `uses` de interface diferentes no mesmo programa gera um erro claro.
:::

## Opções de renderização

O segundo argumento de `render` aceita estas opções:

| Opção       | Tipo    | Padrão   | Descrição                               |
|-------------|---------|----------|-----------------------------------------|
| `title`     | String  | App name | Título da janela                        |
| `width`     | Integer | 960      | Largura inicial                         |
| `height`    | Integer | 720      | Altura inicial                          |
| `centered`  | Boolean | `true`   | Centraliza a janela na tela             |
| `maximized` | Boolean | `false`  | Abre a janela maximizada                |
| `theme`     | String  | `"auto"` | `"light"`, `"dark"` ou `"auto"` (sistema) |

```npas
render(#{ principal: principal }, #{
    title: "Painel",
    width: 1100,
    height: 720,
    centered: true,
    maximized: false,
    theme: "dark"
});
```

## Tema

DesktopInk tem dois temas completos — claro e escuro — mais o modo `auto`, que segue a preferência do sistema.

Os tokens visuais são compartilhados com o WebInk, garantindo que os dois runtimes pareçam o mesmo produto. Você pode alternar o tema com:

```npas
setTheme("dark");
setTheme("light");
setTheme("auto");
```

## Componentes

DesktopInk oferece mais de 25 componentes, de layout a formulários, tabelas, gráficos e modais. Veja a [lista completa de componentes](./components).

![DesktopInk DataGrid editável](/screenshots/desktop-ink.jpg)
*DataGrid editável com destaque de células alteradas no tema escuro.*

## Como funciona

1. `render(#{ telas }, opts)` abre uma janela Swing e constrói a árvore de componentes.
2. O estado vive em variáveis NeoObjectPascal.
3. Eventos (clique, digitação, mudança de Select) chamam callbacks NeoObjectPascal.
4. Após cada callback, o runtime reconstrói a interface com o novo estado.
5. Janelas rodam na EDT (Event Dispatch Thread) do Swing; a reconstrução é segura.

O layout usa `BoxLayout` e `GridLayout`, com cantos arredondados pintados em Java2D. Campos de texto usam `InputHost` nativo do Swing, mas estilizados para parecerem parte do tema.

::: tip Executável nativo
Use `--build` para gerar um executável nativo com `jpackage`. O módulo `java.desktop` entra na imagem de runtime, resultando em um binário autocontido de 15–30 MB.
:::

A seguir, conheça todos os [Componentes do DesktopInk](./components).