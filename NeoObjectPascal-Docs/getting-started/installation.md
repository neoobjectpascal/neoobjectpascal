# Instalação e primeiro programa

O interpretador do NeoObjectPascal é distribuído como um **arquivo JAR executável**, então tudo o que você precisa é do **Java 11 ou superior** instalado.

## Pré-requisitos

- **Java 11+** (JRE ou JDK). Verifique com:

```bash
java -version
```

- (Opcional) **Maven**, caso queira compilar o interpretador a partir do código-fonte.

## Obtendo o interpretador

Você pode usar o JAR já empacotado (por exemplo, o que acompanha a extensão do VS Code, em `VS-Code-Extension/bin/`) ou compilar a partir do repositório:

```bash
cd NeoObjectPascal
mvn package -DskipTests
# O JAR final fica em target/neoobjectpascal-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Olá, mundo

Crie um arquivo `ola.npas`:

```npas
begin
    WriteLn("Olá, NeoObjectPascal!");
end.
```

E execute:

```bash
java -jar neoobjectpascal.jar ola.npas
```

<Output>
Olá, NeoObjectPascal!
</Output>

::: tip Extensão de arquivo
Programas usam a extensão `.npas`. Arquivos de teste usam `.test.npas`.
:::

## Opções da linha de comando

O interpretador aceita várias flags:

| Flag | Descrição |
|------|-----------|
| _(nenhuma)_ | Executa o arquivo `.npas` |
| `-q`, `--no-warnings` | Suprime avisos de análise |
| `-t`, `--test` | Modo de teste (executa um `.test.npas`) |
| `-ta`, `--test-all <dir>` | Executa todos os testes recursivamente, com cobertura |
| `-d`, `--debug` | Modo debugger interativo |
| `--dap` | Modo DAP (integração com o VS Code) |
| `--build <arquivo> [--icon png] [--name] [--output] [--target]` | Gera um executável nativo (exe/app/bin) |
| `--execute-on-cloud <url> <proj> <user> <pass>` | Executa no NeoObjectPascalCloud |
| `-h`, `--help` | Mostra a ajuda |

Exemplos:

```bash
java -jar neoobjectpascal.jar --no-warnings ola.npas
java -jar neoobjectpascal.jar -t calculadora.test.npas
java -jar neoobjectpascal.jar --test-all ./examples
```

::: tip Editor recomendado
Instale a **extensão do VS Code** para NeoObjectPascal e ganhe realce de sintaxe, execução, testes e depuração integrados. Veja [Debugger, VS Code e nuvem](../testing/debugging-tools).
:::

Agora que você já executa código, vamos entender a [estrutura de um programa](./program-structure).
