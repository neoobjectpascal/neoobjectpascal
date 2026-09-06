# Cloud Execution - NeoObjectPascal

## Visão Geral

O NeoObjectPascal v2.0 inclui **integração nativa com o NeoObjectPascal Cloud**, permitindo executar projetos remotamente na nuvem com um único comando.

## Benefícios

### Por Que Usar Cloud Execution?

1. **Escalabilidade**: Execute em servidores potentes na nuvem
2. **Colaboração**: Compartilhe execuções via URL
3. **Histórico**: Mantenha registro de todas as execuções
4. **Logs Persistentes**: Acesse logs a qualquer momento
5. **Sem Configuração**: Não precisa instalar Java localmente
6. **Multiplataforma**: Execute de qualquer lugar

## Sintaxe

```bash
java -jar neoobjectpascal.jar --execute-on-cloud <base_url> <project_name> <username> <password> <file.npas>
```

### Parâmetros

| Parâmetro | Descrição | Exemplo |
|-----------|-----------|---------|
| `base_url` | URL base da API do cloud | `http://api.neocloud.com` |
| `project_name` | Nome do projeto (pasta base) | `meu_projeto` |
| `username` | Usuário do cloud | `usuario@email.com` |
| `password` | Senha do cloud | `senha123` |
| `file.npas` | Arquivo principal a executar | `main.npas` |

## Exemplo Básico

### 1. Arquivo Simples

```pascal
// hello.npas
var mensagem: String;

begin
    mensagem := "Olá do Cloud!";
    WriteLn(mensagem);
end.
```

### 2. Executar no Cloud

```bash
java -jar neoobjectpascal.jar \
  --execute-on-cloud \
  http://localhost:8000 \
  hello_project \
  usuario@email.com \
  senha123 \
  hello.npas
```

### 3. Saída

```
╔══════════════════════════════════════════════════════════╗
║     NeoObjectPascal Cloud - Execução Remota             ║
╚══════════════════════════════════════════════════════════╝

🔐 Autenticando no cloud...
✓ Autenticado com sucesso!

📁 Coletando arquivos do projeto...
✓ Encontrados 1 arquivo(s)
  - hello.npas

🔄 Sincronizando estrutura do projeto...
✓ Estrutura sincronizada!

⬆️  Fazendo upload dos arquivos...
  [1/1] hello.npas
✓ Upload concluído!

🚀 Executando projeto no cloud...
✓ Execução iniciada!

═══════════════════════════════════════════════════════════
✅ Projeto executado com sucesso!
═══════════════════════════════════════════════════════════

🔗 Link da execução:
   http://localhost:8000/executions/123

```

## Projeto com Múltiplos Arquivos

O cloud execution automaticamente detecta e faz upload de **todos os arquivos .npas** no diretório do projeto.

### Estrutura de Exemplo

```
meu_projeto/
├── main.npas
├── helpers/
│   └── matematica.npas
└── utils/
    └── string.npas
```

### Código

**main.npas:**
```pascal
// Import de módulos (futuro)
var resultado: Integer;

function somar(a: Integer, b: Integer): Integer
begin
    return a + b;
end;

begin
    resultado := somar(10, 20);
    WriteLn("Resultado: ", resultado);
end.
```

**helpers/matematica.npas:**
```pascal
function multiplicar(a: Integer, b: Integer): Integer
begin
    return a * b;
end;

function dividir(a: Integer, b: Integer): Integer
begin
    return a / b;
end;
```

### Executar

```bash
cd meu_projeto
java -jar neoobjectpascal.jar \
  --execute-on-cloud \
  http://localhost:8000 \
  meu_projeto \
  usuario@email.com \
  senha123 \
  main.npas
```

### Resultado

Todos os arquivos são automaticamente detectados e enviados:

```
📁 Coletando arquivos do projeto...
✓ Encontrados 3 arquivo(s)
  - main.npas
  - helpers/matematica.npas
  - utils/string.npas

⬆️  Fazendo upload dos arquivos...
  [1/3] main.npas
  [2/3] helpers/matematica.npas
  [3/3] utils/string.npas
✓ Upload concluído!
```

## Fluxo de Execução

### Passo a Passo

1. **Autenticação**
   - Login com username/password
   - Recebe JWT token
   - Token usado em todas as requisições

2. **Coleta de Arquivos**
   - Busca recursiva por arquivos .npas
   - Mantém estrutura de diretórios
   - Calcula caminhos relativos

3. **Sincronização**
   - Cria estrutura do projeto no cloud
   - Define arquivo principal (main)
   - Cria diretórios necessários

4. **Upload**
   - Envia cada arquivo individualmente
   - Mantém estrutura de pastas
   - Valida upload de cada arquivo

5. **Execução**
   - Inicia execução remota
   - Recebe execution_id
   - Retorna URL de visualização

6. **Resultado**
   - Exibe link da execução
   - Usuário pode acessar logs
   - Histórico mantido no cloud

## Visualizando Resultados

### Via API

```bash
# Obter detalhes da execução
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8000/api/v1/executions/123

# Obter logs
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8000/api/v1/executions/123/log
```

### Resposta da API

```json
{
  "id": 123,
  "project_id": 456,
  "status": "completed",
  "started_at": "2025-10-05T10:30:00",
  "finished_at": "2025-10-05T10:30:03",
  "duration_seconds": 3,
  "output": "Resultado: 30\n"
}
```

## Casos de Uso

### 1. Desenvolvimento Distribuído

```bash
# Desenvolvedor A executa no cloud
java -jar neoobjectpascal.jar --execute-on-cloud ... main.npas

# Desenvolvedor B acessa os logs
curl http://api.cloud.com/executions/123/log
```

### 2. CI/CD Pipeline

```bash
#!/bin/bash
# Script de CI/CD

# Executar testes no cloud
java -jar neoobjectpascal.jar \
  --execute-on-cloud \
  $CLOUD_URL \
  $CI_PROJECT_NAME \
  $CLOUD_USER \
  $CLOUD_PASS \
  tests/all.test.npas

# Verificar resultado
if [ $? -eq 0 ]; then
  echo "✓ Testes passaram"
else
  echo "✗ Testes falharam"
  exit 1
fi
```

### 3. Execução Agendada

```bash
# Cron job para executar diariamente
0 9 * * * cd /app && java -jar neoobjectpascal.jar \
  --execute-on-cloud \
  http://api.cloud.com \
  daily_report \
  bot@company.com \
  $BOT_PASSWORD \
  reports/daily.npas
```

### 4. Benchmarking

```bash
# Executar no cloud para medir performance
java -jar neoobjectpascal.jar \
  --execute-on-cloud \
  http://api.cloud.com \
  benchmark \
  user@email.com \
  password \
  benchmarks/performance.npas

# Acessar duração via API
curl http://api.cloud.com/api/v1/executions/123 | jq .duration_seconds
```

## Segurança

### Autenticação

- ✅ JWT tokens com expiração
- ✅ Senhas hasheadas com bcrypt
- ✅ HTTPS recomendado em produção

### Isolamento

- ✅ Cada usuário vê apenas seus projetos
- ✅ Execuções isoladas por usuário
- ✅ Logs privados

### Boas Práticas

```bash
# ❌ Não faça isso (senha exposta)
java -jar neoobjectpascal.jar --execute-on-cloud ... user senha123 main.npas

# ✅ Use variáveis de ambiente
export CLOUD_PASSWORD="senha123"
java -jar neoobjectpascal.jar --execute-on-cloud ... user $CLOUD_PASSWORD main.npas

# ✅ Ou arquivo de configuração
cat > .cloudrc << EOF
CLOUD_URL=http://api.cloud.com
CLOUD_PROJECT=meu_projeto
CLOUD_USER=usuario@email.com
CLOUD_PASSWORD=senha123
EOF

source .cloudrc
java -jar neoobjectpascal.jar \
  --execute-on-cloud \
  $CLOUD_URL \
  $CLOUD_PROJECT \
  $CLOUD_USER \
  $CLOUD_PASSWORD \
  main.npas
```

