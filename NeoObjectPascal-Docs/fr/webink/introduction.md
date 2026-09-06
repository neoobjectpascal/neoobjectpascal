# WebInk — introduction

**WebInk** est le framework de frontend web de NeoObjectPascal — l'analogue web de [TerminalInk](../terminalink/introduction). Avec `uses webink;`, vous construisez une interface web **professionnelle** en code NeoObjectPascal, stylée avec **Tailwind CSS** et dotée de graphiques via **Chart.js**, et vous naviguez entre les écrans avec de vraies URL.

Contrairement à un générateur statique, WebInk est une **application vivante** : `render(...)` démarre un **serveur local** et ouvre le navigateur. L'état vit dans les variables ; à chaque interaction (clic, saisie, envoi), un callback s'exécute sur le serveur, modifie l'état et l'écran est redessiné — une interface **pilotée par le serveur**, exactement comme la boucle réactive de TerminalInk, mais sur le web.

::: warning TerminalInk et WebInk ne se mélangent pas
Un programme est **entièrement TerminalInk ou entièrement WebInk**. Utiliser `uses terminalink` et `uses webink` dans le même programme génère une erreur explicite.
:::

## Première application

```npas
uses webink;

var cliques: Integer;

function registrar(): Boolean
begin
    cliques := cliques + 1;
    return true;
end;

function home(): Object
begin
    return Page(#{}, [
        Navbar(#{}, [
            Heading(#{ level: 3 }, "Acme Inc."),
            Link(#{ href: "/relatorio" }, "Relatório")
        ]),
        Container(#{ className: "py-8 space-y-6" }, [
            Heading(#{ level: 1 }, "Painel"),
            Grid(#{ cols: 3 }, [
                StatCard(#{ label: "Receita", value: "R$ 128k", delta: "+12%" }),
                StatCard(#{ label: "Usuários", value: "3.420" }),
                StatCard(#{ label: "Cliques", value: cliques })
            ]),
            Card(#{}, [
                Heading(#{ level: 4 }, "Vendas por mês"),
                Chart(#{ type: "line", data: #{
                    labels: ["Jan", "Fev", "Mar", "Abr"],
                    datasets: [ #{ label: "R$ mil", data: [30, 45, 38, 52] } ]
                } })
            ]),
            Button(#{ onClick: registrar }, "Registrar clique")
        ])
    ]);
end;

begin
    cliques := 0;
    render(#{ "/": home });
end.
```

Lancez-la et le navigateur s'ouvre avec le tableau de bord ; cliquer sur le bouton incrémente le compteur en direct.

## Comment ça fonctionne

1. `render(rotas)` démarre un serveur HTTP local **mono-thread** et ouvre le navigateur.
2. Le navigateur charge un shell avec Tailwind + Chart.js (embarqués, hors ligne) et un petit runtime JS.
3. Le runtime demande au serveur l'écran de la route actuelle ; le serveur appelle la fonction de construction, génère le HTML et le renvoie.
4. Lors d'une interaction, le runtime envoie l'événement au serveur, qui appelle le **callback NeoObjectPascal**, met à jour l'état, redessine l'écran et renvoie le nouveau HTML.

L'état vit dans des variables globales, comme dans TerminalInk. Les callbacks modifient l'état ; la fonction de l'écran courant le relit et le redessine.

## État et événements

Les widgets interactifs reçoivent des callbacks via les props : `onClick`, `onChange`, `onSubmit`. Le callback est une fonction qui modifie l'état et retourne :

```npas
var nome: String;

function onNome(v): Boolean
begin
    nome := v;
    return true;
end;

// ...
TextInput(#{ placeholder: "Seu nome", value: nome, onChange: onNome })
```

::: tip Props et mots réservés
`class` et `to` sont des mots réservés — c'est pourquoi les widgets utilisent **`className`** (style React) et **`href`** (dans `Link`). Toute autre clé « réservée » peut être passée sous forme de chaîne : `#{ "class": "..." }`.
:::

## Lancer, compiler et déboguer

- **Lancer :** `java -jar neoobjectpascal.jar app.npas` démarre le serveur et ouvre le navigateur ; on quitte avec **Ctrl+C**. Dans VS Code, utilisez **Run**.
- **Compiler en natif :** `--build` empaquette l'application (avec les assets embarqués) — l'exécutable autonome démarre le serveur et ouvre le navigateur, **hors ligne**. Voir [Générer des exécutables natifs](../testing/building-executables).
- **Déboguer :** WebInk offre la **meilleure expérience de débogage**. Le serveur est mono-thread et le navigateur n'est qu'un client — sans la fenêtre Swing du terminal. Placez un point d'arrêt dans une fonction d'écran ou dans un callback : il se déclenche lorsque vous interagissez dans le navigateur, la pause est déterministe et le **Step Into (F7)** entre normalement dans vos fonctions. Voir [Débogueur, VS Code et cloud](../testing/debugging-tools).

## Prochaines étapes

Découvrez les [composants de WebInk](./components) — mise en page, typographie, formulaires, tableaux, graphiques Chart.js et navigation avec routes.
