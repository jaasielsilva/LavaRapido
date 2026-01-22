Vou implementar um sistema visual de alerta de atraso para serviços em andamento.

### 1. Alterações no Frontend (Templates)
O foco principal será na tela de listagem de Serviços Avulsos (`servico-avulso/list.html`) e no `dashboard.html`.

*   **Exibição do Tempo Estimado**:
    *   Vou adicionar o tempo estimado (ex: `30m`) ao lado do nome do serviço, usando um ícone de relógio amarelo (`text-warning`).
    *   Isso será feito buscando `servico.servico.tempoMinutos`.

*   **Estilização (CSS)**:
    *   **Estado Normal (Dentro do Prazo)**: Adicionar classe `.em-prazo`.
        *   Comportamento: Borda azul ao passar o mouse (`hover`).
    *   **Estado Atrasado (Estourou o Tempo)**: Adicionar classe `.atrasado`.
        *   Comportamento: Borda vermelha pulsante (`animation: pulse-red`) constante ou no hover, conforme solicitado "fique vermelho pulsante passando o mouse em cima", mas para melhor UX, farei o efeito visual ser claro. O "pulsante" será implementado com `@keyframes`.

*   **Lógica Dinâmica (JavaScript)**:
    *   Como o tempo passa sem recarregar a página, criarei um script que roda a cada 30 segundos ou 1 minuto.
    *   O script irá iterar sobre os cards com status `EM_ANDAMENTO`.
    *   Lerá o atributo `data-inicio` e `data-tempo-estimado` (que adicionarei ao HTML).
    *   Calculará a diferença entre `Agora` e `Inicio`.
    *   Se `Diferenca > TempoEstimado`: Adiciona classe `atrasado`, remove `em-prazo`.
    *   Caso contrário: Mantém `em-prazo`.

### 2. Estrutura de Implementação
1.  **Atualizar `servico-avulso/list.html`**:
    *   Injetar `data-inicio` (timestamp ISO) e `data-tempo-estimado` (minutos) nos elementos `.item-row`.
    *   Adicionar o span visual do tempo estimado na UI.
    *   Adicionar o CSS para `.atrasado` (animação) e `.em-prazo`.
    *   Adicionar o JavaScript de monitoramento.
2.  **Atualizar `dashboard.html`**:
    *   Replicar a lógica visual simplificada para a lista de "Serviços de Hoje" se necessário, embora o foco principal pareça ser a tela de operação (listagem). Farei também no dashboard para consistência.

### 3. Detalhes Visuais
*   **Normal**: Borda padrão, Hover -> Borda Azul (`#38bdf8`).
*   **Atrasado**: Borda Vermelha (`#ef4444`), Sombra Vermelha Pulsante (`box-shadow` animado).

Vou priorizar a implementação completa em `servico-avulso/list.html` que é a tela operacional descrita.
