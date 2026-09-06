# WebInk — composants

Chaque widget accepte un enregistrement de props `#{}` comme premier argument, puis des enfants (autres nœuds) et/ou du texte. Tous acceptent `className` pour ajouter des classes Tailwind.

## Mise en page

| Widget | Description |
|--------|-----------|
| `Page` | coquille de la page (fond, couleur de texte) |
| `Section` | section avec espacement vertical |
| `Container` | largeur maximale centrée avec padding |
| `Grid(#{ cols })` | grille responsive de `cols` colonnes |
| `Row` / `Col` | ligne flex et colonne flexible |
| `Card` | carte avec bordure, ombre et padding |
| `Divider` · `Spacer` | séparateur · espace vertical |

```npas
Grid(#{ cols: 3 }, [
    Card(#{}, [ Heading(#{ level: 4 }, "A"), Text(#{}, "conteúdo") ]),
    Card(#{}, [ Heading(#{ level: 4 }, "B") ]),
    Card(#{}, [ Heading(#{ level: 4 }, "C") ])
])
```

## Typographie

| Widget | Description |
|--------|-----------|
| `Heading(#{ level: 1..6 })` | titre (h1–h6) |
| `Text` | paragraphe |
| `Badge(#{ color })` | badge coloré |
| `StatCard(#{ label, value, delta })` | carte de KPI (libellé, valeur, variation) |

```npas
StatCard(#{ label: "Receita", value: "R$ 128k", delta: "+12%" })
```

## Navigation

| Widget | Description |
|--------|-----------|
| `Navbar` | barre de navigation supérieure |
| `Sidebar` | barre latérale |
| `Link(#{ href })` | lien de navigation (route) |
| `Tabs` | bande d'onglets |

## Formulaires

| Widget | Props principales |
|--------|------------------|
| `Button(#{ variant, onClick })` | `variant` : `primary` \| `secondary` \| `danger` |
| `TextInput(#{ placeholder, value, onChange, onSubmit })` | `onSubmit` se déclenche sur Entrée |
| `TextArea(#{ rows, value, onChange })` | zone de texte |
| `Select(#{ options, onChange })` | `options` : tableau de valeurs |
| `Checkbox(#{ label, checked, onChange })` | case à cocher |
| `Form(#{ onSubmit })` | regroupe des champs ; envoie au submit |

```npas
Form(#{ onSubmit: enviar }, [
    TextInput(#{ placeholder: "E-mail", onChange: onEmail }),
    Button(#{ onClick: enviar }, "Entrar")
])
```

## Données et retours

| Widget | Description |
|--------|-----------|
| `Table(#{ columns, rows })` | tableau ; `columns` et `rows` sont des tableaux |
| `List(#{ items, ordered })` | liste à puces ou numérotée |
| `Chart(#{ type, data, options })` | graphique Chart.js |
| `Alert(#{ variant })` | alerte (`info`/`success`/`warning`/`error`) |
| `ProgressBar(#{ value })` | barre de progression (0–100) |
| `Spinner` | indicateur de chargement |

```npas
Table(#{
    columns: ["Produto", "Qtd", "Total"],
    rows: [ ["Café", "120", "R$ 600"], ["Chá", "80", "R$ 320"] ]
})
```

## Graphiques (Chart.js)

Le widget `Chart` correspond directement à la configuration de [Chart.js](https://www.chartjs.org) : `type`, `data` et `options` sont des enregistrements `#{}` qui deviennent le JSON du graphique.

```npas
Chart(#{
    type: "bar",
    data: #{
        labels: ["Jan", "Fev", "Mar"],
        datasets: [
            #{ label: "Vendas", data: [30, 45, 38] },
            #{ label: "Meta",   data: [40, 40, 40] }
        ]
    },
    options: #{ plugins: #{ legend: #{ position: "bottom" } } }
})
```

Types pris en charge : `bar`, `line`, `pie`, `doughnut`, `radar`, ainsi que les autres de Chart.js. Les graphiques sont réinstanciés à chaque redessin.

## Navigation avec routes

`render` reçoit une table de routes (URL → fonction d'écran). L'URL du navigateur reflète l'écran courant, et le bouton **Retour** ainsi que les **deep links** fonctionnent.

```npas
function home(): Object      begin return Page(#{}, [ /* ... */ ]); end;
function dashboard(): Object begin return Page(#{}, [ /* ... */ ]); end;

begin
    render(#{
        "/":          home,
        "/dashboard": dashboard
    });
end.
```

Deux façons de naviguer :

- **`Link(#{ href: "/dashboard" }, "Ir")`** — lien déclaratif (`<a>`), intercepté côté client avec `pushState`.
- **`navigate("/dashboard")`** — à l'intérieur d'un callback, change la route de façon programmatique (le serveur prévient le navigateur).

```npas
function irParaPainel(): Boolean
begin
    navigate("/dashboard");
    return true;
end;
```

::: tip Les clés de route sont des chaînes
Les clés de la table de routes sont des chaînes (`"/"`, `"/dashboard"`) — les littéraux d'enregistrement acceptent les clés chaîne en plus des identifiants.
:::

## Prochaines étapes

Pour distribuer votre application sous forme d'exécutable natif, voir [Générer des exécutables natifs](../testing/building-executables). Pour la syntaxe complète du langage, voir la [Référence du langage](../reference/language-reference).
