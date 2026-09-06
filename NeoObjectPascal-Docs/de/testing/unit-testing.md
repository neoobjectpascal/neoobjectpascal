# Unit-Tests und Mocking

NeoObjectPascal bringt ein **natives Test-Framework** mit — es ist keine externe Bibliothek, sondern Teil der Sprache selbst. Sie schreiben Tests mit dem Schlüsselwort `test`, prüfen Ergebnisse mit `expect(...)` und führen alles direkt über den Interpreter aus, samt Ergebnisbericht und Abdeckung.

Dieser Leitfaden behandelt das Schreiben von Tests, alle verfügbaren Matcher, die Organisation und Ausführung von Suiten sowie das **Mocking**-System zur Isolierung von Abhängigkeiten.

## Einen Test schreiben

Ein Test ist eine Deklaration auf oberster Ebene, auf derselben Ebene wie Funktionen und Klassen. Die Form ist:

```npas
test "descrição do teste"
begin
    // corpo do teste
end;
```

Die Beschreibung ist ein freier String, der im Bericht erscheint. Innerhalb des Blocks `begin ... end` schreiben Sie normalen Code: Sie deklarieren Variablen, rufen Funktionen auf, erzeugen Objekte und führen am Ende die Prüfungen mit `expect` durch.

```npas
class Calculadora
    public function somar(a: Integer, b: Integer): Integer
    begin
        return a + b;
    end;
end;

test "Calculadora deve somar dois números corretamente"
begin
    // GIVEN - dado uma calculadora
    var calc: Calculadora;
    calc := new Calculadora();

    // WHEN - quando somamos 5 + 3
    var resultado: Integer;
    resultado := calc.somar(5, 3);

    // THEN - então o resultado deve ser 8
    expect(resultado).toBe(8);
end;
```

::: tip Muster GIVEN-WHEN-THEN
Jeden Test in drei Blöcke zu gliedern — **GIVEN** (Vorbereitung), **WHEN** (Aktion) und **THEN** (Prüfung) — macht die Absicht klar. Es entspricht dem Prinzip Arrange-Act-Assert. Verwenden Sie `//`-Kommentare, um jede Phase zu kennzeichnen.
:::

## Matcher (`expect`)

Die Prüfung beginnt immer mit `expect(realerWert)`, gefolgt von einem Matcher, der die Erwartung beschreibt. Schlägt die Erwartung fehl, wird der Test mit `[FAIL]` und einer Meldung markiert, andernfalls mit `[PASS]`.

### `.toBe(esperado)`

Prüft exakte Gleichheit. Es ist der am häufigsten verwendete Matcher und eignet sich für Zahlen, Strings und Booleans.

```npas
test "toBe compara valores exatos"
begin
    expect(2 + 2).toBe(4);
    expect("Olá" + " mundo").toBe("Olá mundo");
end;
```

### `.toEqual(esperado)`

Entspricht `.toBe` bei der Wertgleichheit — verwenden Sie ihn, wenn Sie im Text ausdrücklich deutlich machen möchten, dass es sich um einen Vergleich struktureller Gleichheit handelt.

```npas
test "toEqual verifica igualdade"
begin
    var total: Integer;
    total := 100 + 50;
    expect(total).toEqual(150);
end;
```

### `.toBeTrue()` und `.toBeFalse()`

Prüfen boolesche Werte, ohne dass explizit mit `true`/`false` verglichen werden muss.

```npas
test "matchers booleanos"
begin
    var maiorDeIdade: Boolean;
    maiorDeIdade := 20 >= 18;

    expect(maiorDeIdade).toBeTrue();
    expect(10 > 100).toBeFalse();
end;
```

### `.toBeNull()`

Prüft, ob ein Wert null ist — nützlich für noch nicht initialisierte Felder oder fehlende Rückgabewerte.

```npas
test "campo não inicializado começa nulo"
begin
    var resultado: Object;
    expect(resultado).toBeNull();
end;
```

| Matcher | Prüft |
|---------|----------|
| `.toBe(v)` | exakte Gleichheit |
| `.toEqual(v)` | Wertgleichheit |
| `.toBeTrue()` | Wert ist wahr |
| `.toBeFalse()` | Wert ist falsch |
| `.toBeNull()` | Wert ist null |

