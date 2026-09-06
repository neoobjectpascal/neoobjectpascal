# Introduction à TerminalInk

**TerminalInk** est le framework d'interfaces de terminal de NeoObjectPascal. Inspiré de [React Ink](https://github.com/vadimdemedes/ink), il vous permet de construire des applications de terminal (TUI) de manière **déclarative** — vous décrivez l'aspect que doit avoir l'écran et le framework se charge de le dessiner, de le redessiner et de décoder le clavier.

En coulisses, TerminalInk s'appuie sur la bibliothèque Java [Lanterna](https://github.com/mabe02/lanterna), qui fournit un terminal multiplateforme, un écran avec tampon et le décodage des touches. Vous n'avez jamais à intervenir directement là-dessus : il suffit d'écrire des composants.

## Activer le module

Pour utiliser TerminalInk, déclarez le module dans la clause `uses` :

```npas
uses terminalink;
```

Cela rend disponibles tous les composants (`Text`, `VBox`, `TextInput`, `Spinner`, ...) ainsi que les fonctions de rendu et de thème.

## Le modèle de rendu (mode immédiat)

TerminalInk fonctionne en **mode immédiat**. Au lieu de monter l'arbre de composants une seule fois, vous écrivez une **fonction de construction** qui **retourne** l'arbre de l'interface. La fonction `render` reçoit cette fonction et l'appelle de façon répétée — à chaque image (~60 ms) — pour redessiner l'écran.

L'état de l'application vit dans des **variables normales** de NeoObjectPascal. Les *callbacks* d'événements (passés par le nom de la fonction) modifient ces variables, et à l'image suivante l'interface est reconstruite en reflétant déjà le nouvel état.

```npas
uses terminalink;

var nome: String;

function onNome(v): Boolean
begin
    nome := v;
    return true;
end;

function ui(): Object
begin
    return VBox(#{ gap: 1, padding: 1, border: "round" }, [
        Text(#{ bold: true, color: "cyan" }, "Cadastro"),
        TextInput(#{ placeholder: "Nome...", onChange: onNome }),
        Text(#{}, "Olá, " + nome)
    ]);
end;

begin
    render(ui);
end.
```

Ce programme dessine une boîte avec un titre, un champ de texte et un message de bienvenue. Chaque touche saisie déclenche `onNome`, qui met à jour la variable `nome` ; à l'image suivante, la ligne « Olá, ... » apparaît mise à jour.

::: tip Le cycle en une phrase
`état` (variables) → `ui()` retourne l'arbre → `render` dessine → l'utilisateur interagit → le *callback* modifie l'`état` → on recommence.
:::

## Props et littéraux d'enregistrement

Chaque composant reçoit ses **props** sous forme d'un **littéral d'enregistrement** — la syntaxe `#{ clé: valeur, ... }` :

```npas
Text(#{ bold: true, color: "yellow" }, "Atenção")
```

Les enregistrements sont une fonctionnalité du langage lui-même. Vous lisez un champ avec `enregistrement.champ`, ce qui vaut également pour les valeurs qui arrivent dans les *callbacks*. Consultez [Variables et types](../language/variables-and-types) pour en savoir plus sur les enregistrements.

## Callbacks : des fonctions passées par nom

Les gestionnaires d'événement (`onChange`, `onSubmit`, `onConfirm`, ...) reçoivent le **nom d'une fonction**. Cette fonction est appelée par le framework lorsque l'événement se produit :

```npas
function aoEnviar(v): Boolean
begin
    WriteLn("Enviado: " + v);
    return true;
end;

// ... dentro do ui():
TextInput(#{ placeholder: "Digite e Enter", onSubmit: aoEnviar })
```

Par convention, les *callbacks* retournent un `Boolean` (généralement `true`). L'important est l'effet de bord : modifier les variables d'état.

## Touches de sortie

Pour quitter une application TerminalInk :

- **Échap** ou **Ctrl+C** permettent toujours de quitter.
- La touche **`q`** permet aussi de quitter — **mais uniquement** lorsqu'aucun `TextInput` (ou champ de saisie) n'a le focus. Si un champ de texte a le focus, `q` est saisi normalement dans le champ.

::: warning Le focus et la touche `q`
Si votre interface comporte des champs de saisie, préférez indiquer à l'utilisateur de quitter avec **Échap**. La touche `q` n'est un raccourci de sortie que lorsque le focus n'est pas sur un champ modifiable.
:::

## Comment cela fonctionne en coulisses

- La base est **Lanterna**, qui fournit un terminal multiplateforme avec un **écran à tampon** et le décodage des touches.
- Chaque composant de haut niveau (`VBox`, `Badge`, `Spinner`, ...) se **développe** en un arbre de boîtes et de textes (une disposition de style *flexbox*).
- Un petit **moteur de disposition** calcule les positions et les tailles ; ensuite, un **moteur de rendu** dessine l'arbre sur l'écran à tampon, image par image.

Vous n'avez pas besoin de comprendre ces détails pour écrire des applications — mais ils expliquent pourquoi le modèle est en mode immédiat et pourquoi les props de disposition comme `flexDirection`, `gap` et `flexGrow` se comportent comme en *flexbox*.

---

Découvrez ensuite tous les [Composants](./components) disponibles.
