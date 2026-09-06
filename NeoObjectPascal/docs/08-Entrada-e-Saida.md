# Entrada e Saída em NeoObjectPascal

O NeoObjectPascal fornece procedimentos simples para interagir com o usuário, permitindo que você exiba informações e leia dados do console.

## Escrevendo no Console com `WriteLn`

O procedimento `WriteLn` é usado para escrever uma linha de texto no console.

```pascal
begin
  WriteLn("Olá, Mundo!");
end.
```

Você pode passar vários argumentos para `WriteLn`, e eles serão concatenados na saída.

```pascal
var nome: String;
begin
  nome := "Carol";
  WriteLn("Olá, ", nome, "!");
end.
```

## Lendo do Console com `Readln`

O procedimento `Readln` é usado para ler uma linha de texto do console e armazená-la em uma variável.

```pascal
var nome: String;
begin
  WriteLn("Qual é o seu nome?");
  Readln(nome);
  WriteLn("Olá, ", nome, "!");
end.
```

## Exibindo Menus com `showMenu`

O procedimento `showMenu` facilita a criação de menus de texto simples.

```pascal
begin
  showMenu("Café", "Chá", "Suco");
end.
```

Isso exibirá um menu numerado com as opções fornecidas.

