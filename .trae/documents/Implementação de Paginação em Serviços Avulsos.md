O plano de implementação para adicionar paginação padronizada na listagem de Serviços Avulsos consiste nas seguintes etapas:

1.  **Atualizar o Repositório (`ServicoAvulsoRepository.java`)**:
    *   Adicionar um novo método `findAllByEmpresa` que aceite um parâmetro `Pageable` e retorne `Page<ServicoAvulso>`. Isso habilitará a paginação eficiente no nível do banco de dados.

2.  **Atualizar o Serviço (`ServicoAvulsoService.java`)**:
    *   Criar um método `listarPaginado(Empresa empresa, int page, int size)` que utilize o novo método do repositório.
    *   Definir a ordenação padrão por data de criação decrescente (mais recentes primeiro).

3.  **Atualizar o Controlador (`ServicoAvulsoController.java`)**:
    *   Modificar o endpoint `listar` para aceitar parâmetros opcionais de paginação (`page` default 0, `size` default 5 - conforme solicitado).
    *   Substituir a chamada de listagem total pela chamada do novo método paginado do serviço.
    *   Adicionar o objeto `Page` ao modelo (`model.addAttribute`) para que o Thymeleaf possa acessá-lo.

4.  **Atualizar o Template (`servico-avulso/list.html`)**:
    *   Inserir o bloco de navegação (paginação) no final da lista.
    *   Utilizar classes CSS padronizadas (ex: `.pagination`, `.page-item`, `.page-link`) para manter a consistência visual com outras telas do sistema (como a de Veículos).
    *   Garantir que os botões "Anterior" e "Próximo" funcionem corretamente e sejam desabilitados quando necessário.

**Benefício**: Essa alteração melhorará a performance da tela ao carregar apenas 5 registros por vez, em vez de todos os serviços do histórico, e fornecerá uma navegação intuitiva para o usuário acessar registros mais antigos.
