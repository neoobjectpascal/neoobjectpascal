# DesktopInk - NeoObjectPascal

> Especificação de implementação. O recurso ainda não existe no código: este documento
> descreve o desenho aprovado para que a implementação aconteça depois, fase a fase.

## Visão Geral

O NeoObjectPascal tem hoje dois recursos de interface: **TerminalInk**, para aplicações de
terminal, e **WebInk**, para aplicações web servidas localmente. O **DesktopInk** é o terceiro:
janelas nativas, bonitas e profissionais, com a mesma linguagem declarativa dos outros dois.

O programa descreve a tela como uma árvore de componentes, o runtime desenha essa árvore numa
janela e os eventos chamam funções NeoObjectPascal que alteram o estado global. Quem já escreveu
uma tela em WebInk escreve uma em DesktopInk sem aprender nada novo.

```npas
uses desktopink;

var cliques: Integer;

function registrar(): Boolean
begin
    cliques := cliques + 1;
    return true;
end;

function principal(): Object
begin
    return Window(#{}, [
        Container(#{ padding: 24, gap: 16 }, [
            Heading(#{ level: 1, text: "Meu Painel" }),
            Grid(#{ cols: 3, gap: 16 }, [
                StatCard(#{ label: "Receita", value: "R$ 128k", delta: "+12%" }),
                StatCard(#{ label: "Usuários", value: "3.420", delta: "+5%" }),
                StatCard(#{ label: "Cliques", value: cliques })
            ]),
            Button(#{ text: "Registrar clique", variant: "primary", onClick: registrar })
        ])
    ]);
end;

begin
    cliques := 0;
    render(#{ principal: principal }, #{ title: "Painel", width: 1100, height: 720 });
end.
```

## Decisão de arquitetura

O DesktopInk é desenhado inteiramente com **Swing e Java2D**, que acompanham o JDK
(Java Development Kit), **sem nenhuma dependência nova**.

### Alternativas consideradas e descartadas

| Caminho | Custo no jar | Por que não |
|---|---|---|
| JavaFX | 40 a 60 MB, com bibliotecas nativas por sistema | Inviável diante da restrição de tamanho |
| Webview nativo embutido | 1 a 3 MB mais nativos por sistema | Traz uma ponte JNI (Java Native Interface) e variação de comportamento entre sistemas |
| Swing com FlatLaf | 886 KB, cerca de 10% do jar | Resolve só a aparência de quatro controles; o resto seria pintado por nós de qualquer forma |

### O desenho híbrido

A escolha não é desenhar tudo à mão por teimosia, e sim por onde o ganho está:

- **Pintado por nós em Java2D**: layout, containers, tipografia, cartões, badges, abas, barra
  lateral, tabelas, listas, gráficos, barras de progresso e botões. Esses componentes não existem
  no Swing, então seriam nossos em qualquer cenário. Pintá-los dá controle visual total e o mesmo
  resultado em macOS, Windows e Linux.
- **Componentes Swing reais, hospedados sobre o canvas**: apenas `JTextField`, `JPasswordField` e
  `JTextArea`, para entrada de texto. Isso nos poupa cursor, seleção, teclado internacional e IME
  (Input Method Editor), que é a parte cara e chata de reimplementar. Estilizados com borda, fonte
  e cores nossas, esses três não denunciam a origem.

### Orçamento de tamanho

- **Jar**: crescimento zero em dependências. Só as classes novas, algumas dezenas de quilobytes.
- **Executável nativo** gerado por `--build`: o jpackage monta a imagem de runtime a partir dos
  módulos usados, e o `java.desktop` passa a entrar. A estimativa é de 15 a 30 MB sobre o artefato
  atual. Isso precisa ser **medido na fase 1** e registrado, não tratado como promessa.

## Arquitetura

Pacote novo `com.neoobjectpascal.desktop`, espelhando a estrutura que os dois recursos atuais já
seguem:

| Papel | TerminalInk | WebInk | DesktopInk |
|---|---|---|---|
| Registro no `uses` | `TerminalInk` | `WebInk` | `DesktopInk` |
| Árvore de nós | `TuiNode` | `WebNode` | `DeskNode` |
| Construtores nativos | `Widgets` e outros | `WebWidgets` | `DeskWidgets` |
| Leitura de propriedades | `tui.Props` | `web.Props` | `desktop.Props` |
| Coerção de filhos | `tui.NodeCoercion` | interno | `desktop.NodeCoercion` |
| Tema | `tui.Theme` | classes do WebInk | `desktop.Theme` |
| Layout | `tui.LayoutEngine` (células) | o navegador | `desktop.LayoutEngine` (pixels) |
| Pintura | `tui.Renderer` | `web.HtmlRenderer` | `desktop.Painter` |
| Runtime e eventos | `tui.TerminalRuntime` | `web.WebRuntime` | `desktop.DesktopRuntime` |

