# Unit testing and mocking

NeoObjectPascal ships with a **native testing framework** — it is not an external library, it is part of the language itself. You write tests with the `test` keyword, validate results with `expect(...)`, and run everything directly through the interpreter, with a results and coverage report.

This guide covers writing tests, all the available matchers, organizing and running suites, and the **mocking** system for isolating dependencies.

## Writing a test

A test is a top-level declaration, at the same level as functions and classes. The form is:

```npas
test "descrição do teste"
begin
    // corpo do teste
end;
```

The description is a free-form string that appears in the report. Inside the `begin ... end` block you write ordinary code: declare variables, call functions, create objects, and, at the end, make the assertions with `expect`.

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

::: tip GIVEN-WHEN-THEN pattern
Organizing each test into three blocks — **GIVEN** (setup), **WHEN** (action), and **THEN** (assertion) — makes the intent clear. It is the same as Arrange-Act-Assert. Use `//` comments to mark each phase.
:::

## Matchers (`expect`)

An assertion always begins with `expect(actualValue)` followed by a matcher that describes the expectation. If the expectation fails, the test is marked as `[FAIL]` with a message; otherwise, `[PASS]`.

### `.toBe(expected)`

Checks exact equality. It is the most-used matcher and works for numbers, strings, and booleans.

```npas
test "toBe compara valores exatos"
begin
    expect(2 + 2).toBe(4);
    expect("Olá" + " mundo").toBe("Olá mundo");
end;
```

### `.toEqual(expected)`

Equivalent to `.toBe` for value equality — use it when you want the text to make explicit that it is a structural equality comparison.

```npas
test "toEqual verifica igualdade"
begin
    var total: Integer;
    total := 100 + 50;
    expect(total).toEqual(150);
end;
```

### `.toBeTrue()` and `.toBeFalse()`

Check boolean values without having to compare with `true`/`false` explicitly.

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

Checks whether a value is null — useful for fields not yet initialized or missing return values.

```npas
test "campo não inicializado começa nulo"
begin
    var resultado: Object;
    expect(resultado).toBeNull();
end;
```

| Matcher | Checks |
|---------|----------|
| `.toBe(v)` | exact equality |
| `.toEqual(v)` | value equality |
| `.toBeTrue()` | value is true |
| `.toBeFalse()` | value is false |
| `.toBeNull()` | value is null |

## Organizing tests

- **`.test.npas` extension** — test files use the `.test.npas` suffix (for example, `calculadora.test.npas`). It is this convention that the test runner recognizes.
- **One behavior per test** — each `test` should verify a single behavior. Prefer several small, well-named tests over one large test that validates everything.
- **Independent tests** — each test should create its own instances and not depend on the execution order.

A typical layout separates source code from tests:

```
projeto/
├── src/
│   ├── calculadora.npas
│   └── conta.npas
└── tests/
    ├── calculadora.test.npas
    └── conta.test.npas
```

## Running the tests

### A single file with `-t`

To run an isolated test file, use the `-t` flag (or `--test`):

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

When a test fails, the report shows the description and the difference found:

<Output>
========== TEST RESULTS ==========
[PASS] Calculadora deve somar dois números corretamente
[FAIL] Calculadora deve multiplicar dois números corretamente: esperado 42 mas obteve 40
==================================
Total: 3 | Passed: 2 | Failed: 1
</Output>

### All tests with `--test-all`

To run an entire suite recursively by pointing to a directory, use `--test-all` (or `-ta`). It finds every `.test.npas` file below the directory and also presents a **coverage** summary. Coverage is measured by the **public class methods exercised by the tests** — each public method of a class counts, and the report lists the covered and uncovered methods per class. It is not a ratio of test files to source files:

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

::: tip Coverage goal
Aim to keep coverage at **80% or more** of your classes' public methods. Every public method not exercised by any test shows up as uncovered in the report, pointing to exactly where tests are missing.
:::

::: info Exit code
The runner returns a non-zero exit code when there are failures, which lets you use it directly in CI/CD pipelines.
:::

## Mocking

Unit tests should be fast and predictable. When the unit you are testing depends on something external — an email service, an API, a database — you do not want to fire off the real call. That is where **mocking** comes in: you replace a function or method with a version that returns a fixed value, and then verify that it was called.

### Syntax

```npas
// Substitui uma função ou método por um retorno fixo:
mock calcularDesconto thenReturn 50;
mock EmailService enviarEmail thenReturn true;

// Verifica que a função/método mockado foi chamado:
verify calcularDesconto;
verify EmailService enviarEmail;
```

- `mock Target method thenReturn value;` — for class methods, provide the class and the method. For standalone functions, provide only the name. The dotted form (`mock EmailService.enviarEmail thenReturn true;`) is also accepted.
- `verify Target method;` — fails the test if the mocked target was never called.

The convention is to **declare the mocks in the GIVEN block** and **do the `verify` calls in the THEN block**.

### Example: isolating an email service

Consider a `NotificadorUsuario` that depends on an `EmailService`. In the test we do not want to send a real email — only ensure that the notifier delegates the call correctly and returns success.

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

### Example: mocking a standalone function

The same mechanism works for top-level functions. Here we replace a computation with a fixed value:

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

::: warning Mocking scope
The current mocking replaces the target's entire return value and verifies **whether** it was called. There is not yet matching by specific parameters, call counting, or order verification. Mock only the external dependencies and keep the internal logic under real test.
:::

## Next steps

With automated tests in place, the natural next step is learning to investigate behavior at run time. Continue to [Debugger, VS Code, and cloud execution](./debugging-tools).

To review the class modeling used in the examples, see [Classes and objects](../oop/classes). A collection of complete programs is available in [Examples](../reference/examples).
