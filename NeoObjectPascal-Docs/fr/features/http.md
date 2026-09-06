# HTTP / appels d’API

NeoObjectPascal parle HTTP nativement. Activez la fonctionnalité avec `uses http;` et vous disposez d’un ensemble de fonctions pour consommer des API REST — sans aucune dépendance externe. En coulisses, tout est assuré par le `HttpClient` de Java 11, déjà fourni avec la JVM.

Les en-têtes, les corps et les options sont décrits avec des **records** (`#{ ... }`), et chaque réponse arrive elle aussi sous forme de record que vous inspectez avec la notation par point.

## Votre premier GET

`httpGet(url)` effectue une requête `GET` et renvoie un record de réponse. Les champs les plus utilisés sont `resp.ok` (vrai pour un statut 200–299), `resp.status` (le code numérique) et `resp.json` (le corps déjà analysé, lorsque la réponse est du JSON) :

```npas
uses http;

var resp: Object;

begin
    resp := httpGet("https://api.exemplo.com/usuarios/7");

    if resp.ok then
    begin
        WriteLn("Status: ", resp.status);
        WriteLn("Nome: ", resp.json.nome);
    end
    else
        WriteLn("Requisição falhou com status ", resp.status);
end.
```

<Output>
Status: 200
Nome: Marina
</Output>

Pour envoyer des en-têtes avec un `GET`, passez un record comme second argument :

```npas
uses http;

var resp: Object;

begin
    resp := httpGet(
        "https://api.exemplo.com/perfil",
        #{ Accept: "application/json" }
    );

    WriteLn(resp.json.email);
end.
```

## Envoyer des données : POST, PUT, PATCH

`httpPost(url, body, headers)` envoie un corps au serveur. Les fonctions `httpPut` et `httpPatch` ont exactement la même forme. Lorsque le corps est un record (ou un tableau), il est **sérialisé en JSON automatiquement** et la requête reçoit l’en-tête `Content-Type: application/json` :

```npas
uses http;

var resp: Object;
var novo: Object;

begin
    novo := #{ nome: "Bruno", idade: 28 };

    resp := httpPost(
        "https://api.exemplo.com/usuarios",
        novo,
        httpBearer("meu-token-secreto")
    );

    WriteLn("Criado com status ", resp.status);
    WriteLn("Novo id: ", resp.json.id);
end.
```

<Output>
Criado com status 201
Novo id: 42
</Output>

### Authentification

Deux fonctions utilitaires construisent l’en-tête `Authorization` pour vous :

- `httpBearer(token)` renvoie `#{ Authorization: "Bearer <token>" }`, idéal pour les API à jeton.
- `httpBasic(usuario, senha)` renvoie `#{ Authorization: "Basic <base64>" }`, pour l’authentification basique.

Comme le résultat est un record d’en-têtes, vous le passez directement là où une API attend des en-têtes :

```npas
resp := httpGet("https://api.exemplo.com/privado", httpBasic("ana", "s3nha"));
```

## Formulaires avec `httpForm`

Toutes les API n’acceptent pas le JSON. Pour envoyer des données au format `application/x-www-form-urlencoded`, enveloppez le record avec `httpForm(#{ ... })`. Le corps obtenu est encodé comme un formulaire HTML classique :

```npas
uses http;

var resp: Object;

begin
    resp := httpPost(
        "https://api.exemplo.com/login",
        httpForm(#{ usuario: "ana", senha: "s3nha" })
    );

    WriteLn("Login: ", resp.status);
end.
```

<Output>
Login: 200
</Output>

::: tip Trois formes de corps
Un **record ou un tableau** devient du JSON automatiquement ; `httpForm(#{...})` envoie `x-www-form-urlencoded` ; et une **chaîne** est envoyée telle quelle, sans transformation. Choisissez selon ce qu’attend l’API.
:::

## Contrôle total avec `httpRequest`

Lorsque vous avez besoin de plus de maîtrise sur la requête, `httpRequest` accepte un unique record avec toutes les options : `method`, `url`, `headers`, `body`, `form`, `query`, `auth` et `timeout`.

