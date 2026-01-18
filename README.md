# POLÍTICA DE PERFIS E PERMISSÕES  
## Sistema Lava-Jato SaaS

### 1. Informações Gerais

**Nome do Sistema:** Lava-Jato SaaS  
**Objetivo:** Gestão operacional e administrativa de lava-rápidos e centros de estética automotiva.  
**Versão:** Protótipo v1.0.0  

Este documento estabelece, de forma oficial, as permissões associadas aos perfis de usuário **ADMIN** e **FUNCIONARIO** no sistema Lava-Jato SaaS, com foco na segurança da informação, segregação de funções e governança dos dados da empresa (tenant).

> Observação: As permissões do perfil **MASTER** (administrador global do SaaS) não são objeto deste documento.

---

### 2. Perfis de Acesso

#### 2.1. Perfil ADMIN

**Descrição:**  
Usuário com papel de gestor da empresa contratante (tenant). Responsável pela administração de cadastros, equipe e parâmetros estratégicos (como catálogo de serviços e produtos).

**Características Gerais:**

- Tem acesso a todos os módulos operacionais da **sua empresa**.
- Pode gerenciar usuários internos (ADMIN e FUNCIONARIO) da empresa.
- Pode administrar catálogo de serviços e produtos da empresa.
- Não possui poderes de administração global do SaaS (função exclusiva do MASTER).

---

#### 2.2. Perfil FUNCIONARIO

**Descrição:**  
Usuário com papel operacional. Atua diretamente no atendimento ao cliente, registro de serviços e movimentação da rotina diária.

**Características Gerais:**

- Acesso restrito a módulos operacionais básicos.
- Não possui acesso a módulos de administração de usuários, catálogo de serviços e produtos.
- Não pode alterar preços tabelados definidos pela gestão.
- Não pode excluir registros sensíveis (clientes, veículos e agendamentos).

---

### 3. Permissões por Módulo – Perfil ADMIN

#### 3.1. Navegação e Menus

O usuário ADMIN, após autenticação, tem acesso aos seguintes itens de menu:

- Dashboard
- Clientes
- Agendamentos
- Serviços Avulsos
- Veículos
- Produtos
- Catálogo
- Usuários

O menu **Empresas** é de uso exclusivo do perfil MASTER e não é exibido ao ADMIN.

---

#### 3.2. Módulo Clientes

**Escopo:** Gestão de clientes da empresa.

**Permissões do ADMIN:**

- Visualizar a lista de clientes da própria empresa.
- Cadastrar novos clientes.
- Editar dados de clientes existentes.
- Excluir clientes da empresa.

> A exclusão de clientes é bloqueada apenas para o perfil FUNCIONARIO. O ADMIN está autorizado a executar esta operação.

---

#### 3.3. Módulo Veículos

**Escopo:** Gestão de veículos vinculados aos clientes da empresa.

**Permissões do ADMIN:**

- Visualizar a lista de veículos da empresa, com recursos de busca.
- Cadastrar novos veículos:
  - Diretamente pelo módulo “Veículos”.
  - A partir dos cards de cliente (“Adicionar Veículo”).
- Editar veículos existentes.
- Excluir veículos da empresa.

> A interface de exclusão é exibida apenas para MASTER/ADMIN, e o backend bloqueia a operação quando o usuário logado é FUNCIONARIO.

---

#### 3.4. Módulo Agendamentos

**Escopo:** Gestão de serviços agendados.

**Permissões do ADMIN:**

- Visualizar lista de agendamentos da empresa.
- Cadastrar novos agendamentos.
- Editar agendamentos existentes.
- Alterar o status de agendamentos:
  - AGENDADO → EM_ANDAMENTO  
  - EM_ANDAMENTO → CONCLUIDO  
  - AGENDADO/EM_ANDAMENTO → CANCELADO
- Excluir agendamentos da empresa.

> A exclusão de agendamentos é bloqueada para FUNCIONARIO, mas permitida ao ADMIN.

---

#### 3.5. Módulo Serviços Avulsos

**Escopo:** Registro de serviços realizados sem agendamento prévio (balcão/dia a dia).

**Permissões do ADMIN:**

- Acessar o módulo “Serviços Avulsos”.
- Registrar novos serviços avulsos para:
  - Cliente cadastrado.
  - Cliente avulso (nome e veículo informados no ato).
