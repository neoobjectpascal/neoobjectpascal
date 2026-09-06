# Galerie d'exemples

Le dépôt de NeoObjectPascal inclut une collection d'exemples prêts à l'emploi dans `NeoObjectPascal/examples/`, numérotés de `01` à `44`. Ils vont du « Hello, World! » à l'orientation objet, aux tests avec mocking et à l'exécution dans le cloud. Cette page les organise par thème et montre le code de quelques-uns, représentatifs.

## Comment exécuter

Exécutez n'importe quel exemple en passant le chemin du fichier au JAR de l'interpréteur :

```bash
java -jar neoobjectpascal.jar examples/01-OlaMundo.npas
```

Les fichiers de test utilisent l'extension `.test.npas` et s'exécutent en mode test :

```bash
# um arquivo de teste
java -jar neoobjectpascal.jar -t examples/34-TesteSimples.test.npas

# todos os testes de um diretório (recursivo, com cobertura)
java -jar neoobjectpascal.jar --test-all examples/
```

::: tip
Le nom réel du JAR dépend de votre build (par exemple, `target/neoobjectpascal-*.jar`). Consultez l'[Introduction](../getting-started/introduction) pour générer l'interpréteur.
:::

## Fondamentaux

| Fichier | Démontre |
| --- | --- |
| `01-OlaMundo.npas` | Le programme minimal : un `WriteLn` dans le bloc principal |
| `02-Variaveis.npas` | Déclaration de variables (`String`, `Integer`) et affectation |
| `15-ConcatenacaoStrings.npas` | Concaténation de chaînes avec `+` et lecture d'entrée |
| `16-ExemploOriginal.npas` | Exemple introductif combinant plusieurs fonctionnalités |

```npas
begin
  WriteLn("Olá, Mundo!");
end.
```

<Output>
Olá, Mundo!
</Output>

## Contrôle et fonctions

| Fichier | Démontre |
| --- | --- |
| `03-EstruturasDeControle.npas` | `if ... then` et boucle `for i := 1 to N do` |
| `04-Funcoes.npas` | Déclaration et appel de fonction avec `return` |
| `07-ProgramacaoFuncional.npas` | Opérateur pipe `\|>` enchaîné |

```npas
function somar(a, b): Integer;
begin
  return a + b;
end;

var resultado: Integer;

begin
  resultado := somar(20, 22);
  WriteLn("O resultado da soma é: ", resultado);
end.
```

<Output>
O resultado da soma é: 42
</Output>

L'opérateur pipe passe la valeur de gauche comme premier argument de la fonction à droite :

```npas
function dobrar(x: Integer): Integer;
begin
  return x * 2;
end;

function adicionarUm(x: Integer): Integer;
begin
  return x + 1;
end;

begin
  WriteLn("O resultado do pipeline é: ", 5 |> dobrar |> adicionarUm);
end.
```

<Output>
O resultado do pipeline é: 11
</Output>

## Données et E/S

| Fichier | Démontre |
| --- | --- |
| `05-JSON.npas` | `JSON.parse(...) into` pour charger un objet |
| `06-CSV.npas` | `CSV.parse(...) into` pour charger des lignes |
| `08-EntradaSaida.npas` | `WriteLn` et `showMenu` |
| `13-MenuInterativo.npas` | Menu numéroté avec `showMenu` |
| `14-EntradaUsuario.npas` | Entrée de l'utilisateur avec `ReadLn` |

```npas
var dados: Object;
var jsonString: String;

begin
  jsonString := '{"nome": "Carmen Sandiego", "idade": 30, "cidade": "São Paulo"}';
  JSON.parse(jsonString) into dados;
  WriteLn("Dados JSON processados com sucesso!");
end.
```

<Output>
Dados JSON processados com sucesso!
</Output>

## Modules et bibliothèques internes

| Fichier | Démontre |
| --- | --- |
| `09-Modulos.npas` | `uses` d'un module `.npas` local |
| `10-ModulosCompleto.npas` | Utilisation de plusieurs modules |
| `11-ProjetoHierarquico.npas` | Modules dans des dossiers hiérarchiques (`lib.core.calculadora`) |
| `17-BibliotecasInternas.npas` | Fonctions de `internal.math`, `internal.string`, `internal.datetime` |
| `26-TesteDatetime.npas` | Bibliothèque `internal.datetime` |
| `28-TesteFile.npas` / `29-TesteFileSimples.npas` | Bibliothèque `internal.file` |

```npas
uses internal.math, internal.string;

begin
  WriteLn("Fatorial de 5: ", factorial(5));
  WriteLn("Fibonacci(8): ", fibonacci(8));
  WriteLn("Maiúsculas: ", toUpperCase("neoobjectpascal"));
end.
```

Détails de toutes les fonctions dans [Bibliothèques internes](../features/internal-libraries).

## Java et systèmes hybrides

| Fichier | Démontre |
| --- | --- |
| `20-SistemaHibrido.npas` | Combinaison de code Pascal et de Java embarqué |
| `21-TesteHibridoSimples.npas` | Version simplifiée du système hybride |
| `22-TesteSemJava.npas` | Équivalent sans Java, pour comparaison |
| `23-DebugJava.npas` | Débogage de blocs Java |
| `24-FuncaoJava.npas` | Fonction qui retourne une valeur d'un bloc Java |
| `25-JavaComParametros.npas` | Bloc Java recevant des paramètres (`param0`, `param1`, ...) |
| `27-SistemaHibridoFinal.npas` | Système hybride complet |

