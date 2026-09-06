# HTTP / API-Aufrufe

NeoObjectPascal spricht HTTP von Haus aus. Aktivieren Sie die Funktion mit `uses http;`, und Sie erhalten eine Reihe von Funktionen zum Ansprechen von REST-APIs — ganz ohne externe Abhängigkeiten. Unter der Haube erledigt alles der `HttpClient` von Java 11, der bereits mit der JVM ausgeliefert wird.

Header, Rümpfe und Optionen werden mit **Records** (`#{ ... }`) beschrieben, und jede Antwort trifft ebenfalls als Record ein, den Sie mit der Punktnotation untersuchen.

## Ihr erster GET

`httpGet(url)` führt eine `GET`-Anfrage aus und liefert einen Antwort-Record zurück. Die am häufigsten genutzten Felder sind `resp.ok` (wahr bei Status 200–299), `resp.status` (der numerische Code) und `resp.json` (der bereits geparste Rumpf, wenn die Antwort JSON ist):

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

Um zusammen mit einem `GET` Header zu senden, übergeben Sie einen Record als zweites Argument:

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

## Daten senden: POST, PUT, PATCH

`httpPost(url, body, headers)` sendet einen Rumpf an den Server. Die Funktionen `httpPut` und `httpPatch` haben genau dieselbe Form. Ist der Rumpf ein Record (oder ein Array), wird er **automatisch zu JSON serialisiert** und die Anfrage erhält den Header `Content-Type: application/json`:

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

### Authentifizierung

Zwei Hilfsfunktionen erstellen für Sie den `Authorization`-Header:

- `httpBearer(token)` liefert `#{ Authorization: "Bearer <token>" }`, ideal für tokenbasierte APIs.
- `httpBasic(usuario, senha)` liefert `#{ Authorization: "Basic <base64>" }`, für die Basisauthentifizierung.

Da das Ergebnis ein Header-Record ist, übergeben Sie es direkt dort, wo eine API Header erwartet:

```npas
resp := httpGet("https://api.exemplo.com/privado", httpBasic("ana", "s3nha"));
```

## Formulare mit `httpForm`

Nicht jede API akzeptiert JSON. Um Daten im Format `application/x-www-form-urlencoded` zu senden, umschließen Sie den Record mit `httpForm(#{ ... })`. Der entstehende Rumpf wird wie ein klassisches HTML-Formular kodiert:

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

::: tip Drei Rumpfformen
Ein **Record oder Array** wird automatisch zu JSON; `httpForm(#{...})` sendet `x-www-form-urlencoded`; und eine **Zeichenkette** wird genau so gesendet, wie sie ist, ohne Umwandlung. Wählen Sie je nachdem, was die API erwartet.
:::

## Volle Kontrolle mit `httpRequest`

Wenn Sie mehr Kontrolle über die Anfrage benötigen, nimmt `httpRequest` einen einzigen Record mit allen Optionen entgegen: `method`, `url`, `headers`, `body`, `form`, `query`, `auth` und `timeout`.

Die `query`-Parameter werden bereits kodiert an die URL angehängt. Das `timeout` wird in Sekunden angegeben (Standard ist 30):

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

Beachten Sie, wie `query: #{ page: 1, q: "termo" }` in `?page=1&q=termo` umgewandelt wurde, mit korrekt escapten Werten.

## Der Antwort-Record

Jede HTTP-Funktion liefert einen Record mit derselben Struktur zurück. Dies sind die verfügbaren Felder:

| Feld | Typ | Beschreibung |
| --- | --- | --- |
| `status` | `Integer` | Der HTTP-Statuscode (zum Beispiel `200`, `404`, `500`). |
| `ok` | `Boolean` | `true`, wenn der Status im Bereich 200–299 liegt. |
| `body` | `String` | Der rohe Antwortrumpf als Text. |
| `json` | `Object` | Der bereits geparste Rumpf oder `nil`, wenn die Antwort kein JSON ist. |
| `headers` | `Object` | Die Antwort-Header als Record. |
| `url` | `String` | Die endgültige Anfrage-URL, einschließlich der `query`-Parameter. |

## Fehler auf die `fetch`-Art behandeln

Das Fehlermodell folgt der `fetch`-Philosophie: Eine HTTP-Antwort **kommt immer normal zurück**, selbst mit Status 404 oder 500 — in diesen Fällen ist `resp.ok` gleich `false`, und Sie entscheiden, was zu tun ist. **Transport**-Fehler dagegen (keine Verbindung, Timeout, DNS ohne Auflösung, ungültige URL) **werfen einen Fehler**, den Sie mit `try/catch` abfangen:

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

::: warning 4xx/5xx werfen keinen Fehler
Erwarten Sie nicht, dass ein `catch` einen `404` oder `500` behandelt: Diese Antworten treffen mit `resp.ok` gleich `false` ein und müssen mit einem `if` geprüft werden. Reservieren Sie `try/catch` für Netzwerkfehler — wenn die Anfrage nicht einmal abgeschlossen wird. Siehe [Fehlerbehandlung](../language/error-handling) für die vollständige Mechanik.
:::

## Verketteter Zugriff auf `json`

Der Punktzugriff funktioniert auf Records, sodass `resp.json.campo` ein Feld direkt liest. **Positionsketten wie `resp.json.itens[0]` werden jedoch nicht unterstützt**: Extrahieren Sie den Wert in eine Zwischenvariable, bevor Sie ihn indizieren:

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

## Bewährte Praktiken

- Prüfen Sie stets `resp.ok` (oder `resp.status`), bevor Sie dem Antwortrumpf vertrauen.
- Umschließen Sie die Aufrufe mit `try/catch`, wenn das Netzwerk unsicher ist, aber denken Sie daran: 4xx und 5xx landen nicht im `catch`.
- Verwenden Sie `httpBearer` und `httpBasic`, anstatt den `Authorization`-Header von Hand zu erstellen.
- Um auf indizierte Elemente von `resp.json` zuzugreifen, speichern Sie die Sammlung in einer Variablen, bevor Sie `[ ]` verwenden.

---

Als Nächstes sehen Sie, wie Sie mit der Konsole interagieren, unter [Ein- und Ausgabe](./io).
