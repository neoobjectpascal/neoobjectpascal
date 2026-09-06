# Sistema Híbrido NeoObjectPascal-Java

O NeoObjectPascal agora suporta um revolucionário sistema híbrido que permite a execução de código Java diretamente dentro de funções NeoObjectPascal, combinando a simplicidade da sintaxe Pascal com o poder e as bibliotecas do Java.

## Sintaxe

A sintaxe para blocos Java é:

```pascal
java:(parametros) {
    // Código Java aqui
    return valor;
}
```

### Exemplos Básicos

#### Função sem parâmetros
```pascal
function obterDataAtual(): String
begin
    return java:() {
        java.time.LocalDateTime agora = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = 
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return agora.format(formatter);
    };
end;
```

#### Função com parâmetros
```pascal
function calcular(a, b): Integer
begin
    return java:(a, b) {
        Integer x = (Integer)param0;
        Integer y = (Integer)param1;
        return x * x + y * y;
    };
end;
```

#### Uso direto em expressões
```pascal
begin
    WriteLn("Resultado: ", java:(10, 20) {
        Integer a = (Integer)param0;
        Integer b = (Integer)param1;
        return a + b;
    });
end.
```

## Acesso a Parâmetros

Dentro do bloco Java, os parâmetros são acessados através de `param0`, `param1`, `param2`, etc., na ordem em que foram passados. É necessário fazer cast para o tipo Java apropriado:

```pascal
function formatarMensagem(nome, idade): String
begin
    return java:(nome, idade) {
        String n = (String)param0;
        Integer i = (Integer)param1;
        return String.format("Olá %s! Você tem %d anos.", n, i);
    };
end;
```

## Bibliotecas Internas Híbridas

As bibliotecas internas do NeoObjectPascal foram reimplementadas usando o sistema híbrido, proporcionando funcionalidades reais e poderosas:

### Biblioteca datetime (internal.datetime)

```pascal
uses internal.datetime;

begin
    WriteLn("Ano atual: ", getCurrentYear());
    WriteLn("Data atual: ", getCurrentDate());
    WriteLn("Hora atual: ", getCurrentTime());
    WriteLn("2024 é bissexto? ", isLeapYear(2024));
    WriteLn("Dias em fevereiro: ", getDaysInMonth(2024, 2));
    WriteLn("Idade: ", getAge(1990, 5, 15));
    WriteLn("Adicionar 30 dias: ", addDays(2024, 1, 1, 30));
end.
```

## Vantagens do Sistema Híbrido

1. **Poder do Java**: Acesso completo às bibliotecas e APIs do Java
2. **Sintaxe Familiar**: Mantém a simplicidade do Pascal
3. **Performance**: Compilação dinâmica de código Java otimizado
4. **Flexibilidade**: Permite soluções que seriam complexas em Pascal puro
5. **Interoperabilidade**: Integração perfeita entre os dois mundos

## Casos de Uso

### Manipulação de Datas
```pascal
function proximaSegunda(): String
begin
    return java:() {
        java.time.LocalDate hoje = java.time.LocalDate.now();
        java.time.LocalDate proximaSegunda = hoje.with(
            java.time.temporal.TemporalAdjusters.next(java.time.DayOfWeek.MONDAY)
        );
        return proximaSegunda.toString();
    };
end;
```

### Cálculos Matemáticos Avançados
```pascal
function calcularRaizQuadrada(numero): Real
begin
    return java:(numero) {
        Double n = ((Integer)param0).doubleValue();
        return Math.sqrt(n);
    };
end;
```

### Manipulação de Strings
```pascal
function inverterString(texto): String
begin
    return java:(texto) {
        String str = (String)param0;
        return new StringBuilder(str).reverse().toString();
    };
end;
```

## Limitações e Considerações

1. **Tipos**: É necessário fazer cast explícito dos parâmetros
2. **Imports**: Não é possível usar imports Java, use nomes completos das classes
3. **Exceptions**: Exceptions Java são capturadas e tratadas automaticamente
4. **Performance**: Há overhead de compilação dinâmica na primeira execução

## Exemplo Completo

```pascal
uses internal.datetime;

function calcularIdade(anoNascimento): Integer
begin
    return java:(anoNascimento) {
        java.time.LocalDate hoje = java.time.LocalDate.now();
        int anoAtual = hoje.getYear();
        return anoAtual - (Integer)param0;
    };
end;

function formatarMensagem(nome, idade): String
begin
    return java:(nome, idade) {
        return String.format("Olá %s! Você tem %d anos.", 
                           (String)param0, (Integer)param1);
    };
end;

begin
    WriteLn("=== Sistema Híbrido NeoObjectPascal-Java ===");
    WriteLn("Data atual: ", getCurrentDate());
    WriteLn("Idade: ", calcularIdade(1990));
    WriteLn("Mensagem: ", formatarMensagem("João", 25));
    
    WriteLn("Cálculo direto: ", java:(10, 20) {
        Integer a = (Integer)param0;
        Integer b = (Integer)param1;
        return a * a + b * b;
    });
end.
```

O sistema híbrido representa uma evolução significativa do NeoObjectPascal, oferecendo o melhor dos dois mundos: a simplicidade e elegância do Pascal com o poder e as bibliotecas do Java.