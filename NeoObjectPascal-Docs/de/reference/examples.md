# Beispielgalerie

Das NeoObjectPascal-Repository enthält eine Sammlung fertiger Beispiele unter `NeoObjectPascal/examples/`, nummeriert von `01` bis `44`. Sie reichen von „Hallo, Welt!" bis hin zu Objektorientierung, Tests mit Mocking und Ausführung in der Cloud. Diese Seite ordnet sie nach Thema und zeigt den Code einiger repräsentativer Beispiele.

## Ausführung

Führen Sie ein beliebiges Beispiel aus, indem Sie den Dateipfad an das JAR des Interpreters übergeben:

```bash
java -jar neoobjectpascal.jar examples/01-OlaMundo.npas
```

Testdateien verwenden die Erweiterung `.test.npas` und werden im Testmodus ausgeführt:

```bash
# um arquivo de teste
java -jar neoobjectpascal.jar -t examples/34-TesteSimples.test.npas

# todos os testes de um diretório (recursivo, com cobertura)
java -jar neoobjectpascal.jar --test-all examples/
```

::: tip
Der tatsächliche Name des JAR hängt von Ihrem Build ab (zum Beispiel `target/neoobjectpascal-*.jar`). Weitere Informationen zum Erzeugen des Interpreters finden Sie in der [Einführung](../getting-started/introduction).
:::

## Grundlagen

| Datei | Zeigt |
| --- | --- |
| `01-OlaMundo.npas` | Das minimale Programm: ein `WriteLn` im Hauptblock |
| `02-Variaveis.npas` | Variablendeklaration (`String`, `Integer`) und Zuweisung |
| `15-ConcatenacaoStrings.npas` | String-Verkettung mit `+` und Einlesen von Eingaben |
| `16-ExemploOriginal.npas` | Einführungsbeispiel, das mehrere Funktionen kombiniert |

```npas
begin
  WriteLn("Olá, Mundo!");
end.
```

<Output>
Olá, Mundo!
</Output>

## Steuerung und Funktionen

| Datei | Zeigt |
| --- | --- |
| `03-EstruturasDeControle.npas` | `if ... then` und Schleife `for i := 1 to N do` |
| `04-Funcoes.npas` | Funktionsdeklaration und -aufruf mit `return` |
| `07-ProgramacaoFuncional.npas` | Verketteter Pipe-Operator `\|>` |

```npas
function somar(a, b): Integer;
begin
  return a + b;
end;

var resultado: Integer;

begin
  resultado := somar(20, 22);
  WriteLn("O resultado da soma é: ", resultado);
end.
```

<Output>
O resultado da soma é: 42
</Output>

Der Pipe-Operator übergibt den linken Wert als erstes Argument der rechten Funktion:

```npas
function dobrar(x: Integer): Integer;
begin
  return x * 2;
end;

function adicionarUm(x: Integer): Integer;
begin
  return x + 1;
end;

begin
  WriteLn("O resultado do pipeline é: ", 5 |> dobrar |> adicionarUm);
end.
```

<Output>
O resultado do pipeline é: 11
</Output>

## Daten und Ein-/Ausgabe

| Datei | Zeigt |
| --- | --- |
| `05-JSON.npas` | `JSON.parse(...) into` zum Laden eines Objekts |
| `06-CSV.npas` | `CSV.parse(...) into` zum Laden von Zeilen |
| `08-EntradaSaida.npas` | `WriteLn` und `showMenu` |
| `13-MenuInterativo.npas` | Nummeriertes Menü mit `showMenu` |
| `14-EntradaUsuario.npas` | Benutzereingabe mit `ReadLn` |

```npas
var dados: Object;
var jsonString: String;

begin
  jsonString := '{"nome": "Carmen Sandiego", "idade": 30, "cidade": "São Paulo"}';
  JSON.parse(jsonString) into dados;
  WriteLn("Dados JSON processados com sucesso!");
end.
```

<Output>
Dados JSON processados com sucesso!
</Output>

## Module und interne Bibliotheken

| Datei | Zeigt |
| --- | --- |
| `09-Modulos.npas` | `uses` eines lokalen `.npas`-Moduls |
| `10-ModulosCompleto.npas` | Verwendung mehrerer Module |
| `11-ProjetoHierarquico.npas` | Module in hierarchischen Ordnern (`lib.core.calculadora`) |
| `17-BibliotecasInternas.npas` | Funktionen aus `internal.math`, `internal.string`, `internal.datetime` |
| `26-TesteDatetime.npas` | Bibliothek `internal.datetime` |
| `28-TesteFile.npas` / `29-TesteFileSimples.npas` | Bibliothek `internal.file` |

```npas
uses internal.math, internal.string;

begin
  WriteLn("Fatorial de 5: ", factorial(5));
  WriteLn("Fibonacci(8): ", fibonacci(8));
  WriteLn("Maiúsculas: ", toUpperCase("neoobjectpascal"));
end.
```

Einzelheiten zu allen Funktionen finden Sie unter [Interne Bibliotheken](../features/internal-libraries).

## Java und hybride Systeme

| Datei | Zeigt |
| --- | --- |
| `20-SistemaHibrido.npas` | Kombination von Pascal-Code und eingebettetem Java |
| `21-TesteHibridoSimples.npas` | Vereinfachte Version des hybriden Systems |
| `22-TesteSemJava.npas` | Entsprechung ohne Java zum Vergleich |
| `23-DebugJava.npas` | Debugging von Java-Blöcken |
| `24-FuncaoJava.npas` | Funktion, die einen Wert aus einem Java-Block zurückgibt |
| `25-JavaComParametros.npas` | Java-Block, der Parameter empfängt (`param0`, `param1`, ...) |
| `27-SistemaHibridoFinal.npas` | Vollständiges hybrides System |

