# Interfaces

Une **interface** est un contrat : elle déclare *quelles* méthodes une classe doit offrir, sans dire *comment* elles fonctionnent. Les classes qui implémentent l'interface s'engagent à fournir chacune de ces méthodes. Les interfaces permettent d'écrire du code qui dépend d'un comportement, et non d'une implémentation spécifique.

## Déclarer une interface

Utilisez le mot-clé `interface`, suivi du nom et uniquement des **signatures** des méthodes — sans corps. Chaque signature se termine par `;`, et le bloc se clôt avec `end;` :

```npas
interface Calculavel
    function calcular(a: Integer, b: Integer): Integer;
end;
```

::: info Uniquement des signatures
Une interface n'a ni champs ni corps de méthode. Elle décrit *ce qui* doit exister ; la classe qui l'implémente décide *comment*.
:::

## Implémenter une interface

Une classe déclare qu'elle remplit un contrat avec `implements`. Elle doit alors fournir une implémentation pour chaque méthode déclarée dans l'interface :

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

`Soma` comme `Multiplicacao` respectent le contrat `Calculavel`, chacune à sa manière :

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

## Interfaces multiples

Une classe peut implémenter **plus d'une interface** à la fois, en séparant les noms par une virgule. Dans ce cas, elle doit fournir toutes les méthodes de toutes les interfaces :

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

## Programmer pour une interface

Le grand avantage des interfaces est de permettre au code de dépendre du **contrat**, et non d'une classe concrète. Comme `Soma` et `Multiplicacao` remplissent `Calculavel`, une même variable générique peut pointer vers l'une ou l'autre et la méthode correcte est appelée à l'exécution :

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

::: tip Interface ou héritage
Utilisez l'**héritage** (`extends`) lorsque les classes partagent une relation « est un type de » et réutilisent une implémentation. Utilisez les **interfaces** (`implements`) lorsque des classes sans lien de parenté doivent seulement garantir le même ensemble d'opérations. Une classe ne peut étendre qu'une seule superclasse, mais implémenter plusieurs interfaces.
:::

Avec les classes, l'héritage, le polymorphisme et les interfaces, vous maîtrisez l'orientation objet de NeoObjectPascal. Le prochain thème présente un style complémentaire d'écriture du code. Continuez avec [Programmation fonctionnelle](../features/functional).
