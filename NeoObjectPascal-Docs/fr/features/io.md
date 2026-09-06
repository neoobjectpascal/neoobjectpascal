# Entrée et sortie

NeoObjectPascal propose des procédures simples pour interagir avec la console : `WriteLn` pour afficher des informations, `ReadLn` pour lire des données de l'utilisateur et `showMenu` pour composer des menus numérotés.

## Écrire avec `WriteLn`

La procédure `WriteLn` écrit une ligne de texte dans la console et ajoute un saut de ligne à la fin :

```npas
begin
    WriteLn("Olá, Mundo!");
end.
```

<Output>
Olá, Mundo!
</Output>

### Plusieurs arguments

`WriteLn` accepte plusieurs arguments et les **concatène sans séparateur** dans la sortie. C'est utile pour composer des messages avec des valeurs dynamiques :

```npas
var nome: String;
var idade: Integer;

begin
    nome := "Carol";
    idade := 28;
    WriteLn("Olá, ", nome, "! Você tem ", idade, " anos.");
end.
```

<Output>
Olá, Carol! Você tem 28 anos.
</Output>

::: tip Ligne vide
Appeler `WriteLn()` sans argument affiche seulement un saut de ligne — pratique pour séparer des blocs de sortie.
:::

## Lire avec `ReadLn`

La procédure `ReadLn` lit une ligne saisie par l'utilisateur et la stocke dans une variable. La valeur est convertie vers le type de la variable de destination :

```npas
var nome: String;

begin
    WriteLn("Qual é o seu nome?");
    ReadLn(nome);
    WriteLn("Prazer, ", nome, "!");
end.
```

<Output>
Qual é o seu nome?
Prazer, Ada!
</Output>

Lorsque la variable de destination est `Integer` ou `Real`, le texte lu est automatiquement converti vers le type numérique correspondant :

```npas
var idade: Integer;

begin
    WriteLn("Digite sua idade:");
    ReadLn(idade);

    if idade >= 18 then
        WriteLn("Maior de idade.")
    else
        WriteLn("Menor de idade.");
end.
```

<Output>
Digite sua idade:
Maior de idade.
</Output>

::: warning Conversion de types
Lors de la lecture vers une variable numérique, assurez-vous que l'entrée est bien un nombre. Des entrées inattendues peuvent provoquer des erreurs de conversion — traitez ces cas avec `try/catch` lorsque vous lisez des données non fiables. Voir [Gestion des erreurs](../language/error-handling).
:::

## Menus avec `showMenu`

La procédure `showMenu` reçoit une liste d'options et affiche automatiquement un menu numéroté :

```npas
begin
    showMenu("Café", "Chá", "Suco");
end.
```

<Output>
1. Café
2. Chá
3. Suco
</Output>

Combinez `showMenu` avec `ReadLn` pour composer une interface de choix :

```npas
var opcao: Integer;

begin
    showMenu("Novo pedido", "Consultar", "Sair");
    WriteLn("Escolha uma opção:");
    ReadLn(opcao);

    if opcao = 1 then
        WriteLn("Iniciando novo pedido...")
    else if opcao = 2 then
        WriteLn("Consultando...")
    else
        WriteLn("Até logo!");
end.
```

<Output>
1. Novo pedido
2. Consultar
3. Sair
Escolha uma opção:
Iniciando novo pedido...
</Output>

## Bonnes pratiques

- Utilisez `WriteLn` avec plusieurs arguments plutôt que de tout concaténer avec `+` : c'est plus lisible et cela évite les conversions manuelles.
- Affichez toujours un message avant un `ReadLn`, pour que l'utilisateur sache quoi saisir.
- Lors de la lecture de nombres, pensez à valider ou à traiter les erreurs de conversion.

---

Ensuite, découvrez comment accéder à la puissance de la JVM dans [Intégration avec Java](./java-integration).
