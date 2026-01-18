Vou implementar o módulo de **Produtos** completo, incluindo a carga inicial com **exatamente os 8 produtos** que você listou, garantindo que o visual fique idêntico.

**Etapas da Implementação:**

1.  **Backend (Estrutura):**
    *   **Enums**:
        *   `CategoriaProduto`: Configurada com as cores exatas (Cera=Amarelo, Shampoo=Azul, Aromatizante=Rosa, Acessório=Verde, etc.).
        *   `UnidadeMedida`: Unidade, Litro, ml, etc.
    *   **Entidade `Produto`**: Com todos os campos (preços, estoque, etc.).
    *   **Service & Repository**: Lógica de negócios.
    *   **Data Initializer**: Vou criar um método para cadastrar automaticamente a "Cera Cristalizadora", "Shampoo Automotivo", "Silicone Gel", etc., com os preços e estoques exatos que você passou.

2.  **Frontend (Visual):**
    *   **Sidebar**: Adicionar "Produtos" no menu.
    *   **Tela `produtos/list.html`**:
        *   **Stats Cards**: Total, Estoque Baixo, Valor.
        *   **Grid de Cards**:
            *   Badge da categoria com a cor correta.
            *   Nome e Descrição fiéis.
            *   Preço destacado em azul.
            *   Linha de Estoque e Seletor de Quantidade (`- 1 +`) alinhados.
            *   Botão "Adicionar ao Carrinho" largura total (full width) embaixo.
    *   **Modal "Novo Produto"**:
        *   Idêntico à imagem: Fundo escuro, campos organizados em linhas (Preço Venda/Custo, Estoque/Mínimo).

3.  **Estilização**:
    *   Ajuste fino de cores para bater com o print (ex: Aromatizante rosa, Acessório verde).

Vou começar criando a estrutura e os dados de exemplo.