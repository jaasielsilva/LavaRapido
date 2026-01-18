Vou implementar a funcionalidade de Carrinho de Compras e Venda Rápida (PDV Simplificado).

**Backend:**
1.  **DTOs**:
    *   `ItemVendaDTO`: Produto ID, Quantidade, Preço Unitário.
    *   `VendaDTO`: Cliente ID (opcional), Lista de Itens, Forma de Pagamento.
2.  **Entidades**:
    *   `Venda`: Data, Cliente (opcional), Valor Total, Forma Pagamento (Enum), Status.
    *   `ItemVenda`: Venda, Produto, Quantidade, Preço Unitário.
3.  **Controller**:
    *   `VendaController`: Endpoint `POST /vendas/salvar` para processar a venda via AJAX.

**Frontend (JavaScript + Thymeleaf):**
1.  **Lógica do Carrinho (Client-Side)**:
    *   Usar `localStorage` ou variáveis JS globais para manter o estado do carrinho sem recarregar a página.
    *   Função `adicionarAoCarrinho(produtoId, nome, preco)`: Atualiza a lista e o contador no botão "Vender".
    *   Função `renderizarCarrinho()`: Preenche o modal de Venda com os itens selecionados.
    *   Função `atualizarTotal()`: Calcula o total dinamicamente.
2.  **Modal de Venda (Checkout)**:
    *   **Cliente**: Select com busca (simples) ou dropdown padrão carregando clientes do banco.
    *   **Lista de Itens**: Tabela simples com Nome, Qtd, Preço e botão de remover.
    *   **Pagamento**: Select com opções (Dinheiro, PIX, Cartão).
    *   **Botão Finalizar**: Envia o JSON para o backend via `fetch`, limpa o carrinho e exibe mensagem de sucesso (SweetAlert).

**Fluxo do Usuário:**
1.  Na tela de Produtos, usuário clica em "Adicionar" no card.
2.  Item vai para o "carrinho virtual" (botão "Vender (X)" no topo atualiza).
3.  Usuário clica em "Vender (X)".
4.  Abre o Modal "Finalizar Venda".
5.  Usuário seleciona Cliente (opcional) e Forma de Pagamento.
6.  Clica em "Finalizar Venda" -> Venda é salva, estoque é baixado.

Vou começar criando as estruturas de dados para a Venda.