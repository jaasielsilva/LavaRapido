Vou transformar a listagem de agendamentos para usar o **mesmo design de cards** do catálogo e refinar o visual do formulário de novo agendamento.

### 1. Padronizar Agendamentos (`/agendamentos`) com o estilo do Catálogo
Vou substituir a lista atual por um **Grid de Cards** idêntico ao do catálogo:
*   **Visual Premium**: Fundo escuro (`#0f1623`), bordas sutis e efeito de elevação (hover) com brilho azul.
*   **Estrutura do Card**:
    *   **Topo**: Badge do **Status** (com as cores correspondentes: Pendente, Concluído, etc).
    *   **Corpo**:
        *   **Título**: Nome do Cliente (em destaque).
        *   **Subtítulo**: Veículo (Modelo e Placa).
        *   **Detalhe**: Lista de serviços truncada.
    *   **Rodapé (Footer)**:
        *   Esquerda: Data e Hora (com ícone de calendário).
        *   Direita: Valor Total (em azul destaque).
*   **Ações**: Vou integrar os botões (Iniciar, Concluir, Cancelar, Excluir) de forma compacta dentro do card (botões de ícone ou barra de ações) para não quebrar o layout vertical.

### 2. Melhorar a UI do Formulário (`/agendamentos/novo`)
Vou aplicar o mesmo "DNA visual" do catálogo no formulário:
*   **Container**: Usar o mesmo estilo de card premium para envolver todo o formulário.
*   **Inputs Modernos**: Adicionar ícones à esquerda de todos os campos para facilitar a leitura visual (ícone de Carro para veículo, Calendário para data, Ferramenta para serviço).
*   **Hierarquia**: Reforçar os títulos das seções com a cor azul padrão do tema.
*   **Botões**: Padronizar os botões de Salvar/Cancelar com o estilo arredondado e cores do tema.

Essa mudança deixará o sistema com uma identidade visual única e muito mais profissional.