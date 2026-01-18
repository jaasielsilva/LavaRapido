# Plano de Correção do Erro de Template Thymeleaf

O erro `org.thymeleaf.exceptions.TemplateInputException` indica que o Thymeleaf falhou ao processar o template `dashboard.html`. Analisando os arquivos, identifiquei o problema:

1.  **Uso Incorreto de `sec:authorize` sem Namespace**:
    *   No arquivo `dashboard.html`, o atributo `sec:authorize="hasRole('MASTER')"` é usado na linha 10.
    *   No entanto, o namespace `xmlns:sec="http://www.thymeleaf.org/extras/spring-security"` **não foi declarado** na tag `<html>` do `dashboard.html`. Embora ele use `th:replace`, o fragmento precisa conhecer o namespace para processar a segurança *antes* ou *durante* a substituição, ou o parser pode falhar se não reconhecer o prefixo `sec`.

2.  **Estratégia de Correção**:
    *   Adicionar `xmlns:sec="http://www.thymeleaf.org/extras/spring-security"` na tag `<html>` do arquivo `src/main/resources/templates/dashboard.html`.

## Passos da Solução

1.  **Editar `dashboard.html`**:
    *   Adicionar o namespace de segurança.

2.  **Reiniciar a Aplicação**:
    *   Parar a execução atual (já parada).
    *   Executar novamente `./mvnw spring-boot:run`.

3.  **Validar**:
    *   Acessar `http://localhost:8080/login` e logar novamente.
