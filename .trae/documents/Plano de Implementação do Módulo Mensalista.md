Vou implementar o módulo **Mensalista** completo, com integração financeira, controle de acesso e interface padronizada.

### 1. Backend: Entidades e Persistência
*   **Criar Entidade `Mensalista`**:
    *   Relacionamento com `Cliente` e `Empresa` (Multi-tenancy).
    *   Campos: `diaVencimento`, `valorMensal`, `dataUltimoPagamento`, `status` (Ativo/Inativo), `ativo` (Soft Delete).
*   **Criar Entidade `PagamentoMensalista`**:
    *   Para histórico de pagamentos.
    *   Campos: `mensalista`, `dataPagamento`, `valorPago`, `competencia` (Mês/Ano de referência).
*   **Repositórios**:
    *   `MensalistaRepository`: Com filtro por Empresa e Status Ativo.
    *   `PagamentoMensalistaRepository`: Para buscar histórico.

### 2. Backend: Regra de Negócio (Service)
*   **`MensalistaService`**:
    *   **CRUD**: Salvar/Editar mensalistas com isolamento por empresa.
    *   **Integração Financeira**: Método `registrarPagamento` que:
        1.  Salva o registro em `PagamentoMensalista`.
        2.  Cria automaticamente um `LancamentoFinanceiro` (Tipo ENTRADA, Categoria "Mensalidade") para refletir no fluxo de caixa.
        3.  Atualiza a `dataUltimoPagamento` do mensalista.

### 3. Backend: Controlador e Segurança
*   **`MensalistaController`**:
    *   Restrito a **ADMIN** e **MASTER** (`@PreAuthorize`).
    *   Endpoints para Listagem, Cadastro, Edição e Registro de Pagamento.
    *   Tratamento de erros amigável retornando mensagens para o SweetAlert2.

### 4. Frontend: Interfaces Padronizadas
*   **Sidebar (`layout.html`)**: Adicionar item "Mensalistas" no menu "Gestão & Financeiro" (visível apenas para ADMIN/MASTER).
*   **Listagem (`mensalista/list.html`)**:
    *   Tabela estilizada com status e ações.
    *   Botão **"Registrar Pagamento"** que abre um Modal SweetAlert2 para confirmar valor e data.
*   **Formulário (`mensalista/form.html`)**:
    *   Seleção de Cliente (Autocomplete ou Select), Dia de Vencimento e Valor.
    *   Validações com feedback visual.

### 5. Execução
1.  Criar estrutura de pacotes e classes Java.
2.  Criar templates HTML (list e form) seguindo o padrão visual do sistema.
3.  Implementar lógica de integração financeira.
4.  Atualizar menu lateral.
