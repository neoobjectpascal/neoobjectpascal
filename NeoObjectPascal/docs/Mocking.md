# Sistema de Mocking Profissional - NeoObjectPascal

## Visão Geral

O NeoObjectPascal inclui um **sistema de mocking profissional** que permite substituir implementações reais de funções e métodos por versões mockadas durante testes, facilitando testes unitários isolados e previsíveis.

## Por Que Usar Mocking?

### Benefícios

1. **Isolamento**: Teste uma unidade sem depender de outras
2. **Velocidade**: Evite operações lentas (banco de dados, rede)
3. **Previsibilidade**: Controle exato do comportamento
4. **Cobertura**: Teste cenários difíceis de reproduzir
5. **Independência**: Testes não dependem de serviços externos

### Casos de Uso

- Mockar chamadas a APIs externas
- Simular falhas e exceções
- Evitar envio real de emails/SMS
- Substituir acesso a banco de dados
- Testar diferentes cenários de retorno

## Sintaxe Básica

### Mock de Função

```pascal
mock nomeDaFuncao thenReturn valor
```

### Mock de Método de Classe

```pascal
mock NomeDaClasse nomeDoMetodo thenReturn valor
```

### Verificar Chamada

```pascal
verify nomeDaFuncao
verify NomeDaClasse nomeDoMetodo
```

## Exemplos Práticos

### 1. Mock de Função Isolada

```pascal
// Função real que queremos mockar
function calcularDesconto(valor: Integer): Integer
begin
    // Cálculo complexo que queremos evitar no teste
    return valor * 10 / 100;
end;

test "Deve usar função mockada"
begin
    // GIVEN - Dado uma função mockada
    mock calcularDesconto thenReturn 50;
    
    // WHEN - Quando chamamos a função
    var desconto: Integer;
    desconto := calcularDesconto(1000);
    
    // THEN - Então deve retornar o valor mockado
    expect(desconto).toBe(50);
    
    // E a função deve ter sido chamada
    verify calcularDesconto;
end;
```

### 2. Mock de Método de Classe

```pascal
// Serviço que faz chamada HTTP real
class ApiService
    public function buscarUsuario(id: Integer): String
    begin
        // Em produção, faria chamada HTTP real
        return "Usuario real do servidor";
    end;
end;

test "Deve usar método mockado"
begin
    // GIVEN - Dado um método mockado
    mock ApiService buscarUsuario thenReturn "Usuario mockado";
    
    var service: ApiService;
    service := new ApiService();
    
    // WHEN - Quando chamamos o método
    var usuario: String;
    usuario := service.buscarUsuario(123);
    
    // THEN - Então deve retornar o valor mockado
    expect(usuario).toBe("Usuario mockado");
    
    // E o método deve ter sido chamado
    verify ApiService buscarUsuario;
end;
```

### 3. Mock de Serviço Externo

```pascal
// Serviço de email que queremos mockar
class EmailService
    public function enviarEmail(destinatario: String, mensagem: String): Boolean
    begin
        // Em produção, enviaria email real via SMTP
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
        return self.emailService.enviarEmail(usuario, "Notificação!");
    end;
end;

test "NotificadorUsuario deve chamar EmailService"
begin
    // GIVEN - Dado um serviço mockado
    mock EmailService.enviarEmail thenReturn true;
    
    var emailService: EmailService;
    emailService := new EmailService();
    
    var notificador: NotificadorUsuario;
    notificador := new NotificadorUsuario(emailService);
    
    // WHEN - Quando notificamos um usuário
    var resultado: Boolean;
    resultado := notificador.notificar("usuario@teste.com");
    
    // THEN - Então deve retornar true
    expect(resultado).toBeTrue();
    
    // E o serviço de email deve ter sido chamado
    verify EmailService enviarEmail;
end;
```

### 4. Mock de Banco de Dados

