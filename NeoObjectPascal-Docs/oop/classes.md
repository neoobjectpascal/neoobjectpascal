# Classes e objetos

A **orientação a objetos** é um dos pilares do NeoObjectPascal. Uma **classe** é um molde que agrupa dados (campos) e comportamento (métodos); um **objeto** é uma instância concreta desse molde, criada com `new`.

## Declarando uma classe

Uma classe começa com a palavra-chave `class`, seguida do nome, dos seus membros e do encerramento `end;`. Cada membro (campo, construtor, método) termina com `;`.

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
```

::: info Convenção de nomes
Nomes de classes usam **PascalCase** (`Pessoa`, `ContaBancaria`). É a mesma convenção usada em interfaces.
:::

## Campos

Os campos guardam o **estado** de cada objeto. Eles são declarados com `var nome: Tipo;`, exatamente como uma variável comum, mas dentro do corpo da classe:

```npas
class ContaBancaria
    var titular: String;
    var saldo: Real;
end;
```

::: warning Campos são privados por padrão
Todo campo é **privado** por padrão — a intenção é que ele seja acessado apenas pelos métodos da própria classe. Exponha o estado através de métodos públicos em vez de deixar o campo acessível diretamente.
:::

## O construtor `Create`

O **construtor** inicializa um novo objeto. Por convenção ele se chama `Create`, é público e recebe os valores iniciais dos campos:

```npas
constructor Create(saldoInicial: Real)
begin
    self.saldo := saldoInicial;
end;
```

## `self` — a instância atual

Dentro de qualquer método ou do construtor, `self` refere-se ao **objeto atual**. Use `self.campo` para ler ou atribuir um campo e distingui-lo de um parâmetro de mesmo nome:

```npas
constructor Create(nome: String)
begin
    self.nome := nome;   // self.nome é o campo; nome é o parâmetro
end;
```

## Métodos: funções e procedimentos

O comportamento da classe é definido por **funções** (retornam um valor com `return`) e **procedimentos** (não retornam nada). Ambos podem receber parâmetros separados por vírgula:

```npas
public function saldoAtual(): Real
begin
    return self.saldo;
end;

public procedure depositar(valor: Real)
begin
    self.saldo := self.saldo + valor;
end;
```

## Visibilidade: `public`, `private`, `protected`

Os modificadores de visibilidade documentam a **intenção** de acesso de cada membro:

- `public` — parte da interface pública; pode ser chamado de fora do objeto.
- `private` — detalhe interno, pensado para uso apenas dentro da classe.
- `protected` — interno, mas compartilhado com subclasses.

```npas
public function saldoAtual(): Real
begin
    return self.saldo;
end;

private procedure registrarLog(mensagem: String)
begin
    WriteLn("[LOG] " + mensagem);
end;
```

::: info Visibilidade como intenção
Os modificadores são reconhecidos pela linguagem e comunicam o contrato da classe. Trate-os como documentação de design: campos e métodos privados descrevem detalhes internos que outras partes do código não deveriam depender.
:::

## Instanciando com `new`

Um objeto é criado com `new NomeDaClasse(argumentos)`, passando os valores esperados pelo construtor. Variáveis que guardam objetos são declaradas com o tipo da classe ou com o tipo genérico `Object`:

```npas
var conta: ContaBancaria;

begin
    conta := new ContaBancaria(100.0);
end.
```

## Acessando campos e métodos

Use o ponto para chamar métodos e (quando permitido) ler campos de um objeto: `obj.metodo(args)` e `obj.campo`.

```npas
conta.depositar(50.0);
WriteLn(conta.saldoAtual());
```

## Exemplo completo: conta bancária

O exemplo abaixo reúne campos privados, construtor, métodos públicos, `self` e instanciação:

```npas
class ContaBancaria
    var titular: String;
    var saldo: Real;

    constructor Create(titular: String, saldoInicial: Real)
    begin
        self.titular := titular;
        self.saldo := saldoInicial;
    end;

    public procedure depositar(valor: Real)
    begin
        self.saldo := self.saldo + valor;
    end;

    public function sacar(valor: Real): Boolean
    begin
        if valor > self.saldo then
            return false;
        self.saldo := self.saldo - valor;
        return true;
    end;

    public function extrato(): String
    begin
        return "Titular: " + self.titular + " | Saldo: " + self.saldo;
    end;
end;

var conta: ContaBancaria;

begin
    conta := new ContaBancaria("Ana", 100.0);
    conta.depositar(50.0);

    if conta.sacar(30.0) then
        WriteLn("Saque realizado.")
    else
        WriteLn("Saldo insuficiente.");

    WriteLn(conta.extrato());
end.
```

<Output>
Saque realizado.
Titular: Ana | Saldo: 120.0
</Output>

::: tip Objetos como `Object`
Você também pode declarar a variável como `var conta: Object;`. O tipo `Object` é genérico e aceita qualquer instância — útil quando o mesmo código precisa trabalhar com objetos de classes diferentes.
:::

Com uma classe no lugar, o próximo passo é reutilizar e especializar comportamento. Siga para [Herança e polimorfismo](./inheritance-polymorphism).
