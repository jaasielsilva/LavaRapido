O problema de alinhamento ocorre porque o CSS global do projeto define que os elementos dentro de qualquer `.card` devem ficar alinhados à esquerda e encolhidos (`align-items: flex-start`), impedindo que o rodapé ocupe a largura total.

**Solução Técnica:**
1.  **Corrigir Alinhamento (Extremidades):**
    *   Adicionar a classe `w-100` na `div` do rodapé (Footer). Isso forçará o rodapé a ocupar 100% da largura do card, permitindo que o `justify-content-between` funcione corretamente e empurre o tempo para a esquerda e o valor para a direita.
    *   Adicionar `w-100` também no corpo do card para garantir consistência.

2.  **Compactar Altura (Mais um pouco):**
    *   Reduzir a margem do Badge: de `mb-2` para `mb-1`.
    *   Remover a margem da Descrição: de `mb-1` para `mb-0` (zerada).
    *   Isso vai "espremer" levemente o conteúdo vertical, atendendo ao pedido de diminuir um pouco mais a altura.

Essa abordagem resolve a causa raiz do conflito com o CSS global e ajusta o design final.