Classes sem equivalente nos outros dois:

- `desktop.InputHost` posiciona os componentes Swing de texto sobre o canvas pintado, acompanhando
  o layout a cada quadro.
- `desktop.Charts` desenha barras, linhas e pizza em Java2D. É o equivalente do `chart.umd.js` que
  o WebInk carrega no navegador, e aqui sai de graça em tamanho.

### Ligação com o interpretador

Duas mudanças pequenas em `Interpreter.java`:

1. Em `visitUsesClause`, reconhecer `desktopink` e `internal.desktopink`, chamando
   `registerNativeModule("desktopink")`.
2. Em `registerNativeModule`, a exclusão mútua passa de duas para três vias. Hoje a mensagem diz
   que um programa é totalmente TerminalInk ou totalmente WebInk; ela passa a valer para os três,
   pelo mesmo motivo de antes: os três definem `render` e `navigate` e miram superfícies
   diferentes.

Os widgets são funções nativas registradas por `interp.registerNative(nome, fn)`, exatamente como
nos outros dois recursos. **A gramática não muda**, nenhum token novo, nenhuma regra nova.

## Modelo de programação

### A cláusula `uses`

```npas
uses desktopink;
```

### A chamada `render`

```npas
render(#{ telaInicial: telaInicial, config: config }, #{
    title: "Minha Aplicação",
    width: 1100,
    height: 720,
    minWidth: 720,
    minHeight: 480,
    theme: "auto"
});
```

O primeiro argumento é o mapa de telas, com identificadores como chave, igual ao TerminalInk com
várias telas. A primeira entrada é a tela inicial. O segundo argumento é opcional e configura a
janela. `theme` aceita `auto`, `light` e `dark`, sendo `auto` o padrão, que segue o sistema quando
o sistema informa a preferência e cai em claro quando não informa.

### Navegação e foco

- `navigate("nome")` troca a tela ativa, como no TerminalInk.
- `focus("chave")` move o foco de teclado para o componente cuja propriedade `key` corresponde.

Ambos seguem a assinatura e o comportamento dos nativos homônimos do TerminalInk, inclusive na
forma de serem chamados de dentro de um manipulador de evento.

### Eventos

As propriedades de evento recebem uma referência de função NeoObjectPascal, sem aspas, como já
acontece nos outros recursos:

| Propriedade | Onde aparece | Assinatura esperada |
|---|---|---|
| `onClick` | `Button`, `Link`, `Card` clicável | `function(): Boolean` |
| `onChange` | `TextInput`, `PasswordInput`, `TextArea`, `Select`, `Checkbox` | `function(valor): Boolean` |
| `onSubmit` | `Form`, `TextInput` com Enter | `function(): Boolean` |

O runtime chama a função por `interp.callCallback(fn, args)`, o mesmo caminho que o `WebRuntime`
usa hoje. A função altera as variáveis globais e o runtime repinta a tela atual.

## Catálogo de widgets

Os nomes espelham os do WebInk sempre que o componente existe lá, para que uma tela possa migrar
entre as duas superfícies com pouco atrito.

### Layout

| Widget | Propriedades | Notas |
|---|---|---|
| `Window` | `padding`, `gap`, `background` | A raiz de uma tela. Uma tela tem exatamente uma |
| `Container` | `padding`, `gap`, `maxWidth`, `align` | Centraliza e limita a largura do conteúdo |
| `Section` | `padding`, `gap`, `title` | Agrupamento com título opcional |
| `Grid` | `cols`, `gap`, `rowGap` | Grade de colunas iguais |
| `Row` | `gap`, `align`, `justify`, `wrap` | Eixo principal horizontal |
| `Col` | `gap`, `align`, `justify`, `flex` | Eixo principal vertical |
| `Card` | `padding`, `gap`, `elevation`, `onClick` | Superfície elevada com raio e borda |
| `Divider` | `spacing` | Linha separadora |
| `Spacer` | `size`, `flex` | Espaço fixo ou elástico |

### Tipografia

| Widget | Propriedades |
|---|---|
| `Heading` | `level` de 1 a 6, `text`, `color` |
| `Text` | `text`, `color`, `bold`, `dim`, `size`, `align` |
| `Badge` | `text`, `color` |
| `StatCard` | `label`, `value`, `delta`, `trend` |

