# Testes Unitários no NeoObjectPascal

## Visão Geral

O NeoObjectPascal inclui um framework de testes unitários integrado que permite:

- Escrever testes unitários usando padrão **GIVEN-WHEN-THEN**
- Executar testes com o parâmetro `-t`
- Usar assertions para validar resultados
- Organizar testes em arquivos `.test.npas`
- Suporte básico a mocking

## Estrutura de um Teste

### Sintaxe Básica

```pascal
test "Descrição do teste"
begin
    // GIVEN - Preparação
    var objeto: MinhaClasse;
    objeto := new MinhaClasse();
    
    // WHEN - Ação
    var resultado: Integer;
    resultado := objeto.metodo();
    
    // THEN - Verificação
    expect(resultado).toBe(valorEsperado);
end;
```

## Assertions Disponíveis

### toBe(valor)

Verifica igualdade exata:

```pascal
expect(resultado).toBe(42);
expect(nome).toBe("João");
```

### toEqual(valor)

Verifica igualdade (similar a toBe):

```pascal
expect(resultado).toEqual(100);
```

### toBeTrue()

Verifica se o valor é verdadeiro:

```pascal
expect(condicao).toBeTrue();
```

### toBeFalse()

Verifica se o valor é falso:

```pascal
expect(condicao).toBeFalse();
```

### toBeNull()

Verifica se o valor é nulo:

```pascal
expect(objeto).toBeNull();
```

## Executando Testes

### Linha de Comando

```bash
java -jar neoobjectpascal.jar -t arquivo.test.npas
```

ou

```bash
java -jar neoobjectpascal.jar --test arquivo.test.npas
```

### Saída

```
========== TEST RESULTS ==========
[PASS] Teste que passou
[FAIL] Teste que falhou: Mensagem de erro
==================================
Total: 2 | Passed: 1 | Failed: 1
```

## Padrão GIVEN-WHEN-THEN

### GIVEN (Dado)

Prepara o ambiente de teste:

```pascal
// GIVEN - Dado uma calculadora
var calc: Calculadora;
calc := new Calculadora();
```

### WHEN (Quando)

Executa a ação a ser testada:

```pascal
// WHEN - Quando somamos 5 + 3
var resultado: Integer;
resultado := calc.somar(5, 3);
```

### THEN (Então)

Verifica o resultado esperado:

```pascal
// THEN - Então o resultado deve ser 8
expect(resultado).toBe(8);
```

## Exemplos Completos

### Exemplo 1: Teste de Classe Simples

```pascal
class Calculadora
    public function somar(a: Integer, b: Integer): Integer
    begin
        return a + b;
    end;
    
    public function subtrair(a: Integer, b: Integer): Integer
    begin
        return a - b;
    end;
end;

test "Calculadora deve somar dois números corretamente"
begin
    // GIVEN
    var calc: Calculadora;
    calc := new Calculadora();
    
    // WHEN
    var resultado: Integer;
    resultado := calc.somar(5, 3);
    
    // THEN
    expect(resultado).toBe(8);
end;

test "Calculadora deve subtrair dois números corretamente"
begin
    // GIVEN
    var calc: Calculadora;
    calc := new Calculadora();
    
    // WHEN
    var resultado: Integer;
    resultado := calc.subtrair(10, 4);
    
    // THEN
    expect(resultado).toBe(6);
end;
```

### Exemplo 2: Teste de Herança

```pascal
class ContaBancaria
    var saldo: Integer;
    
    constructor Create(saldoInicial: Integer)
    begin
        self.saldo := saldoInicial;
    end;
    
    public function getSaldo(): Integer
    begin
        return self.saldo;
    end;
    
    public function depositar(valor: Integer): Integer
    begin
        self.saldo := self.saldo + valor;
        return self.saldo;
    end;
end;

test "ContaBancaria deve inicializar com saldo correto"
begin
    // GIVEN
    var saldoInicial: Integer;
    saldoInicial := 1000;
    
    // WHEN
    var conta: ContaBancaria;
    conta := new ContaBancaria(saldoInicial);
    
    // THEN
    expect(conta.getSaldo()).toBe(1000);
end;

test "ContaBancaria deve depositar valor corretamente"
begin
    // GIVEN
    var conta: ContaBancaria;
    conta := new ContaBancaria(100);
    
    // WHEN
    var novoSaldo: Integer;
    novoSaldo := conta.depositar(50);
    
    // THEN
    expect(novoSaldo).toBe(150);
    expect(conta.getSaldo()).toBe(150);
end;
```

### Exemplo 3: Teste de Interface