- Alterar o status dos serviços:
  - EM_FILA → EM_ANDAMENTO → CONCLUIDO
  - EM_FILA/EM_ANDAMENTO → CANCELADO
- Visualizar indicadores operacionais:
  - Quantidade em fila.
  - Total concluído no dia.
  - Faturamento do dia em serviços avulsos.
- Definir **valor personalizado** para o serviço:
  - Campo de “Valor Personalizado (Opcional)” disponível apenas para MASTER/ADMIN.
  - Quando preenchido, substitui o preço padrão do catálogo.

> O ADMIN pode, portanto, conceder descontos, aplicar preços diferenciados ou ajustar o valor de acordo com a política comercial da empresa.

---

#### 3.6. Módulo Catálogo de Serviços

**Escopo:** Tabela oficial de serviços e preços da empresa.

**Permissões do ADMIN:**

- Acessar o módulo “Catálogo”.
- Visualizar serviços cadastrados, organizados por categoria.
- Cadastrar novos serviços no catálogo:
  - Nome, categoria, tempo estimado, preço.
- Editar e atualizar serviços já existentes (incluindo preço).

> O catálogo é a base de preços utilizada em agendamentos e serviços avulsos. É um módulo estratégico, reservado à gestão (MASTER/ADMIN).

---

#### 3.7. Módulo Produtos

**Escopo:** Controle de produtos, estoque e valor de estoque.

**Permissões do ADMIN:**

- Acessar o módulo “Produtos”.
- Visualizar lista de produtos da empresa.
- Consultar indicadores:
  - Total de produtos.
  - Itens com estoque baixo.
  - Valor total de estoque.
- Cadastrar e editar produtos (incluindo preço, unidade, categoria e quantidade).
- Utilizar produtos em operações de venda, associando a clientes e formas de pagamento, conforme fluxo implementado.

---

#### 3.8. Módulo Usuários

**Escopo:** Gestão de usuários internos da empresa.

**Permissões do ADMIN:**

- Acessar o módulo “Usuários”.
- Visualizar lista de usuários ativos da empresa.
- Visualizar lista de usuários inativos da empresa.
- Cadastrar novos usuários para a empresa:
  - Definir nome, e-mail, senha e perfil (ADMIN ou FUNCIONARIO).
  - O ADMIN não cria usuários MASTER (somente MASTER global).
- Editar usuários existentes.
- Inativar (excluir logicamente) usuários, com as seguintes restrições de segurança:
  - Não é permitido excluir usuário MASTER.
  - Não é permitido deixar a empresa sem pelo menos **um ADMIN ativo**:
    - Ao tentar excluir o último ADMIN, o sistema bloqueia a operação.

> O ADMIN enxerga e gerencia apenas usuários pertencentes à mesma empresa.

---

#### 3.9. Dashboard

**Escopo:** Visão geral do desempenho da operação.

**Permissões do ADMIN:**

- Acessar o Dashboard Operacional da empresa.
- Visualizar:
  - Total de clientes cadastrados.
  - Agendamentos pendentes (informação atualmente parcial/hardcoded).
  - Serviços do dia (informação atualmente parcial/hardcoded).
  - **Card de “Faturamento Hoje”**:
    - Disponível apenas para MASTER e ADMIN.
    - Apresenta visão de faturamento diário, reforçando o caráter gerencial do perfil.

---

### 4. Permissões por Módulo – Perfil FUNCIONARIO

#### 4.1. Navegação e Menus

O usuário FUNCIONARIO, após autenticação, tem acesso aos seguintes itens de menu:

- Dashboard
- Clientes
- Agendamentos
- Serviços Avulsos
- Veículos

Não tem acesso aos itens:

- Produtos
- Catálogo
- Usuários
- Empresas

---

#### 4.2. Módulo Clientes

**Permissões do FUNCIONARIO:**

- Visualizar a lista de clientes da empresa.
- Cadastrar novos clientes.
- Editar clientes existentes.
- **Não pode excluir** clientes.

> Qualquer tentativa de exclusão, inclusive via URL direta, é bloqueada pelo backend, que valida o perfil e impede a operação para FUNCIONARIO.

---

#### 4.3. Módulo Veículos

**Permissões do FUNCIONARIO:**

