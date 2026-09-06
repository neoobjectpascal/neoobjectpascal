# Testes unitários e mocking

O NeoObjectPascal traz um **framework de testes nativo** — não é uma biblioteca externa, é parte da própria linguagem. Você escreve testes com a palavra-chave `test`, valida resultados com `expect(...)` e executa tudo diretamente pelo interpretador, com relatório de resultados e cobertura.

Este guia cobre a escrita de testes, todos os matchers disponíveis, a organização e execução de suítes e o sistema de **mocking** para isolar dependências.

## Escrevendo um teste

Um teste é uma declaração de topo, no mesmo nível de funções e classes. A forma é:

```npas
test "descrição do teste"
begin
    // corpo do teste
end;
```

A descrição é uma string livre que aparece no relatório. Dentro do bloco `begin ... end` você escreve código normal: declara variáveis, chama funções, cria objetos e, ao final, faz as verificações com `expect`.

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

::: tip Padrão GIVEN-WHEN-THEN
Organizar cada teste em três blocos — **GIVEN** (preparação), **WHEN** (ação) e **THEN** (verificação) — deixa a intenção clara. É o mesmo que Arrange-Act-Assert. Use comentários `//` para marcar cada fase.
:::

## Matchers (`expect`)

A verificação sempre começa com `expect(valorReal)` seguido de um matcher que descreve a expectativa. Se a expectativa falhar, o teste é marcado como `[FAIL]` com uma mensagem; caso contrário, `[PASS]`.

### `.toBe(esperado)`

Verifica igualdade exata. É o matcher mais usado, serve para números, strings e booleanos.

```npas
test "toBe compara valores exatos"
begin
    expect(2 + 2).toBe(4);
    expect("Olá" + " mundo").toBe("Olá mundo");
end;
```

### `.toEqual(esperado)`

Equivalente a `.toBe` para igualdade de valores — use quando quiser deixar explícito no texto que se trata de uma comparação de igualdade estrutural.

```npas
test "toEqual verifica igualdade"
begin
    var total: Integer;
    total := 100 + 50;
    expect(total).toEqual(150);
end;
```

### `.toBeTrue()` e `.toBeFalse()`

Verificam valores booleanos sem precisar comparar com `true`/`false` explicitamente.

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

Verifica se um valor é nulo — útil para campos ainda não inicializados ou retornos ausentes.

```npas
test "campo não inicializado começa nulo"
begin
    var resultado: Object;
    expect(resultado).toBeNull();
end;
```

| Matcher | Verifica |
|---------|----------|
| `.toBe(v)` | igualdade exata |
| `.toEqual(v)` | igualdade de valor |
| `.toBeTrue()` | valor é verdadeiro |
| `.toBeFalse()` | valor é falso |
| `.toBeNull()` | valor é nulo |

## Organizando os testes

- **Extensão `.test.npas`** — arquivos de teste usam o sufixo `.test.npas` (por exemplo, `calculadora.test.npas`). É essa convenção que o executor de testes reconhece.
- **Um comportamento por teste** — cada `test` deve verificar um único comportamento. Prefira vários testes pequenos e bem nomeados a um teste grande que valida tudo.
- **Testes independentes** — cada teste deve criar suas próprias instâncias e não depender da ordem de execução.

Uma organização típica separa código-fonte de testes:

```
projeto/
├── src/
│   ├── calculadora.npas
│   └── conta.npas
└── tests/
    ├── calculadora.test.npas
    └── conta.test.npas
```

## Executando os testes

### Um único arquivo com `-t`

Para rodar um arquivo de teste isolado, use a flag `-t` (ou `--test`):

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

Quando um teste falha, o relatório mostra a descrição e a diferença encontrada:

<Output>
========== TEST RESULTS ==========
[PASS] Calculadora deve somar dois números corretamente
[FAIL] Calculadora deve multiplicar dois números corretamente: esperado 42 mas obteve 40
==================================
Total: 3 | Passed: 2 | Failed: 1
</Output>

### Todos os testes com `--test-all`

Para rodar uma suíte inteira de forma recursiva, apontando para um diretório, use `--test-all` (ou `-ta`). Ele encontra todos os arquivos `.test.npas` abaixo do diretório e ainda apresenta um resumo de **cobertura**. A cobertura é medida pelos **métodos públicos de classe exercitados pelos testes** — cada método público de uma classe conta, e o relatório lista os métodos cobertos e não cobertos por classe. Não é uma razão entre arquivos de teste e arquivos de código:

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

::: tip Meta de cobertura
Busque manter a cobertura em **80% ou mais** dos métodos públicos das suas classes. Cada método público não exercitado por nenhum teste aparece como não coberto no relatório, apontando exatamente onde faltam testes.
:::

::: info Código de saída
O executor retorna código de saída diferente de zero quando há falhas, o que permite usá-lo diretamente em pipelines de CI/CD.
:::

## Mocking

Testes unitários devem ser rápidos e previsíveis. Quando a unidade que você está testando depende de algo externo — um serviço de e-mail, uma API, um banco de dados — você não quer disparar a chamada real. É aí que entra o **mocking**: você substitui uma função ou método por uma versão que devolve um valor fixo, e depois verifica que ela foi chamada.

### Sintaxe

```npas
// Substitui uma função ou método por um retorno fixo:
mock calcularDesconto thenReturn 50;
mock EmailService enviarEmail thenReturn true;

// Verifica que a função/método mockado foi chamado:
verify calcularDesconto;
verify EmailService enviarEmail;
```

- `mock Alvo metodo thenReturn valor;` — para métodos de classe, informe a classe e o método. Para funções isoladas, informe apenas o nome. A forma com ponto (`mock EmailService.enviarEmail thenReturn true;`) também é aceita.
- `verify Alvo metodo;` — falha o teste se o alvo mockado nunca tiver sido chamado.

A convenção é **declarar os mocks no bloco GIVEN** e **fazer os `verify` no bloco THEN**.

### Exemplo: isolando um serviço de e-mail

Considere um `NotificadorUsuario` que depende de um `EmailService`. No teste, não queremos enviar e-mail de verdade — apenas garantir que o notificador delega a chamada corretamente e retorna sucesso.

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

### Exemplo: mockando uma função isolada

O mesmo mecanismo funciona para funções de topo. Aqui substituímos um cálculo por um valor fixo:

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

::: warning Escopo do mocking
O mocking atual substitui o retorno completo do alvo e verifica **se** ele foi chamado. Ainda não há correspondência por parâmetros específicos, contagem de chamadas nem verificação de ordem. Mocke apenas as dependências externas e mantenha a lógica interna sob teste real.
:::

## Próximos passos

Com testes automatizados no lugar, o passo natural é aprender a investigar comportamentos em tempo de execução. Continue em [Debugger, VS Code e execução na nuvem](./debugging-tools).

Para revisar a modelagem de classes usada nos exemplos, veja [Classes e objetos](../oop/classes). Uma coletânea de programas completos está em [Exemplos](../reference/examples).
