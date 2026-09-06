# Funktionale Programmierung

NeoObjectPascal übernimmt Ideen aus der funktionalen Programmierung, damit Sie Code schreiben, der prägnanter, ausdrucksstärker und leichter zu warten ist. Das zentrale Werkzeug ist der **Pipe-Operator** `|>`, der Transformationen von links nach rechts lesbar aneinanderreiht.

## Der Pipe-Operator `|>`

Der Operator `|>` übergibt den Wert auf der linken Seite als **erstes Argument** an die Funktion auf der rechten Seite. Mit anderen Worten: `wert |> funktion` ist gleichbedeutend mit `funktion(wert)`:

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

Beide Schreibweisen liefern dasselbe Ergebnis. Der Vorteil der Pipe zeigt sich, sobald wir mehrere Transformationen aneinanderreihen.

## Funktionen verketten

Da die Pipe das Ergebnis einer Funktion als Eingabe der nächsten weitergibt, können Sie eine Abfolge von Schritten aufbauen, ohne Aufrufe zu verschachteln:

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

Vergleichen Sie das mit der verschachtelten Form `aoQuadrado(incrementar(dobrar(10)))`. Die Pipe beseitigt die Klammern und macht die Reihenfolge der Operationen unmittelbar sichtbar.

::: tip Wie einen Satz lesen
Eine Verkettung mit `|>` liest sich in der Reihenfolge, in der die Schritte ablaufen. Es ist wie ein „Förderband“ für Daten: Jede Funktion erhält das Ergebnis der vorhergehenden.
:::

## Kleine, kombinierbare Funktionen

Die Pipe begünstigt einen Stil, bei dem Sie **kleine Funktionen mit einer einzigen Verantwortung** erstellen und sie miteinander kombinieren. Jede Funktion bleibt einfach, testbar und wiederverwendbar:

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

Beachten Sie, dass `emMaiusculas` die eigentliche Arbeit an einen Java-Block delegiert. Sie können NeoObjectPascal-Funktionen und `java:(...) { ... }`-Blöcke innerhalb einer Pipeline beliebig mischen. Einzelheiten finden Sie unter [Java-Integration](./java-integration).

## Pipe mit Arithmetik kombinieren

Die Pipe hat eine niedrige Priorität, daher werden arithmetische Ausdrücke auf ihrer linken Seite ausgewertet, bevor sie durch die Pipe geleitet werden:

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

::: info Priorität
`|>` steht in der Prioritätstabelle direkt oberhalb der unären Operatoren. Die vollständige Tabelle finden Sie unter [Operatoren](../language/operators); setzen Sie Klammern immer dann, wenn Sie Ihre Absicht ausdrücklich verdeutlichen möchten.
:::

## Bewährte Praktiken

- Bevorzugen Sie in Pipelines **reine** Funktionen (ohne Nebenwirkungen): Bei gleichem Argument liefern sie stets dasselbe Ergebnis.
- Vergeben Sie Namen, die die Transformation beschreiben (`dobrar`, `emMaiusculas`, `descrever`), nicht den Schritt (`passo1`).
- Halten Sie jede Funktion kurz. Wird ein Schritt zu groß, zerlegen Sie ihn in kleinere Funktionen und verbinden Sie diese mit `|>`.

Diese kleinen Funktionen lassen sich außerdem leicht mit dem nativen Test-Framework abdecken — siehe [Unit-Tests](../testing/unit-testing).

---

Als Nächstes lernen Sie, wie Sie strukturierten Text in Objekte umwandeln, unter [Datenverarbeitung (JSON und CSV)](./data-parsing).
