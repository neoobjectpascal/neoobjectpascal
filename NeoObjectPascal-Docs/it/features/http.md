# HTTP / chiamate API

NeoObjectPascal parla HTTP in modo nativo. Abilita la funzionalità con `uses http;` e ottieni un insieme di funzioni per consumare API REST — senza alcuna dipendenza esterna. Sotto il cofano tutto è gestito dall’`HttpClient` di Java 11, che è già incluso nella JVM.

Intestazioni, corpi e opzioni si descrivono con i **record** (`#{ ... }`), e anche ogni risposta arriva come record che ispezioni con la notazione a punto.

## Il tuo primo GET

`httpGet(url)` esegue una richiesta `GET` e restituisce un record di risposta. I campi più usati sono `resp.ok` (vero per gli stati 200–299), `resp.status` (il codice numerico) e `resp.json` (il corpo già convertito, quando la risposta è JSON):

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

Per inviare intestazioni insieme a un `GET`, passa un record come secondo argomento:

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

## Inviare dati: POST, PUT, PATCH

`httpPost(url, body, headers)` invia un corpo al server. Le funzioni `httpPut` e `httpPatch` hanno esattamente la stessa forma. Quando il corpo è un record (o un array), viene **serializzato in JSON automaticamente** e la richiesta riceve l’intestazione `Content-Type: application/json`:

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

### Autenticazione

Due funzioni di supporto costruiscono l’intestazione `Authorization` per te:

- `httpBearer(token)` restituisce `#{ Authorization: "Bearer <token>" }`, ideale per le API a token.
- `httpBasic(usuario, senha)` restituisce `#{ Authorization: "Basic <base64>" }`, per l’autenticazione basic.

Poiché il risultato è un record di intestazioni, lo passi direttamente dove un’API si aspetta le intestazioni:

```npas
resp := httpGet("https://api.exemplo.com/privado", httpBasic("ana", "s3nha"));
```

## Moduli con `httpForm`

Non tutte le API accettano il JSON. Per inviare dati nel formato `application/x-www-form-urlencoded`, avvolgi il record con `httpForm(#{ ... })`. Il corpo risultante viene codificato come un classico modulo HTML:

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

::: tip Tre forme di corpo
Un **record o array** diventa JSON automaticamente; `httpForm(#{...})` invia `x-www-form-urlencoded`; e una **stringa** viene inviata esattamente così com’è, senza trasformazioni. Scegli in base a ciò che l’API si aspetta.
:::

## Controllo totale con `httpRequest`

Quando ti serve un maggiore controllo sulla richiesta, `httpRequest` accetta un singolo record con tutte le opzioni: `method`, `url`, `headers`, `body`, `form`, `query`, `auth` e `timeout`.

I parametri di `query` vengono aggiunti all’URL già codificati. Il `timeout` è espresso in secondi (il valore predefinito è 30):

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

Nota come `query: #{ page: 1, q: "termo" }` sia stato trasformato in `?page=1&q=termo`, con i valori opportunamente codificati.

## Il record di risposta

Ogni funzione HTTP restituisce un record con la stessa struttura. Questi sono i campi disponibili:

| Campo | Tipo | Descrizione |
| --- | --- | --- |
| `status` | `Integer` | Il codice di stato HTTP (per esempio `200`, `404`, `500`). |
| `ok` | `Boolean` | `true` quando lo stato è nell’intervallo 200–299. |
| `body` | `String` | Il corpo grezzo della risposta, come testo. |
| `json` | `Object` | Il corpo già convertito, oppure `nil` se la risposta non è JSON. |
| `headers` | `Object` | Le intestazioni della risposta, come record. |
| `url` | `String` | L’URL finale della richiesta, inclusi i parametri di `query`. |

## Gestire gli errori alla maniera di `fetch`

Il modello degli errori segue la filosofia di `fetch`: una risposta HTTP **torna sempre normalmente**, anche con stato 404 o 500 — in questi casi `resp.ok` è `false` e sei tu a decidere cosa fare. I guasti di **trasporto** invece (nessuna connessione, timeout, DNS che non risolve, URL non valido) **lanciano un errore** che intercetti con `try/catch`:

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

::: warning I 4xx/5xx non lanciano errori
Non aspettarti che un `catch` gestisca un `404` o un `500`: queste risposte arrivano con `resp.ok` a `false` e vanno controllate con un `if`. Riserva il `try/catch` ai guasti di rete — quando la richiesta non si completa nemmeno. Vedi [Gestione degli errori](../language/error-handling) per la meccanica completa.
:::

## Accesso concatenato a `json`

L’accesso a punto funziona sui record, quindi `resp.json.campo` legge un campo direttamente. Tuttavia, **le catene posizionali come `resp.json.itens[0]` non sono supportate**: estrai il valore in una variabile intermedia prima di indicizzarlo:

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

## Buone pratiche

- Controlla sempre `resp.ok` (o `resp.status`) prima di fidarti del corpo della risposta.
- Avvolgi le chiamate in `try/catch` quando la rete è incerta, ma ricorda: i 4xx e 5xx non finiscono nel `catch`.
- Usa `httpBearer` e `httpBasic` invece di costruire a mano l’intestazione `Authorization`.
- Per accedere agli elementi indicizzati di `resp.json`, memorizza la collezione in una variabile prima di usare `[ ]`.

---

Successivamente, scopri come interagire con la console in [Input e output](./io).
