# Integração com Java

Como o NeoObjectPascal é interpretado sobre a JVM, você pode **incorporar código Java diretamente** no seu programa. Isso dá acesso a todo o ecossistema Java — bibliotecas de data, matemática, texto e muito mais — sem sair da sintaxe Pascal.

## Sintaxe de um bloco Java

Um bloco Java é uma expressão que produz um valor. A forma geral é:

```npas
java:(arg0, arg1) { <código Java que retorna um valor> }
```

- Entre parênteses vão os valores NeoObjectPascal que você quer disponibilizar ao bloco.
- Dentro das chaves `{ }` vai código Java. Ele deve terminar com um `return`.
- O valor retornado pelo bloco vira o valor da expressão em NeoObjectPascal.

::: info As chaves são do Java
No NeoObjectPascal, `{ }` delimita **exclusivamente** o corpo de um bloco Java. Não existem comentários de bloco — comentários são apenas `// linha única`.
:::

## Acessando os parâmetros: `param0`, `param1`, ...

Dentro do bloco, os argumentos são acessados por **posição**, com os nomes `param0`, `param1`, `param2`, e assim por diante. Como o Java é tipado, é preciso fazer o **cast** para o tipo apropriado:

```npas
function saudar(nome: String): String
begin
    return java:(nome) {
        return "Olá, " + ((String)param0).toUpperCase();
    };
end;

begin
    WriteLn(saudar("ada"));
end.
```

<Output>
Olá, ADA
</Output>

O argumento `nome` foi entregue ao bloco como `param0` e convertido para `String` antes de chamar `.toUpperCase()`.

## Acessando argumentos pelo nome

Quando um argumento do bloco Java é um **identificador simples** — como em `java:(nome)` —, você pode referenciá-lo dentro do bloco **pelo próprio nome**, e não apenas como `param0`. Esse alias nomeado é declarado com o tipo de runtime do argumento, então, para `String`, `Integer`, `Boolean` e `Real`, o _cast_ deixa de ser necessário.

```npas
function saudar(nome: String): String
begin
    return java:(nome) {
        // 'nome' já é String — sem precisar de (String)param0
        return "Olá, " + nome.toUpperCase();
    };
end;

begin
    WriteLn(saudar("ada"));   // Olá, ADA
end.
```

<Output>
Olá, ADA
</Output>

Compare com o estilo posicional antigo, `((String)param0).toUpperCase()`: o alias nomeado dispensa o cast e deixa o corpo Java mais legível.

`param0`, `param1`, ... continuam disponíveis (totalmente compatível com o código existente) e são a forma de acessar argumentos que **não** são identificadores simples — como `java:(a + b)`, um literal ou uma chamada de função —, pois esses não têm nome.

::: warning Palavras reservadas e colisões
Se o nome do argumento coincidir com uma palavra reservada do Java (`class`, `int`, ...) ou com uma variável local declarada dentro do bloco, o alias nomeado é ignorado — nesse caso, use `param0`.
:::

## Múltiplos parâmetros

Cada argumento adicional recebe o próximo índice. Aqui `a` é `param0` e `b` é `param1`:

```npas
function somaDeQuadrados(a: Integer, b: Integer): Integer
begin
    return java:(a, b) {
        Integer x = (Integer)param0;
        Integer y = (Integer)param1;
        return x * x + y * y;
    };
end;

begin
    WriteLn(somaDeQuadrados(3, 4));
end.
```

<Output>
25
</Output>

## Usando bibliotecas da JVM

O verdadeiro ganho está em chamar classes do Java. Use sempre o **nome totalmente qualificado** da classe, pois não é possível declarar `import`:

```npas
function raizQuadrada(numero: Integer): Real
begin
    return java:(numero) {
        Double n = ((Integer)param0).doubleValue();
        return Math.sqrt(n);
    };
end;

function agora(): String
begin
    return java:() {
        java.time.LocalDate hoje = java.time.LocalDate.now();
        return hoje.toString();
    };
end;

begin
    WriteLn("Raiz de 144: ", raizQuadrada(144));
    WriteLn("Data de hoje: ", agora());
end.
```

<Output>
Raiz de 144: 12.0
Data de hoje: 2026-07-17
</Output>

Repare que `agora` usa `java:()` **sem parâmetros** — o bloco Java pode ser autossuficiente.

## Uso direto em expressões

Um bloco Java não precisa estar dentro de uma função: ele pode aparecer diretamente em qualquer expressão, inclusive como argumento de `WriteLn`:

```npas
begin
    WriteLn("Cálculo direto: ", java:(10, 20) {
        Integer a = (Integer)param0;
        Integer b = (Integer)param1;
        return a + b;
    });
end.
```

<Output>
Cálculo direto: 30
</Output>

## Como as bibliotecas internas usam isso

As [bibliotecas internas](./internal-libraries) do NeoObjectPascal são, em grande parte, escritas com blocos Java. Por exemplo, `toUpperCase` da biblioteca `internal.string` é literalmente:

```npas
function toUpperCase(s): String
begin
    return java:(s) {
        return ((String)param0).toUpperCase();
    };
end;
```

Ou seja, você pode escrever suas próprias bibliotecas seguindo o mesmo padrão.

## Considerações

- **Cast explícito**: os parâmetros chegam como `Object`; faça o cast para `Integer`, `String`, `Boolean`, etc.
- **Sem `import`**: use nomes qualificados como `java.time.LocalDate`.
- **Exceções**: exceções Java lançadas no bloco são capturadas pelo runtime e podem ser tratadas com `try/catch`.
- **Desempenho**: há um pequeno custo de compilação dinâmica na primeira execução de cada bloco.

::: tip Combine com o pipe
Funções que embrulham blocos Java entram naturalmente em pipelines `|>`. Veja [Programação funcional](./functional).
:::

---

A seguir, organize seu código em [Módulos e uses](./modules).
