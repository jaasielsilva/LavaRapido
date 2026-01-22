Vou implementar um sistema de Fidelidade onde a cada 10 lavagens (Agendadas ou Avulsas), o cliente registrado ganha 1 grátis.

### 1. Alterações no Banco de Dados e Entidades
*   **Cliente**: Adicionar campo `quantidadeLavagens` (Integer, padrão 0) para controlar a pontuação.
*   **Agendamento** e **ServicoAvulso**: Adicionar campo `usoFidelidade` (Boolean, padrão false) para indicar quando o serviço foi o "prêmio" (não pontua e tem valor R$ 0,00).

### 2. Regras de Negócio (Backend)
*   **Pontuação**:
    *   Ao concluir um serviço (`CONCLUIDO`) em `AgendamentoService` ou `ServicoAvulsoService`:
    *   Verificar se o serviço **NÃO** é um uso de fidelidade (`!usoFidelidade`).
    *   Verificar se existe um **Cliente cadastrado** vinculado.
    *   Incrementar `quantidadeLavagens` do cliente (+1).
*   **Resgate**:
    *   Ao criar um novo serviço, se o cliente tiver `quantidadeLavagens >= 10`, permitir marcar como `usoFidelidade`.
    *   Se marcado:
        *   O valor do serviço será forçado para **R$ 0,00**.
        *   A `quantidadeLavagens` do cliente será resetada (subtraindo 10).
        *   O serviço não gerará pontos ao ser concluído.

### 3. Interface do Usuário (Frontend)
*   **Cadastro de Agendamento/Serviço Avulso**:
    *   Ao selecionar um cliente, exibir um alerta visual se ele tiver 10+ pontos: *"🎉 Cliente tem uma lavagem grátis disponível!"*.
    *   Adicionar um checkbox **"Usar Fidelidade (Grátis)"**.
*   **Listagem de Clientes**:
    *   Exibir uma coluna ou ícone mostrando o progresso (ex: "Fidelidade: 7/10").

### 4. Fluxo de Implementação
1.  Atualizar as Entidades (`Cliente`, `Agendamento`, `ServicoAvulso`).
2.  Criar lógica de incremento nos Services (`alterarStatus`).
3.  Criar lógica de resgate nos Services (`salvar`).
4.  Atualizar as telas de cadastro e listagem.
