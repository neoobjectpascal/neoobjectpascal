# Input e output

NeoObjectPascal offre procedure semplici per interagire con la console: `WriteLn` per mostrare informazioni, `ReadLn` per leggere dati dall'utente e `showMenu` per costruire menu numerati.

## Scrivere con `WriteLn`

La procedura `WriteLn` scrive una riga di testo nella console e aggiunge un'interruzione di riga alla fine:

```npas
begin
    WriteLn("Olá, Mundo!");
end.
```

<Output>
Olá, Mundo!
</Output>

### Più argomenti

`WriteLn` accetta più argomenti e li **concatena senza separatore** nell'output. Questo è utile per comporre messaggi con valori dinamici:

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

::: tip Riga vuota
Chiamare `WriteLn()` senza argomenti stampa solo un'interruzione di riga — pratico per separare blocchi di output.
:::

## Leggere con `ReadLn`

La procedura `ReadLn` legge una riga digitata dall'utente e la memorizza in una variabile. Il valore viene convertito nel tipo della variabile di destinazione:

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

Quando la variabile di destinazione è `Integer` o `Real`, il testo letto viene convertito automaticamente nel tipo numerico corrispondente:

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

::: warning Conversione dei tipi
Quando leggi in una variabile numerica, assicurati che l'input sia effettivamente un numero. Input inaspettati possono generare errori di conversione — gestisci questi casi con `try/catch` quando leggi dati non affidabili. Vedi [Gestione degli errori](../language/error-handling).
:::

## Menu con `showMenu`

La procedura `showMenu` riceve un elenco di opzioni e stampa automaticamente un menu numerato:

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

Combina `showMenu` con `ReadLn` per costruire un'interfaccia di scelta:

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

## Buone pratiche

- Usa `WriteLn` con più argomenti invece di concatenare tutto con `+`: risulta più leggibile ed evita conversioni manuali.
- Mostra sempre un messaggio prima di un `ReadLn`, così l'utente sa cosa digitare.
- Quando leggi numeri, considera di validare o gestire gli errori di conversione.

---

Successivamente, scopri come accedere alla potenza della JVM in [Integrazione Java](./java-integration).
