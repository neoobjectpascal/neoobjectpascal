# Debugger Interativo - NeoObjectPascal

## Visão Geral

O NeoObjectPascal inclui um **debugger interativo completo** que permite depurar programas em tempo de execução com recursos profissionais como breakpoints, step-by-step execution, watch de variáveis e modificação de valores.

## Ativando o Debugger

Para executar um programa em modo debug, use o parâmetro `-d` ou `--debug`:

```bash
java -jar neoobjectpascal.jar -d programa.npas
java -jar neoobjectpascal.jar --debug programa.npas
```

## Interface do Debugger

Ao ativar o debugger, você verá:

```
╔══════════════════════════════════════════════════════════╗
║     NeoObjectPascal Debugger - Modo Interativo          ║
╚══════════════════════════════════════════════════════════╝

Comandos disponíveis:
  b <linha>          - Adicionar breakpoint
  d <linha>          - Remover breakpoint
  list               - Listar breakpoints
  c, continue        - Continuar execução
  s, step            - Executar próxima linha (step over)
  i, into            - Entrar em função (step into)
  w <var>            - Watch variável
  p <var>            - Imprimir valor de variável
  set <var> <valor>  - Modificar valor de variável
  vars               - Listar todas as variáveis
  stack              - Mostrar call stack
  q, quit            - Sair do debugger
```

## Comandos Disponíveis

### Breakpoints

#### Adicionar Breakpoint
```
debug> b 10
✓ Breakpoint adicionado na linha 10
```

#### Remover Breakpoint
```
debug> d 10
✓ Breakpoint removido da linha 10
```

#### Listar Breakpoints
```
debug> list
Breakpoints:
  Linha 10
  Linha 25
  Linha 42
```

### Controle de Execução

#### Continue (c)
Continua a execução até o próximo breakpoint:
```
debug> c
```

#### Step Over (s)
Executa a próxima linha sem entrar em funções:
```
debug> s
```

#### Step Into (i)
Executa a próxima linha e entra em funções:
```
debug> i
```

#### Quit (q)
Encerra o debugger e o programa:
```
debug> q
Encerrando debugger...
```

### Inspeção de Variáveis

#### Print Variável (p)
Imprime o valor de uma variável:
```
debug> p x
x = 42 (tipo: INTEGER)

debug> p nome
nome = "João" (tipo: STRING)
```

#### Listar Todas as Variáveis (vars)
Lista todas as variáveis no escopo atual:
```
debug> vars
Variáveis locais:
  x = 10 (tipo: INTEGER)
  y = 20 (tipo: INTEGER)
  resultado = 30 (tipo: INTEGER)
  nome = "Maria" (tipo: STRING)
```

### Watch de Variáveis

#### Adicionar Watch (w)
Monitora mudanças em uma variável:
```
debug> w x
✓ Watching variável: x
```

Quando a variável mudar, você verá:
```
⚠ WATCH: x = 50 (anterior: 42)
```

### Modificação de Variáveis

#### Set Variável (set)
Modifica o valor de uma variável em tempo de execução:
```
debug> set x 100
✓ x = 100 (anterior: 42)

debug> set nome "Pedro"
✓ nome = "Pedro" (anterior: "João")
```

### Call Stack

#### Mostrar Call Stack (stack)
Exibe a pilha de chamadas:
```
debug> stack
Call Stack:
  #2 calcular (linha 15)
  #1 processar (linha 8)
  #0 main (linha 3)
```

## Exemplo Prático

### Programa de Exemplo

```pascal
// exemplo.npas
var x: Integer;
var y: Integer;
var resultado: Integer;

begin
    x := 10;
    y := 20;
    resultado := x + y;
    WriteLn("Resultado: ", resultado);
    
    x := x * 2;
    y := y + 5;
    resultado := x - y;
    WriteLn("Novo resultado: ", resultado);
end.
```

### Sessão de Debug

```bash
$ java -jar neoobjectpascal.jar -d exemplo.npas
```

