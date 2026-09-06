# Interfacce

Un'**interfaccia** è un contratto: dichiara *quali* metodi una classe deve offrire, senza dire *come* funzionano. Le classi che implementano l'interfaccia si impegnano a fornire ciascuno di questi metodi. Le interfacce permettono di scrivere codice che dipende da un comportamento, non da un'implementazione specifica.

## Dichiarare un'interfaccia

Usa la parola chiave `interface`, seguita dal nome e solo dalle **firme** dei metodi — senza corpo. Ogni firma termina con `;`, e il blocco si chiude con `end;`:

```npas
interface Calculavel
    function calcular(a: Integer, b: Integer): Integer;
end;
```

::: info Solo firme
Un'interfaccia non ha campi né corpo di metodo. Descrive *cosa* deve esistere; la classe che la implementa decide *come*.
:::

## Implementare un'interfaccia

Una classe dichiara di rispettare un contratto con `implements`. Deve quindi fornire un'implementazione per ciascun metodo dichiarato nell'interfaccia:

```npas
interface Calculavel
    function calcular(a: Integer, b: Integer): Integer;
end;

class Soma implements Calculavel
    public function calcular(a: Integer, b: Integer): Integer
    begin
        return a + b;
    end;
end;

class Multiplicacao implements Calculavel
    public function calcular(a: Integer, b: Integer): Integer
    begin
        return a * b;
    end;
end;
```

Sia `Soma` che `Multiplicacao` rispettano il contratto `Calculavel`, ciascuna a modo suo:

```npas
var soma: Soma;
var mult: Multiplicacao;
var resultado: Integer;

begin
    soma := new Soma();
    resultado := soma.calcular(10, 5);
    WriteLn("Soma: 10 + 5 = ", resultado);

    mult := new Multiplicacao();
    resultado := mult.calcular(10, 5);
    WriteLn("Multiplicação: 10 * 5 = ", resultado);
end.
```

<Output>
Soma: 10 + 5 = 15
Multiplicação: 10 * 5 = 50
</Output>

## Interfacce multiple

Una classe può implementare **più di un'interfaccia** contemporaneamente, separando i nomi con la virgola. In questo caso deve fornire tutti i metodi di tutte le interfacce:

```npas
interface Calculavel
    function calcular(a: Integer, b: Integer): Integer;
end;

interface Descritivel
    function descrever(): String;
end;

class Somador implements Calculavel, Descritivel
    public function calcular(a: Integer, b: Integer): Integer
    begin
        return a + b;
    end;

    public function descrever(): String
    begin
        return "Operação de soma de dois inteiros";
    end;
end;

var s: Somador;

begin
    s := new Somador();
    WriteLn(s.descrever());
    WriteLn("Resultado: ", s.calcular(3, 4));
end.
```

<Output>
Operação de soma de dois inteiros
Resultado: 7
</Output>

## Programmare verso un'interfaccia

Il grande vantaggio delle interfacce è consentire che il codice dipenda dal **contratto**, e non da una classe concreta. Poiché `Soma` e `Multiplicacao` rispettano `Calculavel`, una stessa variabile generica può puntare a una qualsiasi di esse e viene chiamato il metodo corretto in fase di esecuzione:

```npas
interface Calculavel
    function calcular(a: Integer, b: Integer): Integer;
end;

class Soma implements Calculavel
    public function calcular(a: Integer, b: Integer): Integer
    begin
        return a + b;
    end;
end;

class Multiplicacao implements Calculavel
    public function calcular(a: Integer, b: Integer): Integer
    begin
        return a * b;
    end;
end;

var operacao: Object;

begin
    operacao := new Soma();
    WriteLn("Soma: ", operacao.calcular(6, 2));

    operacao := new Multiplicacao();
    WriteLn("Produto: ", operacao.calcular(6, 2));
end.
```

<Output>
Soma: 8
Produto: 12
</Output>

::: tip Interfaccia vs. ereditarietà
Usa l'**ereditarietà** (`extends`) quando le classi condividono una relazione "è un tipo di" e riutilizzano l'implementazione. Usa le **interfacce** (`implements`) quando classi senza parentela devono solo garantire lo stesso insieme di operazioni. Una classe può estendere una sola superclasse, ma implementare più interfacce.
:::

Con classi, ereditarietà, polimorfismo e interfacce, padroneggi la programmazione orientata agli oggetti di NeoObjectPascal. Il prossimo argomento introduce uno stile complementare di scrivere codice. Prosegui verso [Programmazione funzionale](../features/functional).
