# Funções e procedimentos

Funções encapsulam lógica reutilizável e **retornam um valor**. No topo do arquivo, você declara funções com a palavra-chave `function`.

## Declarando uma função

A forma geral é `function nome(parâmetros): Tipo begin ... return expr; end;`. Note o `;` final após a declaração no nível superior:

```npas
function saudar(nome: String): String
begin
    return "Olá, " + nome + "!";
end;

begin
    WriteLn(saudar("Ana"));
end.
```

<Output>
Olá, Ana!
</Output>

O tipo depois dos `:` é o tipo do valor retornado. A instrução `return expr;` devolve esse valor e encerra a função.

## Parâmetros

Os parâmetros são separados por **vírgula**. Eles podem ser **tipados** ou **não tipados**:

```npas
// parâmetros tipados
function somar(a: Integer, b: Integer): Integer
begin
    return a + b;
end;

// parâmetros sem tipo explícito
function juntar(x, y): String
begin
    return x + " " + y;
end;

begin
    WriteLn(somar(3, 4));
    WriteLn(juntar("bom", "dia"));
end.
```

<Output>
7
bom dia
</Output>

::: tip Tipe seus parâmetros
Declarar o tipo dos parâmetros torna a intenção clara e ajuda o interpretador a validar os valores. Prefira parâmetros tipados sempre que possível.
:::

## Chamando funções

Chame uma função pelo nome, passando os argumentos entre parênteses. Se a função **não tiver argumentos**, os parênteses são opcionais:

```npas
function agora(): String
begin
    return "sempre agora";
end;

begin
    WriteLn(agora());   // com parênteses
    WriteLn(agora);     // sem parênteses — equivalente
end.
```

<Output>
sempre agora
sempre agora
</Output>

## Recursão

Uma função pode chamar a si mesma. O exemplo clássico é o fatorial:

```npas
function fatorial(n: Integer): Integer
begin
    if n <= 1 then
        return 1;
    return n * fatorial(n - 1);
end;

begin
    WriteLn("5! = ", fatorial(5));
    WriteLn("6! = ", fatorial(6));
end.
```

<Output>
5! = 120
6! = 720
</Output>

::: warning Sempre tenha um caso base
Toda função recursiva precisa de uma condição de parada (aqui, `n <= 1`). Sem ela, a recursão nunca termina.
:::

## E os procedimentos?

Um **procedimento** é como uma função, mas **não retorna valor** — serve para executar efeitos (imprimir, alterar estado). No NeoObjectPascal, procedimentos existem como **membros de classes**, declarados com a palavra-chave `procedure`:

```npas
class Logger
    public procedure registrar(msg: String)
    begin
        WriteLn("[LOG] " + msg);
    end;
end;

var log: Object;

begin
    log := new Logger();
    log.registrar("iniciando");
end.
```

<Output>
[LOG] iniciando
</Output>

Para explorar procedimentos, métodos e visibilidade em profundidade, veja [Classes](../oop/classes).

A seguir, aprenda a trabalhar com coleções em [Arrays](./arrays).
