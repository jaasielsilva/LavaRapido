# Plano de Reorganização de Model, Repository e Service

Seguindo o padrão aplicado aos controladores, reorganizaremos as camadas `model`, `repository` e `service` em subpacotes por domínio.

## 1. Reestruturação do Pacote `model`

*   **Empresa**: `Empresa.java` -> `br.com.lavajato.model.empresa`
*   **Usuario**: `Usuario.java`, `Perfil.java` -> `br.com.lavajato.model.usuario`
*   **Cliente**: `Cliente.java` -> `br.com.lavajato.model.cliente`
*   **Veiculo**: `Veiculo.java` -> `br.com.lavajato.model.veiculo`

## 2. Reestruturação do Pacote `repository`

*   **Empresa**: `EmpresaRepository.java` -> `br.com.lavajato.repository.empresa`
*   **Usuario**: `UsuarioRepository.java` -> `br.com.lavajato.repository.usuario`
*   **Cliente**: `ClienteRepository.java` -> `br.com.lavajato.repository.cliente`
*   **Veiculo**: `VeiculoRepository.java` -> `br.com.lavajato.repository.veiculo`

## 3. Reestruturação do Pacote `service`

*   **Auth**: `CustomUserDetailsService.java` -> `br.com.lavajato.service.auth`
*   **Empresa**: `EmpresaService.java` -> `br.com.lavajato.service.empresa`
*   **Usuario**: `UsuarioService.java` -> `br.com.lavajato.service.usuario`
*   **Cliente**: `ClienteService.java` -> `br.com.lavajato.service.cliente`
*   **Veiculo**: `VeiculoService.java` -> `br.com.lavajato.service.veiculo`

## 4. Atualização de Referências (Refactoring)

*   Atualizar todos os `package` declarations nos arquivos movidos.
*   Atualizar todos os `import` nos arquivos que dependem dessas classes (Controllers, Services, Configs, DataInitializer).
*   Executar o build para garantir a integridade do projeto.
