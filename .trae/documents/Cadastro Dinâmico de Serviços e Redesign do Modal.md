Vou implementar o **Catálogo de Serviços** idêntico à imagem de referência e integrar tudo ao sistema.

### **1. Backend (Catálogo Dinâmico)**
*   **Enum `CategoriaServico`**: Lavagem, Estética, Polimento, Outros.
*   **Entidade `Servico`**:
    *   Substituir o Enum estático por uma tabela real no banco.
    *   Campos: `nome`, `descricao` (novo), `categoria`, `preco`, `tempoMinutos`, `ativo`, `empresa`.
*   **Migração**: Criar um inicializador que popula automaticamente o banco com os serviços padrão (Lavagem Simples, Polimento Técnico, etc.) para você não começar do zero.
*   **Refatoração**: Atualizar o módulo de **Serviços Avulsos** para usar esses serviços do banco em vez do Enum fixo.

### **2. Frontend (Catálogo de Serviços - Fiel à Imagem)**
*   **Sidebar**: Novo item "Catálogo" com ícone de etiqueta/carro.
*   **Tela de Listagem (`catalogo/list.html`)**:
    *   **Filtros de Categoria**: Botões no topo (Todos, Lavagem, Estética...) estilo "Pills".
    *   **Layout de Grade**: Cards escuros organizados por categoria.
    *   **Design do Card**:
        *   Badge da categoria (Azul para Lavagem, Laranja para Polimento, Verde para Estética).
        *   Nome em destaque e descrição curta abaixo.
        *   Rodapé do card com Ícone de relógio (tempo) e Preço em destaque à direita.

### **3. Refinamento Visual (Modal "Perfeito e Amigável")**
*   **Modal de Serviço Avulso**:
    *   **Integração**: A lista de serviços agora virá do banco de dados.
    *   **Design Suave**:
        *   Aumentar arredondamento das bordas (`border-radius: 20px`).
        *   Aplicar sombras difusas e cores de fundo com leve contraste (`#1e293b` vs `#0f1623`).
        *   Efeitos de Hover suaves nos cartões de seleção de serviço.

### **Fluxo de Execução**
1.  Criar estrutura do Catálogo (Entity, Enum, Repository, Service, Controller).
2.  Implementar a tela do Catálogo (Listagem e Cadastro).
3.  Refatorar Serviços Avulsos para conectar com o novo Catálogo.
4.  Aplicar o redesign "suave" no Modal.
