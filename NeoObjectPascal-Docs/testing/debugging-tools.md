# Debugger, VS Code e execução na nuvem

Além do framework de testes, o NeoObjectPascal oferece ferramentas para investigar, editar e executar código em escala: um **debugger interativo** (com modo DAP para editores), uma **extensão para o VS Code** e a **execução remota na nuvem** via NeoObjectPascalCloud. Este guia apresenta as três.

## Debugger interativo

Para depurar um programa passo a passo, execute-o com a flag `-d` (ou `--debug`):

```bash
java -jar neoobjectpascal.jar -d programa.npas
```

O interpretador abre um REPL interativo onde você controla a execução. Os comandos principais:

| Comando | Ação |
|---------|------|
| `b <linha>` | adiciona um breakpoint na linha |
| `d <linha>` | remove o breakpoint da linha |
| `list` | lista os breakpoints ativos |
| `c`, `continue` | continua até o próximo breakpoint |
| `s`, `step` | executa a próxima linha (step over) |
| `i`, `into` | entra na função chamada (step into) |
| `o`, `out` | sai da função atual (step out) |
| `p <var>` | imprime o valor de uma variável |
| `vars` | lista todas as variáveis do escopo atual |
| `w <var>` | monitora (watch) mudanças em uma variável |
| `set <var> <valor>` | altera o valor de uma variável em tempo de execução |
| `stack` | mostra a pilha de chamadas |
| `q`, `quit` | encerra o debugger e o programa |

### Uma sessão de exemplo

Considere o programa abaixo:

```npas
var x: Integer;
var y: Integer;
var resultado: Integer;

begin
    x := 10;
    y := 20;
    resultado := x + y;
    WriteLn("resultado = ", resultado);
end.
```

Colocamos um breakpoint na linha do somatório, observamos `x`, inspecionamos valores e até modificamos uma variável antes de continuar:

<Output>
debug> b 8
✓ Breakpoint adicionado na linha 8

debug> w x
✓ Watching variável: x

debug> c

⏸ PAUSADO na linha 8
  resultado := x + y;

debug> p x
x = 10 (tipo: INTEGER)

debug> vars
Variáveis locais:
  x = 10 (tipo: INTEGER)
  y = 20 (tipo: INTEGER)
  resultado = null (tipo: INTEGER)

debug> set x 50
✓ x = 50 (anterior: 10)

debug> c
resultado = 70
</Output>

::: tip Estratégia de breakpoints
Coloque breakpoints em início de loops, condições `if` importantes, chamadas de função e pontos de retorno. Combine `w` (watch) com `s` (step) para acompanhar exatamente quando e onde uma variável muda.
:::

### Modo DAP para editores

Para depuração gráfica dentro de um editor, o interpretador expõe um servidor **DAP** (Debug Adapter Protocol) — o mesmo protocolo que o VS Code e outros editores usam para breakpoints visuais, inspeção de variáveis e controle passo a passo:

```bash
java -jar neoobjectpascal.jar --dap
```

Nesse modo o interpretador não roda um REPL de texto: ele fica aguardando um cliente DAP (como o VS Code) conectar. Você define breakpoints clicando na margem do editor, e usa os botões de continuar/step/inspecionar da própria interface. Normalmente você não inicia o modo DAP na mão — a extensão do VS Code faz isso por você.

O **Step Into (F7)** entra no corpo de métodos de classe e funções do seu projeto — inclusive quando definidos em outro arquivo importado via `uses` —, abrindo o arquivo e a linha corretos. A **Call Stack** mostra cada frame da pilha (`Classe.metodo`, funções e `main`) com seu arquivo e linha; a aba **Variables** mostra as variáveis locais do frame selecionado, incluindo `self` dentro de um método. Use **Step Over** para executar uma chamada sem entrar nela e **Step Out** para voltar ao chamador.

::: tip Depurando no VS Code
Ao depurar pelo VS Code, a saída do programa (`WriteLn`) aparece no **Debug Console** durante a execução. A resolução de módulos usa o diretório do programa como raiz, então depurar um programa que faz `uses pasta.modulo` funciona normalmente. Porém, a **entrada interativa (`ReadLn`) não está disponível durante a depuração** — ela retorna um valor padrão. Para programas interativos, use **Run** em vez de depurar.
:::

## Extensão do VS Code

A extensão oficial para o **VS Code** transforma o editor em um ambiente completo para NeoObjectPascal. Ela inclui:

