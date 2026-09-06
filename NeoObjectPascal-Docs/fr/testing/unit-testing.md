# Tests unitaires et mocking

NeoObjectPascal propose un **framework de tests natif** — ce n'est pas une bibliothèque externe, c'est une partie du langage lui-même. Vous écrivez des tests avec le mot-clé `test`, validez les résultats avec `expect(...)` et exécutez le tout directement via l'interpréteur, avec un rapport de résultats et de couverture.

Ce guide couvre l'écriture de tests, tous les matchers disponibles, l'organisation et l'exécution des suites, ainsi que le système de **mocking** pour isoler les dépendances.

## Écrire un test

Un test est une déclaration de premier niveau, au même niveau que les fonctions et les classes. La forme est :

```npas
test "descrição do teste"
begin
    // corpo do teste
end;
```

La description est une chaîne libre qui apparaît dans le rapport. À l'intérieur du bloc `begin ... end`, vous écrivez du code normal : vous déclarez des variables, appelez des fonctions, créez des objets et, à la fin, effectuez les vérifications avec `expect`.

```npas
class Calculadora
    public function somar(a: Integer, b: Integer): Integer
    begin
        return a + b;
    end;
end;

test "Calculadora deve somar dois números corretamente"
begin
    // GIVEN - dado uma calculadora
    var calc: Calculadora;
    calc := new Calculadora();

    // WHEN - quando somamos 5 + 3
    var resultado: Integer;
    resultado := calc.somar(5, 3);

    // THEN - então o resultado deve ser 8
    expect(resultado).toBe(8);
end;
```

::: tip Modèle GIVEN-WHEN-THEN
Organiser chaque test en trois blocs — **GIVEN** (préparation), **WHEN** (action) et **THEN** (vérification) — rend l'intention claire. C'est la même chose qu'Arrange-Act-Assert. Utilisez des commentaires `//` pour marquer chaque phase.
:::

## Matchers (`expect`)

La vérification commence toujours par `expect(valorReal)` suivi d'un matcher qui décrit l'attente. Si l'attente échoue, le test est marqué `[FAIL]` avec un message ; sinon, `[PASS]`.

### `.toBe(esperado)`

Vérifie l'égalité exacte. C'est le matcher le plus utilisé ; il fonctionne pour les nombres, les chaînes et les booléens.

```npas
test "toBe compara valores exatos"
begin
    expect(2 + 2).toBe(4);
    expect("Olá" + " mundo").toBe("Olá mundo");
end;
```

### `.toEqual(esperado)`

Équivalent à `.toBe` pour l'égalité de valeurs — à utiliser lorsque vous voulez rendre explicite dans le texte qu'il s'agit d'une comparaison d'égalité structurelle.

```npas
test "toEqual verifica igualdade"
begin
    var total: Integer;
    total := 100 + 50;
    expect(total).toEqual(150);
end;
```

### `.toBeTrue()` et `.toBeFalse()`

Vérifient des valeurs booléennes sans avoir besoin de comparer explicitement avec `true`/`false`.

```npas
test "matchers booleanos"
begin
    var maiorDeIdade: Boolean;
    maiorDeIdade := 20 >= 18;

    expect(maiorDeIdade).toBeTrue();
    expect(10 > 100).toBeFalse();
end;
```

### `.toBeNull()`

Vérifie si une valeur est nulle — utile pour les champs non encore initialisés ou les retours absents.

```npas
test "campo não inicializado começa nulo"
begin
    var resultado: Object;
    expect(resultado).toBeNull();
end;
```

| Matcher | Vérifie |
|---------|----------|
| `.toBe(v)` | égalité exacte |
| `.toEqual(v)` | égalité de valeur |
| `.toBeTrue()` | la valeur est vraie |
| `.toBeFalse()` | la valeur est fausse |
| `.toBeNull()` | la valeur est nulle |

## Organiser les tests

- **Extension `.test.npas`** — les fichiers de test utilisent le suffixe `.test.npas` (par exemple, `calculadora.test.npas`). C'est cette convention que reconnaît l'exécuteur de tests.
- **Un comportement par test** — chaque `test` doit vérifier un seul comportement. Préférez plusieurs petits tests bien nommés à un grand test qui valide tout.
- **Tests indépendants** — chaque test doit créer ses propres instances et ne pas dépendre de l'ordre d'exécution.

Une organisation typique sépare le code source des tests :

```
projeto/
├── src/
│   ├── calculadora.npas
│   └── conta.npas
└── tests/
    ├── calculadora.test.npas
    └── conta.test.npas
```

## Exécuter les tests

### Un seul fichier avec `-t`

Pour exécuter un fichier de test isolé, utilisez le drapeau `-t` (ou `--test`) :

```bash
java -jar neoobjectpascal.jar -t calculadora.test.npas
```

<Output>
========== TEST RESULTS ==========
[PASS] Calculadora deve somar dois números corretamente
[PASS] Calculadora deve subtrair dois números corretamente
[PASS] Calculadora deve multiplicar dois números corretamente
==================================
Total: 3 | Passed: 3 | Failed: 0
</Output>

Lorsqu'un test échoue, le rapport affiche la description et l'écart constaté :

<Output>
========== TEST RESULTS ==========
[PASS] Calculadora deve somar dois números corretamente
[FAIL] Calculadora deve multiplicar dois números corretamente: esperado 42 mas obteve 40
==================================
Total: 3 | Passed: 2 | Failed: 1
</Output>

### Tous les tests avec `--test-all`

