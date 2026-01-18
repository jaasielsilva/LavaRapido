Vou adicionar o "Empty State" (estado vazio) nas páginas de Clientes, Produtos e Catálogo, para quando não houver registros cadastrados.

**Implementação:**

1.  **Clientes (`cliente/list.html`):**
    *   Envolver a lista de clientes (`.client-list`) e a paginação em um `th:if="${!clientes.empty}"`.
    *   Adicionar um bloco `th:if="${clientes.empty}"` logo abaixo do header, com um design centralizado, ícone de usuários e a mensagem "Nenhum cliente encontrado".

2.  **Produtos (`produto/list.html`):**
    *   Envolver o grid de produtos (`.row.g-3`) em um `th:if="${!produtos.empty}"`.
    *   Adicionar um bloco `th:if="${produtos.empty}"` com ícone de caixa aberta e a mensagem "Nenhum produto encontrado".

3.  **Catálogo (`catalogo/list.html`):**
    *   Na aba "Todos" (`#all`): Adicionar verificação `th:if="${servicosPorCategoria.empty}"` para exibir "Nenhum serviço encontrado" com ícone de etiquetas/tags.
    *   Nas abas por categoria: Já existe um `th:if="${servicosPorCategoria.get(cat) == null}"`, vou apenas garantir que a mensagem esteja padronizada com o mesmo estilo visual dos outros.

**Estilo Padronizado:**
Usarei um container centralizado com `py-5`, ícone grande (`fa-3x`) com cor muted e texto descritivo, mantendo a consistência visual do tema escuro.