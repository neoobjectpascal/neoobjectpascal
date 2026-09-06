# Thèmes dans TerminalInk

TerminalInk dispose d'un **système de thèmes** qui permet de définir des styles par défaut pour les composants à un seul endroit, au lieu de répéter les props de couleur à chaque appel. Un thème est un **enregistrement** qui associe des noms de composants à des ensembles de styles.

## Couleurs disponibles

Les couleurs utilisées dans les props et les thèmes sont des chaînes :

`red`, `green`, `yellow`, `blue`, `magenta`, `cyan`, `white`, `black`, `gray`, `default`.

## Les fonctions de thème

| Fonction                     | Rôle                                                                  |
| ---------------------------- | --------------------------------------------------------------------- |
| `defaultTheme()`             | Retourne l'enregistrement du thème par défaut.                        |
| `extendTheme(base, override)`| Effectue une **fusion profonde** : `override` l'emporte sur `base`.  |
| `setTheme(theme)`            | Définit le thème global actif. À appeler **avant** `render`.          |
| `ThemeProvider(#{ theme }, [enfants])` | Définit le thème actif et englobe les enfants.              |

### defaultTheme et extendTheme

Partez du thème par défaut et ne remplacez que ce que vous souhaitez. Un enregistrement de thème associe des noms de composants à des styles :

```npas
uses terminalink;

var t: Object;

function ui(): Object
begin
    return VBox(#{ gap: 1, padding: 1 }, [
        Spinner(#{ label: "Processando..." }),
        Badge(#{}, "PRO")
    ]);
end;

begin
    t := extendTheme(defaultTheme(), #{
        Spinner: #{ color: "magenta" },
        Badge: #{ color: "cyan" }
    });
    setTheme(t);
    render(ui);
end.
```

Ici, le `Spinner` devient magenta et le `Badge` devient cyan **sans** que ces couleurs apparaissent dans les appels des composants — elles proviennent du thème.

### setTheme vs. ThemeProvider

- `setTheme(t)` définit le thème global. C'est la forme la plus directe : appelez-le une fois, avant `render`.
- `ThemeProvider(#{ theme: t }, [ ... ])` définit le thème actif et englobe un sous-arbre. C'est également utile lorsque vous préférez exprimer le thème à l'intérieur de l'arbre d'UI lui-même :

```npas
function ui(): Object
begin
    return ThemeProvider(#{ theme: t }, [
        VBox(#{ padding: 1 }, [ Badge(#{}, "PRO") ])
    ]);
end;
```

::: warning Le thème est global en mode immédiat
En mode immédiat, le thème actif est **global** — il n'est pas isolé par sous-arbre. Même à l'intérieur d'un `ThemeProvider`, le thème défini s'applique à tout le rendu de cette image. Traitez le thème comme une configuration d'application, et non comme un style local.
:::

## Exemple complet : un formulaire

Ce programme construit un petit formulaire d'inscription avec validation visuelle, en combinant saisies et retour d'information.

```npas
uses terminalink;

var nome: String;
var email: String;
var enviado: Boolean;

function onNome(v): Boolean begin nome := v; return true; end;
function onEmail(v): Boolean begin email := v; return true; end;
function aoEnviar(v): Boolean begin enviado := true; return true; end;

function statusEmail(): Object
begin
    if enviado then
        return StatusMessage(#{ variant: "success" }, "Cadastro enviado!");
    return StatusMessage(#{ variant: "info" }, "Preencha e pressione Enter no e-mail.");
end;

function ui(): Object
begin
    return VBox(#{ gap: 1, padding: 1, border: "round", borderColor: "cyan" }, [
        Text(#{ bold: true, color: "cyan" }, "Cadastro"),
        HBox(#{ gap: 1 }, [ Text(#{ dim: true }, "Nome:"), TextInput(#{ placeholder: "Nome...", onChange: onNome }) ]),
        HBox(#{ gap: 1 }, [ Text(#{ dim: true }, "E-mail:"), EmailInput(#{ placeholder: "email...", domains: ["gmail.com"], onChange: onEmail, onSubmit: aoEnviar }) ]),
        Text(#{}, "Olá, " + nome),
        statusEmail()
    ]);
end;

begin
    enviado := false;
    setTheme(extendTheme(defaultTheme(), #{ StatusMessage: #{ } }));
    render(ui);
end.
```

Quittez avec **Échap** (des champs de saisie ont le focus, évitez donc `q`).

## Exemple complet : un tableau de bord

Un panneau de suivi qui réunit `Spinner`, `ProgressBar` et `Select`, avec un thème appliqué.

```npas
uses terminalink;

var progresso: Integer;
var ambiente: String;

function onAmbiente(v): Boolean begin ambiente := v; return true; end;

function ui(): Object
begin
    return VBox(#{ gap: 1, padding: 1, border: "round", borderColor: "blue" }, [
        HBox(#{ gap: 2 }, [
            Text(#{ bold: true }, "Deploy"),
            Spacer(),
            Badge(#{ color: "green" }, ambiente)
        ]),
        Spinner(#{ type: "dots", label: "Publicando artefatos..." }),
        ProgressBar(#{ value: progresso }),
        Text(#{ dim: true }, "Ambiente de destino:"),
        Select(#{ options: [
            #{ label: "Produção", value: "prod" },
            #{ label: "Homologação", value: "staging" },
            #{ label: "Desenvolvimento", value: "dev" }
        ], onChange: onAmbiente, visibleCount: 3 })
    ]);
end;

begin
    progresso := 65;
    ambiente := "prod";
    setTheme(extendTheme(defaultTheme(), #{
        Spinner: #{ color: "cyan" },
        ProgressBar: #{ color: "green" },
        Badge: #{ color: "green" }
    }));
    render(ui);
end.
```

::: tip Combinez avec le reste du langage
Les fonctions de construction de l'UI sont des fonctions normales de NeoObjectPascal. Vous pouvez composer l'arbre avec des [fonctions](../language/functions), réutiliser de la logique et même appeler des [blocs Java](../features/java-integration) pour récupérer des données. TerminalInk n'est qu'une bibliothèque de plus du langage.
:::

---

Revenez au début avec [Introduction à TerminalInk](./introduction) ou revoyez tous les [Composants](./components).
