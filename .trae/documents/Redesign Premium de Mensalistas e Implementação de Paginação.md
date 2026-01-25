Vou recriar o módulo Mensalistas com o design premium de Agendamentos e implementar paginação em Mensalistas e Serviços Avulsos.

### 1. Mensalistas (Recriação Visual + Paginação)
*   **Controller (`MensalistaController`)**:
    *   Alterar o método `listar` para aceitar `Pageable` (padrão 10 itens).
    *   Atualizar o `model` para enviar um objeto `Page<Mensalista>` em vez de `List`.
*   **Service (`MensalistaService`)**:
    *   Criar método `listarPaginado(Pageable)` chamando o repositório.
*   **View (`mensalista/list.html`)**:
    *   **Substituição Completa**: Copiar a estrutura HTML/CSS de `agendamento/list.html` (Cards de KPI, Barra de Filtros, Listagem Flutuante).
    *   **Adaptação de Dados**: Mapear os campos de Agendamento para Mensalista (ex: Data -> Dia Vencimento, Veículo -> Cliente, Valor -> Valor Mensal).
    *   **Paginação**: Adicionar o bloco `<nav>` de paginação no rodapé, idêntico ao de Agendamentos.

### 2. Serviços Avulsos (Apenas Paginação)
*   **Controller (`ServicoAvulsoController`)**:
    *   Adicionar parâmetros `page` e `size` no método `listar`.
    *   Chamar o serviço paginado.
*   **Service (`ServicoAvulsoService`)**:
    *   Já possui `listarPaginado`. Verificar se está sendo usado corretamente.
*   **View (`servico-avulso/list.html`)**:
    *   Inserir o bloco de navegação `<nav>` (copiado de Agendamentos) no final da lista.
    *   Ajustar o loop `th:each` para iterar sobre `servicos.content` se passar a ser uma Page.

### 3. Execução
1.  Atualizar Backend de Mensalistas (Service/Controller).
2.  Atualizar Backend de Serviços Avulsos (Controller).
3.  Reescrever Frontend Mensalistas (Visual Premium + Paginação).
4.  Atualizar Frontend Serviços Avulsos (Adicionar Paginação).