## Tratamento de Erros

### Erro de Autenticação

```
❌ Erro ao executar no cloud: Falha na autenticação: {"detail":"Invalid credentials"}
```

**Solução**: Verificar username e password

### Erro de Upload

```
❌ Erro ao executar no cloud: Falha ao fazer upload do arquivo main.npas: {"detail":"File too large"}
```

**Solução**: Reduzir tamanho do arquivo ou dividir em módulos

### Erro de Execução

```
✅ Projeto executado com sucesso!
🔗 Link da execução:
   http://localhost:8000/executions/123
```

Mesmo com erro, a execução é criada. Acesse o link para ver logs de erro.

## Limitações Atuais

1. **Tamanho de Arquivo**: Máximo 10 MB por arquivo
2. **Timeout**: Execução máxima de 5 minutos
3. **Concorrência**: Máximo 10 execuções simultâneas por usuário
4. **Histórico**: Logs mantidos por 30 dias

## Roadmap Futuro

### v2.1
- Suporte a argumentos customizados
- Upload de assets (imagens, dados)
- Streaming de logs em tempo real

### v2.2
- Execução assíncrona com callback
- Webhooks para notificações
- Integração com GitHub Actions

### v3.0
- Execução em containers isolados
- Suporte a GPU para computação pesada
- Marketplace de projetos públicos

## Comparação: Local vs Cloud

| Aspecto | Local | Cloud |
|---------|-------|-------|
| Velocidade | ⚡ Rápido | 🌐 Depende da rede |
| Escalabilidade | 💻 Limitado | ☁️ Ilimitado |
| Histórico | ❌ Não | ✅ Sim |
| Compartilhamento | ❌ Difícil | ✅ Fácil (URL) |
| Configuração | ⚙️ Necessária | ✅ Zero config |
| Custo | 💰 Hardware próprio | 💳 Pay-as-you-go |

## Integração com CI/CD

### GitHub Actions

```yaml
name: Run NeoObjectPascal Tests

on: [push]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Download NeoObjectPascal
        run: wget https://github.com/.../neoobjectpascal.jar
      
      - name: Run tests on cloud
        env:
          CLOUD_PASSWORD: ${{ secrets.CLOUD_PASSWORD }}
        run: |
          java -jar neoobjectpascal.jar \
            --execute-on-cloud \
            ${{ secrets.CLOUD_URL }} \
            ${{ github.repository }} \
            ${{ secrets.CLOUD_USER }} \
            $CLOUD_PASSWORD \
            tests/all.test.npas
```

### GitLab CI

```yaml
test:
  script:
    - wget https://github.com/.../neoobjectpascal.jar
    - |
      java -jar neoobjectpascal.jar \
        --execute-on-cloud \
        $CLOUD_URL \
        $CI_PROJECT_NAME \
        $CLOUD_USER \
        $CLOUD_PASSWORD \
        tests/all.test.npas
  only:
    - master
```

## Conclusão

O recurso de **Cloud Execution** do NeoObjectPascal v2.0 oferece uma maneira simples e poderosa de executar projetos remotamente, com benefícios de escalabilidade, colaboração e histórico.

**Recursos principais:**
- ✅ Execução remota com um comando
- ✅ Upload automático de múltiplos arquivos
- ✅ Manutenção de estrutura de diretórios
- ✅ Autenticação segura (JWT)
- ✅ Histórico completo de execuções
- ✅ Logs persistentes
- ✅ Compartilhamento via URL

---

**Versão:** 2.0  
**Data:** Outubro 2025
