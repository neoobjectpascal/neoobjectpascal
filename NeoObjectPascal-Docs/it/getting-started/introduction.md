# Introduzione

**NeoObjectPascal** è un linguaggio di programmazione moderno ispirato a Object Pascal, con una sintassi pulita e familiare, ma dotato di funzionalità contemporanee: orientamento agli oggetti completo, un framework di test nativo, gestione degli errori con `try/catch/finally`, array nativi, programmazione funzionale e integrazione diretta con Java.

È interpretato da un runtime scritto in Java (con ANTLR4), il che porta due vantaggi: la portabilità della JVM e la possibilità di **incorporare codice Java direttamente** nel tuo programma quando ti serve qualcosa che solo l'ecosistema Java offre.

## Filosofia

- **Facile da imparare** — la sintassi in stile Pascal è esplicita e leggibile, ottima per chi sta iniziando.
- **Potente** — orientamento agli oggetti, test, mocking, manipolazione di JSON/CSV e l'operatore pipe permettono di scrivere codice conciso ed espressivo.
- **Moderno** — array, `for..in`, aritmetica `Real`, operatori booleani e gestione degli errori rendono il codice robusto e facile da mantenere.

## Un primo sguardo

```npas
// Un programma completo in NeoObjectPascal
class Pessoa
    var nome: String;

    constructor Create(n: String)
    begin
        self.nome := n;
    end;

    public function saudar(): String
    begin
        return "Olá, eu sou " + self.nome;
    end;
end;

var p: Object;

begin
    p := new Pessoa("Alice");
    WriteLn(p.saudar());
end.
```

<Output>
Olá, eu sou Alice
</Output>

## Cosa imparerai in questa guida

- Scrivere ed eseguire i tuoi primi programmi.
- Dichiarare variabili e usare i tipi `Integer`, `String`, `Boolean`, `Real`, `Object` e `Array`.
- Usare le strutture di controllo: `if`, `while`, `for` e `for..in`.
- Creare funzioni, procedure e classi con ereditarietà, interfacce e polimorfismo.
- Gestire gli errori con `try/catch/finally` e `raise`.
- Organizzare il codice in moduli con `uses` e usare le librerie interne.
- Scrivere test unitari e mock con il framework nativo.
- Integrare codice Java ed eseguire nel cloud.

::: tip Pronto per iniziare?
Prosegui con [Installazione e primo programma](./installation) e metti in funzione l'interprete.
:::
