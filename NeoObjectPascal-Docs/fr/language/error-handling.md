# Gestion des erreurs

Les erreurs arrivent : une division par zéro, un indice invalide, une validation qui échoue. NeoObjectPascal gère les défaillances avec `try/catch/finally` et permet de signaler des erreurs avec `raise`.

## La structure `try/catch/finally`

La forme générale utilise des blocs `begin ... end` dans chaque partie :

```npas
try
begin
    // código que pode falhar
end
catch (e)
begin
    // executa se um erro for lançado; e recebe o valor do erro
end
finally
begin
    // sempre executa (opcional)
end;
```

- Le bloc `try` contient le code surveillé.
- Le bloc `catch (e)` s'exécute **uniquement si** une erreur survient ; la variable `e` est liée à la valeur de l'erreur.
- Le bloc `finally` est **facultatif** et **s'exécute toujours**, avec ou sans erreur.

## Lever des erreurs avec `raise`

Utilisez `raise expr;` pour signaler une erreur. Généralement, l'expression est un message de type `String` :

```npas
function dividir(a: Integer, b: Integer): Real
begin
    if b = 0 then
        raise "Divisão por zero não permitida";
    return java:(a, b) {
        return ((Integer)param0).doubleValue() / ((Integer)param1).doubleValue();
    };
end;

var x: Real;
var erro: String;

begin
    try
    begin
        x := dividir(10, 2);
        WriteLn("10 / 2 = ", x);
    end
    catch (erro)
    begin
        WriteLn("Erro: ", erro);
    end;

    try
    begin
        x := dividir(5, 0);
        WriteLn("Nunca chegará aqui");
    end
    catch (erro)
    begin
        WriteLn("Capturado: ", erro);
    end;
end.
```

<Output>
10 / 2 = 5.0
Capturado: Divisão por zero não permitida
</Output>

Remarquez que le second appel lève l'erreur à l'intérieur de `dividir`, si bien que la ligne `"Nunca chegará aqui"` ne s'exécute jamais — le contrôle saute directement au `catch`.

## La variable du `catch`

Le nom entre parenthèses dans `catch (nom)` reçoit la valeur levée. Pour les erreurs que vous levez avec `raise`, il s'agit exactement de l'expression passée. Pour les **erreurs intégrées** (division par zéro, indice hors intervalle), il s'agit du message d'erreur sous forme de chaîne :

```npas
var numeros: Array;
var erro: String;

begin
    numeros := [1, 2, 3];
    try
    begin
        WriteLn(numeros[10]);
    end
    catch (erro)
    begin
        WriteLn("Falhou ao acessar: ", erro);
    end;
end.
```

::: info Les erreurs intégrées sont capturables
Les défaillances comme la **division par zéro** et l'**indice de tableau hors intervalle** génèrent des erreurs que vous pouvez capturer avec `try/catch` comme n'importe quelle autre.
:::

## Le bloc `finally`

Le `finally` s'exécute toujours — aussi bien sur le chemin de réussite que lorsqu'une erreur est capturée. C'est l'endroit idéal pour le nettoyage (fermer des ressources, enregistrer la fin de traitement) :

```npas
function validarIdade(idade: Integer): String
begin
    if idade < 0 then
        raise "Idade inválida: " + idade;
    if idade > 150 then
        raise "Idade improvável: " + idade;
    return "Idade válida: " + idade;
end;

var erro: String;

begin
    try
    begin
        WriteLn(validarIdade(25));
    end
    catch (erro)
    begin
        WriteLn("Erro de validação: ", erro);
    end
    finally
    begin
        WriteLn("Finally sempre executa");
    end;

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
        WriteLn("Cleanup concluído");
    end;
end.
```

<Output>
Idade válida: 25
Finally sempre executa
Capturado: Idade inválida: -5
Cleanup concluído
</Output>

Dans le premier `try`, il n'y a pas d'erreur : le `try` s'exécute puis le `finally`. Dans le second, `validarIdade` lève l'erreur, le `catch` la capture et le `finally` s'exécute ensuite.

::: tip Le `return` traverse le `finally`
Si vous effectuez un retour depuis l'intérieur d'un `try`, le bloc `finally` s'exécute quand même avant que la fonction ne retourne réellement. Utilisez cela pour garantir le nettoyage même lors des sorties anticipées.
:::

Une fois la gestion des erreurs maîtrisée, passez à l'orientation objet dans [Classes](../oop/classes).
