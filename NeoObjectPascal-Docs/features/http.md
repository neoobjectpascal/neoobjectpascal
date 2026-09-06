# Chamadas de API (HTTP)

O NeoObjectPascal fala HTTP de forma nativa. Habilite o recurso com `uses http;` e você ganha um conjunto de funções para consumir APIs REST — sem nenhuma dependência externa. Por baixo dos panos, tudo é servido pelo `HttpClient` do Java 11, que já vem com a JVM.

Cabeçalhos, corpos e opções são descritos com **records** (`#{ ... }`), e cada resposta também chega como um record que você inspeciona com a notação de ponto.

## O primeiro GET

`httpGet(url)` faz uma requisição `GET` e devolve um record de resposta. Os campos mais usados são `resp.ok` (verdadeiro para status 200–299), `resp.status` (o código numérico) e `resp.json` (o corpo já convertido, quando a resposta é JSON):

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

Para enviar cabeçalhos junto de um `GET`, passe um record como segundo argumento:

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

## Enviando dados: POST, PUT, PATCH

`httpPost(url, corpo, cabecalhos)` envia um corpo para o servidor. As funções `httpPut` e `httpPatch` têm exatamente a mesma forma. Quando o corpo é um record (ou um array), ele é **serializado automaticamente para JSON** e a requisição recebe o cabeçalho `Content-Type: application/json`:

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

### Autenticação

Duas funções auxiliares constroem o cabeçalho `Authorization` para você:

- `httpBearer(token)` devolve `#{ Authorization: "Bearer <token>" }`, ideal para APIs com token.
- `httpBasic(usuario, senha)` devolve `#{ Authorization: "Basic <base64>" }`, para autenticação básica.

Como o resultado é um record de cabeçalhos, você o passa diretamente onde uma API espera cabeçalhos:

```npas
resp := httpGet("https://api.exemplo.com/privado", httpBasic("ana", "s3nha"));
```

## Formulários com `httpForm`

Nem toda API aceita JSON. Para enviar dados no formato `application/x-www-form-urlencoded`, envolva o record com `httpForm(#{ ... })`. O corpo resultante é codificado como um formulário HTML clássico:

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

::: tip Três formas de corpo
Um **record ou array** vira JSON automaticamente; `httpForm(#{...})` envia `x-www-form-urlencoded`; e uma **string** é enviada exatamente como está, sem transformação. Escolha conforme o que a API espera.
:::

## Controle total com `httpRequest`

Quando você precisa de mais domínio sobre a requisição, `httpRequest` aceita um único record com todas as opções: `method`, `url`, `headers`, `body`, `form`, `query`, `auth` e `timeout`.

Os parâmetros de `query` são anexados à URL já codificados. O `timeout` é dado em segundos (o padrão é 30):

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

Repare que `query: #{ page: 1, q: "termo" }` foi transformado em `?page=1&q=termo`, com os valores devidamente escapados.

## O record de resposta

Toda função HTTP devolve um record com a mesma estrutura. Estes são os campos disponíveis:

| Campo | Tipo | Descrição |
| --- | --- | --- |
| `status` | `Integer` | O código de status HTTP (por exemplo, `200`, `404`, `500`). |
| `ok` | `Boolean` | `true` quando o status está na faixa 200–299. |
| `body` | `String` | O corpo bruto da resposta, como texto. |
| `json` | `Object` | O corpo já convertido, ou `nil` se a resposta não for JSON. |
| `headers` | `Object` | Os cabeçalhos da resposta, como um record. |
| `url` | `String` | A URL final da requisição, incluindo os parâmetros de `query`. |

## Tratando erros ao estilo `fetch`

O modelo de erros segue a filosofia do `fetch`: uma resposta HTTP **sempre volta normalmente**, mesmo com status 404 ou 500 — nesses casos, `resp.ok` é `false` e você decide o que fazer. Já falhas de **transporte** (sem conexão, timeout, DNS que não resolve, URL inválida) **lançam um erro** que você captura com `try/catch`:

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

::: warning 4xx/5xx não lançam erro
Não espere um `catch` para tratar um `404` ou um `500`: essas respostas chegam com `resp.ok` em `false` e devem ser verificadas com um `if`. Reserve o `try/catch` para falhas de rede — quando a requisição sequer completa. Veja [Tratamento de erros](../language/error-handling) para a mecânica completa.
:::

## Acesso encadeado ao `json`

O acesso por ponto funciona sobre records, então `resp.json.campo` lê um campo diretamente. No entanto, **cadeias posicionais como `resp.json.itens[0]` não são suportadas**: extraia o valor para uma variável intermediária antes de indexá-lo:

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

## Boas práticas

- Sempre verifique `resp.ok` (ou `resp.status`) antes de confiar no corpo da resposta.
- Envolva as chamadas em `try/catch` quando a rede for incerta, mas lembre-se: 4xx e 5xx não caem no `catch`.
- Use `httpBearer` e `httpBasic` em vez de montar o cabeçalho `Authorization` na mão.
- Para acessar itens indexados de `resp.json`, guarde a coleção em uma variável antes de usar `[ ]`.

---

A seguir, veja como interagir com o console em [Entrada e saída](./io).
