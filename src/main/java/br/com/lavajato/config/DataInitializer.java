package br.com.lavajato.config;

import br.com.lavajato.model.cliente.Cliente;
import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.usuario.Perfil;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.model.veiculo.Veiculo;
import br.com.lavajato.repository.cliente.ClienteRepository;
import br.com.lavajato.repository.empresa.EmpresaRepository;
import br.com.lavajato.repository.usuario.UsuarioRepository;
import br.com.lavajato.repository.veiculo.VeiculoRepository;
import br.com.lavajato.service.produto.ProdutoService;
import br.com.lavajato.service.servico.ServicoCatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProdutoService produtoService;
    
    @Autowired
    private ServicoCatalogoService servicoCatalogoService;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (usuarioRepository.findByEmail("master@lavajato.com").isEmpty()) {
                System.out.println("Inicializando dados do sistema...");

                // 1. Criar Empresa Master
                Empresa empresaMaster = criarEmpresa("Lava Jato HQ", "00.000.000/0001-00");
                criarUsuario("Super Admin", "master@lavajato.com", "admin", Perfil.MASTER, empresaMaster);

                // 2. Criar Empresa A (Centro)
                Empresa empresaA = criarEmpresa("Lava Jato Centro", "11.111.111/0001-11");
                criarUsuario("Admin Centro", "admin.centro@lavajato.com", "123456", Perfil.ADMIN, empresaA);
                criarUsuario("Func Centro", "func.centro@lavajato.com", "123456", Perfil.FUNCIONARIO, empresaA);
                
                // Dados Empresa A
                Cliente clienteA = criarCliente("João da Silva (Centro)", "9999-1111", empresaA);
                criarVeiculo("ABC-1234", "Fiat Uno", clienteA);
                produtoService.inicializarProdutosPadrao(empresaA);
                servicoCatalogoService.inicializarServicosPadrao(empresaA);

                // 3. Criar Empresa B (VIP)
                Empresa empresaB = criarEmpresa("Estética Automotiva VIP", "22.222.222/0001-22");
                criarUsuario("Admin VIP", "admin.vip@lavajato.com", "123456", Perfil.ADMIN, empresaB);
                criarUsuario("Func VIP", "func.vip@lavajato.com", "123456", Perfil.FUNCIONARIO, empresaB);

                // Dados Empresa B
                Cliente clienteB = criarCliente("Maria Souza (VIP)", "9999-2222", empresaB);
                criarVeiculo("XYZ-9876", "Honda Civic", clienteB);
                produtoService.inicializarProdutosPadrao(empresaB);
                servicoCatalogoService.inicializarServicosPadrao(empresaB);

                System.out.println("Dados de teste inicializados com sucesso!");
                System.out.println("MASTER: master@lavajato.com / admin");
                System.out.println("EMPRESA A: admin.centro@lavajato.com / 123456");
                System.out.println("EMPRESA B: admin.vip@lavajato.com / 123456");
            }
        };
    }

    private Empresa criarEmpresa(String nome, String cnpj) {
        Empresa empresa = new Empresa();
        empresa.setNome(nome);
        empresa.setCnpj(cnpj);
        empresa.setAtivo(true);
        return empresaRepository.save(empresa);
    }

    private Usuario criarUsuario(String nome, String email, String senha, Perfil perfil, Empresa empresa) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setPerfil(perfil);
        usuario.setEmpresa(empresa);
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }
    
    private Cliente criarCliente(String nome, String telefone, Empresa empresa) {
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setTelefone(telefone);
        cliente.setEmpresa(empresa);
        return clienteRepository.save(cliente);
    }
    
    private Veiculo criarVeiculo(String placa, String modelo, Cliente cliente) {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(placa);
        veiculo.setModelo(modelo);
        veiculo.setCliente(cliente);
        return veiculoRepository.save(veiculo);
    }
}
