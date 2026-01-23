O diagnóstico revelou dois problemas distintos que abordarei:

1. **Erro ao Editar Serviço (Prioritário):**

   * **Problema:** O link `/catalogo/editar/{id}` não existe no Controller, gerando o erro 404 (tratado como erro genérico pelo sistema).

   * **Solução:** Implementar o método `@GetMapping("/editar/{id}")` no `CatalogoController.java`.

2. **Lentidão no Pagamento:**

   * **Problema:** O envio de e-mail é síncrono. O usuário clica em "Pagar" e a interface trava até o Gmail responder (o que pode levar segundos).

   * **Solução:** Transformar o envio de e-mail em uma tarefa de fundo (background task) usando `@Async` do Spring.

### Plano de Implementação

1. **Habilitar Assincronismo:**

   * Adicionar a anotação `@EnableAsync` na classe principal `LavajatoApplication.java`.

   * Adicionar a anotação `@Async` nos métodos de envio de e-mail na classe `EmailService.java`.

   * Isso fará com que o sistema devolva a resposta "Pagamento Realizado" imediatamente ao usuário, enquanto o e-mail segue sendo processado em outra thread.

2. **Corrigir Edição de Catálogo:**

   * Editar `CatalogoController.java` para adicionar o método:

     ```java
     @GetMapping("/editar/{id}")
     public String editar(@PathVariable Long id, Model model) { ... }
     ```

   * Reutilizar a view `catalogo/form.html` (que já deve estar preparada para edição ou precisará de pequenos ajustes para popular os campos).

3. **Verificação:**

   * Testar a edição de um serviço.

   * Realizar um pagamento e verificar se a resposta da tela é instantânea, mesmo com o e-mail sendo enviado (acompanhando pelos logs).

