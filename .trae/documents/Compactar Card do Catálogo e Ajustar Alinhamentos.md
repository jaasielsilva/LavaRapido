Vou ajustar o CSS e as classes utilitárias do card para reduzir sua altura e garantir que o valor fique corretamente posicionado à direita.

**Passos da Implementação:**

1.  **Reduzir Espaçamentos Internos (Padding/Margin):**
    *   Diminuir o espaçamento abaixo do Badge (de `mb-3` para `mb-2`).
    *   Diminuir o espaçamento abaixo da Descrição (de `mb-3` para `mb-0` ou `mb-1`), aproximando o conteúdo do rodapé.
    *   Reduzir a altura do Rodapé (Footer), alterando o padding vertical de `py-3` para `py-2`.
    *   Ajustar o padding do Corpo do Card para `p-3` (laterais) e `py-2` (vertical) para deixá-lo mais compacto.

2.  **Manter Alinhamento do Valor:**
    *   Confirmar que o rodapé continua usando `justify-content-between` para manter o Tempo na extrema esquerda e o Valor na extrema direita, mas agora em um container mais "fino" (menos alto).

Isso deixará o card mais "slim", removendo o excesso de espaço vazio vertical.