# Plano de Inicialização e Verificação do Sistema

Para atender à sua solicitação, vamos preparar o sistema para que você possa validar o funcionamento multi-empresa imediatamente.

## 1. Popular o Banco de Dados (DataInitializer)
Vou atualizar a classe `DataInitializer` para criar automaticamente o cenário que você descreveu ao iniciar o sistema:

*   **Empresa 1**: "Lava Jato Centro"
    *   **Admin**: `admin.centro@lavajato.com` / `123456`
    *   **Funcionário**: `func.centro@lavajato.com` / `123456`
    *   **Dados**: 1 Cliente ("João Centro") e 1 Veículo.
*   **Empresa 2**: "Estética VIP Shopping"
    *   **Admin**: `admin.vip@lavajato.com` / `123456`
    *   **Funcionário**: `func.vip@lavajato.com` / `123456`
    *   **Dados**: 1 Cliente ("Maria VIP") e 1 Veículo.

Isso permitirá que você logue com `admin.centro` e verifique que **NÃO** consegue ver os dados da "Maria VIP", provando o isolamento.

## 2. Iniciar a Aplicação
Vou executar o comando para subir o servidor Spring Boot na porta 8080.

## 3. Roteiro de Teste (Para você executar)
Após a aplicação iniciar, você poderá:
1.  Acessar `http://localhost:8080/login`.
2.  Logar com o **MASTER** (`master@lavajato.com`) para ver ambas as empresas.
3.  Logar com `admin.centro@lavajato.com` e confirmar que vê apenas o cliente "João Centro".
4.  Logar com `admin.vip@lavajato.com` e confirmar que vê apenas o cliente "Maria VIP".
