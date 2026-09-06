# Estrutura de um programa

Um programa em NeoObjectPascal tem três partes, todas opcionais exceto o encerramento:

1. Uma cláusula **`uses`** (importação de módulos), terminada por `;`.
2. Zero ou mais **declarações** (variáveis, funções, classes, interfaces, testes), cada uma terminada por `;`.
3. Um **bloco principal** `begin ... end.` — o ponto final `.` encerra o programa.

```npas
uses internal.string;

var mensagem: String;

function emMaiusculas(s: String): String
begin
    return toUpperCase(s);
end;

begin
    mensagem := emMaiusculas("olá");
    WriteLn(mensagem);
end.
```

<Output>
OLÁ
</Output>

::: warning Sem cabeçalho `program`
Diferente de alguns dialetos Pascal, o NeoObjectPascal **não** usa um cabeçalho `program Nome;`. O programa começa direto pela cláusula `uses`, pelas declarações ou pelo bloco `begin`.
:::

## O bloco principal

O bloco `begin ... end.` contém as instruções executadas quando o programa roda. Cada instrução termina com ponto e vírgula `;`:

```npas
begin
    WriteLn("primeira linha");
    WriteLn("segunda linha");
end.
```

Note o **ponto final** depois do `end` que fecha o programa — ele é obrigatório. Blocos internos (de funções, laços, etc.) terminam apenas com `end` (sem ponto).

## Comentários

O NeoObjectPascal tem **apenas comentários de uma linha**, iniciados por `//`:

```npas
begin
    // Isto é um comentário
    WriteLn("Oi"); // também funciona no fim da linha
end.
```

::: warning Nada de comentários de bloco
Não existem comentários `{ ... }` ou `(* ... *)`. As chaves `{ }` são reservadas para [blocos de código Java](../features/java-integration).
:::

## Declarações no topo vs. dentro do bloco

Você pode declarar variáveis tanto no topo do arquivo quanto dentro de um bloco:

```npas
var x: Integer;   // declaração no topo

begin
    x := 10;
    var y: Integer;   // declaração dentro do bloco
    y := 20;
    WriteLn(x + y);
end.
```

<Output>
30
</Output>

Com a estrutura no lugar, siga para [Variáveis e tipos](../language/variables-and-types).
