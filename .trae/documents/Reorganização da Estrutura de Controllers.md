# Plano de Reorganização da Camada de Controladores

Vou reestruturar o pacote `br.com.lavajato.controller` dividindo-o em subpacotes temáticos para melhor organização, conforme solicitado.

## 1. Criação de Subpacotes e Movimentação de Arquivos

*   **Auth**:
    *   Mover `AuthController.java` para `auth/LoginController.java`.
    *   Renomear classe de `AuthController` para `LoginController`.
    *   Atualizar pacote para `br.com.lavajato.controller.auth`.

*   **Dashboard**:
    *   Mover `DashboardController.java` para `dashboard/DashboardController.java`.
    *   Atualizar pacote para `br.com.lavajato.controller.dashboard`.

*   **Empresa**:
    *   Mover `EmpresaController.java` para `empresa/EmpresaController.java`.
    *   Atualizar pacote para `br.com.lavajato.controller.empresa`.

*   **Usuario**:
    *   Mover `UsuarioController.java` para `usuario/UsuarioController.java`.
    *   Atualizar pacote para `br.com.lavajato.controller.usuario`.

*   **Cliente**:
    *   Mover `ClienteController.java` para `cliente/ClienteController.java`.
    *   Atualizar pacote para `br.com.lavajato.controller.cliente`.

*   **Veiculo**:
    *   Mover `VeiculoController.java` para `veiculo/VeiculoController.java`.
    *   Atualizar pacote para `br.com.lavajato.controller.veiculo`.

## 2. Validação

*   Verificar se todas as referências de pacote foram atualizadas corretamente nos arquivos.
*   Executar o build para garantir que a aplicação continua funcionando.
