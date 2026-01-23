Para corrigir a visualização do Dashboard e unificar "Serviços Avulsos" e "Agendamentos", implementarei as seguintes alterações no arquivo [FinanceiroService.java](file:///d:/Projetos/lavajato/src/main/java/br/com/lavajato/service/financeiro/FinanceiroService.java):

1. **Unificação de Receita no Resumo (`calcularResumoFinanceiro`)**:

   * No mapa `resumo`, a chave `"receitaServicos"` passará a armazenar a soma de `receitaServicos` (avulsos) + `receitaAgendamentos`.

   * A chave `"qtdServicos"` passará a ser a soma de `servicos.size()` + `agendamentos.size()`.

   * Essa alteração fará com que o card "Serviços" no dashboard mostre o valor total consolidado (R$ 1.010,00 + R$ 80,00 = R$ 1.090,00), eliminando a dúvida sobre a diferença.

2. **Unificação no Balanço Mensal (`gerarBalancoMensal`)**:

   * Na lista `BalancoMensalDTO`, o campo `receitaServicos` também passará a somar `recServicos` + `recAgendamentos`.

   * Isso garantirá que a tabela detalhada "Balanço Mensal Detalhado" também reflita essa visão unificada, evitando que o usuário veja colunas separadas que somam valores diferentes do total.

3. **Resultado Esperado**:

   * O card "Serviços" mostrará "R$ 1.090,00".

   * A "Receita Total" continuará sendo "R$ 1.090,00".

   * A confusão visual será resolvida, pois a soma dos cards (Serviços + Produtos + Extras) baterá exatamente com a Receita Total.