```pascal
class DatabaseService
    public function buscarProduto(id: Integer): String
    begin
        // Em produção, consultaria banco de dados real
        return "SELECT * FROM produtos WHERE id = " + id;
    end;
    
    public function salvarProduto(nome: String): Boolean
    begin
        // Em produção, salvaria no banco
        return true;
    end;
end;

class ProdutoController
    var db: DatabaseService;
    
    constructor Create(database: DatabaseService)
    begin
        self.db := database;
    end;
    
    public function obterProduto(id: Integer): String
    begin
        return self.db.buscarProduto(id);
    end;
end;

test "Deve buscar produto sem acessar banco real"
begin
    // GIVEN - Dado um banco de dados mockado
    mock DatabaseService.buscarProduto thenReturn "Produto Mockado";
    
    var db: DatabaseService;
    db := new DatabaseService();
    
    var controller: ProdutoController;
    controller := new ProdutoController(db);
    
    // WHEN - Quando buscamos um produto
    var produto: String;
    produto := controller.obterProduto(123);
    
    // THEN - Então deve retornar o valor mockado
    expect(produto).toBe("Produto Mockado");
    
    // E o banco deve ter sido chamado
    verify DatabaseService buscarProduto;
end;
```

## Padrão GIVEN-WHEN-THEN com Mocking

### Estrutura Recomendada

```pascal
test "Descrição do teste"
begin
    // GIVEN - Preparação (arrange)
    // - Criar mocks
    // - Configurar objetos
    // - Definir valores iniciais
    mock ServicoExterno.metodo thenReturn valorEsperado;
    var objeto: MinhaClasse;
    objeto := new MinhaClasse();
    
    // WHEN - Ação (act)
    // - Executar o método/função sendo testado
    var resultado: TipoRetorno;
    resultado := objeto.metodoSendoTestado();
    
    // THEN - Verificação (assert)
    // - Verificar resultado
    // - Verificar chamadas
    expect(resultado).toBe(valorEsperado);
    verify ServicoExterno metodo;
end;
```

## Recursos Avançados

### 1. Mock de Métodos Privados

Métodos privados podem ser mockados da mesma forma:

```pascal
class MinhaClasse
    private function metodoPrivado(): Integer
    begin
        return 42;
    end;
    
    public function metodoPublico(): Integer
    begin
        return self.metodoPrivado() * 2;
    end;
end;

test "Deve mockar método privado"
begin
    // GIVEN
    mock MinhaClasse.metodoPrivado thenReturn 10;
    
    var obj: MinhaClasse;
    obj := new MinhaClasse();
    
    // WHEN
    var resultado: Integer;
    resultado := obj.metodoPublico();
    
    // THEN
    expect(resultado).toBe(20);
end;
```

### 2. Mock de Construtores

Para mockar construtores, mocke métodos chamados dentro dele:

```pascal
class Usuario
    var nome: String;
    
    constructor Create(n: String)
    begin
        self.nome := self.validarNome(n);
    end;
    
    private function validarNome(n: String): String
    begin
        // Validação complexa
        return n;
    end;
end;

test "Deve mockar validação no construtor"
begin
    // GIVEN
    mock Usuario.validarNome thenReturn "Nome Validado";
    
    // WHEN
    var usuario: Usuario;
    usuario := new Usuario("qualquer");
    
    // THEN
    verify Usuario validarNome;
end;
```

### 3. Múltiplos Mocks

Você pode mockar múltiplas funções/métodos no mesmo teste:

```pascal
test "Deve usar múltiplos mocks"
begin
    // GIVEN
    mock funcao1 thenReturn 10;
    mock funcao2 thenReturn 20;
    mock Classe1.metodo1 thenReturn 30;
    mock Classe2.metodo2 thenReturn 40;
    
    // WHEN
    var r1: Integer;
    var r2: Integer;
    r1 := funcao1();
    r2 := funcao2();
    
    // THEN
    expect(r1).toBe(10);
    expect(r2).toBe(20);
    verify funcao1;
    verify funcao2;
end;
```

## Boas Práticas

### 1. Mock Apenas o Necessário

❌ **Ruim**: Mockar tudo
```pascal
mock ClasseA.metodo1 thenReturn 1;
mock ClasseA.metodo2 thenReturn 2;
mock ClasseA.metodo3 thenReturn 3;
// ... mockando 20 métodos
```

✅ **Bom**: Mockar apenas dependências externas
```pascal
mock ApiExterna.buscarDados thenReturn dados;
// Testar lógica interna sem mocks
```

### 2. Use Nomes Descritivos

❌ **Ruim**:
```pascal
test "teste 1"
begin
    mock A.b thenReturn 1;
    // ...
end;
```

✅ **Bom**:
```pascal
test "EmailService deve retornar sucesso ao enviar email"
begin
    mock EmailService.enviarEmail thenReturn true;
    // ...
end;
```

