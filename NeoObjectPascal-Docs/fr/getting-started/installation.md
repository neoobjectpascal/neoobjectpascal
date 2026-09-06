# Installation et premier programme

L'interpréteur de NeoObjectPascal est distribué sous la forme d'un **fichier JAR exécutable**, donc tout ce dont vous avez besoin est **Java 11 ou une version supérieure** installé.

## Prérequis

- **Java 11+** (JRE ou JDK). Vérifiez avec :

```bash
java -version
```

- (Optionnel) **Maven**, si vous souhaitez compiler l'interpréteur à partir du code source.

## Obtenir l'interpréteur

Vous pouvez utiliser le JAR déjà empaqueté (par exemple, celui qui accompagne l'extension VS Code, dans `VS-Code-Extension/bin/`) ou compiler à partir du dépôt :

```bash
cd NeoObjectPascal
mvn package -DskipTests
# Le JAR final se trouve dans target/neoobjectpascal-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Bonjour, monde

Créez un fichier `ola.npas` :

```npas
begin
    WriteLn("Olá, NeoObjectPascal!");
end.
```

Et exécutez :

```bash
java -jar neoobjectpascal.jar ola.npas
```

<Output>
Olá, NeoObjectPascal!
</Output>

::: tip Extension de fichier
Les programmes utilisent l'extension `.npas`. Les fichiers de test utilisent `.test.npas`.
:::

## Options de la ligne de commande

L'interpréteur accepte plusieurs indicateurs :

| Indicateur | Description |
|------|-----------|
| _(aucun)_ | Exécute le fichier `.npas` |
| `-q`, `--no-warnings` | Supprime les avertissements d'analyse |
| `-t`, `--test` | Mode test (exécute un `.test.npas`) |
| `-ta`, `--test-all <dir>` | Exécute tous les tests de manière récursive, avec couverture |
| `-d`, `--debug` | Mode débogueur interactif |
| `--dap` | Mode DAP (intégration avec VS Code) |
| `--build <fichier> [--icon png] [--name] [--output] [--target]` | Génère un exécutable natif (exe/app/bin) |
| `--execute-on-cloud <url> <proj> <user> <pass>` | Exécute sur NeoObjectPascalCloud |
| `-h`, `--help` | Affiche l'aide |

Exemples :

```bash
java -jar neoobjectpascal.jar --no-warnings ola.npas
java -jar neoobjectpascal.jar -t calculadora.test.npas
java -jar neoobjectpascal.jar --test-all ./examples
```

::: tip Éditeur recommandé
Installez l'**extension VS Code** pour NeoObjectPascal et bénéficiez de la coloration syntaxique, de l'exécution, des tests et du débogage intégrés. Voir [Débogueur, VS Code et cloud](../testing/debugging-tools).
:::

Maintenant que vous exécutez du code, comprenons la [structure d'un programme](./program-structure).
