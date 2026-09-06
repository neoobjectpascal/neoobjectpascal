# Módulos e uses

À medida que um programa cresce, convém dividi-lo em arquivos menores e reutilizáveis. No NeoObjectPascal isso é feito com a cláusula `uses`, que carrega tanto **bibliotecas internas** quanto **módulos em arquivos** que você mesmo escreve.

## A cláusula `uses`

A cláusula `uses` aparece no **início** do programa, antes de qualquer declaração. Ela lista os módulos a carregar, separados por vírgula, e termina com `;`:

```npas
uses internal.math, internal.string;

begin
    WriteLn("Máximo: ", max(10, 20));
    WriteLn("Maiúsculas: ", toUpperCase("neo"));
end.
```

<Output>
Máximo: 20
Maiúsculas: NEO
</Output>

Depois do `uses`, todas as funções dos módulos ficam disponíveis como se tivessem sido declaradas no seu próprio arquivo.

## Bibliotecas internas vs. módulos em arquivo

Existem dois tipos de módulos, distinguidos pelo prefixo:

| Forma                  | Origem                                            | Exemplo                    |
| ---------------------- | ------------------------------------------------- | -------------------------- |
| `internal.<nome>`      | Bibliotecas empacotadas no próprio interpretador  | `uses internal.datetime;`  |
| `<pasta>.<modulo>`     | Arquivo `.npas` relativo ao seu programa          | `uses utils.matematica;`   |

O prefixo `internal.` é reservado: o runtime o reconhece e carrega a biblioteca de dentro do JAR. Qualquer outro caminho é tratado como um arquivo no seu projeto.

## Criando um módulo em arquivo

Um módulo é apenas um arquivo `.npas` com declarações de funções, procedimentos ou variáveis. Por exemplo, crie `matematica.npas`:

```npas
// matematica.npas
function somar(a: Integer, b: Integer): Integer
begin
    return a + b;
end;

function subtrair(a: Integer, b: Integer): Integer
begin
    return a - b;
end;
```

E use-o no programa principal, no mesmo diretório:

```npas
// programa.npas
uses matematica;

begin
    WriteLn(somar(10, 5));
    WriteLn(subtrair(10, 5));
end.
```

<Output>
15
5
</Output>

O interpretador procura por `matematica.npas` a partir do diretório do arquivo principal.

::: warning Separador de parâmetros é a vírgula
Ao declarar parâmetros tipados, separe-os com **vírgula**: `function somar(a: Integer, b: Integer)`. Não use ponto e vírgula entre parâmetros.
:::

## Módulos hierárquicos

Você pode organizar módulos em subpastas usando ponto como separador de caminho. `uses utils.matematica;` carrega o arquivo `utils/matematica.npas`, relativo ao programa principal:

```text
projeto/
├── programa.npas
└── utils/
    └── matematica.npas
```

```npas
// programa.npas
uses utils.matematica;

begin
    WriteLn(somar(2, 3));
end.
```

<Output>
5
</Output>

## Carregando vários módulos

Uma única cláusula `uses` pode misturar bibliotecas internas e módulos próprios, todos separados por vírgula:

```npas
uses internal.math, internal.string, utils.matematica;

begin
    WriteLn(square(4));              // de internal.math
    WriteLn(quote("olá"));          // de internal.string
    WriteLn(somar(1, 1));           // do seu módulo utils.matematica
end.
```

<Output>
16
"olá"
2
</Output>

## Boas práticas

- Agrupe funções relacionadas em um mesmo módulo (por exemplo, tudo de texto em um `texto.npas`).
- Prefira muitos arquivos pequenos e coesos a um único arquivo gigante.
- Use subpastas (`utils.`, `dominio.`) para organizar módulos por área do sistema.
- Só há uma cláusula `uses` por programa: liste todos os módulos nela.

---

A seguir, conheça em detalhe cada uma das [Bibliotecas internas](./internal-libraries).
