Vou refazer a página de Mensalistas para corrigir o visual "branco misturado" e alinhar com o tema Dark do sistema, e implementar a nova regra de Fidelidade.

### 1. Refatoração Visual (Mensalistas)
*   **Problema**: A página atual pode estar herdando estilos claros ou conflitantes (ex: fundo branco em cards) que não combinam com o `style.css` (Dark Theme).
*   **Solução**:
    *   **Layout**: Utilizar o mesmo Grid System (`.cards` para KPIs e `.list-container` para itens) já definido no `style.css` e usado em Agendamentos.
    *   **Cores**: Forçar classes `bg-dark`, `border-secondary`, `text-light` (Bootstrap) ou as classes customizadas (`.card.dark`, `.item-row`) para garantir o fundo escuro `#0f1623`.
    *   **Estrutura**: Manter o layout de Cards Flutuantes (aprovado na intenção anterior), mas garantindo que as classes CSS estejam corretas para o tema escuro.

### 2. Nova Regra de Fidelidade (Backend)
*   **Lógica**: "A cada 4 meses, caso não complete 10 lavagens, zera os pontos".
*   **Alteração no Banco (`Cliente`)**:
    *   Adicionar campo `dataInicioFidelidade` (LocalDate) na entidade `Cliente`.
*   **Alteração no Serviço (`ClienteService.incrementarFidelidade`)**:
    1.  Verificar se `quantidadeLavagens == 0`. Se sim, `dataInicioFidelidade = hoje`.
    2.  Se `quantidadeLavagens > 0`:
        *   Calcular meses entre `dataInicioFidelidade` e `hoje`.
        *   Se `meses > 4`:
            *   **Expirou**: Resetar `quantidadeLavagens = 1` (a lavagem atual conta como a primeira do novo ciclo) e `dataInicioFidelidade = hoje`.
            *   *Feedback*: Talvez logar ou notificar que zerou? (Por enquanto, apenas lógica silenciosa).
        *   Se `meses <= 4`:
            *   Incrementar `quantidadeLavagens`.
*   **Alteração no Resgate (`ClienteService.resgatarFidelidade`)**:
    *   Ao subtrair 10 pontos:
        *   Se sobrar pontos (ex: tinha 12, ficou 2): Manter a `dataInicioFidelidade` original? Ou o ciclo reinicia?
        *   *Interpretação*: Geralmente o ciclo de validade é para *atingir* a meta. Uma vez atingida, resgata. O "novo ciclo" de 4 meses começa para as *próximas* 10 lavagens.
        *   **Decisão**: Ao resgatar, se o saldo cair para < 10, podemos resetar a `dataInicioFidelidade` para `hoje` (assumindo que o saldo restante são pontos "novos" ou que o ciclo reinicia ao ganhar o prêmio).
        *   *Refinamento com base no pedido*: "na 11 em até 4 meses ele ganha". Se ele fez 10 em 3 meses, ganhou. A 11ª começa um novo ciclo de 4 meses? Sim.
        *   Lógica de Resgate: Subtrair 10. Se saldo > 0, `dataInicioFidelidade` = data da 11ª lavagem (precisaria guardar histórico?).
        *   **Simplificação Robusta**: Ao resgatar, reseta `dataInicioFidelidade` para `LocalDate.now()` (data do resgate/uso). Assim, o cliente tem mais 4 meses para juntar as próximas 10.

### 3. Plano de Execução
1.  **Backend Fidelidade**:
    *   Adicionar `dataInicioFidelidade` em `Cliente.java`.
    *   Atualizar lógica em `ClienteService.java` (incrementar e resgatar).
2.  **Frontend Mensalista**:
    *   Reescrever `mensalista/list.html` usando estritamente as classes do `style.css` (`.card`, `.item`, `.badge`) para garantir consistência visual Dark.
