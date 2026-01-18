Vou implementar um fluxo unificado para "Novo Contrato" que cria a Empresa e o Usuário Admin simultaneamente.

### **1. Backend (DTO e Controller)**
*   **DTO `NovoContratoDTO`**: Criar uma classe simples para transportar os dados do formulário unificado:
    *   Dados da Empresa: `nomeEmpresa`, `cnpj`, `MultipartFile logo`.
    *   Dados do Admin: `nomeAdmin`, `emailAdmin`, `senhaAdmin`.
*   **`EmpresaController`**:
    *   Novo endpoint GET `/empresas/novo-contrato`: Exibe o formulário unificado.
    *   Novo endpoint POST `/empresas/salvar-contrato`:
        1.  Processa o upload da logo.
        2.  Cria e salva a entidade `Empresa`.
        3.  Cria e salva a entidade `Usuario` (Admin) vinculada a essa empresa recém-criada.
        4.  Redireciona para a lista de empresas com mensagem de sucesso.

### **2. Frontend (View)**
*   **Nova View `empresa/contrato-form.html`**:
    *   Formulário dividido em duas seções: "Dados da Empresa" e "Dados do Administrador".
    *   Campos: Nome Empresa, CNPJ, Logo (Upload), Nome Admin, Email (Login), Senha.
*   **Atualização em `empresa/list.html`**:
    *   Adicionar um botão de destaque "Novo Contrato" (atalho rápido) ao lado de "Nova Empresa".
    *   (Opcional) Adicionar um botão pequeno "Criar Admin" na lista de empresas existentes, caso alguma tenha sido criada sem admin (para cobrir o cenário legado).

### **3. Fluxo de Execução**
1.  Criar o DTO `NovoContratoDTO`.
2.  Implementar a lógica no `EmpresaController` (aproveitando o `EmpresaService` e `UsuarioService` existentes).
3.  Criar o HTML do formulário unificado.
4.  Adicionar o botão na listagem.