## Die Tests organisieren

- **Endung `.test.npas`** — Testdateien verwenden das Suffix `.test.npas` (zum Beispiel `calculadora.test.npas`). An dieser Konvention erkennt der Test-Runner die Dateien.
- **Ein Verhalten pro Test** — jeder `test` sollte ein einziges Verhalten prüfen. Bevorzugen Sie mehrere kleine, gut benannte Tests gegenüber einem großen Test, der alles prüft.
- **Unabhängige Tests** — jeder Test sollte seine eigenen Instanzen erzeugen und nicht von der Ausführungsreihenfolge abhängen.

Eine typische Organisation trennt Quellcode und Tests:

```
projeto/
├── src/
│   ├── calculadora.npas
│   └── conta.npas
└── tests/
    ├── calculadora.test.npas
    └── conta.test.npas
```

## Die Tests ausführen

### Eine einzelne Datei mit `-t`

Um eine einzelne Testdatei auszuführen, verwenden Sie die Flag `-t` (oder `--test`):

```bash
java -jar neoobjectpascal.jar -t calculadora.test.npas
```

<Output>
========== TEST RESULTS ==========
[PASS] Calculadora deve somar dois números corretamente
[PASS] Calculadora deve subtrair dois números corretamente
[PASS] Calculadora deve multiplicar dois números corretamente
==================================
Total: 3 | Passed: 3 | Failed: 0
</Output>

Wenn ein Test fehlschlägt, zeigt der Bericht die Beschreibung und die festgestellte Abweichung:

<Output>
========== TEST RESULTS ==========
[PASS] Calculadora deve somar dois números corretamente
[FAIL] Calculadora deve multiplicar dois números corretamente: esperado 42 mas obteve 40
==================================
Total: 3 | Passed: 2 | Failed: 1
</Output>

### Alle Tests mit `--test-all`

Um eine ganze Suite rekursiv auszuführen und dabei auf ein Verzeichnis zu zeigen, verwenden Sie `--test-all` (oder `-ta`). Es findet alle `.test.npas`-Dateien unterhalb des Verzeichnisses und liefert zusätzlich eine Zusammenfassung der **Abdeckung**. Die Abdeckung wird anhand der **von den Tests ausgeführten öffentlichen Klassenmethoden** gemessen — jede öffentliche Methode einer Klasse zählt, und der Bericht listet die abgedeckten und nicht abgedeckten Methoden pro Klasse auf. Es ist kein Verhältnis von Testdateien zu Quelldateien:

```bash
java -jar neoobjectpascal.jar --test-all ./tests
```

<Output>
========== TEST RESULTS ==========
[PASS] Calculadora deve somar dois números corretamente
[PASS] ContaBancaria deve inicializar com saldo correto
[PASS] ContaBancaria deve depositar valor corretamente
[PASS] Email válido deve passar na validação
==================================
Total: 4 | Passed: 4 | Failed: 0

Test Coverage (public class methods):
  Public methods: 3
  Covered: 3
  Coverage: 100.00%
    ✓ Calculadora.somar
    ✓ ContaBancaria.depositar
    ✓ ValidadorEmail.validar
</Output>

::: tip Abdeckungsziel
Streben Sie an, die Abdeckung bei **80 % oder mehr** der öffentlichen Methoden Ihrer Klassen zu halten. Jede öffentliche Methode, die von keinem Test ausgeführt wird, erscheint im Bericht als nicht abgedeckt und zeigt genau, wo Tests fehlen.
:::

::: info Exit-Code
Der Runner gibt bei Fehlschlägen einen von null verschiedenen Exit-Code zurück, wodurch er sich direkt in CI/CD-Pipelines einsetzen lässt.
:::

## Mocking

Unit-Tests sollten schnell und vorhersehbar sein. Wenn die getestete Einheit von etwas Externem abhängt — einem E-Mail-Dienst, einer API, einer Datenbank — möchten Sie den echten Aufruf nicht auslösen. Genau hier kommt das **Mocking** ins Spiel: Sie ersetzen eine Funktion oder Methode durch eine Version, die einen festen Wert zurückgibt, und prüfen anschließend, dass sie aufgerufen wurde.

### Syntax

