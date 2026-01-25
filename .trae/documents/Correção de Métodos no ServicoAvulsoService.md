Vou corrigir o erro de compilação e adicionar os métodos faltantes no `ServicoAvulsoService`.

### 1. Ajuste no `ServicoAvulsoService`
*   **Refatorar `listarPaginado`**: Alterar a assinatura para aceitar `Pageable` diretamente, permitindo que o Controller defina a ordenação.
*   **Criar `buscarPorId(Long id)`**: Método para recuperar um serviço ou lançar exceção.
*   **Criar `excluir(Long id)`**: Método para deletar um serviço.
*   **Criar `concluirServico(Long id)`**: Método de conveniência que chama `alterarStatus` com o status `CONCLUIDO`.

### 2. Validação
*   Essas alterações alinharão o Service com o Controller atualizado e resolverão o erro "Unresolved compilation problem".
