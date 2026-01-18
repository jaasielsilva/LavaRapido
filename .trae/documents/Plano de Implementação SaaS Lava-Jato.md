# Plano de Implementação Refinado - SaaS Lava-Jato

Este plano detalha a criação do sistema com ênfase rigorosa no controle de acesso e isolamento de dados (Multi-tenancy).

## 1. Configuração e Estrutura
*   **Tecnologias**: Java 17, Spring Boot 3.x, Thymeleaf, Bootstrap 5.
*   **Dependências**: Adicionar/Validar Security, Data JPA, Validation, DevTools, MySQL Driver.
*   **Configuração**: Ajustar `application.yml` para conexão MySQL e JPA.

## 2. Modelagem de Dados e Segurança (Core)
*   **Entidades**:
    *   `Empresa`: Cadastro das lojas.
    *   `Usuario`: Vinculado a uma `Empresa` (exceto MASTER que pode ser nulo ou empresa "raiz").
    *   `Cliente` e `Veiculo`: Vinculados obrigatòriamente a uma `Empresa`.
*   **Enums de Perfil**:
    *   `MASTER`: Superusuário da plataforma (Vê tudo).
    *   `ADMIN`: Administrador da lavanderia (Vê apenas sua empresa).
    *   `FUNCIONARIO`: Operador da lavanderia (Vê apenas sua empresa).

## 3. Implementação da Segurança (Spring Security)
*   **CustomUserDetailsService**: Carregar usuários do banco e atribuir Roles (`ROLE_MASTER`, `ROLE_ADMIN`, `ROLE_FUNCIONARIO`).
*   **SecurityConfig**:
    *   Bloquear todas as requisições não autenticadas.
    *   Restringir rotas de gestão de empresas (`/empresas/**`) apenas para `ROLE_MASTER`.
    *   Configurar Login Page customizada e Logout.
*   **Isolamento de Dados (Lógica Multi-tenant)**:
    *   Criar uma classe utilitária `SecurityUtils` para recuperar o usuário logado e sua empresa.
    *   **Regra de Ouro**: Nos Services (`ClienteService`, `VeiculoService`), verificar a role:
        *   Se **MASTER**: Permite buscar todos os registros (findAll).
        *   Se **ADMIN/FUNCIONARIO**: O sistema **força** o filtro `where empresa_id = ?` usando o ID da empresa do usuário logado. Nenhum dado de outra empresa será retornado.

## 4. Camadas de Aplicação
*   **Repositories**: Métodos customizados como `findAllByEmpresa(Empresa empresa)`.
*   **Services**:
    *   `EmpresaService`: Apenas MASTER acessa.
    *   `UsuarioService`: ADMIN cadastra usuários apenas para sua própria empresa.
    *   `ClienteService/VeiculoService`: Implementam a lógica de isolamento descrita acima.
*   **Controllers**: Expor endpoints para as Views.

## 5. Interface Gráfica (Thymeleaf + Bootstrap 5)
*   **Layout Responsivo**: Menu lateral adaptável ao perfil (MASTER vê menu "Empresas", outros não).
*   **Telas**:
    *   Dashboard (KPIs gerais para Master, KPIs da loja para Admin).
    *   CRUDs padronizados (Listagem, Cadastro, Edição).

## 6. Entregáveis e Inicialização
*   Script SQL e `CommandLineRunner` para criar o **primeiro usuário MASTER** (email: `master@lavajato.com`, senha: `admin`) automaticamente ao rodar o projeto.
