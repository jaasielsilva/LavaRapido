Vou implementar o módulo de **Agendamentos** completo, criando a estrutura de backend e a interface idêntica ao solicitado.

### **1. Backend (Estrutura de Dados)**
*   **Enum `StatusAgendamento`**: Definir os estados (`AGENDADO`, `EM_ANDAMENTO`, `CONCLUIDO`, `CANCELADO`).
*   **Entidade `Agendamento`**:
    *   `id`, `data` (Data/Hora), `veiculo` (Relacionamento), `servicos` (Texto livre ou lista), `valor`, `status`, `observacoes`.
*   **Repository & Service**:
    *   Criar `AgendamentoRepository` e `AgendamentoService`.
    *   Implementar filtro de segurança (Multi-tenancy): Usuários só veem agendamentos da sua própria empresa (exceto Master).
*   **Controller**:
    *   Endpoints para listar, salvar (novo/editar) e alterar status (Iniciar, Concluir, Cancelar).

### **2. Frontend (Interface)**
*   **Menu Lateral (`layout.html`)**: Adicionar o item "Agendamentos" com ícone de calendário.
*   **Tela de Listagem (`agendamento/list.html`)**:
    *   Reproduzir fielmente o design "Dark" da imagem.
    *   Cards largos com badges de status coloridos.
    *   Exibição de ícones para Data, Cliente e Veículo.
    *   Botões de ação rápida (`Iniciar`, `Concluir`, `Cancelar`) dinâmicos baseados no status atual.
*   **Tela de Cadastro (`agendamento/form.html`)**:
    *   Formulário para criar/editar agendamentos (seleção de veículo, data, serviços e valor).

### **3. Fluxo de Trabalho**
1.  Criar o pacote `agendamento` e as classes Java (`Model`, `Repository`, `Service`, `Controller`).
2.  Atualizar o `layout.html` para incluir o link no menu.
3.  Criar a view `list.html` com o CSS customizado para os cards.
4.  Criar a view `form.html` para permitir o cadastro de novos agendamentos.
5.  Compilar e validar.
