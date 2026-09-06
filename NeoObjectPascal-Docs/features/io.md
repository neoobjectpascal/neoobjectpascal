# Entrada e saída

O NeoObjectPascal oferece procedimentos simples para interagir com o console: `WriteLn` para exibir informações, `ReadLn` para ler dados do usuário e `showMenu` para montar menus numerados.

## Escrevendo com `WriteLn`

O procedimento `WriteLn` escreve uma linha de texto no console e adiciona uma quebra de linha ao final:

```npas
begin
    WriteLn("Olá, Mundo!");
end.
```

<Output>
Olá, Mundo!
</Output>

### Vários argumentos

`WriteLn` aceita vários argumentos e os **concatena sem separador** na saída. Isso é útil para montar mensagens com valores dinâmicos:

```npas
var nome: String;
var idade: Integer;

begin
    nome := "Carol";
    idade := 28;
    WriteLn("Olá, ", nome, "! Você tem ", idade, " anos.");
end.
```

<Output>
Olá, Carol! Você tem 28 anos.
</Output>

::: tip Linha em branco
Chamar `WriteLn()` sem argumentos imprime apenas uma quebra de linha — prático para separar blocos de saída.
:::

## Lendo com `ReadLn`

O procedimento `ReadLn` lê uma linha digitada pelo usuário e a armazena em uma variável. O valor é convertido para o tipo da variável de destino:

```npas
var nome: String;

begin
    WriteLn("Qual é o seu nome?");
    ReadLn(nome);
    WriteLn("Prazer, ", nome, "!");
end.
```

<Output>
Qual é o seu nome?
Prazer, Ada!
</Output>

Quando a variável de destino é `Integer` ou `Real`, o texto lido é convertido automaticamente para o tipo numérico correspondente:

```npas
var idade: Integer;

begin
    WriteLn("Digite sua idade:");
    ReadLn(idade);

    if idade >= 18 then
        WriteLn("Maior de idade.")
    else
        WriteLn("Menor de idade.");
end.
```

<Output>
Digite sua idade:
Maior de idade.
</Output>

::: warning Conversão de tipos
Ao ler para uma variável numérica, garanta que a entrada seja de fato um número. Entradas inesperadas podem gerar erros de conversão — trate esses casos com `try/catch` quando estiver lendo dados não confiáveis. Veja [Tratamento de erros](../language/error-handling).
:::

## Menus com `showMenu`

O procedimento `showMenu` recebe uma lista de opções e imprime um menu numerado automaticamente:

```npas
begin
    showMenu("Café", "Chá", "Suco");
end.
```

<Output>
1. Café
2. Chá
3. Suco
</Output>

Combine `showMenu` com `ReadLn` para montar uma interface de escolha:

```npas
var opcao: Integer;

begin
    showMenu("Novo pedido", "Consultar", "Sair");
    WriteLn("Escolha uma opção:");
    ReadLn(opcao);

    if opcao = 1 then
        WriteLn("Iniciando novo pedido...")
    else if opcao = 2 then
        WriteLn("Consultando...")
    else
        WriteLn("Até logo!");
end.
```

<Output>
1. Novo pedido
2. Consultar
3. Sair
Escolha uma opção:
Iniciando novo pedido...
</Output>

## Boas práticas

- Use `WriteLn` com múltiplos argumentos em vez de concatenar tudo com `+`: fica mais legível e evita conversões manuais.
- Sempre exiba uma mensagem antes de um `ReadLn`, para o usuário saber o que digitar.
- Ao ler números, considere validar ou tratar erros de conversão.

---

A seguir, descubra como acessar o poder da JVM em [Integração com Java](./java-integration).