```npas
// Substitui uma função ou método por um retorno fixo:
mock calcularDesconto thenReturn 50;
mock EmailService enviarEmail thenReturn true;

// Verifica que a função/método mockado foi chamado:
verify calcularDesconto;
verify EmailService enviarEmail;
```

- `mock Alvo metodo thenReturn valor;` — geben Sie bei Klassenmethoden die Klasse und die Methode an. Bei eigenständigen Funktionen geben Sie nur den Namen an. Die Punktschreibweise (`mock EmailService.enviarEmail thenReturn true;`) wird ebenfalls akzeptiert.
- `verify Alvo metodo;` — lässt den Test fehlschlagen, wenn das gemockte Ziel nie aufgerufen wurde.

Die Konvention ist, **die Mocks im GIVEN-Block zu deklarieren** und **die `verify`-Aufrufe im THEN-Block durchzuführen**.

### Beispiel: einen E-Mail-Dienst isolieren

Betrachten Sie einen `NotificadorUsuario`, der von einem `EmailService` abhängt. Im Test wollen wir keine echte E-Mail versenden — nur sicherstellen, dass der Notifier den Aufruf korrekt delegiert und Erfolg zurückgibt.

```npas
// Serviço externo que queremos mockar
class EmailService
    public function enviarEmail(destinatario: String, mensagem: String): Boolean
    begin
        // Em produção, enviaria e-mail real via SMTP
        WriteLn("Enviando email para ", destinatario);
        return true;
    end;
end;

// Classe que usa o serviço
class NotificadorUsuario
    var emailService: EmailService;

    constructor Create(service: EmailService)
    begin
        self.emailService := service;
    end;

    public function notificar(usuario: String): Boolean
    begin
        return self.emailService.enviarEmail(usuario, "Você tem uma nova notificação!");
    end;
end;

test "NotificadorUsuario deve chamar EmailService ao notificar"
begin
    // GIVEN - dado um serviço de e-mail mockado
    var emailService: EmailService;
    emailService := new EmailService();

    // Mock do método enviarEmail para retornar true sem enviar e-mail real
    mock EmailService enviarEmail thenReturn true;

    var notificador: NotificadorUsuario;
    notificador := new NotificadorUsuario(emailService);

    // WHEN - quando notificamos um usuário
    var resultado: Boolean;
    resultado := notificador.notificar("usuario@teste.com");

    // THEN - então deve retornar true
    expect(resultado).toBeTrue();

    // E o método enviarEmail deve ter sido chamado
    verify EmailService enviarEmail;
end;
```

### Beispiel: eine eigenständige Funktion mocken

Derselbe Mechanismus funktioniert für Funktionen auf oberster Ebene. Hier ersetzen wir eine Berechnung durch einen festen Wert:

```npas
function calcularDesconto(valor: Integer): Integer
begin
    // Cálculo complexo que queremos evitar no teste
    return valor * 10;
end;

test "Deve usar função mockada em vez da real"
begin
    // GIVEN - dada uma função mockada
    mock calcularDesconto thenReturn 50;

    // WHEN - quando chamamos a função
    var desconto: Integer;
    desconto := calcularDesconto(100);

    // THEN - então deve retornar o valor mockado
    expect(desconto).toBe(50);

    // E a função deve ter sido chamada
    verify calcularDesconto;
end;
```

::: warning Umfang des Mockings
Das aktuelle Mocking ersetzt den vollständigen Rückgabewert des Ziels und prüft, **ob** es aufgerufen wurde. Es gibt noch keine Zuordnung nach bestimmten Parametern, keine Zählung der Aufrufe und keine Prüfung der Reihenfolge. Mocken Sie nur die externen Abhängigkeiten und lassen Sie die interne Logik echt getestet.
:::

## Nächste Schritte

Mit automatisierten Tests an Ort und Stelle ist der nächste natürliche Schritt, das Verhalten zur Laufzeit zu untersuchen. Weiter geht es mit [Debugger, VS Code und Ausführung in der Cloud](./debugging-tools).

Um die in den Beispielen verwendete Klassenmodellierung noch einmal durchzugehen, siehe [Klassen und Objekte](../oop/classes). Eine Sammlung vollständiger Programme finden Sie unter [Beispiele](../reference/examples).