```
╔══════════════════════════════════════════════════════════╗
║     NeoObjectPascal Debugger - Modo Interativo          ║
╚══════════════════════════════════════════════════════════╝

debug> b 9
✓ Breakpoint adicionado na linha 9

debug> w x
✓ Watching variável: x

debug> c

═══════════════════════════════════════════════════════════
⏸ PAUSADO na linha 9
───────────────────────────────────────────────────────────
  resultado := x + y;
═══════════════════════════════════════════════════════════

debug> p x
x = 10 (tipo: INTEGER)

debug> p y
y = 20 (tipo: INTEGER)

debug> vars
Variáveis locais:
  x = 10 (tipo: INTEGER)
  y = 20 (tipo: INTEGER)
  resultado = null (tipo: INTEGER)

debug> set x 50
✓ x = 50 (anterior: 10)

debug> s

⚠ WATCH: x = 100 (anterior: 50)

debug> p resultado
resultado = 70 (tipo: INTEGER)

debug> c
Resultado: 70
Novo resultado: 75
```

## Recursos Avançados

### 1. Breakpoints Condicionais
Atualmente, breakpoints são incondicionais. Em versões futuras, será possível adicionar condições:
```
debug> b 10 if x > 50
```

### 2. Watch Expressions
Atualmente, apenas variáveis simples podem ser watched. Futuras versões suportarão expressões:
```
debug> w x + y
```

### 3. Modificação de Objetos
Você pode modificar campos de objetos:
```
debug> set pessoa.nome "Carlos"
```

## Casos de Uso

### 1. Encontrar Bugs
Use breakpoints para pausar em pontos suspeitos e inspecionar valores:
```
debug> b 42
debug> c
debug> vars
```

### 2. Entender Fluxo de Execução
Use step-by-step para acompanhar o fluxo:
```
debug> s
debug> s
debug> stack
```

### 3. Testar Cenários Diferentes
Modifique valores para testar diferentes caminhos:
```
debug> set condicao true
debug> c
```

### 4. Monitorar Mudanças
Use watch para detectar quando variáveis mudam:
```
debug> w contador
debug> c
```

## Limitações Conhecidas

1. **Source Lines**: O debugger mostra o AST, não o código-fonte original
2. **Breakpoints em Expressões**: Apenas statements podem ter breakpoints
3. **Conditional Breakpoints**: Não suportado ainda
4. **Data Breakpoints**: Não suportado (pause quando variável muda)

## Dicas e Truques

### 1. Use Breakpoints Estratégicos
Coloque breakpoints em:
- Início de loops
- Condições if importantes
- Chamadas de função
- Pontos de retorno

### 2. Combine Watch e Step
```
debug> w resultado
debug> s
debug> s
debug> s
```

### 3. Use vars para Overview
Antes de investigar, veja todas as variáveis:
```
debug> vars
debug> p variavel_suspeita
```

### 4. Modifique e Continue
Teste correções sem recompilar:
```
debug> set valor 100
debug> c
```

### 5. Use Stack para Contexto
Quando pausado, veja de onde veio:
```
debug> stack
```

## Integração com IDEs

O debugger pode ser integrado com IDEs através de:
- **Pipes**: Enviar comandos via stdin
- **Scripts**: Automatizar sessões de debug
- **Protocolo DAP**: Debug Adapter Protocol (futuro)

### Exemplo de Script
```bash
#!/bin/bash
cat > /tmp/debug-session.txt << EOF
b 10
c
p x
vars
q
EOF

java -jar neoobjectpascal.jar -d programa.npas < /tmp/debug-session.txt
```

## Comparação com Outros Debuggers

| Recurso | NeoObjectPascal | GDB | LLDB | VS Code |
|---------|----------------|-----|------|---------|
| Breakpoints | ✅ | ✅ | ✅ | ✅ |
| Step Over/Into | ✅ | ✅ | ✅ | ✅ |
| Watch Variables | ✅ | ✅ | ✅ | ✅ |
| Modify Values | ✅ | ✅ | ✅ | ✅ |
| Call Stack | ✅ | ✅ | ✅ | ✅ |
| Conditional BP | ❌ | ✅ | ✅ | ✅ |
| Data BP | ❌ | ✅ | ✅ | ✅ |
| GUI | ❌ | ❌ | ❌ | ✅ |

## Conclusão

O debugger do NeoObjectPascal oferece recursos profissionais para depuração interativa, tornando o desenvolvimento mais produtivo e a resolução de bugs mais eficiente.

**Recursos principais:**
- ✅ Breakpoints
- ✅ Step-by-step execution
- ✅ Variable inspection
- ✅ Variable modification
- ✅ Watch variables
- ✅ Call stack
- ✅ Interactive REPL

---

**Versão:** 2.0  
**Data:** Outubro 2025
