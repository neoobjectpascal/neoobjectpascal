# Datas, horas e moeda

O NeoObjectPascal traz quatro tipos embutidos para lidar com o mundo real: `Date`, `Time`, `DateTime` e `Currency`. Todos estão **sempre disponíveis** — assim como `Integer`, você não precisa de nenhum `uses` para usá-los.

- `Date` — uma data de calendário no formato ISO `yyyy-MM-dd` (por baixo, `java.time.LocalDate`).
- `Time` — uma hora do dia `HH:mm:ss` (`java.time.LocalTime`).
- `DateTime` — data e hora combinadas (`java.time.LocalDateTime`).
- `Currency` — valores monetários decimais **exatos** (`java.math.BigDecimal`).

Além disso, `Double` e `Float` são **apelidos de `Real`** (ponto flutuante de 64 bits): as três palavras nomeiam exatamente o mesmo tipo, então escolha a que ficar mais clara no seu código.

::: tip Por que `Currency` e não `Real` para dinheiro?
`Real` (ponto flutuante) sofre com o erro clássico em que `0.1 + 0.2` não dá exatamente `0.3`. `Currency` é decimal exato: `0.1 + 0.2` é precisamente `0.3`. Use `Currency` para dinheiro e `Real`/`Double` para cálculos científicos ou de propósito geral.
:::

## Os tipos de data e hora

### Criando datas e horas

As funções de criação vêm em dois grupos: as que capturam o instante atual e as que montam um valor a partir de números. Não é preciso `uses` para nenhuma delas.

```npas
var hoje: Date;
var agora: DateTime;
var abertura: Time;
var natal: Date;

begin
    hoje := today();            // a data de hoje
    agora := now();             // data e hora atuais
    abertura := currentTime();  // a hora atual

    natal := date(2026, 12, 25);
    WriteLn(natal);             // WriteLn imprime datas em ISO
end.
```

<Output>
2026-12-25
</Output>

Você monta cada tipo com sua função dedicada: `date(ano, mes, dia)`, `time(hora, min, seg)` e `dateTime(ano, mes, dia, hora, min, seg)`. Para partir de texto, use as funções `parse`:

```npas
var d: Date;
var t: Time;
var dt: DateTime;

begin
    d := parseDate("2026-01-15");
    t := parseTime("10:30");
    dt := parseDateTime("2026-01-15 10:30:00");
    WriteLn(d);
    WriteLn(t);
end.
```

<Output>
2026-01-15
10:30:00
</Output>

::: tip Coerção automática a partir de String
Ao atribuir uma **String** no formato ISO a uma variável `Date`, `Time` ou `DateTime`, o valor é convertido automaticamente — não é preciso chamar `parse`:

```npas
var feriado: Date;
feriado := "2026-12-25";   // String vira Date
```
:::

### Componentes

Extraia partes de qualquer data ou hora com funções diretas. `dayOfWeek` devolve `1` para segunda-feira até `7` para domingo.

```npas
var dt: DateTime;

begin
    dt := dateTime(2026, 7, 18, 14, 30, 0);
    WriteLn("Ano: ", year(dt));
    WriteLn("Mês: ", month(dt));
    WriteLn("Dia: ", day(dt));
    WriteLn("Hora: ", hour(dt));
    WriteLn("Minuto: ", minute(dt));
    WriteLn("Dia da semana (1=segunda): ", dayOfWeek(dt));
end.
```

<Output>
Ano: 2026
Mês: 7
Dia: 18
Hora: 14
Minuto: 30
Dia da semana (1=segunda): 6
</Output>

### Comparação

Os operadores `<`, `>`, `<=`, `>=`, `=` e `<>` funcionam diretamente sobre `Date`, `Time` e `DateTime` — uma data é "menor" que outra quando é anterior.

```npas
var vencimento: Date;
var hoje: Date;

begin
    vencimento := date(2026, 7, 10);
    hoje := date(2026, 7, 18);

    if hoje > vencimento then
        WriteLn("Prazo vencido!")
    else
        WriteLn("Ainda dentro do prazo");
end.
```

<Output>
Prazo vencido!
</Output>

### Aritmética

Some ou subtraia intervalos com `addDays`, `addMonths`, `addYears`, `addHours` e `addMinutes` (use valores negativos para voltar no tempo). Para medir a distância entre dois pontos, use `daysBetween` e `hoursBetween`.

