# Gerar executáveis nativos

O NeoObjectPascal empacota um programa `.npas` em um **executável nativo autocontido** — sem exigir que o usuário final tenha Java instalado. Um único comando produz:

- **macOS** → um pacote `.app`
- **Windows** → uma pasta com um `.exe`
- **Linux** → uma pasta com um binário em `bin/`

Por baixo, o build usa o **`jpackage`** (parte do JDK 14+), que empacota o interpretador, o seu projeto e um runtime Java (JRE) enxuto dentro do app.

## Pré-requisitos

- Um **JDK 14 ou superior** no PATH (o `jpackage` acompanha o JDK).
- Rodar a partir do **JAR empacotado** do interpretador (`neoobjectpascal.jar`).

## Uso

```bash
java -jar neoobjectpascal.jar --build programa.npas [opções]
```

| Opção | Descrição |
|-------|-----------|
| `--icon <png>` | ícone do app, a partir de um **PNG** (convertido por plataforma) |
| `--name <Nome>` | nome do app/executável (padrão: nome do programa) |
| `--output <dir>` | diretório de saída (padrão: `./dist`) |
| `--target <mac\|windows\|linux>` | plataforma-alvo (padrão: o SO atual) |

### Exemplo

```bash
java -jar neoobjectpascal.jar --build main.npas --icon icon.png --name CalculoITBI --output dist
```

<Output>
📦 Empacotando 'main.npas' → macOS (.app) (CalculoITBI)
   • 5 arquivo(s) do projeto incluídos (a partir de .../NeoObjectPascal-Examples)
   • ícone: app-icon.icns
   • rodando jpackage...
✅ Build concluído: dist/CalculoITBI.app
   Executar: open "dist/CalculoITBI.app"
</Output>

## O projeto inteiro é empacotado

Todo o diretório do programa é incluído no app — **arquivos e subpastas**, preservando a estrutura. Ou seja:

- Módulos importados com `uses pasta.modulo` (por exemplo `helpers/calculoitbi.npas`) vão junto.
- Arquivos de dados e assets que o programa lê em tempo de execução também são incluídos.

Assim, o app empacotado roda exatamente como o programa rodava a partir do código-fonte. Diretórios de "lixo" (`.git`, `node_modules`, o próprio diretório de saída, ...) são ignorados.

## Ícone a partir de um PNG

Passe um único **PNG** com `--icon`; o build converte para o formato certo de cada plataforma:

| Plataforma | Formato do ícone | Conversão |
|-----------|------------------|-----------|
| macOS | `.icns` | via `iconset` + `iconutil` (nativos do macOS) |
| Windows | `.ico` | embutido em Java (sem ferramentas externas) |
| Linux | `.png` | usado diretamente |

Use um PNG quadrado (por exemplo 512×512 ou 1024×1024) para o melhor resultado.

## Uma plataforma por vez

O `jpackage` **não faz cross-compile**: cada artefato é gerado no seu próprio sistema operacional. No macOS você gera o `.app`; o `.exe` é gerado no Windows e o `bin` no Linux. Para produzir os três, rode o build em cada SO (ou use uma matriz de CI). Se você passar um `--target` diferente do SO atual, o comando avisa com um erro claro.

## Pelo VS Code

Com a extensão instalada, clique com o botão direito em um arquivo `.npas` (ou em uma pasta do projeto) e escolha **Build Native Executable**. A extensão pergunta o nome do app e, opcionalmente, um ícone PNG, e gera o resultado em `dist/`.

## Executando o resultado

- **macOS:** `open dist/CalculoITBI.app` (ou dê duplo-clique).
- **Windows:** `dist\CalculoITBI\CalculoITBI.exe`.
- **Linux:** `dist/CalculoITBI/bin/CalculoITBI`.

::: info Tamanho do app
Cada executável é autocontido e inclui um JRE, então ocupa algumas dezenas de MB — é a natureza de um app Java nativo, em troca de não exigir Java na máquina do usuário.
:::

::: tip Programas interativos (TerminalInk)
Apps de terminal feitos com [TerminalInk](../terminalink/introduction) funcionam empacotados: ao serem abertos por duplo-clique (sem um terminal), o TerminalInk abre a própria janela. Programas de console puro (`WriteLn`) são melhores executados a partir de um terminal, para ver a saída.
:::

## Próximos passos

Com o build nativo você distribui seus programas como apps de verdade. Para revisar a sintaxe completa da linguagem, veja a [Referência da linguagem](../reference/language-reference).
