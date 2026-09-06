# Programação funcional

O NeoObjectPascal incorpora ideias da programação funcional para que você escreva um código mais conciso, expressivo e fácil de manter. O recurso central é o **operador pipe** `|>`, que encadeia transformações de forma legível, da esquerda para a direita.

## O operador pipe `|>`

O operador `|>` passa o valor da esquerda como **primeiro argumento** da função à direita. Em outras palavras, `valor |> funcao` é equivalente a `funcao(valor)`:

```npas
function dobrar(n: Integer): Integer
begin
    return n * 2;
end;

begin
    WriteLn(dobrar(5));      // chamada tradicional
    WriteLn(5 |> dobrar);    // com pipe — mesmo resultado
end.
```

<Output>
10
10
</Output>

Os dois estilos produzem o mesmo resultado. A vantagem do pipe aparece quando encadeamos várias transformações.

## Encadeando funções

Como o pipe entrega o resultado de uma função como entrada da próxima, você pode montar uma sequência de passos sem aninhar chamadas:

```npas
function dobrar(n: Integer): Integer
begin
    return n * 2;
end;

function incrementar(n: Integer): Integer
begin
    return n + 1;
end;

function aoQuadrado(n: Integer): Integer
begin
    return n * n;
end;

begin
    // Leia da esquerda para a direita: 10 → 20 → 21 → 441
    WriteLn(10 |> dobrar |> incrementar |> aoQuadrado);
end.
```

<Output>
441
</Output>

Compare com a forma aninhada `aoQuadrado(incrementar(dobrar(10)))`. O pipe elimina os parênteses e torna a ordem das operações imediatamente visível.

::: tip Leia como uma frase
Um encadeamento com `|>` é lido na ordem em que os passos acontecem. É como uma "esteira" de dados: cada função recebe o resultado da anterior.
:::

## Funções pequenas e componíveis

O pipe favorece um estilo em que você cria **funções pequenas com uma única responsabilidade** e as combina. Cada função permanece simples, testável e reutilizável:

```npas
function limpar(texto: String): String
begin
    // Remove um prefixo de espaço e normaliza
    return "[" + texto + "]";
end;

function emMaiusculas(texto: String): String
begin
    return java:(texto) {
        return ((String)param0).toUpperCase();
    };
end;

begin
    WriteLn("relatorio" |> emMaiusculas |> limpar);
end.
```

<Output>
[RELATORIO]
</Output>

Repare que `emMaiusculas` delega o trabalho pesado a um bloco Java. Você pode misturar livremente funções NeoObjectPascal e blocos `java:(...) { ... }` dentro de um pipeline. Veja detalhes em [Integração com Java](./java-integration).

## Combinando pipe com aritmética

O pipe tem precedência baixa, então expressões aritméticas à sua esquerda são avaliadas antes de serem canalizadas:

```npas
function descrever(n: Integer): String
begin
    if n > 100 then
        return "grande"
    else
        return "pequeno";
end;

begin
    WriteLn(20 * 3 |> descrever);   // (20 * 3) = 60 → "pequeno"
    WriteLn(50 * 3 |> descrever);   // (50 * 3) = 150 → "grande"
end.
```

<Output>
pequeno
grande
</Output>

::: info Precedência
O `|>` fica logo acima dos operadores unários na tabela de precedência. Consulte [Operadores](../language/operators) para a tabela completa e use parênteses sempre que quiser deixar a intenção explícita.
:::

## Boas práticas

- Prefira funções **puras** (sem efeitos colaterais) nos pipelines: dado o mesmo argumento, elas devolvem sempre o mesmo resultado.
- Dê nomes que descrevam a transformação (`dobrar`, `emMaiusculas`, `descrever`), não o passo (`passo1`).
- Mantenha cada função curta. Se um passo cresce demais, quebre-o em funções menores e ligue-as com `|>`.

Essas funções pequenas também são fáceis de cobrir com o framework de testes nativo — veja [Testes de unidade](../testing/unit-testing).

---

A seguir, aprenda a transformar textos estruturados em objetos com [Manipulação de dados (JSON e CSV)](./data-parsing).
