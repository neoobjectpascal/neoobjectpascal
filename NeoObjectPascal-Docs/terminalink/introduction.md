# Introdução ao TerminalInk

O **TerminalInk** é o framework de interfaces de terminal do NeoObjectPascal. Inspirado no [React Ink](https://github.com/vadimdemedes/ink), ele deixa você construir aplicações de terminal (TUIs) de forma **declarativa** — você descreve como a tela deve ficar e o framework cuida de desenhá-la, redesenhá-la e decodificar o teclado.

Por baixo dos panos, o TerminalInk é apoiado pela biblioteca Java [Lanterna](https://github.com/mabe02/lanterna), que oferece um terminal multiplataforma, uma tela com buffer e a decodificação de teclas. Você nunca precisa mexer nisso diretamente: basta escrever componentes.

## Habilitando o módulo

Para usar o TerminalInk, declare o módulo na cláusula `uses`:

```npas
uses terminalink;
```

Isso disponibiliza todos os componentes (`Text`, `VBox`, `TextInput`, `Spinner`, ...) e as funções de renderização e tema.

## O modelo de renderização (modo imediato)

O TerminalInk trabalha em **modo imediato**. Em vez de montar a árvore de componentes uma vez, você escreve uma **função de construção** que **retorna** a árvore da interface. A função `render` recebe essa função e a chama repetidamente — a cada quadro (~60 ms) — para redesenhar a tela.

O estado da aplicação vive em **variáveis normais** do NeoObjectPascal. Os *callbacks* de eventos (passados pelo nome da função) alteram essas variáveis, e no quadro seguinte a interface é reconstruída já refletindo o novo estado.

```npas
uses terminalink;

var nome: String;

function onNome(v): Boolean
begin
    nome := v;
    return true;
end;

function ui(): Object
begin
    return VBox(#{ gap: 1, padding: 1, border: "round" }, [
        Text(#{ bold: true, color: "cyan" }, "Cadastro"),
        TextInput(#{ placeholder: "Nome...", onChange: onNome }),
        Text(#{}, "Olá, " + nome)
    ]);
end;

begin
    render(ui);
end.
```

Esse programa desenha uma caixa com título, um campo de texto e uma saudação. Cada tecla digitada dispara `onNome`, que atualiza a variável `nome`; no próximo quadro, a linha "Olá, ..." aparece atualizada.

::: tip O ciclo em uma frase
`estado` (variáveis) → `ui()` retorna a árvore → `render` desenha → o usuário interage → o *callback* muda o `estado` → repete.
:::

## Props e literais de registro

Todo componente recebe suas **props** como um **literal de registro** — a sintaxe `#{ chave: valor, ... }`:

```npas
Text(#{ bold: true, color: "yellow" }, "Atenção")
```

Registros são um recurso da própria linguagem. Você lê um campo com `registro.campo`, o que também vale para valores que chegam nos *callbacks*. Veja [Variáveis e tipos](../language/variables-and-types) para mais sobre registros.

## Callbacks: funções passadas por nome

Os manipuladores de evento (`onChange`, `onSubmit`, `onConfirm`, ...) recebem o **nome de uma função**. Essa função é chamada pelo framework quando o evento acontece:

```npas
function aoEnviar(v): Boolean
begin
    WriteLn("Enviado: " + v);
    return true;
end;

// ... dentro do ui():
TextInput(#{ placeholder: "Digite e Enter", onSubmit: aoEnviar })
```

Por convenção, os *callbacks* retornam `Boolean` (normalmente `true`). O importante é o efeito colateral: mudar as variáveis de estado.

## Teclas de saída

Para encerrar uma aplicação TerminalInk:

- **Esc** ou **Ctrl+C** sempre saem.
- A tecla **`q`** também sai — **mas apenas** quando nenhum `TextInput` (ou campo de entrada) está em foco. Se um campo de texto estiver focado, `q` é digitado normalmente no campo.

::: warning Foco e a tecla `q`
Se a sua interface tem campos de entrada, prefira instruir o usuário a sair com **Esc**. A tecla `q` só é atalho de saída quando o foco não está sobre um campo editável.
:::

## Como funciona por baixo dos panos

- A base é a **Lanterna**, que fornece um terminal multiplataforma com **tela em buffer** e decodificação de teclas.
- Cada componente de alto nível (`VBox`, `Badge`, `Spinner`, ...) **expande** para uma árvore de caixas e textos (um layout no estilo *flexbox*).
- Um pequeno **motor de layout** calcula posições e tamanhos; em seguida, um **renderizador** desenha a árvore na tela com buffer, quadro a quadro.

Você não precisa entender esses detalhes para escrever aplicações — mas eles explicam por que o modelo é de modo imediato e por que props de layout como `flexDirection`, `gap` e `flexGrow` se comportam como no *flexbox*.

---

A seguir, conheça todos os [Componentes](./components) disponíveis.
