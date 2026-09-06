# Éditeur visuel d'interfaces (.xnpas)

L'extension VS Code inclut un **éditeur visuel (WYSIWYG)** pour créer des interfaces **sans écrire de code** — à la fois pour le terminal (**TerminalInk**) et pour le web (**WebInk**). Vous assemblez l'écran en glissant-déposant des composants, ajustez les propriétés et les événements, et l'éditeur génère automatiquement le fichier `.npas` correspondant.

## Le fichier `.xnpas`

L'éditeur travaille avec des fichiers **`.xnpas`** — un JSON contenant la conception de l'écran. Lorsque vous enregistrez `test.xnpas`, l'éditeur **(re)génère** le `test.npas` associé. La synchronisation est **unidirectionnelle** : le `.xnpas` est la source de vérité, et le `.npas` généré porte un en-tête indiquant qu'il **ne doit pas être modifié à la main**.

## Créer un écran

1. Ouvrez la palette de commandes (`Cmd/Ctrl+Maj+P`) et exécutez **« New UI Builder File (.xnpas) »**.
2. Choisissez un modèle : *WebInk — vide*, *WebInk — Tableau de bord*, *TerminalInk — vide* ou *TerminalInk — Formulaire*.
3. Le fichier s'ouvre directement dans l'éditeur visuel.

## La disposition de l'éditeur

- **Palette** (à gauche) — les composants de la cible choisie, groupés. Glissez-en un sur le canevas.
- **Canevas** (au centre) — un aperçu fidèle. Cliquez pour sélectionner ; faites glisser un composant placé pour le **réordonner** (ou le déplacer dans un autre conteneur).
- **Inspecteur** (à droite) — onglets **Propriétés**, **État** et **Événements**.
- **Barre d'outils** — le sélecteur **WebInk / TerminalInk**, le choix d'écran/route, et **Exécuter en direct** (génère et lance le `.npas`).

## État et événements

- **État** — déclarez des variables globales (nom, type et valeur initiale) partagées entre les écrans et les événements.
- **Événements** — pour les props comme `onClick`, `onChange`, `onSubmit`, `onConfirm` et `onCancel`, utilisez l'**éditeur hybride** : une **action sans code** (incrémenter une variable, définir une variable, naviguer vers un écran, utiliser la valeur saisie) **ou** passez en mode **Code** et écrivez du NeoObjectPascal librement. La fonction générée est prévisualisée sur place.

## Cibles : WebInk ou TerminalInk

Un `.xnpas` est entièrement **WebInk** ou **TerminalInk**. Le sélecteur change la cible (réinitialise l'arbre, car les jeux de composants diffèrent).

- **WebInk** — `Page`, `Container`, `Grid`, `Card`, `Navbar`, `Heading`, `Text`, `Badge`, `StatCard`, `Button`, `TextInput`, `Select`, `Checkbox`, `Table`, `List`, `Chart`, `Alert`, `ProgressBar`…
- **TerminalInk** — `VBox`, `HBox`, `Box`, `Text`, `Badge`, `TextInput`, `Select`, `MultiSelect`, `ConfirmInput`, `ProgressBar`, `StatusMessage`, `Alert`, listes…

## Synchronisation et le `.npas` généré

Enregistrer le `.xnpas` régénère le `.npas` associé à partir de la conception. **Ne modifiez pas le `.npas` généré à la main** — il est écrasé au prochain enregistrement. Si vous ouvrez un `.npas` généré, l'éditeur vous avertit et propose d'ouvrir le `.xnpas` correspondant.
## Visibilité, données dynamiques et focus

Trois fonctionnalités permettent aux écrans de réagir à l'état à l'exécution.

### La propriété `visible`

Chaque composant dispose, dans l'Inspecteur, du contrôle **Visible** : *Toujours* (par défaut) ou *Condition (fx)*. En mode *Condition*, saisissez une expression booléenne — le composant (et son sous-arbre) n'apparaît que lorsqu'elle est vraie. Par exemple, un `ConfirmInput` qui n'apparaît qu'après le remplissage du nom, avec la condition `nome <> ""`.

### Données issues de variables (fx)

Les champs de données — `options` (Select/MultiSelect), `columns`/`rows` (Table), le graphique (Chart), `value` (ProgressBar), `items` (List) et les champs de valeur — comportent un bouton **fx**. Activé, le champ n'accepte plus une valeur fixe mais se lie au nom d'une **variable ou expression**. Vous pouvez ainsi alimenter un composant avec des données d'une API, par exemple, plutôt qu'avec des valeurs saisies à la main.

### Focus dans TerminalInk

Les composants de saisie de TerminalInk (`TextInput`, `PasswordInput`, `EmailInput`, `ConfirmInput`, `Select`, `MultiSelect`) reçoivent deux champs :

- **Clé** (`key`) — un identifiant stable du composant.
- **Focus initial** (`autoFocus`) — place le focus dessus dès la première image.

De plus, l'éditeur d'événements propose l'action **Focaliser le composant**, qui génère `focus("clé")` — pratique, par exemple, pour redonner le focus à un champ lorsqu'une confirmation est annulée.

## Langue de l'éditeur

L'éditeur visuel est **multilingue**, dans les mêmes 5 langues que la documentation : **Português, English, Deutsch, Français, Italiano**. Un sélecteur de langue (icône de globe) dans la barre supérieure change toute l'interface de l'éditeur à la volée — palette, propriétés, événements, conseils et messages.

La préférence se trouve dans le paramètre **`neoobjectpascal.uiBuilder.language`** (global, persistant). La valeur par défaut est **`auto`**, qui suit la langue d'affichage de VS Code et revient au portugais lorsqu'elle ne fait pas partie des cinq. Choisir une langue dans le sélecteur écrit ce paramètre.