```npas
function testeJava(): String
begin
    return java:() {
        return "Olá do Java!";
    };
end;

begin
    WriteLn("Resultado: ", testeJava());
end.
```

<Output>
Resultado: Olá do Java!
</Output>

Plus d'informations dans [Intégration avec Java](../features/java-integration).

## Orientation objet

| Fichier | Démontre |
| --- | --- |
| `30-ClasseSimples.npas` | Classe avec champs, `constructor Create` et méthode |
| `31-Heranca.npas` | Héritage avec `extends`, `virtual`/`override` |
| `32-Interface.npas` | `interface` et `implements` |
| `33-Polimorfismo.npas` | Polymorphisme avec méthodes surchargées |

```npas
class Pessoa
    var nome: String;
    var idade: Integer;

    constructor Create(n: String, i: Integer)
    begin
        self.nome := n;
        self.idade := i;
    end;

    public function apresentar(): String
    begin
        return "Olá, meu nome é " + self.nome + " e tenho " + self.idade + " anos.";
    end;
end;

var pessoa: Pessoa;

begin
    pessoa := new Pessoa("João", 30);
    WriteLn(pessoa.apresentar());
end.
```

<Output>
Olá, meu nome é João e tenho 30 anos.
</Output>

Guide complet dans [Classes](../oop/classes), [Héritage](../oop/inheritance-polymorphism) et [Interfaces](../oop/interfaces).

## Tests et mocking

| Fichier | Démontre |
| --- | --- |
| `18-TesteSimples.npas` / `19-TesteCompleto.npas` | Blocs `test` introductifs |
| `34-TesteSimples.test.npas` | Modèle GIVEN-WHEN-THEN avec `expect(...).toBe(...)` |
| `35-TesteHeranca.test.npas` | Tests d'héritage |
| `36-TesteInterface.test.npas` | Tests d'interface |
| `37-TestePolimorfismo.test.npas` | Tests de polymorphisme |
| `38-TesteMocking.test.npas` | `mock ... thenReturn` et `verify` |

```npas
class Calculadora
    public function somar(a: Integer, b: Integer): Integer
    begin
        return a + b;
    end;
end;

test "Calculadora deve somar dois números corretamente"
begin
    var calc: Calculadora;
    calc := new Calculadora();

    var resultado: Integer;
    resultado := calc.somar(5, 3);

    expect(resultado).toBe(8);
end;
```

Remplacez une dépendance par un mock puis vérifiez qu'elle a été appelée :

```npas
test "NotificadorUsuario deve chamar EmailService ao notificar"
begin
    var emailService: EmailService;
    emailService := new EmailService();

    mock EmailService enviarEmail thenReturn true;

    var notificador: NotificadorUsuario;
    notificador := new NotificadorUsuario(emailService);

    var resultado: Boolean;
    resultado := notificador.notificar("usuario@teste.com");

    expect(resultado).toBeTrue();
    verify EmailService enviarEmail;
end;
```

Guide complet dans [Tests unitaires](../testing/unit-testing).

## Débogage et cloud

| Fichier | Démontre |
| --- | --- |
| `39-DebugSimples.npas` | Programme simple pour le débogueur interactif (`-d`) |
| `40-CloudExample.npas` | Exemple à exécuter sur NeoObjectPascal Cloud |
| `debug-boolean.npas`, `debug-class.npas`, `debug-class2.npas` | Scénarios auxiliaires de débogage |

## Nouvelles fonctionnalités

Exemples qui mettent en œuvre des fonctionnalités récentes du langage :

| Fichier | Démontre |
| --- | --- |
| `41-OperadoresBooleanos.npas` | Opérateurs `and`, `or`, `not` dans les conditions |
| `42-AritmenticaReal.npas` | Arithmétique avec `Real` et auto-promotion d'`Integer` |
| `43-Arrays.npas` | Tableaux littéraux, index base 0, affectation d'élément et `for ... in` |
| `44-TratamentoErros.npas` | `try`/`catch`/`finally` et `raise` |

```npas
var numeros: Array;
var item: Integer;
var soma: Integer;

begin
    numeros := [10, 20, 30, 40, 50];
    numeros[2] := 99;              // atribuição de elemento (base 0)

    soma := 0;
    for item in numeros do
        soma := soma + item;

    WriteLn("Soma: ", soma);
end.
```

<Output>
Soma: 219
</Output>

Gestion des erreurs avec `try`/`catch`/`finally` et `raise` :

```npas
function validarIdade(idade: Integer): String
begin
    if idade < 0 then
        raise "Idade invalida: " + idade;
    return "Idade valida: " + idade;
end;

begin
    try
    begin
        WriteLn(validarIdade(0 - 5));
    end
    catch (erro)
    begin
        WriteLn("Capturado: ", erro);
    end
    finally
    begin
        WriteLn("Cleanup concluido");
    end;
end.
```

<Output>
Capturado: Idade invalida: -5
Cleanup concluido
</Output>

## Continuez à explorer

Nouveau ici ? Commencez par l'[Introduction](../getting-started/introduction) pour installer l'interpréteur et exécuter votre premier programme. Pour la syntaxe complète, voir la [Référence du langage](./language-reference).
