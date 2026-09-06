# Classes et objets

L'**orientation objet** est l'un des piliers de NeoObjectPascal. Une **classe** est un moule qui regroupe des données (champs) et un comportement (méthodes) ; un **objet** est une instance concrète de ce moule, créée avec `new`.

## Déclarer une classe

Une classe commence par le mot-clé `class`, suivi du nom, de ses membres et de la clôture `end;`. Chaque membre (champ, constructeur, méthode) se termine par `;`.

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

::: info Convention de nommage
Les noms de classes utilisent le **PascalCase** (`Pessoa`, `ContaBancaria`). C'est la même convention que celle utilisée pour les interfaces.
:::

## Champs

Les champs stockent l'**état** de chaque objet. Ils sont déclarés avec `var nom: Type;`, exactement comme une variable ordinaire, mais à l'intérieur du corps de la classe :

```npas
class ContaBancaria
    var titular: String;
    var saldo: Real;
end;
```

::: warning Les champs sont privés par défaut
Tout champ est **privé** par défaut — l'intention est qu'il ne soit accessible que par les méthodes de la classe elle-même. Exposez l'état à travers des méthodes publiques plutôt que de laisser le champ directement accessible.
:::

## Le constructeur `Create`

Le **constructeur** initialise un nouvel objet. Par convention, il s'appelle `Create`, est public et reçoit les valeurs initiales des champs :

```npas
constructor Create(saldoInicial: Real)
begin
    self.saldo := saldoInicial;
end;
```

## `self` — l'instance courante

À l'intérieur de toute méthode ou du constructeur, `self` fait référence à l'**objet courant**. Utilisez `self.champ` pour lire ou affecter un champ et le distinguer d'un paramètre de même nom :

```npas
constructor Create(nome: String)
begin
    self.nome := nome;   // self.nome é o campo; nome é o parâmetro
end;
```

## Méthodes : fonctions et procédures

Le comportement de la classe est défini par des **fonctions** (qui renvoient une valeur avec `return`) et des **procédures** (qui ne renvoient rien). Les deux peuvent recevoir des paramètres séparés par une virgule :

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

## Visibilité : `public`, `private`, `protected`

Les modificateurs de visibilité documentent l'**intention** d'accès de chaque membre :

- `public` — fait partie de l'interface publique ; peut être appelé depuis l'extérieur de l'objet.
- `private` — détail interne, pensé pour un usage uniquement à l'intérieur de la classe.
- `protected` — interne, mais partagé avec les sous-classes.

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

::: info La visibilité comme intention
Les modificateurs sont reconnus par le langage et communiquent le contrat de la classe. Traitez-les comme une documentation de conception : les champs et méthodes privés décrivent des détails internes dont les autres parties du code ne devraient pas dépendre.
:::

## Instancier avec `new`

Un objet est créé avec `new NomDeLaClasse(arguments)`, en passant les valeurs attendues par le constructeur. Les variables qui contiennent des objets sont déclarées avec le type de la classe ou avec le type générique `Object` :

```npas
var conta: ContaBancaria;

begin
    conta := new ContaBancaria(100.0);
end.
```

## Accéder aux champs et aux méthodes

Utilisez le point pour appeler des méthodes et (lorsque cela est permis) lire les champs d'un objet : `obj.metodo(args)` et `obj.campo`.

```npas
conta.depositar(50.0);
WriteLn(conta.saldoAtual());
```

## Exemple complet : compte bancaire

L'exemple ci-dessous réunit des champs privés, un constructeur, des méthodes publiques, `self` et l'instanciation :

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

::: tip Les objets en tant qu'`Object`
Vous pouvez également déclarer la variable comme `var conta: Object;`. Le type `Object` est générique et accepte n'importe quelle instance — utile lorsque le même code doit travailler avec des objets de classes différentes.
:::

Une fois une classe en place, l'étape suivante consiste à réutiliser et à spécialiser le comportement. Continuez avec [Héritage et polymorphisme](./inheritance-polymorphism).
