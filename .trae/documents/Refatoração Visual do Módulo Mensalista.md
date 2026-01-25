Vou corrigir o erro de template e refatorar completamente a interface para seguir o padrão visual "Premium" de Agendamentos.

### 1. Backend: Dados para o Dashboard (KPIs)
*   **Criar DTO**: `MensalistaResumoDTO` para transportar os totais.
*   **Atualizar Repositórios**:
    *   `MensalistaRepository`: Somar valor mensal de ativos.
    *   `PagamentoMensalistaRepository`: Somar pagamentos do mês atual.
*   **Atualizar Service/Controller**:
    *   Calcular e enviar o objeto `resumo` para a view.

### 2. Frontend: Refatoração Visual (`mensalista/list.html`)
*   **Novo Layout**: Substituir a tabela por **Cards Flutuantes** (`.item-row`), idênticos aos de Agendamentos.
*   **Estilo**: Importar o CSS sofisticado (efeitos hover, badges, fontes).
*   **Dashboard**: Adicionar 3 Cards no topo:
    *   🔵 **Ativos**: Quantidade de contratos ativos.
    *   🟢 **Receita Prevista**: Soma dos valores mensais.
    *   🟠 **Recebido Mês**: Total já pago no mês corrente.
*   **Lista de Mensalistas**:
    *   **Dia**: Destaque para o dia de vencimento.
    *   **Cliente**: Nome e telefone com ícone.
    *   **Status**: Badge dinâmico (Em Dia / Pendente).
    *   **Ações**: Menu dropdown (Editar, Pagar, Excluir).

### 3. Correção de Erro
*   A reescrita completa do arquivo HTML corrigirá qualquer tag mal fechada ou atributo inválido que causou o erro de parsing anterior.
