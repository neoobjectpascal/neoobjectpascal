# Fonctions et procédures

Les fonctions encapsulent une logique réutilisable et **renvoient une valeur**. En tête de fichier, vous déclarez des fonctions avec le mot-clé `function`.

## Déclarer une fonction

La forme générale est `function nom(paramètres): Type begin ... return expr; end;`. Notez le `;` final après la déclaration au niveau supérieur :

```npas
function saudar(nome: String): String
begin
    return "Olá, " + nome + "!";
end;

begin
    WriteLn(saudar("Ana"));
end.
```

<Output>
Olá, Ana!
</Output>

Le type situé après les `:` est le type de la valeur renvoyée. L'instruction `return expr;` restitue cette valeur et termine la fonction.

## Paramètres

Les paramètres sont séparés par une **virgule**. Ils peuvent être **typés** ou **non typés** :

```npas
// parâmetros tipados
function somar(a: Integer, b: Integer): Integer
begin
    return a + b;
end;

// parâmetros sem tipo explícito
function juntar(x, y): String
begin
    return x + " " + y;
end;

begin
    WriteLn(somar(3, 4));
    WriteLn(juntar("bom", "dia"));
end.
```

<Output>
7
bom dia
</Output>

::: tip Typez vos paramètres
Déclarer le type des paramètres rend l'intention claire et aide l'interpréteur à valider les valeurs. Préférez des paramètres typés chaque fois que possible.
:::

## Appeler des fonctions

Appelez une fonction par son nom, en passant les arguments entre parenthèses. Si la fonction **n'a aucun argument**, les parenthèses sont facultatives :

```npas
function agora(): String
begin
    return "sempre agora";
end;

begin
    WriteLn(agora());   // com parênteses
    WriteLn(agora);     // sem parênteses — equivalente
end.
```

<Output>
sempre agora
sempre agora
</Output>

## Récursivité

Une fonction peut s'appeler elle-même. L'exemple classique est la factorielle :

```npas
function fatorial(n: Integer): Integer
begin
    if n <= 1 then
        return 1;
    return n * fatorial(n - 1);
end;

begin
    WriteLn("5! = ", fatorial(5));
    WriteLn("6! = ", fatorial(6));
end.
```

<Output>
5! = 120
6! = 720
</Output>

::: warning Prévoyez toujours un cas de base
Toute fonction récursive a besoin d'une condition d'arrêt (ici, `n <= 1`). Sans elle, la récursivité ne se termine jamais.
:::

## Et les procédures ?

Une **procédure** est comme une fonction, mais **ne renvoie pas de valeur** — elle sert à produire des effets (afficher, modifier l'état). En NeoObjectPascal, les procédures existent en tant que **membres de classes**, déclarées avec le mot-clé `procedure` :

```npas
class Logger
    public procedure registrar(msg: String)
    begin
        WriteLn("[LOG] " + msg);
    end;
end;

var log: Object;

begin
    log := new Logger();
    log.registrar("iniciando");
end.
```

<Output>
[LOG] iniciando
</Output>

Pour explorer les procédures, les méthodes et la visibilité en profondeur, voir [Classes](../oop/classes).

Ensuite, apprenez à travailler avec les collections dans [Tableaux](./arrays).