- Visualizar lista de veículos da empresa, com busca.
- Cadastrar novos veículos:
  - Pelo módulo “Veículos”.
  - Pelo atalho “Adicionar Veículo” nos cards de clientes.
- Editar veículos existentes.
- **Não pode excluir** veículos:
  - O botão de exclusão não é exibido para FUNCIONARIO.
  - A tentativa de exclusão via URL é bloqueada pelo backend.

---

#### 4.4. Módulo Agendamentos

**Permissões do FUNCIONARIO:**

- Visualizar lista de agendamentos da empresa.
- Cadastrar novos agendamentos.
- Editar agendamentos existentes.
- Alterar status de agendamentos:
  - AGENDADO → EM_ANDAMENTO
  - EM_ANDAMENTO → CONCLUIDO
  - AGENDADO/EM_ANDAMENTO → CANCELADO
- **Não pode excluir** agendamentos:
  - A exclusão é recusada pelo backend quando o usuário é FUNCIONARIO.

---

#### 4.5. Módulo Serviços Avulsos

**Permissões do FUNCIONARIO:**

- Acessar o módulo “Serviços Avulsos”.
- Registrar serviços avulsos:
  - Associando a cliente cadastrado.
  - Registrando cliente avulso (nome e veículo informados).
- Alterar status do serviço:
  - EM_FILA → EM_ANDAMENTO → CONCLUIDO
  - EM_FILA/EM_ANDAMENTO → CANCELADO
- Visualizar indicadores:
  - Em fila.
  - Concluídos hoje.
  - Faturamento do dia em serviços avulsos.

**Restrição crítica – Preços:**

- O FUNCIONARIO **não visualiza** o campo de “Valor Personalizado (Opcional)” no momento do registro do serviço.
- Quando o serviço é baseado no catálogo:
  - O sistema força o valor do serviço para o **preço tabelado** definido no catálogo.
  - Qualquer tentativa de enviar valor diferente (por manipulação de requisição) é sobrescrita no backend.

> Assim, o FUNCIONARIO não pode alterar preços oficiais, garantindo integridade da política comercial.

---

#### 4.6. Módulo Catálogo de Serviços

- O FUNCIONARIO **não possui acesso** ao módulo “Catálogo”.
- Tentativas de acessar `/catalogo/**` são bloqueadas pelas regras de segurança (rota exclusiva de MASTER/ADMIN).

---

#### 4.7. Módulo Produtos

- O FUNCIONARIO **não possui acesso** ao módulo “Produtos”.
- Tentativas de acessar `/produtos/**` são bloqueadas (exclusivo MASTER/ADMIN).

---

#### 4.8. Módulo Usuários

- O FUNCIONARIO **não possui acesso** ao módulo “Usuários”.
- Não pode listar, criar, editar ou excluir usuários.
- A gestão de usuários é uma responsabilidade do ADMIN (e do MASTER, na camada global).

---

#### 4.9. Dashboard

**Permissões do FUNCIONARIO:**

- Acessar o Dashboard Operacional da empresa.
- Visualizar:
  - Total de clientes cadastrados.
  - Agendamentos pendentes (informação atualmente parcial/hardcoded).
  - Serviços do dia (informação atualmente parcial/hardcoded).
- **Não visualizar** o card “Faturamento Hoje”:
  - Este indicador financeiro é destinado somente a MASTER e ADMIN.
  - O FUNCIONARIO não tem acesso a esse dado de faturamento consolidado via dashboard.

---

### 5. Considerações Finais

- Todas as operações são automaticamente limitadas ao **contexto da empresa do usuário logado**, garantindo isolamento entre tenants.
- O perfil **ADMIN** concentra as funções de gestão (usuários, catálogo, produtos e dados sensíveis).
- O perfil **FUNCIONARIO** concentra as funções operacionais, com restrições claras sobre:
  - Exclusão de registros (não permitido em clientes, veículos e agendamentos).
  - Alteração de preços (não permitido em serviços avulsos baseados no catálogo).
  - Acesso a módulos estratégicos (usuários, catálogo, produtos).

Este documento pode ser utilizado:

- Como referência interna de TI e Segurança.
- Em treinamentos de onboarding de novos usuários.
- Como anexo em contratos, manuais operacionais ou normas internas da empresa.
