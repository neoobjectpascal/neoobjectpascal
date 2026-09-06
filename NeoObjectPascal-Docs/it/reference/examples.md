# Galleria di esempi

Il repository di NeoObjectPascal include una raccolta di esempi pronti all'uso in `NeoObjectPascal/examples/`, numerati da `01` a `44`. Spaziano dal "Ciao, Mondo!" alla programmazione orientata agli oggetti, ai test con mocking e all'esecuzione nel cloud. Questa pagina li organizza per tema e mostra il codice di alcuni esempi rappresentativi.

## Come eseguire

Esegui qualsiasi esempio passando il percorso del file al JAR dell'interprete:

```bash
java -jar neoobjectpascal.jar examples/01-OlaMundo.npas
```

I file di test usano l'estensione `.test.npas` e vengono eseguiti in modalità test:

```bash
# un singolo file di test
java -jar neoobjectpascal.jar -t examples/34-TesteSimples.test.npas

# tutti i test di una directory (ricorsivo, con copertura)
java -jar neoobjectpascal.jar --test-all examples/
```

::: tip
Il nome effettivo del JAR dipende dalla tua build (ad esempio, `target/neoobjectpascal-*.jar`). Consulta l'[Introduzione](../getting-started/introduction) per generare l'interprete.
:::

## Fondamenti

| File | Dimostra |
| --- | --- |
| `01-OlaMundo.npas` | Il programma minimo: un `WriteLn` nel blocco principale |
| `02-Variaveis.npas` | Dichiarazione di variabili (`String`, `Integer`) e assegnazione |
| `15-ConcatenacaoStrings.npas` | Concatenazione di stringhe con `+` e lettura dell'input |
| `16-ExemploOriginal.npas` | Esempio introduttivo che combina diverse funzionalità |

```npas
begin
  WriteLn("Olá, Mundo!");
end.
```

<Output>
Olá, Mundo!
</Output>

## Controllo e funzioni

| File | Dimostra |
| --- | --- |
| `03-EstruturasDeControle.npas` | `if ... then` e ciclo `for i := 1 to N do` |
| `04-Funcoes.npas` | Dichiarazione e chiamata di funzione con `return` |
| `07-ProgramacaoFuncional.npas` | Operatore pipe `\|>` concatenato |

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

L'operatore pipe passa il valore di sinistra come primo argomento della funzione a destra:

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

## Dati e I/O

| File | Dimostra |
| --- | --- |
| `05-JSON.npas` | `JSON.parse(...) into` per caricare un oggetto |
| `06-CSV.npas` | `CSV.parse(...) into` per caricare righe |
| `08-EntradaSaida.npas` | `WriteLn` e `showMenu` |
| `13-MenuInterativo.npas` | Menu numerato con `showMenu` |
| `14-EntradaUsuario.npas` | Input dell'utente con `ReadLn` |

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

## Moduli e librerie interne

| File | Dimostra |
| --- | --- |
| `09-Modulos.npas` | `uses` di un modulo `.npas` locale |
| `10-ModulosCompleto.npas` | Uso di più moduli |
| `11-ProjetoHierarquico.npas` | Moduli in cartelle gerarchiche (`lib.core.calculadora`) |
| `17-BibliotecasInternas.npas` | Funzioni di `internal.math`, `internal.string`, `internal.datetime` |
| `26-TesteDatetime.npas` | Libreria `internal.datetime` |
| `28-TesteFile.npas` / `29-TesteFileSimples.npas` | Libreria `internal.file` |

```npas
uses internal.math, internal.string;

begin
  WriteLn("Fatorial de 5: ", factorial(5));
  WriteLn("Fibonacci(8): ", fibonacci(8));
  WriteLn("Maiúsculas: ", toUpperCase("neoobjectpascal"));
end.
```

Dettagli di tutte le funzioni in [Librerie interne](../features/internal-libraries).

## Java e sistemi ibridi

| File | Dimostra |
| --- | --- |
| `20-SistemaHibrido.npas` | Combinazione di codice Pascal e Java incorporato |
| `21-TesteHibridoSimples.npas` | Versione semplificata del sistema ibrido |
| `22-TesteSemJava.npas` | Equivalente senza Java per confronto |
| `23-DebugJava.npas` | Debug di blocchi Java |
| `24-FuncaoJava.npas` | Funzione che restituisce un valore da un blocco Java |
| `25-JavaComParametros.npas` | Blocco Java che riceve parametri (`param0`, `param1`, ...) |
| `27-SistemaHibridoFinal.npas` | Sistema ibrido completo |

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

Altro in [Integrazione con Java](../features/java-integration).

## Programmazione orientata agli oggetti

| File | Dimostra |
| --- | --- |
| `30-ClasseSimples.npas` | Classe con campi, `constructor Create` e metodo |
| `31-Heranca.npas` | Ereditarietà con `extends`, `virtual`/`override` |
| `32-Interface.npas` | `interface` e `implements` |
| `33-Polimorfismo.npas` | Polimorfismo con metodi sovrascritti |

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

Guida completa in [Classi](../oop/classes), [Ereditarietà](../oop/inheritance-polymorphism) e [Interfacce](../oop/interfaces).

## Test e mocking

| File | Dimostra |
| --- | --- |
| `18-TesteSimples.npas` / `19-TesteCompleto.npas` | Blocchi `test` introduttivi |
| `34-TesteSimples.test.npas` | Pattern GIVEN-WHEN-THEN con `expect(...).toBe(...)` |
| `35-TesteHeranca.test.npas` | Test di ereditarietà |
| `36-TesteInterface.test.npas` | Test di interfaccia |
| `37-TestePolimorfismo.test.npas` | Test di polimorfismo |
| `38-TesteMocking.test.npas` | `mock ... thenReturn` e `verify` |

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

Sostituisci una dipendenza con un mock e poi verifica che sia stata chiamata:

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

Guida completa in [Test unitari](../testing/unit-testing).

## Debug e cloud

| File | Dimostra |
| --- | --- |
| `39-DebugSimples.npas` | Programma semplice per il debugger interattivo (`-d`) |
| `40-CloudExample.npas` | Esempio da eseguire su NeoObjectPascal Cloud |
| `debug-boolean.npas`, `debug-class.npas`, `debug-class2.npas` | Scenari ausiliari di debug |

## Nuove funzionalità

Esempi che mettono alla prova le funzionalità più recenti del linguaggio:

| File | Dimostra |
| --- | --- |
| `41-OperadoresBooleanos.npas` | Operatori `and`, `or`, `not` nelle condizioni |
| `42-AritmenticaReal.npas` | Aritmetica con `Real` e auto-promozione di `Integer` |
| `43-Arrays.npas` | Array letterali, indice in base 0, assegnazione di elemento e `for ... in` |
| `44-TratamentoErros.npas` | `try`/`catch`/`finally` e `raise` |

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

Gestione degli errori con `try`/`catch`/`finally` e `raise`:

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

## Continua a esplorare

Sei nuovo qui? Inizia dall'[Introduzione](../getting-started/introduction) per installare l'interprete ed eseguire il tuo primo programma. Per la sintassi completa, consulta la [Referenza del linguaggio](./language-reference).
