# Créer des exécutables natifs

NeoObjectPascal empaquette un programme `.npas` dans un **exécutable natif autonome** — sans exiger que l'utilisateur final ait Java installé. Une seule commande produit :

- **macOS** → un paquet `.app`
- **Windows** → un dossier avec un `.exe`
- **Linux** → un dossier avec un binaire dans `bin/`

En interne, le build utilise **`jpackage`** (fourni avec le JDK 14+), qui empaquette l'interpréteur, votre projet et un runtime Java (JRE) allégé à l'intérieur de l'app.

## Prérequis

- Un **JDK 14 ou supérieur** dans le PATH (`jpackage` accompagne le JDK).
- Exécuter à partir du **JAR empaqueté** de l'interpréteur (`neoobjectpascal.jar`).

## Utilisation

```bash
java -jar neoobjectpascal.jar --build programa.npas [opções]
```

| Option | Description |
|-------|-----------|
| `--icon <png>` | icône de l'app, à partir d'un **PNG** (converti selon la plateforme) |
| `--name <Nome>` | nom de l'app/exécutable (par défaut : nom du programme) |
| `--output <dir>` | répertoire de sortie (par défaut : `./dist`) |
| `--target <mac\|windows\|linux>` | plateforme cible (par défaut : le système d'exploitation actuel) |

### Exemple

```bash
java -jar neoobjectpascal.jar --build main.npas --icon icon.png --name CalculoITBI --output dist
```

<Output>
📦 Empacotando 'main.npas' → macOS (.app) (CalculoITBI)
   • 5 arquivo(s) do projeto incluídos (a partir de .../NeoObjectPascal-Examples)
   • ícone: app-icon.icns
   • rodando jpackage...
✅ Build concluído: dist/CalculoITBI.app
   Executar: open "dist/CalculoITBI.app"
</Output>

## Le projet entier est empaqueté

Tout le répertoire du programme est inclus dans l'app — **fichiers et sous-dossiers**, en préservant la structure. Autrement dit :

- Les modules importés avec `uses pasta.modulo` (par exemple `helpers/calculoitbi.npas`) sont inclus.
- Les fichiers de données et les assets que le programme lit à l'exécution sont également inclus.

Ainsi, l'app empaquetée s'exécute exactement comme le programme s'exécutait à partir du code source. Les répertoires « poubelle » (`.git`, `node_modules`, le répertoire de sortie lui-même, ...) sont ignorés.

## Icône à partir d'un PNG

Passez un unique **PNG** avec `--icon` ; le build le convertit au bon format pour chaque plateforme :

| Plateforme | Format de l'icône | Conversion |
|-----------|------------------|-----------|
| macOS | `.icns` | via `iconset` + `iconutil` (natifs de macOS) |
| Windows | `.ico` | intégré en Java (sans outils externes) |
| Linux | `.png` | utilisé directement |

Utilisez un PNG carré (par exemple 512×512 ou 1024×1024) pour un meilleur résultat.

## Une plateforme à la fois

`jpackage` **ne fait pas de cross-compilation** : chaque artefact est généré sur son propre système d'exploitation. Sur macOS vous générez le `.app` ; le `.exe` est généré sur Windows et le `bin` sur Linux. Pour produire les trois, exécutez le build sur chaque système d'exploitation (ou utilisez une matrice de CI). Si vous passez un `--target` différent du système d'exploitation actuel, la commande vous avertit par une erreur claire.

## Depuis VS Code

Avec l'extension installée, faites un clic droit sur un fichier `.npas` (ou sur un dossier du projet) et choisissez **Build Native Executable**. L'extension demande le nom de l'app et, éventuellement, une icône PNG, puis génère le résultat dans `dist/`.

## Exécuter le résultat

- **macOS :** `open dist/CalculoITBI.app` (ou double-cliquez).
- **Windows :** `dist\CalculoITBI\CalculoITBI.exe`.
- **Linux :** `dist/CalculoITBI/bin/CalculoITBI`.

::: info Taille de l'app
Chaque exécutable est autonome et inclut un JRE, il occupe donc quelques dizaines de Mo — c'est la nature d'une app Java native, en échange de ne pas exiger Java sur la machine de l'utilisateur.
:::

::: tip Programmes interactifs (TerminalInk)
Les apps de terminal créées avec [TerminalInk](../terminalink/introduction) fonctionnent une fois empaquetées : lorsqu'elles sont ouvertes par double-clic (sans terminal), TerminalInk ouvre sa propre fenêtre. Les programmes de console pure (`WriteLn`) sont mieux exécutés à partir d'un terminal, pour voir la sortie.
:::

## Prochaines étapes

Avec le build natif, vous distribuez vos programmes comme de vraies apps. Pour revoir la syntaxe complète du langage, voir la [Référence du langage](../reference/language-reference).