- **Realce de sintaxe** para arquivos `.npas` e `.test.npas`.
- **Interpretador embutido** — a extensão traz o JAR empacotado (em `VS-Code-Extension/bin/`), então você não precisa configurar o caminho do interpretador manualmente.
- **Comandos** para executar, testar e depurar sem sair do editor.

Os comandos ficam disponíveis pela paleta de comandos (`Cmd/Ctrl+Shift+P`):

| Comando | Ação |
|---------|------|
| `neoobjectpascal.run` | executa o arquivo `.npas` atual |
| `neoobjectpascal.debug` | inicia o debugger (via DAP) no arquivo atual |
| `neoobjectpascal.runTest` | roda o arquivo `.test.npas` atual |
| `neoobjectpascal.runAllTests` | roda todos os testes do projeto de forma recursiva |
| `neoobjectpascal.build` | gera um executável nativo (exe/app/bin) do projeto — veja [Gerar executáveis nativos](./building-executables) |

::: info Execução na nuvem
A execução na nuvem é feita pela linha de comando com a flag `--execute-on-cloud` do interpretador (veja a seção abaixo).
:::

::: tip Fluxo recomendado
Escreva o código com realce e autocompletar, rode `neoobjectpascal.runTest` para validar o arquivo aberto, e use `neoobjectpascal.debug` para acompanhar a execução com breakpoints visuais. Tudo usando o JAR que já vem com a extensão.
:::

## Execução na nuvem

O NeoObjectPascal integra-se nativamente ao **NeoObjectPascalCloud**, permitindo executar um projeto em um servidor remoto com um único comando. Isso é útil para compartilhar execuções via URL, manter histórico de logs e rodar sem depender do ambiente local.

### Sintaxe

```bash
java -jar neoobjectpascal.jar --execute-on-cloud <url> <projeto> <usuario> <senha> <arquivo.npas>
```

| Parâmetro | Descrição |
|-----------|-----------|
| `<url>` | URL base da API do cloud (ex.: `http://localhost:8000`) |
| `<projeto>` | nome do projeto (pasta base) |
| `<usuario>` | usuário do cloud |
| `<senha>` | senha do cloud |
| `<arquivo.npas>` | arquivo principal a executar |

### Exemplo

Dado o programa `hello.npas`:

```npas
var mensagem: String;
var numero: Integer;

begin
    mensagem := "Olá do NeoObjectPascal Cloud!";
    numero := 42;

    WriteLn("=================================");
    WriteLn(mensagem);
    WriteLn("Número mágico: ", numero);
    WriteLn("=================================");
end.
```

Executamos na nuvem:

```bash
java -jar neoobjectpascal.jar \
  --execute-on-cloud \
  http://localhost:8000 \
  hello_project \
  usuario@email.com \
  senha123 \
  hello.npas
```

O interpretador autentica, coleta **todos os arquivos `.npas`** do diretório do projeto (mantendo a estrutura de pastas), faz o upload, dispara a execução remota e devolve um link para acompanhar os logs:

<Output>
🔐 Autenticando no cloud...
✓ Autenticado com sucesso!

📁 Coletando arquivos do projeto...
✓ Encontrados 1 arquivo(s)
  - hello.npas

⬆️  Fazendo upload dos arquivos...
  [1/1] hello.npas
✓ Upload concluído!

🚀 Executando projeto no cloud...
✅ Projeto executado com sucesso!

🔗 Link da execução:
   http://localhost:8000/executions/123
</Output>

::: warning Não exponha senhas
Evite passar a senha diretamente na linha de comando, pois ela fica visível no histórico do shell e na lista de processos. Prefira variáveis de ambiente:

```bash
export CLOUD_PASSWORD="senha123"
java -jar neoobjectpascal.jar --execute-on-cloud \
  http://localhost:8000 meu_projeto usuario@email.com "$CLOUD_PASSWORD" main.npas
```
:::

::: info Projetos com múltiplos arquivos
A coleta é recursiva: se o projeto tiver subpastas (`helpers/`, `utils/`), todos os arquivos `.npas` são detectados e enviados automaticamente, preservando os caminhos relativos. Basta indicar o arquivo principal no comando.
:::

## Próximos passos

Você agora conhece as três ferramentas de investigação e execução do NeoObjectPascal. Para consultar a sintaxe completa da linguagem em um único lugar, siga para a [Referência da linguagem](../reference/language-reference).
