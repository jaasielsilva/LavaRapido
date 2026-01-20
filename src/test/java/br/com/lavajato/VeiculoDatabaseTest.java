package br.com.lavajato;

import br.com.lavajato.model.cliente.Cliente;
import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.veiculo.Veiculo;
import br.com.lavajato.repository.cliente.ClienteRepository;
import br.com.lavajato.repository.empresa.EmpresaRepository;
import br.com.lavajato.repository.veiculo.VeiculoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class VeiculoDatabaseTest {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void testCicloCompletoVeiculo() {
        System.out.println(">>> INICIANDO TESTE COMPLETO (CADASTRO, EDICAO, EXCLUSAO)...");

        // 1. Setup (Empresa & Cliente)
        Empresa empresa = empresaRepository.findAll().stream().findFirst().orElseGet(() -> {
            Empresa e = new Empresa();
            e.setNome("Lava Jato Teste " + System.currentTimeMillis());
            e.setCnpj("00.000.000/0001-" + new Random().nextInt(99));
            return empresaRepository.save(e);
        });

        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Teste Ciclo " + System.currentTimeMillis());
        cliente.setEmpresa(empresa);
        cliente.setTelefone("11988887777");
        cliente = clienteRepository.save(cliente);

        // --- TESTE 1: CADASTRO (Para Verificação do Usuário) ---
        Veiculo veiculoFixo = new Veiculo();
        veiculoFixo.setPlaca("FIX" + (1000 + new Random().nextInt(8999)));
        veiculoFixo.setModelo("Veiculo Fixo");
        veiculoFixo.setCor("Branco");
        veiculoFixo.setAno(2024);
        veiculoFixo.setCliente(cliente);
        veiculoFixo = veiculoRepository.save(veiculoFixo);
        System.out.println(">>> [CADASTRO] Veiculo Fixo salvo: ID=" + veiculoFixo.getId() + ", Placa=" + veiculoFixo.getPlaca());

        // --- TESTE 2: CICLO DE VIDA (Cria, Edita, Deleta) ---
        Veiculo veiculoCiclo = new Veiculo();
        veiculoCiclo.setPlaca("CIC" + (1000 + new Random().nextInt(8999)));
        veiculoCiclo.setModelo("Veiculo Ciclo Original");
        veiculoCiclo.setCor("Prata");
        veiculoCiclo.setAno(2023);
        veiculoCiclo.setCliente(cliente);
        
        // 2.1 Salvar
        veiculoCiclo = veiculoRepository.save(veiculoCiclo);
        assertNotNull(veiculoCiclo.getId());
        System.out.println(">>> [CADASTRO] Veiculo Ciclo salvo: ID=" + veiculoCiclo.getId());

        // 2.2 Editar
        veiculoCiclo.setModelo("Veiculo Ciclo Editado");
        veiculoCiclo.setCor("Dourado");
        veiculoCiclo = veiculoRepository.save(veiculoCiclo);
        
        Veiculo veiculoEditado = veiculoRepository.findById(veiculoCiclo.getId()).orElseThrow();
        assertEquals("Veiculo Ciclo Editado", veiculoEditado.getModelo());
        assertEquals("Dourado", veiculoEditado.getCor());
        System.out.println(">>> [EDICAO] Veiculo atualizado com sucesso.");

        // 2.3 Excluir
        veiculoRepository.delete(veiculoCiclo);
        
        Optional<Veiculo> veiculoExcluido = veiculoRepository.findById(veiculoCiclo.getId());
        assertFalse(veiculoExcluido.isPresent());
        System.out.println(">>> [EXCLUSAO] Veiculo excluido com sucesso.");
        
        System.out.println(">>> TESTE COMPLETO FINALIZADO COM SUCESSO.");
        System.out.println(">>> Pode verificar o Veiculo Fixo (ID " + veiculoFixo.getId() + ") no banco de dados.");
    }
}