```npas
function testeJava(): String
begin
    return java:() {
        return "Olá do Java!";
    };
end;

begin
    WriteLn("Resultado: ", testeJava());
end.
```

<Output>
Resultado: Olá do Java!
</Output>

Mehr dazu unter [Java-Integration](../features/java-integration).

## Objektorientierung

| Datei | Zeigt |
| --- | --- |
| `30-ClasseSimples.npas` | Klasse mit Feldern, `constructor Create` und Methode |
| `31-Heranca.npas` | Vererbung mit `extends`, `virtual`/`override` |
| `32-Interface.npas` | `interface` und `implements` |
| `33-Polimorfismo.npas` | Polymorphie mit überschriebenen Methoden |

```npas
class Pessoa
    var nome: String;
    var idade: Integer;

    constructor Create(n: String, i: Integer)
    begin
        self.nome := n;
        self.idade := i;
    end;

    public function apresentar(): String
    begin
        return "Olá, meu nome é " + self.nome + " e tenho " + self.idade + " anos.";
    end;
end;

var pessoa: Pessoa;

begin
    pessoa := new Pessoa("João", 30);
    WriteLn(pessoa.apresentar());
end.
```

<Output>
Olá, meu nome é João e tenho 30 anos.
</Output>

Vollständiges Handbuch unter [Klassen](../oop/classes), [Vererbung](../oop/inheritance-polymorphism) und [Interfaces](../oop/interfaces).

## Tests und Mocking

| Datei | Zeigt |
| --- | --- |
| `18-TesteSimples.npas` / `19-TesteCompleto.npas` | Einführende `test`-Blöcke |
| `34-TesteSimples.test.npas` | GIVEN-WHEN-THEN-Muster mit `expect(...).toBe(...)` |
| `35-TesteHeranca.test.npas` | Vererbungstests |
| `36-TesteInterface.test.npas` | Interface-Tests |
| `37-TestePolimorfismo.test.npas` | Polymorphie-Tests |
| `38-TesteMocking.test.npas` | `mock ... thenReturn` und `verify` |

```npas
class Calculadora
    public function somar(a: Integer, b: Integer): Integer
    begin
        return a + b;
    end;
end;

test "Calculadora deve somar dois números corretamente"
begin
    var calc: Calculadora;
    calc := new Calculadora();

    var resultado: Integer;
    resultado := calc.somar(5, 3);

    expect(resultado).toBe(8);
end;
```

Ersetzen Sie eine Abhängigkeit durch einen Mock und überprüfen Sie anschließend, dass sie aufgerufen wurde:

```npas
test "NotificadorUsuario deve chamar EmailService ao notificar"
begin
    var emailService: EmailService;
    emailService := new EmailService();

    mock EmailService enviarEmail thenReturn true;

    var notificador: NotificadorUsuario;
    notificador := new NotificadorUsuario(emailService);

    var resultado: Boolean;
    resultado := notificador.notificar("usuario@teste.com");

    expect(resultado).toBeTrue();
    verify EmailService enviarEmail;
end;
```

Vollständiges Handbuch unter [Unit-Tests](../testing/unit-testing).

## Debugging und Cloud

| Datei | Zeigt |
| --- | --- |
| `39-DebugSimples.npas` | Einfaches Programm für den interaktiven Debugger (`-d`) |
| `40-CloudExample.npas` | Beispiel für die Ausführung in NeoObjectPascal Cloud |
| `debug-boolean.npas`, `debug-class.npas`, `debug-class2.npas` | Ergänzende Debugging-Szenarien |

## Neue Funktionen

Beispiele, die aktuelle Sprachfunktionen einsetzen:

| Datei | Zeigt |
| --- | --- |
| `41-OperadoresBooleanos.npas` | Operatoren `and`, `or`, `not` in Bedingungen |
| `42-AritmenticaReal.npas` | Arithmetik mit `Real` und automatische Heraufstufung von `Integer` |
| `43-Arrays.npas` | Array-Literale, nullbasierter Index, Elementzuweisung und `for ... in` |
| `44-TratamentoErros.npas` | `try`/`catch`/`finally` und `raise` |

```npas
var numeros: Array;
var item: Integer;
var soma: Integer;

begin
    numeros := [10, 20, 30, 40, 50];
    numeros[2] := 99;              // atribuição de elemento (base 0)

    soma := 0;
    for item in numeros do
        soma := soma + item;

    WriteLn("Soma: ", soma);
end.
```

<Output>
Soma: 219
</Output>

Fehlerbehandlung mit `try`/`catch`/`finally` und `raise`:

```npas
function validarIdade(idade: Integer): String
begin
    if idade < 0 then
        raise "Idade invalida: " + idade;
    return "Idade valida: " + idade;
end;

begin
    try
    begin
        WriteLn(validarIdade(0 - 5));
    end
    catch (erro)
    begin
        WriteLn("Capturado: ", erro);
    end
    finally
    begin
        WriteLn("Cleanup concluido");
    end;
end.
```

<Output>
Capturado: Idade invalida: -5
Cleanup concluido
</Output>

## Weiter erkunden

Neu hier? Beginnen Sie mit der [Einführung](../getting-started/introduction), um den Interpreter zu installieren und Ihr erstes Programm auszuführen. Die vollständige Syntax finden Sie in der [Sprachreferenz](./language-reference).
