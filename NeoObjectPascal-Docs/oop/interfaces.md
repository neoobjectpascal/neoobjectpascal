# Interfaces

Uma **interface** é um contrato: ela declara *quais* métodos uma classe deve oferecer, sem dizer *como* eles funcionam. Classes que implementam a interface se comprometem a fornecer cada um desses métodos. Interfaces permitem escrever código que depende de um comportamento, não de uma implementação específica.

## Declarando uma interface

Use a palavra-chave `interface`, seguida do nome e apenas das **assinaturas** dos métodos — sem corpo. Cada assinatura termina com `;`, e o bloco encerra com `end;`:

```npas
interface Calculavel
    function calcular(a: Integer, b: Integer): Integer;
end;
```

::: info Só assinaturas
Uma interface não tem campos nem corpo de método. Ela descreve *o que* deve existir; a classe que a implementa decide *como*.
:::

## Implementando uma interface

Uma classe declara que cumpre um contrato com `implements`. Ela precisa então fornecer uma implementação para cada método declarado na interface:

```npas
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

Tanto `Soma` quanto `Multiplicacao` respeitam o contrato `Calculavel`, cada uma à sua maneira:

```npas
var soma: Soma;
var mult: Multiplicacao;
var resultado: Integer;

begin
    soma := new Soma();
    resultado := soma.calcular(10, 5);
    WriteLn("Soma: 10 + 5 = ", resultado);

    mult := new Multiplicacao();
    resultado := mult.calcular(10, 5);
    WriteLn("Multiplicação: 10 * 5 = ", resultado);
end.
```

<Output>
Soma: 10 + 5 = 15
Multiplicação: 10 * 5 = 50
</Output>

## Múltiplas interfaces

Uma classe pode implementar **mais de uma interface** ao mesmo tempo, separando os nomes por vírgula. Nesse caso ela precisa fornecer todos os métodos de todas as interfaces:

```npas
interface Calculavel
    function calcular(a: Integer, b: Integer): Integer;
end;

interface Descritivel
    function descrever(): String;
end;

class Somador implements Calculavel, Descritivel
    public function calcular(a: Integer, b: Integer): Integer
    begin
        return a + b;
    end;

    public function descrever(): String
    begin
        return "Operação de soma de dois inteiros";
    end;
end;

var s: Somador;

begin
    s := new Somador();
    WriteLn(s.descrever());
    WriteLn("Resultado: ", s.calcular(3, 4));
end.
```

<Output>
Operação de soma de dois inteiros
Resultado: 7
</Output>

## Programando para uma interface

O grande benefício das interfaces é permitir que o código dependa do **contrato**, e não de uma classe concreta. Como `Soma` e `Multiplicacao` cumprem `Calculavel`, uma mesma variável genérica pode apontar para qualquer uma delas e o método correto é chamado em tempo de execução:

```npas
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

var operacao: Object;

begin
    operacao := new Soma();
    WriteLn("Soma: ", operacao.calcular(6, 2));

    operacao := new Multiplicacao();
    WriteLn("Produto: ", operacao.calcular(6, 2));
end.
```

<Output>
Soma: 8
Produto: 12
</Output>

::: tip Interface vs. herança
Use **herança** (`extends`) quando as classes compartilham uma relação "é um tipo de" e reaproveitam implementação. Use **interfaces** (`implements`) quando classes sem parentesco precisam apenas garantir o mesmo conjunto de operações. Uma classe pode estender apenas uma superclasse, mas implementar várias interfaces.
:::

Com classes, herança, polimorfismo e interfaces, você domina a orientação a objetos do NeoObjectPascal. O próximo tema traz um estilo complementar de escrever código. Siga para [Programação funcional](../features/functional).
