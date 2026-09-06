# HTTP / API calls

NeoObjectPascal speaks HTTP natively. Enable the feature with `uses http;` and you gain a set of functions for consuming REST APIs — with no external dependencies. Under the hood everything is served by Java 11's `HttpClient`, which already ships with the JVM.

Headers, bodies and options are described with **records** (`#{ ... }`), and every response also arrives as a record that you inspect with dot notation.

## Your first GET

`httpGet(url)` performs a `GET` request and returns a response record. The most-used fields are `resp.ok` (true for status 200–299), `resp.status` (the numeric code) and `resp.json` (the already-parsed body, when the response is JSON):

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

To send headers along with a `GET`, pass a record as the second argument:

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

## Sending data: POST, PUT, PATCH

`httpPost(url, body, headers)` sends a body to the server. The `httpPut` and `httpPatch` functions have exactly the same shape. When the body is a record (or an array), it is **serialized to JSON automatically** and the request receives the `Content-Type: application/json` header:

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

### Authentication

Two helper functions build the `Authorization` header for you:

- `httpBearer(token)` returns `#{ Authorization: "Bearer <token>" }`, ideal for token-based APIs.
- `httpBasic(usuario, senha)` returns `#{ Authorization: "Basic <base64>" }`, for basic authentication.

Since the result is a headers record, you pass it directly wherever an API expects headers:

```npas
resp := httpGet("https://api.exemplo.com/privado", httpBasic("ana", "s3nha"));
```

## Forms with `httpForm`

Not every API accepts JSON. To send data in the `application/x-www-form-urlencoded` format, wrap the record with `httpForm(#{ ... })`. The resulting body is encoded like a classic HTML form:

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

::: tip Three body forms
A **record or array** becomes JSON automatically; `httpForm(#{...})` sends `x-www-form-urlencoded`; and a **string** is sent exactly as-is, with no transformation. Choose according to what the API expects.
:::

## Full control with `httpRequest`

When you need more command over the request, `httpRequest` accepts a single record with all the options: `method`, `url`, `headers`, `body`, `form`, `query`, `auth` and `timeout`.

The `query` parameters are appended to the URL already encoded. The `timeout` is given in seconds (the default is 30):

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

Notice how `query: #{ page: 1, q: "termo" }` was turned into `?page=1&q=termo`, with the values properly escaped.

## The response record

Every HTTP function returns a record with the same structure. These are the available fields:

| Field | Type | Description |
| --- | --- | --- |
| `status` | `Integer` | The HTTP status code (for example, `200`, `404`, `500`). |
| `ok` | `Boolean` | `true` when the status is in the 200–299 range. |
| `body` | `String` | The raw response body, as text. |
| `json` | `Object` | The already-parsed body, or `nil` if the response is not JSON. |
| `headers` | `Object` | The response headers, as a record. |
| `url` | `String` | The final request URL, including the `query` parameters. |

## Handling errors the `fetch` way

The error model follows the `fetch` philosophy: an HTTP response **always comes back normally**, even with a 404 or 500 status — in those cases, `resp.ok` is `false` and you decide what to do. **Transport** failures, however (no connection, timeout, DNS that does not resolve, invalid URL), **throw an error** that you catch with `try/catch`:

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

::: warning 4xx/5xx do not throw
Do not expect a `catch` to handle a `404` or a `500`: those responses arrive with `resp.ok` set to `false` and must be checked with an `if`. Reserve `try/catch` for network failures — when the request does not even complete. See [Error Handling](../language/error-handling) for the full mechanics.
:::

## Chained access to `json`

Dot access works on records, so `resp.json.campo` reads a field directly. However, **positional chains such as `resp.json.itens[0]` are not supported**: extract the value into an intermediate variable before indexing it:

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

## Best practices

- Always check `resp.ok` (or `resp.status`) before trusting the response body.
- Wrap the calls in `try/catch` when the network is uncertain, but remember: 4xx and 5xx do not fall into the `catch`.
- Use `httpBearer` and `httpBasic` instead of building the `Authorization` header by hand.
- To access indexed items of `resp.json`, store the collection in a variable before using `[ ]`.

---

Next, see how to interact with the console in [Input and Output](./io).
