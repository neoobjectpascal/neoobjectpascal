# Galeria de exemplos

O repositório do NeoObjectPascal inclui uma coleção de exemplos prontos em `NeoObjectPascal/examples/`, numerados de `01` a `44`. Eles vão do "Olá, Mundo!" a orientação a objetos, testes com mocking e execução na nuvem. Esta página os organiza por tema e mostra o código de alguns representativos.

## Como executar

Rode qualquer exemplo passando o caminho do arquivo para o JAR do interpretador:

```bash
java -jar neoobjectpascal.jar examples/01-OlaMundo.npas
```

Arquivos de teste usam a extensão `.test.npas` e rodam em modo de teste:

```bash
# um arquivo de teste
java -jar neoobjectpascal.jar -t examples/34-TesteSimples.test.npas

# todos os testes de um diretório (recursivo, com cobertura)
java -jar neoobjectpascal.jar --test-all examples/
```

::: tip
O nome real do JAR depende da sua build (por exemplo, `target/neoobjectpascal-*.jar`). Consulte a [Introdução](../getting-started/introduction) para gerar o interpretador.
:::

## Fundamentos

| Arquivo | Demonstra |
| --- | --- |
| `01-OlaMundo.npas` | O programa mínimo: um `WriteLn` no bloco principal |
| `02-Variaveis.npas` | Declaração de variáveis (`String`, `Integer`) e atribuição |
| `15-ConcatenacaoStrings.npas` | Concatenação de strings com `+` e leitura de entrada |
| `16-ExemploOriginal.npas` | Exemplo introdutório combinando vários recursos |

```npas
begin
  WriteLn("Olá, Mundo!");
end.
```

<Output>
Olá, Mundo!
</Output>

## Controle e funções

| Arquivo | Demonstra |
| --- | --- |
| `03-EstruturasDeControle.npas` | `if ... then` e laço `for i := 1 to N do` |
| `04-Funcoes.npas` | Declaração e chamada de função com `return` |
| `07-ProgramacaoFuncional.npas` | Operador pipe `\|>` encadeado |

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

O operador pipe passa o valor da esquerda como primeiro argumento da função à direita:

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

## Dados e E/S

| Arquivo | Demonstra |
| --- | --- |
| `05-JSON.npas` | `JSON.parse(...) into` para carregar um objeto |
| `06-CSV.npas` | `CSV.parse(...) into` para carregar linhas |
| `08-EntradaSaida.npas` | `WriteLn` e `showMenu` |
| `13-MenuInterativo.npas` | Menu numerado com `showMenu` |
| `14-EntradaUsuario.npas` | Entrada do usuário com `ReadLn` |

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

## Módulos e bibliotecas internas

| Arquivo | Demonstra |
| --- | --- |
| `09-Modulos.npas` | `uses` de um módulo `.npas` local |
| `10-ModulosCompleto.npas` | Uso de múltiplos módulos |
| `11-ProjetoHierarquico.npas` | Módulos em pastas hierárquicas (`lib.core.calculadora`) |
| `17-BibliotecasInternas.npas` | Funções de `internal.math`, `internal.string`, `internal.datetime` |
| `26-TesteDatetime.npas` | Biblioteca `internal.datetime` |
| `28-TesteFile.npas` / `29-TesteFileSimples.npas` | Biblioteca `internal.file` |

```npas
uses internal.math, internal.string;

begin
  WriteLn("Fatorial de 5: ", factorial(5));
  WriteLn("Fibonacci(8): ", fibonacci(8));
  WriteLn("Maiúsculas: ", toUpperCase("neoobjectpascal"));
end.
```

Detalhes de todas as funções em [Bibliotecas internas](../features/internal-libraries).

## Java e sistemas híbridos

| Arquivo | Demonstra |
| --- | --- |
| `20-SistemaHibrido.npas` | Combinação de código Pascal e Java embutido |
| `21-TesteHibridoSimples.npas` | Versão simplificada do sistema híbrido |
| `22-TesteSemJava.npas` | Equivalente sem Java para comparação |
| `23-DebugJava.npas` | Depuração de blocos Java |
| `24-FuncaoJava.npas` | Função que retorna um valor de um bloco Java |
| `25-JavaComParametros.npas` | Bloco Java recebendo parâmetros (`param0`, `param1`, ...) |
| `27-SistemaHibridoFinal.npas` | Sistema híbrido completo |

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

Mais em [Integração com Java](../features/java-integration).

## Orientação a objetos

| Arquivo | Demonstra |
| --- | --- |
| `30-ClasseSimples.npas` | Classe com campos, `constructor Create` e método |
| `31-Heranca.npas` | Herança com `extends`, `virtual`/`override` |
| `32-Interface.npas` | `interface` e `implements` |
| `33-Polimorfismo.npas` | Polimorfismo com métodos sobrescritos |

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

Guia completo em [Classes](../oop/classes), [Herança](../oop/inheritance-polymorphism) e [Interfaces](../oop/interfaces).

## Testes e mocking

| Arquivo | Demonstra |
| --- | --- |
| `18-TesteSimples.npas` / `19-TesteCompleto.npas` | Blocos `test` introdutórios |
| `34-TesteSimples.test.npas` | Padrão GIVEN-WHEN-THEN com `expect(...).toBe(...)` |
| `35-TesteHeranca.test.npas` | Testes de herança |
| `36-TesteInterface.test.npas` | Testes de interface |
| `37-TestePolimorfismo.test.npas` | Testes de polimorfismo |
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

Substitua uma dependência por um mock e depois verifique que ela foi chamada:

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

Guia completo em [Testes unitários](../testing/unit-testing).

## Debug e nuvem

| Arquivo | Demonstra |
| --- | --- |
| `39-DebugSimples.npas` | Programa simples para o depurador interativo (`-d`) |
| `40-CloudExample.npas` | Exemplo para executar no NeoObjectPascal Cloud |
| `debug-boolean.npas`, `debug-class.npas`, `debug-class2.npas` | Cenários auxiliares de depuração |

## Novos recursos

Exemplos que exercitam recursos recentes da linguagem:

| Arquivo | Demonstra |
| --- | --- |
| `41-OperadoresBooleanos.npas` | Operadores `and`, `or`, `not` em condições |
| `42-AritmenticaReal.npas` | Aritmética com `Real` e auto-promoção de `Integer` |
| `43-Arrays.npas` | Arrays literais, índice base 0, atribuição de elemento e `for ... in` |
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

Tratamento de erros com `try`/`catch`/`finally` e `raise`:

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

## Continue explorando

Novo por aqui? Comece pela [Introdução](../getting-started/introduction) para instalar o interpretador e rodar seu primeiro programa. Para a sintaxe completa, veja a [Referência da linguagem](./language-reference).