### 3. Sempre Verifique Chamadas Importantes

✅ **Bom**:
```pascal
test "Deve chamar serviço de log"
begin
    // GIVEN
    mock LogService.registrar thenReturn true;
    
    // WHEN
    processarPedido();
    
    // THEN
    verify LogService.registrar;
end;
```

### 4. Mock em GIVEN, Verify em THEN

```pascal
test "Padrão correto"
begin
    // GIVEN - Setup de mocks
    mock Servico.metodo thenReturn valor;
    
    // WHEN - Ação
    executar();
    
    // THEN - Verificações
    verify Servico.metodo;
end;
```

## Limitações Atuais

1. **Parâmetros**: Não há matching de parâmetros específicos
2. **Contagem**: Não verifica quantas vezes foi chamado
3. **Ordem**: Não verifica ordem de chamadas
4. **Spy**: Não há suporte a spies (mock parcial)
5. **Stub**: Apenas mocking completo

## Roadmap Futuro

### Versão 2.1
- Mock com matching de parâmetros
- Verificação de contagem de chamadas
- Spy (mock parcial)

### Versão 2.2
- Mock de propriedades
- Mock de eventos
- Verificação de ordem de chamadas

### Versão 3.0
- Mock automático de interfaces
- Geração de mocks via annotations
- Integração com frameworks de teste

## Comparação com Outros Frameworks

| Recurso | NeoObjectPascal | Mockito | Moq | Jest |
|---------|----------------|---------|-----|------|
| Mock Básico | ✅ | ✅ | ✅ | ✅ |
| Verify | ✅ | ✅ | ✅ | ✅ |
| Param Matching | ❌ | ✅ | ✅ | ✅ |
| Call Count | ❌ | ✅ | ✅ | ✅ |
| Spy | ❌ | ✅ | ✅ | ✅ |
| Order Verification | ❌ | ✅ | ✅ | ✅ |

## Exemplos Completos

### Teste de Sistema de Pagamento

```pascal
class PagamentoService
    public function processar(valor: Integer): Boolean
    begin
        // Chamada real a gateway de pagamento
        return true;
    end;
end;

class CarrinhoCompras
    var pagamento: PagamentoService;
    
    constructor Create(p: PagamentoService)
    begin
        self.pagamento := p;
    end;
    
    public function finalizar(total: Integer): Boolean
    begin
        return self.pagamento.processar(total);
    end;
end;

test "Carrinho deve processar pagamento com sucesso"
begin
    // GIVEN - Dado um serviço de pagamento mockado
    mock PagamentoService.processar thenReturn true;
    
    var pagamento: PagamentoService;
    pagamento := new PagamentoService();
    
    var carrinho: CarrinhoCompras;
    carrinho := new CarrinhoCompras(pagamento);
    
    // WHEN - Quando finalizamos a compra
    var sucesso: Boolean;
    sucesso := carrinho.finalizar(100);
    
    // THEN - Então deve retornar sucesso
    expect(sucesso).toBeTrue();
    
    // E o serviço de pagamento deve ter sido chamado
    verify PagamentoService.processar;
end;

test "Carrinho deve lidar com falha no pagamento"
begin
    // GIVEN - Dado um serviço de pagamento que falha
    mock PagamentoService.processar thenReturn false;
    
    var pagamento: PagamentoService;
    pagamento := new PagamentoService();
    
    var carrinho: CarrinhoCompras;
    carrinho := new CarrinhoCompras(pagamento);
    
    // WHEN - Quando finalizamos a compra
    var sucesso: Boolean;
    sucesso := carrinho.finalizar(100);
    
    // THEN - Então deve retornar falha
    expect(sucesso).toBeFalse();
end;
```

## Conclusão

O sistema de mocking do NeoObjectPascal oferece recursos essenciais para testes unitários profissionais, permitindo isolar unidades de código e testar diferentes cenários de forma previsível e eficiente.

**Recursos principais:**
- ✅ Mock de funções isoladas
- ✅ Mock de métodos de classes
- ✅ Mock de métodos públicos e privados
- ✅ Verificação de chamadas
- ✅ Integração com framework de testes
- ✅ Sintaxe simples e intuitiva

---

**Versão:** 2.0  
**Data:** Outubro 2025
