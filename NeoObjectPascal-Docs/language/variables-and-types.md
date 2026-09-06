# Variáveis e tipos

Toda variável em NeoObjectPascal tem um **tipo** declarado. A declaração usa a palavra-chave `var`, seguida do nome, dois-pontos e o tipo:

```npas
var idade: Integer;
var nome: String;
```

A atribuição de valores é feita com o operador `:=` (não confunda com `=`, que é o operador de igualdade):

```npas
begin
    var idade: Integer;
    idade := 30;
    WriteLn("Idade: ", idade);
end.
```

<Output>
Idade: 30
</Output>

## Onde declarar

Você pode declarar variáveis em dois lugares:

- **No topo do arquivo**, antes do bloco principal.
- **Dentro de um bloco** `begin ... end`, como uma instrução comum.

```npas
var total: Integer;   // declaração no topo

begin
    total := 100;
    var imposto: Integer;   // declaração dentro do bloco
    imposto := total / 10;
    WriteLn("Imposto: ", imposto);
end.
```

<Output>
Imposto: 10
</Output>

## Os dez tipos

O NeoObjectPascal tem dez tipos embutidos:

| Tipo      | Descrição                                             | Exemplos de literal          |
| --------- | ----------------------------------------------------- | ---------------------------- |
| `Integer` | Números inteiros                                      | `0`, `42`, `-7`              |
| `Real`    | Números de ponto flutuante (precisão dupla)           | `3.14159`, `5.0`, `-0.5`     |
| `String`  | Texto                                                 | `"olá"`, `'mundo'`           |
| `Boolean` | Valor lógico                                          | `true`, `false`              |
| `Array`   | Coleção ordenada de valores                           | `[1, 2, 3]`, `["a", "b"]`    |
| `Object`  | Tipo genérico (instâncias, JSON/CSV, valores diversos)| `new Pessoa("Ana")`          |
| `Date`    | Data de calendário (ISO `yyyy-MM-dd`)                 | `today()`, `date(2026,12,25)`|
| `Time`    | Hora do dia (`HH:mm:ss`)                              | `currentTime()`, `time(10,30,0)`|
| `DateTime`| Data e hora combinadas                                | `now()`                      |
| `Currency`| Valor monetário decimal exato (sem erro de ponto flutuante)| `currency(199.90)`      |

Além desses, `Double` e `Float` são **apelidos de `Real`** (ponto flutuante de 64 bits) — use o nome que preferir. Os tipos `Date`, `Time`, `DateTime` e `Currency` estão detalhados em [Datas, horas e moeda](../features/dates-and-currency).

## Literais e detalhes de cada tipo

### Integer e Real

Inteiros são escritos diretamente: `10`, `-3`, `0`. Números `Real` precisam de dígitos **dos dois lados** do ponto decimal:

```npas
begin
    var meio: Real;
    meio := 0.5;    // correto
    WriteLn(meio);
end.
```

<Output>
0.5
</Output>

::: warning Real precisa de dígitos completos
Escreva `5.0`, nunca `5.` — a segunda forma é inválida. Da mesma forma, use `0.5` e não `.5`.
:::

Quando você mistura `Integer` e `Real` em uma expressão, o inteiro é **promovido automaticamente** para `Real`. O mesmo ocorre ao atribuir um inteiro a uma variável `Real`:

```npas
begin
    var x: Real;
    x := 5;          // 5 vira 5.0
    var y: Real;
    y := 2 * 3.5;    // resultado Real
    WriteLn("x = ", x);
    WriteLn("y = ", y);
end.
```

<Output>
x = 5.0
y = 7.0
</Output>

### String

Strings podem usar **aspas duplas** ou **aspas simples** — ambas funcionam da mesma forma:

```npas
begin
    var a: String;
    var b: String;
    a := "aspas duplas";
    b := 'aspas simples';
    WriteLn(a);
    WriteLn(b);
end.
```

<Output>
aspas duplas
aspas simples
</Output>

O operador `+` **concatena** strings. Se qualquer um dos lados for `String`, o `+` junta os valores em vez de somá-los:

```npas
begin
    var nome: String;
    nome := "Mundo";
    WriteLn("Olá, " + nome + "!");
end.
```

<Output>
Olá, Mundo!
</Output>

### Boolean

Um `Boolean` guarda `true` ou `false` (também aceitos em maiúsculas, como `True` ou `TRUE`):

```npas
begin
    var ativo: Boolean;
    ativo := true;
    WriteLn("Ativo: ", ativo);
end.
```

<Output>
Ativo: true
</Output>

### Object

`Object` é o tipo genérico da linguagem. Ele guarda instâncias de classes, resultados de `JSON.parse`/`CSV.parse` e qualquer valor cujo tipo específico você não precise nomear:

```npas
class Pessoa
    var nome: String;

    constructor Create(n: String)
    begin
        self.nome := n;
    end;

    public function saudar(): String
    begin
        return "Sou " + self.nome;
    end;
end;

var p: Object;

begin
    p := new Pessoa("Alice");
    WriteLn(p.saudar());
end.
```

<Output>
Sou Alice
</Output>

::: tip Escolha do tipo
Prefira o tipo mais específico possível (`Integer`, `String`, etc.) e reserve `Object` para instâncias e dados dinâmicos.
:::

Agora que você sabe declarar variáveis, veja como combiná-las em [Operadores](./operators).
