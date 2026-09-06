# Recursos de Orientação a Objetos no NeoObjectPascal

## Visão Geral

O NeoObjectPascal agora suporta programação orientada a objetos completa, incluindo:

- **Classes** com campos e métodos
- **Interfaces** para contratos de implementação
- **Herança** entre classes
- **Polimorfismo** com métodos virtuais e override
- **Construtores** para inicialização de objetos
- **Encapsulamento** com modificadores de acesso (public, private, protected)

## Sintaxe de Classes

### Declaração Básica

```pascal
class NomeDaClasse
    var campo1: Tipo;
    var campo2: Tipo;
    
    constructor Create(param: Tipo)
    begin
        self.campo1 := param;
    end;
    
    public function metodo(): TipoRetorno
    begin
        return valor;
    end;
end;
```

### Criando Objetos

```pascal
var obj: NomeDaClasse;
begin
    obj := new NomeDaClasse(parametro);
    WriteLn(obj.metodo());
end.
```

## Herança

### Sintaxe

```pascal
class ClasseFilha extends ClassePai
    var novoCampo: Tipo;
    
    public override function metodo(): TipoRetorno
    begin
        return novoValor;
    end;
end;
```

### Exemplo Completo

```pascal
class Animal
    var nome: String;
    
    public virtual function emitirSom(): String
    begin
        return "Som genérico";
    end;
end;

class Cachorro extends Animal
    public override function emitirSom(): String
    begin
        return "Au au!";
    end;
end;
```

## Interfaces

### Declaração

```pascal
interface NomeDaInterface
    function metodo1(param: Tipo): TipoRetorno;
    function metodo2(): TipoRetorno;
end;
```

### Implementação

```pascal
class MinhaClasse implements NomeDaInterface
    public function metodo1(param: Tipo): TipoRetorno
    begin
        return valor;
    end;
    
    public function metodo2(): TipoRetorno
    begin
        return valor;
    end;
end;
```

## Polimorfismo

### Métodos Virtuais

Use `virtual` na classe base e `override` nas classes derivadas:

```pascal
class Base
    public virtual function calcular(): Integer
    begin
        return 0;
    end;
end;

class Derivada extends Base
    public override function calcular(): Integer
    begin
        return 42;
    end;
end;
```

## Modificadores de Acesso

- **public**: Acessível de qualquer lugar (padrão)
- **private**: Acessível apenas dentro da classe
- **protected**: Acessível na classe e suas derivadas

```pascal
class Exemplo
    var campoPublico: Integer;
    var campoPrivado: String;
    var campoProtegido: Boolean;
    
    public function metodoPublico(): String
    begin
        return "Público";
    end;
    
    private function metodoPrivado(): Integer
    begin
        return 42;
    end;
end;
```

## Construtores

### Sintaxe

```pascal
constructor Create(param1: Tipo1, param2: Tipo2)
begin
    self.campo1 := param1;
    self.campo2 := param2;
end;
```

### Uso

```pascal
var obj: MinhaClasse;
begin
    obj := new MinhaClasse(valor1, valor2);
end.
```

## Palavra-chave `self`

Use `self` para referenciar a instância atual dentro de métodos:

```pascal
public function obterNome(): String
begin
    return self.nome;
end;

public procedure definirNome(novoNome: String)
begin
    self.nome := novoNome;
end;
```

## Procedures

Procedures são métodos que não retornam valor:

```pascal
public procedure executar()
begin
    WriteLn("Executando...");
    self.campo := valor;
end;
```

## Exemplos Práticos

### Exemplo 1: Classe Simples

```pascal
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
        return "Olá, meu nome é " + self.nome;
    end;
end;
```

### Exemplo 2: Herança e Polimorfismo

```pascal
class Forma
    public virtual function calcularArea(): Integer
    begin
        return 0;
    end;
end;

class Retangulo extends Forma
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
```

### Exemplo 3: Interface

```pascal
interface Calculavel
    function calcular(a: Integer, b: Integer): Integer;
end;

class Soma implements Calculavel
    public function calcular(a: Integer, b: Integer): Integer
    begin
        return a + b;
    end;
end;

class Multiplicacao implements Calculavel
    public function calcular(a: Integer, b: Integer): Integer
    begin
        return a * b;
    end;
end;
```

## Limitações Atuais

1. Não há suporte a múltipla herança (apenas uma classe pai)
2. Uma classe pode implementar múltiplas interfaces
3. Não há suporte a propriedades (getters/setters automáticos)
4. Não há suporte a métodos estáticos
5. Não há suporte a classes abstratas (use interfaces)

## Próximos Passos

Consulte a documentação de **Testes Unitários** para aprender como testar suas classes e métodos.
