# Example gallery

The NeoObjectPascal repository includes a collection of ready-to-run examples in `NeoObjectPascal/examples/`, numbered from `01` to `44`. They range from "Hello, World!" to object orientation, tests with mocking, and cloud execution. This page organizes them by theme and shows the code of a few representative ones.

## How to run

Run any example by passing the file path to the interpreter JAR:

```bash
java -jar neoobjectpascal.jar examples/01-OlaMundo.npas
```

Test files use the `.test.npas` extension and run in test mode:

```bash
# um arquivo de teste
java -jar neoobjectpascal.jar -t examples/34-TesteSimples.test.npas

# todos os testes de um diretório (recursivo, com cobertura)
java -jar neoobjectpascal.jar --test-all examples/
```

::: tip
The actual JAR name depends on your build (for example, `target/neoobjectpascal-*.jar`). See the [Introduction](../getting-started/introduction) to generate the interpreter.
:::

## Fundamentals

| File | Demonstrates |
| --- | --- |
| `01-OlaMundo.npas` | The minimal program: a single `WriteLn` in the main block |
| `02-Variaveis.npas` | Variable declaration (`String`, `Integer`) and assignment |
| `15-ConcatenacaoStrings.npas` | String concatenation with `+` and reading input |
| `16-ExemploOriginal.npas` | Introductory example combining several features |

```npas
begin
  WriteLn("Olá, Mundo!");
end.
```

<Output>
Olá, Mundo!
</Output>

## Control flow and functions

| File | Demonstrates |
| --- | --- |
| `03-EstruturasDeControle.npas` | `if ... then` and the `for i := 1 to N do` loop |
| `04-Funcoes.npas` | Function declaration and call with `return` |
| `07-ProgramacaoFuncional.npas` | Chained pipe operator `\|>` |

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

The pipe operator passes the left-hand value as the first argument of the function on the right:

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

## Data and I/O

| File | Demonstrates |
| --- | --- |
| `05-JSON.npas` | `JSON.parse(...) into` to load an object |
| `06-CSV.npas` | `CSV.parse(...) into` to load rows |
| `08-EntradaSaida.npas` | `WriteLn` and `showMenu` |
| `13-MenuInterativo.npas` | Numbered menu with `showMenu` |
| `14-EntradaUsuario.npas` | User input with `ReadLn` |

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

## Modules and internal libraries

| File | Demonstrates |
| --- | --- |
| `09-Modulos.npas` | `uses` of a local `.npas` module |
| `10-ModulosCompleto.npas` | Using multiple modules |
| `11-ProjetoHierarquico.npas` | Modules in hierarchical folders (`lib.core.calculadora`) |
| `17-BibliotecasInternas.npas` | Functions from `internal.math`, `internal.string`, `internal.datetime` |
| `26-TesteDatetime.npas` | The `internal.datetime` library |
| `28-TesteFile.npas` / `29-TesteFileSimples.npas` | The `internal.file` library |

```npas
uses internal.math, internal.string;

begin
  WriteLn("Fatorial de 5: ", factorial(5));
  WriteLn("Fibonacci(8): ", fibonacci(8));
  WriteLn("Maiúsculas: ", toUpperCase("neoobjectpascal"));
end.
```

Details of all the functions in [Internal libraries](../features/internal-libraries).

## Java and hybrid systems

| File | Demonstrates |
| --- | --- |
| `20-SistemaHibrido.npas` | Combining Pascal code and embedded Java |
| `21-TesteHibridoSimples.npas` | A simplified version of the hybrid system |
| `22-TesteSemJava.npas` | The equivalent without Java for comparison |
| `23-DebugJava.npas` | Debugging Java blocks |
| `24-FuncaoJava.npas` | A function that returns a value from a Java block |
| `25-JavaComParametros.npas` | A Java block receiving parameters (`param0`, `param1`, ...) |
| `27-SistemaHibridoFinal.npas` | The complete hybrid system |

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

More in [Java integration](../features/java-integration).

## Object orientation

| File | Demonstrates |
| --- | --- |
| `30-ClasseSimples.npas` | A class with fields, `constructor Create` and a method |
| `31-Heranca.npas` | Inheritance with `extends`, `virtual`/`override` |
| `32-Interface.npas` | `interface` and `implements` |
| `33-Polimorfismo.npas` | Polymorphism with overridden methods |

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

Full guide in [Classes](../oop/classes), [Inheritance](../oop/inheritance-polymorphism) and [Interfaces](../oop/interfaces).

## Testing and mocking

| File | Demonstrates |
| --- | --- |
| `18-TesteSimples.npas` / `19-TesteCompleto.npas` | Introductory `test` blocks |
| `34-TesteSimples.test.npas` | GIVEN-WHEN-THEN pattern with `expect(...).toBe(...)` |
| `35-TesteHeranca.test.npas` | Inheritance tests |
| `36-TesteInterface.test.npas` | Interface tests |
| `37-TestePolimorfismo.test.npas` | Polymorphism tests |
| `38-TesteMocking.test.npas` | `mock ... thenReturn` and `verify` |

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

Replace a dependency with a mock and then verify it was called:

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

Full guide in [Unit testing](../testing/unit-testing).

## Debugging and cloud

| File | Demonstrates |
| --- | --- |
| `39-DebugSimples.npas` | A simple program for the interactive debugger (`-d`) |
| `40-CloudExample.npas` | An example to run on NeoObjectPascal Cloud |
| `debug-boolean.npas`, `debug-class.npas`, `debug-class2.npas` | Auxiliary debugging scenarios |

## New features

Examples that exercise recent language features:

| File | Demonstrates |
| --- | --- |
| `41-OperadoresBooleanos.npas` | The `and`, `or`, `not` operators in conditions |
| `42-AritmenticaReal.npas` | Arithmetic with `Real` and auto-promotion of `Integer` |
| `43-Arrays.npas` | Array literals, 0-based indexing, element assignment and `for ... in` |
| `44-TratamentoErros.npas` | `try`/`catch`/`finally` and `raise` |

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

Error handling with `try`/`catch`/`finally` and `raise`:

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

## Keep exploring

New here? Start with the [Introduction](../getting-started/introduction) to install the interpreter and run your first program. For the complete syntax, see the [Language reference](./language-reference).
