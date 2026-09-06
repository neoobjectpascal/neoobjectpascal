# Test unitari e mocking

NeoObjectPascal offre un **framework di test nativo** — non è una libreria esterna, è parte del linguaggio stesso. Scrivi i test con la parola chiave `test`, verifichi i risultati con `expect(...)` ed esegui il tutto direttamente tramite l'interprete, con report dei risultati e copertura.

Questa guida copre la scrittura dei test, tutti i matcher disponibili, l'organizzazione e l'esecuzione delle suite e il sistema di **mocking** per isolare le dipendenze.

## Scrivere un test

Un test è una dichiarazione di primo livello, allo stesso livello di funzioni e classi. La forma è:

```npas
test "descrição do teste"
begin
    // corpo do teste
end;
```

La descrizione è una stringa libera che appare nel report. All'interno del blocco `begin ... end` scrivi codice normale: dichiari variabili, chiami funzioni, crei oggetti e, alla fine, esegui le verifiche con `expect`.

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

::: tip Pattern GIVEN-WHEN-THEN
Organizzare ogni test in tre blocchi — **GIVEN** (preparazione), **WHEN** (azione) e **THEN** (verifica) — rende chiara l'intenzione. È lo stesso di Arrange-Act-Assert. Usa i commenti `//` per marcare ciascuna fase.
:::

## Matcher (`expect`)

La verifica inizia sempre con `expect(valoreReale)` seguito da un matcher che descrive l'aspettativa. Se l'aspettativa fallisce, il test viene marcato come `[FAIL]` con un messaggio; in caso contrario, `[PASS]`.

### `.toBe(esperado)`

Verifica l'uguaglianza esatta. È il matcher più usato, adatto a numeri, stringhe e booleani.

```npas
test "toBe compara valores exatos"
begin
    expect(2 + 2).toBe(4);
    expect("Olá" + " mundo").toBe("Olá mundo");
end;
```

### `.toEqual(esperado)`

Equivalente a `.toBe` per l'uguaglianza di valori — usalo quando vuoi rendere esplicito nel testo che si tratta di un confronto di uguaglianza strutturale.

```npas
test "toEqual verifica igualdade"
begin
    var total: Integer;
    total := 100 + 50;
    expect(total).toEqual(150);
end;
```

### `.toBeTrue()` e `.toBeFalse()`

Verificano valori booleani senza bisogno di confrontarli esplicitamente con `true`/`false`.

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

Verifica se un valore è nullo — utile per campi non ancora inizializzati o ritorni assenti.

```npas
test "campo não inicializado começa nulo"
begin
    var resultado: Object;
    expect(resultado).toBeNull();
end;
```

| Matcher | Verifica |
|---------|----------|
| `.toBe(v)` | uguaglianza esatta |
| `.toEqual(v)` | uguaglianza di valore |
| `.toBeTrue()` | il valore è vero |
| `.toBeFalse()` | il valore è falso |
| `.toBeNull()` | il valore è nullo |

## Organizzare i test

- **Estensione `.test.npas`** — i file di test usano il suffisso `.test.npas` (ad esempio, `calculadora.test.npas`). È questa convenzione che l'esecutore di test riconosce.
- **Un comportamento per test** — ogni `test` deve verificare un singolo comportamento. Preferisci molti test piccoli e ben nominati a un test grande che valida tutto.
- **Test indipendenti** — ogni test deve creare le proprie istanze e non dipendere dall'ordine di esecuzione.

Un'organizzazione tipica separa il codice sorgente dai test:

```
projeto/
├── src/
│   ├── calculadora.npas
│   └── conta.npas
└── tests/
    ├── calculadora.test.npas
    └── conta.test.npas
```

## Eseguire i test

### Un singolo file con `-t`

Per eseguire un file di test isolato, usa il flag `-t` (o `--test`):

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

Quando un test fallisce, il report mostra la descrizione e la differenza riscontrata:

<Output>
========== TEST RESULTS ==========
[PASS] Calculadora deve somar dois números corretamente
[FAIL] Calculadora deve multiplicar dois números corretamente: esperado 42 mas obteve 40
==================================
Total: 3 | Passed: 2 | Failed: 1
</Output>

### Tutti i test con `--test-all`

Per eseguire un'intera suite in modo ricorsivo, puntando a una directory, usa `--test-all` (o `-ta`). Trova tutti i file `.test.npas` sotto la directory e presenta inoltre un riepilogo di **copertura**. La copertura è misurata in base ai **metodi pubblici di classe esercitati dai test** — ogni metodo pubblico di una classe conta, e il report elenca i metodi coperti e non coperti per classe. Non è un rapporto tra file di test e file sorgente:

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

::: tip Obiettivo di copertura
Punta a mantenere la copertura all'**80% o più** dei metodi pubblici delle tue classi. Ogni metodo pubblico non esercitato da alcun test appare come non coperto nel report, indicando esattamente dove mancano i test.
:::

::: info Codice di uscita
L'esecutore restituisce un codice di uscita diverso da zero quando ci sono fallimenti, il che permette di usarlo direttamente nelle pipeline di CI/CD.
:::

## Mocking

I test unitari devono essere veloci e prevedibili. Quando l'unità che stai testando dipende da qualcosa di esterno — un servizio di posta elettronica, un'API, un database — non vuoi far scattare la chiamata reale. È qui che entra in gioco il **mocking**: sostituisci una funzione o un metodo con una versione che restituisce un valore fisso, e poi verifichi che sia stata chiamata.

### Sintassi

```npas
// Substitui uma função ou método por um retorno fixo:
mock calcularDesconto thenReturn 50;
mock EmailService enviarEmail thenReturn true;

// Verifica que a função/método mockado foi chamado:
verify calcularDesconto;
verify EmailService enviarEmail;
```

- `mock Alvo metodo thenReturn valor;` — per i metodi di classe, indica la classe e il metodo. Per le funzioni isolate, indica solo il nome. È accettata anche la forma con punto (`mock EmailService.enviarEmail thenReturn true;`).
- `verify Alvo metodo;` — fa fallire il test se il target mockato non è mai stato chiamato.

La convenzione è **dichiarare i mock nel blocco GIVEN** ed **eseguire i `verify` nel blocco THEN**.

### Esempio: isolare un servizio di posta elettronica

Considera un `NotificadorUsuario` che dipende da un `EmailService`. Nel test non vogliamo inviare e-mail reali — vogliamo solo garantire che il notificatore deleghi correttamente la chiamata e restituisca successo.

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

### Esempio: mockare una funzione isolata

Lo stesso meccanismo funziona per le funzioni di primo livello. Qui sostituiamo un calcolo con un valore fisso:

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

::: warning Ambito del mocking
Il mocking attuale sostituisce l'intero valore di ritorno del target e verifica **se** è stato chiamato. Non esiste ancora la corrispondenza per parametri specifici, il conteggio delle chiamate né la verifica dell'ordine. Mocka soltanto le dipendenze esterne e mantieni la logica interna sotto test reale.
:::

## Prossimi passi

Con i test automatizzati al loro posto, il passo naturale è imparare a investigare i comportamenti a runtime. Continua in [Debugger, VS Code e esecuzione nel cloud](./debugging-tools).

Per rivedere la modellazione delle classi usata negli esempi, consulta [Classi e oggetti](../oop/classes). Una raccolta di programmi completi si trova in [Esempi](../reference/examples).
