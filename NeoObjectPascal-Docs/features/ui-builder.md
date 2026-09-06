# Editor visual de interfaces (.xnpas)

A extensão do VS Code inclui um **editor visual (WYSIWYG)** para criar interfaces **sem escrever código** — tanto para o terminal (**TerminalInk**) quanto para a web (**WebInk**). Você monta a tela arrastando componentes, ajusta propriedades e eventos, e o editor gera automaticamente o `.npas` correspondente.

## O arquivo `.xnpas`

O editor trabalha com arquivos de extensão **`.xnpas`** — um JSON com o desenho da tela. Ao salvar `teste.xnpas`, o editor **(re)gera** o `teste.npas` irmão. A sincronização é de **uma via**: o `.xnpas` é a fonte da verdade, e o `.npas` gerado traz um cabeçalho avisando que **não deve ser editado à mão**.

## Criando uma tela

1. Abra a paleta de comandos (`Cmd/Ctrl+Shift+P`) e execute **"New UI Builder File (.xnpas)"**.
2. Escolha um modelo: *WebInk — em branco*, *WebInk — Dashboard*, *TerminalInk — em branco* ou *TerminalInk — Formulário*.
3. O arquivo abre direto no editor visual.

## A interface do editor

- **Paleta** (esquerda) — os componentes do alvo escolhido, agrupados. Arraste um componente para o canvas.
- **Canvas** (centro) — pré-visualização fiel. Clique para selecionar um componente; arraste um componente já colocado para **reordenar** (ou movê-lo para dentro de outro contêiner).
- **Inspetor** (direita) — abas **Propriedades**, **Estado** e **Eventos**.
- **Barra superior** — o alternador **WebInk / TerminalInk**, o seletor de tela/rota, e o botão **Rodar ao vivo** (gera e executa o `.npas`).

## Estado e eventos

- **Estado** — declare variáveis globais (nome, tipo e valor inicial) compartilhadas entre as telas e os eventos.
- **Eventos** — para props como `onClick`, `onChange`, `onSubmit`, `onConfirm` e `onCancel`, use o **editor híbrido**: escolha uma **ação sem código** (incrementar uma variável, definir uma variável, navegar para outra tela, usar o valor digitado) **ou** troque para o modo **Código** e escreva NeoObjectPascal livremente. A prévia da função gerada aparece ali mesmo.

## Alvos: WebInk ou TerminalInk

Um `.xnpas` é inteiramente **WebInk** ou **TerminalInk**. O alternador na barra troca o alvo (reiniciando a árvore, pois os conjuntos de componentes diferem).

- **WebInk** — `Page`, `Container`, `Section`, `Grid`, `Row`, `Col`, `Card`, `Navbar`, `Sidebar`, `Tabs`, `Heading`, `Text`, `Badge`, `StatCard`, `Link`, `Button`, `TextInput`, `TextArea`, `Select`, `Checkbox`, `Form`, `Table`, `List`, `Chart`, `Alert`, `ProgressBar`, `Spinner`.
- **TerminalInk** — `VBox`, `HBox`, `Box`, `Spacer`, `Text`, `Badge`, `TextInput`, `PasswordInput`, `EmailInput`, `ConfirmInput`, `Select`, `MultiSelect`, `Spinner`, `ProgressBar`, `StatusMessage`, `Alert`, `UnorderedList`, `OrderedList`.

## Sincronização e o `.npas` gerado

Ao salvar o `.xnpas`, o `.npas` irmão é regerado a partir do desenho. **Não edite o `.npas` gerado à mão** — ele será sobrescrito no próximo salvamento. Se você abrir um `.npas` gerado, o editor avisa e oferece abrir o `.xnpas` correspondente.

::: tip Dica
Um `teste.xnpas` gera um `teste.npas`. Rode o `.npas` normalmente (botão *Run*), ou use **Rodar ao vivo** direto do editor visual.
:::
## Visibilidade, dados dinâmicos e foco

Três recursos deixam as telas reagirem ao estado em tempo de execução.

### Propriedade `visible`

Todo componente traz, no Inspetor, o controle **Visível**: *Sempre* (padrão) ou *Condição (fx)*. Em *Condição*, informe uma expressão booleana — o componente (e seus filhos) só aparece quando ela for verdadeira. Exemplo: um `ConfirmInput` que só surge depois que o nome foi preenchido, com a condição `nome <> ""`.

### Dados vindos de variáveis (fx)

Os campos de dados — `options` (Select/MultiSelect), `columns`/`rows` (Table), o gráfico (Chart), `value` (ProgressBar), `items` (List) e os campos de valor — trazem um botão **fx**. Ligado, o campo deixa de aceitar um valor fixo e passa a receber o nome de uma **variável ou expressão**. Assim o programador popula o componente com dados de uma API, por exemplo, em vez de valores digitados à mão.

### Foco no TerminalInk

Os componentes de entrada do TerminalInk (`TextInput`, `PasswordInput`, `EmailInput`, `ConfirmInput`, `Select`, `MultiSelect`) ganham dois campos:

- **Chave** (`key`) — um identificador estável do componente.
- **Foco inicial** (`autoFocus`) — coloca o foco nele já no primeiro quadro.

Além disso, o editor de eventos oferece a ação **Focar componente**, que gera `focus("chave")` — útil, por exemplo, para devolver o foco a um campo ao cancelar uma confirmação.

## Idioma do editor

O editor visual é **multilíngue**, nos mesmos 5 idiomas da documentação: **Português, English, Deutsch, Français, Italiano**. Um seletor de idioma (ícone de globo) na barra superior troca, na hora, toda a interface do editor — paleta, propriedades, eventos, dicas e mensagens.

A preferência fica no setting **`neoobjectpascal.uiBuilder.language`** (global, persistente). O padrão é **`auto`**, que segue o idioma de exibição do VS Code e cai em Português quando o idioma não é um dos cinco. Escolher um idioma no seletor grava esse setting.
