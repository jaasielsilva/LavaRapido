O plano a seguir aborda as duas solicitações: a discrepância no Excel e a responsividade para tablets.

### 1. Correção da Exportação para Excel (Discrepância de R$ 2.830,00)

**Análise**:
A diferença de R$ 2.830,00 corresponde exatamente ao valor das "Despesas Operacionais". Isso indica que a soma manual no Excel está incorreta porque a planilha lista todas as movimentações (Entradas e Saídas) na mesma coluna "Valor", sem diferenciar o sinal. Se o usuário somar a coluna inteira, as despesas (que deveriam ser subtraídas) são somadas, gerando o erro.

**Solução**:
Alterar o método `exportarExcel` no `FinanceiroService.java` para:
1.  **Exibir valores negativos**: Para itens do tipo `SAIDA` (Despesas), o valor na célula do Excel será multiplicado por -1.
2.  **Formatação Visual**: Aplicar cor vermelha para valores negativos na planilha, facilitando a identificação visual.

Isso permitirá que a função "SOMA" do Excel funcione corretamente, subtraindo as despesas automaticamente.

### 2. Responsividade para Tablet (Samsung Tab A9 - ~800px)

**Análise**:
O layout atual só esconde a barra lateral (sidebar) em telas menores que 768px. Tablets como o A9 têm largura de ~800px (modo retrato) ou mais, caindo na faixa onde a sidebar é fixa, mas ocupando muito espaço (260px), o que espreme o conteúdo principal.

**Solução**:
Ajustar o CSS (`style.css`) para alterar o *breakpoint* de responsividade:
1.  **Alterar Media Query**: Mudar de `@media (max-width: 768px)` para `@media (max-width: 992px)`.
2.  **Efeito**: Em tablets (até 992px), a sidebar ficará oculta por padrão (com menu hambúrguer), liberando 100% da largura da tela para os dados e tabelas.
3.  **Ajuste de Grid**: Garantir que os cards e gráficos se adaptem a uma coluna única ou duas colunas fluidas nessa resolução.

### Passos de Execução:
1.  Modificar `FinanceiroService.java`: Ajustar a lógica de exportação para negativar valores de saída.
2.  Modificar `style.css`: Atualizar as Media Queries para suportar tablets.
3.  Adicionar botão de "Menu" (Toggle) no layout principal (`layout.html`) para permitir abrir a sidebar no tablet (caso ainda não exista ou esteja oculto).
