Vou implementar o módulo de **Serviços Avulsos** completo, incluindo a entidade, controller, visualização e o modal solicitado.

### **1. Backend (Estrutura de Dados)**
*   **Enum `TipoServico`**: Para padronizar a lista de serviços fornecida (Lavagem Simples, Completa, etc.) com seus preços e tempos base.
*   **Entidade `ServicoAvulso`**:
    *   Campos para suportar Cliente Cadastrado (relacionamento) OU Cliente Avulso (nome/placa manuais).
    *   Status: `EM_FILA`, `EM_ANDAMENTO`, `CONCLUIDO`.
    *   Data/Hora de entrada e conclusão.
*   **Repository & Service**:
    *   Métodos para listar do dia, calcular métricas (Em Fila, Concluídos Hoje, Faturamento Hoje).
*   **Controller**:
    *   Endpoints para a tela principal e para salvar o serviço (recebendo dados do modal).

### **2. Frontend (Interface)**
*   **Sidebar (`layout.html`)**: Adicionar "Serviços Avulsos" com ícone de chave inglesa (`fa-wrench`).
*   **Tela Principal (`servico-avulso/list.html`)**:
    *   **Cards de Métricas**: "Em Fila", "Concluídos Hoje", "Faturamento Hoje" (design escuro).
    *   **Listagem**: Lista de serviços com status diferenciado (design idêntico à imagem).
    *   **Modal de Novo Serviço**:
        *   Abas: "Cliente Cadastrado" (Select2/Dropdown) vs "Cliente Avulso" (Inputs manuais).
        *   Seleção de Serviços: Lista com Radio Buttons estilizados mostrando Nome, Tempo e Preço, conforme sua especificação.
        *   Botão "Registrar Serviço".

### **3. Fluxo de Trabalho**
1.  Criar `TipoServico` (Enum) e `ServicoAvulso` (Entity).
2.  Implementar `ServicoAvulsoRepository`, `Service` e `Controller`.
3.  Atualizar `layout.html`.
4.  Criar a view `list.html` contendo o HTML do Modal integrado.
