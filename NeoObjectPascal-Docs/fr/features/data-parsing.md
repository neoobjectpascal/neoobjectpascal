# Manipulation de données (JSON et CSV)

NeoObjectPascal offre une prise en charge native de deux des formats de données les plus courants : **JSON** et **CSV**. Tous deux utilisent la même syntaxe déclarative `parse(...) into variavel;`, qui analyse une chaîne et stocke le résultat dans une variable de type `Object`.

## Analyser du JSON

Utilisez `JSON.parse(textoJson) into variavel;` pour convertir une chaîne JSON en objet. Accédez ensuite aux champs avec la notation par point :

```npas
var dados: Object;
var jsonString: String;

begin
    jsonString := '{"nome": "Carmen", "idade": 30}';
    JSON.parse(jsonString) into dados;

    WriteLn("Nome: ", dados.nome);
    WriteLn("Idade: ", dados.idade);
end.
```

<Output>
Nome: Carmen
Idade: 30
</Output>

::: tip Les guillemets simples facilitent les choses
Comme le JSON utilise des guillemets doubles en interne, écrire la chaîne avec des **guillemets simples** (`'...'`) évite les conflits. Les deux formes de guillemets sont équivalentes pour les littéraux de chaîne en NeoObjectPascal.
:::

### Objets imbriqués

L'accès par point fonctionne à n'importe quel niveau de profondeur de la structure :

```npas
var config: Object;

begin
    config := '{"servidor": {"host": "localhost", "porta": 8080}}';
    JSON.parse(config) into config;

    WriteLn("Host: ", config.servidor.host);
    WriteLn("Porta: ", config.servidor.porta);
end.
```

<Output>
Host: localhost
Porta: 8080
</Output>

## Analyser du CSV

Utilisez `CSV.parse(textoCsv) into variavel;` pour convertir une chaîne CSV en une liste de lignes, où chaque ligne est une liste de colonnes. Le résultat est un `Object` que vous pouvez parcourir :

```npas
var dados: Object;
var csvString: String;

begin
    csvString := "nome,idade
Carina,30
Bruno,25";
    CSV.parse(csvString) into dados;

    // dados[0] é a primeira linha; dados[0][0] é a primeira coluna
    WriteLn("Primeira pessoa: ", dados[0][0]);
    WriteLn("Idade dela: ", dados[0][1]);
end.
```

<Output>
Primeira pessoa: Carina
Idade dela: 30
</Output>

### Parcourir les lignes

Comme le résultat de `CSV.parse` est itérable, vous pouvez utiliser `for..in` pour traiter chaque ligne :

```npas
var linhas: Object;
var linha: Object;

begin
    linhas := "produto,preco
Cafe,12
Cha,8";
    CSV.parse(linhas) into linhas;

    for linha in linhas do
        WriteLn(linha[0], " custa ", linha[1]);
end.
```

<Output>
produto custa preco
Cafe custa 12
Cha custa 8
</Output>

::: warning L'en-tête est aussi une ligne
`CSV.parse` ne distingue pas l'en-tête des lignes de données : la première ligne (`produto,preco`) est renvoyée comme n'importe quelle autre. Si votre CSV comporte un en-tête, ignorez l'index `0` lors du traitement des données.
:::

## Combiner avec le pipe et Java

Les données analysées sont des valeurs ordinaires et peuvent alimenter un pipeline fonctionnel ou un bloc Java. Par exemple, transformer un champ juste après l'analyse :

```npas
function emMaiusculas(texto: String): String
begin
    return java:(texto) {
        return ((String)param0).toUpperCase();
    };
end;

var pedido: Object;

begin
    pedido := '{"cliente": "ana silva"}';
    JSON.parse(pedido) into pedido;

    WriteLn(pedido.cliente |> emMaiusculas);
end.
```

<Output>
ANA SILVA
</Output>

## Bonnes pratiques

- Validez l'origine des données : les chaînes provenant de fichiers ou du réseau peuvent être malformées. Enveloppez l'analyse dans un `try/catch` lorsque l'entrée n'est pas fiable — voir [Gestion des erreurs](../language/error-handling).
- Déclarez la variable de destination en `Object`, car JSON et CSV produisent des structures dynamiques.
- Pour un CSV avec en-tête, traitez la ligne `0` séparément des autres.

---

Ensuite, découvrez comment interagir avec la console dans [Entrée et sortie](./io).