### Navegação

| Widget | Propriedades |
|---|---|
| `Navbar` | `title`, `gap` mais filhos |
| `Sidebar` | `width`, `gap` mais filhos |
| `Tabs` | `active`, `onChange` mais filhos `Tab` |
| `Link` | `text`, `to`, `onClick` |

### Formulários

| Widget | Propriedades |
|---|---|
| `Button` | `text`, `variant` (`primary`, `secondary`, `ghost`, `danger`), `disabled`, `onClick` |
| `TextInput` | `placeholder`, `value`, `key`, `autoFocus`, `onChange`, `onSubmit` |
| `PasswordInput` | as mesmas de `TextInput` |
| `TextArea` | as de `TextInput` mais `rows` |
| `Select` | `options`, `value`, `placeholder`, `onChange` |
| `Checkbox` | `label`, `checked`, `onChange` |
| `Form` | `gap` mais filhos |

### Dados e feedback

| Widget | Propriedades |
|---|---|
| `Table` | `columns`, `rows`, `striped`, `onRowClick` |
| `List` | `items`, `marker`, `onItemClick` |
| `Chart` | `type` (`bar`, `line`, `pie`), `data` com `labels` e `datasets` |
| `Alert` | `variant`, `title`, `text` |
| `ProgressBar` | `value` de 0 a 100, `label` |
| `Spinner` | `size`, `label` |

Todo widget aceita também `visible`, com o mesmo significado que tem no WebInk e no TerminalInk:
quando a propriedade é falsa, o componente e sua subárvore saem da renderização.

## Tema

`desktop.Theme` guarda os tokens de design em dois conjuntos, claro e escuro. Os valores partem da
paleta que o WebInk já usa, de modo que os três recursos pareçam um produto só e não três.

| Grupo | Tokens |
|---|---|
| Superfícies | `background`, `surface`, `surfaceAlt`, `overlay` |
| Texto | `text`, `textMuted`, `textInverse` |
| Marca | `primary`, `primaryHover`, `primaryText` |
| Semântica | `success`, `warning`, `danger`, `info` |
| Bordas | `border`, `borderStrong`, `focusRing` |
| Raios | `radiusSm` 6, `radiusMd` 10, `radiusLg` 14, `radiusFull` |
| Espaçamento | escala de 4 em 4 pixels |
| Tipografia | família, tamanhos de 11 a 28, pesos regular, medium e bold |
| Elevação | três níveis de sombra desenhada |

A família tipográfica padrão segue o sistema: San Francisco no macOS, Segoe UI no Windows e a
fonte de interface do ambiente no Linux, com fallback para `Dialog`. Nada de fonte embutida, que
custaria centenas de quilobytes no jar.

Os nativos de tema acompanham os do TerminalInk: `defaultTheme`, `extendTheme`, `ThemeProvider` e
`setTheme`.

## Motor de layout

`desktop.LayoutEngine` é o porte de `tui.LayoutEngine` de células para pixels. O algoritmo é o
mesmo, em duas passagens:

1. **Tamanho intrínseco, de baixo para cima.** Cada nó calcula sua dimensão natural. A diferença
   em relação ao terminal está nas folhas: onde o TerminalInk mede texto pelo número de caracteres,
   o DesktopInk mede por `FontMetrics`, com quebra de linha quando a largura disponível aperta.
2. **Posicionamento, de cima para baixo.** O nó recebe uma caixa, desconta bordas e espaçamento
   interno, distribui a sobra do eixo principal entre os filhos com `flex`, e honra `justify` e
   `align`.

Decisões do porte:

- **Cópia adaptada, não generalização.** Extrair um motor compartilhado entre terminal e desktop
  arriscaria desestabilizar o TerminalInk agora, por um ganho que só se paga se os dois realmente
  convergirem. Se convergirem, a extração fica fácil depois, porque o algoritmo é o mesmo.
- **`Grid` como caso próprio**, e não emulado com linhas e colunas, porque a grade de colunas
  iguais é o caso mais comum das telas de painel.
- **Rolagem por container**, resolvida no layout: um container com conteúdo maior que a caixa ganha
  deslocamento vertical e uma barra pintada por nós.
- **HiDPI (High Dots Per Inch), as telas de alta densidade**, resolvido pela transformação do
  `Graphics2D`, com as coordenadas de layout sempre em pixels lógicos.

## Pintura