Pour exécuter une suite entière de manière récursive, en pointant vers un répertoire, utilisez `--test-all` (ou `-ta`). Il trouve tous les fichiers `.test.npas` sous le répertoire et présente en plus un résumé de **couverture**. La couverture est mesurée par les **méthodes publiques de classe exercées par les tests** — chaque méthode publique d'une classe compte, et le rapport liste les méthodes couvertes et non couvertes par classe. Ce n'est pas un rapport entre fichiers de test et fichiers source :

```bash
java -jar neoobjectpascal.jar --test-all ./tests
```

<Output>
========== TEST RESULTS ==========
[PASS] Calculadora deve somar dois números corretamente
[PASS] ContaBancaria deve inicializar com saldo correto
[PASS] ContaBancaria deve depositar valor corretamente
[PASS] Email válido deve passar na validação
==================================
Total: 4 | Passed: 4 | Failed: 0

Test Coverage (public class methods):
  Public methods: 3
  Covered: 3
  Coverage: 100.00%
    ✓ Calculadora.somar
    ✓ ContaBancaria.depositar
    ✓ ValidadorEmail.validar
</Output>

::: tip Objectif de couverture
Visez à maintenir la couverture à **80 % ou plus** des méthodes publiques de vos classes. Chaque méthode publique non exercée par un test apparaît comme non couverte dans le rapport, indiquant précisément où des tests manquent.
:::

::: info Code de sortie
L'exécuteur retourne un code de sortie différent de zéro en cas d'échec, ce qui permet de l'utiliser directement dans des pipelines de CI/CD.
:::

## Mocking

Les tests unitaires doivent être rapides et prévisibles. Lorsque l'unité que vous testez dépend de quelque chose d'externe — un service d'e-mail, une API, une base de données — vous ne voulez pas déclencher l'appel réel. C'est là qu'intervient le **mocking** : vous remplacez une fonction ou une méthode par une version qui renvoie une valeur fixe, puis vous vérifiez qu'elle a été appelée.

### Syntaxe

```npas
// Substitui uma função ou método por um retorno fixo:
mock calcularDesconto thenReturn 50;
mock EmailService enviarEmail thenReturn true;

// Verifica que a função/método mockado foi chamado:
verify calcularDesconto;
verify EmailService enviarEmail;
```

- `mock Alvo metodo thenReturn valor;` — pour les méthodes de classe, indiquez la classe et la méthode. Pour les fonctions isolées, indiquez seulement le nom. La forme avec point (`mock EmailService.enviarEmail thenReturn true;`) est également acceptée.
- `verify Alvo metodo;` — fait échouer le test si la cible mockée n'a jamais été appelée.

La convention est de **déclarer les mocks dans le bloc GIVEN** et de **faire les `verify` dans le bloc THEN**.

### Exemple : isoler un service d'e-mail

Considérez un `NotificadorUsuario` qui dépend d'un `EmailService`. Dans le test, nous ne voulons pas envoyer de véritable e-mail — seulement garantir que le notificateur délègue correctement l'appel et retourne un succès.

```npas
// Serviço externo que queremos mockar
class EmailService
    public function enviarEmail(destinatario: String, mensagem: String): Boolean
    begin
        // Em produção, enviaria e-mail real via SMTP
        WriteLn("Enviando email para ", destinatario);
        return true;
    end;
end;

// Classe que usa o serviço
class NotificadorUsuario
    var emailService: EmailService;

    constructor Create(service: EmailService)
    begin
        self.emailService := service;
    end;

    public function notificar(usuario: String): Boolean
    begin
        return self.emailService.enviarEmail(usuario, "Você tem uma nova notificação!");
    end;
end;

test "NotificadorUsuario deve chamar EmailService ao notificar"
begin
    // GIVEN - dado um serviço de e-mail mockado
    var emailService: EmailService;
    emailService := new EmailService();

    // Mock do método enviarEmail para retornar true sem enviar e-mail real
    mock EmailService enviarEmail thenReturn true;

    var notificador: NotificadorUsuario;
    notificador := new NotificadorUsuario(emailService);

    // WHEN - quando notificamos um usuário
    var resultado: Boolean;
    resultado := notificador.notificar("usuario@teste.com");

    // THEN - então deve retornar true
    expect(resultado).toBeTrue();

    // E o método enviarEmail deve ter sido chamado
    verify EmailService enviarEmail;
end;
```

### Exemple : mocker une fonction isolée

Le même mécanisme fonctionne pour les fonctions de premier niveau. Ici, nous remplaçons un calcul par une valeur fixe :

```npas
function calcularDesconto(valor: Integer): Integer
begin
    // Cálculo complexo que queremos evitar no teste
    return valor * 10;
end;

test "Deve usar função mockada em vez da real"
begin
    // GIVEN - dada uma função mockada
    mock calcularDesconto thenReturn 50;

    // WHEN - quando chamamos a função
    var desconto: Integer;
    desconto := calcularDesconto(100);

    // THEN - então deve retornar o valor mockado
    expect(desconto).toBe(50);

    // E a função deve ter sido chamada
    verify calcularDesconto;
end;
```

::: warning Portée du mocking
Le mocking actuel remplace le retour complet de la cible et vérifie **si** elle a été appelée. Il n'y a pas encore de correspondance par paramètres spécifiques, de comptage d'appels ni de vérification d'ordre. Ne mockez que les dépendances externes et gardez la logique interne sous test réel.
:::

## Prochaines étapes

Avec des tests automatisés en place, l'étape naturelle est d'apprendre à investiguer les comportements à l'exécution. Poursuivez avec [Débogueur, VS Code et exécution dans le cloud](./debugging-tools).

Pour revoir la modélisation des classes utilisée dans les exemples, voir [Classes et objets](../oop/classes). Une collection de programmes complets se trouve dans [Exemples](../reference/examples).
