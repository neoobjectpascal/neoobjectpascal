# Débogueur, VS Code et exécution dans le cloud

Au-delà du framework de tests, NeoObjectPascal offre des outils pour investiguer, éditer et exécuter du code à grande échelle : un **débogueur interactif** (avec un mode DAP pour les éditeurs), une **extension pour VS Code** et l'**exécution distante dans le cloud** via NeoObjectPascalCloud. Ce guide présente les trois.

## Débogueur interactif

Pour déboguer un programme pas à pas, exécutez-le avec le drapeau `-d` (ou `--debug`) :

```bash
java -jar neoobjectpascal.jar -d programa.npas
```

L'interpréteur ouvre un REPL interactif où vous contrôlez l'exécution. Les commandes principales :

| Commande | Action |
|---------|------|
| `b <linha>` | ajoute un point d'arrêt sur la ligne |
| `d <linha>` | supprime le point d'arrêt de la ligne |
| `list` | liste les points d'arrêt actifs |
| `c`, `continue` | continue jusqu'au prochain point d'arrêt |
| `s`, `step` | exécute la ligne suivante (step over) |
| `i`, `into` | entre dans la fonction appelée (step into) |
| `o`, `out` | sort de la fonction actuelle (step out) |
| `p <var>` | affiche la valeur d'une variable |
| `vars` | liste toutes les variables de la portée actuelle |
| `w <var>` | surveille (watch) les changements d'une variable |
| `set <var> <valor>` | modifie la valeur d'une variable à l'exécution |
| `stack` | affiche la pile d'appels |
| `q`, `quit` | termine le débogueur et le programme |

### Une session d'exemple

Considérez le programme ci-dessous :

```npas
var x: Integer;
var y: Integer;
var resultado: Integer;

begin
    x := 10;
    y := 20;
    resultado := x + y;
    WriteLn("resultado = ", resultado);
end.
```

Nous plaçons un point d'arrêt sur la ligne de la somme, observons `x`, inspectons des valeurs et modifions même une variable avant de continuer :

<Output>
debug> b 8
✓ Breakpoint adicionado na linha 8

debug> w x
✓ Watching variável: x

debug> c

⏸ PAUSADO na linha 8
  resultado := x + y;

debug> p x
x = 10 (tipo: INTEGER)

debug> vars
Variáveis locais:
  x = 10 (tipo: INTEGER)
  y = 20 (tipo: INTEGER)
  resultado = null (tipo: INTEGER)

debug> set x 50
✓ x = 50 (anterior: 10)

debug> c
resultado = 70
</Output>

::: tip Stratégie de points d'arrêt
Placez des points d'arrêt en début de boucles, sur les conditions `if` importantes, les appels de fonction et les points de retour. Combinez `w` (watch) avec `s` (step) pour suivre exactement quand et où une variable change.
:::

### Mode DAP pour les éditeurs

Pour un débogage graphique au sein d'un éditeur, l'interpréteur expose un serveur **DAP** (Debug Adapter Protocol) — le même protocole que VS Code et d'autres éditeurs utilisent pour les points d'arrêt visuels, l'inspection de variables et le contrôle pas à pas :

```bash
java -jar neoobjectpascal.jar --dap
```

Dans ce mode, l'interpréteur n'exécute pas de REPL textuel : il attend qu'un client DAP (comme VS Code) se connecte. Vous définissez des points d'arrêt en cliquant dans la marge de l'éditeur, et vous utilisez les boutons continuer/step/inspecter de l'interface elle-même. Normalement, vous ne lancez pas le mode DAP à la main — l'extension VS Code le fait pour vous.

Le **Step Into (F7)** entre dans le corps des méthodes de classe et des fonctions de votre projet — y compris lorsqu'elles sont définies dans un autre fichier importé via `uses` —, en ouvrant le fichier et la ligne corrects. La **Call Stack** affiche chaque frame de la pile (`Classe.metodo`, les fonctions et `main`) avec son fichier et sa ligne ; l'onglet **Variables** affiche les variables locales du frame sélectionné, y compris `self` à l'intérieur d'une méthode. Utilisez **Step Over** pour exécuter un appel sans y entrer et **Step Out** pour revenir à l'appelant.

::: tip Débogage dans VS Code
Lors du débogage depuis VS Code, la sortie du programme (`WriteLn`) apparaît dans la **console de débogage** pendant l'exécution. La résolution des modules est ancrée sur le répertoire du programme, si bien que déboguer un programme qui fait `uses dossier.module` fonctionne comme prévu. En revanche, l'**entrée interactive (`ReadLn`) n'est PAS disponible pendant le débogage** — elle renvoie une valeur par défaut. Pour les programmes interactifs, utilisez **Run** plutôt que le débogueur.
:::

## Extension VS Code