```npas
var inicio: Date;

begin
    inicio := date(2026, 7, 18);
    WriteLn("Daqui a 30 dias: ", addDays(inicio, 30));
    WriteLn("No mês que vem: ", addMonths(inicio, 1));
    WriteLn("Dias até o fim do ano: ", daysBetween(inicio, date(2026, 12, 31)));
end.
```

<Output>
Daqui a 30 dias: 2026-08-17
No mês que vem: 2026-08-18
Dias até o fim do ano: 166
</Output>

### Formatação

`format(valor, padrao)` devolve uma `String` no formato que você pedir, usando os mesmos padrões de `java.time` (`dd`, `MM`, `yyyy`, `HH`, `mm`, `ss`). Este exemplo combina tudo: idade em dias, aritmética e formatação — no espírito de `examples/54`.

```npas
var nascimento: Date;
var referencia: Date;

begin
    nascimento := date(1990, 5, 20);
    referencia := date(2026, 7, 18);

    WriteLn("Nascimento: ", format(nascimento, "dd/MM/yyyy"));
    WriteLn("Idade em dias: ", daysBetween(nascimento, referencia));
    WriteLn("Daqui a 30 dias: ", format(addDays(referencia, 30), "dd/MM/yyyy"));
    WriteLn("Dia da semana (1=segunda): ", dayOfWeek(referencia));
end.
```

<Output>
Nascimento: 20/05/1990
Idade em dias: 13208
Daqui a 30 dias: 17/08/2026
Dia da semana (1=segunda): 6
</Output>

## Dinheiro exato com `Currency`

`Currency` guarda valores monetários sem nenhum erro de ponto flutuante. Crie um valor com `currency(...)`, a partir de um número ou de uma string.

### Aritmética exata

Os operadores `+`, `-`, `*` e `/` são exatos sobre `Currency`. O caso clássico que falha com `Real` funciona perfeitamente aqui:

```npas
var a: Currency;
var b: Currency;

begin
    a := currency(0.1);
    b := currency(0.2);
    WriteLn(a + b);   // exato, sem erro de ponto flutuante
end.
```

<Output>
0.3
</Output>

Também vale a comparação: `<`, `>`, `<=`, `>=`, `=` e `<>` funcionam sobre `Currency`.

### Formatando e arredondando

`formatCurrency(valor, simbolo)` devolve uma `String` no estilo brasileiro — ponto como separador de milhar e vírgula como decimal. `roundCurrency(valor, casas)` arredonda para o número de casas desejado.

```npas
var preco: Currency;

begin
    preco := currency(1234.56);
    WriteLn(formatCurrency(preco, "R$"));
end.
```

<Output>
R$ 1.234,56
</Output>

### Exemplo: cálculo de imposto

Um imposto de 2% sobre um valor, no espírito do cálculo de ITBI de `examples/55`. Como tudo é `Currency`, o resultado é exato até o centavo:

```npas
var baseCalculo: Currency;
var aliquota: Real;
var imposto: Currency;

begin
    baseCalculo := currency(520000.00);
    aliquota := 0.02;   // 2%
    imposto := baseCalculo * currency(aliquota);

    WriteLn("Base de cálculo: ", formatCurrency(baseCalculo, "R$"));
    WriteLn("Alíquota:        2%");
    WriteLn("Imposto devido:  ", formatCurrency(imposto, "R$"));
end.
```

<Output>
Base de cálculo: R$ 520.000,00
Alíquota:        2%
Imposto devido:  R$ 10.400,00
</Output>

::: tip Escolha o tipo certo
Use `Currency` para qualquer valor em dinheiro — preços, impostos, saldos —, onde o centavo precisa fechar exatamente. Reserve `Real`/`Double` para matemática científica e cálculos de propósito geral, onde uma pequena imprecisão de ponto flutuante é aceitável.
:::

::: warning Impressão padrão
`WriteLn` imprime datas em ISO (`2026-12-25`) e valores `Currency` como um decimal simples (`10400.00`). Para uma saída no formato do seu país, use sempre `format` e `formatCurrency`.
:::

---

A seguir, garanta a qualidade do seu código com [Testes de unidade](../testing/unit-testing).
