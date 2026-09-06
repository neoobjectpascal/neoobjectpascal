# Módulos Hierárquicos no NeoObjectPascal

## Visão Geral

O NeoObjectPascal suporta **módulos hierárquicos** usando a notação de pontos (.), permitindo organizar código em estruturas de diretórios complexas e profissionais.

## ✅ Status: 100% Funcional

O sistema de módulos hierárquicos está completamente implementado e testado.

## Sintaxe

### Importação com Pontos
```pascal
uses
  helpers.matematica,
  lib.core.calculadora,
  lib.utils.formatacao;
```

### Estrutura de Diretórios
```
projeto/
├── main.npas
├── helpers/
│   └── matematica.npas
└── lib/
    ├── core/
    │   └── calculadora.npas
    └── utils/
        └── formatacao.npas
```

## Exemplos Práticos

### 1. Módulo Simples em Subdiretório

**helpers/matematica.npas:**
```pascal
function somar(a, b): Integer;
begin
  return a + b;
end;

function multiplicar(x, y): Integer;
begin
  return x * y;
end;
```

**main.npas:**
```pascal
uses
  helpers.matematica;

var resultado: Integer;

begin
  resultado := somar(15, 25);
  WriteLn("Soma: ", resultado);
end.
```

### 2. Múltiplos Níveis de Hierarquia

**lib/core/calculadora.npas:**
```pascal
function elevarAoQuadrado(n): Integer;
begin
  return n * n;
end;
```

**lib/utils/formatacao.npas:**
```pascal
function duplicar(valor): Integer;
begin
  return valor * 2;
end;
```

**Uso:**
```pascal
uses
  lib.core.calculadora,
  lib.utils.formatacao;

begin
  WriteLn("Pipeline: ", 5 |> elevarAoQuadrado |> duplicar);
  // Resultado: 50
end.
```

## Conversão de Caminhos

O interpretador converte automaticamente:
- `helpers.matematica` → `helpers/matematica.npas`
- `lib.core.calculadora` → `lib/core/calculadora.npas`
- `lib.utils.formatacao` → `lib/utils/formatacao.npas`

## Recursos Avançados

### 1. Integração com Programação Funcional
```pascal
uses
  helpers.matematica,
  lib.utils.formatacao;

begin
  // Pipeline usando funções de diferentes módulos
  resultado := 10 |> somar(5) |> duplicar;
end.
```

### 2. Organização de Projetos Grandes
```
projeto_enterprise/
├── main.npas
├── business/
│   ├── logic/
│   │   ├── validation.npas
│   │   └── processing.npas
│   └── models/
│       ├── user.npas
│       └── product.npas
├── data/
│   ├── access/
│   │   ├── database.npas
│   │   └── cache.npas
│   └── parsers/
│       ├── json.npas
│       └── csv.npas
└── utils/
    ├── string.npas
    ├── math.npas
    └── io.npas
```

### 3. Importação Múltipla
```pascal
uses
  business.logic.validation,
  business.models.user,
  data.access.database,
  data.parsers.json,
  utils.string,
  utils.math;
```

## Vantagens

### ✅ Organização
- Estrutura clara de diretórios
- Separação lógica de responsabilidades
- Facilita manutenção de projetos grandes

### ✅ Reutilização
- Módulos podem ser compartilhados entre projetos
- Bibliotecas organizadas hierarquicamente
- Versionamento independente de módulos

### ✅ Escalabilidade
- Suporte a projetos de qualquer tamanho
- Estrutura familiar para desenvolvedores
- Compatível com ferramentas de build

## Limitações

1. **Caminhos Relativos**: Módulos devem estar acessíveis a partir do diretório de execução
2. **Warning Cosmético**: Ainda existe o warning de sintaxe (não afeta funcionalidade)
3. **Sem Namespace**: Funções ficam no escopo global (última importação prevalece)

## Comparação com Outras Linguagens

| Linguagem | Sintaxe | NeoObjectPascal |
|-----------|---------|-----------------|
| Java | `import com.example.Utils` | `uses com.example.Utils` |
| Python | `from helpers import math` | `uses helpers.math` |
| C# | `using System.Collections` | `uses System.Collections` |
| JavaScript | `import { func } from './utils'` | `uses utils` |

## Conclusão

O sistema de módulos hierárquicos do NeoObjectPascal oferece:
- ✅ **Sintaxe familiar** com notação de pontos
- ✅ **Flexibilidade total** para organização
- ✅ **Compatibilidade** com programação funcional
- ✅ **Escalabilidade** para projetos enterprise

É uma implementação robusta e profissional que permite organizar projetos de qualquer complexidade.