Les paramètres de `query` sont ajoutés à l’URL déjà encodés. Le `timeout` est indiqué en secondes (la valeur par défaut est 30) :

```npas
uses http;

var resp: Object;

begin
    resp := httpRequest(#{
        method: "GET",
        url: "https://api.exemplo.com/busca",
        query: #{ page: 1, q: "termo" },
        auth: httpBearer("meu-token"),
        timeout: 10
    });

    WriteLn("URL final: ", resp.url);
    WriteLn("Total: ", resp.json.total);
end.
```

<Output>
URL final: https://api.exemplo.com/busca?page=1&q=termo
Total: 3
</Output>

Remarquez comment `query: #{ page: 1, q: "termo" }` a été transformé en `?page=1&q=termo`, avec les valeurs correctement échappées.

## Le record de réponse

Chaque fonction HTTP renvoie un record avec la même structure. Voici les champs disponibles :

| Champ | Type | Description |
| --- | --- | --- |
| `status` | `Integer` | Le code de statut HTTP (par exemple `200`, `404`, `500`). |
| `ok` | `Boolean` | `true` lorsque le statut est dans la plage 200–299. |
| `body` | `String` | Le corps brut de la réponse, sous forme de texte. |
| `json` | `Object` | Le corps déjà analysé, ou `nil` si la réponse n’est pas du JSON. |
| `headers` | `Object` | Les en-têtes de la réponse, sous forme de record. |
| `url` | `String` | L’URL finale de la requête, y compris les paramètres de `query`. |

## Gérer les erreurs à la manière de `fetch`

Le modèle d’erreurs suit la philosophie de `fetch` : une réponse HTTP **revient toujours normalement**, même avec un statut 404 ou 500 — dans ces cas, `resp.ok` vaut `false` et c’est vous qui décidez quoi faire. En revanche, les échecs de **transport** (pas de connexion, délai dépassé, DNS qui ne résout pas, URL invalide) **lèvent une erreur** que vous interceptez avec `try/catch` :

```npas
uses http;

var resp: Object;
var erro: String;

begin
    try
    begin
        resp := httpGet("https://api.exemplo.com/relatorio");

        if resp.ok then
            WriteLn("Conteúdo: ", resp.body)
        else
            WriteLn("Servidor respondeu: ", resp.status);
    end
    catch (erro)
    begin
        WriteLn("Falha de transporte: ", erro);
    end;
end.
```

::: warning Les 4xx/5xx ne lèvent pas d’erreur
N’attendez pas d’un `catch` qu’il traite un `404` ou un `500` : ces réponses arrivent avec `resp.ok` à `false` et doivent être vérifiées avec un `if`. Réservez `try/catch` aux échecs réseau — lorsque la requête n’aboutit même pas. Voir [Gestion des erreurs](../language/error-handling) pour la mécanique complète.
:::

## Accès chaîné à `json`

L’accès par point fonctionne sur les records, donc `resp.json.campo` lit un champ directement. En revanche, **les chaînes positionnelles comme `resp.json.itens[0]` ne sont pas prises en charge** : extrayez la valeur dans une variable intermédiaire avant de l’indexer :

```npas
uses http;

var resp: Object;
var itens: Object;

begin
    resp := httpGet("https://api.exemplo.com/pedidos");

    // resp.json.itens[0] não é suportado; use uma variável intermediária
    itens := resp.json.itens;
    WriteLn("Primeiro item: ", itens[0]);
end.
```

## Bonnes pratiques

- Vérifiez toujours `resp.ok` (ou `resp.status`) avant de faire confiance au corps de la réponse.
- Enveloppez les appels dans `try/catch` lorsque le réseau est incertain, mais souvenez-vous : les 4xx et 5xx ne tombent pas dans le `catch`.
- Utilisez `httpBearer` et `httpBasic` plutôt que de construire l’en-tête `Authorization` à la main.
- Pour accéder aux éléments indexés de `resp.json`, stockez la collection dans une variable avant d’utiliser `[ ]`.

---

Ensuite, découvrez comment interagir avec la console dans [Entrée et sortie](./io).