L'extension officielle pour **VS Code** transforme l'éditeur en un environnement complet pour NeoObjectPascal. Elle inclut :

- **Coloration syntaxique** pour les fichiers `.npas` et `.test.npas`.
- **Interpréteur embarqué** — l'extension embarque le JAR empaqueté (dans `VS-Code-Extension/bin/`), vous n'avez donc pas besoin de configurer manuellement le chemin de l'interpréteur.
- **Commandes** pour exécuter, tester et déboguer sans quitter l'éditeur.

Les commandes sont disponibles via la palette de commandes (`Cmd/Ctrl+Shift+P`) :

| Commande | Action |
|---------|------|
| `neoobjectpascal.run` | exécute le fichier `.npas` actuel |
| `neoobjectpascal.debug` | démarre le débogueur (via DAP) sur le fichier actuel |
| `neoobjectpascal.runTest` | exécute le fichier `.test.npas` actuel |
| `neoobjectpascal.runAllTests` | exécute tous les tests du projet de manière récursive |
| `neoobjectpascal.build` | génère un exécutable natif (exe/app/bin) du projet — voir [Créer des exécutables natifs](./building-executables) |

::: info Exécution dans le cloud
L'exécution dans le cloud se fait en ligne de commande avec l'option `--execute-on-cloud` de l'interpréteur (voir la section ci-dessous).
:::

::: tip Flux recommandé
Écrivez le code avec la coloration et l'autocomplétion, exécutez `neoobjectpascal.runTest` pour valider le fichier ouvert, et utilisez `neoobjectpascal.debug` pour suivre l'exécution avec des points d'arrêt visuels. Le tout en utilisant le JAR déjà fourni avec l'extension.
:::

## Exécution dans le cloud

NeoObjectPascal s'intègre nativement à **NeoObjectPascalCloud**, permettant d'exécuter un projet sur un serveur distant avec une seule commande. C'est utile pour partager des exécutions via une URL, conserver un historique de logs et exécuter sans dépendre de l'environnement local.

### Syntaxe

```bash
java -jar neoobjectpascal.jar --execute-on-cloud <url> <projeto> <usuario> <senha> <arquivo.npas>
```

| Paramètre | Description |
|-----------|-----------|
| `<url>` | URL de base de l'API du cloud (ex. : `http://localhost:8000`) |
| `<projeto>` | nom du projet (dossier de base) |
| `<usuario>` | utilisateur du cloud |
| `<senha>` | mot de passe du cloud |
| `<arquivo.npas>` | fichier principal à exécuter |

### Exemple

Soit le programme `hello.npas` :

```npas
var mensagem: String;
var numero: Integer;

begin
    mensagem := "Olá do NeoObjectPascal Cloud!";
    numero := 42;

    WriteLn("=================================");
    WriteLn(mensagem);
    WriteLn("Número mágico: ", numero);
    WriteLn("=================================");
end.
```

Nous l'exécutons dans le cloud :

```bash
java -jar neoobjectpascal.jar \
  --execute-on-cloud \
  http://localhost:8000 \
  hello_project \
  usuario@email.com \
  senha123 \
  hello.npas
```

L'interpréteur s'authentifie, collecte **tous les fichiers `.npas`** du répertoire du projet (en conservant la structure des dossiers), effectue l'envoi, déclenche l'exécution distante et renvoie un lien pour suivre les logs :

<Output>
🔐 Autenticando no cloud...
✓ Autenticado com sucesso!

📁 Coletando arquivos do projeto...
✓ Encontrados 1 arquivo(s)
  - hello.npas

⬆️  Fazendo upload dos arquivos...
  [1/1] hello.npas
✓ Upload concluído!

🚀 Executando projeto no cloud...
✅ Projeto executado com sucesso!

🔗 Link da execução:
   http://localhost:8000/executions/123
</Output>

::: warning N'exposez pas les mots de passe
Évitez de passer le mot de passe directement en ligne de commande, car il devient visible dans l'historique du shell et dans la liste des processus. Préférez les variables d'environnement :

```bash
export CLOUD_PASSWORD="senha123"
java -jar neoobjectpascal.jar --execute-on-cloud \
  http://localhost:8000 meu_projeto usuario@email.com "$CLOUD_PASSWORD" main.npas
```
:::

::: info Projets à plusieurs fichiers
La collecte est récursive : si le projet comporte des sous-dossiers (`helpers/`, `utils/`), tous les fichiers `.npas` sont détectés et envoyés automatiquement, en préservant les chemins relatifs. Il suffit d'indiquer le fichier principal dans la commande.
:::

## Prochaines étapes

Vous connaissez désormais les trois outils d'investigation et d'exécution de NeoObjectPascal. Pour consulter la syntaxe complète du langage en un seul endroit, poursuivez avec la [Référence du langage](../reference/language-reference).
