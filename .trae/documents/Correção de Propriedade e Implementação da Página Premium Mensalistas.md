Vou corrigir o erro de propriedade inexistente `data` em `ServicoAvulso` e implementar a Página Premium de Mensalistas.

### 1. Correção em `ServicoAvulso`
*   **Erro**: O template `servico-avulso/list.html` refere-se a `s.data`, mas a entidade tem `dataCriacao`.
*   **Ação**: Renomear o campo na view de `data` para `dataCriacao`.

### 2. Implementação da Página Premium de Mensalistas
*   **Local**: `templates/mensalista/list.html` (já existente, será aprimorada).
*   **Estrutura**:
    *   **Header**: Título, Subtítulo e Botão "Novo".
    *   **KPIs**: 4 Cards (Ativos, Receita, Recebido, Inadimplentes) alimentados pelo `resumo` do Controller.
    *   **Filtro**: Formulário GET com campo de busca.
    *   **Listagem**:
        *   Estilo "Premium" (Cards Flutuantes `item-row`).
        *   Colunas: Dia Venc., Cliente, Último Pagto, Status (Automático), Valor, Ações.
        *   Badge Status: Verde (Em dia), Vermelho (Pendente/Atrasado).
    *   **Paginação**: Bloco `<nav>` no rodapé.
    *   **Modal de Pagamento**: Estilizado, com JavaScript para preenchimento dinâmico.
*   **JavaScript**: Script para gerenciar o modal, mensagens SweetAlert e filtros.

### 3. Validação
*   O sistema continuará seguindo o padrão multiempresa e layout escuro.