`desktop.Painter` percorre a árvore já posicionada e desenha cada nó com Java2D, no mesmo formato
de despacho por tipo que o `HtmlRenderer` usa hoje.

Regras que separam uma interface bonita de uma interface de 1998:

- Suavização de contornos e de texto sempre ligadas, por `RenderingHints`.
- Cantos arredondados por `RoundRectangle2D`, nunca retângulos duros em cartões e botões.
- Sombras desenhadas como camadas de retângulos translúcidos, em três níveis de elevação, em vez
  de bordas grossas.
- Estados visuais completos em tudo que é interativo: repouso, sob o cursor, pressionado, com foco
  e desabilitado. O anel de foco é pintado do lado de fora da borda, como nos navegadores.
- Nada de gradiente decorativo nem de ícone bitmap. Os poucos ícones necessários, como a seta do
  `Select` e o certo do `Checkbox`, são desenhados por `Path2D`, o que custa zero byte e escala em
  qualquer densidade de tela.

A pintura acontece numa única `JPanel` com buffer duplo, na EDT (Event Dispatch Thread), a linha de
execução que o Swing reserva para a interface.

## Entrada de texto

`desktop.InputHost` mantém os componentes Swing de texto como filhos reais da `JPanel`, com
`setBounds` recalculado a cada layout, para que sigam a caixa que o motor atribuiu ao nó. Cada um
recebe borda vazia, fonte e cores do tema, e a borda visível é pintada por nós junto com o resto da
tela. O resultado é indistinguível de um campo desenhado, com cursor, seleção e teclado
internacional funcionando de graça.

Os demais controles não hospedam nada: `Button`, `Select`, `Checkbox`, `Tabs` e as linhas de
`Table` são áreas pintadas com regiões de acerto registradas durante o layout, e o runtime resolve
o clique procurando o nó mais profundo que contém o ponto.

O foco de teclado é gerido pelo runtime, com `Tab` e `Shift+Tab` percorrendo a ordem de leitura da
árvore, `Enter` e `Espaço` acionando o componente focado e `Esc` fechando o que estiver aberto.

## Runtime e ciclo de eventos

`desktop.DesktopRuntime` guarda a tabela de telas, a tela atual e a janela, e é bem mais simples
que o `WebRuntime`, porque não há servidor nem tabela de manipuladores no meio: o clique chama a
função NeoObjectPascal direto.

O ciclo é:

1. O usuário clica, digita ou usa o teclado.
2. O runtime encontra o nó alvo e chama a função da propriedade de evento por `callCallback`.
3. A função altera as variáveis globais do programa.
4. O runtime reconstrói a árvore chamando de novo a função da tela atual, refaz o layout e repinta.

Reconstruir a árvore inteira a cada evento é o mesmo modelo dos outros dois recursos, e é barato
nas escalas de uma aplicação de painel. Se algum dia doer, a saída é comparar a árvore nova com a
anterior antes de repintar, e não trocar o modelo.

Como o TerminalInk faz, um `DesktopRuntime.active` estático serve os nativos `navigate` e `focus`
durante um callback.

## Ambiente sem tela

`render` precisa degradar com elegância quando não há tela disponível, exatamente como o
TerminalInk faz quando não há terminal interativo:

```
[DesktopInk] render ignorado (ambiente sem interface gráfica): <motivo>
```

Isso não é detalhe: o smoke test da integração contínua roda o interpretador com
`-Djava.awt.headless=true`, e um `render` que lance exceção nesse ambiente quebraria a pipeline de
publicação. A verificação usa `GraphicsEnvironment.isHeadless()` antes de tocar em qualquer classe
de janela.

## Build nativo

O `--build` já existente não muda de forma: ele empacota o jar com `jpackage --type app-image` e a
classe `AppLauncher`. Pontos a tratar quando a fase 3 fechar:

- Medir e registrar o crescimento do artefato nativo pela entrada do `java.desktop` na imagem de
  runtime.
- No Windows, garantir que a aplicação não abra janela de console junto com a janela da interface.
- No macOS, definir o nome da aplicação que aparece na barra de menu por
  `-Dapple.awt.application.name`.

## Integração com o editor visual

O editor visual de `.xnpas` ganha o DesktopInk como terceiro alvo:

| Arquivo | Mudança |
|---|---|
| `media/widgets-desktopink.js` | O registro dos widgets com a prévia em HTML (HyperText Markup Language) para o canvas |
| `media/preview-desktop.css` | O visual da prévia, imitando o que o Java2D desenha |
| `media/builder.js` | O alternador da barra passa de dois para três alvos, em `TARGETS` |
| `codegen.js` | `generateDesktop`, com telas nomeadas e o segundo argumento de configuração da janela |
| `npasParser.js` | O caminho de volta para o novo alvo, mantendo a ida-e-volta idempotente |
| `extension.js` | Modelos novos: *DesktopInk em branco* e *DesktopInk Dashboard* |
| `media/i18n.js` | Os textos novos nos cinco idiomas |

A forma gerada segue a do TerminalInk, com telas nomeadas em vez de rotas, e o teste de
idempotência que já existe passa a cobrir o terceiro alvo.

## Testes

- **Motor de layout**: caixas esperadas para grade, linha, coluna, espaçamento interno e `flex`,
  sem tocar em janela alguma.
- **Pintura**: desenho em `BufferedImage` e verificação de pixels em pontos conhecidos, por
  exemplo a cor de fundo de um `Card` no centro da sua caixa. Isso **funciona em ambiente sem
  tela**, então roda na integração contínua sem exigir servidor gráfico.
- **Coerção de nós e leitura de propriedades**: os mesmos casos que `tui` e `web` já cobrem.
- **Regiões de acerto**: dado um layout, um ponto resolve para o nó certo.
- **Degradação sem tela**: `render` não lança e imprime o aviso.
- **Extensão**: geração e ida-e-volta do alvo novo, junto com os testes que já existem.

## Fases de implementação

Cada fase termina com o conjunto de testes verde e algo demonstrável.

### Fase 1: núcleo de renderização

`DeskNode`, `Props`, `NodeCoercion`, `Theme`, `LayoutEngine`, `Painter` e `DesktopInk` com os
widgets estáticos: `Window`, `Container`, `Section`, `Grid`, `Row`, `Col`, `Card`, `Divider`,
`Spacer`, `Heading`, `Text`, `Badge`, `StatCard` e `Alert`. Ligação no `Interpreter`, degradação
sem tela e a medição do artefato nativo.

**Entrega**: uma tela estática bonita, aberta numa janela.

### Fase 2: janela, eventos e entrada

`DesktopRuntime`, `InputHost`, regiões de acerto, foco por teclado e os widgets `Button`,
`TextInput`, `PasswordInput`, `TextArea`, `Select`, `Checkbox` e `Form`.

**Entrega**: um formulário que responde ao mouse e ao teclado e altera o estado do programa.

### Fase 3: dados, navegação e tema

`Charts`, mais `Table`, `List`, `ProgressBar`, `Spinner`, `Tabs`, `Sidebar`, `Navbar` e `Link`.
Navegação entre telas, rolagem e alternância entre tema claro e escuro.

**Entrega**: um painel completo, com gráfico, tabela e navegação lateral.

### Fase 4: editor visual

Os sete itens da tabela de integração, com os modelos novos e os testes de ida-e-volta.

**Entrega**: montar uma tela DesktopInk arrastando componentes.

### Fase 5: documentação e exemplos

Exemplos em `examples/`, documentação nova em `desktopink/introduction.md` e
`desktopink/components.md` nos cinco idiomas do site, entrada no menu do VitePress, atualização
deste documento com o que a implementação mudou e entrada no CHANGELOG da extensão.

## Riscos e pontos em aberto

| Risco | Tratamento |
|---|---|
| O artefato nativo crescer mais que o estimado | Medir na fase 1, antes de seguir. Se passar muito de 30 MB, avaliar restringir os módulos passados ao jpackage |
| Rolagem e tabelas grandes ficarem lentas | Pintar apenas as linhas visíveis, o que o layout já sabe calcular |
| Diferença de fonte entre sistemas quebrar o layout | O layout mede por `FontMetrics`, nunca por constante em pixels |
| A duplicação do motor de layout entre `tui` e `desktop` | Aceita conscientemente. A extração de um motor comum fica para quando os dois convergirem de fato |
| Acessibilidade | Fora do escopo da versão 1. Como os componentes são pintados, leitores de tela não os enxergam. Se virar requisito, o caminho é expor a árvore por `AccessibleContext` |

## Não-objetivos da versão 1

- Suporte a múltiplas janelas simultâneas.
- Menu de aplicação, bandeja do sistema e notificações do sistema.
- Arrastar e soltar dentro da aplicação.
- Animações e transições entre telas.
- Impressão.
- Acessibilidade por leitor de tela.

Nada disso é impedido pelo desenho: são adições naturais depois que as cinco fases fecharem.