```pascal
interface Validavel
    function validar(): Boolean;
end;

class Email implements Validavel
    public var endereco: String;
    
    constructor Create(email: String)
    begin
        self.endereco := email;
    end;
    
    public function validar(): Boolean
    begin
        return true;
    end;
end;

test "Email válido deve passar na validação"
begin
    // GIVEN
    var email: Email;
    email := new Email("teste@email.com");
    
    // WHEN
    var resultado: Boolean;
    resultado := email.validar();
    
    // THEN
    expect(resultado).toBeTrue();
end;
```

### Exemplo 4: Teste de Polimorfismo

```pascal
class FiguraGeometrica
    public virtual function calcularArea(): Integer
    begin
        return 0;
    end;
end;

class Retangulo extends FiguraGeometrica
    var largura: Integer;
    var altura: Integer;
    
    constructor Create(l: Integer, a: Integer)
    begin
        self.largura := l;
        self.altura := a;
    end;
    
    public override function calcularArea(): Integer
    begin
        return self.largura * self.altura;
    end;
end;

test "Retângulo deve calcular área corretamente"
begin
    // GIVEN
    var retangulo: Retangulo;
    retangulo := new Retangulo(5, 3);
    
    // WHEN
    var area: Integer;
    area := retangulo.calcularArea();
    
    // THEN
    expect(area).toBe(15);
end;

test "Polimorfismo permite usar método virtual da classe base"
begin
    // GIVEN
    var figura: FiguraGeometrica;
    figura := new FiguraGeometrica();
    
    // WHEN
    var area: Integer;
    area := figura.calcularArea();
    
    // THEN
    expect(area).toBe(0);
end;
```

## Organização de Testes

### Convenção de Nomes

- Arquivos de teste devem ter extensão `.test.npas`
- Exemplo: `calculadora.test.npas`, `conta-bancaria.test.npas`

### Estrutura de Diretórios

```
projeto/
├── src/
│   ├── calculadora.npas
│   └── conta.npas
└── tests/
    ├── calculadora.test.npas
    └── conta.test.npas
```

## Mocking (Básico)

### Sintaxe

```pascal
test "Teste com mock"
begin
    // GIVEN
    var objeto: MinhaClasse;
    mock(objeto);
    
    // WHEN
    // ... código de teste
    
    // THEN
    verify(objeto.metodo);
end;
```

**Nota**: O suporte a mocking é básico. Para casos mais complexos, considere usar objetos de teste reais.

## Boas Práticas

### 1. Um Teste, Uma Responsabilidade

Cada teste deve verificar apenas um comportamento:

```pascal
// BOM
test "Calculadora deve somar números positivos"
begin
    // ...
end;

test "Calculadora deve somar números negativos"
begin
    // ...
end;

// RUIM
test "Calculadora deve fazer todas as operações"
begin
    // testa soma, subtração, multiplicação, etc.
end;
```

### 2. Nomes Descritivos

Use nomes que descrevam claramente o que está sendo testado:

```pascal
// BOM
test "ContaBancaria deve lançar erro ao sacar valor maior que saldo"

// RUIM
test "Teste de saque"
```

### 3. Arrange-Act-Assert (GIVEN-WHEN-THEN)

Sempre organize seus testes neste padrão:

```pascal
test "Descrição clara"
begin
    // GIVEN - Preparação
    
    // WHEN - Ação
    
    // THEN - Verificação
end;
```

### 4. Testes Independentes

Cada teste deve ser independente e não depender da ordem de execução:

```pascal
// BOM - Cada teste cria sua própria instância
test "Teste 1"
begin
    var obj: MinhaClasse;
    obj := new MinhaClasse();
    // ...
end;

test "Teste 2"
begin
    var obj: MinhaClasse;
    obj := new MinhaClasse();
    // ...
end;
```

## Limitações Atuais

1. Não há suporte a setup/teardown automático
2. Mocking é básico e limitado
3. Não há suporte a testes parametrizados
4. Não há suporte a grupos de testes (test suites)
5. Não há geração de relatórios em HTML/XML

## Executando Múltiplos Testes

Para executar todos os testes de um projeto:

```bash
# Linux/Mac
find tests -name "*.test.npas" -exec java -jar neoobjectpascal.jar -t {} \;

# Windows
for /r tests %f in (*.test.npas) do java -jar neoobjectpascal.jar -t "%f"
```

## Integração Contínua

Exemplo de script para CI/CD:

```bash
#!/bin/bash
EXIT_CODE=0

for test_file in tests/*.test.npas; do
    echo "Executando $test_file..."
    java -jar neoobjectpascal.jar -t "$test_file"
    if [ $? -ne 0 ]; then
        EXIT_CODE=1
    fi
done

exit $EXIT_CODE
```

## Conclusão

O framework de testes unitários do NeoObjectPascal fornece as ferramentas essenciais para garantir a qualidade do código através de testes automatizados. Use-o em conjunto com os recursos de orientação a objetos para criar aplicações robustas e bem testadas.
